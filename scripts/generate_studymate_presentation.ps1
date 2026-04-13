$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $PSScriptRoot
$assetsDir = Join-Path $projectRoot "presentation_assets"
$outputPath = Join-Path $projectRoot "StudyMate_Presentation.pptx"

$homeShot = Join-Path $assetsDir "studymate_home.png"
$tasksShot = Join-Path $assetsDir "studymate_tasks.png"
$statsShot = Join-Path $assetsDir "studymate_stats.png"
$addTaskShot = Join-Path $assetsDir "studymate_add_task.png"

foreach ($path in @($homeShot, $tasksShot, $statsShot, $addTaskShot)) {
    if (-not (Test-Path $path)) {
        throw "Missing asset: $path"
    }
}

function Get-OfficeRgb {
    param([string]$Hex)

    $clean = $Hex.TrimStart("#")
    $r = [Convert]::ToInt32($clean.Substring(0, 2), 16)
    $g = [Convert]::ToInt32($clean.Substring(2, 2), 16)
    $b = [Convert]::ToInt32($clean.Substring(4, 2), 16)
    return ($r + ($g * 256) + ($b * 65536))
}

$Accent = Get-OfficeRgb "81818B"
$White = Get-OfficeRgb "FFFFFF"

function Add-Textbox {
    param(
        [object]$Slide,
        [string]$Text,
        [double]$Left,
        [double]$Top,
        [double]$Width,
        [double]$Height,
        [int]$FontSize = 22,
        [bool]$Bold = $false,
        [int]$Color = 0,
        [string]$FontName = "Aptos"
    )

    $shape = $Slide.Shapes.AddTextbox(1, $Left, $Top, $Width, $Height)
    $range = $shape.TextFrame.TextRange
    $range.Text = $Text
    $range.Font.Size = $FontSize
    $range.Font.Name = $FontName
    $range.Font.Bold = [int]$Bold
    $range.Font.Color.RGB = $Color
    return $shape
}

function Add-BulletList {
    param(
        [object]$Slide,
        [string[]]$Items,
        [double]$Left,
        [double]$Top,
        [double]$Width,
        [double]$Height,
        [int]$FontSize = 20,
        [int]$Color = 0
    )

    $shape = $Slide.Shapes.AddTextbox(1, $Left, $Top, $Width, $Height)
    $textFrame = $shape.TextFrame
    $textFrame.WordWrap = -1
    $textFrame.TextRange.Text = ""

    for ($i = 0; $i -lt $Items.Count; $i++) {
        if ($i -eq 0) {
            $paragraph = $textFrame.TextRange.Paragraphs(1)
        } else {
            $paragraph = $textFrame.TextRange.InsertAfter("`r").Paragraphs($i + 1)
        }

        $paragraph.Text = $Items[$i]
        $paragraph.ParagraphFormat.Bullet.Visible = -1
        $paragraph.ParagraphFormat.Bullet.Character = 8226
        $paragraph.ParagraphFormat.SpaceAfter = 8
        $paragraph.Font.Size = $FontSize
        $paragraph.Font.Name = "Aptos"
        $paragraph.Font.Color.RGB = $Color
    }

    return $shape
}

function Add-Card {
    param(
        [object]$Slide,
        [double]$Left,
        [double]$Top,
        [double]$Width,
        [double]$Height,
        [int]$FillColor = 0,
        [int]$LineColor = 0
    )

    $shape = $Slide.Shapes.AddShape(1, $Left, $Top, $Width, $Height)
    $shape.Fill.ForeColor.RGB = $FillColor
    $shape.Line.Visible = -1
    $shape.Line.ForeColor.RGB = $LineColor
    $shape.Line.Weight = 1.2
    return $shape
}

function Add-Title {
    param(
        [object]$Slide,
        [string]$Title,
        [string]$Subtitle
    )

    Add-Textbox -Slide $Slide -Text $Title -Left 32 -Top 20 -Width 860 -Height 36 -FontSize 26 -Bold $true -Color $Accent | Out-Null
    Add-Textbox -Slide $Slide -Text $Subtitle -Left 32 -Top 56 -Width 860 -Height 24 -FontSize 13 -Color $Accent | Out-Null
}

$ppt = New-Object -ComObject PowerPoint.Application
$ppt.Visible = -1

try {
    $presentation = $ppt.Presentations.Add()
    $presentation.PageSetup.SlideWidth = 960
    $presentation.PageSetup.SlideHeight = 540

    $slide1 = $presentation.Slides.Add(1, 12)
    $slide1.FollowMasterBackground = 0
    $slide1.Background.Fill.ForeColor.RGB = $White
    $titleCard = Add-Card -Slide $slide1 -Left 32 -Top 28 -Width 430 -Height 210 -FillColor $White -LineColor $Accent
    Add-Textbox -Slide $slide1 -Text "StudyMate" -Left 58 -Top 54 -Width 220 -Height 40 -FontSize 30 -Bold $true -Color $Accent | Out-Null
    Add-Textbox -Slide $slide1 -Text "Мобильное приложение для управления учебными задачами" -Left 58 -Top 98 -Width 350 -Height 70 -FontSize 24 -Bold $true -Color $Accent | Out-Null
    Add-Textbox -Slide $slide1 -Text "Презентация проекта" -Left 58 -Top 180 -Width 220 -Height 24 -FontSize 16 -Color $Accent | Out-Null
    Add-Textbox -Slide $slide1 -Text "Kotlin | Jetpack Compose | Android" -Left 58 -Top 204 -Width 280 -Height 22 -FontSize 13 -Color $Accent | Out-Null
    $slide1.Shapes.AddPicture($homeShot, 0, -1, 610, 34, 215, 466) | Out-Null
    Add-Textbox -Slide $slide1 -Text "Назначение приложения: помочь студенту держать под контролем дедлайны, задачи и прогресс по учебным направлениям." -Left 32 -Top 278 -Width 540 -Height 66 -FontSize 18 -Color $Accent | Out-Null

    $slide2 = $presentation.Slides.Add(2, 12)
    $slide2.Background.Fill.ForeColor.RGB = $White
    Add-Title -Slide $slide2 -Title "Что представляет собой приложение" -Subtitle "Краткий обзор идеи проекта"
    Add-Card -Slide $slide2 -Left 32 -Top 96 -Width 420 -Height 300 -FillColor $White -LineColor $Accent | Out-Null
    Add-Card -Slide $slide2 -Left 480 -Top 96 -Width 448 -Height 300 -FillColor $White -LineColor $Accent | Out-Null
    Add-Textbox -Slide $slide2 -Text "Идея" -Left 56 -Top 120 -Width 120 -Height 26 -FontSize 22 -Bold $true -Color $Accent | Out-Null
    Add-BulletList -Slide $slide2 -Items @(
        "StudyMate - это учебный планировщик для студента.",
        "Он объединяет задачи, дедлайны, прогресс и простую аналитику.",
        "Приложение рассчитано на быстрый ежедневный контроль учебной нагрузки."
    ) -Left 56 -Top 160 -Width 372 -Height 210 -FontSize 20 -Color $Accent | Out-Null
    Add-Textbox -Slide $slide2 -Text "Сценарий использования" -Left 504 -Top 120 -Width 250 -Height 26 -FontSize 22 -Bold $true -Color $Accent | Out-Null
    Add-BulletList -Slide $slide2 -Items @(
        "Пользователь открывает приложение и сразу видит текущую учебную картину.",
        "Далее он просматривает задачи, отмечает выполненные и добавляет новые.",
        "На экране статистики можно оценить темп выполнения и приоритеты."
    ) -Left 504 -Top 160 -Width 392 -Height 210 -FontSize 20 -Color $Accent | Out-Null

    $slide3 = $presentation.Slides.Add(3, 12)
    $slide3.Background.Fill.ForeColor.RGB = $White
    Add-Title -Slide $slide3 -Title "Средства разработки" -Subtitle "Технологии, использованные в проекте"
    Add-Card -Slide $slide3 -Left 32 -Top 100 -Width 896 -Height 296 -FillColor $White -LineColor $Accent | Out-Null
    Add-BulletList -Slide $slide3 -Items @(
        "Среда разработки: IntelliJ IDEA с поддержкой Android.",
        "Язык программирования: Kotlin.",
        "Построение интерфейса: Jetpack Compose и Material 3.",
        "Система сборки: Gradle Kotlin DSL.",
        "Тестирование и запуск: Android Emulator и ADB.",
        "Минимальная поддерживаемая версия Android: API 24."
    ) -Left 60 -Top 130 -Width 840 -Height 230 -FontSize 24 -Color $Accent | Out-Null

    $slide4 = $presentation.Slides.Add(4, 12)
    $slide4.Background.Fill.ForeColor.RGB = $White
    Add-Title -Slide $slide4 -Title "Главный экран" -Subtitle "Обзор текущего состояния учебы"
    $slide4.Shapes.AddPicture($homeShot, 0, -1, 36, 92, 214, 430) | Out-Null
    Add-Card -Slide $slide4 -Left 280 -Top 108 -Width 640 -Height 292 -FillColor $White -LineColor $Accent | Out-Null
    Add-BulletList -Slide $slide4 -Items @(
        "Отображает сводную информацию о количестве активных и выполненных задач.",
        "Показывает блок Фокус дня с наиболее важной задачей.",
        "Содержит прогресс по учебным направлениям и ближайшие задачи.",
        "Через нижнюю навигацию можно перейти к задачам и статистике."
    ) -Left 308 -Top 136 -Width 590 -Height 210 -FontSize 22 -Color $Accent | Out-Null
    Add-Textbox -Slide $slide4 -Text "Этот экран выступает как стартовая панель: пользователь сразу понимает, что важно именно сейчас." -Left 308 -Top 346 -Width 580 -Height 40 -FontSize 18 -Color $Accent | Out-Null

    $slide5 = $presentation.Slides.Add(5, 12)
    $slide5.Background.Fill.ForeColor.RGB = $White
    Add-Title -Slide $slide5 -Title "Работа с задачами" -Subtitle "Список задач и добавление новой записи"
    $slide5.Shapes.AddPicture($tasksShot, 0, -1, 36, 92, 184, 430) | Out-Null
    $slide5.Shapes.AddPicture($addTaskShot, 0, -1, 236, 92, 184, 430) | Out-Null
    Add-Card -Slide $slide5 -Left 446 -Top 110 -Width 476 -Height 292 -FillColor $White -LineColor $Accent | Out-Null
    Add-BulletList -Slide $slide5 -Items @(
        "Экран задач поддерживает фильтры: все, сегодня, активные и готово.",
        "Нажатием по карточке задача отмечается выполненной.",
        "Кнопка Новая задача открывает отдельную форму добавления.",
        "Для новой задачи можно указать название, учебный блок, дедлайн, длительность и приоритет."
    ) -Left 470 -Top 138 -Width 430 -Height 220 -FontSize 19 -Color $Accent | Out-Null

    $slide6 = $presentation.Slides.Add(6, 12)
    $slide6.Background.Fill.ForeColor.RGB = $White
    Add-Title -Slide $slide6 -Title "Экран статистики" -Subtitle "Простая аналитика по учебной нагрузке"
    $slide6.Shapes.AddPicture($statsShot, 0, -1, 36, 92, 214, 430) | Out-Null
    Add-Card -Slide $slide6 -Left 280 -Top 108 -Width 640 -Height 292 -FillColor $White -LineColor $Accent | Out-Null
    Add-BulletList -Slide $slide6 -Items @(
        "Показывает общий процент завершения задач.",
        "Отдельно считает срочные задачи и задачи на сегодня.",
        "Оценивает оставшуюся нагрузку по времени.",
        "Разбивает задачи по приоритетам: высокий, средний и низкий."
    ) -Left 308 -Top 136 -Width 590 -Height 210 -FontSize 22 -Color $Accent | Out-Null
    Add-Textbox -Slide $slide6 -Text "Такой экран помогает быстро понять, насколько равномерно распределена учебная нагрузка." -Left 308 -Top 346 -Width 580 -Height 40 -FontSize 18 -Color $Accent | Out-Null

    $slide7 = $presentation.Slides.Add(7, 12)
    $slide7.Background.Fill.ForeColor.RGB = $White
    Add-Title -Slide $slide7 -Title "Что умеет приложение" -Subtitle "Основные возможности текущей версии"
    Add-Card -Slide $slide7 -Left 32 -Top 96 -Width 896 -Height 308 -FillColor $White -LineColor $Accent | Out-Null
    Add-BulletList -Slide $slide7 -Items @(
        "Хранит и отображает список учебных задач в рамках текущего запуска приложения.",
        "Позволяет добавлять новые задачи с ключевыми параметрами.",
        "Поддерживает приоритеты и группы по срокам выполнения.",
        "Фильтрует задачи по состоянию и актуальности.",
        "Показывает прогресс по учебным направлениям.",
        "Дает краткую статистику по выполнению и временной нагрузке."
    ) -Left 58 -Top 128 -Width 842 -Height 236 -FontSize 23 -Color $Accent | Out-Null

    $slide8 = $presentation.Slides.Add(8, 12)
    $slide8.Background.Fill.ForeColor.RGB = $White
    Add-Title -Slide $slide8 -Title "Итог и перспективы" -Subtitle "Что можно развивать дальше"
    Add-BulletList -Slide $slide8 -Items @(
        "Проект уже представляет собой готовый мобильный прототип с современным интерфейсом.",
        "Приложение решает понятную задачу: помогает организовать учебный процесс.",
        "В дальнейшем можно добавить базу данных Room для постоянного хранения задач.",
        "Также можно реализовать уведомления, календарь и синхронизацию с облаком."
    ) -Left 46 -Top 112 -Width 870 -Height 214 -FontSize 24 -Color $Accent | Out-Null
    Add-Card -Slide $slide8 -Left 46 -Top 314 -Width 870 -Height 88 -FillColor $White -LineColor $Accent | Out-Null
    Add-Textbox -Slide $slide8 -Text "StudyMate - это наглядный и понятный учебный помощник, который можно расширить в полноценное Android-приложение." -Left 72 -Top 338 -Width 820 -Height 34 -FontSize 22 -Bold $true -Color $Accent | Out-Null

    $presentation.SaveAs($outputPath)
    $presentation.Close()
}
finally {
    $ppt.Quit()
    [System.Runtime.Interopservices.Marshal]::ReleaseComObject($ppt) | Out-Null
}
