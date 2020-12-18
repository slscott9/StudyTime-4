package com.example.studytime_4.ui.week

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.studytime_4.R
import com.example.studytime_4.data.WeekData
import com.example.studytime_4.databinding.FragmentWeekViewBinding
import com.example.studytime_4.ui.goal.AddGoalFragment
import com.example.studytime_4.ui.home.HomeFragmentDirections
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.text.DecimalFormat
import java.time.OffsetDateTime

@AndroidEntryPoint
class WeekViewFragment : Fragment() {

    private lateinit var binding: FragmentWeekViewBinding
    private val viewModel: WeekViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_week_view, container, false)
        binding.lifecycleOwner = viewLifecycleOwner


        val time = OffsetDateTime.now().toEpochSecond()

        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.weekBarData.observe(viewLifecycleOwner) {
            it?.let {
                setupWeekBarChart(it)
            }
        }

        viewModel.goalData.observe(viewLifecycleOwner) {
            it?.let {

                val limitLine = LimitLine(it.limit.toFloat(), "Weekly Goal")

                binding.totalHoursChart.apply {
                    data = it.totalHours
                    axisLeft.axisMaximum = (it.limit.toFloat() + it.totalHours.yMax)
                    axisLeft.axisMinimum = 0F

                    //if limit is zero dont draw it
                    //if there a limit line remove the current and add new one
                    if(it.limit != 0){
                        axisLeft.removeAllLimitLines()
                        axisLeft.addLimitLine(limitLine)
                    }

                    //Both must be set to false or double gridlines will be drawn for the goalbarchart
                    axisRight.setDrawLabels(false)
                    axisRight.setDrawGridLines(false)
                    description.isEnabled = false
                    axisLeft.valueFormatter = MyValueFormatter() //remove float decimals
                    axisLeft.granularity = 1F //sets steps by ones
                    xAxis.setDrawLabels(false) //disable labels for x axis
                }
            }
        }

        binding.addGoalChip.setOnClickListener {
            parentFragment?.findNavController()?.navigate(HomeFragmentDirections.actionHomeFragmentToAddGoalFragment(false))
        }
    }

    class MyValueFormatter : ValueFormatter() {
        private val format = DecimalFormat("###,##0.0")

        override fun getFormattedValue(value: Float): String {
            return value.toInt().toString() //gets y axis values to integers instead of 0.0 floats
        }
    }


    private fun setupWeekBarChart(weekData: WeekData) {

//        val description = Description()
//        description.text = "Total weekly hours ${weekData.totalHours}"
        binding.weekBarChart.data =
            weekData.weekBarData // set the data and list of lables into chart

        //case 1 multiple bar chart values -> axis labels need to move in order to match chart values
        //case 2 only one bar chart value -> so center the label over value
        val force: Boolean = if (weekData.labels.size > 1) {
            binding.weekBarChart.xAxis.setCenterAxisLabels(false)
            false
        } else {
            binding.weekBarChart.xAxis.setCenterAxisLabels(true)
            true
        }

        binding.weekBarChart.apply {

            //for labels since we know they will only ever be 7 values for the week
            xAxis.setLabelCount(
                weekData.labels.size,
                force
            ) //force = false aligns values with labels
            xAxis.valueFormatter = IndexAxisValueFormatter(weekData.labels);
            axisLeft.valueFormatter = MyValueFormatter()
            axisLeft.axisMinimum = 0F
            axisRight.setDrawLabels(false)
            axisRight.setDrawGridLines(false)
            description.isEnabled = false
            axisLeft.valueFormatter = MyValueFormatter() //remove float decimals
            axisLeft.granularity = 1F //sets steps by one
            animateY(1000)
        }


    }


}