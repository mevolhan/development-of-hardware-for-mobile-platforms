package com.studymate.ui

import com.studymate.model.CourseProgress
import com.studymate.model.DeadlineBucket
import com.studymate.model.StudyTask
import com.studymate.model.TaskPriority

internal fun sampleTasks(): List<StudyTask> = listOf(
    StudyTask(
        id = 1,
        title = "Собрать макет экрана профиля",
        subject = "UI/UX",
        dueLabel = "Сегодня, 18:00",
        estimatedMinutes = 45,
        priority = TaskPriority.High,
        bucket = DeadlineBucket.Today,
        completed = false
    ),
    StudyTask(
        id = 2,
        title = "Подготовить UML-диаграмму",
        subject = "Анализ",
        dueLabel = "Завтра, 12:30",
        estimatedMinutes = 35,
        priority = TaskPriority.Medium,
        bucket = DeadlineBucket.Tomorrow,
        completed = true
    ),
    StudyTask(
        id = 3,
        title = "Написать описание проекта",
        subject = "Документация",
        dueLabel = "14 апреля",
        estimatedMinutes = 25,
        priority = TaskPriority.High,
        bucket = DeadlineBucket.ThisWeek,
        completed = false
    ),
    StudyTask(
        id = 4,
        title = "Проверить навигацию в приложении",
        subject = "Тестирование",
        dueLabel = "15 апреля",
        estimatedMinutes = 30,
        priority = TaskPriority.Low,
        bucket = DeadlineBucket.ThisWeek,
        completed = false
    )
)

internal fun sampleCourses(): List<CourseProgress> = listOf(
    CourseProgress("Мобильная разработка", 78, 0xFF2E7D6C),
    CourseProgress("UI-прототипирование", 54, 0xFFD97A2B),
    CourseProgress("Проектная защита", 66, 0xFF4B6BFB)
)
