package com.arasaka.todolist.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arasaka.todolist.Model.Task
import com.arasaka.todolist.R
import com.google.android.material.checkbox.MaterialCheckBox
import java.time.format.DateTimeFormatter

class TasksAdapter(
    private val list: MutableList<Task>,
    var onClickDoneTask:(task:Task, position: Int) -> Unit,
    var onClickDetailTask:(task: Task) -> Unit
):
    RecyclerView.Adapter<TasksAdapter.TaskViewHolder>() {


    fun add(task: Task) {
        list.add(task)
        notifyItemInserted(list.size -1)
    }


    fun remove(position: Int){
        list.removeAt(position)
        notifyItemRemoved(position);
        //layoutPosition
        //position
        //notifyItemRangeChanged(position, list.size);//BUG SOLVED: Delete correct task position
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.task_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        //(holder as TaskViewHolder).bind(list[position])
        holder.bind(list[position])

    }

    override fun getItemCount() = list.size

    inner class TaskViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(data: Task) = view.apply {

            val txvTitle: TextView = findViewById(R.id.txvTitleTask)
            val txvDateTime: TextView = findViewById(R.id.txvDateTime)
            val chkCompleted: MaterialCheckBox = findViewById(R.id.chkCompleted)

            txvTitle.text = data.title;
            txvDateTime.text = data.dateTime?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a"));
            chkCompleted.isChecked = false;

            chkCompleted.setOnClickListener {
                onClickDoneTask(data, adapterPosition)
            }


            rootView.setOnClickListener { }
        }
    }

}