package com.gas.flexapp.network

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface AuthService {
    @POST
    suspend fun logIn(@Url url: String, @Body form: JsonObject) : Response<JsonObject>
}