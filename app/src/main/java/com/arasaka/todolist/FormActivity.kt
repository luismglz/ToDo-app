package com.arasaka.todolist

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.arasaka.todolist.MainActivity.Companion.NEW_TASK
import com.arasaka.todolist.MainActivity.Companion.NEW_TASK_KEY
import com.arasaka.todolist.MainActivity.Companion.UPDATE_TASK
import com.arasaka.todolist.Model.Task
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class FormActivity : AppCompatActivity() {

    private lateinit var edtTitle: EditText;
    private lateinit var edtDescription: EditText;
    private lateinit var edtTime: EditText;
    private lateinit var edtDate: EditText;
    private lateinit var btnAddTask: Button;

    private var isDetailTask = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        isDetailTask = intent.getBooleanExtra("isTaskDetail", false)

        initViews();
        if(isDetailTask) setTaskInfo(intent.getParcelableExtra("task")?: Task())
    }

    private fun setTaskInfo(task: Task) {
        edtTitle.setText(task.title)
        edtDescription.setText(task.description)
        edtDate.setText(task.dateTime?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
        edtTime.setText(task.dateTime?.format(DateTimeFormatter.ofPattern("HH:mm")))

        btnAddTask.text = getString(R.string.update_task)
    }


    //Add 'zero' to one digit numbers, this is how to comply with the date and time format.
    fun checkDigit(number: Int): String? {
        return if (number <= 9) "0$number" else number.toString()
    }

    private fun initViews(){
        edtTitle = findViewById(R.id.edtTitle);
        edtDescription = findViewById(R.id.edtDescription);
        edtTime = findViewById(R.id.edtTime);
        edtDate = findViewById(R.id.edtDate);
        btnAddTask = findViewById(R.id.btnAddTask);


        edtDate.setOnClickListener {
            val nowDate = LocalDate.now()

            val picker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val realMonth = month + 1;
                    //edtDate.setText("${if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth}/${if (realMonth < 10) "0$realMonth" else realMonth}/$year")
                    edtDate.setText("${checkDigit(dayOfMonth)}/${checkDigit(realMonth)}/$year")
                },
                nowDate.year,
                nowDate.monthValue - 1,
                nowDate.dayOfMonth
            )
            //picker.datePicker.minDate = System.currentTimeMillis()-1000
            picker.datePicker.minDate = Calendar.getInstance().timeInMillis
            picker.show()
        }


        edtTime.setOnClickListener {
            val nowTime = LocalTime.now()

            TimePickerDialog(this,
                { _, hour, minute ->
                    edtTime.setText("${checkDigit(hour)}:${checkDigit(minute)}")
                }, nowTime.hour,
                nowTime.minute,
                true
            ).show()
        }


        btnAddTask.setOnClickListener {

            if (edtTitle.text.isEmpty() || edtDescription.text.isEmpty() || edtDate.text.isEmpty() || edtTime.text.isEmpty()) {
                Toast.makeText(this, "Invalid field values", Toast.LENGTH_LONG).show()
                //finish()
            } else {
                setResult(
                    if (isDetailTask) UPDATE_TASK else NEW_TASK, Intent().putExtra(
                        NEW_TASK_KEY,
                        Task(
                            intent.getParcelableExtra<Task>("task")?.id ?: 0,
                            edtTitle.text.toString(),
                            edtDescription.text.toString(),
                            LocalDateTime.of(
                                LocalDate.parse(
                                    edtDate.text,
                                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                                ),
                                LocalTime.parse(edtTime.text, DateTimeFormatter.ofPattern("HH:mm"))
                            )
                        )
                    )
                )
                finish()
            }
        }



    }

}