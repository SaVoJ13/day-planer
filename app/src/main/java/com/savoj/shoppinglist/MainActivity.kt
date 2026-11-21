package com.savoj.shoppinglist

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.savoj.shoppinglist.data.local.AppDatabase
import com.savoj.shoppinglist.data.remote.GroupShoppingRepository
import com.savoj.shoppinglist.data.repository.SettingsRepositoryImpl
import com.savoj.shoppinglist.data.repository.ShoppingRepositoryImpl
import com.savoj.shoppinglist.ui.LocaleHelper
import com.savoj.shoppinglist.ui.details.ListDetailsScreen
import com.savoj.shoppinglist.ui.details.ListDetailsViewModel
import com.savoj.shoppinglist.ui.lists.ListsScreen
import com.savoj.shoppinglist.ui.lists.ListsViewModel
import com.savoj.shoppinglist.ui.navigation.Screen
import com.savoj.shoppinglist.ui.settings.SettingsScreen
import com.savoj.shoppinglist.ui.settings.SettingsViewModel
import com.savoj.shoppinglist.ui.theme.ShoppingListTheme

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val lang = prefs.getString(SettingsRepositoryImpl.KEY_LANGUAGE, SettingsRepositoryImpl.LANG_SYSTEM)
            ?: SettingsRepositoryImpl.LANG_SYSTEM
        val context = LocaleHelper.updateLocale(newBase, lang)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getInstance(this)
        val shoppingRepository = ShoppingRepositoryImpl(database)
        val settingsRepository = SettingsRepositoryImpl(this)
        val groupRepository = runCatching {
            GroupShoppingRepository(
                listDao = database.shoppingListDao(),
                itemDao = database.shoppingItemDao()
            )
        }.getOrNull()

        setContent {
            val settingsViewModel = remember { SettingsViewModel(settingsRepository) }
            val themeSetting by settingsViewModel.theme.collectAsState()
            val languageSetting by settingsViewModel.language.collectAsState()

            val currentContext = LocalContext.current
            val localizedContext = remember(languageSetting, currentContext) {
                LocaleHelper.updateLocale(currentContext, languageSetting)
            }

            CompositionLocalProvider(
                LocalConfiguration provides localizedContext.resources.configuration,
                LocalContext provides localizedContext,
                LocalActivityResultRegistryOwner provides this
            ) {
                key(languageSetting) {
                    ShoppingListTheme(themeSetting = themeSetting) {
                        val navController = rememberNavController()

                        NavHost(
                            navController = navController,
                            startDestination = Screen.Lists.route
                        ) {
                            composable(Screen.Lists.route) {
                                val listsViewModel = remember { ListsViewModel(shoppingRepository, groupRepository) }
                                ListsScreen(
                                    viewModel = listsViewModel,
                                    onNavigateToListDetails = { listId ->
                                        navController.navigate(Screen.ListDetails.createRoute(listId))
                                    },
                                    onNavigateToSettings = {
                                        navController.navigate(Screen.Settings.route)
                                    }
                                )
                            }

                            composable(
                                route = Screen.ListDetails.route,
                                arguments = listOf(navArgument("listId") { type = NavType.LongType })
                            ) { backStackEntry ->
                                val listId = backStackEntry.arguments?.getLong("listId") ?: 0L
                                val detailsViewModel = remember(listId) {
                                    ListDetailsViewModel(listId, shoppingRepository, groupRepository)
                                }
                                ListDetailsScreen(
                                    viewModel = detailsViewModel,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }

                            composable(Screen.Settings.route) {
                                SettingsScreen(
                                    viewModel = settingsViewModel,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
