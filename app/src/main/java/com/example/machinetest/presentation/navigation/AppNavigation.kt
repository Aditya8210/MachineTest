package com.example.machinetest.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.machinetest.presentation.screen.ContactsScreenUi
import com.example.machinetest.presentation.screen.HomeScreenUi
import com.example.machinetest.presentation.screen.LoginScreenUi
import com.example.machinetest.presentation.screen.SignupScreenUi
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    val startDest = if (auth.currentUser != null) HomeScreen else SignInScreen

    NavHost(navController = navController, startDestination = startDest) {
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
            HomeScreenUi(
                onNavigateToContacts = { navController.navigate(ContactsScreen) },
                onLogout = {
                    auth.signOut()
                    navController.navigate(SignInScreen) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable<ContactsScreen> {
            ContactsScreenUi(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
