package com.gas.flexapp.network

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface RequestService {
    @GET
    suspend fun getList(@Url url: String) : Response<List<JsonObject>>
}