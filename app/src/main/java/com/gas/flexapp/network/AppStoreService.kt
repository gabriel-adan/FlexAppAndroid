package com.gas.flexapp.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET

interface AppStoreService {
    @GET("AppStore/DownloadDatabaseFile")
    suspend fun downloadAppStore() : Response<ResponseBody>
}