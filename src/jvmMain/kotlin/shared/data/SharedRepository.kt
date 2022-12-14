package shared.data

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import shared.domain.KeyCombo
import shared.domain.Navigation
import tasks.domain.Task
import kotlin.math.min
import kotlin.math.max

class SharedRepository {

    private val storedTasks = mutableListOf<Task>()
    private val _tasks = MutableSharedFlow<List<Task>>()
    val tasks = _tasks.asSharedFlow()

    suspend fun replaceTask(old: Task, new: Task) {
        val index = storedTasks.indexOf(old)
        storedTasks.removeAt(index)
        storedTasks.add(index, new)

        _tasks.emit(storedTasks.toList())
    }

    suspend fun addTask(task: Task) {
        storedTasks.add(task)

        _tasks.emit(storedTasks.toList())
    }

    private val _keyCombo = MutableSharedFlow<KeyCombo?>()
    val keyCombo = _keyCombo.asSharedFlow()

    suspend fun setKeyCombo(combo: KeyCombo?) {
        _keyCombo.emit(combo)
    }

    private val _notification = MutableSharedFlow<Boolean>()
    val notification = _notification.asSharedFlow()

    suspend fun setNotification(enabled: Boolean) {
        _notification.emit(enabled)
    }

    private val _navigation = MutableSharedFlow<Navigation>()
    val navigation = _navigation.asSharedFlow()

    suspend fun setNavigation(navigation: Navigation) {
        _navigation.emit(navigation)
    }

    private val _timer = MutableSharedFlow<Long>(replay = 1)
    val timer = _timer.asSharedFlow()

    suspend fun setTimer(time: Long) {
        _timer.emit(max(min(time, SIXTY_MINUTES), FIVE_MINUTES))
    }

    suspend fun setDefaultTimer() {
        _timer.emit(TWENTY_FIVE_MINUTES)
    }

    private val _elapsed = MutableSharedFlow<Long>()
    val elapsed = _elapsed.asSharedFlow()

    suspend fun setElapsedTime(elapsed: Long) {
        _elapsed.emit(elapsed)
    }

    private companion object {
        const val TWENTY_FIVE_MINUTES = 1500000L
        const val SIXTY_MINUTES = 3600000L
        const val FIVE_MINUTES = 300000L
    }
}