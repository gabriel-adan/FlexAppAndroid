package com.gas.flexapp

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.gas.flexapp.network.AuthService
import com.gas.flexapp.ui.LogInActivity

class FlexAppAccountAuthenticator(
    private val context: Context,
    private val authService: AuthService
) : AbstractAccountAuthenticator(context) {
    override fun editProperties(
        response: AccountAuthenticatorResponse?,
        accountType: String?
    ): Bundle {
        TODO("Not yet implemented")
    }

    override fun addAccount(
        response: AccountAuthenticatorResponse?,
        accountType: String?,
        authTokenType: String?,
        requiredFeatures: Array<out String>?,
        options: Bundle?
    ): Bundle {
        TODO("Not yet implemented")
    }

    override fun confirmCredentials(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        options: Bundle?
    ): Bundle {
        TODO("Not yet implemented")
    }

    override fun getAuthToken(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        authTokenType: String?,
        options: Bundle?
    ): Bundle {
        val accountManager = AccountManager.get(context)
        var accessToken = accountManager.peekAuthToken(account, authTokenType)
        if (accessToken.isNullOrEmpty()) {
            val intent = Intent(context, LogInActivity::class.java).apply {
                putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
            }
            val versionKey = context.getString(R.string.version_id)
            val versionId = accountManager.getUserData(account, versionKey)
            return Bundle().apply {
                putParcelable(AccountManager.KEY_INTENT, intent)
                putInt(versionKey, versionId.toInt())
            }
        } else {
            val userName = accountManager.getUserData(account, AccountManager.KEY_ACCOUNT_NAME)
            val password = accountManager.getUserData(account, AccountManager.KEY_PASSWORD)
            println(userName)
            println(password)
        }

        return options!!
    }

    override fun getAuthTokenLabel(authTokenType: String?): String {
        TODO("Not yet implemented")
    }

    override fun updateCredentials(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        authTokenType: String?,
        options: Bundle?
    ): Bundle {
        TODO("Not yet implemented")
    }

    override fun hasFeatures(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        features: Array<out String>?
    ): Bundle {
        TODO("Not yet implemented")
    }
}