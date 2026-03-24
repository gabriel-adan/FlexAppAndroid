package com.gas.androidtemplate

import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.gas.androidtemplate.network.NetworkModule
import com.gas.androidtemplate.network.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.StringStartsWith.startsWith
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(RobolectricTestRunner::class)
@UninstallModules(NetworkModule::class)
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
class MainActivityTest {

    private val mockWebServer: MockWebServer = MockWebServer()

    @Module
    @InstallIn(ViewModelComponent::class)
    class FakeNetworkModule {
        @Provides
        fun bindUserService(): UserService {
            val client = OkHttpClient.Builder().build()
            return Retrofit.Builder()
                .baseUrl("http://localhost:4873/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UserService::class.java)
        }
    }

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        mockWebServer.start(4873)
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `should init main activity successfully`() {
        // Given
        val response = MockResponse().setResponseCode(200).setBody(
            """
                [
                    {
                        "id": 1,
                        "name": "Leanne Graham"
                    },
                    {
                        "id": 2,
                        "name": "Ervin Howell"
                    },
                    {
                        "id": 3,
                        "name": "Clementine Bauch"
                    }
                ]
            """.trimIndent()
        )

        val dispatcher: Dispatcher = object: Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                when (request.path) {
                    "/users" -> {
                        return  response
                    }
                }
                return MockResponse().setResponseCode(404)
            }
        }

        mockWebServer.dispatcher = dispatcher

        // When
        Robolectric.buildActivity(MainActivity::class.java).use { controller ->
            controller.setup()
            val activity = controller.get()
            activity.findViewById<Button>(R.id.next_btn).performClick()

            // Then
            assertThat("1 - Leanne Graham", allOf(startsWith("1 -"), containsString("Leanne Graham")))

            onView(withId(R.id.recycler_view)).check { view, noViewFoundException ->
                if (noViewFoundException != null) {
                    throw noViewFoundException
                }
                val recyclerView = view as RecyclerView
                assertEquals(3, recyclerView.adapter?.itemCount)
            }

            dispatcher.shutdown()
        }
    }

    @Test
    fun `should show snackBar message`() {
        // Given
        val controller = Robolectric.buildActivity(MainActivity::class.java)

        // When
        controller.setup()

        // Then
        onView(withId(R.id.fab)).perform(click())
        onView(withText("Replace with your own action")).check(matches(isDisplayed()))
    }
}