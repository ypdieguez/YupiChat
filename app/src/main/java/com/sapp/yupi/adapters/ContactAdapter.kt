package com.sapp.yupi.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sapp.yupi.data.Contact
import com.sapp.yupi.databinding.ItemContactBinding
import com.sapp.yupi.ui.ContactFragmentDirections
import com.sapp.yupi.utils.PhoneUtil

class ContactAdapter : ListAdapter<Contact, ContactAdapter.ViewHolder>(ContactDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemContactBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = getItem(position)
        holder.apply {
            bind(contact)
            itemView.tag = contact
        }
    }

    class ViewHolder(private val binding: ItemContactBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Contact) {
            binding.apply {
                this.contact = item

                clickListener = View.OnClickListener {

                    val phone = PhoneUtil.toE164(item.number, null)
                    val direction = ContactFragmentDirections
                            .actionContactFragmentToConversationFragment(phone, item.name)
                    it.findNavController().navigate(direction)
                }

                executePendingBindings()
            }
        }
    }
}

class ContactDiffCallback : DiffUtil.ItemCallback<Contact>() {
    override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem.number == newItem.number
    }

    override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem == newItem
    }
}