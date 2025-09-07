package com.todo.shared.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.todo.shared.ui.home.HomeScreen
import com.todo.shared.ui.list.TodoListScreen
import com.todo.shared.ui.theme.SharedTodoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Handle deep links
        handleIntent(intent)

        setContent {
            SharedTodoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TodoApp()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener { pendingDynamicLinkData ->
                val deepLink = pendingDynamicLinkData?.link
                if (deepLink != null) {
                    // Handle dynamic link
                    val shortId = deepLink.getQueryParameter("shortId")
                    val writeKey = deepLink.getQueryParameter("k")
                    if (shortId != null) {
                        // Navigate to list screen
                        // This will be handled by the navigation system
                    }
                }
            }
            .addOnFailureListener { e ->
                // Handle error
            }
    }
}

@Composable
fun TodoApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController)
        }
        composable("list/{shortId}?writeKey={writeKey}") { backStackEntry ->
            val shortId = backStackEntry.arguments?.getString("shortId") ?: ""
            val writeKey = backStackEntry.arguments?.getString("writeKey")
            TodoListScreen(navController, shortId, writeKey)
        }
    }
}
