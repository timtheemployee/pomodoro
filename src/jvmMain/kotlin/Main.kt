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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import timer.domain.Tick
import timer.presentation.TimerViewModel

fun main() = application {

    val timerViewModel = TimerViewModel()
    val timer = timerViewModel.timer.collectAsState()
    val isPaused = timerViewModel.isPausedIcon.collectAsState()
    val tick = timerViewModel.tick.collectAsState()

    Window(
        undecorated = true,
        onCloseRequest = ::exitApplication,
        state = WindowState(
            placement = WindowPlacement.Floating,
            size = DpSize(320.dp, 480.dp),
            position = WindowPosition.Aligned(Alignment.Center)
        )
    ) {
        MaterialTheme(
            content = {
                Column(
                    modifier = Modifier.fillMaxSize().background(AppColors.red),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    content = {
                        toolbar(
                            onSkip = timerViewModel::onSkipClicked,
                            onClose = {
                                timerViewModel.onCloseClicked()
                                exitApplication()
                            }
                        )
                        Text(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            text = timer.value,
                            style = MaterialTheme.typography.h2,
                            color = AppColors.textColor
                        )
                        tickView(tick = tick.value)
                        Spacer(modifier = Modifier.padding(vertical = 24.dp))
                        actionButton(
                            outlineColor = AppColors.textColor,
                            fillColor = AppColors.red,
                            isPaused = isPaused.value,
                            onClick = timerViewModel::onActionClicked
                        )
                        footer()
                    })
            }
        )
    }
}

@Composable
private fun tickView(modifier: Modifier = Modifier, tick: Tick) {
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
            color = AppColors.textColor
        )
    })
}

@Composable
private fun verticalBar(modifier: Modifier = Modifier, isHigh: Boolean) {
    Spacer(
        modifier = modifier.drawWithCache {
            onDrawBehind {
                drawRoundRect(AppColors.textColor, cornerRadius = CornerRadius(10.dp.toPx()))
            }
        }
            .height(if (isHigh) 48.dp else 32.dp)
            .width(6.dp)
    )
}

@Composable
private fun actionButton(outlineColor: Color, fillColor: Color, isPaused: Boolean, onClick: () -> Unit) {
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
private fun footer() {
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
                            horizontalAlignment = Alignment.CenterHorizontally, content = {
                                Text(
                                    "GOALS",
                                    color = AppColors.blue,
                                    style = MaterialTheme.typography.subtitle1,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                                Spacer(Modifier.padding(vertical = 8.dp))
                                Text("2/4", color = AppColors.blue, style = MaterialTheme.typography.h4)
                            })
                        Spacer(
                            modifier = Modifier
                                .width(2.dp)
                                .fillMaxHeight(0.5f)
                                .padding(vertical = 8.dp)
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
                                    style = MaterialTheme.typography.subtitle1,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                                Spacer(Modifier.padding(vertical = 8.dp))
                                Text("4", color = AppColors.blue, style = MaterialTheme.typography.h4)
                            })
                    })
            })
    })
}

@Composable
private fun ColumnScope.toolbar(
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
                        tint = AppColors.textColor
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
                        tint = AppColors.textColor
                    )
                }
            )
        }
    )
}
