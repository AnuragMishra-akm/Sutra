package com.example.sutra.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.sutra.R
import com.example.sutra.model.SutraModel
import com.example.sutra.model.UserModel
import com.example.sutra.navigation.Routes

@Composable
fun PostItem(
    sutra: SutraModel,
    user: UserModel,
    navController: NavHostController,
    userId: String,
) {
    var selectedImageUrl by remember { mutableStateOf<String?>(null) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // User Info Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    AsyncImage(
                        model = user.imageUrl,
                        contentDescription = "User Profile Picture",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable {
                                navController.navigate("${Routes.Profile.route}/$userId")
                            },
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.person),
                        error = painterResource(id = R.drawable.person)
                    )
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape),
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user.username,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable {
                                navController.navigate("${Routes.Profile.route}/$userId")
                            }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "20m",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
                IconButton(onClick = { }) {
                    Text(text = "...", fontWeight = FontWeight.Bold)
                }
            }
            
            Text(
                text = sutra.sutra,
                fontSize = 18.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            
            // Post Content / Images
            if (sutra.images.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(sutra.images) { imageUrl ->
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Post image",
                            modifier = Modifier
                                .size(200.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedImageUrl = imageUrl },
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { }) {
                    Icon(Icons.Outlined.FavoriteBorder, contentDescription = "Like")
                }
                IconButton(onClick = { }) {
                    Icon(
                        painterResource(id = R.drawable.message),
                        contentDescription = "Comment",
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = { }) {
                    Icon(
                        painterResource(id = R.drawable.arrows),
                        contentDescription = "Repost",
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = { }) {
                    Icon(Icons.AutoMirrored.Outlined.Send, contentDescription = "Share")
                }
            }
        }
    }

    if (selectedImageUrl != null) {
        FullScreenImageViewer(
            imageUrl = selectedImageUrl!!,
            onDismiss = { selectedImageUrl = null }
        )
    }
}

@Composable
fun FullScreenImageViewer(imageUrl: String, onDismiss: () -> Unit) {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Full screen image",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Fit
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
