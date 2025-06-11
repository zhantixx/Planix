package com.example.planix.Activity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.planix.R

class UserAdapter(
    private var users: List<User>,
    private val onDeleteClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userLoginTextView: TextView = itemView.findViewById(R.id.userLoginTextView)
        val userEmailTextView: TextView = itemView.findViewById(R.id.userEmailTextView)
        val userIdTextView: TextView = itemView.findViewById(R.id.userIdTextView)
        val deleteUserButton: Button = itemView.findViewById(R.id.deleteUserButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.userLoginTextView.text = user.login
        holder.userEmailTextView.text = user.email
        holder.userIdTextView.text = "ID: ${user.id}"

        holder.deleteUserButton.setOnClickListener {
            onDeleteClick(user)
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    fun updateUsers(newUsers: List<User>) {
        users = newUsers
        notifyDataSetChanged()
    }
}
