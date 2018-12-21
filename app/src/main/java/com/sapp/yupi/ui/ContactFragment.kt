package com.sapp.yupi.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.sapp.yupi.Injector
import com.sapp.yupi.R
import com.sapp.yupi.data.Contact
import com.sapp.yupi.databinding.FragmentContactBinding
import com.sapp.yupi.viewmodel.ContactViewModel


class ContactFragment : Fragment() {

    private lateinit var binding: FragmentContactBinding
    private lateinit var model: ContactViewModel
    private var mContact: Contact? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentContactBinding.inflate(inflater, container, false)
        val context = context ?: return binding.root

        val factory = Injector.provideContactViewModelFactory(context)
        model = ViewModelProviders.of(this, factory).get(ContactViewModel::class.java)

        val id = ContactFragmentArgs.fromBundle(arguments!!).id.toLong()

        model.getContact(id).observe(this, Observer { contact ->
            mContact = contact
            binding.contact = contact

            if (mContact == null) {
                binding.nameView.requestFocus()
                requestInputMethod()
            }
        })

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_contact, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save -> {
                val name = binding.nameView.text.toString()
                val phone = binding.phoneView.text.toString()

                if (name.isEmpty()) {
                    binding.nameView.error = "Campo obligatorio"
                }
                if (phone.isEmpty()) {
                    binding.phoneView.error = "Campo obligatorio"
                }

                if(name.isNotEmpty() and phone.isNotEmpty()){
                    if (mContact == null) {
                        mContact = Contact(name, phone)
                        model.insert(mContact!!)
                    } else {
                        mContact!!.name = name
                        mContact!!.phone = phone
                        model.update(mContact!!)
                    }
                    activity!!.onBackPressed()
//                    findNavController().popBackStack()
                }

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Sets the required flags on the dialog window to enable input method window to show up.
     */
    private fun requestInputMethod() {
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }
}
