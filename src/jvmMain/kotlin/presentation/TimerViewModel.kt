package presentation

import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TimerViewModel {

    private val scope = CoroutineScope(Dispatchers.IO)
    private var milliseconds = ROUND_TIME
    private var isPaused = true
    private var countDownJob: Job? = null

    private val _timer = MutableStateFlow("")
    val timer: StateFlow<String> = _timer

    private val _isPausedIcon = MutableStateFlow(false)
    val isPausedIcon: StateFlow<Boolean> = _isPausedIcon

    init {
        _timer.value = getFormattedTime()
    }

    fun onSkipClicked() {
        milliseconds = ROUND_TIME
        _timer.value = getFormattedTime()
        isPaused = true
        _isPausedIcon.value = !isPaused
        countDownJob?.cancel()
    }

    fun onActionClicked() {
        _isPausedIcon.value = isPaused
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

    fun onCloseClicked() {
        scope.cancel()
    }

    private companion object {
        const val ROUND_TIME = 900000L
        const val ONE_SECOND = 1000L
    }
}