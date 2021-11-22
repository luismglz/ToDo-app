package com.arasaka.todolist

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.arasaka.todolist.Adapter.TasksAdapter
import com.arasaka.todolist.Model.Task
import com.arasaka.todolist.Model.TaskDatabase
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.Month
import java.time.OffsetDateTime
import java.util.ArrayList
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    //companion = static, Therefore it could be used in other classes without creating an instance
    companion object{
        val NEW_TASK = 200
        val UPDATE_TASK = 201

        val NEW_TASK_KEY = "newTask"
    }

    private lateinit var rcvTask : RecyclerView
    private lateinit var btnAddTask : FloatingActionButton
    private lateinit var adapter : TasksAdapter
    private val SAVED_TASKS_KEY = "task"

    private var tasks = mutableListOf<Task>()

    private lateinit var db : TaskDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        savedInstanceState?.let {
            val savedTasks = it.getParcelableArrayList<Task>(SAVED_TASKS_KEY)?.toMutableList() ?: tasks
            tasks = savedTasks
        }

        createNotificationChannel()
        initViews();

    }

    override fun onResume() {
        super.onResume()
        db = Room.databaseBuilder(this, TaskDatabase::class.java, "Tasks").build()

        MainScope().launch {
            tasks = db.taskDao().getPendingTasks().toMutableList()
            setAdapter();
        }
    }


    private fun initViews() {
        rcvTask = findViewById(R.id.rcvTasks)
        btnAddTask = findViewById(R.id.btnAddTask)

        btnAddTask.setOnClickListener {
            //  adapter.add(Task(1, "New task", "Description1", LocalDateTime.of(2021, Month.AUGUST, 6, 10, 40)))

            startActivityForResult(Intent(this, FormActivity::class.java), NEW_TASK)
        }

    }


    private fun setAdapter() {
        adapter = TasksAdapter(tasks,
            onClickDoneTask = { task, position ->
                MainScope().launch {
                    db.taskDao().updateTask(task.apply {
                        status = false
                    })
                    adapter.remove(position)
                }
            },
            onClickDetailTask = { task ->
                startActivityForResult(Intent(this, FormActivity::class.java).apply {
                    putExtra("isTaskDetail", true)
                    putExtra("task", task)
                }, UPDATE_TASK)
            })

        rcvTask.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rcvTask.adapter = adapter;
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.apply {
            putParcelableArrayList(SAVED_TASKS_KEY, tasks as ArrayList<Task>)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //check the "response" code
        if (requestCode == NEW_TASK) {

            data?.getParcelableExtra<Task>(NEW_TASK_KEY)?.let {
                MainScope().launch(Dispatchers.Main) {
                    adapter.add(it);

                }

                MainScope().launch(Dispatchers.IO) {
                    db.taskDao().saveNewTask(it);

                    val zone = OffsetDateTime.now().offset
                    val selectedMillis = it.dateTime?.toInstant(zone)?.toEpochMilli() ?: 0
                    val nowMillis = LocalDateTime.now().toInstant(zone).toEpochMilli()

                    scheduleNotification(selectedMillis - nowMillis, Data.Builder().apply{
                        putInt("notificationID",it.id)
                        putString("notificationTitle", it.title)
                        putString("notificationDescription", it.description)
                    }.build())
                }
            }

        } else if (requestCode == UPDATE_TASK) {
            data?.getParcelableExtra<Task>(NEW_TASK_KEY)?.let {
                MainScope().launch(Dispatchers.Main) {
                    adapter.update(it)
                }

                MainScope().launch(Dispatchers.IO) {
                    db.taskDao().updateTask(it)
                }
            }
        }


    }





    private fun scheduleNotification(delay: Long, data:Data){

        val notificationWork = OneTimeWorkRequest.Builder(NotificationManagerImpl::class.java)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS).setInputData(data).build()

        val instanceWorkManager = WorkManager.getInstance(this)
        instanceWorkManager.beginUniqueWork(
            "NOTIFICATION_WORK ${data.getInt("notificationID",taskId)}",
            ExistingWorkPolicy.APPEND_OR_REPLACE, notificationWork
        ).enqueue()
    }


    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "TASKS"
            val descriptionText = "Channel of pending tasks"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("TASK_CHANNEL",name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel);
        }
    }


}