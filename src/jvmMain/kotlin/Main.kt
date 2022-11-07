import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import presentation.TimerViewModel

fun main() = application {

    val timerViewModel = TimerViewModel()
    val timer = timerViewModel.timer.collectAsState()

    Window(
        undecorated = true,
        onCloseRequest = ::exitApplication,
        state = WindowState(
            placement = WindowPlacement.Floating,
            size = DpSize(640.dp, 480.dp),
            position = WindowPosition.Aligned(Alignment.Center)
        )
    ) {
        MaterialTheme(
            content = {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    content = {
                        IconButton(
                            modifier = Modifier.align(Alignment.End),
                            onClick = {
                                timerViewModel.onCloseClicked()
                                exitApplication()
                            },
                            content = {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = null,
                                    tint = MaterialTheme.colors.primary
                                )
                            }
                        )
                        TextButton(
                            modifier = Modifier.align(Alignment.End),
                            content = { Text("Skip") },
                            onClick = timerViewModel::onSkipClicked,
                        )
                        Text(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            text = timer.value
                        )
                        Text(modifier = Modifier.align(Alignment.CenterHorizontally), text = "Timer layout")
                        Button(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            onClick = timerViewModel::onActionClicked,
                            content = { Text("Start/Stop button") })

                        Row(modifier = Modifier.align(Alignment.CenterHorizontally), content = {
                            Column(content = {
                                Text("Round")
                                Text("2/4")
                            })
                            Column(content = {
                                Text("Goal")
                                Text("1/4")
                            })
                        })
                    })
            }
        )
    }
}
