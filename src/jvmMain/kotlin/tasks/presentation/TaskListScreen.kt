package tasks.presentation

import AppColors
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import shared.domain.OverlayColor
import tasks.domain.Task

@Composable
fun TaskListScreen(
    modifier: Modifier = Modifier,
    viewModel: TaskListViewModel
) {
    val tasks = viewModel.tasks.collectAsState()
    val input = viewModel.input.collectAsState()
    val overlayColor = viewModel.overlayColor.collectAsState()

    Box(modifier = modifier.fillMaxSize(),
        content = {
            TaskInputView(
                overlayColor = overlayColor.value.asAppColor(),
                modifier = modifier.align(Alignment.BottomCenter),
                onTextChanged = viewModel::onTaskTextChanged,
                value = input.value,
                onTrailingIconClicked = viewModel::onTaskEditingComplete
            )
            Column(modifier = modifier
                .verticalScroll(rememberScrollState())
                .background(AppColors.white),

                content = {
                    tasks.value.forEach {
                        TaskView(overlayColor.value.asAppColor(), it, viewModel::onTaskCompletionChanged)
                    }
                }
            )
        })
}

@Composable
private fun TaskView(
    overlayColor: Color,
    task: Task,
    onTaskCompletionChanged: (Boolean, Task) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically, content = {
        Checkbox(
            checked = task.checked,
            onCheckedChange = { onTaskCompletionChanged(it, task) },
            colors = CheckboxDefaults.colors(
                checkedColor = overlayColor
            )
        )
        Text(text = task.description)
    })
}

@Composable
private fun TaskInputView(
    overlayColor: Color,
    modifier: Modifier = Modifier,
    value: String,
    onTextChanged: (String) -> Unit,
    onTrailingIconClicked: () -> Unit,
) {
    TextField(
        value,
        onTextChanged,
        modifier,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = AppColors.textColor,
            cursorColor = overlayColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        singleLine = true,
        shape = RoundedCornerShape(topStartPercent = 10, topEndPercent = 10),
        trailingIcon = { TaskInputTrailingView(onClick = onTrailingIconClicked, tint = overlayColor) }
    )
}

@Composable
private fun TaskInputTrailingView(tint: Color, onClick: () -> Unit) {
    IconButton(
        content = {
            Icon(
                Icons.Default.Done,
                contentDescription = null,
                tint = tint
            )
        },
        onClick = onClick
    )
}

private fun OverlayColor.asAppColor(): Color =
    when (this) {
        OverlayColor.ACTIVE -> AppColors.red
        OverlayColor.REST -> AppColors.blue
    }