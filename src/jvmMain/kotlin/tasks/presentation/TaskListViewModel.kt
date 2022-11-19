package tasks.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import shared.data.SharedRepository
import shared.domain.OverlayColor
import tasks.domain.Task

class TaskListViewModel(
    private val scope: CoroutineScope,
    private val sharedRepository: SharedRepository
) {

    private val _input = MutableStateFlow("")
    val input: StateFlow<String> = _input

    private val _tasks = MutableStateFlow(emptyList<Task>())
    val tasks: StateFlow<List<Task>> = _tasks

    private val _overlayColor = MutableStateFlow(OverlayColor.ACTIVE)
    val overlayColor: StateFlow<OverlayColor> = _overlayColor

    init {
        sharedRepository.overlayColor
            .onEach { _overlayColor.value = it }
            .launchIn(scope)

        sharedRepository.tasks
            .onEach { _tasks.value = it }
            .launchIn(scope)
    }

    fun toggleTaskCompletion(isChecked: Boolean, task: Task) {
        val newTask = task.copy(checked = isChecked)

        scope.launch {
            sharedRepository.replaceTask(task, newTask)
        }
    }

    fun updateInputField(text: String) {
        _input.value = text
    }

    fun addNewTask() {
        scope.launch {
            val newTask = Task(checked = false, description = _input.value)
            sharedRepository.addTask(newTask)
            _input.value = ""
        }
    }
}