@file:OptIn(ExperimentalFoundationApi::class)

package edu.farmingdale.draganddropanim_demo

import android.content.ClipData
import android.content.ClipDescription
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp



@Composable
fun DragAndDropBoxes(modifier: Modifier = Modifier) {
    // Controls the continuous rotation of the rectangle.
    var isPlaying by remember { mutableStateOf(true) }

    // Indicates the current drop direction: -1 for up, +1 for down, 0 for centre.
    var dropDirection by remember { mutableStateOf(0) }

    // Index of the currently active drop box.  Used to show the arrow in the
    // selected box.
    var dragBoxIndex by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Row of drop boxes.  A drag operation can be started on the arrow
        // displayed in the active box.  When dropped onto a target the
        // dragBoxIndex and dropDirection states are updated accordingly.
        Row(
            modifier = modifier
                .fillMaxWidth()
                .weight(0.2f)
        ) {
            val boxCount = 4
            repeat(boxCount) { index ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(10.dp)
                        .border(1.dp, Color.Black)
                        .dragAndDropTarget(
                            shouldStartDragAndDrop = { event ->
                                event
                                    .mimeTypes()
                                    .contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
                            },
                            target = remember {
                                object : DragAndDropTarget {
                                    override fun onDrop(event: DragAndDropEvent): Boolean {
                                        // Toggle rotation on each drop.
                                        isPlaying = !isPlaying
                                        dragBoxIndex = index
                                        // Determine animation direction based on which half of the row
                                        // the drop occurred in.  The first half moves the
                                        // rectangle upwards, the second half moves it downwards.
                                        dropDirection = if (index < boxCount / 2) -1 else 1
                                        return true
                                    }
                                }
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    this@Row.AnimatedVisibility(
                        visible = index == dragBoxIndex,
                        enter = scaleIn() + fadeIn(),
                        exit = scaleOut() + fadeOut()
                    ) {
                        // Replace the textual "Right" command with a right‑arrow icon.
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowRight,
                            contentDescription = "Drag me",
                            modifier = Modifier
                                .fillMaxSize()
                                .dragAndDropSource {
                                    detectTapGestures(
                                        onLongPress = { offset ->
                                            startTransfer(
                                                transferData = DragAndDropTransferData(
                                                    clipData = ClipData.newPlainText(
                                                        "text",
                                                        ""
                                                    )
                                                )
                                            )
                                        }
                                    )
                                },
                            tint = Color.Red
                        )
                    }
                }
            }
        }

        // Animate the rectangle's position based on the drop direction.
        val pOffset by animateIntOffsetAsState(
            targetValue = when (dropDirection) {
                -1 -> IntOffset(130, 100) // Upwards
                1 -> IntOffset(130, 300)  // Downwards
                else -> IntOffset(130, 200) // Centre
            },
            animationSpec = tween(3000, easing = LinearEasing),
            label = "pOffset"
        )

        // Animate rotation.  When isPlaying is true we rotate continuously,
        // otherwise we reset to zero rotation.
        val rotation by animateFloatAsState(
            targetValue = if (isPlaying) 360f else 0.0f,
            animationSpec = repeatable(
                iterations = if (isPlaying) 10 else 1,
                tween(durationMillis = 3000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotation"
        )

        // Lower area containing the moving rectangle.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.7f)
                .background(Color.Red)
        ) {
            // Draw the rectangle.  We apply rotation and translation via
            // rotate() and offset().
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .offset(pOffset.x.dp, pOffset.y.dp)
                    .rotate(rotation)
                    .background(Color.Yellow)
            )
        }

        // Reset button.  Tapping this resets the drop direction which in turn
        // returns the rectangle to the centre of the screen.
        Button(
            onClick = {                dropDirection = 0
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "Reset", fontSize = 18.sp)
        }
    }
}
