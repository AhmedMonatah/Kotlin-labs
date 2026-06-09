package com.example.noteai.presentation.search

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.noteai.data.remote.GeneratedTopic
import com.example.noteai.presentation.home.AppBottomNav
import com.example.noteai.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateToTopicByTitle: (String) -> Unit,
    onTabSelected: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val query by viewModel.query.collectAsState()
    val state by viewModel.state.collectAsState()

    Scaffold(
        bottomBar = { AppBottomNav(activeTab = "Search", onTabSelected = onTabSelected) }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .background(Background)
                .padding(padding)
        ) {
            // ── Premium Search Header ────────────────────────────────────────
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(Surface)
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Column {
                    Text("Explore Topics", fontWeight = FontWeight.Bold, fontSize = 28.sp, color = OnBackground)
                    Text("Deep dive into coding interview concepts", fontSize = 14.sp, color = TextSecondary)
                    
                    Spacer(Modifier.height(20.dp))
                    
                    TextField(
                        value = query,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        placeholder = { Text("Search logic, architecture, algorithms...", color = OutlineVariant, fontSize = 14.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, null,
                                tint = if (query.isNotBlank()) PrimaryBlue else TextSecondary,
                                modifier = Modifier.size(22.dp))
                        },
                        trailingIcon = {
                            if (query.isNotBlank()) {
                                IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                    Icon(Icons.Default.Close, null, tint = TextSecondary)
                                }
                            }
                        },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Background,
                            unfocusedContainerColor = Background,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = PrimaryBlue
                        )
                    )
                }
            }

            // ── Popular searches ─────────────────────────────────────────────
            if (query.isBlank() && !state.isLoading) {
                Column(Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                        Text("Suggested", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OnBackground)
                    }
                    Spacer(Modifier.height(12.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(listOf("SOLID", "Clean Arch", "Coroutines", "Flow", "Retrofit")) { chip ->
                            Surface(
                                onClick = { viewModel.onSearchQueryChange(chip) },
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, SurfaceVariant),
                                color = Surface
                            ) {
                                Text(
                                    chip, fontSize = 13.sp, color = PrimaryBlue,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            // ── Search State (Loading / Results) ──────────────────────────────
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (state.isLoading) {
                    items(4) { ShimmerSearchCard() }
                } else if (state.searchResults.isNotEmpty()) {
                    item {
                        Text(
                            "Gemini found ${state.searchResults.size} topics",
                            fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                            color = TextSecondary,
                            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                        )
                    }
                    items(state.searchResults) { topic ->
                        EnhancedSearchTopicCard(topic) { onNavigateToTopicByTitle(topic.title) }
                    }
                } else if (query.isNotBlank()) {
                    item { SearchEmptyState(query) }
                } else {
                    item {
                        Box(Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.MenuBook, null, tint = SurfaceVariant, modifier = Modifier.size(64.dp))
                                Spacer(Modifier.height(12.dp))
                                Text("Search for anything to start learning", color = TextSecondary, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShimmerSearchCard() {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.2f, targetValue = 0.5f,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing), RepeatMode.Reverse),
        label = "a"
    )
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Surface,
        border = BorderStroke(1.dp, SurfaceVariant.copy(alpha = alpha)),
        modifier = Modifier.fillMaxWidth().height(100.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(48.dp).clip(CircleShape).background(SurfaceVariant.copy(alpha = alpha)))
            Spacer(Modifier.width(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.height(14.dp).fillMaxWidth(0.5f).clip(RoundedCornerShape(7.dp)).background(SurfaceVariant.copy(alpha = alpha)))
                Box(Modifier.height(12.dp).fillMaxWidth().clip(RoundedCornerShape(6.dp)).background(SurfaceVariant.copy(alpha = alpha)))
            }
        }
    }
}

@Composable
private fun EnhancedSearchTopicCard(topic: GeneratedTopic, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = Surface,
        border = BorderStroke(1.dp, SurfaceVariant),
        shadowElevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon component
            Box(
                Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(PrimaryLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.MenuBook, null,
                    tint = PrimaryBlue, modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(Modifier.weight(1f)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        topic.title, fontWeight = FontWeight.Bold,
                        fontSize = 16.sp, color = OnBackground,
                        maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                    // Difficulty pill
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when(topic.difficulty.lowercase()) {
                            "easy" -> DifficultyEasy.copy(alpha = 0.1f)
                            "hard" -> DifficultyHard.copy(alpha = 0.1f)
                            else -> DifficultyMedium.copy(alpha = 0.1f)
                        }
                    ) {
                        Text(
                            topic.difficulty,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp, fontWeight = FontWeight.Bold,
                            color = when(topic.difficulty.lowercase()) {
                                "easy" -> DifficultyEasy
                                "hard" -> DifficultyHard
                                else -> DifficultyMedium
                            }
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    topic.content,
                    fontSize = 13.sp, color = TextSecondary,
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            }
            
            Spacer(Modifier.width(8.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = SurfaceVariant, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun SearchEmptyState(query: String) {
    Box(Modifier.fillMaxWidth().padding(top = 48.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🔍", fontSize = 48.sp)
            Spacer(Modifier.height(16.dp))
            Text("No results for \"$query\"", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = OnBackground)
            Text("Try searching for something else", color = TextSecondary, fontSize = 14.sp)
        }
    }
}
