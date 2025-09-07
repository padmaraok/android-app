package com.todo.shared.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.todo.shared.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = viewModel()) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val uiState by viewModel.uiState.collectAsState()
    var listCode by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.home_title)) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // New List Button
            Button(
                onClick = {
                    scope.launch {
                        viewModel.createList()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = uiState !is HomeUiState.Loading
            ) {
                if (uiState is HomeUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(stringResource(R.string.new_list))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Open Existing List
            OutlinedTextField(
                value = listCode,
                onValueChange = { listCode = it.uppercase() },
                label = { Text(stringResource(R.string.list_code_hint)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (listCode.isNotBlank()) {
                        navController.navigate("list/${listCode}")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = listCode.isNotBlank()
            ) {
                Text("Open List")
            }
        }
    }

    // Handle UI state changes
    LaunchedEffect(uiState) {
        when (uiState) {
            is HomeUiState.Success -> {
                val result = uiState as HomeUiState.Success
                navController.navigate("list/${result.shortId}?writeKey=${result.writeKey}")
                viewModel.resetState()
            }
            is HomeUiState.Error -> {
                val error = uiState as HomeUiState.Error
                snackbarHostState.showSnackbar(error.message)
                viewModel.resetState()
            }
            else -> {}
        }
    }
}
