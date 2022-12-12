package control.presentation

import AppColors
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import timer.domain.TasksState

@Composable
fun ControlScreen(
    modifier: Modifier,
    controlViewModel: ControlViewModel
) {
    val stopped by controlViewModel.stopped.collectAsState()
    val remainingTime by controlViewModel.timer.collectAsState()
    val tasksState by controlViewModel.tasksState.collectAsState()
    val timerPercent by controlViewModel.timerPercent.collectAsState()

    Row(modifier = modifier.shadow(1.dp), verticalAlignment = Alignment.CenterVertically,
        content = {
            ActionButton(timerPercent, stopped, controlViewModel::switchTimerMode)
            TimerView(remainingTime, stopped)
            TaskListStatusView(tasksState)
        }
    )
}

@Composable
private fun TaskListStatusView(state: TasksState) {
    Text(
        modifier = Modifier.padding(horizontal = 8.dp),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.subtitle1,
        fontWeight = FontWeight.Light,
        text = "${state.remaining}/${state.count} (${state.percent}%)",
        color = AppColors.gray
    )
}

@Composable
private fun TimerView(remainingTime: String, rest: Boolean) {
    val statusText = if (rest) {
        "Rest"
    } else {
        "Work"
    }

    val backgroundColor = if (rest) {
        AppColors.green
    } else {
        AppColors.red
    }

    Column(
        modifier = Modifier.padding(horizontal = 8.dp),
        content = {
            Text(
                text = remainingTime,
                style = MaterialTheme.typography.subtitle1,
                color = AppColors.gray,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                modifier = Modifier.clip(RoundedCornerShape(15)).background(backgroundColor).padding(2.dp),
                text = statusText,
                style = MaterialTheme.typography.caption,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    )
}

@Composable
private fun ActionButton(
    timerPercent: Float,
    stopped: Boolean,
    onClick: () -> Unit,
) {
    Box(modifier = Modifier
        .padding(horizontal = 16.dp)
        .clickable(onClick = onClick)
        .clip(CircleShape),
        content = {
            Canvas(
                modifier = Modifier.size(32.dp),
                onDraw = {
                    val canvasRect = size.toRect()
                    val width = canvasRect.width
                    val height = canvasRect.height

                    drawArc(
                        color = AppColors.gray,
                        startAngle = -90f,
                        sweepAngle = -360f * timerPercent,
                        useCenter = true
                    )

                    drawCircle(
                        color = AppColors.dark,
                        center = Offset(x = width / 2, y = height / 2),
                        radius = width / 2.6f
                    )
                }
            )

            val icon = if (stopped) {
                Icons.Rounded.PlayArrow
            } else {
                Icons.Rounded.Stop
            }

            Icon(
                icon,
                contentDescription = null,
                tint = AppColors.gray,
                modifier = Modifier.align(Alignment.Center).size(16.dp)
            )
        }
    )
}