package tasks.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import shared.data.SharedRepository
import shared.domain.KeyCombo
import shared.domain.Navigation
import tasks.domain.Task
import tasks.domain.TaskStatus

class TaskListViewModel(
    private val scope: CoroutineScope,
    private val sharedRepository: SharedRepository
) {

    private val _input = MutableStateFlow("")
    val input: StateFlow<String> = _input

    private val _tasks = MutableStateFlow(emptyList<Task>())
    val tasks: StateFlow<List<Task>> = _tasks

    init {
        sharedRepository.tasks
            .onEach { _tasks.value = it }
            .launchIn(scope)

        sharedRepository.keyCombo
            .onEach(::obtainKeyCombo)
            .launchIn(scope)
    }

    private fun obtainKeyCombo(combo: KeyCombo) {
        when (combo) {
            KeyCombo.DONE_FIRST -> {
                _tasks.value
                    .firstOrNull { it.status == TaskStatus.CREATED }
                    ?.let(::toggleTaskCompletion)
            }

            KeyCombo.ADD_NEW_TASK -> {
                addNewTask()
            }
        }
    }

    fun toggleTaskCompletion(task: Task) {
        val newStatus = when (task.status) {
            TaskStatus.CREATED -> TaskStatus.DONE
            TaskStatus.DONE -> TaskStatus.CREATED
            TaskStatus.CANCELLED -> TaskStatus.CANCELLED
        }
        val newTask = task.copy(status = newStatus)

        scope.launch {
            sharedRepository.replaceTask(task, newTask)
        }
    }

    fun updateInputField(text: String) {
        _input.value = text
    }

    fun addNewTask() {
        scope.launch {
            if (_input.value.isNotEmpty()) {
                val newTask = Task(status = TaskStatus.CREATED, description = _input.value)
                sharedRepository.addTask(newTask)
                _input.value = ""
            }
        }
    }

    fun closeApp() {
        scope.launch {
            sharedRepository.setNavigation(Navigation.CLOSE)
        }
    }
}