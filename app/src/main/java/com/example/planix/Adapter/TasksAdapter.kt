package com.example.planix.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.planix.Data.TaskEntity
import com.example.planix.R

class TasksAdapter(
    private var taskList: List<TaskEntity>,
    private val onEditClick: (TaskEntity) -> Unit,
    private val onDeleteClick: (TaskEntity) -> Unit,
    private val onCompleteClick: (TaskEntity) -> Unit
) : RecyclerView.Adapter<TasksAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.taskTitle)
        val description: TextView = itemView.findViewById(R.id.taskDescription)
        val deadline: TextView = itemView.findViewById(R.id.taskDeadline)
        val priority: TextView = itemView.findViewById(R.id.taskPriority)
        val category: TextView = itemView.findViewById(R.id.taskCategory)
        val status: TextView = itemView.findViewById(R.id.taskStatus)

        val editBtn: ImageView = itemView.findViewById(R.id.btnEdit)
        val deleteBtn: ImageView = itemView.findViewById(R.id.btnDelete)
        val completeBtn: ImageView = itemView.findViewById(R.id.btnComplete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewholder_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun getItemCount(): Int = taskList.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]

        holder.title.text = task.title
        holder.description.text = task.description
        holder.deadline.text = "Merzimi: ${task.deadline.replace("T", " ")}"
        holder.priority.text = "Basymdylyq: ${task.priority.displayName}"
        holder.category.text =
            "Sanat: ${task.category.displayName}"
        holder.status.text =
            "Küiı: ${task.status.displayName}"


        holder.editBtn.setOnClickListener {
            onEditClick(task)
        }

        holder.deleteBtn.setOnClickListener {
            onDeleteClick(task)
        }
        holder.completeBtn.setOnClickListener {
                onCompleteClick(task)
        }

    }
    fun updateList(newList: List<TaskEntity>) {
        taskList = newList
        notifyDataSetChanged()
    }

}
