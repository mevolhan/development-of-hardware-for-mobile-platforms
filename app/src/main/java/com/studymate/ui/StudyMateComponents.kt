package com.studymate.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.studymate.model.CourseProgress
import com.studymate.model.DeadlineBucket
import com.studymate.model.StudyTask
import com.studymate.model.TaskFilter
import com.studymate.model.TaskPriority

@Composable
internal fun AddTaskDialog(
    onDismiss: () -> Unit,
    onAddTask: (
        title: String,
        subject: String,
        dueLabel: String,
        duration: Int,
        priority: TaskPriority,
        bucket: DeadlineBucket
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("Мобильная разработка") }
    var dueLabel by remember { mutableStateOf("Сегодня, 19:00") }
    var duration by remember { mutableStateOf("30") }
    var priority by remember { mutableStateOf(TaskPriority.Medium) }
    var bucket by remember { mutableStateOf(DeadlineBucket.Today) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Новая задача",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Название") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Предмет или блок") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = dueLabel,
                    onValueChange = { dueLabel = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Дедлайн") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it.filter(Char::isDigit) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Длительность, мин") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                CaptionLine("Приоритет")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(TaskPriority.entries) { item ->
                        FilterChip(
                            selected = priority == item,
                            onClick = { priority = item },
                            label = { Text(item.label) }
                        )
                    }
                }

                CaptionLine("Когда выполнить")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(DeadlineBucket.entries) { item ->
                        FilterChip(
                            selected = bucket == item,
                            onClick = {
                                bucket = item
                                if (dueLabel.isBlank() || dueLabel == "Сегодня, 19:00") {
                                    dueLabel = when (item) {
                                        DeadlineBucket.Today -> "Сегодня, 19:00"
                                        DeadlineBucket.Tomorrow -> "Завтра, 12:00"
                                        DeadlineBucket.ThisWeek -> "На этой неделе"
                                        DeadlineBucket.Later -> "Позже"
                                    }
                                }
                            },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onAddTask(
                        title.trim(),
                        subject.trim().ifBlank { "Учеба" },
                        dueLabel.trim().ifBlank { bucket.label },
                        duration.toIntOrNull()?.coerceIn(10, 240) ?: 30,
                        priority,
                        bucket
                    )
                },
                enabled = title.isNotBlank()
            ) {
                Text("Добавить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

@Composable
internal fun HeroCard(
    title: String,
    subtitle: String,
    body: String
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF101A33))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0x22FFFFFF))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFFF8D7B5)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFFD7DEEF)
            )
        }
    }
}

@Composable
internal fun FocusTaskCard(
    task: StudyTask,
    onOpenTasks: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenTasks() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.84f))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Фокус дня",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = Icons.Outlined.School,
                    contentDescription = null,
                    tint = Color(0xFF335CFF)
                )
            }
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF10203D)
            )
            Text(
                text = "${task.subject} | ${task.dueLabel}",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF59657A)
            )
            Text(
                text = "Приоритет: ${task.priority.label} | ${task.estimatedMinutes} мин",
                style = MaterialTheme.typography.labelLarge,
                color = Color(task.priority.accent)
            )
        }
    }
}

@Composable
internal fun MiniStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    tint: Color,
    icon: ImageVector
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.78f))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                modifier = Modifier.size(34.dp),
                shape = CircleShape,
                color = tint.copy(alpha = 0.14f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = tint
                    )
                }
            }
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
internal fun ProgressCard(course: CourseProgress) {
    val progress = course.completedPercent / 100f

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = course.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${course.completedPercent}%",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(course.accent)
                )
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(CircleShape),
                color = Color(course.accent),
                trackColor = Color(0xFFE9EDF5)
            )
        }
    }
}

@Composable
internal fun TaskCard(
    task: StudyTask,
    onClick: (() -> Unit)?
) {
    val targetColor = if (task.completed) Color(0xFFE6F6EF) else Color.White.copy(alpha = 0.82f)
    val containerColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(300),
        label = "task_color"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = if (task.completed) Color(0xFF1E8E63) else Color(0xFFE8ECF5)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (task.completed) Icons.Outlined.CheckCircle else Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = if (task.completed) Color.White else Color(0xFF5E6780)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${task.subject} | ${task.dueLabel}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PriorityBadge(priority = task.priority)
                    Text(
                        text = "${task.estimatedMinutes} мин",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFF59657A)
                    )
                }
            }
        }
    }
}

@Composable
internal fun PriorityBadge(priority: TaskPriority) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(Color(priority.accent).copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = priority.label,
            style = MaterialTheme.typography.labelLarge,
            color = Color(priority.accent)
        )
    }
}

@Composable
internal fun SummaryCard(
    title: String,
    value: String,
    caption: String
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.82f))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF10203D)
            )
            Text(
                text = caption,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF59657A)
            )
        }
    }
}

@Composable
internal fun PriorityBreakdownCard(
    priority: TaskPriority,
    total: Int,
    completed: Int
) {
    val progress = if (total == 0) 0f else completed.toFloat() / total.toFloat()

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.82f))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = priority.label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "$completed / $total",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(priority.accent)
                )
            }
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = Color(priority.accent),
                trackColor = Color(0xFFE9EDF5)
            )
        }
    }
}

@Composable
internal fun EmptyStateCard() {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.82f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Outlined.DoneAll,
                contentDescription = null,
                tint = Color(0xFF335CFF),
                modifier = Modifier.size(34.dp)
            )
            Text(
                text = "По этому фильтру задач пока нет",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Нажми на кнопку \"Новая задача\", чтобы добавить следующий шаг.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF59657A),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
internal fun TaskFilterChip(
    filter: TaskFilter,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(filter.label) }
    )
}

@Composable
internal fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF10203D)
    )
}

@Composable
internal fun SectionHeader(
    title: String,
    subtitle: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF10203D)
        )
        CaptionLine(subtitle)
    }
}

@Composable
internal fun CaptionLine(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = Color(0xFF59657A)
    )
}
