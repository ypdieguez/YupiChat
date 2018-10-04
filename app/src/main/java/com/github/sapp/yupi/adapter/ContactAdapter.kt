package com.github.sapp.yupi.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.sapp.yupi.data.Contact
import com.github.sapp.yupi.databinding.ListItemContactBinding
import com.github.sapp.yupi.ui.ContactActivity
import com.github.sapp.yupi.ui.ConversationActivity

/**
 * Adapter for the [RecyclerView] in [com.github.sapp.yupi.MainActivity].
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

    private fun createOnClickListener(id: Int): View.OnClickListener {
        return View.OnClickListener {
            //            val direction = PlantListFragmentDirections.ActionPlantListFragmentToPlantDetailFragment(plantId)
//            it.findNavController().navigate(direction)
        }
    }

    class ViewHolder(private val binding: ListItemContactBinding, private val listener: Listener)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Contact) {
            binding.apply {
                contact = item
                clickListener = View.OnClickListener {
                    val context = it.context
                    context.startActivity(Intent(context, ConversationActivity::class.java)
                            .putExtra("name", contact!!.name)
                            .putExtra("phone", contact!!.phone)
                    )
                }
                editView.setOnClickListener {
                    it.context.startActivity(Intent(it.context, ContactActivity::class.java)
                            .putExtra("id", item.id)
                            .putExtra("name", item.name)
                            .putExtra("phone", item.phone)
                    )
                }
                deleteView.setOnClickListener {
                    listener.delete(contact!!)
                }
                phoneActionView.setOnClickListener {
                    startActivity(it.context,
                            Intent(Intent.ACTION_DIAL).setData(Uri.parse("tel:${item.phone}")),
                            null)
                }

                executePendingBindings()
            }
        }
    }
}