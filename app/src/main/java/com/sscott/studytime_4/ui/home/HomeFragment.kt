package com.sscott.studytime_4.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.sscott.studytime_4.R
import com.sscott.studytime_4.ui.adapters.WeekMonthVPAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*

@AndroidEntryPoint
class HomeFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment



        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNav()

        val viewPager = WeekMonthVPAdapter(this)
        viewPager2.adapter = viewPager

        TabLayoutMediator(tabLayout, viewPager2){tab, position ->
            tab.text = getTabTitle(position)

        }.attach()

    }

    private fun setupNav() {

        bottom_nav.setOnNavigationItemSelectedListener {

            when(it.itemId){
                R.id.timerFragment -> {
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToTimerFragment())
                    true
                }
                R.id.sessionListFragment -> {
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSessionListFragment())
                    true
                }
                R.id.homeFragment -> {
                    findNavController().navigate(R.id.homeFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun getTabTitle(position: Int): String? {
        return when (position) {
            0 -> getString(R.string.week)
            1 -> getString(R.string.month)
            else -> null
        }
    }


}