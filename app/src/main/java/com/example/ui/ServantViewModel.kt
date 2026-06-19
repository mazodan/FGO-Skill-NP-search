package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AlignmentEntity
import com.example.data.Servant
import com.example.data.ServantRepository
import com.example.data.TraitEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SearchFilters(
    val query: String = "",
    val targetGender: String = "",
    val targetAttribute: String = "",
    val targetClass: String = "",
    val targetAlignment: List<String> = emptyList(),
    val targetTrait: List<String> = emptyList(),
    val isOrConditional: Boolean = false,
    val sortByRarity: Boolean = false
)

class ServantViewModel(private val repository: ServantRepository) : ViewModel() {

    val searchFilters = MutableStateFlow(SearchFilters())

    val traits: StateFlow<List<TraitEntity>> = repository.allTraits.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val alignments: StateFlow<List<AlignmentEntity>> = repository.allAlignments.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val servants: StateFlow<List<Servant>> = repository.allServants
        .combine(searchFilters) { list, filters ->
            if (filters.query.isBlank() && filters.targetGender.isBlank() && filters.targetAttribute.isBlank() && filters.targetClass.isBlank() && filters.targetAlignment.isEmpty() && filters.targetTrait.isEmpty() && !filters.sortByRarity) {
                return@combine emptyList()
            }
            
            var filteredList = list
            if (filters.query.isNotBlank()) {
                val q = filters.query.lowercase()
                filteredList = filteredList.filter { servant ->
                    servant.name.lowercase().contains(q) ||
                    servant.traits.any { it.lowercase().contains(q) } ||
                    servant.alignments.any { it.lowercase().contains(q) } ||
                    servant.gender.lowercase().contains(q) ||
                    servant.attribute.lowercase().contains(q) ||
                    servant.servantClass.lowercase().contains(q) ||
                    matchSearch(servant, q)
                }
            }
            val hasBonusFilters = filters.targetGender.isNotBlank() || filters.targetAttribute.isNotBlank() || filters.targetClass.isNotBlank() || filters.targetAlignment.isNotEmpty() || filters.targetTrait.isNotEmpty()
            
            if (hasBonusFilters) {
                filteredList = filteredList.filter { servant ->
                    val conditions = mutableListOf<Boolean>()
                    
                    if (filters.targetGender.isNotBlank()) {
                        val q = filters.targetGender.lowercase()
                        conditions.add(
                            servant.noblePhantasm.effectiveGenders.any { it.lowercase() == q } ||
                            servant.skills.any { skill -> skill.effectiveGenders.any { it.lowercase() == q } }
                        )
                    }
                    if (filters.targetAttribute.isNotBlank()) {
                        val q = filters.targetAttribute.lowercase()
                        conditions.add(
                            servant.noblePhantasm.effectiveAttributes.any { it.lowercase() == q } ||
                            servant.skills.any { skill -> skill.effectiveAttributes.any { it.lowercase() == q } }
                        )
                    }
                    if (filters.targetClass.isNotBlank()) {
                        val q = filters.targetClass.lowercase()
                        conditions.add(
                            servant.noblePhantasm.effectiveClasses.any { it.lowercase() == q } ||
                            servant.skills.any { skill -> skill.effectiveClasses.any { it.lowercase() == q } }
                        )
                    }
                    if (filters.targetAlignment.isNotEmpty()) {
                        conditions.add(
                            filters.targetAlignment.any { q ->
                                val lowerQ = q.lowercase()
                                servant.noblePhantasm.effectiveAlignments.any { it.lowercase() == lowerQ } ||
                                servant.skills.any { skill -> skill.effectiveAlignments.any { it.lowercase() == lowerQ } }
                            }
                        )
                    }
                    if (filters.targetTrait.isNotEmpty()) {
                        conditions.add(
                            filters.targetTrait.any { q ->
                                val lowerQ = q.lowercase()
                                servant.noblePhantasm.effectiveTraits.any { it.lowercase() == lowerQ } ||
                                servant.skills.any { skill -> skill.effectiveTraits.any { it.lowercase() == lowerQ } }
                            }
                        )
                    }
                    
                    if (filters.isOrConditional) {
                        conditions.any { it }
                    } else {
                        conditions.all { it }
                    }
                }
            }
            if (filters.sortByRarity) {
                val rarityValues = mapOf("SSR" to 5, "SR" to 4, "R" to 3, "U" to 2, "C" to 1)
                filteredList = filteredList.sortedByDescending { rarityValues[it.rarity] ?: 0 }
            }
            filteredList
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private fun matchSearch(servant: Servant, query: String): Boolean {

        // Search NP
        val np = servant.noblePhantasm
        if (np.effectiveTraits.any { it.lowercase().contains(query) }) return true
        if (np.effectiveAlignments.any { it.lowercase().contains(query) }) return true
        if (np.effectiveAttributes.any { it.lowercase().contains(query) }) return true
        if (np.effectiveClasses.any { it.lowercase().contains(query) }) return true
        if (np.effectiveGenders.any { it.lowercase().contains(query) }) return true
        if (np.description.lowercase().contains(query)) return true

        // Search skills
        return servant.skills.any { skill ->
            skill.effectiveTraits.any { it.lowercase().contains(query) } ||
            skill.effectiveAlignments.any { it.lowercase().contains(query) } ||
            skill.effectiveAttributes.any { it.lowercase().contains(query) } ||
            skill.effectiveClasses.any { it.lowercase().contains(query) } ||
            skill.effectiveGenders.any { it.lowercase().contains(query) } ||
            skill.description.lowercase().contains(query)
        }
    }

    fun updateSearchFilters(filters: SearchFilters) {
        searchFilters.value = filters
    }

    fun addServant(servant: Servant) {
        viewModelScope.launch {
            repository.insert(servant)
        }
    }

    fun deleteServant(id: Int) {
        viewModelScope.launch {
            repository.delete(id)
        }
    }

    fun addTrait(name: String) {
        viewModelScope.launch {
            repository.insertTrait(TraitEntity(name = name.trim()))
        }
    }

    fun updateTrait(id: Int, name: String) {
        viewModelScope.launch {
            repository.updateTrait(TraitEntity(id = id, name = name.trim()))
        }
    }

    fun deleteTrait(id: Int) {
        viewModelScope.launch {
            repository.deleteTrait(id)
        }
    }

    fun addAlignment(name: String) {
        viewModelScope.launch {
            repository.insertAlignment(AlignmentEntity(name = name.trim()))
        }
    }

    fun updateAlignment(id: Int, name: String) {
        viewModelScope.launch {
            repository.updateAlignment(AlignmentEntity(id = id, name = name.trim()))
        }
    }

    fun deleteAlignment(id: Int) {
        viewModelScope.launch {
            repository.deleteAlignment(id)
        }
    }
}

class ServantViewModelFactory(private val repository: ServantRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ServantViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ServantViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
