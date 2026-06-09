package com.example.noteai.presentation.addtopic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.noteai.ui.theme.*
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTopicScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddTopicViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedFolderId by remember { mutableStateOf<Int?>(null) }
    var selectedDifficulty by remember { mutableStateOf<String?>(null) }
    
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AddTopicViewModel.UiEvent.SaveTopic -> onNavigateBack()
                is AddTopicViewModel.UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Add New Topic", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    TextButton(onClick = onNavigateBack) { Text("Cancel", color = OutlineVariant) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).background(Background),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { AddField("Title", title, { title = it }, "Enter topic title") }
            item {
                 SelectFolderUI(selectedFolderId) { selectedFolderId = it }
            }
            item { AddField("Content", content, { content = it }, "Write definitions, explanations...", minLines = 5) }
            item { AddField("Notes", notes, { notes = it }, "Add key notes or exam tips...", minLines = 3) }
            item {
                Text("Optional Tags", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Important", "Interview", "Easy", "Hard").forEach { tag ->
                        FilterChip(
                            selected = selectedDifficulty == tag,
                            onClick = { selectedDifficulty = if (selectedDifficulty == tag) null else tag },
                            label = { Text(tag) },
                            shape = CircleShape
                        )
                    }
                }
            }
            item {
                Button(
                    onClick = { viewModel.saveTopic(title, selectedFolderId, content, notes, selectedDifficulty) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Save Topic", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun AddField(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String, minLines: Int = 1) {
    Column {
        Text(label, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = OnBackground)
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)),
            placeholder = { Text(placeholder, color = OutlineVariant) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Surface,
                unfocusedContainerColor = Surface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            minLines = minLines
        )
    }
}

@Composable
fun SelectFolderUI(selectedId: Int?, onFolderSelected: (Int) -> Unit) {
    Column {
        Text("Select Folder", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Surface).padding(16.dp)) {
            Text("Algorithms", color = OnBackground) // Placeholder dropdown
            Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.align(Alignment.CenterEnd))
        }
    }
}
