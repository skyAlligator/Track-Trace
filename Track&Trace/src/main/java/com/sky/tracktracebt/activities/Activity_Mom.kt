package com.sky.tracktracebt.activities

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Window
import com.sky.tracktracebt.FragmentBackPress
import com.sky.tracktracebt.R
import kotlinx.android.synthetic.main.alert_logout.view.*
import kotlinx.android.synthetic.main.common_action_bar.*

/**
 * Created by Sky_Alligator on 4/7/2017.
 * 12:47 PM
 */
abstract class Activity_Mom(val layout: Int) : AppCompatActivity() {

    var backListener: FragmentBackPress? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout)

        ui_actionBar_logoutV.setOnClickListener {
            showLogoutAlert()
        }
    }

    private fun showLogoutAlert() {
        val builder = AlertDialog.Builder(this)
        val alertV = layoutInflater.inflate(R.layout.alert_logout, null)
        builder.setView(alertV)

        val dialog = builder.create()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        alertV.ui_alert_logout_cancelV.setOnClickListener {
            dialog.dismiss()
        }
        alertV.ui_alert_logout_yesV.setOnClickListener {
            dialog.dismiss()
            processLogout()
        }
        dialog.show()
    }

    private fun processLogout() {
        val i = Intent(this, LoginActivity::class.java)
        i.putExtra("logout", true)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
    }

    override fun onBackPressed() {
        backListener?.let {
            it.onBackPressed()
            return
        }
        super.onBackPressed()
    }
}