package sys.tianr.test.scanwithdb.ui.scanner

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import sys.tianr.test.scanwithdb.R
import sys.tianr.test.scanwithdb.databinding.FragmentScannerBinding

import sys.tianr.test.scanwithdb.util.BarcodeAnalyzer
import sys.tianr.test.scanwithdb.util.VibrationHelper
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class ScannerFragment : Fragment() { // 确保继承自 BaseFragment

    // 将 viewModel 的获取方式改回 by viewModels()
    private val viewModel: ScannerViewModel by viewModels()

    private var _binding: FragmentScannerBinding? = null
    // 我们不再使用会崩溃的 !! 操作符
    private val binding get() = _binding

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraExecutor: ExecutorService
    private var cameraProvider: ProcessCameraProvider? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                startCamera()
            } else {
                Toast.makeText(requireContext(), "相机权限被拒绝，无法使用扫描功能", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // 在 onCreateView 中赋值
        _binding = FragmentScannerBinding.inflate(inflater, container, false)
        // 返回 binding!!.root 是安全的，因为我们刚刚创建了它
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraExecutor = Executors.newSingleThreadExecutor()
        setupClickListeners()
        setupObservers()
        checkCameraPermissionAndStart()
    }

    override fun onResume() {
        super.onResume() // 调用父类的方法，以执行刷新检查
        // onResume 中视图肯定存在，可以安全地更新 UI
        updateCurrentDbName()
        if (binding?.groupScanner?.visibility == View.VISIBLE) {
            startCamera()
        }
    }

    override fun onPause() {
        super.onPause()
        cameraProvider?.unbindAll()
    }

    private fun updateCurrentDbName() {
        val prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val dbName = prefs.getString("current_db", "default_database.db")
        // 安全地访问 binding
        binding?.textViewCurrentDb?.text = getString(R.string.current_db_template, dbName)
    }

    private fun checkCameraPermissionAndStart() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                startCamera()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            // 关键修复：在回调执行时，检查视图是否还存在
            if (_binding == null) {
                Log.w(TAG, "相机回调执行时，视图已销毁，操作已取消。")
                return@addListener
            }
            cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider!!)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        // 安全地访问 binding，如果 binding 为 null，则直接返回
        val localBinding = binding ?: return

        val preview: Preview = Preview.Builder().build()
        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        preview.setSurfaceProvider(localBinding.previewView.surfaceProvider)

        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        imageAnalysis.setAnalyzer(cameraExecutor, BarcodeAnalyzer { barcodeValue ->
            activity?.runOnUiThread {
                viewModel.onBarcodeScanned(barcodeValue)
            }
        })

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector, preview, imageAnalysis)
        } catch (exc: Exception) {
            Log.e(TAG, "相机用例绑定失败", exc)
        }
    }

    private fun setupClickListeners() {
        // 安全地设置监听器
        binding?.buttonFinishScan?.setOnClickListener { showReport() }
        binding?.buttonCloseReport?.setOnClickListener { hideReport() }
    }

    private fun showReport() {
        cameraProvider?.unbindAll()
        binding?.groupScanner?.visibility = View.GONE
        binding?.groupReport?.visibility = View.VISIBLE
    }

    private fun hideReport() {
        binding?.groupReport?.visibility = View.GONE
        binding?.groupScanner?.visibility = View.VISIBLE
        startCamera()
    }

    private fun setupObservers() {
        // 在观察者内部也要使用安全的 binding 访问
        viewModel.scanResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                val message = if (viewModel.scanFeedback.value == ScanFeedback.ALREADY_MARKED) {
                    "已标记: ${it.name}"
                } else {
                    "成功标记: ${it.name}"
                }
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                viewModel.onResultShown()
            }
        }

        viewModel.progress.observe(viewLifecycleOwner) { (marked, total) ->
            binding?.progressBar?.max = total
            binding?.progressBar?.progress = marked
            binding?.progressText?.text = "$marked / $total"
        }

        viewModel.scanFeedback.observe(viewLifecycleOwner) { feedback ->
            val color = when (feedback) {
                ScanFeedback.SUCCESS -> {
                    VibrationHelper.vibrate(requireContext())
                    Color.GREEN
                }
                ScanFeedback.ALREADY_MARKED -> Color.BLUE
                ScanFeedback.NOT_FOUND -> {
                    Toast.makeText(context, "未找到匹配项", Toast.LENGTH_SHORT).show()
                    Color.RED
                }
                ScanFeedback.IDLE, null -> Color.WHITE
            }
            binding?.viewfinderView?.setViewfinderColor(color)

            if (feedback != ScanFeedback.IDLE) {
                view?.postDelayed({
                    viewModel.onFeedbackShown()
                }, 500)
            }
        }

        viewModel.report.observe(viewLifecycleOwner) { report ->
            val markedText = report.markedList.joinToString("\n") { it.name }
            val unmarkedText = report.unmarkedList.joinToString("\n") { it.name }

            binding?.textViewMarked?.text = markedText.ifEmpty { "无" }
            binding?.textViewUnmarked?.text = unmarkedText.ifEmpty { "无" }
            binding?.markedTitle?.text = getString(R.string.marked_title, report.markedList.size)
            binding?.unmarkedTitle?.text = getString(R.string.unmarked_title, report.unmarkedList.size)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        // 关键：在视图销毁时将 _binding 置为 null
        _binding = null
        Log.d(TAG, "onDestroyView: _binding has been set to null.")
    }

    companion object {
        private const val TAG = "ScannerFragment"
    }
}