package com.github.sapp.yupi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.sapp.yupi.data.Contact
import com.github.sapp.yupi.databinding.ListItemContactBinding

/**
 * Adapter for the [RecyclerView] in [com.github.sapp.yupi.MainActivity].
 */
class ContactAdapter : ListAdapter<Contact, ContactAdapter.ViewHolder>(ContactDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ListItemContactBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = getItem(position)
        holder.apply {
            bind(createOnClickListener(contact.id), contact)
            itemView.tag = contact
        }
    }

    private fun createOnClickListener(id: Int): View.OnClickListener {
        return View.OnClickListener {
//            val direction = PlantListFragmentDirections.ActionPlantListFragmentToPlantDetailFragment(plantId)
//            it.findNavController().navigate(direction)
        }
    }

    class ViewHolder(private val binding: ListItemContactBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(listener: View.OnClickListener, item: Contact) {
            binding.apply {
                clickListener = listener
                contact = item
                executePendingBindings()
            }
        }
    }
}