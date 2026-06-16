package com.example.codebrahma

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.codebrahma.ui.theme.CodeBrahmaTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class FirstActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CodeBrahmaTheme {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LearnNavBotSheet()
                    FCMMessage()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
@Preview
fun LearnNavBotSheet() {
    val navigationcontroller = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var showBottomSheet by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("Loading...") }

    // Fetch email from Firestore
    LaunchedEffect(Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            FirebaseFirestore.getInstance().collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    email = document.getString("email") ?: "No Email Found"
                }
                .addOnFailureListener {
                    email = "Error fetching email"
                }
        } else {
            email = "Not Logged In"
        }
    }
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet {
                Box(
                    modifier = Modifier
                        .background(Color.White)
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    Column(modifier = Modifier.align(Alignment.Center)) {
                        Text(
                            text = "Code Brahma",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(20.dp))

                        Image(
                            painter = painterResource(id = R.drawable.github),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .align(Alignment.CenterHorizontally)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = email,
                            style = MaterialTheme.typography.labelMedium.copy(color = Color.Black),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
                Divider()

                val navBackStackEntry by navigationcontroller.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // --- Productivity ---
                NavigationDrawerItem(
                    label = { Text(text = "Manage Tasks and Reminders", color = Color.Black) },
                    selected = currentRoute == Screens.TaskandReminderManager.screen,
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.task_rem_manager),
                            contentDescription = "Tasks",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Unspecified
                        )
                    },
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        navigationcontroller.navigate(Screens.TaskandReminderManager.screen) {
                            popUpTo(Screens.TaskandReminderManager.screen) { inclusive = false }
                        }
                    }
                )

                NavigationDrawerItem(
                    label = { Text(text = "Your Code Snippets", color = Color.Black) },
                    selected = currentRoute == Screens.SnippetScreen.screen,
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.auto_write_code),
                            contentDescription = "Saved Code Snippets",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Blue
                        )
                    },
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        navigationcontroller.navigate(Screens.SnippetScreen.screen) {
                            popUpTo(Screens.SnippetScreen.screen) { inclusive = false }
                        }
                    }
                )

                NavigationDrawerItem(
                    label = { Text(text = "Bookmarks", color = Color.Black) },
                    selected = currentRoute == Screens.Bookmarks.screen,
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.bookmarks),
                            contentDescription = "Bookmarks",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Unspecified
                        )
                    },
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        navigationcontroller.navigate(Screens.Bookmarks.screen) {
                            popUpTo(Screens.Bookmarks.screen) { inclusive = false }
                        }
                    }
                )

                Divider()

                // --- Learning ---
                NavigationDrawerItem(
                    label = { Text(text = "Roadmap Generator", color = Color.Black) },
                    selected = currentRoute == Screens.AiLearningPaths.screen,
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ai_learning_path),
                            contentDescription = "AI Learning Paths",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Unspecified
                        )
                    },
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        navigationcontroller.navigate(Screens.AiLearningPaths.screen) {
                            popUpTo(Screens.AiLearningPaths.screen) { inclusive = false }
                        }
                    }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Smart Documentation Search", color = Color.Black) },
                    selected = currentRoute == Screens.SearchDocumentation.screen,
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.search_documentation),
                            contentDescription = "Smartly Search Documentation",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Unspecified
                        )
                    },
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        navigationcontroller.navigate(Screens.SearchDocumentation.screen) {
                            popUpTo(Screens.SearchDocumentation.screen) { inclusive = false }
                        }
                    }
                )

                Divider()

                // --- Tools ---
                NavigationDrawerItem(
                    label = { Text(text = "Run and Debug Code", color = Color.Black) },
                    selected = currentRoute == Screens.CodeOptimizationandDebugging.screen,
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.optimize_debug),
                            contentDescription = "Optimize and debug code",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Unspecified
                        )
                    },
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        navigationcontroller.navigate(Screens.CodeOptimizationandDebugging.screen) {
                            popUpTo(Screens.CodeOptimizationandDebugging.screen) { inclusive = false }
                        }
                    }
                )

                NavigationDrawerItem(
                    label = { Text(text = "Voice Command Debugging", color = Color.Black) },
                    selected = currentRoute == Screens.VoiceCommandDebugging.screen,
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.voicecommand),
                            contentDescription = "Voice Command Debugging",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Unspecified
                        )
                    },
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        navigationcontroller.navigate(Screens.VoiceCommandDebugging.screen) {
                            popUpTo(Screens.VoiceCommandDebugging.screen) { inclusive = false }
                        }
                    }
                )

                NavigationDrawerItem(
                    label = { Text(text = "Web Scrapping Insights", color = Color.Black) },
                    selected = currentRoute == Screens.WebScrapperInsights.screen,
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.web_scrapper),
                            contentDescription = "Web Scraper",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Unspecified
                        )
                    },
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        navigationcontroller.navigate(Screens.WebScrapperInsights.screen) {
                            popUpTo(Screens.WebScrapperInsights.screen) { inclusive = false }
                        }
                    }
                )

                NavigationDrawerItem(
                    label = { Text(text = "Offline Mode AI", color = Color.Black) },
                    selected = currentRoute == Screens.OfflineMode.screen,
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.offline_mode),
                            contentDescription = "Offline Mode AI",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Unspecified
                        )
                    },
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        navigationcontroller.navigate(Screens.OfflineMode.screen) {
                            popUpTo(Screens.OfflineMode.screen) { inclusive = false }
                        }
                    }
                )

                Divider()

                // --- App ---
                NavigationDrawerItem(
                    label = { Text(text = "Settings", color = Color.Black) },
                    selected = currentRoute == Screens.Settings.screen,
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.settings),
                            contentDescription = "Settings",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Unspecified
                        )
                    },
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        navigationcontroller.navigate(Screens.Settings.screen) {
                            popUpTo(Screens.Settings.screen) { inclusive = false }
                        }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                Row {
                    TopAppBar(
                        title = { Text("Code Brahma", fontFamily = FontFamily.Serif) },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color(0xFF2196F3),
                            titleContentColor = Color.White,
                            navigationIconContentColor = Color.White
                        ),
                        navigationIcon = {
                            IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                                Icon(Icons.Rounded.Menu, contentDescription = "MenuButton")
                            }
                        },
                        actions = {
                            val context = LocalContext.current
                            IconButton(onClick = { MyNotification(context, "FCM", "Wake up! With Great Knowledge comes Increased Development")
                                .FirNotification() }) {
                                Icon(Icons.Default.Favorite, contentDescription = "Favorite")
                            }
                        }
                    )
                }
            },
            bottomBar = {
                val navBackStackEntry by navigationcontroller.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                if (currentRoute != Screens.Profile.screen) {
                    BottomAppBar(containerColor = Color(0xFF2196F3)) {
                        // 1. Home
                        IconButton(
                            onClick = {
                                navigationcontroller.navigate(Screens.Home.screen) {
                                    popUpTo(Screens.Home.screen) { inclusive = true }
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = "Home",
                                modifier = Modifier.size(26.dp),
                                tint = if (currentRoute == Screens.Home.screen) Color.White else Color.Black
                            )
                        }

                        // 2. Auto Write Code
                        IconButton(
                            onClick = {
                                navigationcontroller.navigate(Screens.AutoWriteCode.screen) {
                                    popUpTo(Screens.AutoWriteCode.screen) { inclusive = true }
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.auto_write_code),
                                contentDescription = "Write Code",
                                modifier = Modifier.size(26.dp),
                                tint = if (currentRoute == Screens.AutoWriteCode.screen) Color.White else Color.Black
                            )
                        }

                        // 3. FAB (Scan Code)
                        Box(
                            modifier = Modifier
                                .weight(1.2f)
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            FloatingActionButton(
                                onClick = {
                                    navigationcontroller.navigate(Screens.ScanCode.screen) {
                                        popUpTo(Screens.ScanCode.screen) { inclusive = true }
                                    }
                                },
                                containerColor = Color(0xFFF44336)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.qrcode),
                                    contentDescription = "Scan Code",
                                    modifier = Modifier.size(34.dp),
                                    tint = Color.White
                                )
                            }
                        }

                        // 4. AI Debug Assistant
                        IconButton(
                            onClick = {
                                navigationcontroller.navigate(Screens.AIDebugAssistant.screen) {
                                    popUpTo(Screens.AIDebugAssistant.screen) { inclusive = true }
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.aibot),
                                contentDescription = "AI Debugging Assistant",
                                modifier = Modifier.size(34.dp),
                                tint = if (currentRoute == Screens.AIDebugAssistant.screen) Color.White else Color.Black
                            )
                        }

                        // 5. Profile
                        IconButton(
                            onClick = {
                                navigationcontroller.navigate(Screens.Profile.screen) {
                                    popUpTo(Screens.Profile.screen) { inclusive = true }
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.profile),
                                contentDescription = "Profile",
                                modifier = Modifier.size(26.dp),
                                tint = if (currentRoute == Screens.Profile.screen) Color.White else Color.Black
                            )
                        }
                    }
                }
            }
        ) {
            NavHost(navController = navigationcontroller, startDestination = Screens.Home.screen) {
                composable(Screens.Home.screen) { Home() }
                composable(Screens.SnippetScreen.screen) { SnippetScreen() }
                composable(Screens.AiLearningPaths.screen) { AiLearningPaths() }
                composable(Screens.SearchDocumentation.screen) { SearchDocumentation() }
                composable(Screens.CodeOptimizationandDebugging.screen) { CodeOptimizationAndDebugging() }
                composable(Screens.TaskandReminderManager.screen) { TaskAndReminderManager() }
                composable(Screens.WebScrapperInsights.screen) { WebScrapperInsights() }
                composable(Screens.VoiceCommandDebugging.screen) { VoiceCommandDebugging() }
                composable(Screens.OfflineMode.screen) { OfflineMode() }
                composable(Screens.Profile.screen) { Profile() }
                composable(Screens.AutoWriteCode.screen) { AutoWriteCode() }
                composable(Screens.AIDebugAssistant.screen) { AIDebugAssistant() }
                composable(Screens.Bookmarks.screen) { Bookmarks() }
                composable(Screens.ScanCode.screen) { ScanCode() }
                composable(Screens.Settings.screen) { Settings() }
            }
        }
    }
}

@Composable
fun FCMMessage() {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp,end = 4.dp),
        contentAlignment = Alignment.TopEnd
    ) {
    }
}