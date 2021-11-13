package com.arasaka.todolist

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val dateTime: LocalDateTime
):Parcelable
