package com.sscott.studytime_4.ui.week

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.transition.TransitionManager
import com.sscott.studytime_4.R
import com.sscott.studytime_4.data.GoalData
import com.sscott.studytime_4.data.WeekData
import com.sscott.studytime_4.databinding.FragmentWeekViewBinding
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.transition.MaterialContainerTransform
import com.sscott.studytime_4.data.local.entities.WeeklyGoal
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_week_view.*
import timber.log.Timber
import java.text.DecimalFormat

@AndroidEntryPoint
class WeekViewFragment : Fragment() {

    private lateinit var binding: FragmentWeekViewBinding
    private val viewModel: WeekViewModel by viewModels()

    private val weekDays = listOf<String>("S","M","T","W","T","F","S")


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

        viewModel.totalHours.observe(viewLifecycleOwner){
            it?.let {
                setTotalHours(it)
            }
        }

        viewModel.weeklyGoal.observe(viewLifecycleOwner){
            it?.let {
                setupGoalBarChart(it)
            }
        }

        binding.addGoalChip.setOnClickListener {
            expandAddGoalCV()
        }
    }

    private fun setupGoalBarChart(goal : WeeklyGoal) {
        val limitLineLabel = setLabel(goal.hours.toFloat())
        val limitLine = LimitLine(goal.hours.toFloat(), limitLineLabel)

//        .color = ResourcesCompat.getColor(resources, R.color.marigold, null)

        binding.totalHoursChart.apply {
            axisLeft.axisMaximum = (goal.hours.toFloat() + 7)
            axisLeft.axisMinimum = 0F

            //if limit is zero dont draw it
            //if there a limit line remove the current and add new one
            if(goal.hours != 0){
                axisLeft.removeAllLimitLines()
                axisLeft.addLimitLine(limitLine)
            }
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
                hideSoftKeyboard(it)
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
                viewModel.saveGoal(etWeeklyGoal.text.toString().toInt())
                hideSoftKeyboard(it)
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

    private fun setupWeekBarChart(weekData: BarDataSet) {
        weekData.color = ContextCompat.getColor(requireActivity(), R.color.marigold)

        //case 1 multiple bar chart values -> axis labels need to move in order to match chart values
        //case 2 only one bar chart value -> so center the label over value

        binding.weekBarChart.apply {

            data = BarData(weekData) // set the data and list of lables into chart
            xAxis.setCenterAxisLabels(false)
            xAxis.setLabelCount(weekDays.size, false)
            xAxis.valueFormatter = IndexAxisValueFormatter(weekDays);
            axisLeft.axisMinimum = 0F
            axisRight.setDrawLabels(false)
            axisRight.setDrawGridLines(false)
            description.isEnabled = false
            axisLeft.valueFormatter = MyValueFormatter() //remove float decimals
            axisLeft.granularity = 1F //sets steps by one
            animateY(1000)
        }
    }

    private fun setTotalHours(hours : Float){

        Timber.i("Total hours is $hours")
        binding.totalHoursChart.apply {
            data = BarData(BarDataSet(listOf(BarEntry(0F, hours)), "").apply {
                color = ContextCompat.getColor(requireActivity(), R.color.marigold)

            })
            //Both must be set to false or double grid lines will be drawn for the goalbarchart
            data.barWidth = .25F
            axisRight.setDrawLabels(false)
            axisRight.setDrawGridLines(false)
            axisLeft.axisMinimum = 0F
            description.isEnabled = false
            axisLeft.valueFormatter = MyValueFormatter() //remove float decimals
            axisLeft.granularity = 1F //sets steps by ones
            xAxis.setDrawLabels(false) //disable labels for x axis
        }
    }

    private fun hideSoftKeyboard(view: View) {
        val imm =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


}