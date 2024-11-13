package com.example.gaston.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gaston.R
import com.example.gaston.util.Notification

class NotificationAdapter(private val notifications: MutableList<Notification>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvNotificationTitle)
        val message: TextView = itemView.findViewById(R.id.tvNotificationMessage)
        val time: TextView = itemView.findViewById(R.id.tvNotificationTime)
        val deleteButton: Button = itemView.findViewById(R.id.buttonApaga)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]
        holder.title.text = notification.title
        holder.message.text = notification.message
        holder.time.text = notification.time

        holder.deleteButton.setOnClickListener {
            removeNotification(position)
        }
    }

    override fun getItemCount() = notifications.size

    private fun removeNotification(position: Int) {
        notifications.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, notifications.size)
    }
}
