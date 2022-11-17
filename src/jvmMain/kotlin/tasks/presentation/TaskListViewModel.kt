package tasks.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import shared.data.SharedRepository
import shared.domain.OverlayColor
import tasks.domain.Task

class TaskListViewModel(
    scope: CoroutineScope,
    private val sharedRepository: SharedRepository
) {

    private val _input = MutableStateFlow("")
    val input: StateFlow<String> = _input

    private val _tasks = MutableStateFlow(emptyList<Task>())
    val tasks: StateFlow<List<Task>> = _tasks

    private val _overlayColor = MutableStateFlow(OverlayColor.ACTIVE)
    val overlayColor: StateFlow<OverlayColor> = _overlayColor

    init {
        _tasks.value = listOf(
            Task(checked = false, "First task"),
            Task(checked = true, "Second task")
        )

        scope.launch {
            sharedRepository.overlayColor
                .collect { color -> _overlayColor.value = color }
        }
    }

    fun onTaskCompletionChanged(isChecked: Boolean, task: Task) {
        val storedTasks = _tasks.value.toMutableList()
        val index = storedTasks.indexOf(task)
        val storedTask = storedTasks[index]
        val updatedTask = storedTask.copy(checked = isChecked)
        storedTasks.removeAt(index)
        storedTasks.add(index, updatedTask)
        _tasks.value = storedTasks
    }

    fun onTaskTextChanged(text: String) {
        _input.value = text
    }

    fun onTaskEditingComplete() {
        val storedTasks = _tasks.value.toMutableList()
        val newTask = Task(checked = false, description = _input.value)
        storedTasks.add(newTask)
        _tasks.value = storedTasks
    }
}