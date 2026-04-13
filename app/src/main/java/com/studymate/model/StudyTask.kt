package com.studymate.model

data class StudyTask(
    val id: Int,
    val title: String,
    val subject: String,
    val dueLabel: String,
    val estimatedMinutes: Int,
    val priority: TaskPriority,
    val bucket: DeadlineBucket,
    val completed: Boolean
)

data class CourseProgress(
    val title: String,
    val completedPercent: Int,
    val accent: Long
)

enum class TaskPriority(
    val label: String,
    val accent: Long
) {
    High("Высокий", 0xFFD9485F),
    Medium("Средний", 0xFFD97A2B),
    Low("Низкий", 0xFF2E7D6C)
}

enum class DeadlineBucket(val label: String) {
    Today("Сегодня"),
    Tomorrow("Завтра"),
    ThisWeek("На неделе"),
    Later("Позже")
}

enum class TaskFilter(val label: String) {
    All("Все"),
    Today("Сегодня"),
    Active("Активные"),
    Completed("Готово")
}
