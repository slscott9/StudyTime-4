package com.example.studytime_4.ui.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.studytime_4.ui.month.MonthViewFragment
import com.example.studytime_4.ui.week.WeekViewFragment

class WeekMonthVPAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {

        return when(position){
            0 -> WeekViewFragment()
            else -> MonthViewFragment()
        }
    }
}