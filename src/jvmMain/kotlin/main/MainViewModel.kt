package main

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
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

    var notification = MutableStateFlow(false)
        private set

    var navigation = MutableStateFlow(Navigation.TASK_LIST)
        private set

    init {
        sharedRepository.notification
            .onEach { notification.value = it }
            .launchIn(scope)

        sharedRepository.navigation
            .onEach { navigation.value = it }
            .launchIn(scope)

        scope.launch {
            sharedRepository.setDefaultTimer()
        }
    }

    fun obtainKeyComboKey(keyCombo: KeyCombo?) {
        scope.launch { sharedRepository.setKeyCombo(keyCombo) }
    }

    fun requestFocus() {
        scope.launch { sharedRepository.setNotification(false) }
    }
}