package com.example.machinetest.presentation.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.machinetest.domain.dataModel.UserData
import com.example.machinetest.presentation.viewmodel.MyViewModel
import com.example.machinetest.utils.ResultState

@Composable
fun SignupScreenUi(
    viewModel: MyViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("User") }
    var secretCode by remember { mutableStateOf("") }
    
    val registerState by viewModel.registerUser.collectAsState()
    val adminKey by viewModel.adminKey.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.fetchAdminSecretKey()
    }

    LaunchedEffect(registerState) {
        if (registerState.data != null) {
            Toast.makeText(context, registerState.data, Toast.LENGTH_SHORT).show()
            onNavigateToLogin()
        }
        if (registerState.error != null) {
            Toast.makeText(context, registerState.error, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .imePadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Create Account",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Role Selection
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = role == "User", onClick = { role = "User" })
                Text("User")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = role == "Admin", onClick = { role = "Admin" })
                Text("Admin")
            }
        }

        if (role == "Admin") {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = secretCode,
                onValueChange = { secretCode = it },
                label = { Text("Admin Secret Code") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (registerState.isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = { 
                    if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                        if (role == "Admin") {
                            if (adminKey == null) {
                                Toast.makeText(context, "Checking Admin Security...", Toast.LENGTH_SHORT).show()
                                viewModel.fetchAdminSecretKey()
                            } else if (secretCode != adminKey) {
                                Toast.makeText(context, "Invalid Admin Code", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.registerUser(UserData(name, email, password, role))
                            }
                        } else {
                            viewModel.registerUser(UserData(name, email, password, role))
                        }
                    } else {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Sign Up", fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onNavigateToLogin) {
            Text("Already have an account? Login")
        }
    }
}
