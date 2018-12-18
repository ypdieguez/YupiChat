package com.sapp.yupi.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sapp.yupi.data.Contact
import com.sapp.yupi.databinding.ListItemContactBinding
import com.sapp.yupi.ui.MainFragmentDirections

/**
 * Adapter for the [RecyclerView] in [com.sapp.yupi.MainActivity].
 */
class ContactAdapter(listener: Listener)
    : ListAdapter<Contact, ContactAdapter.ViewHolder>(ContactDiffCallback()) {

    var mListener: Listener = listener

    interface Listener {
        fun delete(contact: Contact)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ListItemContactBinding.inflate(
                LayoutInflater.from(parent.context), parent, false), mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = getItem(position)
        holder.apply {
            bind(contact)
            itemView.tag = contact
        }
    }

    class ViewHolder(private val binding: ListItemContactBinding, private val listener: Listener)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(contact: Contact) {
            binding.apply {
                this.contact = contact

                clickListener = View.OnClickListener {
                    val action = MainFragmentDirections.actionMainFragmentToConversationFragment()
                    action.setContactId(contact.id.toString())
                    it.findNavController().navigate(action)
                }

                editView.setOnClickListener {
                    val action = MainFragmentDirections.actionMainFragmentToContactFragment()
                    action.setId(contact.id.toString())
                    it.findNavController().navigate(action)
                }

                deleteView.setOnClickListener {
                    listener.delete(contact)
                }

                phoneActionView.setOnClickListener {
                    startActivity(it.context,
                            Intent(Intent.ACTION_DIAL)
                                    .setData(Uri.parse("tel:${contact.phone}")), null)
                }

                executePendingBindings()
            }
        }
    }
}