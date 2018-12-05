package com.sapp.yupi.ui.appintro


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.ISlidePolicy
import com.sapp.yupi.R
import com.sapp.yupi.databinding.ViewPermissionBinding
import com.sapp.yupi.util.UIUtils

class ReadSmsPermissionFragment : Fragment(), ISlidePolicy {

    private lateinit var mBinding: ViewPermissionBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = ViewPermissionBinding.inflate(inflater, container, false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        managePermission()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        mBinding.apply {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                btn.visibility = View.GONE
                description.text = getString(R.string.intro_permssion_description_granted)
            } else {
                managePermission()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        mBinding.apply {
            if (UIUtils.checkPermission(activity!!)) {
                btn.visibility = View.GONE
                description.text = getString(R.string.intro_permssion_description_granted)
            }
        }
    }

    override fun isPolicyRespected(): Boolean {
        return UIUtils.checkPermission(activity!!)
    }

    override fun onUserIllegallyRequestedNextPage() {
        mBinding.apply {
            description.text = getString(R.string.intro_description_permission_denied)
        }
    }

    private fun managePermission() {
        mBinding.btn.apply {
            if(!shouldShowRequestPermissionRationale(Manifest.permission.READ_SMS)){
                text = getString(R.string.settings)
                setOnClickListener {
                    startActivityForResult(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:" + activity!!.packageName)), 1)
                }
                mBinding.description.text = "Bla Bla"
            } else {
                text = getString(R.string.intro_btn_grant_permission)
                setOnClickListener {
                    requestPermissions(arrayOf(Manifest.permission.READ_SMS), 1)
                }
            }
        }
    }
}
