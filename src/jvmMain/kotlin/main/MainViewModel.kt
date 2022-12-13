package main

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import shared.data.SharedRepository
import shared.domain.KeyCombo
import shared.domain.Navigation

class MainViewModel(
    private val scope: CoroutineScope,
    private val sharedRepository: SharedRepository
) {

    private val _notification = MutableStateFlow(false)
    val notification: StateFlow<Boolean> = _notification

    private val _navigation = MutableStateFlow(Navigation.TASK_LIST)
    val navigation: StateFlow<Navigation> = _navigation

    init {
        sharedRepository.notification
            .onEach { _notification.value = it }
            .launchIn(scope)

        sharedRepository.navigation
            .onEach { _navigation.value = it }
            .launchIn(scope)

        scope.launch {
            sharedRepository.setDefaultTimer()
        }
    }

    fun createNewTask() {
        scope.launch {
            sharedRepository.setKeyCombo(KeyCombo.ADD_NEW_TASK)
        }
    }

    fun makeFirstTaskCompeted() {
        scope.launch {
            sharedRepository.setKeyCombo(KeyCombo.DONE_FIRST)
        }
    }

    fun requestFocus() {
        scope.launch { sharedRepository.setNotification(false) }
    }
}