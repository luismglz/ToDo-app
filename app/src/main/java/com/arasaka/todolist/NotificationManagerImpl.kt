package com.arasaka.todolist

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.ListenableWorker.Result.failure
import androidx.work.ListenableWorker.Result.success
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.arasaka.todolist.Adapter.TasksAdapter
import com.arasaka.todolist.MainActivity.Companion.UPDATE_TASK
import com.arasaka.todolist.Model.LocalDateTimeConverter
import com.arasaka.todolist.Model.Task
import com.arasaka.todolist.Model.TaskDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class NotificationManagerImpl(private val context: Context, params:WorkerParameters): Worker(context,params) {

    override fun doWork(): Result {
        val id = inputData.getInt("notificationID",0)
        val title = inputData.getString("notificationTitle") ?:""
        val description = inputData.getString("notificationDescription")?:""
       // val status = inputData.getBoolean("notificationStatus",true)
        val date = inputData.getString("notificationDateTime")?: ""

        createNotification(Task(id,title,description, LocalDateTimeConverter.toDateTime(date)))
        return success()

    }



    private fun createNotification(task:Task){
        val resultIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("isTaskDetail",true)
            putExtra("task",task)
        }

        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            // Add the intent, which inflates the backstack
            addNextIntentWithParentStack(resultIntent)


            // Get the PendingIntent containing the entire backstack
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val builder = NotificationCompat.Builder(context, "TASK_CHANNEL")
            .setSmallIcon(R.drawable.ic_clock)
            .setContentTitle(task.title)
            .setContentText(task.description)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setContentIntent(resultPendingIntent)

        with(NotificationManagerCompat.from(context)){
            notify(task.id, builder.build())
        }

    }
}