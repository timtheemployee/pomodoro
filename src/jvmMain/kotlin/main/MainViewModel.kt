package main

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import shared.data.SharedRepository
import shared.domain.KeyCombo
import tasks.domain.Task

class MainViewModel(
    private val scope: CoroutineScope,
    private val sharedRepository: SharedRepository
) {

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
}