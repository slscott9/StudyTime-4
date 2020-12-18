package com.example.studytime_4.ui.adapters

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.studytime_4.R
import com.example.studytime_4.data.StudySession
import com.example.studytime_4.databinding.DayItemBinding

class CalendarAdapter(
    private val firstDayOfMonth: Int,
    private val daysInMonth: Int,
    val listener : CalendarListener
) : ListAdapter<StudySession?, CalendarAdapter.CalendarViewHolder>(CalendarDiffUtilCallback()){

    private var count = 0

    private val weekDays = listOf<String>("S", "M","T","W","T","F","S")


    private val lastIndex = daysInMonth + WEEKDAYS + firstDayOfMonth

    private val firstIndex = firstDayOfMonth + WEEKDAYS

    private val daysInWeek = 7



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DayItemBinding.inflate(layoutInflater, parent, false)
        return CalendarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val studySession = getItem(position)

        holder.bind(studySession, position, listener)

    }

    inner class CalendarViewHolder(val binding: DayItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(studySession: StudySession?, position: Int, listener: CalendarListener ){

            when {
                position < daysInWeek ->{
                    binding.tvDay.apply {
                        text = weekDays[position]
                        typeface = Typeface.DEFAULT_BOLD
                    }
                }

                position in daysInWeek until firstIndex -> {
                    binding.cvDayContainer.visibility = View.GONE
                }

                position in firstIndex .. lastIndex -> {
                    count += 1
                    binding.tvDay.text = "$count"

                    studySession?.let {
                        binding.tvDay.setBackgroundResource(R.drawable.circle)
                        ViewCompat.setTransitionName(binding.tvDay, studySession.date)
                        binding.tvDay.setTextColor(ContextCompat.getColor(binding.tvDay.context, R.color.white))
                        binding.tvDay.setOnClickListener {
                            listener.onClick(studySession, binding.tvDay)
                        }
                    }
                }

                position > lastIndex -> binding.cvDayContainer.visibility = View.GONE
            }

        }
    }

    class CalendarListener(val listener : (StudySession?, TextView) -> Unit) {
        fun onClick(studySession: StudySession?, textView: TextView){
            listener(studySession, textView)
        }
    }

    class CalendarDiffUtilCallback : DiffUtil.ItemCallback<StudySession?>() {
        override fun areItemsTheSame(oldItem: StudySession, newItem: StudySession): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: StudySession, newItem: StudySession): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        const val WEEKDAYS = 7
    }
}