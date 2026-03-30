package com.gas.flexapp.network

import android.accounts.AccountManager
import android.content.Context
import com.gas.flexapp.BuildConfig
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val AUTHORIZATION_HEADER = "Authorization"
    private const val AUTHORIZATION_SCHEME = "Bearer"

    private val flexAppBuilder = Retrofit.Builder()
        .baseUrl(BuildConfig.FLEXAPP_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())

    private var flexAppRetrofit = flexAppBuilder.build()

    private val flexAppHttpClient = OkHttpClient.Builder()
        .callTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)

    @Provides
    fun provideFlexAuthService(): FlexAuthService {
        val authBuilder = Retrofit.Builder()
            .baseUrl(BuildConfig.FLEXAPP_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .apply {
                val httpClient = OkHttpClient.Builder().apply {
                    addNetworkInterceptor { it ->
                        val request = it.request()
                        val response = it.proceed(request)
                        if (response.code == 200) {
                            val gson = Gson()
                            val result = gson.fromJson(response.peekBody(Long.MAX_VALUE).string(), LogInResult::class.java)
                            flexAppHttpClient.addInterceptor { chain ->
                                val original = chain.request()
                                val builder = original.newBuilder().header(AUTHORIZATION_HEADER, "$AUTHORIZATION_SCHEME ${result.accessToken}")
                                chain.proceed(builder.build())
                            }
                            flexAppBuilder.client(flexAppHttpClient.build())
                            flexAppRetrofit = flexAppBuilder.build()
                        }
                        response
                    }
                }
                client(httpClient.build())
            }
        val retrofit: Retrofit = authBuilder.build()
        return retrofit.create(FlexAuthService::class.java)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AccountAuthInterceptor,
        tokenAuthenticator: AccountTokenAuthenticator
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.tuapp.com")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val builder = Retrofit.Builder()
        .baseUrl("https://jsonplaceholder.typicode.com")
        .addConverterFactory(NullOnEmptyConverterFactory())
        .addConverterFactory(GsonConverterFactory.create())

    @Provides
    @Singleton
    fun provideAccountManager(@ApplicationContext context: Context): AccountManager {
        return AccountManager.get(context)
    }

    private var retrofit = builder.build()

    @Provides
    @Singleton
    fun bindAuthService(): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    @Provides
    @Singleton
    fun bindRequestService(retrofit: Retrofit): RequestService {
        return retrofit.create(RequestService::class.java)
    }

    @Provides
    fun bindFormHttpService(retrofit: Retrofit): FormHttpService {
        return retrofit.create(FormHttpService::class.java)
    }

    fun <T> handleErrorResponse(body: ResponseBody, type: Class<T>) : T {
        return flexAppRetrofit.responseBodyConverter<T>(type, arrayOf<Annotation>()).convert(body)!!
    }
}