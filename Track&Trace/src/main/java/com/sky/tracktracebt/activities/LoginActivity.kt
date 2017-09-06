package com.sky.tracktracebt.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.sky.tracktracebt.*
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.*

/**
 * Created by Sky_Alligator on 3/3/2017.
 * 10:41 AM
 */
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val (n, p) = getUserData(this)
        val bundle = intent.extras

        if (bundle != null && bundle.getBoolean("logout") && !n.isNullOrBlank() && !p.isNullOrBlank()) {
            ui_RememberMeChbx.isChecked = true
            ui_UserNameField.setText(n)
            ui_PasswordField.setText(p)
        } else if (!n.isNullOrBlank() && !p.isNullOrBlank()) {
            setViewVisible(false, ui_UserNameField, ui_PasswordField, ui_LoginBtn, ui_RememberMeChbx)
            ui_RememberMeChbx.isChecked = true
            loginNOlist(n, p)
        }

        ui_UserNameField.requestFocus()
        hideKeyboard(this)

        ui_LoginBtn.setOnClickListener {
            val name = ui_UserNameField.text.toString()
            val password = ui_PasswordField.text.toString()
            loginNOlist(name, password)
        }
    }

    private fun loginNOlist(name: String, password: String) {

        if (validateFields(name, password)) {
            ui_LoginBtn.enabled = false
            setViewVisible(true, ui_login_progV)

            doAsync {
                apiCallLoginNOlist(act, name, password) { type, statusCode, success, oList ->
                    uiThread {
                        setViewVisible(false, ui_login_progV)
                        ui_LoginBtn.enabled = true

                        when (type) {
                            ApiType.OLIST -> {
                                AppData.loginUserName = name
                                if (success) {
                                    if (updateData(oList)) {
                                        if (ui_RememberMeChbx.isChecked) setUserData(this@LoginActivity, name, password)
                                        else clearUserData(this@LoginActivity)

                                        startActivity<HomeTabActivity>()
                                        finish()
                                    }
                                } else
                                    toast("Error getting Owner List")
                            }
                            ApiType.LOGIN -> {
                                if (statusCode == 200) toast("Incorrect username/password")
                                else toast("Server Error, Unable to Login")
                                setViewVisible(true, ui_UserNameField, ui_PasswordField, ui_LoginBtn, ui_RememberMeChbx)
                            }
                        }
                    }
                }
            }
        }
    }

    fun validateFields(name: String, pass: String): Boolean {
        when {
            !internetConnected(this) -> toast("No Internet connection")
            name.isNullOrBlank() -> toast("username field can't be left blank")
            pass.isNullOrBlank() -> toast("password field can't be left blank")
            isInValidUser(name) -> toast("Invalid UserName")
            isInValidPassword(pass) -> toast("Invalid Password")
            else -> return true
        }
        return false
    }

    private fun isInValidPassword(name: String) = name != "blockchain"


    private fun isInValidUser(name: String) = when (name) {
        AppData.FLEET_MANAGER -> false
        AppData.OPERATIONS_MANAGER -> false
        AppData.PURCHASING_MANAGER -> false
        AppData.SUPPLY_MANAGER -> false
        else -> true
    }

    /**
     * split the owner list for each category and hide the unwanted category's
     */
    private fun updateData(arrayList: ArrayList<ReceiverData>?): Boolean {

        if (arrayList != null) {
            val data = arrayList.find { it.role == AppData.loginUserName }
            if (data == null) {
                toast("Error getting Owner List")
                return false
            }
            if (data.role == "Insurer") {
                toast("Incorrect username/password")
                return false
            }

            AppData.loginActualName = data.actualName
            AppData.loginRole = data.role
            AppData.loginOwnerId = data.id
            AppData.fleetManager = arrayList.filter { it.role.contains(AppData.FLEET_MANAGER, true) }
            AppData.operationsManager = arrayList.filter { it.role.contains(AppData.OPERATIONS_MANAGER, true) }
            AppData.purchasingManager = arrayList.filter { it.role.contains(AppData.PURCHASING_MANAGER, true) }
            AppData.supplyManager = arrayList.filter { it.role.contains(AppData.SUPPLY_MANAGER, true) }
            return true
        }
        return false
    }
}
