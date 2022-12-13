package tasks.domain

import java.util.UUID

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val status: TaskStatus,
    val description: String
) {
    val taskText: String
        get() = "${status.startIcon} $description"
}

enum class TaskStatus(val startIcon: String) {
    CREATED("⬜"),
    DONE("✔"),
    CANCELLED("❌")
}