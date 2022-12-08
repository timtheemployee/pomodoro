package tasks.presentation

import AppColors
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import tasks.domain.Task
import tasks.domain.TaskStatus

@Composable
fun TaskListScreen(
    modifier: Modifier = Modifier,
    viewModel: TaskListViewModel
) {
    val tasks by viewModel.tasks.collectAsState()
    val input by viewModel.input.collectAsState()

    Column(modifier = modifier.fillMaxHeight()
        .verticalScroll(rememberScrollState())
        .background(AppColors.dark)
        .padding(8.dp),
        content = {
            tasks.forEach {
                TaskView(modifier, it, viewModel::toggleTaskCompletion)
            }
            TaskInputView(
                modifier = modifier.fillMaxWidth(),
                onTextChanged = viewModel::updateInputField,
                value = input,
                onTrailingIconClicked = viewModel::addNewTask
            )
        }
    )
}

@Composable
private fun TaskView(
    modifier: Modifier = Modifier,
    task: Task,
    onTaskClicked: (Task) -> Unit
) {
    Row(modifier = modifier
        .padding(horizontal = 8.dp, vertical = 8.dp)
        .clickable { onTaskClicked(task) },
        verticalAlignment = Alignment.CenterVertically,
        content = {
            val color = when (task.status) {
                TaskStatus.CREATED -> AppColors.gray
                TaskStatus.DONE -> AppColors.green
                TaskStatus.CANCELLED -> AppColors.red
            }

            val result = buildAnnotatedString {
                withStyle(style = SpanStyle(color)) {
                    append(task.taskText)
                }
            }

            Text(modifier = modifier, text = result)
        })
}

@Composable
private fun TaskInputView(
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
            backgroundColor = AppColors.dark,
            cursorColor = AppColors.gray,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            placeholderColor = AppColors.gray,
            textColor = AppColors.gray
        ),
        placeholder = { Text("Input new task here") },
        singleLine = true,
        trailingIcon = { TaskInputTrailingView(onClick = onTrailingIconClicked, tint = AppColors.gray) }
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