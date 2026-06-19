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

class ServantViewModel(private val repository: ServantRepository) : ViewModel() {

    val searchQuery = MutableStateFlow("")

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
        .combine(searchQuery) { list, query ->
            if (query.isBlank()) {
                list
            } else {
                val q = query.lowercase()
                list.filter { servant ->
                    servant.name.lowercase().contains(q) ||
                    servant.traits.any { it.lowercase().contains(q) } ||
                    servant.alignments.any { it.lowercase().contains(q) } ||
                    servant.gender.lowercase().contains(q) ||
                    servant.attribute.lowercase().contains(q) ||
                    servant.servantClass.lowercase().contains(q) ||
                    matchSearch(servant, q)
                }
            }
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

    fun onSearchQueryChanged(newQuery: String) {
        searchQuery.value = newQuery
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
