package sys.tianr.test.scanwithdb.ui.about

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sys.tianr.test.scanwithdb.databinding.ItemOssLicenseBinding

class OssLicenseAdapter(private val libraries: List<OssLibrary>) :
    RecyclerView.Adapter<OssLicenseAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemOssLicenseBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOssLicenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val library = libraries[position]
        holder.binding.libraryName.text = library.name
        holder.binding.libraryLicense.text = library.license
    }

    override fun getItemCount() = libraries.size
}
