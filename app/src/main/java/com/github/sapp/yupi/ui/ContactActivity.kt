package com.github.sapp.yupi.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.github.sapp.yupi.Injector
import com.github.sapp.yupi.R
import com.github.sapp.yupi.data.Contact
import com.github.sapp.yupi.databinding.ActivityContactBinding
import com.github.sapp.yupi.viewmodel.ContactViewModel
import androidx.lifecycle.Observer


class ContactActivity : AppCompatActivity() {

    lateinit var binding: ActivityContactBinding
    lateinit var model: ContactViewModel
    private var mContact: Contact? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_contact)

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.apply {
            title = if (intent.extras == null) "Crear contacto" else "Editar contacto"
            setDisplayHomeAsUpEnabled(true)
        }

        val factory = Injector.provideContactViewModelFactory(this)
        model = ViewModelProviders.of(this, factory).get(ContactViewModel::class.java)

        val id = intent.getIntExtra("id", 0)

        model.getContact(id).observe(this, Observer { contact ->
            mContact = contact
            binding.contact = contact

            if (mContact == null) {
                binding.nameView.requestFocus()
                requestInputMethod()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_contact, menu)
        return super.onCreateOptionsMenu(menu)
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
                    onBackPressed()
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
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }
}
