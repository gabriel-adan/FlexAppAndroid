package com.gas.flexapp.ui

import android.accounts.AccountManager
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.graphics.toColorInt
import com.gas.flexapp.MainActivity
import com.gas.flexapp.R
import com.gas.flexapp.viewmodels.AuthViewModel
import com.gas.model.services.Authentication
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogInActivity : AppCompatActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val versionKey = getString(R.string.version_id)
        val versionId = intent.getIntExtra(versionKey, 0)

        val authService = authViewModel.getServiceModel("auth", versionId, Authentication::class.java)!!

        val progressBar = ProgressBar(this).also {
            it.id = View.generateViewId()
            val layoutParams = ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            it.setPadding(450, 450, 450, 450)
            it.layoutParams = layoutParams
            it.setBackgroundColor("#F2F2F2F2".toColorInt())
            it.visibility = View.GONE
            it.tag = "progressBar"
        }

        val linearLayout = LinearLayout(this).apply {
            id = View.generateViewId()
            orientation = LinearLayout.VERTICAL
            val layoutParams = ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(50, 0, 50, 0)
            this.layoutParams = layoutParams
        }

        val textView = TextView(this).also {
            it.text = "Inicio de sesion"
            val layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(0, 100, 0, 100)
            it.layoutParams = layoutParams
            it.textSize = 20f
            it.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            it.setTypeface(null, Typeface.BOLD)
        }
        linearLayout.addView(textView)

        val textViewUserName = TextView(this).also {
            it.text = "Nombre de usuario"
        }
        linearLayout.addView(textViewUserName)

        val editTextUserName = EditText(this)
        linearLayout.addView(editTextUserName)

        val textViewPassword = TextView(this).also {
            it.text = "Contraseña"
        }
        linearLayout.addView(textViewPassword)

        val editTextPassword = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        linearLayout.addView(editTextPassword)

        val button = Button(this).also {
            it.text = "Ingresar"
            it.isAllCaps = false
            val layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(0, 100, 0, 0)
            it.layoutParams = layoutParams
            it.setOnClickListener {
                progressBar.visibility = View.VISIBLE
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
                authViewModel.logIn(editTextUserName.text.toString(), editTextPassword.text.toString(), authService)
            }
        }
        linearLayout.addView(button)

        val constraintLayout = ConstraintLayout(this).apply {
            id = View.generateViewId()
            this.layoutParams = ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            addView(linearLayout)
            addView(progressBar)

            val constraintSet = ConstraintSet()
            constraintSet.clone(this)

            constraintSet.centerHorizontally(linearLayout.id, ConstraintSet.PARENT_ID)
            constraintSet.centerVertically(linearLayout.id, ConstraintSet.PARENT_ID)

            constraintSet.centerHorizontally(progressBar.id, ConstraintSet.PARENT_ID)
            constraintSet.centerVertically(progressBar.id, ConstraintSet.PARENT_ID)

            constraintSet.applyTo(this)
        }

        setContentView(constraintLayout)

        val accountManager = AccountManager.get(this)

        authViewModel.onAccessToken.observe(this) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

            val accounts = accountManager.getAccountsByType(getString(R.string.account_type))
            val account = accounts[0]
            val userName = it.getString(AccountManager.KEY_ACCOUNT_NAME, "")
            val password = it.getString(AccountManager.KEY_PASSWORD, "")
            val encryptedPassword = authViewModel.encrypt(password)

            val versionTokenType = getString(R.string.version_token_type)
            val accessToken = it.getString(authService.tokenFieldName, "")
            val encryptedAccessToken = authViewModel.encrypt(accessToken)

            accountManager.setUserData(account, AccountManager.KEY_ACCOUNT_NAME, userName)
            accountManager.setUserData(account, AccountManager.KEY_PASSWORD, encryptedPassword)
            accountManager.setUserData(account, versionKey, versionId.toString())
            accountManager.setAuthToken(account, versionTokenType, encryptedAccessToken)

            authService.claims.forEach { claim ->
                accountManager.setUserData(account, claim, it.getString(claim))
            }

            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(versionKey, versionId)
            startActivity(intent)
            finish()
        }

        authViewModel.onFail.observe(this) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            progressBar.visibility = View.GONE
            Toast.makeText(this, it.get(authService.responseErrorMessageFieldName).asString, Toast.LENGTH_LONG).show()
        }
    }
}