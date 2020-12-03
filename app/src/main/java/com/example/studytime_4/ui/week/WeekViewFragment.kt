package com.example.studytime_4.ui.week

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.studytime_4.R
import com.example.studytime_4.databinding.FragmentWeekViewBinding
import com.example.studytime_4.ui.goal.AddGoalFragment
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_week_view.*
import timber.log.Timber

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
                val limitline = LimitLine(it.limit, "Weekly goal")

                Timber.i(it.limit.toString())

                binding.totalHoursChart.apply {
                    data = it.totalHours
                    axisLeft.axisMaximum = it.limit
                    axisLeft.axisMinimum = 0F
                    axisLeft.addLimitLine(limitline)
                }

                val xaxis = binding.totalHoursChart.xAxis
                xaxis.valueFormatter = IndexAxisValueFormatter(viewModel.datesFromSessions)

                Timber.i(viewModel.datesFromSessions.toString())

            }
        }
        binding.addGoalChip.setOnClickListener {
            val addGoalDialogFragment = AddGoalFragment()
            addGoalDialogFragment.show(parentFragmentManager, addGoalDialogFragment.tag)
        }




        return binding.root

    }


    private fun setBarChart(weekData: WeekViewModel.WeekData) {
        binding.weekBarChart.fitScreen()

        binding.weekBarChart.data =
            weekData.weekBarData // set the data and list of lables into chart
        binding.weekBarChart.getXAxis().setValueFormatter(IndexAxisValueFormatter(weekData.labels));
        binding.weekBarChart.xAxis.setLabelCount(weekData.labels.size, false) //force = false aligns values with labels
        binding.weekBarChart.xAxis.spaceMin = 0.5F


//        binding.weekBarChart.setDescription("Sessions from last 7 days")
//        binding.weekBarChart.setVisibleXRange(1F,viewModel.datesFromSessions.size.toFloat())
//        binding.weekBarChart.xAxis.setLabelCount(6, true)

//        //barDataSet.setColors(ColorTemplate.COLORFUL_COLORS)
//        barDataSet.color = resources.getColor(R.color.colorAccent)

        binding.weekBarChart.animateY(1000)
    }


}