package com.example.studytime_4.ui.week

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.studytime_4.R
import com.example.studytime_4.data.WeekData
import com.example.studytime_4.databinding.FragmentWeekViewBinding
import com.example.studytime_4.ui.goal.AddGoalFragment
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat

@AndroidEntryPoint
class WeekViewFragment : Fragment() {

    private lateinit var binding: FragmentWeekViewBinding
    private val viewModel: WeekViewModel by viewModels()

    private val monthDayLabels = arrayListOf<String>(
        "1",
        "2",
        "3",
        "4",
        "5",
        "6",
        "7",
        "8",
        "9",
        "10",
        "11",
        "12",
        "13",
        "14",
        "15",
        "16",
        "17",
        "18",
        "19",
        "20",
        "21",
        "22",
        "23",
        "24",
        "25",
        "26",
        "27",
        "28",
        "29",
        "30",
        "31"
    )
    private val nullLabels = arrayListOf<String>(
        "No Data",
        "No Data",
        "No Data",
        "No Data",
        "No Data",
        "No Data",
        "No Data"
    )
    private val months = arrayListOf<String>(
        "January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
        "August",
        "September",
        "October",
        "November",
        "December"
    )




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_week_view, container, false)
        binding.lifecycleOwner = viewLifecycleOwner


        viewModel.weekBarData.observe(viewLifecycleOwner) {
            it?.let {
                setBarChart(it)
            }
        }

        viewModel.goalData.observe(viewLifecycleOwner) {
            it?.let {
                val limitLine = LimitLine(it.limit.toFloat(), "Weekly goal")

                binding.totalHoursChart.apply {
                    data = it.totalHours
                    axisLeft.axisMaximum = (it.limit.toFloat() + 2F)
                    axisLeft.axisMinimum = 0F
                    axisLeft.addLimitLine(limitLine)
                    axisRight.setDrawLabels(false)
                    axisLeft.valueFormatter = MyValueFormatter() //remove float decimals
                    axisLeft.granularity = 1F //sets steps by ones
                    xAxis.setDrawLabels(false) //disable labels for x axis
                }
            }
        }
        binding.addGoalChip.setOnClickListener {
            val addGoalDialogFragment = AddGoalFragment()
            addGoalDialogFragment.show(parentFragmentManager, addGoalDialogFragment.tag)
        }

        return binding.root

    }

    class MyValueFormatter : ValueFormatter() {
        private val format = DecimalFormat("###,##0.0")

        override fun getFormattedValue(value: Float): String {
            return value.toInt().toString()
        }
    }



    private fun setBarChart(weekData: WeekData) {

        var force: Boolean

        binding.weekBarChart.data =
            weekData.weekBarData // set the data and list of lables into chart

        if (weekData.labels.size > 1) {
            binding.weekBarChart.apply {
                xAxis.setCenterAxisLabels(false)
                force = false

            }
        } else {
            binding.weekBarChart.apply {
                xAxis.setCenterAxisLabels(true)
                force = true

            }
        }

        binding.weekBarChart.apply {
            xAxis.setLabelCount(
                weekData.labels.size,
                force
            ) //force = false aligns values with labels
            xAxis.valueFormatter = IndexAxisValueFormatter(weekData.labels);
            animateY(1000)
        }


    }


}