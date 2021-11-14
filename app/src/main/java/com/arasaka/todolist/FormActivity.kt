package com.arasaka.todolist

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.arasaka.todolist.MainActivity.Companion.NEW_TASK
import com.arasaka.todolist.MainActivity.Companion.NEW_TASK_KEY
import java.lang.String.format
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.time.format.DateTimeFormatter
import java.util.*

class FormActivity : AppCompatActivity() {

    private lateinit var edtTitle: EditText;
    private lateinit var edtDescription: EditText;
    private lateinit var edtTime: EditText;
    private lateinit var edtDate: EditText;
    private lateinit var btnAddTask: Button;



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        initViews();
    }

    private fun initViews(){
        edtTitle = findViewById(R.id.edtTitle);
        edtDescription = findViewById(R.id.edtDescription);
        edtTime = findViewById(R.id.edtTime);
        edtDate = findViewById(R.id.edtDate);
        btnAddTask = findViewById(R.id.btnAddTask);


        edtDate.setOnClickListener {
            val nowDate = LocalDate.now()
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth -> edtDate.setText("$dayOfMonth/$month/$year").toString() },
                nowDate.year,
                nowDate.monthValue-1 ,
                nowDate.dayOfMonth
            ).show()
        }


        edtTime.setOnClickListener {
            val nowTime = LocalTime.now()

            TimePickerDialog(this,
                { _, hour, minute ->
                    edtTime.setText("$hour:$minute")
                }, nowTime.hour, nowTime.minute, false
            ).show()
        }

        btnAddTask.setOnClickListener {
            setResult(
                NEW_TASK, Intent().putExtra(
                    NEW_TASK_KEY,
                    Task(
                        0,
                        edtTitle.text.toString(),
                        edtDescription.text.toString(),
                        LocalDateTime.of(
                            LocalDate.parse(edtDate.text, DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            LocalTime.parse(edtTime.text, DateTimeFormatter.ofPattern("hh:mm"))
                        )
                    )
                )
            )
        }
    }
}