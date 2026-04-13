package com.studymate.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.studymate.model.CourseProgress
import com.studymate.model.DeadlineBucket
import com.studymate.model.StudyTask
import com.studymate.model.TaskFilter
import com.studymate.model.TaskPriority

@Composable
internal fun DashboardScreen(
    innerPadding: PaddingValues,
    tasks: List<StudyTask>,
    courses: List<CourseProgress>,
    onOpenTasks: () -> Unit
) {
    val completedTasks = tasks.count { it.completed }
    val activeTasks = tasks.size - completedTasks
    val focusMinutes = tasks.filterNot { it.completed }.sumOf { it.estimatedMinutes }
    val todayTasks = tasks.count { it.bucket == DeadlineBucket.Today && !it.completed }
    val completionRate = if (tasks.isEmpty()) 0 else (completedTasks * 100) / tasks.size
    val topTask = tasks
        .filterNot { it.completed }
        .sortedWith(compareBy<StudyTask>({ deadlineRank(it.bucket) }, { priorityRank(it.priority) }))
        .firstOrNull()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 20.dp,
            top = innerPadding.calculateTopPadding() + 20.dp,
            end = 20.dp,
            bottom = innerPadding.calculateBottomPadding() + 88.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            HeroCard(
                title = "Твой учебный ритм",
                subtitle = "StudyMate",
                body = "Сейчас в работе $activeTasks задач, выполнено $completedTasks. Темп недели: $completionRate%."
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MiniStatCard(
                    modifier = Modifier.weight(1f),
                    title = "Сегодня",
                    value = todayTasks.toString(),
                    tint = Color(0xFF335CFF),
                    icon = Icons.Outlined.CalendarMonth
                )
                MiniStatCard(
                    modifier = Modifier.weight(1f),
                    title = "Фокус",
                    value = "$focusMinutes мин",
                    tint = Color(0xFFD97706),
                    icon = Icons.Outlined.AutoAwesome
                )
            }
        }

        if (topTask != null) {
            item {
                FocusTaskCard(task = topTask, onOpenTasks = onOpenTasks)
            }
        }

        item {
            SectionTitle("Прогресс по направлениям")
        }

        items(courses) { course ->
            ProgressCard(course = course)
        }

        item {
            SectionTitle("Ближайшие задачи")
        }

        items(
            tasks
                .filterNot { it.completed }
                .sortedWith(compareBy<StudyTask>({ deadlineRank(it.bucket) }, { priorityRank(it.priority) }))
                .take(3)
        ) { task ->
            TaskCard(task = task, onClick = null)
        }
    }
}

@Composable
internal fun TasksScreen(
    innerPadding: PaddingValues,
    tasks: List<StudyTask>,
    totalTaskCount: Int,
    selectedFilter: TaskFilter,
    onFilterSelected: (TaskFilter) -> Unit,
    onToggleTask: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 20.dp,
            top = innerPadding.calculateTopPadding() + 20.dp,
            end = 20.dp,
            bottom = innerPadding.calculateBottomPadding() + 88.dp
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SectionHeader(
                title = "Мои задачи",
                subtitle = "Нажми на карточку, чтобы отметить выполнение"
            )
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(TaskFilter.entries) { filter ->
                    TaskFilterChip(
                        filter = filter,
                        selected = selectedFilter == filter,
                        onClick = { onFilterSelected(filter) }
                    )
                }
            }
        }

        item {
            CaptionLine("Показано ${tasks.size} из $totalTaskCount задач")
        }

        if (tasks.isEmpty()) {
            item {
                EmptyStateCard()
            }
        } else {
            items(
                tasks.sortedWith(
                    compareBy<StudyTask>({ it.completed }, { deadlineRank(it.bucket) }, { priorityRank(it.priority) })
                )
            ) { task ->
                TaskCard(
                    task = task,
                    onClick = { onToggleTask(task.id) }
                )
            }
        }
    }
}

@Composable
internal fun StatsScreen(
    innerPadding: PaddingValues,
    tasks: List<StudyTask>,
    courses: List<CourseProgress>
) {
    val completed = tasks.count { it.completed }
    val completionRate = if (tasks.isEmpty()) 0 else (completed * 100) / tasks.size
    val focusMinutes = tasks.filterNot { it.completed }.sumOf { it.estimatedMinutes }
    val averageProgress = courses.map { it.completedPercent }.average().toInt()
    val highPriorityPending = tasks.count { !it.completed && it.priority == TaskPriority.High }
    val todayPending = tasks.count { !it.completed && it.bucket == DeadlineBucket.Today }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 20.dp,
            top = innerPadding.calculateTopPadding() + 20.dp,
            end = 20.dp,
            bottom = innerPadding.calculateBottomPadding() + 88.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionHeader(
                title = "Статистика недели",
                subtitle = "Короткий срез по прогрессу и нагрузке"
            )
        }

        item {
            HeroCard(
                title = "$completionRate% завершения",
                subtitle = "Средний темп",
                body = "Средний прогресс по учебным направлениям сейчас составляет $averageProgress%."
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MiniStatCard(
                    modifier = Modifier.weight(1f),
                    title = "Срочно",
                    value = highPriorityPending.toString(),
                    tint = Color(0xFFD9485F),
                    icon = Icons.Outlined.Flag
                )
                MiniStatCard(
                    modifier = Modifier.weight(1f),
                    title = "Сегодня",
                    value = todayPending.toString(),
                    tint = Color(0xFF335CFF),
                    icon = Icons.Outlined.DoneAll
                )
            }
        }

        item {
            SummaryCard(
                title = "Нагрузка по времени",
                value = "$focusMinutes минут",
                caption = "Столько времени нужно, чтобы закрыть все активные задачи"
            )
        }

        item {
            SectionTitle("Распределение по приоритетам")
        }

        items(TaskPriority.entries) { priority ->
            PriorityBreakdownCard(
                priority = priority,
                total = tasks.count { it.priority == priority },
                completed = tasks.count { it.priority == priority && it.completed }
            )
        }
    }
}

internal fun priorityRank(priority: TaskPriority): Int = when (priority) {
    TaskPriority.High -> 0
    TaskPriority.Medium -> 1
    TaskPriority.Low -> 2
}

internal fun deadlineRank(bucket: DeadlineBucket): Int = when (bucket) {
    DeadlineBucket.Today -> 0
    DeadlineBucket.Tomorrow -> 1
    DeadlineBucket.ThisWeek -> 2
    DeadlineBucket.Later -> 3
}
