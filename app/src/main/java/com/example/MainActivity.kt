package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.AppDatabase
import com.example.data.ServantRepository
import com.example.ui.AddServantScreen
import com.example.ui.HomeScreen
import com.example.ui.ServantViewModel
import com.example.ui.ServantViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    
    val database = AppDatabase.getDatabase(this)
    val repository = ServantRepository(database.servantDao())
    val viewModelFactory = ServantViewModelFactory(repository)

    setContent {
      MyApplicationTheme {
        val navController = rememberNavController()
        val viewModel: ServantViewModel = viewModel(factory = viewModelFactory)
        
        NavHost(navController = navController, startDestination = "home") {
            composable("home") {
                HomeScreen(
                    viewModel = viewModel,
                    onAddClick = { navController.navigate("add") },
                    onEditClick = { servantId -> navController.navigate("edit/$servantId") }
                )
            }
            composable("add") {
                AddServantScreen(
                    viewModel = viewModel,
                    servantId = null,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = "edit/{servantId}",
                arguments = listOf(androidx.navigation.navArgument("servantId") { type = androidx.navigation.NavType.IntType })
            ) { backStackEntry ->
                val servantId = backStackEntry.arguments?.getInt("servantId")
                AddServantScreen(
                    viewModel = viewModel,
                    servantId = servantId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
      }
    }
  }
}

