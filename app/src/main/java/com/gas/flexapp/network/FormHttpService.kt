package com.gas.flexapp.network

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface FormHttpService {
    @POST
    suspend fun post(@Url url: String, @Body form: JsonObject) : Response<JsonObject?>

    @GET
    suspend fun get(@Url url: String) : Response<JsonObject>

    @GET
    suspend fun getList(@Url url: String) : Response<List<JsonObject>>
}