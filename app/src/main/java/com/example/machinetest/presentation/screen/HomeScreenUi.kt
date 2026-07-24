package com.example.machinetest.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.machinetest.presentation.viewmodel.MyViewModel

@Composable
fun HomeScreenUi(
    viewModel: MyViewModel = hiltViewModel(),
    onNavigateToContacts: () -> Unit,
    onLogout: () -> Unit
) {
    val userDataState by viewModel.userData.collectAsState()
    val dashboardState by viewModel.dashboardData.collectAsState()
    val context = LocalContext.current


    //for runtime permission
    val permissions = arrayOf(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.READ_SMS
    )

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result.values.all { it }) {
            viewModel.fetchDashboardStats()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchUserData()
        val allGranted = permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
        if (allGranted) {
            viewModel.fetchDashboardStats()
        } else {
            launcher.launch(permissions)
        }
    }
// Design part
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (userDataState.isLoading) {
            CircularProgressIndicator()
        } else if (userDataState.data != null) {
            val user = userDataState.data!!
            
            Text(
                text = "Dashboard",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(text = "Welcome, ${user.name}", fontSize = 20.sp)
            Text(text = "Role: ${user.role}", fontWeight = FontWeight.SemiBold)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // for Role based info
            Column(modifier = Modifier.fillMaxWidth()) {
                DashboardItem("Total Contacts", dashboardState.contactsCount.toString())
                DashboardItem("Total Call Logs", dashboardState.callLogsCount.toString())
                DashboardItem("Total SMS", dashboardState.smsCount.toString())
                DashboardItem("Last Sync", dashboardState.lastSyncTime)
            }

            if (user.role == "Admin") {
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
                
                // Development Mode for Admins
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Development Mode: ", fontWeight = FontWeight.Bold)
                    Text(
                        text = "ON", 
                        color = Color.Green, 
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onNavigateToContacts) {
                Text(text = "View Contacts")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onLogout) {
                Text(text = "Logout")
            }
        } else {
            Text(text = "Error loading user data: ${userDataState.error ?: "Unknown error"}")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onLogout) {
                Text(text = "Back to Login")
            }
        }
    }
}

@Composable
fun DashboardItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "$label:", fontWeight = FontWeight.Medium)
        Text(text = value, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
    }
}
