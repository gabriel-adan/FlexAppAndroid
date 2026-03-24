package com.gas.androidtemplate.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gas.androidtemplate.network.UserService
import com.gas.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val userService: UserService
) : ViewModel() {

    private val _items: MutableLiveData<List<User>> by lazy {
        MutableLiveData()
    }

    val items: LiveData<List<User>> get() = _items

    fun getItems() = viewModelScope.launch {
        val response = withContext(Dispatchers.IO) { userService.getUserList() }
        if (response.isSuccessful) {
            _items.value = response.body()
        } else {
            _items.value = emptyList()
        }
    }
}