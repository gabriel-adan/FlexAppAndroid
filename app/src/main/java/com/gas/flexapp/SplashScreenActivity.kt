package com.gas.flexapp

import android.accounts.Account
import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import com.gas.flexapp.ui.ApplicationListActivity
import com.gas.flexapp.ui.FlexAppLoginActivity
import com.gas.flexapp.viewmodels.FlexAppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {
    private val flexAppViewModel: FlexAppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val accountManager = AccountManager.get(this)
        val accountType = getString(R.string.account_type)
        val accounts = accountManager.getAccountsByType(accountType)
        if (accounts.isNotEmpty()) {
            val account = accounts.first()
            val userName = account.name
            val encryptedPassword = accountManager.getPassword(account)
            val password = flexAppViewModel.decrypt(encryptedPassword)
            lifecycleScope.launch {
                flexAppViewModel.logIn(userName, password, { data ->
                    val authTokenType = getString(R.string.token_type)
                    val accessToken = data.getString(authTokenType, "")
                    val encryptedAccessToken = flexAppViewModel.encrypt(accessToken)
                    accountManager.setAuthToken(account, getString(R.string.token_type), encryptedAccessToken)
                    val intent = Intent(applicationContext, ApplicationListActivity::class.java)
                    startActivity(intent)
                    finish()
                }, {

                })
            }
        } else {
            val logInResponse = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    RESULT_OK -> {
                        it.data?.also { data ->
                            val authTokenType = getString(R.string.token_type)
                            val accessToken = data.getStringExtra(authTokenType) ?: ""
                            val encryptedAccessToken = flexAppViewModel.encrypt(accessToken)
                            val userName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME) ?: ""
                            val password = data.getStringExtra(AccountManager.KEY_PASSWORD) ?: ""
                            val encryptedPassword = flexAppViewModel.encrypt(password)
                            val account = Account(userName, this.getString(R.string.account_type))
                            val fullNameKey = getString(R.string.full_name)
                            val userData = bundleOf().also { bundle ->
                                bundle.putString(fullNameKey, data.getStringExtra(fullNameKey))
                            }
                            accountManager.addAccountExplicitly(account, encryptedPassword, userData)
                            accountManager.setAuthToken(account, this.getString(R.string.token_type), encryptedAccessToken)
                            val intent = Intent(this, ApplicationListActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                    RESULT_CANCELED -> {

                    }
                }
            }

            val logInIntent = Intent(applicationContext, FlexAppLoginActivity::class.java)
            logInResponse.launch(logInIntent)
        }
    }
}