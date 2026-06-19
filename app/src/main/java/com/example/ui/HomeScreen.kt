package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.Servant
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.ui.theme.*

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ServantViewModel,
    onAddClick: () -> Unit
) {
    val servants by viewModel.servants.collectAsStateWithLifecycle()
    val searchFilters by viewModel.searchFilters.collectAsStateWithLifecycle()
    val traits by viewModel.traits.collectAsStateWithLifecycle()
    val alignments by viewModel.alignments.collectAsStateWithLifecycle()

    var showFilters by remember { mutableStateOf(false) }
    var showTraitsDbDialog by remember { mutableStateOf(false) }
    var showAlignmentsDbDialog by remember { mutableStateOf(false) }

    if (showTraitsDbDialog) {
        DatabaseManagerDialog(
            title = "Traits Database",
            items = traits.map { Pair(it.id, it.name) },
            onAdd = { viewModel.addTrait(it) },
            onUpdate = { id, name -> viewModel.updateTrait(id, name) },
            onDelete = { viewModel.deleteTrait(it) },
            onDismiss = { showTraitsDbDialog = false }
        )
    }

    if (showAlignmentsDbDialog) {
        DatabaseManagerDialog(
            title = "Alignments Database",
            items = alignments.map { Pair(it.id, it.name) },
            onAdd = { viewModel.addAlignment(it) },
            onUpdate = { id, name -> viewModel.updateAlignment(id, name) },
            onDelete = { viewModel.deleteAlignment(it) },
            onDismiss = { showAlignmentsDbDialog = false }
        )
    }

    Scaffold(
        containerColor = HighDensityBg,
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SmallFloatingActionButton(
                    onClick = { showTraitsDbDialog = true },
                    containerColor = FilterSelectedBg,
                    contentColor = FilterSelectedText
                ) {
                    Icon(Icons.Filled.Star, contentDescription = "Traits Database")
                }
                SmallFloatingActionButton(
                    onClick = { showAlignmentsDbDialog = true },
                    containerColor = FilterSelectedBg,
                    contentColor = FilterSelectedText
                ) {
                    Icon(Icons.Filled.Build, contentDescription = "Alignments Database")
                }
                FloatingActionButton(
                    onClick = onAddClick,
                    containerColor = FabBg,
                    contentColor = FabText,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Servant")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header with Search Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp)
                    .height(48.dp)
                    .background(Color.White, CircleShape)
                    .border(1.dp, SearchBorder, CircleShape)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search",
                    tint = FilterUnselectedText,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                BasicTextField(
                    value = searchFilters.query,
                    onValueChange = { viewModel.updateSearchFilters(searchFilters.copy(query = it)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = HighDensityText),
                    decorationBox = { innerTextField ->
                        if (searchFilters.query.isEmpty()) {
                            Text("Search Traits: Dragon, Evil...", color = FilterUnselectedText, fontSize = 14.sp)
                        }
                        innerTextField()
                    }
                )
                if (searchFilters != com.example.ui.SearchFilters()) {
                    IconButton(onClick = { viewModel.updateSearchFilters(com.example.ui.SearchFilters()) }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Clear all", tint = FilterUnselectedText)
                    }
                }
                IconButton(onClick = { showFilters = !showFilters }) {
                    Icon(Icons.Filled.List, contentDescription = "Advanced Filters", tint = if (showFilters) FilterSelectedBg else FilterUnselectedText)
                }
            }

            // Quick Filters
            if (showFilters) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Bonus Damage Targets", style = MaterialTheme.typography.labelMedium, color = FilterUnselectedText)
                        if (searchFilters != com.example.ui.SearchFilters()) {
                            TextButton(
                                onClick = { viewModel.updateSearchFilters(com.example.ui.SearchFilters()) },
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.height(24.dp)
                            ) {
                                Text("Clear All", fontSize = 12.sp)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterDropdown(
                            label = "Attribute",
                            options = listOf("Earth", "Man", "Sky"),
                            selected = searchFilters.targetAttribute,
                            onSelect = { viewModel.updateSearchFilters(searchFilters.copy(targetAttribute = it)) }
                        )
                        MultiFilterDropdown(
                            label = "Alignment",
                            options = alignments.map { it.name },
                            selected = searchFilters.targetAlignment,
                            searchable = true,
                            onToggle = { opt -> 
                                val newList = if (searchFilters.targetAlignment.contains(opt)) {
                                    searchFilters.targetAlignment - opt
                                } else {
                                    searchFilters.targetAlignment + opt
                                }
                                viewModel.updateSearchFilters(searchFilters.copy(targetAlignment = newList)) 
                            },
                            onClear = { viewModel.updateSearchFilters(searchFilters.copy(targetAlignment = emptyList())) }
                        )
                        FilterDropdown(
                            label = "Gender",
                            options = listOf("Male", "Female"),
                            selected = searchFilters.targetGender,
                            onSelect = { viewModel.updateSearchFilters(searchFilters.copy(targetGender = it)) }
                        )
                        MultiFilterDropdown(
                            label = "Traits",
                            options = traits.map { it.name },
                            selected = searchFilters.targetTrait,
                            searchable = true,
                            onToggle = { opt ->
                                val newList = if (searchFilters.targetTrait.contains(opt)) {
                                    searchFilters.targetTrait - opt
                                } else {
                                    searchFilters.targetTrait + opt
                                }
                                viewModel.updateSearchFilters(searchFilters.copy(targetTrait = newList))
                            },
                            onClear = { viewModel.updateSearchFilters(searchFilters.copy(targetTrait = emptyList())) }
                        )
                        // Simple text input for Class so it doesn't need to read all class values
                        FilterDropdown(
                            label = "Class",
                            options = listOf("Saber", "Archer", "Lancer", "Rider", "Caster", "Assassin", "Berserker", "Ruler", "Avenger", "Alter Ego", "Moon Cancer", "Foreigner", "Pretender", "Beast"),
                            selected = searchFilters.targetClass,
                            onSelect = { viewModel.updateSearchFilters(searchFilters.copy(targetClass = it)) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Content: Servant List
            if (servants.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (searchFilters.query.isNotEmpty() || showFilters) "No servants found matching criteria." else "No servants defined yet.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = FilterUnselectedText
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(servants, key = { it.id }) { servant ->
                        ServantCard(
                            servant = servant,
                            onDelete = { viewModel.deleteServant(servant.id) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDropdown(
    label: String,
    options: List<String>,
    selected: String,
    searchable: Boolean = false,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = if (selected.isNotEmpty()) FilterSelectedBg else Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    width = 1.dp,
                    color = if (selected.isNotEmpty()) FilterSelectedBg else FilterUnselectedBorder,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                .clickable { expanded = true }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (selected.isNotEmpty()) "$label: $selected" else label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (selected.isNotEmpty()) FilterSelectedText else FilterUnselectedText
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "▼", fontSize = 10.sp, color = if (selected.isNotEmpty()) FilterSelectedText else FilterUnselectedText)
            }
        }
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                searchQuery = ""
            },
            modifier = Modifier.exposedDropdownSize(matchTextFieldWidth = false).widthIn(min = 200.dp)
        ) {
            if (searchable) {
                Box(modifier = Modifier.padding(8.dp)) {
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text("Search...", color = Color.Gray)
                            }
                            innerTextField()
                        }
                    )
                }
            }
            
            DropdownMenuItem(
                text = { Text("Any (Clear)") },
                onClick = {
                    onSelect("")
                    expanded = false
                    searchQuery = ""
                }
            )
            
            val filteredOptions = if (searchable && searchQuery.isNotBlank()) {
                options.filter { it.contains(searchQuery, ignoreCase = true) }
            } else {
                options
            }
            
            filteredOptions.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(opt) },
                    onClick = {
                        onSelect(opt)
                        expanded = false
                        searchQuery = ""
                    }
                )
            }
        }
    }
}

@Composable
fun FilterChipUI(text: String, isSelected: Boolean) {
    Box(
        modifier = Modifier
            .background(
                color = if (isSelected) FilterSelectedBg else Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = if (isSelected) FilterSelectedBg else FilterUnselectedBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) FilterSelectedText else FilterUnselectedText
            )
            if (isSelected) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "▼", fontSize = 10.sp, color = FilterSelectedText)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiFilterDropdown(
    label: String,
    options: List<String>,
    selected: List<String>,
    searchable: Boolean = false,
    onToggle: (String) -> Unit,
    onClear: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val displayValue = if (selected.isNotEmpty()) selected.joinToString(", ") else ""
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = if (selected.isNotEmpty()) FilterSelectedBg else Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    width = 1.dp,
                    color = if (selected.isNotEmpty()) FilterSelectedBg else FilterUnselectedBorder,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                .clickable { expanded = true }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (selected.isNotEmpty()) "$label: $displayValue" else label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (selected.isNotEmpty()) FilterSelectedText else FilterUnselectedText,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "▼", fontSize = 10.sp, color = if (selected.isNotEmpty()) FilterSelectedText else FilterUnselectedText)
            }
        }
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                searchQuery = ""
            },
            modifier = Modifier.exposedDropdownSize(matchTextFieldWidth = false).widthIn(min = 240.dp)
        ) {
            if (searchable) {
                Box(modifier = Modifier.padding(8.dp)) {
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text("Search...", color = Color.Gray)
                            }
                            innerTextField()
                        }
                    )
                }
            }
            
            DropdownMenuItem(
                text = { Text("Any (Clear)") },
                onClick = {
                    onClear()
                    expanded = false
                    searchQuery = ""
                }
            )
            
            val filteredOptions = if (searchable && searchQuery.isNotBlank()) {
                options.filter { it.contains(searchQuery, ignoreCase = true) }
            } else {
                options
            }
            
            filteredOptions.forEach { opt ->
                val isSelected = selected.contains(opt)
                DropdownMenuItem(
                    text = { 
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = isSelected, onCheckedChange = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(opt)
                        }
                    },
                    onClick = {
                        onToggle(opt)
                        searchQuery = ""
                    }
                )
            }
        }
    }
}

@Composable
fun ServantCard(servant: Servant, onDelete: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            // Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(IconBgBlue, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (servant.iconUrl.isNotBlank()) {
                    AsyncImage(
                        model = servant.iconUrl,
                        contentDescription = "Servant Icon",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = "SSR",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Top-right pip
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-4).dp)
                        .size(20.dp)
                        .background(Color(0xFFFFD700), CircleShape)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "S", fontSize = 8.sp, color = Color.Black)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = servant.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = HighDensityText,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(ClassTagBg, CircleShape)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "CLASS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ClassTagText
                        )
                    }
                }
                
                val alignText = buildString {
                    val attrs = servant.attribute
                    val align = servant.alignments.joinToString(", ")
                    val gen = servant.gender
                    
                    if (attrs.isNotBlank()) append(attrs)
                    if (align.isNotBlank()) { if (isNotEmpty()) append(" • "); append(align) }
                    if (gen.isNotBlank()) { if (isNotEmpty()) append(" • "); append(gen) }
                }.takeIf { it.isNotBlank() } ?: "Unknown • Unknown"

                Text(
                    text = alignText,
                    fontSize = 11.sp,
                    color = FilterUnselectedText,
                    modifier = Modifier.padding(top = 2.dp)
                )

                @OptIn(ExperimentalLayoutApi::class)
                FlowRow(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val traits = servant.traits
                    traits.forEachIndexed { index, trait ->
                        val bg = if (index % 3 == 2) TagRedBg else TagBlueBg
                        val fg = if (index % 3 == 2) TagRedText else TagBlueText
                        TagItem(text = trait, bg = bg, fg = fg)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "NP: ${servant.noblePhantasm.name}", fontSize = 12.sp, color = HighDensityText)
                val skillText = servant.skills.joinToString(", ") { it.name }
                Text(text = "Skills: $skillText", fontSize = 11.sp, color = FilterUnselectedText, maxLines = 1)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "DELETE",
                fontSize = 11.sp,
                color = TagRedText,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onDelete() }.padding(8.dp)
            )
        }
    }
}

@Composable
fun TagItem(text: String, bg: Color, fg: Color) {
    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = text.uppercase(),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = fg
        )
    }
}

@Composable
fun DatabaseManagerDialog(
    title: String,
    items: List<Pair<Int, String>>,
    onAdd: (String) -> Unit,
    onUpdate: (Int, String) -> Unit,
    onDelete: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var newItemName by remember { mutableStateOf("") }
    var editingItemId by remember { mutableStateOf<Int?>(null) }
    var editingItemName by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newItemName,
                        onValueChange = { newItemName = it },
                        label = { Text("New Item") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        if (newItemName.isNotBlank()) {
                            onAdd(newItemName)
                            newItemName = ""
                        }
                    }) {
                        Text("Add")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 240.dp)) {
                    items(items, key = { it.first }) { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (editingItemId == item.first) {
                                OutlinedTextField(
                                    value = editingItemName,
                                    onValueChange = { editingItemName = it },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                IconButton(onClick = {
                                    if (editingItemName.isNotBlank()) {
                                        onUpdate(item.first, editingItemName)
                                        editingItemId = null
                                    }
                                }) {
                                    Icon(Icons.Filled.Check, contentDescription = "Save", tint = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(onClick = { editingItemId = null }) {
                                    Icon(Icons.Filled.Clear, contentDescription = "Cancel", tint = MaterialTheme.colorScheme.error)
                                }
                            } else {
                                Text(text = item.second, modifier = Modifier.weight(1f))
                                IconButton(onClick = {
                                    editingItemId = item.first
                                    editingItemName = item.second
                                }) {
                                    Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(onClick = { onDelete(item.first) }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = TagRedText)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
