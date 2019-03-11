package com.sapp.yupi.ui

import android.Manifest
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.sapp.yupi.*
import com.sapp.yupi.adapters.ConversationAdapter
import com.sapp.yupi.data.Conversation
import com.sapp.yupi.databinding.FragmentMainBinding
import com.sapp.yupi.utils.Injector
import com.sapp.yupi.utils.MessageNotification
import com.sapp.yupi.utils.PermissionUtil
import com.sapp.yupi.viewmodels.ConversationViewModel


class MainFragment : Fragment() {

    private lateinit var mBinding: FragmentMainBinding
    private lateinit var mViewModel: ConversationViewModel

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            abortBroadcast()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = FragmentMainBinding.inflate(inflater, container, false)

        val factory = Injector.provideConversationModelFactory(requireContext())
        mViewModel = ViewModelProviders.of(this, factory)
                .get(ConversationViewModel::class.java)

        val adapter = ConversationAdapter(this)
        mBinding.conversationList.apply {
            this.adapter = adapter
            addOnItemTouchListener(ItemTouchListener(context, this))
        }

        mViewModel.conversations.observe(this,
                Observer {
                    if (it != null && it.isNotEmpty()) {
                        adapter.submitList(it)
                        mBinding.hasContacts = true
                    } else {
                        mBinding.hasContacts = false
                    }
                })

        mBinding.fab.setOnClickListener {
            if (!askForReadContactPermission(REQUEST_CODE_READ_CONTACTS_FROM_BTN)) {
                it.findNavController().navigate(R.id.action_mainFragment_to_contactFragment)
            }
        }

        return mBinding.root
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        refresh(requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        refresh(requestCode)
    }

    private fun refresh(requestCode: Int) {
        if (PermissionUtil.hasReadContactPermission(requireContext())) {
            mViewModel.updateConversations(true)
            if (requestCode == REQUEST_CODE_READ_CONTACTS_FROM_BTN) {
                mBinding.fab.findNavController().navigate(R.id.action_mainFragment_to_contactFragment)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(BROADCAST_NOTIFICATION)
        filter.priority = 1
        context?.registerReceiver(receiver, filter)
    }

    override fun onResume() {
        super.onResume()
        context?.getSharedPreferences(PERMISSION_PREFERENCES, Context.MODE_PRIVATE)?.apply {
            if (!getBoolean(PREF_READ_CONTACT_PERMISSION_ASKED, false) &&
                    !getBoolean(PREF_READ_CONTACT_PERMISSION_ASKED_FROM_RESUME, false)) {
                if (askForReadContactPermission(REQUEST_CODE_READ_CONTACTS_FROM_RESUME)) {
                    edit {
                        putBoolean(PREF_READ_CONTACT_PERMISSION_ASKED_FROM_RESUME, true)
                    }
                }
            }
        }

        // Cancel notification
        MessageNotification.cancel(requireContext())
    }

    override fun onStop() {
        super.onStop()
        context?.unregisterReceiver(receiver)
    }

    fun deleteConversation(conversation: Conversation) {
        mViewModel.delete(conversation)
    }

    private fun askForReadContactPermission(requestCode: Int): Boolean {
        requireContext().let { context ->
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) !=
                    PackageManager.PERMISSION_GRANTED) {

                val pref = context.getSharedPreferences(PERMISSION_PREFERENCES, Context.MODE_PRIVATE)
                val isPermanentlyDenied = pref.run {
                    val isPermanentlyDenied =
                            getBoolean(PREF_READ_CONTACT_PERMISSION_ASKED, false)
                                    && !shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)

                    isPermanentlyDenied
                }

                val dialog = Dialog(context)
                dialog.apply {
                    setContentView(R.layout.permission_dialog)
                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    setCanceledOnTouchOutside(false)
                    setCancelable(false)

                    // ImageView Setup
                    val contactIcon = ImageView(context)
                    // Setting image resource
                    contactIcon.setImageResource(R.drawable.contacts)
                    // Setting image size
                    val size = resources.getDimensionPixelSize(R.dimen.icon)
                    contactIcon.layoutParams = LinearLayout.LayoutParams(size, size)
                    // Adding view to layout
                    findViewById<LinearLayout>(R.id.icons_container).addView(contactIcon)

                    val appendText = when (isPermanentlyDenied) {
                        true -> String.format(getString(R.string.perm_permanently_denied),
                                getString(R.string.contacts))
                        false -> ""
                    }

                    val textView = findViewById<AppCompatTextView>(R.id.permission_text)
                    textView.text = String.format(getString(R.string.perm_read_contact),
                            getString(R.string.app_name))
                    textView.append(" $appendText")

                    findViewById<AppCompatButton>(R.id.permission_yes).apply {
                        if (appendText != "") {
                            setText(R.string.settings)
                            setOnClickListener {
                                startActivityForResult(Intent(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.parse("package:" + context.packageName)),
                                        requestCode)
                                dismiss()
                            }
                        } else {
                            setText(R.string.go_on)
                            setOnClickListener {
                                requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), requestCode)
                                pref.edit {
                                    putBoolean(PREF_READ_CONTACT_PERMISSION_ASKED, true)
                                }
                                dismiss()
                            }
                        }
                    }

                    findViewById<AppCompatButton>(R.id.permission_no).setOnClickListener {
                        dismiss()
                    }
                }.show()

                return true
            }
            return false
        }
    }

    companion object {
        private const val REQUEST_CODE_READ_CONTACTS_FROM_BTN = 1
        private const val REQUEST_CODE_READ_CONTACTS_FROM_RESUME = 2
    }
}
