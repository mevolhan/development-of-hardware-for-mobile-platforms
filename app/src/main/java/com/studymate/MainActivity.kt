package com.studymate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.studymate.ui.StudyMateApp
import com.studymate.ui.theme.StudyMateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudyMateTheme {
                StudyMateApp()
            }
        }
    }
}
