package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.Servant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddServantScreen(
    viewModel: ServantViewModel,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var iconUrl by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf("") }
    var noblePhantasm by remember { mutableStateOf("") }
    
    var attributesStr by remember { mutableStateOf("") }
    var alignmentsStr by remember { mutableStateOf("") }
    var gendersStr by remember { mutableStateOf("") }
    var traitsStr by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Servant") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Servant Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            OutlinedTextField(
                value = iconUrl,
                onValueChange = { iconUrl = it },
                label = { Text("Icon Image URL (e.g. from FGO Wiki)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = noblePhantasm,
                onValueChange = { noblePhantasm = it },
                label = { Text("Noble Phantasm") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = skills,
                onValueChange = { skills = it },
                label = { Text("Skills (e.g. Charisma A, etc)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Text("Effectiveness (comma separated)", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = attributesStr,
                onValueChange = { attributesStr = it },
                label = { Text("Attributes (e.g. Earth, Sky)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = alignmentsStr,
                onValueChange = { alignmentsStr = it },
                label = { Text("Alignments (e.g. Lawful, Good)") },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = gendersStr,
                onValueChange = { gendersStr = it },
                label = { Text("Gender (e.g. Female)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = traitsStr,
                onValueChange = { traitsStr = it },
                label = { Text("Traits (e.g. Dragon, Roman, King)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val servant = Servant(
                        name = name,
                        iconUrl = iconUrl,
                        skills = skills,
                        noblePhantasm = noblePhantasm,
                        effectiveAttributes = attributesStr.split(",").map{it.trim()}.filter{it.isNotBlank()},
                        effectiveAlignments = alignmentsStr.split(",").map{it.trim()}.filter{it.isNotBlank()},
                        effectiveGenders = gendersStr.split(",").map{it.trim()}.filter{it.isNotBlank()},
                        effectiveTraits = traitsStr.split(",").map{it.trim()}.filter{it.isNotBlank()}
                    )
                    viewModel.addServant(servant)
                    onBack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank()
            ) {
                Text("Save Servant")
            }
        }
    }
}
