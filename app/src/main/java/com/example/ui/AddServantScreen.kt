package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.NoblePhantasm
import com.example.data.Servant
import com.example.data.Skill

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddServantScreen(
    viewModel: ServantViewModel,
    onBack: () -> Unit
) {
    val traits by viewModel.traits.collectAsStateWithLifecycle()
    val alignments by viewModel.alignments.collectAsStateWithLifecycle()

    var name by remember { mutableStateOf("") }
    var iconUrl by remember { mutableStateOf("") }
    var rarity by remember { mutableStateOf("C") }
    var servantClass by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var attribute by remember { mutableStateOf("") }
    
    val selectedTraits = remember { mutableStateListOf<String>() }
    val selectedAlignments = remember { mutableStateListOf<String>() }

    var npName by remember { mutableStateOf("") }
    var npDesc by remember { mutableStateOf("") }
    var npBonusGender by remember { mutableStateOf("") }
    val npBonusAttr = remember { mutableStateListOf<String>() }
    var npBonusClass by remember { mutableStateOf("") }
    val npBonusAlign = remember { mutableStateListOf<String>() }
    val npBonusTraits = remember { mutableStateListOf<String>() }

    val skills = remember { mutableStateListOf<SkillInput>() }

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
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Basic Info", style = MaterialTheme.typography.titleLarge)
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Servant Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = iconUrl, onValueChange = { iconUrl = it }, label = { Text("Icon URL") }, modifier = Modifier.fillMaxWidth())
            SingleSelectDropdown(label = "Rarity", options = listOf("C", "U", "R", "SR", "SSR"), selected = rarity, onSelect = { rarity = it })
            OutlinedTextField(value = servantClass, onValueChange = { servantClass = it }, label = { Text("Class") }, modifier = Modifier.fillMaxWidth())
            
            SingleSelectDropdown(label = "Gender", options = listOf("Male", "Female", ""), selected = gender, onSelect = { gender = it })
            SingleSelectDropdown(label = "Attribute", options = listOf("Earth", "Man", "Sky", ""), selected = attribute, onSelect = { attribute = it })

            Divider()
            ChecklistSection("Traits", traits.map { it.name }, selectedTraits, onAdd = { viewModel.addTrait(it) })
            Spacer(modifier = Modifier.height(8.dp))
            ChecklistSection("Alignments", alignments.map { it.name }, selectedAlignments, onAdd = { viewModel.addAlignment(it) })

            HorizontalDivider()
            Text("Noble Phantasm", style = MaterialTheme.typography.titleLarge)
            OutlinedTextField(value = npName, onValueChange = { npName = it }, label = { Text("NP Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = npDesc, onValueChange = { npDesc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
            Text("NP Bonus Damage Targets", style = MaterialTheme.typography.titleSmall)
            SingleSelectDropdown(label = "Bonus Gender", options = listOf("Male", "Female", ""), selected = npBonusGender, onSelect = { npBonusGender = it })
            MultiSelectDropdown(label = "Bonus Attributes", options = listOf("Earth", "Man", "Sky"), selectedOptions = npBonusAttr)
            OutlinedTextField(value = npBonusClass, onValueChange = { npBonusClass = it }, label = { Text("Bonus Class (comma sep)") }, modifier = Modifier.fillMaxWidth())
            
            ChecklistSection("Bonus Alignments", alignments.map { it.name }, npBonusAlign, onAdd = { viewModel.addAlignment(it) })
            ChecklistSection("Bonus Traits", traits.map { it.name }, npBonusTraits, onAdd = { viewModel.addTrait(it) })

            HorizontalDivider()
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Skills (${skills.size}/3)", style = MaterialTheme.typography.titleLarge)
                if (skills.size < 3) {
                    IconButton(onClick = { skills.add(SkillInput()) }) {
                        Icon(Icons.Filled.Add, "Add Skill")
                    }
                }
            }
            skills.forEachIndexed { i, skill ->
                SkillEditor(
                    index = i, skill = skill, onRemove = { skills.removeAt(i) },
                    traits = traits.map { it.name }, alignments = alignments.map { it.name },
                    onAddTrait = { viewModel.addTrait(it) }, onAddAlignment = { viewModel.addAlignment(it) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val np = NoblePhantasm(
                        name = npName, description = npDesc,
                        effectiveAttributes = npBonusAttr.toList(),
                        effectiveGenders = listOf(npBonusGender).filter { it.isNotBlank() },
                        effectiveClasses = npBonusClass.split(",").map{it.trim()}.filter{it.isNotBlank()},
                        effectiveAlignments = npBonusAlign.toList(),
                        effectiveTraits = npBonusTraits.toList()
                    )
                    val sList = skills.map { s ->
                        Skill(
                            name = s.name, description = s.desc,
                            effectiveAttributes = s.bonusAttr.toList(),
                            effectiveGenders = listOf(s.bonusGender).filter { it.isNotBlank() },
                            effectiveClasses = s.bonusClass.split(",").map{it.trim()}.filter{it.isNotBlank()},
                            effectiveAlignments = s.bonusAlign.toList(),
                            effectiveTraits = s.bonusTraits.toList()
                        )
                    }
                    val servant = Servant(
                        name = name, iconUrl = iconUrl, rarity = rarity, servantClass = servantClass,
                        gender = gender, attribute = attribute,
                        traits = selectedTraits, alignments = selectedAlignments,
                        noblePhantasm = np, skills = sList
                    )
                    viewModel.addServant(servant)
                    onBack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank()
            ) {
                Text("Save Servant")
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

class SkillInput {
    var name by mutableStateOf("")
    var desc by mutableStateOf("")
    var bonusGender by mutableStateOf("")
    val bonusAttr = mutableStateListOf<String>()
    var bonusClass by mutableStateOf("")
    val bonusAlign = mutableStateListOf<String>()
    val bonusTraits = mutableStateListOf<String>()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleSelectDropdown(label: String, options: List<String>, selected: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(opt.ifBlank { "None" }) },
                    onClick = {
                        onSelect(opt)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MultiSelectDropdown(
    label: String,
    options: List<String>,
    selectedOptions: MutableList<String>
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOptions.joinToString(", "),
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { opt ->
                val isSelected = selectedOptions.contains(opt)
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = isSelected, onCheckedChange = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(opt)
                        }
                    },
                    onClick = {
                        if (isSelected) selectedOptions.remove(opt) else selectedOptions.add(opt)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistSection(title: String, items: List<String>, selectedItems: MutableList<String>, onAdd: (String) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        var newItem by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add new to $title") },
            text = {
                OutlinedTextField(
                    value = newItem,
                    onValueChange = { newItem = it },
                    label = { Text("Name") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newItem.isNotBlank()) onAdd(newItem)
                    showAddDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
            }
        )
    }

    Text(title, style = MaterialTheme.typography.titleMedium)
    Row(verticalAlignment = Alignment.CenterVertically) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it; expanded = true },
                label = { Text("Search $title") },
                modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryEditable, true),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                singleLine = true
            )
            
            val filteredItems = items.filter { it.contains(searchQuery, ignoreCase = true) && !selectedItems.contains(it) }
            if (filteredItems.isNotEmpty() && expanded) {
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    filteredItems.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                selectedItems.add(item)
                                searchQuery = ""
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = { showAddDialog = true }) { Text("Add") }
    }
    
    @OptIn(ExperimentalLayoutApi::class)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        selectedItems.forEach { item ->
            FilterChip(
                selected = true,
                onClick = { selectedItems.remove(item) },
                label = { Text(item) }
            )
        }
    }
}

@Composable
fun SkillEditor(
    index: Int, skill: SkillInput, onRemove: () -> Unit,
    traits: List<String>, alignments: List<String>,
    onAddTrait: (String) -> Unit, onAddAlignment: (String) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Skill ${index + 1}", fontWeight = FontWeight.Bold)
                IconButton(onClick = onRemove) { Icon(Icons.Filled.Delete, "Remove") }
            }
            OutlinedTextField(value = skill.name, onValueChange = { skill.name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = skill.desc, onValueChange = { skill.desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
            SingleSelectDropdown(label = "Bonus Gender", options = listOf("Male", "Female", ""), selected = skill.bonusGender, onSelect = { skill.bonusGender = it })
            MultiSelectDropdown(label = "Bonus Attributes", options = listOf("Earth", "Man", "Sky"), selectedOptions = skill.bonusAttr)
            OutlinedTextField(value = skill.bonusClass, onValueChange = { skill.bonusClass = it }, label = { Text("Bonus Class (comma sep)") }, modifier = Modifier.fillMaxWidth())
            ChecklistSection("Bonus Alignments", alignments, skill.bonusAlign, onAdd = onAddAlignment)
            ChecklistSection("Bonus Traits", traits, skill.bonusTraits, onAdd = onAddTrait)
        }
    }
}
