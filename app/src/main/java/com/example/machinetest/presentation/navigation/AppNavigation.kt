package com.example.machinetest.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.machinetest.presentation.screen.HomeScreenUi
import com.example.machinetest.presentation.screen.LoginScreenUi
import com.example.machinetest.presentation.screen.SignupScreenUi

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = SignInScreen) {
        composable<SignInScreen> {
            LoginScreenUi(
                onNavigateToSignup = { navController.navigate(SignUpScreen) },
                onLoginSuccess = {
                    navController.navigate(HomeScreen) {
                        popUpTo(SignInScreen) { inclusive = true }
                    }
                }
            )
        }
        composable<SignUpScreen> {
            SignupScreenUi(
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        composable<HomeScreen> {
            HomeScreenUi()
        }
    }
}
