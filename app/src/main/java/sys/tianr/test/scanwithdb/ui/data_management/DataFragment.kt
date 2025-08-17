package sys.tianr.test.scanwithdb.ui.data_management

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import sys.tianr.test.scanwithdb.R
import sys.tianr.test.scanwithdb.data.model.Person
import sys.tianr.test.scanwithdb.databinding.DialogAddPersonBinding
import sys.tianr.test.scanwithdb.databinding.FragmentDataBinding

import androidx.hilt.navigation.fragment.hiltNavGraphViewModels



@AndroidEntryPoint
class DataFragment : Fragment() {

    private var _binding: FragmentDataBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DataViewModel by viewModels()
    private lateinit var personAdapter: PersonAdapter

    private val importCsvLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.also { uri ->
                viewModel.importFromCsv(requireContext(), uri)
                Toast.makeText(context, "导入成功", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val exportCsvLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.also { uri ->
                viewModel.exportToCsv(requireContext(), uri)
                Toast.makeText(context, "导出成功", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        personAdapter = PersonAdapter(
            onEditClicked = { person -> showEditPersonDialog(person) },
            onDeleteClicked = { person -> showDeleteConfirmationDialog(person) }
        )
        binding.recyclerView.adapter = personAdapter
    }

    private fun setupObservers() {
        viewModel.allPeople.observe(viewLifecycleOwner) { people ->
            people?.let { personAdapter.submitList(it) }
        }
    }

    private fun setupClickListeners() {
        binding.fabAddPerson.setOnClickListener { showAddPersonDialog() }
        binding.buttonImport.setOnClickListener { openFilePickerForImport() }
        binding.buttonExport.setOnClickListener { createCsvFileForExport() }
    }

    private fun showAddPersonDialog() {
        val dialogBinding = DialogAddPersonBinding.inflate(layoutInflater)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("添加新条目")
            .setView(dialogBinding.root)
            .setNegativeButton("取消", null)
            .setPositiveButton("添加") { _, _ ->
                val name = dialogBinding.editTextName.text.toString().trim()
                val barcode = dialogBinding.editTextBarcode.text.toString().trim()
                if (name.isNotEmpty() && barcode.isNotEmpty()) {
                    viewModel.addPerson(name, barcode)
                } else {
                    Toast.makeText(context, "姓名和学号/条码不能为空", Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }

    private fun showEditPersonDialog(person: Person) {
        val dialogBinding = DialogAddPersonBinding.inflate(layoutInflater)
        dialogBinding.editTextName.setText(person.name)
        dialogBinding.editTextBarcode.setText(person.barcode)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("编辑条目")
            .setView(dialogBinding.root)
            .setNegativeButton("取消", null)
            .setPositiveButton("保存") { _, _ ->
                val newName = dialogBinding.editTextName.text.toString().trim()
                val newBarcode = dialogBinding.editTextBarcode.text.toString().trim()
                if (newName.isNotEmpty() && newBarcode.isNotEmpty()) {
                    val updatedPerson = person.copy(name = newName, barcode = newBarcode)
                    viewModel.updatePerson(updatedPerson)
                } else {
                    Toast.makeText(context, "姓名和学号/条码不能为空", Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }

    private fun showDeleteConfirmationDialog(person: Person) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("删除确认")
            .setMessage("确定要删除 ${person.name} 吗？此操作无法撤销。")
            .setNegativeButton("取消", null)
            .setPositiveButton("删除") { _, _ ->
                viewModel.deletePerson(person)
            }
            .show()
    }

    // --- 修改这里：修复文件选择器问题 ---
    private fun openFilePickerForImport() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            // 设置一个通用类型，然后用 EXTRA_MIME_TYPES 明确指定你想要的类型
            // 这是处理不同设备文件选择器兼容性的常用方法
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("*/*"))
        }
        importCsvLauncher.launch(intent)
    }

    private fun createCsvFileForExport() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/csv"
            putExtra(Intent.EXTRA_TITLE, "export_data_${System.currentTimeMillis()}.csv")
        }
        exportCsvLauncher.launch(intent)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}