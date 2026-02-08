package com.example.sutra.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.example.sutra.R
import com.example.sutra.model.SutraModel
import com.example.sutra.model.UserModel
import com.example.sutra.navigation.Routes
import com.example.sutra.viewmodel.UserProfileViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun Profile(navController: NavHostController, userId: String? = null) {
    val profileViewModel: UserProfileViewModel = viewModel()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val targetUserId = userId ?: currentUser?.uid

    if (targetUserId != null) {
        LaunchedEffect(targetUserId) {
            profileViewModel.fetchUser(targetUserId)
            profileViewModel.fetchSutras(targetUserId)
            profileViewModel.getFollowers(targetUserId)
            profileViewModel.getFollowing(targetUserId)
        }
    }

    val user by profileViewModel.user.observeAsState(null)
    val sutras by profileViewModel.sutras.observeAsState(emptyList())
    val followers by profileViewModel.followerList.observeAsState(emptyList())
    val following by profileViewModel.followingList.observeAsState(emptyList())
    val usersListData by profileViewModel.usersListData.observeAsState(emptyList())

    var showFollowerList by remember { mutableStateOf(false) }
    var listTitle by remember { mutableStateOf("") }

    if (showFollowerList) {
        AlertDialog(
            onDismissRequest = { showFollowerList = false },
            title = { Text(text = listTitle) },
            text = {
                LazyColumn {
                    items(usersListData) { userItem ->
                        UserItem(
                            users = userItem,
                            navController = navController
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFollowerList = false }) {
                    Text("Close")
                }
            }
        )
    }

    ProfileContent(
        user = user,
        sutras = sutras,
        followersCount = followers.size,
        followingCount = following.size,
        isCurrentUser = targetUserId == currentUser?.uid,
        navController = navController,
        onFollowersClick = {
            listTitle = "Followers"
            profileViewModel.fetchUsersFromIds(followers)
            showFollowerList = true
        },
        onFollowingClick = {
            listTitle = "Following"
            profileViewModel.fetchUsersFromIds(following)
            showFollowerList = true
        },
        onFollowClick = {
            if (currentUser != null && targetUserId != null) {
                profileViewModel.followUsers(targetUserId, currentUser.uid)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    user: UserModel?,
    sutras: List<SutraModel>,
    followersCount: Int,
    followingCount: Int,
    isCurrentUser: Boolean,
    navController: NavHostController,
    onFollowersClick: () -> Unit,
    onFollowingClick: () -> Unit,
    onFollowClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Routes.Home.route)}) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    var showMenu by remember { mutableStateOf(false) }

                    if (isCurrentUser) {
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "More options")
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Logout") },
                                    onClick = {
                                        showMenu = false
                                        FirebaseAuth.getInstance().signOut()
                                        navController.navigate(Routes.Login.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                inclusive = true
                                            }
                                            launchSingleTop = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            item {
                user?.let { 
                    ProfileHeader(
                        it, 
                        isCurrentUser, 
                        followersCount, 
                        followingCount,
                        onFollowersClick,
                        onFollowingClick,
                        onFollowClick
                    ) 
                }
            }
            items(sutras) { sutra ->
                user?.let { userModel ->
                    PostItem(
                        sutra = sutra,
                        user = userModel,
                        navController = navController,
                        userId = sutra.userId,
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileHeader(
    user: UserModel, 
    isCurrentUser: Boolean,
    followersCount: Int,
    followingCount: Int,
    onFollowersClick: () -> Unit,
    onFollowingClick: () -> Unit,
    onFollowClick: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = user.name, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                Text(text = user.username, fontSize = 18.sp)
                Text(text = user.bio, fontSize = 16.sp, modifier = Modifier.padding(top = 4.dp))
            }
            AsyncImage(
                model = user.imageUrl,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.person),
                error = painterResource(id = R.drawable.person)
            )
        }
        
        Row(modifier = Modifier.padding(top = 8.dp)) {
            Text(
                text = "$followersCount followers", 
                fontSize = 16.sp, 
                modifier = Modifier.clickable { onFollowersClick() }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "$followingCount following", 
                fontSize = 16.sp, 
                modifier = Modifier.clickable { onFollowingClick() }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (isCurrentUser) {
                OutlinedButton(onClick = { /* Edit Profile */ }, modifier = Modifier.weight(1f)) {
                    Text("Edit profile")
                }
                OutlinedButton(onClick = { /* Share Profile */ }, modifier = Modifier.weight(1f)) {
                    Text("Share profile")
                }
            } else {
                Button(onClick = { onFollowClick() }, modifier = Modifier.fillMaxWidth()) {
                    Text("Follow")
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider()
        Text("Sutras", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(vertical = 8.dp))
        HorizontalDivider()
    }
}

@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
    ProfileContent(
        user = UserModel(
            name = "Tyler Durden",
            username = "its_tyler_durden_24",
            bio = "Android Developer"
        ),
        sutras = listOf(
            SutraModel(sutra = "Anyone suggest me some good movies !!!"),
            SutraModel(sutra = "Eyes can't lie")
        ),
        followersCount = 2,
        followingCount = 10,
        isCurrentUser = true,
        navController = rememberNavController(),
        onFollowersClick = {},
        onFollowingClick = {},
        onFollowClick = {}
    )
}
