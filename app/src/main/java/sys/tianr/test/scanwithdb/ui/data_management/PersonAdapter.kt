package sys.tianr.test.scanwithdb.ui.data_management

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import sys.tianr.test.scanwithdb.data.model.Person
import sys.tianr.test.scanwithdb.databinding.ItemPersonBinding

class PersonAdapter(
    private val onEditClicked: (Person) -> Unit,
    private val onDeleteClicked: (Person) -> Unit
) : ListAdapter<Person, PersonAdapter.PersonViewHolder>(PersonDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val binding = ItemPersonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PersonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        val person = getItem(position)
        holder.bind(person, onEditClicked, onDeleteClicked)
    }

    class PersonViewHolder(private val binding: ItemPersonBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(person: Person, onEditClicked: (Person) -> Unit, onDeleteClicked: (Person) -> Unit) {
            binding.textViewName.text = person.name
            binding.textViewBarcode.text = person.barcode
            binding.buttonEdit.setOnClickListener { onEditClicked(person) }
            binding.buttonDelete.setOnClickListener { onDeleteClicked(person) }
        }
    }

    class PersonDiffCallback : DiffUtil.ItemCallback<Person>() {
        override fun areItemsTheSame(oldItem: Person, newItem: Person): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Person, newItem: Person): Boolean {
            return oldItem == newItem
        }
    }
}