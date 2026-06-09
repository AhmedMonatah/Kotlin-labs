package com.example.noteai.presentation.saved

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteai.data.remote.GeneratedTopic
import com.example.noteai.domain.model.Topic
import com.example.noteai.domain.repository.NoteRepository
import com.example.noteai.presentation.home.AppBottomNav
import com.example.noteai.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

// ──────────────────────────────────────────────────────────────────────────────
// ViewModel
// ──────────────────────────────────────────────────────────────────────────────

@HiltViewModel
class SavedViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    private val _topics = MutableStateFlow<List<Topic>>(emptyList())
    val topics: StateFlow<List<Topic>> = _topics

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            repository.getAllTopics()
                .catch { _isLoading.value = false }
                .collect { list ->
                    _topics.value = list
                    _isLoading.value = false
                }
        }
    }

    fun deleteTopic(topic: Topic) {
        viewModelScope.launch { repository.deleteTopic(topic) }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Screen
// ──────────────────────────────────────────────────────────────────────────────

@Composable
fun SavedScreen(
    onTopicClick: (String) -> Unit,
    onTabSelected: (String) -> Unit,
    viewModel: SavedViewModel = hiltViewModel()
) {
    val topics by viewModel.topics.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        bottomBar = { AppBottomNav(activeTab = "Saved", onTabSelected = onTabSelected) }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .background(Background)
                .padding(padding)
        ) {
            // Header
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Surface)
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Text("Saved Topics", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = OnBackground)
                Text(
                    if (topics.isEmpty()) "Nothing saved yet" else "${topics.size} topics saved",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }

            HorizontalDivider(thickness = 1.dp, color = SurfaceVariant)

            when {
                isLoading -> SavedShimmer()
                topics.isEmpty() -> EmptySavedState()
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(topics, key = { it.id ?: it.title }) { topic ->
                            SavedTopicCard(
                                topic = topic,
                                onClick = { onTopicClick(topic.title) },
                                onDelete = { viewModel.deleteTopic(topic) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Cards
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun SavedTopicCard(
    topic: Topic,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val diffColor = when (topic.difficulty?.lowercase()) {
        "easy" -> DifficultyEasy
        "hard" -> DifficultyHard
        else   -> DifficultyMedium
    }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Remove topic?") },
            text = { Text("\"${topic.title}\" will be removed from saved.") },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDeleteDialog = false }) {
                    Text("Remove", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }

    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Surface,
        shadowElevation = 1.dp
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(PrimaryLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Bookmark, null, tint = PrimaryBlue, modifier = Modifier.size(22.dp))
            }

            Spacer(Modifier.width(14.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    topic.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = OnBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                if (topic.content.isNotBlank()) {
                    Text(
                        topic.content,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        maxLines = 2,
                        lineHeight = 17.sp,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(6.dp))
                }
                Surface(shape = CircleShape, color = diffColor.copy(alpha = 0.12f)) {
                    Text(
                        topic.difficulty ?: "Medium",
                        fontSize = 10.sp,
                        color = diffColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // Delete button
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(Icons.Default.DeleteOutline, null, tint = OutlineVariant, modifier = Modifier.size(20.dp))
            }

            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = OutlineVariant, modifier = Modifier.size(16.dp))
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Empty & Shimmer states
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun EmptySavedState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("🔖", fontSize = 56.sp)
            Text("No saved topics yet", fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = OnBackground)
            Text(
                "Tap the bookmark icon on any topic\nto save it here.",
                fontSize = 14.sp,
                color = TextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 21.sp
            )
        }
    }
}

@Composable
private fun SavedShimmer() {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.25f, targetValue = 0.65f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Reverse),
        label = "alpha"
    )
    Column(
        Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        repeat(5) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(78.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceVariant.copy(alpha = alpha))
            )
        }
    }
}
