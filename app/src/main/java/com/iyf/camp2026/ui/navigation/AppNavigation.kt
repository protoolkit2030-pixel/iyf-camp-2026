package com.iyf.camp2026.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.iyf.camp2026.ui.screens.confirmation.ConfirmationScreen
import com.iyf.camp2026.ui.screens.home.HomeScreen
import com.iyf.camp2026.ui.screens.myregistrations.MyRegistrationsScreen
import com.iyf.camp2026.ui.screens.registration.RegistrationScreen
import com.iyf.camp2026.ui.screens.splash.SplashScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Registration : Screen("registration")
    object Confirmation : Screen("confirmation/{inscriptionId}") {
        fun createRoute(id: Long) = "confirmation/$id"
    }
    object MyRegistrations : Screen("my_registrations")
}

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToRegistration = {
                    navController.navigate(Screen.Registration.route)
                },
                onNavigateToMyRegistrations = {
                    navController.navigate(Screen.MyRegistrations.route)
                }
            )
        }

        composable(Screen.Registration.route) {
            RegistrationScreen(
                onNavigateToConfirmation = { id ->
                    navController.navigate(Screen.Confirmation.createRoute(id)) {
                        popUpTo(Screen.Registration.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.Confirmation.route,
            arguments = listOf(navArgument("inscriptionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val inscriptionId = backStackEntry.arguments?.getLong("inscriptionId") ?: 0L
            ConfirmationScreen(
                inscriptionId = inscriptionId,
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.MyRegistrations.route) {
            MyRegistrationsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
