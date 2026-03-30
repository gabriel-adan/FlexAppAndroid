package com.gas.flexapp

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.gas.flexapp.network.AccountAuthInterceptor
import com.gas.flexapp.network.AccountTokenAuthenticator
import com.gas.flexapp.network.AuthService
import com.gas.flexapp.network.FlexAuthService
import com.gas.flexapp.network.FormHttpService
import com.gas.flexapp.network.NetworkModule
import com.gas.flexapp.network.NullOnEmptyConverterFactory
import com.gas.flexapp.network.RequestService
import com.gas.flexapp.ui.ApplicationListActivity
import com.gas.flexapp.ui.FlexAppLoginActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowAccountManager
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@RunWith(RobolectricTestRunner::class)
@UninstallModules(NetworkModule::class)
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
class SplashScreenActivityTest {

    private val mockWebServer: MockWebServer = MockWebServer()

    @Module
    @InstallIn(SingletonComponent::class)
    class FakeNetworkModule {
        @Provides
        fun provideFlexAuthService(): FlexAuthService {
            val client = OkHttpClient.Builder().build()
            return Retrofit.Builder()
                .baseUrl("http://localhost:4873/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(FlexAuthService::class.java)
        }

        @Provides
        fun bindAuthService(): AuthService {
            val builder = Retrofit.Builder()
                .baseUrl("http://localhost:4873/")
                .addConverterFactory(NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
            return builder.build().create(AuthService::class.java)
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
                .baseUrl("https://api.com")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        @Provides
        @Singleton
        fun provideAccountManager(@ApplicationContext context: Context): AccountManager {
            return AccountManager.get(context)
        }

        @Provides
        fun bindRequestService(retrofit: Retrofit): RequestService {
            return retrofit.create(RequestService::class.java)
        }

        @Provides
        fun bindFormHttpService(retrofit: Retrofit): FormHttpService {
            return retrofit.create(FormHttpService::class.java)
        }
    }

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var accountManager: AccountManager
    private lateinit var shadowAccountManager: ShadowAccountManager

    @Before
    fun init() {
        mockWebServer.start(4873)
        hiltRule.inject()
        accountManager = AccountManager.get(ApplicationProvider.getApplicationContext())
        shadowAccountManager = Shadows.shadowOf(accountManager)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `should init and redirect to to FlexApp login screen`() {
        // Given

        // When
        Robolectric.buildActivity(SplashScreenActivity::class.java).use { controller ->
            controller.setup()
            val activity = controller.get()
            val shadowActivity = Shadows.shadowOf(activity)
            val startedIntent = shadowActivity.nextStartedActivity

            // Then
            assertNotNull(startedIntent)
            val intentComponent = startedIntent.component
            assertEquals(FlexAppLoginActivity::class.java.name, intentComponent?.className)
        }
    }

    @Test
    fun `should init with one FlexApp account and redirect to application list`() {
        // Given
        val account = Account("jdoe", "flexapp.com")
        shadowAccountManager.addAccount(account)
        accountManager.setPassword(account, "KH9u7mcC2pN4TMdc478R98cFIU0JGbPXyIEiqLbpg94=")

        val response = MockResponse().setResponseCode(200).setBody(
            """
                {
                    "accessToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9uYW1lIjoiZ3NhbnRpbGxhbiIsIkZ1bGxOYW1lIjoiR2FicmllbCBTYW50aWxsYW4iLCJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9zeXN0ZW0iOiJGbGV4QXBwQmFja2VuZFRlc3QiLCJraWQiOiJmOWQwYWRkNy04MTY5LTQ4ZDgtODZkYy0xYmYxYjQ1ODIxZTQiLCJodHRwOi8vc2NoZW1hcy5taWNyb3NvZnQuY29tL3dzLzIwMDgvMDYvaWRlbnRpdHkvY2xhaW1zL3JvbGUiOiJBZG1pbiIsIm5iZiI6MTc3NDc5ODMyMCwiZXhwIjoxNzc0Nzk4MzgwLCJpc3MiOiJodHRwczovL2dhc2FwaXNlcnZpY2VzZGV2Lm9ubGluZS8iLCJhdWQiOiJodHRwczovL2dhc2FwaXNlcnZpY2VzZGV2Lm9ubGluZS8ifQ.R-5tXMNjGU3CLKaGxbpo1S5axWMuU2rNy7obmT8fk_JQb2HcxSfZbCYlC6D-9UMogHTZ0KdKTCRuT6JvXmPxgXcbxZVi6wLARoA0189maeVDEGQ1tcVtFt-JdA7H0Pehd7YEzMUuYlTWlQo9Vv4AX4jAEuDJJr3rbs1eZuzAwfX0bDJUftspQAjw6bM8a9Uc1nG7mKQvDdcMvqr2fULWCjpzIN8c8j5jbvOceRZvtCH-MHMIdGHFhVeJavOboYzZQ8dMsP7K1SVFX1HcKomdxM_6q6frXVv_bi2IUsrf3cZOOTqMTGDxR2nuWckGJOLQFLGI82mSHUz2ILAXIj6r1A",
                    "userName": "John Doe"
                }
            """.trimIndent()
        )

        val dispatcher: Dispatcher = object: Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                when (request.path) {
                    "/Authenticator/LogIn" -> {
                        return  response
                    }
                }
                return MockResponse().setResponseCode(404)
            }
        }

        mockWebServer.dispatcher = dispatcher

        // When
        Robolectric.buildActivity(SplashScreenActivity::class.java).use { controller ->
            controller.setup()
            val activity = controller.get()

            val shadowActivity = Shadows.shadowOf(activity)
            val startedIntent = shadowActivity.nextStartedActivity

            // Then
            assertNotNull(startedIntent)
            val intentComponent = startedIntent.component
            assertEquals(ApplicationListActivity::class.java.name, intentComponent?.className)

            dispatcher.shutdown()
        }
    }
}