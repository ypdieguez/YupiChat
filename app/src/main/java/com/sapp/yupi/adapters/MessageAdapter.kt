package com.sapp.yupi.adapters

import android.graphics.PorterDuff
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sapp.yupi.R
import com.sapp.yupi.STATUS_SENDING
import com.sapp.yupi.STATUS_SUCCESS
import com.sapp.yupi.TYPE_INCOMING
import com.sapp.yupi.data.Message
import com.sapp.yupi.databinding.ViewMessageBinding
import com.sapp.yupi.utils.SmsUtil
import com.sapp.yupi.utils.NetworkStatus


class MessageAdapter : ListAdapter<Message, MessageAdapter.ViewHolder>(MessageDiffCallback()) {

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
                updateViewAppearance(message)

                executePendingBindings()
            }
        }

        private fun updateViewAppearance(msg: Message) {
            binding.apply {
                // Context
                val context = root.context

                val gravity: Int
                @ColorRes val bubbleColor: Int
                @ColorRes val messageColor: Int
                @ColorRes val dateColor: Int

                var clickable = false

                if (msg.type == TYPE_INCOMING) {
                    gravity = Gravity.START
                    bubbleColor = R.color.bubble_incoming_color
                    messageColor = R.color.msg_incoming_color
                    dateColor = R.color.date_incoming_color

                    // Status is not necessary in incoming msg.
                    statusContainer.visibility = View.GONE
                } else {
                    gravity = Gravity.END
                    bubbleColor = R.color.bubble_outgoing_color
                    messageColor = R.color.msg_outgoing_color
                    dateColor = R.color.date_outgoing_color

                    // Status is necessary in outgoing msg.
                    if (msg.status == STATUS_SENDING) {
                        statusView.visibility = View.GONE
                        statusSendingView.visibility = View.VISIBLE
                    } else {
                        statusSendingView.visibility = View.GONE
                        statusView.visibility = View.VISIBLE

                        if (msg.status == STATUS_SUCCESS) {
                            statusView.setImageResource(R.drawable.check)
                        } else {
                            statusView.setImageResource(R.drawable.exclamation)
                            clickable = true
                        }
                    }
                }

                // BubbleView Click
                if (clickable) {
                    bubbleViewForeground.setOnClickListener {
                        if(NetworkStatus.isConnected()) {
                            SmsUtil.handleOutgoingMsg(msg.phone, msg.text, msg.id)
                        } else {
                            Snackbar.make(it, context.getString(R.string.network_not_connected),
                                    Snackbar.LENGTH_LONG).show()
                        }
                    }
                } else {
                    bubbleViewForeground.setOnClickListener(null)
                }
                bubbleViewForeground.isClickable = clickable

                // Message Container
                messageContainerView.gravity = gravity

                // Bubble
                bubbleView.background.setColorFilter(ContextCompat.getColor(context, bubbleColor),
                        PorterDuff.Mode.SRC_ATOP)

                // Message
                messageView.setTextColor(ContextCompat.getColor(context, messageColor))

                // Date
                dateView.setTextColor(ContextCompat.getColor(context, dateColor))
            }
        }
    }
}