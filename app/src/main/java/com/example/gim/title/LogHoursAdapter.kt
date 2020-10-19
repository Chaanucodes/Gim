package com.example.gim.title

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gim.databinding.RecordTimeItemBinding

class LogHoursAdapter : ListAdapter<LogHours, LogHoursAdapter.LogHoursViewHolder>(SearchResultsDiff()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogHoursViewHolder {
        return LogHoursViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: LogHoursViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class LogHoursViewHolder private constructor(private val binding : RecordTimeItemBinding) :
        RecyclerView.ViewHolder(binding.root){

        companion object{
            fun from(parent: ViewGroup) : LogHoursViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecordTimeItemBinding.inflate(layoutInflater, parent, false)

                return LogHoursViewHolder(binding)
            }
        }

        fun bind(
            item: LogHours
        ) {
            binding.data = item
            binding.executePendingBindings()
        }
    }


}

class SearchResultsDiff : DiffUtil.ItemCallback<LogHours>(){
    override fun areItemsTheSame(oldItem: LogHours, newItem: LogHours): Boolean {
        return oldItem.time == oldItem.time
    }

    override fun areContentsTheSame(oldItem: LogHours, newItem: LogHours): Boolean {
        return oldItem == newItem
    }
}