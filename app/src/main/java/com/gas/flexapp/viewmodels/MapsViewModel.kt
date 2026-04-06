package com.gas.flexapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gas.flexapp.network.RequestService
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
class MapsViewModel @Inject constructor(
    private val dbContext: DbContext,
    private val requestService: RequestService
) : ViewModel() {

    fun getContentViewModel(viewKey: String?): ViewContentModel {
        val viewContentModelDbSet: DbSet<ViewContentModel, String> = dbContext.registerEntityMap(
            ViewContentModel::class.java)
        return  viewContentModelDbSet.get(viewKey)
    }

    private val _onList: MutableLiveData<List<JsonObject>> by lazy {
        MutableLiveData()
    }

    val onList: LiveData<List<JsonObject>> get() = _onList

    fun list(dataSource: DataSource) = viewModelScope.launch {
        try {
            when (dataSource.type) {
                DataSourceTypes.REMOTE -> {
                    val remoteDataSource = dataSource as RemoteDataSource
                    when (remoteDataSource.requestType) {
                        "GET" -> {
                            when (dataSource.responseType) {
                                ResponseTypes.LIST -> {
                                    val response = withContext(Dispatchers.IO) { requestService.getList(remoteDataSource.url) }
                                    if (response.isSuccessful) {
                                        val results = response.body()
                                        _onList.value = results
                                    } else {

                                    }
                                }
                                ResponseTypes.OBJECT -> {

                                }
                            }
                        }
                    }
                }
                else -> {}
            }
        } catch (ex: Exception) {
            println(ex)
        }
    }
}