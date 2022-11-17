package timer.presentation

import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import shared.data.SharedRepository
import shared.domain.OverlayColor
import timer.domain.Tick

class TimerViewModel(
    private val scope: CoroutineScope,
    private val sharedRepository: SharedRepository
) {

    private var milliseconds = ROUND_TIME
    private var fiveSecondsAccumulator = 0L
    private var isPaused = true
    private var countDownJob: Job? = null

    private val _timer = MutableStateFlow("")
    val timer: StateFlow<String> = _timer

    private val _isPausedIcon = MutableStateFlow(false)
    val isPausedIcon: StateFlow<Boolean> = _isPausedIcon

    private val _tick = MutableStateFlow(Tick(index = 0, anchorSeconds = 0))
    val tick: StateFlow<Tick> = _tick

    private val _overlayColor = MutableStateFlow(OverlayColor.ACTIVE)
    val overlayColor: StateFlow<OverlayColor> = _overlayColor

    init {
        _timer.value = getFormattedTime()

        scope.launch {
            sharedRepository.overlayColor
                .collect(::obtainOverlayColorChanges)
        }
    }

    private fun obtainOverlayColorChanges(overlayColor: OverlayColor) {
        _overlayColor.value = overlayColor
    }

    fun onSkipClicked() {
        milliseconds = ROUND_TIME
        _timer.value = getFormattedTime()
        isPaused = true
        _isPausedIcon.value = !isPaused
        fiveSecondsAccumulator = 0L
        _tick.value = Tick(index = 0, anchorSeconds = 0)
        countDownJob?.cancel()
    }

    fun onActionClicked() {

        scope.launch {
            sharedRepository.setOverlayColor(getOppositeOverlayColor())
        }

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

    private fun getOppositeOverlayColor(): OverlayColor =
        when (_overlayColor.value) {
            OverlayColor.ACTIVE -> OverlayColor.REST
            OverlayColor.REST -> OverlayColor.ACTIVE
        }

    private suspend fun tick() {
        delay(ONE_SECOND)
        milliseconds -= ONE_SECOND
        _timer.value = getFormattedTime()
        updateTick()
    }

    private fun updateTick() {
        fiveSecondsAccumulator += ONE_SECOND
        var updatedTick = _tick.value
        if (fiveSecondsAccumulator == ONE_SECOND * 5) {

            if (updatedTick.index == 0) {
                updatedTick = updatedTick.copy(anchorSeconds = (updatedTick.anchorSeconds + 15) % 60)
            }

            updatedTick = when (updatedTick.index) {
                0 -> updatedTick.copy(index = 2)
                2 -> updatedTick.copy(index = 1)
                1 -> updatedTick.copy(index = 0)
                else -> throw IllegalStateException("Unsupported index ${updatedTick.index}")
            }

            fiveSecondsAccumulator = 0L
        }
        _tick.value = updatedTick
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