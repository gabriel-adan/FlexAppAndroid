package com.gas.flexapp.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface FlexAuthService {
    @POST("Authenticator/LogIn")
    suspend fun logIn(@Body form: LogInForm) : Response<LogInResult>
}