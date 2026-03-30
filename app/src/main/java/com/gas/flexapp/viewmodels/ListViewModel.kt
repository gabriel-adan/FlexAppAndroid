package com.gas.flexapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gas.flexapp.network.RequestService
import com.gas.model.ViewContentModel
import com.gas.orm.context.DbContext
import com.gas.orm.data.DbSet
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val dbContext: DbContext,
    private val requestService: RequestService
) : ViewModel() {

    fun getContentViewModel(viewKey: String?): ViewContentModel {
        val viewContentModelDbSet: DbSet<ViewContentModel, String> = dbContext.registerEntityMap(
            ViewContentModel::class.java)
        return  viewContentModelDbSet.get(viewKey)
    }

    private val _selectedItem: MutableLiveData<JsonObject> by lazy {
        MutableLiveData()
    }

    val selectedItem: LiveData<JsonObject> get() = _selectedItem

    fun setSelectedItem(jsonObject: JsonObject) {
        _selectedItem.value = jsonObject
    }

    private val _items: MutableLiveData<List<JsonObject>?> by lazy {
        MutableLiveData()
    }

    val items: LiveData<List<JsonObject>?> get() = _items

    fun getItems(uri: String) = viewModelScope.launch {
        val response = withContext(Dispatchers.IO) { requestService.getList(uri) }
        if (response.isSuccessful) {
            _items.value = response.body()
        } else {
            println("Error")
        }
    }
}