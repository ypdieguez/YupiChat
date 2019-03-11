package com.sapp.yupi.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.method.TextKeyListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sapp.yupi.*
import com.sapp.yupi.adapters.MessageAdapter
import com.sapp.yupi.databinding.FragmentConversationBinding
import com.sapp.yupi.utils.Injector
import com.sapp.yupi.utils.MessageNotification
import com.sapp.yupi.utils.SmsUtil
import com.sapp.yupi.utils.NetworkStatus
import com.sapp.yupi.viewmodels.ConversationViewModel
import com.sapp.yupi.viewmodels.MessageViewModel


class ConversationFragment : Fragment() {

    private lateinit var mBinding: FragmentConversationBinding

    private var mAdapter: MessageAdapter = MessageAdapter()

    private lateinit var mMsgViewModel: MessageViewModel
    private lateinit var mConvViewModel: ConversationViewModel

    private lateinit var mName: String
    private lateinit var mPhone: String

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val phone = intent.getStringExtra(PHONE_NOTIFICATION)
            if (phone == mPhone) {
                mConvViewModel.markConversationAsRead(mPhone)
                abortBroadcast()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Get arguments
        val args = ConversationFragmentArgs.fromBundle(arguments!!)
        mPhone = args.phone
        mName = args.name

        // Get Message ViewModel
        val factory = Injector.provideMessageViewModelFactory(requireContext())
        mMsgViewModel = ViewModelProviders.of(this, factory).get(MessageViewModel::class.java)

        // Get Conversation ViewModel
        val convFactory = Injector.provideConversationModelFactory(requireContext())
        mConvViewModel = ViewModelProviders.of(this, convFactory)
                .get(ConversationViewModel::class.java)

        // Set title to toolbar
        (requireActivity() as MainActivity).setTitle(mName)

        // Menu
        setHasOptionsMenu(true)

        // Mark conversation as read
        mConvViewModel.markConversationAsRead(mPhone)

        // Observe messages
        mMsgViewModel.getMessagesForConversation(mPhone).observe(this, Observer { messages ->
            if (messages != null && messages.isNotEmpty()) {
                mAdapter.submitList(messages)
            }
        })

        // Binding
        mBinding = FragmentConversationBinding.inflate(inflater, container, false)
        mBinding.apply {
            messageList.let {

                val manager = LinearLayoutManager(activity)
                manager.stackFromEnd = true

                mAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                        manager.smoothScrollToPosition(it, null,
                                positionStart + itemCount)
                    }
                })

                it.adapter = mAdapter
                it.layoutManager = manager
            }

            composeMessage.apply {
                textEditor.requestFocus()

                sendMessageBtn.setOnClickListener {
                    val msg = textEditor.text.toString()
                    if (msg.trim().isNotEmpty()) {
                        if (NetworkStatus.isConnected()) {
                            // Send msg
                            SmsUtil.handleOutgoingMsg(mPhone, msg)
                            // Focus to the text editor.
                            textEditor.requestFocus()
                            // Clear the text box.
                            TextKeyListener.clear(textEditor.text)
                        } else {
                            Snackbar.make(mBinding.root, getString(R.string.network_not_connected),
                                    Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
            }

            return root
        }
    }

    override fun onStart() {
        super.onStart()

        val filter = IntentFilter(BROADCAST_NOTIFICATION)
        filter.priority = 1
        context?.registerReceiver(receiver, filter)
    }

    override fun onStop() {
        super.onStop()
        context?.unregisterReceiver(receiver)
    }
}
