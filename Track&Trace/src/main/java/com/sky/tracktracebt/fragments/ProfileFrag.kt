package com.sky.tracktracebt.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import com.sky.tracktracebt.AppData
import com.sky.tracktracebt.R

import com.sky.tracktracebt.activities.LoginActivity
import com.sky.tracktracebt.clearUserData
import com.sky.tracktracebt.setTitleBar

import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.frag_profile.*
import kotlinx.android.synthetic.main.lay_alertdialog_profile.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.io.File
import java.io.FileOutputStream


/**
 * Created by Sky_Alligator on 4/10/2017.
 * 11:03 PM
 */
class ProfileFrag : FragmentMom(R.layout.frag_profile) {

    val CAMERA_REQUEST = 1888
    val CAMERA_PERMISSION_REQUEST = 9808
    val WRITE_PERMISSION_REQUEST = 4508
    val READ_PERMISSION_REQUEST = 4538
    val profileFileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + "/profileImage.png"

    lateinit var phoneNo: String
    lateinit var workNo: String
    lateinit var emailID: String

    val isEditMode: Boolean
        get() = ui_profile_editV.tag == "edit_mode"


    override fun onBackPressed() {
        activity.finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            val bitmap = data?.extras?.get("data") as Bitmap
            saveToFile(bitmap)
            updateProfilePic()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) toast("Permission Granted for Camera") else toast("Permission denied for Camera")
            }
            WRITE_PERMISSION_REQUEST -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) toast("Permission Granted for Storage") else toast("Permission denied for Storage")
            }
            READ_PERMISSION_REQUEST -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) toast("Permission Granted for Storage Access") else toast("Permission denied for Storage Access")
            }
        }
    }

    private fun saveToFile(bmp: Bitmap) {
        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(profileFileName)
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out)
        } catch (e: Exception) {
            e.printStackTrace()
            toast("Could not save the profile image")
        } finally {
            try {
                out?.close()
            } catch (ex: Exception) {
            }
        }
    }

    override fun onViewCreated(recyclerV: View?, savedInstanceState: Bundle?) {
        setTitleBar("Profile", false)
        getProfileData(activity)
        updateUI()

        ui_profile_logoutV.setOnClickListener {
            activity.finish()
            clearUserData(activity)
            startActivity<LoginActivity>()
        }

        ui_profile_editV.setOnClickListener {
            if (isEditMode) doneEdit()
            else beginEdit()
        }

        ui_profile_imgV.setOnClickListener {

            val cameraPm = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            if (!cameraPm)
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST)

            val writePm = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            if (!writePm)
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_PERMISSION_REQUEST)

            val readPm = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            if (!readPm)
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), READ_PERMISSION_REQUEST)

            if (cameraPm && writePm && readPm && isEditMode) startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), CAMERA_REQUEST)
        }

        ui_profile_fieldsContainerV.setOnClickListener Listener@ {
            if (!isEditMode) return@Listener

            alert("Edit Profile", null) {
                val v = activity.layoutInflater.inflate(R.layout.lay_alertdialog_profile, null)
                customView(v)
                v.ui_profile_alertD_phoneV.setText(ui_profile_MobileTv.text.toString())
                v.ui_profile_alertD_workV.setText(ui_profile_WorkTv.text.toString())
                v.ui_profile_alertD_emailV.setText(ui_profile_EmailTv.text.toString())

                v.ui_profile_alertD_okBtn.setOnClickListener {
                    setProfileData(activity,
                            v.ui_profile_alertD_phoneV.text.toString(),
                            v.ui_profile_alertD_workV.text.toString(),
                            v.ui_profile_alertD_emailV.text.toString())
                    getProfileData(activity)
                    updateProfileFields()
                    doneEdit()
                    dismiss()
                }
                v.ui_profile_alertD_cancelBtn.setOnClickListener {
                    toast("Canceled")
                    dismiss()
                }
            }.show()
        }
    }

    private fun updateUI() {
        ui_profile_nameV.text = AppData.loginActualName
        updateProfileFields()
        updateProfilePic()
    }

    private fun updateProfileFields() {
        ui_profile_MobileTv.text = if (phoneNo.isBlank()) getString(R.string.profile_mobile) else phoneNo
        ui_profile_WorkTv.text = if (workNo.isBlank()) getString(R.string.profile_work) else workNo
        ui_profile_EmailTv.text = if (emailID.isBlank()) getString(R.string.profile_email) else emailID
    }

    private fun updateProfilePic() {
        val file = File(profileFileName)
        if (file.exists())
            Picasso.with(activity).load(file).memoryPolicy(MemoryPolicy.NO_CACHE).into(ui_profile_imgV)
        else Picasso.with(activity).load(R.drawable.homescreen_login_icon).into(ui_profile_imgV)
    }

    private fun beginEdit() {
        ui_profile_editV.tag = "edit_mode"
        ui_profile_editV.imageResource = R.drawable.profile_done
        toast("Click Field or Profile image to Edit")
    }

    private fun doneEdit() {
        ui_profile_editV.tag = "actual_mode"
        ui_profile_editV.imageResource = R.drawable.profile_edit_ic
        toast("Saved to profile")
    }

    fun getProfileData(context: Context) {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        phoneNo = pref.getString("profile_phoneNo", "")
        workNo = pref.getString("profile_workNo", "")
        emailID = pref.getString("profile_emailId", "")
    }

    fun setProfileData(context: Context, ph: String, wrk: String, eml: String) {
        with(PreferenceManager.getDefaultSharedPreferences(context).edit()) {
            putString("profile_phoneNo", ph)
            putString("profile_workNo", wrk)
            putString("profile_emailId", eml)
            apply()
        }
    }

}