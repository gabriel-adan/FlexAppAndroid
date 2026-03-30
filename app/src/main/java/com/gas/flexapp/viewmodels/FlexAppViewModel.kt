package com.gas.flexapp.viewmodels

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gas.flexapp.local.CipherService
import com.gas.flexapp.network.ErrorResponse
import com.gas.flexapp.network.FlexAuthService
import com.gas.flexapp.network.LogInForm
import com.gas.flexapp.network.NetworkModule
import com.gas.model.Version
import com.gas.orm.context.DbContext
import com.gas.orm.data.DbSet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FlexAppViewModel @Inject constructor(
    private val flexAuthService: FlexAuthService,
    private val cipherService: CipherService,
    private val dbContext: DbContext,
) : ViewModel() {

    fun logIn(userName: String?, password: String?, onSuccess: (Bundle) -> Unit, onFail: (String?) -> Unit) = viewModelScope.launch {
        try {
            val response = withContext(Dispatchers.IO) { flexAuthService.logIn(LogInForm(userName, password)) }
            if (response.isSuccessful) {
                val logInResult = response.body()
                val bundle = bundleOf().also {
                    it.putString("authAccount", userName)
                    it.putString("password", password)
                    it.putString("access_token", logInResult!!.accessToken)
                    it.putString("full_name", logInResult.fullName)
                }
                onSuccess(bundle)
            } else {
                val result = NetworkModule.handleErrorResponse(response.errorBody()!!, ErrorResponse::class.java)
                onFail(result.message)
            }
        } catch (ex: Exception) {
            onFail("Ocurrió un error al iniciar sesion")
        }
    }

    private val _onApplicationList: MutableLiveData<List<Version>> by lazy {
        MutableLiveData()
    }

    val onApplicationList: LiveData<List<Version>> get() = _onApplicationList

    fun getApplicationList() = viewModelScope.launch {
        val appStoreDbSet: DbSet<Version, Int> = dbContext.registerEntityMap(Version::class.java)
        _onApplicationList.value = appStoreDbSet.all
    }

    fun encrypt(data: String): String {
        return cipherService.encrypt(data)
    }

    fun decrypt(encryptedData: String): String {
        return cipherService.decrypt(encryptedData)
    }
}