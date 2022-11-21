import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.MaterialTheme
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
import tasks.presentation.TaskListScreen
import tasks.presentation.TaskListViewModel
import timer.presentation.TimerScreen
import timer.presentation.TimerViewModel

@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {

    val scope = CoroutineScope(Dispatchers.IO)
    val sharedRepository = SharedRepository()

    val mainViewModel = MainViewModel(scope, sharedRepository)
    val timerViewModel = TimerViewModel(scope, sharedRepository)
    val taskListViewModel = TaskListViewModel(scope, sharedRepository)

    Window(
        undecorated = true,
        resizable = false,
        onCloseRequest = ::exitApplication,
        state = WindowState(
            placement = WindowPlacement.Floating,
            size = DpSize(640.dp, 480.dp),
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
                        TaskListScreen(modifier = Modifier.width(320.dp), viewModel = taskListViewModel)
                        TimerScreen(
                            modifier = Modifier.width(320.dp),
                            viewModel = timerViewModel,
                            onClose = {
                                scope.cancel()
                                exitApplication()
                            }
                        )
                    })
                }
            )
        }
    }
}
