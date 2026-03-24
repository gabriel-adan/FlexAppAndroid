package com.gas.androidtemplate

import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.gas.androidtemplate.network.NetworkModule
import com.gas.androidtemplate.network.UserService
import com.gas.androidtemplate.ui.ListFragment
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
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(RobolectricTestRunner::class)
@UninstallModules(NetworkModule::class)
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
class ListFragmentTest {

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
    fun `should get user list successfully`() {
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

        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        // When
        launchFragmentInHiltContainer<ListFragment>(Bundle(), R.style.Theme_App) {
            navController.setGraph(R.navigation.nav_graph)
            navController.setCurrentDestination(R.id.ListFragment)
            Navigation.setViewNavController(requireView(), navController)
        }

        // Then
        onView(withId(R.id.recycler_view)).check { view, noViewFoundException ->
            if (noViewFoundException != null) {
                throw noViewFoundException
            }
            val recyclerView = view as RecyclerView
            assertEquals(3, recyclerView.adapter?.itemCount)
        }

        onView(withText("1 - Leanne Graham")).perform(click())
        assertEquals(navController.currentDestination?.id, R.id.DetailFragment)

        dispatcher.shutdown()
    }

    @Test
    fun `should get empty list`() {
        // Given
        val response = MockResponse().setResponseCode(400).setBody("")

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

        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        // When
        launchFragmentInHiltContainer<ListFragment>(Bundle(), R.style.Theme_App) {
            navController.setGraph(R.navigation.nav_graph)
            navController.setCurrentDestination(R.id.ListFragment)
            Navigation.setViewNavController(requireView(), navController)
        }

        // Then
        onView(withId(R.id.recycler_view)).check { view, noViewFoundException ->
            if (noViewFoundException != null) {
                throw noViewFoundException
            }
            val recyclerView = view as RecyclerView
            assertEquals(0, recyclerView.adapter?.itemCount)
        }

        dispatcher.shutdown()
    }
}