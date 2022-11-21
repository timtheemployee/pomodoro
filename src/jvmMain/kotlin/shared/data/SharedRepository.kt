package shared.data

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import shared.domain.AppMode
import shared.domain.KeyCombo
import tasks.domain.Task

class SharedRepository {

    private val _mode = MutableSharedFlow<AppMode>()
    val mode = _mode.asSharedFlow()

    suspend fun setMode(mode: AppMode) {
        _mode.emit(mode)
    }

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

    private val _keyCombo = MutableSharedFlow<KeyCombo>()
    val keyCombo = _keyCombo.asSharedFlow()

    suspend fun setKeyCombo(combo: KeyCombo) {
        _keyCombo.emit(combo)
    }
}