package com.sapp.yupi.adapters

import android.content.Intent
import android.net.Uri
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sapp.yupi.data.Conversation
import com.sapp.yupi.databinding.ItemConversationBinding
import com.sapp.yupi.ui.MainFragment
import com.sapp.yupi.ui.MainFragmentDirections

class ConversationAdapter(val fragment: MainFragment) :
        ListAdapter<Conversation, ConversationAdapter.ViewHolder>(ConversationDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemConversationBinding.inflate(
                LayoutInflater.from(parent.context), parent, false), fragment)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val conversation = getItem(position)
        holder.apply {
            bind(conversation)
            itemView.tag = conversation
        }
    }

    class ViewHolder(private val binding: ItemConversationBinding, private val fragment: MainFragment) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Conversation) {
            binding.apply {
                conversation = item

                clickListener = View.OnClickListener {
                    val direction = MainFragmentDirections
                            .actionMainFragmentToConversationFragment(item.phone, item.contact.name)
                    it.findNavController().navigate(direction)
                }

                deleteView.setOnClickListener {
                    fragment.deleteConversation(item)
                }

                phoneAddress.text = SpannableStringBuilder(item.phone)

                phoneActionView.setOnClickListener {
                    ContextCompat.startActivity(it.context,
                            Intent(Intent.ACTION_DIAL)
                                    .setData(Uri.parse("tel:${item.phone}")), null)
                }

                executePendingBindings()
            }
        }
    }
}

private class ConversationDiffCallback : DiffUtil.ItemCallback<Conversation>() {

    override fun areItemsTheSame(oldItem: Conversation, newItem: Conversation): Boolean {
        return oldItem.phone == newItem.phone
    }

    override fun areContentsTheSame(oldItem: Conversation, newItem: Conversation): Boolean {
        return oldItem == newItem
    }
}