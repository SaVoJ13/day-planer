package com.savoj.shoppinglist

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.savoj.shoppinglist.data.local.AppDatabase
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getInstance(this)
        val shoppingRepository = ShoppingRepositoryImpl(database)
        val settingsRepository = SettingsRepositoryImpl(this)

        setContent {
            val settingsViewModel = remember { SettingsViewModel(settingsRepository) }
            val themeSetting by settingsViewModel.theme.collectAsState()
            val languageSetting by settingsViewModel.language.collectAsState()

            val currentContext = LocalContext.current
            val baseConfig = LocalConfiguration.current
            val localizedConfig = remember(languageSetting, baseConfig) {
                if (languageSetting != SettingsRepositoryImpl.LANG_SYSTEM) {
                    val targetLocale = LocaleHelper.getLocale(languageSetting)
                    Configuration(baseConfig).apply {
                        setLocale(targetLocale)
                    }
                } else {
                    baseConfig
                }
            }

            CompositionLocalProvider(LocalConfiguration provides localizedConfig) {
                ShoppingListTheme(themeSetting = themeSetting) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = Screen.Lists.route
                    ) {
                        composable(Screen.Lists.route) {
                            val listsViewModel = remember { ListsViewModel(shoppingRepository) }
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
                                ListDetailsViewModel(listId, shoppingRepository)
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
