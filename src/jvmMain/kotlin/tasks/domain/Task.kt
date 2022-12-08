package tasks.domain

data class Task(
    val status: TaskStatus,
    val description: String
) {
    val taskText: String
        get() = "${status.startIcon} $description"
}

enum class TaskStatus(val startIcon: String) {
    CREATED("□"),
    DONE("✓"),
    CANCELLED("✕")
}