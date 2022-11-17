package tasks.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import tasks.domain.Task

class TaskListViewModel {

    private val _input = MutableStateFlow("")
    val input: StateFlow<String> = _input

    private val _tasks = MutableStateFlow(emptyList<Task>())
    val tasks: StateFlow<List<Task>> = _tasks

    init {
        _tasks.value = listOf(
            Task(checked = false, "First task"),
            Task(checked = true, "Second task")
        )
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