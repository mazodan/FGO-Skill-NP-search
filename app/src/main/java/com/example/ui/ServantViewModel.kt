package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Servant
import com.example.data.ServantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ServantViewModel(private val repository: ServantRepository) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    val servants: StateFlow<List<Servant>> = repository.allServants
        .combine(searchQuery) { list, query ->
            if (query.isBlank()) {
                list
            } else {
                val q = query.lowercase()
                list.filter { servant ->
                    servant.name.lowercase().contains(q) ||
                    servant.effectiveAttributes.any { it.lowercase().contains(q) } ||
                    servant.effectiveAlignments.any { it.lowercase().contains(q) } ||
                    servant.effectiveGenders.any { it.lowercase().contains(q) } ||
                    servant.effectiveTraits.any { it.lowercase().contains(q) }
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

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
