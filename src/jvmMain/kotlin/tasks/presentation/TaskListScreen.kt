package tasks.presentation

import AppColors
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import tasks.domain.Task
import tasks.domain.TaskStatus

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskListScreen(
    modifier: Modifier = Modifier,
    viewModel: TaskListViewModel
) {
    val tasks by viewModel.tasks.collectAsState()
    val input by viewModel.input.collectAsState()

    val listState = rememberLazyListState(tasks.lastIndex + 1)
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    LazyColumn(
        modifier = modifier
            .fillMaxHeight()
            .background(AppColors.dark)
            .padding(8.dp),
        state = listState,
        content = {
            stickyHeader { Toolbar(modifier, viewModel::closeApp) }
            items(tasks.size) {
                TaskView(modifier, tasks[it], viewModel::toggleTaskCompletion)
            }
            item {
                TaskInputView(
                    modifier = modifier.fillMaxWidth().focusRequester(focusRequester).padding(bottom = 80.dp + 16.dp),
                    onTextChanged = viewModel::updateInputField,
                    value = input,
                    onTrailingIconClicked = viewModel::addNewTask
                )
            }

            coroutineScope.launch {
                listState.animateScrollToItem(tasks.lastIndex + 1, -100)
                focusRequester.requestFocus()
            }
        }
    )
}

@Composable
private fun Toolbar(
    modifier: Modifier = Modifier,
    onCloseClick: () -> Unit,
) {
    Box(modifier = modifier
        .padding(horizontal = 8.dp, vertical = 8.dp)
        .background(AppColors.dark),
        content = {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "\uD83C\uDF45 Pomodoro",
                style = MaterialTheme.typography.subtitle1,
                color = AppColors.gray,
                textAlign = TextAlign.Center
            )
            IconButton(
                modifier = Modifier.size(24.dp).align(Alignment.CenterEnd),
                onClick = onCloseClick,
                content = {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = null,
                        tint = AppColors.gray
                    )
                }
            )
        })
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
        placeholder = { Text("\uD83D\uDCDD Input new task here") },
        singleLine = true,
        trailingIcon = { TaskInputTrailingView(onClick = onTrailingIconClicked, tint = AppColors.gray) },
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