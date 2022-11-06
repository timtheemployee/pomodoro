package presentation

import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TimerViewModel {

    private val scope = CoroutineScope(Dispatchers.IO)
    private var milliseconds = ROUND_TIME
    private var rounds = 0
    private var goals = 0
    private var isPaused = true
    private var countDownJob: Job? = null

    private val _timer = MutableStateFlow("")
    val timer: StateFlow<String> = _timer

    fun onSkipClicked() {
        milliseconds = ROUND_TIME
        _timer.value = getFormattedTime()
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
        milliseconds -= ONE_SECOND
        _timer.value = getFormattedTime()
    }

    private fun getFormattedTime(): String =
        String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(milliseconds),
            TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds))
        )

    private companion object {
        const val ROUND_TIME = 900000L
        const val ONE_SECOND = 1000L
    }
}