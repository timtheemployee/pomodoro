package presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.suspendCoroutine

class TimerViewModel {

    private val scope = CoroutineScope(Dispatchers.IO)
    private var seconds = ROUND_TIME
    private var rounds = 0
    private var goals = 0
    private var isPaused = true
    private var countDownJob: Job? = null

    private val _timer = MutableStateFlow("")
    val timer: StateFlow<String> = _timer

    fun onSkipClicked() {
        isPaused = true
        countDownJob?.cancel()
    }

    fun onActionClicked() {
        if (isPaused) {
            isPaused = false
            countDownJob = scope.launch {
                while (isActive) {
                    tick()
                }
            }
        } else {
            isPaused = true
            countDownJob?.cancel()
        }
    }

    private suspend fun tick() {
        delay(ONE_SECOND)
        seconds -= ONE_SECOND
        _timer.value = "Seconds $seconds"
    }

    private companion object {
        const val ROUND_TIME = 900000L
        const val ONE_SECOND = 1000L
    }
}