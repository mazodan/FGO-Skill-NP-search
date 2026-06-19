package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@Entity(tableName = "traits")
data class TraitEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)

@Entity(tableName = "alignments")
data class AlignmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)

data class NoblePhantasm(
    val name: String,
    val description: String,
    val effectiveAttributes: List<String>,
    val effectiveAlignments: List<String>,
    val effectiveGenders: List<String>,
    val effectiveTraits: List<String>,
    val effectiveClasses: List<String>
)

data class Skill(
    val name: String,
    val description: String,
    val effectiveAttributes: List<String>,
    val effectiveAlignments: List<String>,
    val effectiveGenders: List<String>,
    val effectiveTraits: List<String>,
    val effectiveClasses: List<String>
)

@Entity(tableName = "servants")
@TypeConverters(Converters::class)
data class Servant(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val iconUrl: String,
    val rarity: String = "C",
    
    val servantClass: String = "Unknown",
    val attribute: String = "",
    val gender: String = "",
    val alignments: List<String> = emptyList(),
    val traits: List<String> = emptyList(),
    
    val noblePhantasm: NoblePhantasm,
    val skills: List<Skill>
)

class Converters {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @TypeConverter
    fun fromStringList(value: String): List<String> {
        val type = Types.newParameterizedType(List::class.java, String::class.java)
        val adapter = moshi.adapter<List<String>>(type)
        return adapter.fromJson(value) ?: emptyList()
    }

    @TypeConverter
    fun toStringList(list: List<String>): String {
        val type = Types.newParameterizedType(List::class.java, String::class.java)
        val adapter = moshi.adapter<List<String>>(type)
        return adapter.toJson(list)
    }

    @TypeConverter
    fun fromNP(value: String): NoblePhantasm? {
        return moshi.adapter(NoblePhantasm::class.java).fromJson(value)
    }

    @TypeConverter
    fun toNP(np: NoblePhantasm): String {
        return moshi.adapter(NoblePhantasm::class.java).toJson(np)
    }

    @TypeConverter
    fun fromSkillList(value: String): List<Skill> {
        val type = Types.newParameterizedType(List::class.java, Skill::class.java)
        val adapter = moshi.adapter<List<Skill>>(type)
        return adapter.fromJson(value) ?: emptyList()
    }

    @TypeConverter
    fun toSkillList(list: List<Skill>): String {
        val type = Types.newParameterizedType(List::class.java, Skill::class.java)
        val adapter = moshi.adapter<List<Skill>>(type)
        return adapter.toJson(list)
    }
}
