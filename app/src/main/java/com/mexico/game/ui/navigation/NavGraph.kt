package com.mexico.game.ui.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mexico.game.ui.screens.*
import com.mexico.game.viewmodel.GameViewModel

sealed class Screen(val route: String) {
    data object Setup : Screen("setup")
    data object InitialRoll : Screen("initial_roll")
    data object Game : Screen("game")
    data object Result : Screen("result")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: GameViewModel = viewModel()
) {
    val navigationEvent by viewModel.navigationEvent.collectAsState()

    // Handle navigation events from ViewModel
    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {
            is GameViewModel.NavigationEvent.ToInitialRoll -> {
                navController.navigate(Screen.InitialRoll.route) {
                    popUpTo(Screen.Setup.route) { inclusive = true }
                }
                viewModel.clearNavigationEvent()
            }
            is GameViewModel.NavigationEvent.ToGame -> {
                navController.navigate(Screen.Game.route) {
                    popUpTo(Screen.InitialRoll.route) { inclusive = true }
                }
                viewModel.clearNavigationEvent()
            }
            is GameViewModel.NavigationEvent.ToResult -> {
                navController.navigate(Screen.Result.route) {
                    popUpTo(Screen.Game.route) { inclusive = false }
                }
                viewModel.clearNavigationEvent()
            }
            null -> { /* No navigation event */ }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Setup.route
    ) {
        composable(Screen.Setup.route) {
            SetupScreen(
                viewModel = viewModel,
                onStartGame = {
                    // Navigation is handled by the ViewModel event
                }
            )
        }

        composable(Screen.InitialRoll.route) {
            InitialRollScreen(viewModel = viewModel)
        }

        composable(Screen.Game.route) {
            GameScreen(viewModel = viewModel)
        }

        composable(Screen.Result.route) {
            ResultScreen(viewModel = viewModel)
        }
    }
}
