package com.gas.androidtemplate.network

import com.gas.model.User
import retrofit2.Response
import retrofit2.http.GET

interface UserService {

    @GET("users")
    suspend fun getUserList() : Response<List<User>>
}