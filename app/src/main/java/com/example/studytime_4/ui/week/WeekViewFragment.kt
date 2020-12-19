package com.example.studytime_4.ui.week

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.example.studytime_4.R
import com.example.studytime_4.data.WeekData
import com.example.studytime_4.databinding.FragmentWeekViewBinding
import com.example.studytime_4.ui.goal.AddGoalFragment
import com.example.studytime_4.ui.home.HomeFragmentDirections
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_week_view.*
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

                val limitLineLabel = setLabel(it.limit.toFloat())
                val limitLine = LimitLine(it.limit.toFloat(), limitLineLabel)

                it.totalHours.color = ResourcesCompat.getColor(resources, R.color.marigold, null)

                binding.totalHoursChart.apply {
                    data = BarData(it.totalHours)
                    axisLeft.axisMaximum = (it.limit.toFloat() + it.totalHours.yMax)
                    axisLeft.axisMinimum = 0F

                    //if limit is zero dont draw it
                    //if there a limit line remove the current and add new one
                    if(it.limit != 0){
                        axisLeft.removeAllLimitLines()
                        axisLeft.addLimitLine(limitLine)
                    }

                    //Both must be set to false or double gridlines will be drawn for the goalbarchart
                    data.barWidth = .25F
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
            expandAddGoalCV()
        }
    }

    private fun setLabel(hours : Float) : String{

        return when {
            hours == 1F -> "$hours hour"
            hours > 1F -> "$hours hours"
            else -> "$hours minutes"
        }
    }

    private fun expandAddGoalCV() {
        binding.run {
            binding.cvTotalHours.visibility = View.INVISIBLE
            binding.scrimView.visibility = View.VISIBLE

            binding.scrimView.setOnClickListener {
                collapseAddGoalCV()
            }

            val transformation = MaterialContainerTransform().apply {
                startView = binding.addGoalChip
                endView = binding.mcvAddGoal
                scrimColor = Color.TRANSPARENT

                addTarget(binding.mcvAddGoal) // transform does not run on starting and ending view
            }

            TransitionManager.beginDelayedTransition(binding.mcvAddGoal, transformation)
            binding.mcvAddGoal.visibility = View.VISIBLE
            binding.addGoalChip.visibility = View.INVISIBLE

            setupSaveGoalButton()
        }
    }

    private fun collapseAddGoalCV() {
        binding.mcvAddGoal.visibility = View.GONE
        binding.cvTotalHours.visibility = View.VISIBLE
        binding.scrimView.visibility = View.GONE

        val transformation = MaterialContainerTransform().apply {
            startView = binding.mcvAddGoal
            endView = binding.addGoalChip
            scrimColor = Color.TRANSPARENT
            startElevation = 3F

            addTarget(binding.addGoalChip)
        }

        TransitionManager.beginDelayedTransition(binding.clBarCharts, transformation)
        binding.addGoalChip.visibility = View.VISIBLE
        binding.mcvAddGoal.visibility = View.INVISIBLE
    }

    private fun setupSaveGoalButton(){
        binding.btnSaveGoal.setOnClickListener {
            if(etWeeklyGoal.text.isNullOrBlank()){
                Toast.makeText(requireActivity(), "Please enter a goal", Toast.LENGTH_SHORT).show()
            }else{
                viewModel.addGoal(etWeeklyGoal.text.toString().toInt())
                collapseAddGoalCV()
            }
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

        weekData.weekBarData.color = resources.getColor(R.color.marigold)
        binding.weekBarChart.data =
            BarData( weekData.weekBarData) // set the data and list of lables into chart

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