package com.example.planix.Adapter
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.planix.Data.TaskEntity
import com.example.planix.Domain.TaskStatus
import com.example.planix.R
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeParseException
class HistoryTasksAdapter :
    ListAdapter<TaskEntity, HistoryTasksAdapter.TaskHistoryViewHolder>(HistoryTasksDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task_history, parent, false)
        return TaskHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskHistoryViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task)
    }

    inner class TaskHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.historyTaskTitleTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.historyTaskDescription)
        val deadlineTextView: TextView = itemView.findViewById(R.id.historyDeadlineTextView)
        val priorityTextView: TextView = itemView.findViewById(R.id.historyPriorityTextView)
        val categoryTextView: TextView = itemView.findViewById(R.id.historyCategoryTextView)
        val statusTextView: TextView = itemView.findViewById(R.id.historyStatusTextView)

        fun bind(task: TaskEntity) {
            titleTextView.text = task.title
            if (task.description.isNullOrEmpty()) {
                descriptionTextView.visibility = View.GONE
            } else {
                descriptionTextView.visibility = View.VISIBLE
                descriptionTextView.text = task.description
            }

            try {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                val dateTime = LocalDateTime.parse(task.deadline, formatter)
                deadlineTextView.text = "Merzimi: ${dateTime.format(formatter)}"
            } catch (e: DateTimeParseException) {
                deadlineTextView.text = "Merzimi: ${task.deadline.replace("T", " ")}"
            } catch (e: Exception) {
                deadlineTextView.text = "Merzimi: ${task.deadline}"
            }

            priorityTextView.text = "Basymdylyq: ${task.priority.displayName}"
            categoryTextView.text = "Sanat: ${task.category.displayName}"
            statusTextView.text = "Quyi: ${task.status.displayName}"

            when (task.status) {
                TaskStatus.ORYNDALGAN -> { // No more "Unresolved reference"
                    titleTextView.paintFlags = titleTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    titleTextView.setTextColor(itemView.context.getColor(R.color.gray_text))
                    descriptionTextView.setTextColor(itemView.context.getColor(R.color.gray_text))
                    deadlineTextView.setTextColor(itemView.context.getColor(R.color.gray_text))
                    priorityTextView.setTextColor(itemView.context.getColor(R.color.gray_text))
                    categoryTextView.setTextColor(itemView.context.getColor(R.color.gray_text))
                    statusTextView.setTextColor(itemView.context.getColor(R.color.completed_green))
                }
                TaskStatus.JOYULGAN -> { // No more "Unresolved reference"
                    titleTextView.paintFlags = titleTextView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    titleTextView.setTextColor(itemView.context.getColor(R.color.deleted_red))
                    descriptionTextView.setTextColor(itemView.context.getColor(R.color.deleted_red))
                    deadlineTextView.setTextColor(itemView.context.getColor(R.color.deleted_red))
                    priorityTextView.setTextColor(itemView.context.getColor(R.color.deleted_red))
                    categoryTextView.setTextColor(itemView.context.getColor(R.color.deleted_red))
                    statusTextView.setTextColor(itemView.context.getColor(R.color.deleted_red))
                }
                else -> {
                    titleTextView.paintFlags = titleTextView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    titleTextView.setTextColor(itemView.context.getColor(R.color.black_text))
                    descriptionTextView.setTextColor(itemView.context.getColor(R.color.black_text))
                    deadlineTextView.setTextColor(itemView.context.getColor(R.color.black_text))
                    priorityTextView.setTextColor(itemView.context.getColor(R.color.black_text))
                    categoryTextView.setTextColor(itemView.context.getColor(R.color.black_text))
                    statusTextView.setTextColor(itemView.context.getColor(R.color.black_text))
                }
            }
        }
    }

    private class HistoryTasksDiffCallback : DiffUtil.ItemCallback<TaskEntity>() {
        override fun areItemsTheSame(oldItem: TaskEntity, newItem: TaskEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TaskEntity, newItem: TaskEntity): Boolean {
            return oldItem == newItem
        }
    }
}