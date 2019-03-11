package com.sapp.yupi.ui

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.sapp.yupi.R
import com.sapp.yupi.adapters.ContactAdapter
import com.sapp.yupi.databinding.FragmentContactBinding
import com.sapp.yupi.utils.Injector
import com.sapp.yupi.utils.PermissionUtil
import com.sapp.yupi.viewmodels.ContactViewModel


class ContactFragment : Fragment() {

    private lateinit var mBinding: FragmentContactBinding
    private lateinit var mViewModel: ContactViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (!PermissionUtil.hasReadContactPermission(requireContext())) {
            startActivity(Intent(requireContext(), MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK)
            })
            return null
        }

        mBinding = FragmentContactBinding.inflate(inflater, container, false)
        mBinding.apply {
            val adapter = ContactAdapter()
            contactList.adapter = adapter

            val factory = Injector.provideContactViewModelFactory(requireContext())
            mViewModel = ViewModelProviders.of(requireActivity(), factory)
                    .get(ContactViewModel::class.java)
            mViewModel.contacts.observe(viewLifecycleOwner, Observer {
                hasContacts = if (it != null && it.isNotEmpty()) {
                    adapter.submitList(it)
                    true
                } else {
                    false
                }
            })

            setHasOptionsMenu(true)

            return root
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_contact, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_contact -> {
                val i = Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI)
                startActivityForResult(i, 1)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mViewModel.refreshContacts()
    }
}
