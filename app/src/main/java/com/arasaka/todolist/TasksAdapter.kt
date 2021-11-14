package com.arasaka.todolist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import java.time.format.DateTimeFormatter

class TasksAdapter(val list: MutableList<Task>) :
    RecyclerView.Adapter<TasksAdapter.TaskViewHolder>() {


    fun add(task: Task) {
        list.add(task)
        notifyItemInserted(list.size -1)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksAdapter.TaskViewHolder {
        return TaskViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.task_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TasksAdapter.TaskViewHolder, position: Int) {
        //(holder as TaskViewHolder).bind(list[position])
        holder.bind(list[position], position)

    }

    override fun getItemCount() = list.size

    inner class TaskViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(data: Task, position: Int) = view.apply {
            val txvTitle: TextView = findViewById(R.id.txvTitleTask)
            val txvDateTime: TextView = findViewById(R.id.txvDateTime)
            val chkCompleted: MaterialCheckBox = findViewById(R.id.chkCompleted)

            txvTitle.text = data.title;
            txvDateTime.text = data.dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a"));

            chkCompleted.setOnClickListener {
                list.removeAt(position)
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, list.size);//BUG SOLVED: Delete correct task position

            }


            rootView.setOnClickListener { }
        }
    }

}