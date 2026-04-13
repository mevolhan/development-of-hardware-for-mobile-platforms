package com.studymate.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.studymate.model.DeadlineBucket
import com.studymate.model.StudyTask
import com.studymate.model.TaskFilter
import com.studymate.ui.theme.StudyMateTheme

private enum class StudyMateScreen(
    val title: String,
    val icon: ImageVector
) {
    Dashboard("Главная", Icons.Outlined.Home),
    Tasks("Задачи", Icons.Outlined.CheckCircle),
    Stats("Статистика", Icons.Outlined.Assessment)
}

@Composable
fun StudyMateApp() {
    var currentScreen by remember { mutableStateOf(StudyMateScreen.Dashboard) }
    var currentFilter by remember { mutableStateOf(TaskFilter.All) }
    var showAddDialog by remember { mutableStateOf(false) }

    val tasks = remember {
        mutableStateListOf<StudyTask>().apply { addAll(sampleTasks()) }
    }
    val courses = remember { sampleCourses() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    currentScreen = StudyMateScreen.Tasks
                    showAddDialog = true
                },
                containerColor = Color(0xFF10203D),
                contentColor = Color.White,
                icon = { Icon(Icons.Outlined.Add, contentDescription = null) },
                text = { Text("Новая задача") }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f)
            ) {
                StudyMateScreen.entries.forEach { screen ->
                    NavigationBarItem(
                        selected = currentScreen == screen,
                        onClick = { currentScreen = screen },
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        StudyMateBackground {
            AnimatedContent(
                targetState = currentScreen,
                label = "screen_switcher",
                transitionSpec = {
                    androidx.compose.animation.fadeIn(
                        animationSpec = tween(350, easing = FastOutSlowInEasing)
                    ) togetherWith androidx.compose.animation.fadeOut(
                        animationSpec = tween(220)
                    )
                }
            ) { screen ->
                when (screen) {
                    StudyMateScreen.Dashboard -> DashboardScreen(
                        innerPadding = innerPadding,
                        tasks = tasks,
                        courses = courses,
                        onOpenTasks = { currentScreen = StudyMateScreen.Tasks }
                    )

                    StudyMateScreen.Tasks -> TasksScreen(
                        innerPadding = innerPadding,
                        tasks = filterTasks(tasks, currentFilter),
                        totalTaskCount = tasks.size,
                        selectedFilter = currentFilter,
                        onFilterSelected = { currentFilter = it },
                        onToggleTask = { taskId ->
                            val index = tasks.indexOfFirst { it.id == taskId }
                            if (index >= 0) {
                                val task = tasks[index]
                                tasks[index] = task.copy(completed = !task.completed)
                            }
                        }
                    )

                    StudyMateScreen.Stats -> StatsScreen(
                        innerPadding = innerPadding,
                        tasks = tasks,
                        courses = courses
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddTaskDialog(
            onDismiss = { showAddDialog = false },
            onAddTask = { title, subject, dueLabel, duration, priority, bucket ->
                val nextId = (tasks.maxOfOrNull { it.id } ?: 0) + 1
                tasks.add(
                    0,
                    StudyTask(
                        id = nextId,
                        title = title,
                        subject = subject,
                        dueLabel = dueLabel,
                        estimatedMinutes = duration,
                        priority = priority,
                        bucket = bucket,
                        completed = false
                    )
                )
                currentFilter = TaskFilter.All
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun StudyMateBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFF4EDE1),
                        Color(0xFFDDE8F5),
                        Color(0xFFF7F8FC)
                    ),
                    start = Offset.Zero,
                    end = Offset(1200f, 1800f)
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(0x33FF9B71),
                radius = size.minDimension * 0.28f,
                center = Offset(size.width * 0.15f, size.height * 0.12f)
            )
            drawCircle(
                color = Color(0x335A80FF),
                radius = size.minDimension * 0.34f,
                center = Offset(size.width * 0.88f, size.height * 0.25f)
            )
        }

        content()
    }
}

private fun filterTasks(
    tasks: List<StudyTask>,
    filter: TaskFilter
): List<StudyTask> = when (filter) {
    TaskFilter.All -> tasks
    TaskFilter.Today -> tasks.filter { it.bucket == DeadlineBucket.Today }
    TaskFilter.Active -> tasks.filterNot { it.completed }
    TaskFilter.Completed -> tasks.filter { it.completed }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun StudyMatePreview() {
    StudyMateTheme {
        StudyMateApp()
    }
}
