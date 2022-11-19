package shared.data

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import shared.domain.OverlayColor
import tasks.domain.Task

class SharedRepository {

    private val _overlayColor = MutableSharedFlow<OverlayColor>()
    val overlayColor = _overlayColor.asSharedFlow()

    suspend fun setOverlayColor(color: OverlayColor) {
        _overlayColor.emit(color)
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
}