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
import com.example.studytime_4.ui.home.HomeFragmentDirections
import com.example.studytime_4.ui.week.WeekViewFragment
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.text.DecimalFormat
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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.monthBarData.observe(viewLifecycleOwner){
            it?.let {
                setBarChart(it)
            }
        }

        binding.addMonthGoalChip.setOnClickListener {
            parentFragment?.findNavController()?.navigate(HomeFragmentDirections.actionHomeFragmentToAddGoalFragment(true))
        }

    }

    private fun setBarChart(monthData: MonthData) {

        binding.monthBarChart.data =
            monthData.monthBarData // set the data and list of labels into chart

        val force: Boolean = if(monthData.labels.size > 1) {
            binding.monthBarChart.xAxis.setCenterAxisLabels(false)
            false
        } else {
            binding.monthBarChart.xAxis.setCenterAxisLabels(true)
            true
        }

        binding.monthBarChart.apply {
            xAxis.setLabelCount(
                monthData.labels.size,
                force
            ) //force = false aligns values with labels
            xAxis.valueFormatter = IndexAxisValueFormatter(monthData.labels);
            axisLeft.valueFormatter = MyValueFormatter()
            axisLeft.axisMinimum = 0F
            axisRight.setDrawLabels(false)
            axisRight.setDrawGridLines(false)

            axisLeft.valueFormatter = WeekViewFragment.MyValueFormatter() //remove float decimals
            axisLeft.granularity = 1F //sets steps by one
            animateY(1000)
        }


    }

    class MyValueFormatter : ValueFormatter() {
        private val format = DecimalFormat("###,##0.0")

        override fun getFormattedValue(value: Float): String {
            return value.toInt().toString() //gets y axis values to integers instead of 0.0 floats
        }
    }




}