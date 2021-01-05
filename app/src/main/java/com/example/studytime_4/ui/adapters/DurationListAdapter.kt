package com.example.studytime_4.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.studytime_4.data.local.entities.Duration
import com.example.studytime_4.databinding.DurationItemBinding

class DurationListAdapter() : ListAdapter<Duration, DurationListAdapter.ViewHolder>(DiffUtilCallback()) {


    class ViewHolder(val binding : DurationItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(duration: Duration){
            binding.duration = duration
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DurationItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val duration = getItem(position)
        holder.bind(duration)
    }


    class DiffUtilCallback : DiffUtil.ItemCallback<Duration>() {
        override fun areItemsTheSame(oldItem: Duration, newItem: Duration): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Duration, newItem: Duration): Boolean {
            return oldItem == newItem
        }

    }
}