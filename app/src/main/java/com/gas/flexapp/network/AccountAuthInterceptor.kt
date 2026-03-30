package com.gas.flexapp.network

import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import com.gas.flexapp.R
import com.gas.flexapp.local.CipherService
import com.gas.flexapp.ui.LogInActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountAuthInterceptor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cipherService: CipherService,
    private val accountManager: AccountManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request().newBuilder()
        val accountType = context.getString(R.string.account_type)
        val accounts = accountManager.getAccountsByType(accountType)
        val authTokenType = context.getString(R.string.version_token_type)
        val account = accounts[0]
        val future = accountManager.getAuthToken(
            account, authTokenType, null, false, null, null
        )

        val encryptedAccessToken = future.result.getString(AccountManager.KEY_AUTHTOKEN)
        if (encryptedAccessToken.isNullOrEmpty()) {
            val intent = Intent(context, LogInActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            context.startActivity(intent)
            val request = chain.request()
            val response = chain.proceed(request)
            return response
        }

        val accessToken = cipherService.decrypt(encryptedAccessToken)
        request.addHeader("Authorization", "Bearer $accessToken")
        return chain.proceed(request.build())
    }
}