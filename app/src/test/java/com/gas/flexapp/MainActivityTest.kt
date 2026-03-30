package com.gas.flexapp

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.gas.flexapp.network.AuthService
import com.gas.flexapp.network.FlexAuthService
import com.gas.flexapp.network.FormHttpService
import com.gas.flexapp.network.NetworkModule
import com.gas.flexapp.network.NullOnEmptyConverterFactory
import com.gas.flexapp.network.RequestService
import com.gas.orm.context.SQLiteDatabaseContext
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
import kotlin.use

@RunWith(RobolectricTestRunner::class)
@UninstallModules(NetworkModule::class)
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
class MainActivityTest {

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
        fun provideOkHttpClient(): OkHttpClient {
            return OkHttpClient.Builder()
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
    fun `should init application`() {
        // Given
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dbContext = SQLiteDatabaseContext(context, "flexapp.db", null, 1)
        val db = dbContext.writableDatabase

        val versionTableSql = readTextFile(this, "/sql/version-table.sql")
        db.execSQL(versionTableSql)

        val applicationTableSql = readTextFile(this, "/sql/application-table.sql")
        db.execSQL(applicationTableSql)

        val serviceTableSql = readTextFile(this, "/sql/service-table.sql")
        db.execSQL(serviceTableSql)

        val sqlVersionInsert = "INSERT INTO Version (Id, Application, Name, Description) VALUES " +
                "(1, 'COD', '1.0.1', 'Gestión de servicios de usuarios finales');"
        db.execSQL(sqlVersionInsert)

        val navMenuContent = readTextFile(this, "/data/side-menu.json")
        val sqlNavMenuInsert = "INSERT INTO Application (ViewKey, Type, Content, VersionId) VALUES " +
                "('1', 'nav_menu', '$navMenuContent', 1);"
        db.execSQL(sqlNavMenuInsert)

        val listContent = readTextFile(this, "/data/product-list.json")
        val sqlListInsert = "INSERT INTO Application (ViewKey, Type, Content, VersionId) VALUES " +
                "('1754939266574', 'list', '$listContent', 1);"
        db.execSQL(sqlListInsert)

        val viewPageContent = readTextFile(this, "/data/incident-home-view-page.json")
        val sqlViewPageInsert = "INSERT INTO Application (ViewKey, Type, Content, VersionId) VALUES " +
                "('1755477628620', 'view_page', '$viewPageContent', 1);"
        db.execSQL(sqlViewPageInsert)

        val expandableListContent = readTextFile(this, "/data/incident-expandable-list.json")
        val sqlExpandableListInsert = "INSERT INTO Application (ViewKey, Type, Content, VersionId) VALUES " +
                "('1755042381594', 'expandable_list', '$expandableListContent', 1);"
        db.execSQL(sqlExpandableListInsert)

        val formContent = readTextFile(this, "/data/product-form.json")
        val sqlFormInsert = "INSERT INTO Application (ViewKey, Type, Content, VersionId) VALUES " +
                "('1754940958966', 'form', '$formContent', 1);"
        db.execSQL(sqlFormInsert)

        val authServiceContent = readTextFile(this, "/data/auth-service.json")
        val sqlAuthServiceInsert = "INSERT INTO Service (Type, Content, VersionId) VALUES " +
                "('auth', '$authServiceContent', 1);"
        db.execSQL(sqlAuthServiceInsert)

        dbContext.close()

        val account = Account("jdoe", "flexapp.com")
        shadowAccountManager.addAccount(account)
        accountManager.setPassword(account, "KH9u7mcC2pN4TMdc478R98cFIU0JGbPXyIEiqLbpg94=")
        accountManager.setUserData(account, "versionId", "1")

        val response = MockResponse().setResponseCode(200).setBody(
            """
                [
                    {
                        "code": "7791909410481",
                        "price": 1800,
                        "brand": "Coca Cola",
                        "description": "Gaseosa Coca Cola x 3 Lts",
                        "id": 1
                    },
                    {
                        "code": "7791909410482",
                        "price": 1753.68,
                        "brand": "Coca Cola",
                        "description": "Gaseosa Coca Cola x 2,25 Lts",
                        "id": 2
                    },
                    {
                        "code": "7791909410483",
                        "price": 1250.41,
                        "brand": "Sprite",
                        "description": "Gaseosa Sprite x 2,25 Lts",
                        "id": 3
                    }
                ]
            """.trimIndent()
        )

        val dispatcher: Dispatcher = object: Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                when (request.path) {
                    "/AuthorizedBearer/GetProductList" -> {
                        return  response
                    }
                }
                return MockResponse().setResponseCode(404)
            }
        }

        mockWebServer.dispatcher = dispatcher

        // When
        val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java).apply {
            putExtra("versionId", 1)
        }
        Robolectric.buildActivity(MainActivity::class.java, intent).use { controller ->
            controller.setup()
            controller.get()

            // Then
            onView(withId(2))
                .perform(
                    RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                        hasDescendant(withText("7791909410482"))
                    )
                )
                .check(matches(hasDescendant(withText("7791909410482"))))

            dispatcher.shutdown()
        }
    }

    private inline fun <reified T> readTextFile(
        caller: T,
        filePath: String
    ): String =
        T::class.java.getResource(filePath)?.readText() ?: throw IllegalArgumentException(
            "Could not find file $filePath. Make sure to put it in the correct resources folder for $caller's runtime."
        )
}