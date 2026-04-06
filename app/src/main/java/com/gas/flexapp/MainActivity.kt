package com.gas.flexapp

import android.accounts.AccountManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavOptions
import androidx.navigation.createGraph
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.ui.setupWithNavController
import com.gas.components.ComponentBuilder
import com.gas.flexapp.ui.lists.ExpandableFragment
import com.gas.flexapp.ui.forms.FormFragment
import com.gas.flexapp.ui.lists.ListFragment
import com.gas.flexapp.ui.maps.MapsFragment
import com.gas.flexapp.ui.pages.PageFragment
import com.gas.flexapp.viewmodels.ComponentViewModel
import com.gas.flexapp.viewmodels.FormViewModel
import com.gas.flexapp.viewmodels.ListViewModel
import com.gas.model.ComponentTypes
import com.gas.model.DataTypes
import com.gas.model.menus.MenuModel
import com.gas.model.menus.MenuTypes
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlin.collections.forEach

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    NavigationBarView.OnItemSelectedListener {

    private val componentViewModel: ComponentViewModel by viewModels()
    private var appBarConfiguration: AppBarConfiguration? = null
    private var drawerLayout: DrawerLayout? = null
    private var navMenuModel: MenuModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val versionKey = getString(R.string.version_id)
        val versionId = intent.getIntExtra(versionKey, 0)
        componentViewModel.setVersionSelected(versionId)

        val navMenu = componentViewModel.getContentViewModel("1", versionId)
        if (navMenu != null) {
            navMenuModel = ComponentBuilder.buildMenu(navMenu.content)
            when (navMenuModel!!.type) {
                MenuTypes.SIDE_MENU -> {
                    buildSideMenu()
                }
                MenuTypes.BOTTOM_MENU -> {
                    buildBottomMenu()
                }
            }
        } else {
            buildDefaultNavigation()
        }
    }

    private fun buildDefaultNavigation() {
        val initComponent = componentViewModel.getFirstContentViewModel()
        if (initComponent == null) {
            finish()
        }

        val context = this
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)

        val navGraph = navController.createGraph(
            startDestination = "1",
            route = "0"
        ) {

        }

        val destinationId = 1

        when (ComponentTypes.valueOf(initComponent!!.type.uppercase())) {
            ComponentTypes.VIEW_PAGE -> {
                PageFragment::class.java
            }
            ComponentTypes.FORM -> {
                FormFragment::class.java
            }
            ComponentTypes.LIST -> {
                val destination = navController.navigatorProvider.getNavigator(FragmentNavigator::class.java)
                    .createDestination().apply {
                        id = destinationId
                        label = "List"
                        setClassName(ComponentName(context, ListFragment::class.java).className)
                    }
                navGraph.addDestination(destination)
                navGraph.setStartDestination(destination.id)

            } else -> TODO("The component type ${initComponent.type} not yet implemented.")
        }

        navController.graph = navGraph

        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration!!)

        val navOptions = NavOptions.Builder()
            .setPopUpTo(destinationId, inclusive = true, saveState = true)
            .build()
        val args = Bundle()
        args.putString("viewKey", initComponent.viewKey)

        navController.navigate(destinationId, args, navOptions)
    }

    private fun buildSideMenu() {
        val context = this
        setContentView(R.layout.activity_side_menu)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        val navGraph = navController.createGraph(
            startDestination = "1",
            route = "0"
        ) {

        }

        navMenuModel!!.menuItems.forEach {
            val viewContentModel = componentViewModel.getContentViewModel(it.navigation.viewKey, componentViewModel.onVersionSelected.value!!)!!
            val type = when (ComponentTypes.valueOf(viewContentModel.type.uppercase())) {
                ComponentTypes.VIEW_PAGE -> {
                    PageFragment::class.java
                }
                ComponentTypes.FORM -> {
                    FormFragment::class.java
                }
                ComponentTypes.LIST -> {
                    ListFragment::class.java
                }
                ComponentTypes.EXPANDABLE_LIST -> {
                    ExpandableFragment::class.java
                }
                ComponentTypes.MAPS -> {
                    MapsFragment::class.java
                } else -> TODO("The component type ${viewContentModel.type} not yet implemented. Menu item [${it.label}]")
            }
            val destination = navController.navigatorProvider.getNavigator(FragmentNavigator::class.java)
                .createDestination().apply {
                    id = it.navigation.destinationId
                    label = if (it.navigation.title.isNullOrEmpty()) it.label else it.navigation.title
                    setClassName(ComponentName(context, type).className)
                }
            navGraph.addDestination(destination)
        }

        val firstMenuItem = navMenuModel!!.menuItems.first()
        val firstDestinationId = firstMenuItem.navigation.destinationId

        navGraph.setStartDestination(firstDestinationId)

        navController.graph = navGraph

        drawerLayout = findViewById(R.id.drawer_layout)

        val menuItems = mutableListOf<Int>()

        navMenuModel!!.menuItems.forEach {
            menuItems.add(it.navigation.destinationId)
        }

        val hasAuth = intent.getBooleanExtra("hasAuth", false)
        if (hasAuth) {
            menuItems.add(0)
        }

        appBarConfiguration = AppBarConfiguration(menuItems.toSet(), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration!!)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setupWithNavController(navController)
        navigationView.setNavigationItemSelectedListener(this)

        navMenuModel!!.menuItems.forEach {
            val menuItem = navigationView.menu.add(R.id.menu_group, it.navigation.destinationId, 0, it.label)
            menuItem.isCheckable = true
        }

        if (hasAuth) {
            val menuItem = navigationView.menu.add(R.id.menu_group, 0, 0, "Cerrar sesion")
            menuItem.isCheckable = true
            menuItem.setOnMenuItemClickListener {
                closeSession()
                true
            }
        }

        val navOptions = NavOptions.Builder()
            .setPopUpTo(firstDestinationId, inclusive = true, saveState = true)
            .build()
        val args = Bundle()
        args.putString("viewKey", firstMenuItem.navigation.viewKey)

        navController.navigate(firstDestinationId, args, navOptions)
    }

    private fun buildBottomMenu() {
        val context = this
        setTheme(R.style.Theme_BottomMenu)
        setContentView(R.layout.activity_bottom_menu)
        val navView: BottomNavigationView = findViewById(R.id.bottom_nav_view)

        val navController = findNavController(R.id.bottom_nav_host_fragment_activity_main)
        val navGraph = navController.createGraph(
            startDestination = "1",
            route = "0"
        ) {

        }

        val setOfs = mutableListOf<Int>()

        navMenuModel!!.menuItems.forEach {
            val destinationId = it.navigation.destinationId
            setOfs.add(destinationId)
            val viewContentModel = componentViewModel.getContentViewModel(it.navigation.viewKey, componentViewModel.onVersionSelected.value!!)!!
            val type = when (ComponentTypes.valueOf(viewContentModel.type.uppercase())) {
                ComponentTypes.VIEW_PAGE -> {
                    PageFragment::class.java
                }
                ComponentTypes.FORM -> {
                    viewModels<FormViewModel>()
                    FormFragment::class.java
                }
                ComponentTypes.LIST -> {
                    viewModels<ListViewModel>()
                    ListFragment::class.java
                }
                ComponentTypes.EXPANDABLE_LIST -> {
                    ExpandableFragment::class.java
                } else -> TODO("The component type ${viewContentModel.type} not yet implemented. Menu item [${it.label}]")
            }

            val destination = navController.navigatorProvider.getNavigator(FragmentNavigator::class.java)
                .createDestination().apply {
                    id = destinationId
                    label = if (it.navigation.title.isNullOrEmpty()) it.label else it.navigation.title
                    setClassName(ComponentName(context, type).className)
                }

            navGraph.addDestination(destination)
            navView.menu.add(1, destinationId, 0, it.label)
        }

        val firstMenuItem = navMenuModel!!.menuItems.first()
        val firstDestinationId = firstMenuItem.navigation.destinationId

        navGraph.setStartDestination(firstDestinationId)

        navController.graph = navGraph

        appBarConfiguration = AppBarConfiguration(setOfs.toSet())
        setupActionBarWithNavController(navController, appBarConfiguration!!)
        navView.setupWithNavController(navController)
        navView.setOnItemSelectedListener(this)

        val navOptions = NavOptions.Builder()
            .setPopUpTo(firstDestinationId, inclusive = true, saveState = true)
            .build()
        val args = Bundle()
        args.putString("viewKey", firstMenuItem.navigation.viewKey)

        navController.navigate(firstDestinationId, args, navOptions)
    }

    override fun onSupportNavigateUp(): Boolean {
        return when (navMenuModel?.type) {
            MenuTypes.SIDE_MENU -> {
                val navController = findNavController(R.id.nav_host_fragment_content_main)
                navController.navigateUp(appBarConfiguration!!) || super.onSupportNavigateUp()
            }
            MenuTypes.BOTTOM_MENU -> {
                val navController = findNavController(R.id.bottom_nav_host_fragment_activity_main)
                navController.navigateUp(appBarConfiguration!!) || super.onSupportNavigateUp()
            } else -> {
                val navController = findNavController(R.id.nav_host_fragment_content_main)
                navController.navigateUp(appBarConfiguration!!) || super.onSupportNavigateUp()
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val navController = when (navMenuModel!!.type) {
            MenuTypes.SIDE_MENU -> {
                findNavController(R.id.nav_host_fragment_content_main)
            }
            MenuTypes.BOTTOM_MENU -> {
                findNavController(R.id.bottom_nav_host_fragment_activity_main)
            }
        }

        val menuItemModel = navMenuModel!!.menuItems.find { menuItemModel -> menuItemModel.navigation.destinationId == item.itemId }
        menuItemModel?.also {
            val destinationId = it.navigation.destinationId
            val navOptions = NavOptions.Builder()
                .setPopUpTo(destinationId, inclusive = true, saveState = true)
                .build()
            val args = Bundle()
            args.putString("viewKey", it.navigation.viewKey)
            it.navigation.paramValues.forEach { paramValueModel ->
                when (paramValueModel.type) {
                    DataTypes.INT -> {

                    }
                    DataTypes.STRING -> {

                    }
                    DataTypes.DECIMAL -> {

                    }
                    DataTypes.FLOAT -> {

                    }
                    DataTypes.DATE -> {

                    }
                    DataTypes.TIME -> {

                    }
                    DataTypes.DATETIME -> {

                    }
                    DataTypes.BOOLEAN -> {

                    }
                }
            }
            navController.navigate(destinationId, args, navOptions)
        }

        drawerLayout?.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (navMenuModel != null) {
            when (navMenuModel!!.type) {
                MenuTypes.BOTTOM_MENU -> {
                    val hasAuth = intent.getBooleanExtra("hasAuth", false)
                    if (hasAuth) {
                        menu?.also {
                            it.add(1, 100, 0, "Cerrar sesion")
                                .setOnMenuItemClickListener {
                                    closeSession()
                                    true
                                }
                        }
                    }
                } else -> {}
            }
        }

        return true
    }

    private fun closeSession() {
        val accountManager = AccountManager.get(this)
        val accounts = accountManager.getAccountsByType(getString(R.string.account_type))
        accountManager.removeAccountExplicitly(accounts.first())
        startActivity(Intent(applicationContext, SplashScreenActivity::class.java))
        finish()
    }
}