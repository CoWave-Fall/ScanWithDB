package sys.tianr.test.scanwithdb.ui.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import sys.tianr.test.scanwithdb.R
import sys.tianr.test.scanwithdb.data.DatabaseManager
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    private val viewModel: SettingsViewModel by viewModels()

    @Inject
    lateinit var databaseManager: DatabaseManager

    private val dbListPref: ListPreference? by lazy { findPreference("database_selection") }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        setupActionPreferences()
    }

    override fun onResume() {
        super.onResume()
        updateDatabaseListDisplay()
    }

    private fun updateDatabaseListDisplay() {
        val dbList = viewModel.getDatabaseList()
        val currentDb = databaseManager.database.value?.openHelper?.databaseName

        dbListPref?.let {
            it.isVisible = false // Hide to force redraw
            it.isEnabled = dbList.isNotEmpty()
            if (dbList.isEmpty()) {
                it.summary = "没有可用的数据库"
                it.entries = emptyArray()
                it.entryValues = emptyArray()
            } else {
                it.entries = dbList.toTypedArray()
                it.entryValues = dbList.toTypedArray()
                it.value = currentDb
                it.summary = currentDb ?: "未选择"
            }
            it.isVisible = true // Show again
        }
    }

    private fun setupActionPreferences() {
        dbListPref?.setOnPreferenceChangeListener { preference, newValue ->
            val newDbName = newValue as String
            Log.d(TAG, "User selected a new database -> $newDbName")
            databaseManager.switchDatabase(newDbName)
            preference.summary = newDbName // Immediately update the summary
            true
        }

        findPreference<Preference>("create_database")?.setOnPreferenceClickListener {
            showCreateDatabaseDialog()
            true
        }

        findPreference<Preference>("delete_database")?.setOnPreferenceClickListener {
            val currentDb = databaseManager.database.value?.openHelper?.databaseName
            if (currentDb != null) {
                showDeleteDbConfirmationDialog(currentDb)
            } else {
                Toast.makeText(context, "没有活动的数据库可删除", Toast.LENGTH_SHORT).show()
            }
            true
        }

        findPreference<Preference>("about_app")?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_aboutFragment)
            true
        }
    }

    private fun showDeleteDbConfirmationDialog(dbName: String) {
        showConfirmationDialog("删除数据库?", "确定要永久删除数据库 '$dbName' 吗？此操作不可恢复。") {
            if (viewModel.deleteDatabase(dbName)) {
                Toast.makeText(context, "'$dbName' 已删除", Toast.LENGTH_SHORT).show()
                updateDatabaseListDisplay()
            } else {
                Toast.makeText(context, "删除失败", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showCreateDatabaseDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_input_text, null, false)
        val textInputLayout = dialogView.findViewById<TextInputLayout>(R.id.textInputLayout)
        val editText = dialogView.findViewById<EditText>(R.id.editText)
        textInputLayout.hint = "数据库文件名"
        editText.setHint("例如: class_a_database")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("创建新数据库")
            .setView(dialogView)
            .setNegativeButton("取消", null)
            .setPositiveButton("创建") { _, _ ->
                var dbName = editText.text.toString().trim().replace(" ", "_")
                if (dbName.isNotEmpty()) {
                    if (!dbName.endsWith(".db")) {
                        dbName += ".db"
                    }
                    if (databaseManager.createNewDatabaseFile(dbName)) {
                        Toast.makeText(requireContext(), "已创建数据库: $dbName", Toast.LENGTH_SHORT).show()
                        updateDatabaseListDisplay()
                    } else {
                        Toast.makeText(context, "数据库已存在或创建失败", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "数据库名不能为空", Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }

    private fun showConfirmationDialog(title: String, message: String, onConfirm: () -> Unit) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setNegativeButton("取消", null)
            .setPositiveButton("确认") { _, _ ->
                onConfirm()
            }
            .show()
    }

    companion object {
        private const val TAG = "SettingsFragment"
    }
}