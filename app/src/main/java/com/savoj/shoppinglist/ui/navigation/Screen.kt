package com.savoj.shoppinglist.ui.navigation

sealed class Screen(val route: String) {
    data object Lists : Screen("lists")
    data object ListDetails : Screen("details/{listId}") {
        fun createRoute(listId: Long) = "details/$listId"
    }
    data object Settings : Screen("settings")
}
