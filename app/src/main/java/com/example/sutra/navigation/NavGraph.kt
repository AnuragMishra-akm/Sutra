package com.example.sutra.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.sutra.screens.AddSutra
import com.example.sutra.screens.BottomNav
import com.example.sutra.screens.Home
import com.example.sutra.screens.LoginScreen
import com.example.sutra.screens.Notification
import com.example.sutra.screens.Profile
import com.example.sutra.screens.Register
import com.example.sutra.screens.Search
import com.example.sutra.screens.Splash

@Composable
fun NavGraph(navController: NavHostController){
    NavHost(navController = navController, startDestination = Routes.Splash.route){
        composable(Routes.Home.route){
            Home(navController)
        }
        composable(Routes.AddSutra.route){
            AddSutra(navController)
        }
        composable(Routes.Notification.route){
            Notification()
        }
        // Route for the current user's profile
        composable(Routes.Profile.route){
            Profile(navController, null) // Pass null for current user
        }
        // Route for viewing another user's profile
        composable(
            route = "${Routes.Profile.route}/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            Profile(navController, userId)
        }
        composable (Routes.Search.route){
            Search(navController)
        }
        composable(Routes.Splash.route){
            Splash(navController)
        }
        composable(Routes.BottomNav.route){
            BottomNav(navController)
        }
        composable(Routes.Login.route){
            LoginScreen(navController)
        }
        composable(Routes.Register.route){
            Register(navController)
        }
    }
}
