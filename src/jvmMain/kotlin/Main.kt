import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import main.MainViewModel
import shared.data.SharedRepository
import shared.domain.Notification
import tasks.presentation.TaskListScreen
import tasks.presentation.TaskListViewModel
import timer.presentation.TimerScreen
import timer.presentation.TimerViewModel

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    application {
        val state = remember { MainState() }
        val scope = state.scope
        val mainViewModel = state.mainViewModel
        val timerViewModel = state.timerViewModel
        val taskListViewModel = state.taskListViewModel

        val notification by mainViewModel.notification.collectAsState()

        if (notification != null) {
            NotificationWindow(notification, mainViewModel::clearNotification)
        }

        Window(
            undecorated = true,
            resizable = false,
            onCloseRequest = ::exitApplication,
            state = WindowState(
                placement = WindowPlacement.Floating,
                size = DpSize(560.dp, 720.dp),
                position = WindowPosition.Aligned(Alignment.Center)
            ),
            onKeyEvent = { event ->
                when {
                    event.isShiftPressed && event.key == Key.Enter && event.type == KeyEventType.KeyUp -> {
                        mainViewModel.makeFirstTaskCompeted()
                        true
                    }

                    event.isAltPressed && event.key == Key.Enter -> {
                        mainViewModel.createNewTask()
                        true
                    }

                    else -> false
                }
            }
        ) {
            WindowDraggableArea {
                MaterialTheme(
                    content = {
                        Row(content = {
                            TaskListScreen(modifier = Modifier.width(560.dp), viewModel = taskListViewModel)
                        })
                    }
                )
            }
        }
    }
}

@Composable
private fun NotificationWindow(notification: Notification?, onOkClicked: () -> Unit) {
    if (notification != null) {
        val color = when (notification) {
            Notification.ACTIVE -> AppColors.red
            Notification.REST -> AppColors.blue
        }

        val text = when (notification) {
            Notification.ACTIVE -> "Active time is up! Prepare to rest"
            Notification.REST -> "Rest time is up! Prepare to work"
        }

        Window(
            alwaysOnTop = true,
            undecorated = true,
            resizable = false,
            state = WindowState(
                placement = WindowPlacement.Floating,
                size = DpSize(320.dp, 96.dp),
                position = WindowPosition.Aligned(Alignment.BottomEnd)
            ),
            onCloseRequest = {},
            content = {
                Column(
                    verticalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxSize().background(color),
                    content = {
                        Text(
                            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 24.dp),
                            text = text,
                            color = AppColors.gray,
                            style = MaterialTheme.typography.subtitle1,
                            textAlign = TextAlign.Center
                        )
                        TextButton(
                            onClick = onOkClicked,
                            modifier = Modifier.align(Alignment.End),
                            content = {
                                Text(
                                    color = AppColors.gray,
                                    style = MaterialTheme.typography.caption,
                                    textAlign = TextAlign.Center,
                                    text = "Ok"
                                )
                            }
                        )
                    })
            }
        )
    }
}

class MainState {
    val scope = CoroutineScope(Dispatchers.IO)
    private val sharedRepository = SharedRepository()

    val mainViewModel = MainViewModel(scope, sharedRepository)
    val timerViewModel = TimerViewModel(scope, sharedRepository)
    val taskListViewModel = TaskListViewModel(scope, sharedRepository)
}
