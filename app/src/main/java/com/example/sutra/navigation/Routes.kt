package com.example.sutra.navigation

sealed class Routes(val route: String) {
    object Home: Routes("home")
    object AddSutra: Routes("addSutra")
    object Notification: Routes("notification")
    object Profile: Routes("profile")
    object Search: Routes("search")
    object Splash: Routes("splash")
    object BottomNav: Routes("bottomnav")
    object Login: Routes("login")
    object Register: Routes("register")
}