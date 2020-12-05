package com.example.studytime_4.ui.month

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.studytime_4.R
import com.example.studytime_4.data.MonthData
import com.example.studytime_4.data.WeekData
import com.example.studytime_4.databinding.FragmentMonthViewBinding
import com.example.studytime_4.ui.goal.AddGoalFragment
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.time.Month

@AndroidEntryPoint
class MonthViewFragment : Fragment() {

    private lateinit var binding: FragmentMonthViewBinding
    private val viewModel: MonthViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_month_view, container, false)
        binding.lifecycleOwner = viewLifecycleOwner


        viewModel.monthBarData.observe(viewLifecycleOwner){
            it?.let {
                setBarChart(it)
            }
        }

        binding.addMonthGoalChip.setOnClickListener {

            findNavController().navigate(MonthViewFragmentDirections.actionMonthViewFragmentToAddGoalFragment(true))
        }

        return binding.root
    }


    /*

        add a monthly goal

        REDO RESUME TELL HOW THE PROJECTS YOU MADE SOLVED A PROBLEM!!!
     */

    private fun setBarChart(monthData: MonthData) {

        val force: Boolean

        binding.monthBarChart.data =
            monthData.monthBarData // set the data and list of labels into chart

        if(monthData.labels.size > 1) {
            binding.monthBarChart.xAxis.setCenterAxisLabels(false)
            force = false
        } else {
            binding.monthBarChart.xAxis.setCenterAxisLabels(true)
            force = true
        }

        binding.monthBarChart.apply {
            xAxis.setLabelCount(
                monthData.labels.size,
                force
            ) //force = false aligns values with labels
            xAxis.valueFormatter = IndexAxisValueFormatter(monthData.labels);
            animateY(1000)
        }


    }




}