package com.example.studytime_4.ui.month

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.example.studytime_4.R
import com.example.studytime_4.data.MonthData
import com.example.studytime_4.data.WeekData
import com.example.studytime_4.databinding.FragmentMonthViewBinding
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


        return binding.root
    }


    private fun setBarChart(monthData: MonthData) {

        var force: Boolean

        binding.monthBarChart.data =
            monthData.monthBarData // set the data and list of lables into chart

        if (monthData.labels.size > 1) {
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