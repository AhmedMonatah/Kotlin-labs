package com.example.noteai.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.noteai.presentation.home.HomeScreen
import com.example.noteai.presentation.saved.SavedScreen
import com.example.noteai.presentation.search.SearchScreen
import com.example.noteai.presentation.topicdetail.TopicDetailScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Screen(val route: String) {
    object Home   : Screen("home")
    object Search : Screen("search")
    object Saved  : Screen("saved")

    object TopicDetailByTitle : Screen("topic_detail/title/{topicTitle}") {
        fun createRoute(title: String): String {
            val encoded = URLEncoder.encode(title, StandardCharsets.UTF_8.toString())
            return "topic_detail/title/$encoded"
        }
    }

    object TopicDetailById : Screen("topic_detail/id/{topicId}") {
        fun createRoute(id: Int) = "topic_detail/id/$id"
    }
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                onNavigateToTopicByTitle = { title ->
                    navController.navigate(Screen.TopicDetailByTitle.createRoute(title))
                },
                onTabSelected = { tab -> handleTabNavigation(navController, tab) }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                onNavigateToTopicByTitle = { title ->
                    navController.navigate(Screen.TopicDetailByTitle.createRoute(title))
                },
                onTabSelected = { tab -> handleTabNavigation(navController, tab) }
            )
        }

        composable(Screen.Saved.route) {
            SavedScreen(
                onTopicClick = { title ->
                    navController.navigate(Screen.TopicDetailByTitle.createRoute(title))
                },
                onTabSelected = { tab -> handleTabNavigation(navController, tab) }
            )
        }

        composable(
            route = Screen.TopicDetailByTitle.route,
            arguments = listOf(navArgument("topicTitle") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedTitle = backStackEntry.arguments?.getString("topicTitle") ?: ""
            val title = URLDecoder.decode(encodedTitle, StandardCharsets.UTF_8.toString())
            TopicDetailScreen(
                topicTitle = title,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.TopicDetailById.route,
            arguments = listOf(navArgument("topicId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("topicId")
            TopicDetailScreen(
                topicTitle = null,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

private fun handleTabNavigation(navController: NavHostController, tab: String) {
    val route = when (tab) {
        "Home"   -> Screen.Home.route
        "Search" -> Screen.Search.route
        "Saved"  -> Screen.Saved.route
        else     -> return
    }
    navController.navigate(route) {
        popUpTo(Screen.Home.route) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
