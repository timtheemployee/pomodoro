package tasks.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import shared.data.SharedRepository
import shared.domain.AppMode
import shared.domain.KeyCombo
import tasks.domain.Task

class TaskListViewModel(
    private val scope: CoroutineScope,
    private val sharedRepository: SharedRepository
) {

    private val _input = MutableStateFlow("")
    val input: StateFlow<String> = _input

    private val _tasks = MutableStateFlow(emptyList<Task>())
    val tasks: StateFlow<List<Task>> = _tasks

    private val _overlayColor = MutableStateFlow(AppMode.ACTIVE)
    val overlayColor: StateFlow<AppMode> = _overlayColor

    init {
        sharedRepository.mode
            .onEach { _overlayColor.value = it }
            .launchIn(scope)

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
                val task = _tasks.value.firstOrNull { !it.checked }
                task?.let { toggleTaskCompletion(true, it) }
            }

            KeyCombo.ADD_NEW_TASK -> {
                if (_input.value.isNotEmpty()) {
                    addNewTask()
                }
            }
        }
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
            if (_input.value.isNotEmpty()) {
                val newTask = Task(checked = false, description = _input.value)
                sharedRepository.addTask(newTask)
                _input.value = ""
            }
        }
    }
}