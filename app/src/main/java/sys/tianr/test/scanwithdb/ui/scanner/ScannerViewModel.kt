package sys.tianr.test.scanwithdb.ui.scanner

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import sys.tianr.test.scanwithdb.data.model.Person
import sys.tianr.test.scanwithdb.data.repository.PersonRepository
import javax.inject.Inject

// 定义扫描反馈的几种状态
enum class ScanFeedback {
    SUCCESS, // 成功匹配并标记
    ALREADY_MARKED, // 已标记
    NOT_FOUND, // 未在数据库中找到
    IDLE // 空闲状态
}

data class ScanReport(
    val markedList: List<Person> = emptyList(),
    val unmarkedList: List<Person> = emptyList()
)

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val repository: PersonRepository
) : ViewModel() {

    // 用于显示标记成功的人名 (Toast)
    private val _scanResult = MutableLiveData<Person?>()
    val scanResult: LiveData<Person?> = _scanResult

    // 用于给取景框提供颜色反馈
    private val _scanFeedback = MutableLiveData<ScanFeedback>(ScanFeedback.IDLE)
    val scanFeedback: LiveData<ScanFeedback> = _scanFeedback

    // 进度条数据
    private val _progress = MutableLiveData<Pair<Int, Int>>()
    val progress: LiveData<Pair<Int, Int>> = _progress

    // 报告数据
    val report: LiveData<ScanReport> = repository.getMarkedPeople()
        .combine(repository.getUnmarkedPeople()) { marked, unmarked ->
            ScanReport(marked, unmarked)
        }.asLiveData()


    init {
        // 使用 combine 操作符同时观察已标记和未标记列表，计算总进度
        repository.getMarkedPeople().combine(repository.getUnmarkedPeople()) { marked, unmarked ->
            val markedCount = marked.size
            val totalCount = marked.size + unmarked.size
            _progress.postValue(Pair(markedCount, totalCount))
        }.asLiveData() // 转换为 LiveData 来触发观察
            .observeForever{} // 添加一个空观察者以保持流的活跃
    }

    fun onBarcodeScanned(barcodeValue: String) {
        // 如果正在显示反馈，则暂时不处理新的扫描结果，避免闪烁
        if (_scanFeedback.value != ScanFeedback.IDLE) return

        viewModelScope.launch {
            val person = repository.getPersonByBarcode(barcodeValue)
            if (person != null) {
                // 只要找到就标记，无论之前是否已标记
                // 这里检查 isMarked 是为了避免不必要的数据库更新和UI反馈
                if (!person.isMarked) {
                    person.isMarked = true
                    repository.updatePerson(person)
                    _scanResult.postValue(person)
                    _scanFeedback.postValue(ScanFeedback.SUCCESS)
                } else {
                    _scanResult.postValue(person) // 即使已标记，也显示名字
                    _scanFeedback.postValue(ScanFeedback.ALREADY_MARKED)
                }
            } else {
                // 扫到了码，但数据库里没有
                _scanFeedback.postValue(ScanFeedback.NOT_FOUND)
            }
        }
    }

    fun onResultShown() {
        _scanResult.value = null
    }

    fun onFeedbackShown() {
        _scanFeedback.value = ScanFeedback.IDLE
    }
}