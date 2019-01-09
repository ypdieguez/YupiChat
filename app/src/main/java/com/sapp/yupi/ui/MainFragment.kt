package com.sapp.yupi.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.sapp.yupi.Injector
import com.sapp.yupi.R
import com.sapp.yupi.adapters.ContactAdapter
import com.sapp.yupi.data.Contact
import com.sapp.yupi.databinding.FragmentMainBinding
import com.sapp.yupi.viewmodels.ContactViewModel


class MainFragment : Fragment(), ContactAdapter.Listener {
    private lateinit var model: ContactViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentMainBinding.inflate(inflater, container, false)
        val context = context ?: return binding.root

        val factory = Injector.provideContactViewModelFactory(requireContext())
        model = ViewModelProviders.of(this, factory).get(ContactViewModel::class.java)

        val adapter = ContactAdapter(this)
        binding.contactList.apply {
            this.adapter = adapter
            addOnItemTouchListener(ItemTouchListener(context, this))
        }

        model.getContacts().observe(this, Observer { contacts ->
            if (contacts != null && contacts.isNotEmpty()) {
                adapter.submitList(contacts)
                binding.hasContacts = true
            } else {
                binding.hasContacts = false
            }
        })

        binding.fab.setOnClickListener {
            it.findNavController().navigate(R.id.action_mainFragment_to_contactFragment)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        IncomingMsgNotification.notify(context!!, "Tienes un mensaje nuevo.", 1)

//        val contentResolver = activity!!.contentResolver

//        val smsObserver = SMSObserver(contentResolver, Handler())
//        smsObserver.getReceivedMmsInfo()
//        smsObserver.onChange(false)

//        val mmsObserver = MmsObserver(contentResolver)
//        mmsObserver.onChange(false)

//        cursor?.apply {
//            moveToFirst()
//            for (i in 45..52){
//                val valor = getString(i)
//                val a = valor
//            }
//        }


//        contentResolver.registerContentObserver(
//                Uri.parse("content://sms/"),
//                true,
//                SMSObserver(contentResolver, Handler())
//        )
    }

    override fun delete(contact: Contact) {
        model.delete(contact)
    }
}
