package com.example.sutra.screens

import android.net.http.SslCertificate.saveState
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.sutra.model.BottomNavItem
import com.example.sutra.navigation.Routes

@Composable
fun BottomNav(
    navController: NavHostController
){
    val navController1 = rememberNavController()
    Scaffold(
        bottomBar = { MyCustomBottomBar(navController1) }
    ) {innerPadding ->
        NavHost(navController = navController1, startDestination = Routes.Home.route, modifier = Modifier.padding(innerPadding)){
            composable(Routes.Home.route){
                Home(navController)
            }
            composable(Routes.AddSutra.route){
                AddSutra(navController1)
            }
            composable(Routes.Notification.route){
                Notification()
            }
            composable(Routes.Profile.route){
                Profile(navController)
            }
            composable (Routes.Search.route){
                Search(navController)
            }

        }

    }
}

@Composable
fun MyCustomBottomBar(navController: NavController){
    val backStackEntry = navController.currentBackStackEntryAsState()
    val list = listOf(
        BottomNavItem(
            title = "Home",
            route = Routes.Home.route,
            icon = Icons.Rounded.Home
        ),
        BottomNavItem(
            title = "Search",
            route = Routes.Search.route,
            icon = Icons.Rounded.Search
        ),
        BottomNavItem(
            title = "Add Sutra",
            route = Routes.AddSutra.route,
            icon = Icons.Rounded.Add
        ),
        BottomNavItem(
            title = "Notification",
            route = Routes.Notification.route,
            icon = Icons.Rounded.Notifications
        ),
        BottomNavItem(
            title = "Profile",
            route = Routes.Profile.route,
            icon = Icons.Rounded.Person
        ),

        )
    BottomAppBar {
        list.forEach { item ->
            val selected = item.route == backStackEntry.value?.destination?.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route){
                        popUpTo(navController.graph.findStartDestination().id){
                            saveState = true
                        }
                        launchSingleTop=true
                    }
                },
                {
                    Icon(
                        imageVector = if (selected) item.icon else item.icon,
                        contentDescription = item.title
                    )
                }
            )
        }
    }
}