package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Entity(tableName = "servants")
@TypeConverters(StringListConverter::class)
data class Servant(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val iconUrl: String, // Can be from web or content URI
    val skills: String, // E.g., multiple skills listed with newlines
    val noblePhantasm: String,
    val effectiveAttributes: List<String>,
    val effectiveAlignments: List<String>,
    val effectiveGenders: List<String>,
    val effectiveTraits: List<String>
)

class StringListConverter {
    @TypeConverter
    fun fromString(value: String): List<String> {
        if (value.isBlank()) return emptyList()
        return value.split(",").map { it.trim() }
    }

    @TypeConverter
    fun toString(list: List<String>): String {
        return list.joinToString(",")
    }
}
