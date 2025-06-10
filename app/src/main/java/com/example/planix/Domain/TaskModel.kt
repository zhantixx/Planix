package com.example.planix.Domain

import java.io.Serializable
import java.time.LocalDateTime

data class TaskModel(
    val id: Int,
    val title: String,
    val description: String,
    val deadline: LocalDateTime,
    val priority: PriorityLevel,
    val category: TaskCategory,
    val status: TaskStatus
): Serializable

enum class PriorityLevel(val displayName: String) {
    TOMENGI("Tömen"),
    ORTASA("Ortaşa"),
    JOGARGY("Joğarğy"),
    KUNDELIKTI("Kundelikti")
}

enum class TaskCategory(val displayName: String) {
    BILIM("Bilim"),
    JUMYS("Jūmys"),
    JEKE("Jeke"),
    BASQA("Basqa")
}

enum class TaskStatus(val displayName: String) {
    JOSPARLANGAN("Josparlangan"),
    ORYNDALGAN("Oryndalgan"),
    JOYULGAN("Joyulgan")
}
