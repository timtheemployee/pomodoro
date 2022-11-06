import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Shapes
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
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
                    modifier = Modifier.fillMaxWidth(),
                    content = {
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
