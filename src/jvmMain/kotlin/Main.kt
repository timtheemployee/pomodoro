import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import shared.data.SharedRepository
import tasks.presentation.TaskListScreen
import tasks.presentation.TaskListViewModel
import timer.presentation.TimerScreen
import timer.presentation.TimerViewModel

fun main() = application {

    val scope = CoroutineScope(Dispatchers.IO)
    val sharedRepository = SharedRepository()
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
        )
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
