package com.sapp.yupi.ui.appintro


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.ISlidePolicy
import com.sapp.yupi.R
import com.sapp.yupi.databinding.ViewPermissionBinding
import com.sapp.yupi.util.UIUtils

private const val PERMISSION_PREFERENCES = "permissions_preferences"
private const val PREF_FIRST_READ_SMS_PERMISSION = "first_read_sms_permission"

class ReadSmsPermissionFragment : Fragment(), ISlidePolicy {

    private lateinit var pref: SharedPreferences
    private lateinit var mBinding: ViewPermissionBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = ViewPermissionBinding.inflate(inflater, container, false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Initialize preferences
        pref = context!!.getSharedPreferences(PERMISSION_PREFERENCES, Context.MODE_PRIVATE)

        pref.takeIf { it.getBoolean(PREF_FIRST_READ_SMS_PERMISSION, true) }?.apply {
            mBinding.apply {
                btn.text = getString(R.string.intro_btn_grant_permission)
                btn.setOnClickListener {
                    requestPermissions(arrayOf(Manifest.permission.READ_SMS), 1)
                    edit { putBoolean(PREF_FIRST_READ_SMS_PERMISSION, false) }
                }

                error.text = ""
                error.visibility = View.GONE
            }
        } ?: mBinding.apply {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_SMS)) {
                btn.text = getString(R.string.settings)
                btn.setOnClickListener {
                    startActivityForResult(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:" + activity!!.packageName)), 1)
                }

                error.text = getString(R.string.read_sms_denied_permanetly)
                error.visibility = View.VISIBLE
            } else {
                btn.text = getString(R.string.intro_btn_grant_permission)
                btn.setOnClickListener {
                    requestPermissions(arrayOf(Manifest.permission.READ_SMS), 1)
                }

                error.text = ""
                error.visibility = View.GONE
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        mBinding.apply {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                btn.visibility = View.GONE
                error.visibility = View.GONE
                description.setText(R.string.intro_permission_description_granted)
            } else {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_SMS)) {
                    btn.text = getString(R.string.settings)
                    btn.setOnClickListener {
                        startActivityForResult(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.parse("package:" + activity!!.packageName)), 1)
                    }

                    error.text = getString(R.string.read_sms_denied_permanetly)
                    error.visibility = View.VISIBLE
                } else {
                    btn.text = getString(R.string.intro_btn_grant_permission)
                    btn.setOnClickListener {
                        requestPermissions(arrayOf(Manifest.permission.READ_SMS), 1)
                    }

                    error.text = getString(R.string.read_sms_denied)
                    error.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        mBinding.apply {
            if (UIUtils.checkReadSmsPermission(activity!!)) {
                btn.visibility = View.GONE
                error.visibility = View.GONE
                description.setText(R.string.intro_permission_description_granted)
            }
        }
    }

    override fun isPolicyRespected(): Boolean {
        return UIUtils.checkReadSmsPermission(context!!)
    }

    override fun onUserIllegallyRequestedNextPage() {
        mBinding.apply {
            val stringId = if (pref.getBoolean(PREF_FIRST_READ_SMS_PERMISSION, true)) {
                R.string.read_sms_denied
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_SMS)) {
                    R.string.read_sms_denied
                } else {
                    R.string.read_sms_denied_permanetly
                }
            }

            error.text = getString(stringId)
            error.visibility = View.VISIBLE
        }
    }
}
