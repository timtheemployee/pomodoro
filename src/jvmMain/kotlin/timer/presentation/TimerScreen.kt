package timer.presentation

import AppColors
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import control.presentation.ControlViewModel
import timer.domain.Tick

@Deprecated("Will be removed soon")
@Composable
fun TimerScreen(
    modifier: Modifier = Modifier,
    viewModel: ControlViewModel,
    onClose: () -> Unit
) {
    val timer by viewModel.timer.collectAsState()
    val isPaused by viewModel.stopped.collectAsState()
    val goals by viewModel.tasksState.collectAsState()
    val rounds by viewModel.rounds.collectAsState()

    Column(
        modifier = modifier.fillMaxSize().background(AppColors.dark),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = {
            Toolbar(
                onSkip = { },
                onClose = { onClose() }
            )
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = timer,
                style = MaterialTheme.typography.h2,
                color = AppColors.gray
            )
            Spacer(modifier = modifier.padding(vertical = 24.dp))
            RoundActionButton(
                outlineColor = AppColors.gray,
                fillColor = AppColors.dark,
                isPaused = isPaused,
                onClick = viewModel::switchTimerMode
            )
        })
}

@Composable
private fun TickView(modifier: Modifier = Modifier, tick: Tick) {
    var secondsOnScreen = tick.anchorSeconds
    Row(verticalAlignment = Alignment.Bottom, content = {
        Spacer(modifier = modifier.height(48.dp).width(12.dp))
        repeat(12) { index ->
            val isHigh = ((index - tick.index).takeIf { it >= 0 } ?: tick.index) % 3 == 0
            if (isHigh) {
                verticalBarWithLabel(modifier, secondsOnScreen.toString())
                secondsOnScreen = (secondsOnScreen + 15) % 60
            } else {
                verticalBar(modifier, false)
            }
            if (!isHigh) {
                Spacer(modifier = modifier.height(48.dp).width(12.dp))
            }
        }
    })
}

@Composable
private fun verticalBarWithLabel(modifier: Modifier = Modifier, label: String) {
    Box(modifier = modifier, content = {
        verticalBar(modifier, true)
        Text(
            text = label,
            modifier = modifier.offset(x = -(7.5.dp), y = 50.dp).width(20.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body2,
            color = AppColors.gray
        )
    })
}

@Composable
private fun verticalBar(modifier: Modifier = Modifier, isHigh: Boolean) {
    Spacer(
        modifier = modifier.drawWithCache {
            onDrawBehind {
                drawRoundRect(AppColors.gray, cornerRadius = CornerRadius(10.dp.toPx()))
            }
        }
            .height(if (isHigh) 48.dp else 32.dp)
            .width(6.dp)
    )
}

@Composable
private fun RoundActionButton(outlineColor: Color, fillColor: Color, isPaused: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(128.dp))
            .clickable(
                onClick = onClick,
            ),
        content = {
            Canvas(
                modifier = Modifier.size(128.dp),
                onDraw = {
                    val canvasRect = size.toRect()
                    val width = canvasRect.width
                    val height = canvasRect.height

                    drawCircle(
                        color = outlineColor,
                        center = Offset(x = width / 2, y = height / 2),
                        radius = width / 2
                    )

                    drawCircle(
                        color = fillColor,
                        center = Offset(x = width / 2, y = height / 2),
                        radius = width / 2.5f
                    )
                })

            val icon = if (isPaused) {
                Icons.Rounded.Pause
            } else {
                Icons.Rounded.PlayArrow
            }

            Icon(
                icon,
                contentDescription = null,
                tint = outlineColor,
                modifier = Modifier.align(Alignment.Center).size(72.dp)
            )
        })
}


@Composable
private fun Footer(goals: String, rounds: Int, onGoalsClicked: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), content = {
        Card(
            shape = RoundedCornerShape(topStartPercent = 10, topEndPercent = 10),
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
            content = {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    content = {
                        Column(
                            modifier = Modifier.clickable { onGoalsClicked() },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            content = {
                                Text(
                                    "GOALS",
                                    color = AppColors.blue,
                                    style = MaterialTheme.typography.subtitle2,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                Spacer(Modifier.padding(vertical = 4.dp))
                                Text(goals, color = AppColors.blue, style = MaterialTheme.typography.subtitle1)
                            })
                        Spacer(
                            modifier = Modifier
                                .width(2.dp)
                                .fillMaxHeight(0.4f)
                                .padding(vertical = 6.dp)
                                .drawWithCache {
                                    onDrawBehind {
                                        drawRoundRect(AppColors.blue, cornerRadius = CornerRadius(10.dp.toPx()))
                                    }
                                }
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            content = {
                                Text(
                                    "ROUNDS",
                                    color = AppColors.blue,
                                    style = MaterialTheme.typography.subtitle2,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                Spacer(Modifier.padding(vertical = 4.dp))
                                Text(
                                    rounds.toString(),
                                    color = AppColors.blue,
                                    style = MaterialTheme.typography.subtitle1
                                )
                            })
                    })
            })
    })
}

@Composable
private fun ColumnScope.Toolbar(
    onSkip: () -> Unit,
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier.align(Alignment.End),
        content = {
            IconButton(
                content = {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = null,
                        tint = AppColors.gray
                    )
                },
                onClick = onSkip,
            )
            IconButton(
                onClick = onClose,
                content = {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = null,
                        tint = AppColors.gray
                    )
                }
            )
        }
    )
}