package control.domain

import kotlin.math.roundToInt

data class TasksState(
    val count: Int = 0,
    val remaining: Int = 0
) {

    val percent: Int =
        if (count == 0) {
            0
        } else {
            ((remaining.toFloat() / count) * 100).roundToInt()
        }
}