package com.sparktech.studyzoneadmin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.sparktech.studyzoneadmin.databinding.LoginLayoutBinding
import com.sparktech.studyzoneadmin.helpers.saveAdminLoginData
import com.sparktech.studyzoneadmin.helpers.validateAdminEmail
import com.sparktech.studyzoneadmin.helpers.validateAdminPassword
import com.sparktech.studyzoneadmin.models.AuthLogin
import com.sparktech.studyzoneadmin.network.Network
import com.wajahatkarim3.easyvalidation.core.view_ktx.validEmail
import kotlinx.coroutines.*

class Auth : AppCompatActivity() {
    private lateinit var binding: LoginLayoutBinding
    private val job = Job()
    private val networkScope = CoroutineScope(Dispatchers.IO + job)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.login_layout)
        authenticateAdmin()
        setupListener()

    }

    private fun authenticateAdmin() {
        //check if user is already logged in and navigate to main screen instead
        val sp = this.getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
        //check login state
        val loggedIn = sp.getBoolean("loggedIn", false)
        val email = sp.getString("email", "")
        val adminId = sp.getString("adminId", "")
        if (adminId != null && email != null) {
            if (loggedIn && email.validEmail() && adminId.isNotEmpty()) {
                navigateToMainActivity()
            }

        }
    }

    private fun setupListener() {
        binding.apply {
            loginBtn.setOnClickListener {
                it.visibility = View.GONE
                authLoader.visibility = View.VISIBLE
                //get inputs from the text inputs
                val email = emailInput.text.toString()
                val password = passwordInput.text.toString()
                //validate the inputs
                val isEmailValid = validateAdminEmail(email)
                val isPasswordValid = validateAdminPassword(password)
                //if validation fails notify user and show login button again
                if (!isEmailValid || !isPasswordValid) {
                    it.visibility = View.VISIBLE
                    authLoader.visibility = View.GONE
                    Toast.makeText(this@Auth, "Invalid email or password", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener Unit
                }
                //if validation succeeds start network requests
                makeNetWorkRequest(email, password, binding.loginBtn, binding.authLoader)
            }
        }
    }

    private fun makeNetWorkRequest(email: String, password: String, loginBtn: Button, authLoader: ProgressBar) {
        networkScope.launch {
            try {
                val response = Network.apiService.adminLogin(AuthLogin(email, password))
                //hide button and show spinner
                withContext(Dispatchers.Main) {
                    loginBtn.visibility = View.VISIBLE
                    authLoader.visibility = View.GONE
                }
                //process response response
                val sp = this@Auth.getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
                saveAdminLoginData(sp, response.email, response.loggedIn, response.role, response.adminId)
                //navigate to main activity
                navigateToMainActivity()
                Log.i("RESPONSE", response.toString())
            } catch (e: Exception) {
                e.suppressed
                e.printStackTrace()
                Log.i("NETWORK", "${e.message}", e.cause)
                //if request code does not default to 200
                //notify user
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Auth,
                            "an error occurred ensure details supplied are authentic", Toast.LENGTH_SHORT).show()

                    //toggle ui elements
                    loginBtn.visibility = View.VISIBLE
                    authLoader.visibility = View.GONE
                }
            }
        }
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finishAfterTransition()
    }
}