package tasks.presentation

import AppColors
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import shared.domain.AppMode
import tasks.domain.Task
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import java.awt.SystemColor.text

@Composable
fun TaskListScreen(
    modifier: Modifier = Modifier,
    viewModel: TaskListViewModel
) {
    val tasks by viewModel.tasks.collectAsState()
    val input by viewModel.input.collectAsState()
    val overlayColor by viewModel.overlayColor.collectAsState()

    Box(modifier = modifier
        .fillMaxSize(),
        content = {
            TaskInputView(
                overlayColor = overlayColor.asAppColor(),
                modifier = modifier.align(Alignment.BottomCenter),
                onTextChanged = viewModel::updateInputField,
                value = input,
                onTrailingIconClicked = viewModel::addNewTask
            )
            if (tasks.isEmpty()) {
                EmptyListView(modifier = modifier.align(Alignment.Center))
            } else {
                Column(modifier = modifier
                    .verticalScroll(rememberScrollState())
                    .background(AppColors.white),

                    content = {
                        tasks.forEach {
                            TaskView(overlayColor.asAppColor(), it, viewModel::toggleTaskCompletion)
                        }
                    }
                )
            }
        })
}

@Composable
private fun EmptyListView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        content = {
            Text(text = "Add new task")
            Spacer(modifier = modifier.height(16.dp))
            Icon(Icons.Default.ArrowDownward, contentDescription = null)
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

private fun AppMode.asAppColor(): Color =
    when (this) {
        AppMode.ACTIVE -> AppColors.red
        AppMode.REST -> AppColors.blue
    }