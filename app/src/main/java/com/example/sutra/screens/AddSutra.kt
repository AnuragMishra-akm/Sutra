package com.example.sutra.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil3.compose.rememberAsyncImagePainter
import com.example.sutra.R
import com.example.sutra.utils.SharedPrep
import com.example.sutra.viewmodel.AddSutraViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSutra(navController: NavHostController) {
    var text by remember { mutableStateOf("") }
    val imageUris = remember { mutableStateListOf<Uri>() }
    val scrollState = rememberScrollState()
    val sutraViewModel: AddSutraViewModel = viewModel()
    val isPosted by sutraViewModel.isPosted.observeAsState()
    val error by sutraViewModel.error.observeAsState()

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        imageUris.addAll(uris)
    }

    LaunchedEffect(isPosted, error) {
        if (isPosted == true) {
            navController.popBackStack()
            imageUris.clear()
            text = ""
            Toast.makeText(context, "Sutra posted successfully", Toast.LENGTH_SHORT).show()
            sutraViewModel.resetState()
        } else if (isPosted == false) {
            Toast.makeText(context, error ?: "Error posting sutra", Toast.LENGTH_SHORT).show()
            sutraViewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "New Sutra",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Handle drafts */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.writing),
                            contentDescription = "Drafts",
                            modifier = Modifier.size(24.dp),
                        )
                    }
                    IconButton(onClick = { /* Handle more */ }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Menu options")
                    }
                }
            )
        },
        bottomBar = {
            Column {
                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.options),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = Color(0xFF616161)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Reply options",
                            color = Color(0xFF616161),
                            fontSize = 14.sp
                        )
                    }
                    
                    Button(
                        onClick = { 
                            val uid = FirebaseAuth.getInstance().currentUser?.uid
                            if (uid != null) {
                                sutraViewModel.saveSutra(text, uid, imageUris.toList())
                            } else {
                                Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = text.isNotEmpty() || imageUris.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (text.isNotEmpty() || imageUris.isNotEmpty()) MaterialTheme.colorScheme.primary else Color(0xFFE0E0E0),
                            contentColor = if (text.isNotEmpty() || imageUris.isNotEmpty()) Color.White else Color(0xFF616161)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
                    ) {
                        Text(text = "Post", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                // Profile Image
                Image(
                    painter = if (SharedPrep.getImageUri(context).isEmpty()) 
                        painterResource(id = R.drawable.person) 
                    else 
                        rememberAsyncImagePainter(SharedPrep.getImageUri(context)),
                    contentDescription = null,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = SharedPrep.getUserName(context),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(modifier = Modifier.fillMaxWidth()) {
                        if (text.isEmpty()) {
                            Text(
                                text = "What's new?",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                        BasicTextField(
                            value = text,
                            onValueChange = { 
                                if (it.length <= 999) text = it 
                            },
                            textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 488.dp)
                        )
                    }

                    // Selected Images Display
                    if (imageUris.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(imageUris) { uri ->
                                Box(modifier = Modifier.size(200.dp)) {
                                    Image(
                                        painter = rememberAsyncImagePainter(uri),
                                        contentDescription = "Selected Image",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    IconButton(
                                        onClick = { imageUris.remove(uri) },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(4.dp)
                                            .size(24.dp)
                                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove Image",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Toolbar Icons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.photo), 
                            contentDescription = "Gallery", 
                            modifier = Modifier
                                .size(20.dp)
                                .clickable { launcher.launch("image/*") }, 
                            tint = Color.Gray
                        )
                        Icon(painter = painterResource(id = R.drawable.gif), contentDescription = "GIF", modifier = Modifier.size(20.dp), tint = Color.Gray)
                        Icon(painter = painterResource(id = R.drawable.write), contentDescription = "List", modifier = Modifier.size(20.dp), tint = Color.Gray)
                        Icon(painter = painterResource(id = R.drawable.quote), contentDescription = "Quote", modifier = Modifier.size(20.dp), tint = Color.Gray)
                        Icon(painter = painterResource(id = R.drawable.menudots), contentDescription = "More tools", modifier = Modifier.size(20.dp), tint = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Add to thread section
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = if (SharedPrep.getImageUri(context).isEmpty()) 
                                painterResource(id = R.drawable.person) 
                            else 
                                rememberAsyncImagePainter(SharedPrep.getImageUri(context)),
                            contentDescription = null,
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                            alpha = 0.5f
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Add to thread",
                            color = Color(0xFF616161),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddSutraPreview() {
    AddSutra(rememberNavController())
}
