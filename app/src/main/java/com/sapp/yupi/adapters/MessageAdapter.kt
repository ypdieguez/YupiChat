package com.sapp.yupi.adapters

import android.graphics.PorterDuff
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sapp.yupi.R
import com.sapp.yupi.STATUS_SENDING
import com.sapp.yupi.STATUS_SUCCESS
import com.sapp.yupi.TYPE_INCOMING
import com.sapp.yupi.data.Message
import com.sapp.yupi.databinding.ViewMessageBinding
import com.sapp.yupi.util.MessageUtils.formatTimeStampString


class MessageAdapter : ListAdapter<Message, MessageAdapter.ViewHolder>(MessageDiffCallback()) {

    var mMaxWith = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewMessageBinding.inflate(LayoutInflater.from(parent.context), parent,
                false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ViewMessageBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message) {
            binding.apply {
                setMessage(message)
                updateViewAppearance(this, message)

                executePendingBindings()
            }
        }

        private fun updateViewAppearance(binding: ViewMessageBinding, msg: Message) {
            binding.apply {
                val gravity: Int
                @ColorRes val bubbleColor: Int
                @ColorRes val messageColor: Int
                @ColorRes val dateColor: Int

                if (msg.type == TYPE_INCOMING) {
                    gravity = Gravity.START
                    bubbleColor = R.color.bubble_incoming_color
                    messageColor = R.color.msg_incoming_color
                    dateColor = R.color.date_incoming_color
                } else {
                    gravity = Gravity.END
                    bubbleColor = R.color.bubble_outgoing_color
                    messageColor = R.color.msg_outgoing_color
                    dateColor = R.color.date_outgoing_color
                }

                // Context
                val context = root.context
                // Message Container
                messageContainerView.gravity = gravity
                // Bubble
                bubbleView.background.setColorFilter(ContextCompat.getColor(context, bubbleColor),
                        PorterDuff.Mode.SRC_ATOP)
                // Message
                messageView.setTextColor(ContextCompat.getColor(context, messageColor))
                // Date
                dateView.text = formatTimeStampString(context, msg.date)
                dateView.setTextColor(ContextCompat.getColor(context, dateColor))
                // Status
                statusView.setImageResource(
                        when (msg.status) {
                            STATUS_SENDING -> R.mipmap.ic_dots_horizontal
                            STATUS_SUCCESS -> R.mipmap.ic_check
                            else -> R.mipmap.ic_exclamation
                        })
                statusView.setColorFilter(ContextCompat.getColor(context, dateColor))
            }
        }
    }
}