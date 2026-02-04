package com.example.sutra.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.sutra.R
import com.example.sutra.model.SutraModel
import com.example.sutra.model.UserModel
import com.example.sutra.navigation.Routes
import com.example.sutra.viewmodel.HomeViewModel

@Composable
fun Home(navController: NavHostController) {
    val homeViewModel: HomeViewModel = viewModel()
    val sutraAndUser by homeViewModel.sutraAndUser.observeAsState(initial = emptyList())

    HomeContent(navController = navController, sutraAndUser = sutraAndUser)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    navController: NavHostController,
    sutraAndUser: List<Pair<SutraModel, UserModel>>
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier.size(32.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.person),
                        contentDescription = "User Profile",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "What's new?",
                        color = Color.Gray,
                        fontSize = 16.sp,
                        modifier = Modifier.clickable {
                            navController.navigate(Routes.AddSutra.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    )
                }
                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
            }

            items(sutraAndUser) { pairs ->
                PostItem(
                    sutra = pairs.first,
                    user = pairs.second,
                    navController = navController,
                    userId = pairs.second.uid
                )
                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    val mockData = listOf(
        SutraModel(sutra = "Welcome to Sutra! This is a mock post for preview.", userId = "1") to
                UserModel(username = "preview_user", uid = "1")
    )
    HomeContent(
        navController = rememberNavController(),
        sutraAndUser = mockData
    )
}
