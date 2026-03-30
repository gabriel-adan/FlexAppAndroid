package com.gas.flexapp.viewmodels

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gas.flexapp.local.CipherService
import com.gas.flexapp.network.AuthService
import com.gas.flexapp.network.NetworkModule
import com.gas.model.ServiceModel
import com.gas.model.services.Authentication
import com.gas.orm.context.DbContext
import com.gas.orm.data.And
import com.gas.orm.data.Criteria
import com.gas.orm.data.DbSet
import com.gas.orm.data.Eq
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val dbContext: DbContext,
    private val authService: AuthService,
    private val cipherService: CipherService
) : ViewModel() {
    companion object {
        private val gson = GsonBuilder().create()
    }

    fun <T> getServiceModel(type: String?, versionId: Int, serviceType: Class<T>): T? {
        val serviceModelDbSet: DbSet<ServiceModel, String> = dbContext.registerEntityMap(ServiceModel::class.java)
        val serviceModel = serviceModelDbSet.where(arrayOf<Criteria>(
            And(
                Eq(ServiceModel::class.java, "type", type),
                Eq(ServiceModel::class.java, "versionId", versionId)
            )
        )).firstOrNull()
        return gson.fromJson(serviceModel?.content, serviceType)
    }

    private val _onAccessToken: MutableLiveData<Bundle> by lazy {
        MutableLiveData()
    }

    val onAccessToken: LiveData<Bundle> get() = _onAccessToken

    private val _onFail: MutableLiveData<JsonObject> by lazy {
        MutableLiveData()
    }

    val onFail: LiveData<JsonObject> get() = _onFail

    fun logIn(userName: String?, password: String?, authentication: Authentication) = viewModelScope.launch {
        try {
            val data = JsonObject()
            data.addProperty(authentication.userFieldName, userName)
            data.addProperty(authentication.passwordFieldName, password)
            val response = withContext(Dispatchers.IO) { authService.logIn(authentication.url, data) }
            if (response.isSuccessful) {
                val jsonObject = response.body()!!
                _onAccessToken.value = bundleOf().also {
                    val accessToken = jsonObject.get(authentication.tokenFieldName).asString
                    it.putString(authentication.tokenFieldName, accessToken)
                    it.putString("authAccount", userName)
                    it.putString("password", password)
                    authentication.claims.forEach { claim ->
                        if (jsonObject.has(claim)) {
                            it.putString(claim, jsonObject.get(claim).asString)
                        }
                    }
                }
            } else {
                val result = NetworkModule.handleErrorResponse(response.errorBody()!!, JsonObject::class.java)
                _onFail.value = result
            }
        } catch (ex: Exception) {
            println(ex)
        }
    }

    fun encrypt(data: String): String {
        return cipherService.encrypt(data)
    }
}