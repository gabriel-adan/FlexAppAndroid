package com.gas.flexapp.network

import android.accounts.AccountManager
import android.content.Context
import com.gas.flexapp.R
import com.gas.flexapp.local.CipherService
import com.gas.model.ServiceModel
import com.gas.model.services.Authentication
import com.gas.orm.context.DbContext
import com.gas.orm.data.And
import com.gas.orm.data.Criteria
import com.gas.orm.data.DbSet
import com.gas.orm.data.Eq
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class AccountTokenAuthenticator @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cipherService: CipherService,
    private val accountManager: AccountManager,
    private val authService: AuthService,
    private val dbContext: DbContext
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.priorResponse != null) return null

        synchronized(this) {
            val accountType = context.getString(R.string.account_type)
            val accounts = accountManager.getAccountsByType(accountType)
            if (accounts.isEmpty()) return null

            val currentToken = response.request.header("Authorization")?.replace("Bearer ", "")

            currentToken?.let {
                accountManager.invalidateAuthToken(accountType, it)
            }

            val account = accounts[0]
            val userName = accountManager.getUserData(account, AccountManager.KEY_ACCOUNT_NAME)
            val encryptedPassword = accountManager.getPassword(account)
            val password = cipherService.decrypt(encryptedPassword)
            val versionKey = context.getString(R.string.version_id)
            val versionId = accountManager.getUserData(account, versionKey)

            val newToken = runBlocking {
                val gson = GsonBuilder().create()
                val serviceModelDbSet: DbSet<ServiceModel, String> = dbContext.registerEntityMap(ServiceModel::class.java)
                val serviceModel = serviceModelDbSet.where(arrayOf<Criteria>(
                    And(
                        Eq(ServiceModel::class.java, "type", "auth"),
                        Eq(ServiceModel::class.java, "versionId", versionId.toInt())
                    )
                )).firstOrNull()
                val authentication = gson.fromJson(serviceModel?.content, Authentication::class.java)!!

                val data = JsonObject()
                data.addProperty(authentication.userFieldName, userName)
                data.addProperty(authentication.passwordFieldName, password)

                val response = withContext(Dispatchers.IO) { authService.logIn(authentication.url, data) }
                if (response.isSuccessful) {
                    val jsonObject = response.body()!!
                    jsonObject.get(authentication.tokenFieldName).asString
                } else {
                    null
                }
            }

            if (newToken.isNullOrEmpty()) {
                return null
            } else {
                val authTokenType = context.getString(R.string.version_token_type)
                val encryptedAccessToken = cipherService.encrypt(newToken)
                accountManager.setAuthToken(account, authTokenType, encryptedAccessToken)
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $newToken")
                    .build()
            }
        }
    }
}