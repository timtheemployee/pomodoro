package timer.presentation

import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import shared.data.SharedRepository
import shared.domain.AppMode
import shared.domain.Notification
import tasks.domain.Task
import timer.domain.Tick

class TimerViewModel(
    private val scope: CoroutineScope,
    private val sharedRepository: SharedRepository
) {

    private var milliseconds = DEBUG_ROUND_TIME
    private var fiveSecondsAccumulator = 0L
    private var isPaused = true
    private var countDownJob: Job? = null

    private val _timer = MutableStateFlow("")
    val timer: StateFlow<String> = _timer

    private val _isPausedIcon = MutableStateFlow(false)
    val isPausedIcon: StateFlow<Boolean> = _isPausedIcon

    private val _tick = MutableStateFlow(Tick(index = 0, anchorSeconds = 0))
    val tick: StateFlow<Tick> = _tick

    private val _appMode = MutableStateFlow(AppMode.ACTIVE)
    val appMode: StateFlow<AppMode> = _appMode

    private val _goals = MutableStateFlow("0/0")
    val goals: StateFlow<String> = _goals

    private val _rounds = MutableStateFlow(0)
    val rounds: StateFlow<Int> = _rounds

    private var firstTask: Task? = null

    init {
        _timer.value = getFormattedTime()

        sharedRepository.mode
            .onEach { _appMode.value = it }
            .launchIn(scope)

        sharedRepository.tasks
            .onEach { tasks ->
                val completed = tasks.filter { it.checked }.size
                _goals.value = "$completed/${tasks.size}"
                firstTask = tasks.firstOrNull { !it.checked }
            }
            .launchIn(scope)

        _rounds.value = 0
    }

    fun resetTimer() {
        milliseconds = DEBUG_ROUND_TIME
        _timer.value = getFormattedTime()
        isPaused = true
        _isPausedIcon.value = !isPaused
        fiveSecondsAccumulator = 0L
        _tick.value = Tick(index = 0, anchorSeconds = 0)
        countDownJob?.cancel()
    }

    fun switchTimerMode() {
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

    private fun getOppositeAppMode(): AppMode =
        when (_appMode.value) {
            AppMode.ACTIVE -> AppMode.REST
            AppMode.REST -> AppMode.ACTIVE
        }

    private suspend fun tick() {
        delay(ONE_SECOND)
        milliseconds -= ONE_SECOND
        _timer.value = getFormattedTime()
        updateTick()

        if (milliseconds <= 0) {
            if (_appMode.value == AppMode.ACTIVE) {
                _rounds.value += 1
                sharedRepository.setNotification(Notification.ACTIVE)
            } else {
                sharedRepository.setNotification(Notification.REST)
            }

            sharedRepository.setMode(getOppositeAppMode())
            resetTimer()
        }
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

    fun makeFirstTaskCompleted() {
        firstTask?.let { task ->
            val updatedTask = task.copy(checked = !task.checked)

            scope.launch {
                sharedRepository.replaceTask(task, updatedTask)
            }
        }
    }

    private companion object {
        const val DEBUG_ROUND_TIME = 3000L
        const val ROUND_TIME = 900000L
        const val ONE_SECOND = 1000L
    }
}