package shared.domain

data class KeyCombo(
    val shiftPressed: Boolean = false,
    val altPressed: Boolean = false,
    val ctrlPressed: Boolean = false,
    val enterPressed: Boolean = false
) {

    val onlyEnterPressed: Boolean =
        !shiftPressed && !altPressed && !ctrlPressed && enterPressed

    val anyControlKeyPressed: Boolean =
        shiftPressed || altPressed || ctrlPressed || enterPressed
}