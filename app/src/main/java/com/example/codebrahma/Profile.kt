package com.example.codebrahma

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.codebrahma.auth.FirebaseAuthManager
import com.example.codebrahma.auth.GitHubSignInActivity
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun Profile() {
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    val user = FirebaseAuthManager.getCurrentUser()

    var userName by remember { mutableStateOf("Loading...") }
    var githubUsername by remember { mutableStateOf("") }
    var userBio by remember { mutableStateOf("") }
    var userCompany by remember { mutableStateOf("") }
    var userLocation by remember { mutableStateOf("") }
    var userPhotoUrl by remember { mutableStateOf<String?>(null) }
    
    // Stats
    var publicRepos by remember { mutableStateOf(0L) }
    var publicGists by remember { mutableStateOf(0L) }
    var followers by remember { mutableStateOf(0L) }
    var following by remember { mutableStateOf(0L) }
    var totalStars by remember { mutableStateOf(0L) }
    var totalCommits by remember { mutableStateOf(0L) }
    var totalIssues by remember { mutableStateOf(0L) }
    var totalPRs by remember { mutableStateOf(0L) }

    LaunchedEffect(user) {
        user?.let {
            firestore.collection("users").document(it.uid).addSnapshotListener { document, _ ->
                if (document != null && document.exists()) {
                    userName = document.getString("name") ?: "N/A"
                    githubUsername = document.getString("githubUsername") ?: ""
                    userBio = document.getString("bio") ?: ""
                    userCompany = document.getString("company") ?: ""
                    userLocation = document.getString("location") ?: ""
                    userPhotoUrl = document.getString("photoUrl")
                    
                    publicRepos = document.getLong("public_repos") ?: 0
                    publicGists = document.getLong("public_gists") ?: 0
                    followers = document.getLong("followers") ?: 0
                    following = document.getLong("following") ?: 0
                    totalStars = document.getLong("total_stars") ?: 0
                    totalCommits = document.getLong("total_commits") ?: 0
                    totalIssues = document.getLong("total_issues") ?: 0
                    totalPRs = document.getLong("total_prs") ?: 0
                }
            }
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2196F3))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    val intent = Intent(context, FirstActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    context.startActivity(intent)
                }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Profile",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Profile Header
            AsyncImage(
                model = userPhotoUrl,
                contentDescription = "Profile Picture",
                placeholder = painterResource(R.drawable.profile),
                error = painterResource(R.drawable.profile),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .shadow(4.dp, CircleShape)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = userName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            if (githubUsername.isNotEmpty()) {
                Text(
                    text = "@$githubUsername",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
            
            if (userBio.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = userBio,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(horizontal = 32.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                if (userCompany.isNotEmpty()) {
                    Text(text = "🏢 $userCompany", fontSize = 14.sp, color = Color.Gray)
                }
                if (userLocation.isNotEmpty()) {
                    Text(text = "📍 $userLocation", fontSize = 14.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Stats Grid
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard("Repos", publicRepos.toString(), Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                StatCard("Gists", publicGists.toString(), Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard("Followers", followers.toString(), Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                StatCard("Following", following.toString(), Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard("Stars Earned", totalStars.toString(), Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                StatCard("Commits", totalCommits.toString(), Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard("Issues", totalIssues.toString(), Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                StatCard("PRs", totalPRs.toString(), Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Contribution Graph
            if (githubUsername.isNotEmpty()) {
                Text(
                    text = "Daily Contributions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Start).padding(start = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(150.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    AsyncImage(
                        model = "https://ghchart.rshah.org/$githubUsername",
                        contentDescription = "Contribution Graph",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.fillMaxSize().padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val intent = Intent(context, GitHubSignInActivity::class.java)
                    intent.putExtra("is_update_mode", true)
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("Update Profile Stats", fontSize = 16.sp, color = Color.White)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .height(80.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1565C0)
            )
            Text(
                text = label,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}
