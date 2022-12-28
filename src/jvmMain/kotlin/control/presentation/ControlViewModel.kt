package control.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import shared.data.SharedRepository
import tasks.domain.Task
import tasks.domain.TaskStatus
import control.domain.TasksState
import java.util.concurrent.TimeUnit

class ControlViewModel(
    private val scope: CoroutineScope,
    private val sharedRepository: SharedRepository
) {

    private var milliseconds = 0L
    private var roundTime = 0L
    private var elapsedTime = 0L
    private var countDownJob: Job? = null

    var timer = MutableStateFlow("")
        private set

    var configTimer = MutableStateFlow("")
        private set

    var timerPercent = MutableStateFlow(1f)
        private set

    var stopped = MutableStateFlow(true)
        private set

    var tasksState = MutableStateFlow(TasksState())
        private set

    var elapsed = MutableStateFlow("00:00:00")
        private set

    init {
        sharedRepository.tasks
            .onEach(::updateTasksState)
            .launchIn(scope)

        sharedRepository.timer
            .onEach {
                milliseconds = it
                roundTime = it
                timer.value = formatTimer()
                configTimer.value = formatTimeConfig()
            }
            .launchIn(scope)

        sharedRepository.elapsed
            .onEach { elapsed.value = formatElapsedTime(it) }
            .launchIn(scope)
    }

    private fun updateTasksState(tasks: List<Task>) {
        val remaining = tasks.count { it.status == TaskStatus.DONE }
        val count = tasks.count { it.status != TaskStatus.CANCELLED }

        tasksState.value = TasksState(count, remaining)
    }

    fun switchTimerMode() {
        stopped.value = !stopped.value

        if (!stopped.value) {
            countDownJob = scope.launch {
                while (isActive) {
                    tick()
                }
            }
        } else {
            elapsedTime += roundTime - milliseconds
            milliseconds = roundTime
            timer.value = formatTimer()
            timerPercent.value = 1f
            countDownJob?.cancel()

            scope.launch {
                sharedRepository.setNotification(true)
                sharedRepository.setElapsedTime(elapsedTime)
            }
        }
    }

    private suspend fun tick() {
        delay(ONE_SECOND)
        milliseconds -= ONE_SECOND
        timerPercent.value = milliseconds.toFloat() / roundTime
        timer.value = formatTimer()

        if (milliseconds <= 0) {
            switchTimerMode()
        }
    }


    private fun formatTimer(): String =
        String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(milliseconds),
            TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds))
        )

    private fun formatTimeConfig(): String =
        String.format("%02dm", TimeUnit.MILLISECONDS.toMinutes(milliseconds))

    private fun formatElapsedTime(millis: Long): String =
        String.format(
            "%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(millis),
            TimeUnit.MILLISECONDS.toMinutes(millis) -
                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
            TimeUnit.MILLISECONDS.toSeconds(millis) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        )


    fun increaseTimer() {
        scope.launch { sharedRepository.setTimer(roundTime + FIVE_MINUTES) }
    }

    fun decreaseTimer() {
        scope.launch { sharedRepository.setTimer(roundTime - FIVE_MINUTES) }
    }

    private companion object {
        const val ONE_SECOND = 1000L
        const val FIVE_MINUTES = 300000L
    }
}