package control.presentation

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
import tasks.domain.Task
import tasks.domain.TaskStatus
import timer.domain.TasksState
import java.util.concurrent.TimeUnit

class ControlViewModel(
    private val scope: CoroutineScope,
    private val sharedRepository: SharedRepository
) {

    private var milliseconds = ROUND_TIME
    private var fiveSecondsAccumulator = 0L
    private var countDownJob: Job? = null

    private val _timer = MutableStateFlow("")
    val timer: StateFlow<String> = _timer

    private val _timerPercent = MutableStateFlow(1f)
    val timerPercent: StateFlow<Float> = _timerPercent

    private val _stopped = MutableStateFlow(true)
    val stopped: StateFlow<Boolean> = _stopped

    private val _tasksState = MutableStateFlow(TasksState())
    val tasksState: StateFlow<TasksState> = _tasksState

    private val _rounds = MutableStateFlow(0)
    val rounds: StateFlow<Int> = _rounds


    init {
        _timer.value = getFormattedTime()
        _rounds.value = 0

        sharedRepository.tasks
            .onEach(::updateTasksState)
            .launchIn(scope)
    }

    private fun updateTasksState(tasks: List<Task>) {
        val remaining = tasks.count { it.status == TaskStatus.DONE }
        val count = tasks.count { it.status != TaskStatus.CANCELLED }

        _tasksState.value = TasksState(count, remaining)
    }

    fun switchTimerMode() {
        _stopped.value = !_stopped.value

        if (!_stopped.value) {
            countDownJob = scope.launch {
                while (isActive) {
                    tick()
                }
            }
        } else {
            milliseconds = ROUND_TIME
            _timer.value = getFormattedTime()
            _timerPercent.value = 1f
            fiveSecondsAccumulator = 0L
            countDownJob?.cancel()
        }
    }

    private suspend fun tick() {
        delay(ONE_SECOND)
        milliseconds -= ONE_SECOND
        _timerPercent.value = milliseconds.toFloat() / ROUND_TIME
        _timer.value = getFormattedTime()

        if (milliseconds <= 0) {
            switchTimerMode()
        }
    }


    private fun getFormattedTime(): String =
        String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(milliseconds),
            TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds))
        )

    private companion object {
        const val ROUND_TIME = 180000L
        const val ONE_SECOND = 1000L
    }
}