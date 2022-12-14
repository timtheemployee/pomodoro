import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
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
import control.presentation.ControlScreen
import control.presentation.ControlViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import main.MainViewModel
import shared.data.SharedRepository
import shared.domain.KeyCombo
import shared.domain.Navigation
import tasks.presentation.TaskListScreen
import tasks.presentation.TaskListViewModel

@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {
    val state = remember { MainState() }
    val mainViewModel = state.mainViewModel
    val taskListViewModel = state.taskListViewModel

    val navigation by mainViewModel.navigation.collectAsState()
    val requestNotification by mainViewModel.notification.collectAsState()

    if (requestNotification) {
        NotificationWindow(mainViewModel::requestFocus)
    }

    if (navigation != Navigation.CLOSE) {
        Window(
            undecorated = true,
            resizable = false,
            onCloseRequest = ::exitApplication,
            state = WindowState(
                placement = WindowPlacement.Floating,
                size = DpSize(560.dp, 720.dp),
                position = WindowPosition.Aligned(Alignment.Center),
            ),
            onKeyEvent = { event ->
                val combo = KeyCombo(
                    shiftPressed = event.isShiftPressed,
                    altPressed = event.isAltPressed,
                    ctrlPressed = event.isCtrlPressed,
                    enterPressed = event.key == Key.Enter && event.type == KeyEventType.KeyDown
                )
                if (combo.anyControlKeyPressed) {
                    mainViewModel.obtainKeyComboKey(combo)
                } else {
                    mainViewModel.obtainKeyComboKey(null)
                }
                false
            }
        ) {
            WindowDraggableArea {
                MaterialTheme(
                    content = {
                        Box(content = {
                            TaskListScreen(
                                modifier = Modifier.fillMaxSize(),
                                viewModel = taskListViewModel
                            )
                            ControlScreen(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .align(Alignment.BottomCenter),
                                controlViewModel = state.controlViewModel
                            )
                        })
                    }
                )
            }
        }
    }
}

@Composable
private fun NotificationWindow(onFocusReturnClick: () -> Unit) {
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
                modifier = Modifier.fillMaxSize().background(AppColors.dark),
                content = {
                    Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 24.dp),
                        text = "Work time is over! Take time to rest",
                        color = AppColors.gray,
                        style = MaterialTheme.typography.subtitle1,
                        textAlign = TextAlign.Center
                    )
                    TextButton(
                        onClick = onFocusReturnClick,
                        modifier = Modifier.align(Alignment.End),
                        content = {
                            Text(
                                color = AppColors.gray,
                                style = MaterialTheme.typography.caption,
                                textAlign = TextAlign.Center,
                                text = "Return to focus"
                            )
                        }
                    )
                })
        }
    )
}

class MainState {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val sharedRepository = SharedRepository()

    val mainViewModel = MainViewModel(scope, sharedRepository)
    val controlViewModel = ControlViewModel(scope, sharedRepository)
    val taskListViewModel = TaskListViewModel(scope, sharedRepository)
}
