package com.todo.shared.ui.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.todo.shared.R
import com.todo.shared.data.model.TodoItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    navController: NavController,
    shortId: String,
    writeKey: String?,
    viewModel: TodoListViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val items by viewModel.items.collectAsState()
    val isReadOnly = writeKey.isNullOrBlank()

    LaunchedEffect(shortId) {
        viewModel.loadList(shortId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("List $shortId") },
                actions = {
                    if (!isReadOnly) {
                        IconButton(onClick = { /* Share menu */ }) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = if (!isReadOnly) {
            {
                FloatingActionButton(onClick = { /* Add item */ }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Item")
                }
            }
        } else null
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isReadOnly) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.read_only_banner),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(items) { item ->
                    TodoItemRow(
                        item = item,
                        isReadOnly = isReadOnly,
                        onToggle = { done ->
                            if (!isReadOnly && writeKey != null) {
                                scope.launch {
                                    viewModel.toggleItem(shortId, item.id, done, writeKey)
                                }
                            }
                        },
                        onDelete = {
                            if (!isReadOnly && writeKey != null) {
                                scope.launch {
                                    viewModel.deleteItem(shortId, item.id, writeKey)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoItemRow(
    item: TodoItem,
    isReadOnly: Boolean,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = { if (!isReadOnly) onToggle(!item.done) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.done,
                onCheckedChange = { if (!isReadOnly) onToggle(it) },
                enabled = !isReadOnly
            )

            Text(
                text = item.text,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
                style = if (item.done) {
                    MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                } else {
                    MaterialTheme.typography.bodyMedium
                }
            )

            if (!isReadOnly) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete"
                    )
                }
            }
        }
    }
}
