package edu.farmingdale.draganddropanim_demo

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import edu.farmingdale.draganddropanim_demo.ui.theme.DragAndDropAnim_DemoTheme




class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        // Enable drawing behind the system bars.
        enableEdgeToEdge()

        // Set the composable content of the activity.
        setContent {
            DragAndDropAnim_DemoTheme {
                // Display our interactive drag‑and‑drop UI.
                DragAndDropBoxes()
            }
        }
    }
}