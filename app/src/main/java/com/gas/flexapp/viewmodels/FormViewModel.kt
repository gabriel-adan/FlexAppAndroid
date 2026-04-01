package com.gas.flexapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gas.flexapp.network.FormHttpService
import com.gas.flexapp.network.NetworkModule
import com.gas.model.DataTypes
import com.gas.model.ViewContentModel
import com.gas.model.sources.DataSource
import com.gas.model.sources.DataSourceTypes
import com.gas.model.sources.RemoteDataSource
import com.gas.model.sources.ResponseTypes
import com.gas.orm.context.DbContext
import com.gas.orm.data.DbSet
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FormViewModel @Inject constructor(
    private val dbContext: DbContext,
    private val formHttpService: FormHttpService
) : ViewModel() {

    fun getContentViewModel(viewKey: String?): ViewContentModel {
        val viewContentModelDbSet: DbSet<ViewContentModel, String> = dbContext.registerEntityMap(
            ViewContentModel::class.java)
        return  viewContentModelDbSet.get(viewKey)
    }

    private val jsonData = JsonObject()

    private val _data = MutableLiveData<JsonObject>().apply {
        value = jsonData
    }

    val data: LiveData<JsonObject> get() = _data

    fun setDataValue(propertyName: String, dataType: DataTypes, value: Any?) {
        if (value == null) {
            jsonData.addProperty(propertyName, null as String?)
        } else {
            if (value.toString().isNotEmpty()) {
                when (dataType) {
                    DataTypes.INT -> {
                        jsonData.addProperty(propertyName, value.toString().toInt())
                    }
                    DataTypes.DATE, DataTypes.TIME, DataTypes.DATETIME -> {
                        jsonData.addProperty(propertyName, value.toString())
                    }
                    DataTypes.DECIMAL -> {
                        jsonData.addProperty(propertyName, value.toString().toBigDecimal())
                    }
                    DataTypes.FLOAT -> {
                        jsonData.addProperty(propertyName, value.toString().toFloat())
                    }
                    DataTypes.BOOLEAN -> {
                        jsonData.addProperty(propertyName, value.toString().toBoolean())
                    }
                    DataTypes.STRING -> {
                        jsonData.addProperty(propertyName, value.toString())
                    }
                }
            }
        }
    }

    private val _fromRemoteSource: MutableLiveData<JsonObject> by lazy {
        MutableLiveData()
    }

    val fromRemoteSource: LiveData<JsonObject> get() = _fromRemoteSource

    private val _editRemoteSource: MutableLiveData<JsonObject> by lazy {
        MutableLiveData()
    }

    val editRemoteSource: LiveData<JsonObject> get() = _editRemoteSource

    private val _onSuccess: MutableLiveData<JsonObject?> by lazy {
        MutableLiveData()
    }

    val onSuccess: LiveData<JsonObject?> get() = _onSuccess

    private val _onFail: MutableLiveData<JsonObject?> by lazy {
        MutableLiveData()
    }

    val onFail: LiveData<JsonObject?> get() = _onFail

    fun send(url: String, data: JsonObject) = viewModelScope.launch {
        try {
            val response = withContext(Dispatchers.IO) { formHttpService.post(url, data) }
            if (response.isSuccessful) {
                _onSuccess.value = response.body()
            } else {
                _onFail.value = NetworkModule.handleErrorResponse(response.errorBody()!!, JsonObject::class.java)
            }
        } catch (e: Exception) {
            _onFail.value = JsonObject()
            println(e.message)
        }
    }

    fun requestFromRemoteSource(dataSource: DataSource) = viewModelScope.launch {
        try {
            when (dataSource.type) {
                DataSourceTypes.REMOTE -> {
                    val remoteDataSource = dataSource as RemoteDataSource
                    when (remoteDataSource.requestType) {
                        "GET" -> {
                            when (dataSource.responseType) {
                                ResponseTypes.OBJECT -> {
                                    val response = withContext(Dispatchers.IO) { formHttpService.get(remoteDataSource.url) }
                                    if (response.isSuccessful) {
                                        _fromRemoteSource.value = response.body()
                                    } else {

                                    }
                                }
                                ResponseTypes.LIST -> {
                                    val response = withContext(Dispatchers.IO) { formHttpService.getList(remoteDataSource.url) }
                                    if (response.isSuccessful) {
                                        //_source.value = response.body()
                                    } else {

                                    }
                                }
                            }
                        }
                    }
                } else -> {}
            }
        } catch (e: Exception) {
            println(e.message)
        }
    }

    fun requestEditFromRemoteSource(dataSource: DataSource) = viewModelScope.launch {
        try {
            when (dataSource.type) {
                DataSourceTypes.REMOTE -> {
                    val remoteDataSource = dataSource as RemoteDataSource
                    when (remoteDataSource.requestType) {
                        "GET" -> {
                            when (dataSource.responseType) {
                                ResponseTypes.OBJECT -> {
                                    val response = withContext(Dispatchers.IO) { formHttpService.get(remoteDataSource.url) }
                                    if (response.isSuccessful) {
                                        _editRemoteSource.value = response.body()
                                    } else {

                                    }
                                }
                                ResponseTypes.LIST -> {
                                    val response = withContext(Dispatchers.IO) { formHttpService.getList(remoteDataSource.url) }
                                    if (response.isSuccessful) {
                                        //_source.value = response.body()
                                    } else {

                                    }
                                }
                            }
                        }
                    }
                } else -> {}
            }
        } catch (e: Exception) {
            println(e.message)
        }
    }
}