package com.example.noteai.presentation.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.noteai.data.remote.GeneratedTopic
import com.example.noteai.presentation.addtopic.AddTopicViewModel
import com.example.noteai.ui.theme.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToSearch: () -> Unit,
    onNavigateToTopicByTitle: (String) -> Unit,
    onTabSelected: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    addTopicViewModel: AddTopicViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showAddSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        addTopicViewModel.eventFlow.collectLatest { event ->
            if (event is AddTopicViewModel.UiEvent.SaveTopic) {
                scope.launch { sheetState.hide() }.invokeOnCompletion { showAddSheet = false }
            }
        }
    }

    if (showAddSheet) {
        AddTopicSheet(
            sheetState = sheetState,
            onDismiss = { showAddSheet = false },
            onSave = { title, content, notes, diff ->
                addTopicViewModel.saveTopic(title, null, content, notes, diff)
            }
        )
    }

    Scaffold(
        bottomBar = { AppBottomNav(activeTab = "Home", onTabSelected = onTabSelected) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddSheet = true },
                containerColor = PrimaryBlue,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
            ) { Icon(Icons.Default.Add, "Add topic") }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).background(Background),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // ── Premium Header ───────────────────────────────────────────────
            item {
                Box(
                    Modifier.fillMaxWidth().height(180.dp)
                        .background(Brush.verticalGradient(listOf(PrimaryBlue, Color(0xFF1976D2), Surface)))
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                ) {
                    Column {
                        Text("Welcome Back,", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                        Text("Future Developer", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 28.sp)
                        
                        Spacer(Modifier.height(20.dp))
                        
                        // Search bar proxy
                        Surface(
                            onClick = onNavigateToSearch,
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            shadowElevation = 4.dp
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Search, null, tint = TextSecondary, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(12.dp))
                                Text("Search interview topics...", color = OutlineVariant, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }

            // ── Recommended Section ──────────────────────────────────────────
            item {
                Text(
                    "Recommended for You", fontWeight = FontWeight.Bold, fontSize = 18.sp,
                    color = OnBackground, modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 16.dp)
                )
            }

            if (state.isLoadingAiTopics) {
                item { HomeShimmerList() }
            } else {
                items(state.aiSuggestedTopics) { topic ->
                    PremiumTopicCard(topic) { onNavigateToTopicByTitle(topic.title) }
                }
            }

            // ── Explore Categories ───────────────────────────────────────────
            item {
                Text(
                    "Master the Categories", fontWeight = FontWeight.Bold, fontSize = 18.sp,
                    color = OnBackground, modifier = Modifier.padding(start = 24.dp, top = 32.dp, bottom = 16.dp)
                )
                PremiumCategoryGrid(onNavigateToTopicByTitle)
            }
        }
    }
}

@Composable
fun PremiumTopicCard(topic: GeneratedTopic, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 6.dp),
        shape = RoundedCornerShape(24.dp),
        color = Surface,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, SurfaceVariant)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(54.dp).clip(RoundedCornerShape(16.dp)).background(PrimaryLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.AutoMirrored.Filled.MenuBook, null, tint = PrimaryBlue, modifier = Modifier.size(26.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(topic.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = OnBackground)
                Text(topic.content, fontSize = 13.sp, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(shape = CircleShape, color = DifficultyEasy.copy(alpha = 0.1f)) {
                        Text(topic.difficulty, fontSize = 10.sp, color = DifficultyEasy, fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                    }
                    topic.keyConcepts.take(2).forEach { concept ->
                        Surface(shape = CircleShape, color = Background) {
                            Text(concept, fontSize = 10.sp, color = TextSecondary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                        }
                    }
                }
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = SurfaceVariant, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun HomeShimmerList() {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.2f, targetValue = 0.6f,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing), RepeatMode.Reverse),
        label = "a"
    )
    Column(Modifier.padding(horizontal = 24.dp)) {
        repeat(3) {
            Box(
                Modifier.fillMaxWidth().height(100.dp).padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(24.dp)).background(SurfaceVariant.copy(alpha = alpha))
            )
        }
    }
}

@Composable
fun PremiumCategoryGrid(onClick: (String) -> Unit) {
    val cats = listOf(
        "OOP" to Icons.Default.Code,
        "Algorithms" to Icons.Default.Sort,
        "Data Struct" to Icons.Default.Layers,
        "System D" to Icons.Default.AccountTree,
        "Databases" to Icons.Default.Storage,
        "Networking" to Icons.Default.Public
    )
    LazyRow(
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(cats) { (name, icon) ->
            Surface(
                onClick = { onClick(name) },
                shape = RoundedCornerShape(20.dp),
                color = Surface,
                border = BorderStroke(1.dp, SurfaceVariant)
            ) {
                Column(
                    Modifier.width(110.dp).padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(Modifier.size(40.dp).clip(CircleShape).background(PrimaryLight), contentAlignment = Alignment.Center) {
                        Icon(icon, null, tint = PrimaryBlue, modifier = Modifier.size(22.dp))
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(name, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, color = OnBackground)
                }
            }
        }
    }
}

@Composable
fun AppBottomNav(activeTab: String, onTabSelected: (String) -> Unit) {
    NavigationBar(containerColor = Surface, tonalElevation = 8.dp) {
        listOf(
            Triple("Home", Icons.Default.Home, "Home"),
            Triple("Search", Icons.Default.Search, "Search"),
            Triple("Saved", Icons.Default.Bookmark, "Saved"),
            Triple("Profile", Icons.Default.Person, "Profile")
        ).forEach { (label, icon, tab) ->
            NavigationBarItem(
                selected = activeTab == tab,
                onClick = { onTabSelected(tab) },
                icon = { Icon(icon, null) },
                label = { Text(label, fontWeight = if (activeTab == tab) FontWeight.Bold else FontWeight.Normal) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryBlue,
                    indicatorColor = PrimaryLight,
                    unselectedIconColor = TextSecondary
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTopicSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf("Medium") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = Surface
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Add New Topic", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = OnBackground)
            
            OutlinedTextField(
                value = title, onValueChange = { title = it },
                label = { Text("Title") }, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            
            OutlinedTextField(
                value = content, onValueChange = { content = it },
                label = { Text("Explanation") }, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp), minLines = 3
            )
            
            OutlinedTextField(
                value = notes, onValueChange = { notes = it },
                label = { Text("Interview Tip") }, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Easy", "Medium", "Hard").forEach { diff ->
                    FilterChip(
                        selected = difficulty == diff,
                        onClick = { difficulty = diff },
                        label = { Text(diff) },
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }
            
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title, content, notes, difficulty)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Save Topic", fontWeight = FontWeight.Bold)
            }
        }
    }
}
