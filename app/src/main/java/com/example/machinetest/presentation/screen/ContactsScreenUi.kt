package com.example.machinetest.presentation.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.machinetest.domain.dataModel.ContactModel
import com.example.machinetest.presentation.viewmodel.MyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreenUi(
    viewModel: MyViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val contacts by viewModel.contactsList.collectAsState()
    val syncState by viewModel.syncState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.fetchLocalContacts()
    }

    LaunchedEffect(syncState) {
        if (syncState.data != null) {
            Toast.makeText(context, syncState.data, Toast.LENGTH_SHORT).show()
        }
        if (syncState.error != null) {
            Toast.makeText(context, syncState.error, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Device Contacts") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Back")
                    }
                }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                if (syncState.isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.syncContacts() },
                        modifier = Modifier.weight(1f),
                        enabled = !syncState.isLoading
                    ) {
                        Text("Sync Contacts")
                    }
                    Button(
                        onClick = { viewModel.deleteCloudContacts() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        enabled = !syncState.isLoading
                    ) {
                        Text("Clear Cloud")
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(contacts) { contact ->
                ContactItem(contact)
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun ContactItem(contact: ContactModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(text = contact.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
        Text(text = contact.number, style = MaterialTheme.typography.bodyMedium)
    }
}
