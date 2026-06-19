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
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ServantViewModel,
    onAddClick: () -> Unit
) {
    val servants by viewModel.servants.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = HighDensityBg,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = FabBg,
                contentColor = FabText,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Servant")
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
                    value = searchQuery,
                    onValueChange = viewModel::onSearchQueryChanged,
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = HighDensityText),
                    decorationBox = { innerTextField ->
                        if (searchQuery.isEmpty()) {
                            Text("Search Traits: Dragon, Evil...", color = FilterUnselectedText, fontSize = 14.sp)
                        }
                        innerTextField()
                    }
                )
            }

            // Quick Filters
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { FilterChipUI(text = "Attribute", isSelected = true) }
                item { FilterChipUI(text = "Alignment", isSelected = true) }
                item { FilterChipUI(text = "Gender", isSelected = false) }
                item { FilterChipUI(text = "Traits", isSelected = false) }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Content: Servant List
            if (servants.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (searchQuery.isNotEmpty()) "No servants found." else "No servants defined yet.",
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
