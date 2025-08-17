package sys.tianr.test.scanwithdb.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 代表数据库中的一个人员条目。
 *
 * @param id 主键，自动生成。
 * @param name 人员姓名。
 * @param barcode 人员对应的条码/学号，必须唯一。
 * @param isMarked 标记状态，用于扫描功能。
 */
@Entity(
    tableName = "people",
    // 为 barcode 创建一个唯一索引，确保不会有重复的学号/条码
    indices = [Index(value = ["barcode"], unique = true)]
)
data class Person(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var name: String,
    var barcode: String,
    var isMarked: Boolean = false
)