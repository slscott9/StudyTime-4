package com.sscott.studytime_4.ui.month

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.transition.TransitionManager
import com.sscott.studytime_4.R
import com.sscott.studytime_4.data.GoalData
import com.sscott.studytime_4.data.MonthData
import com.sscott.studytime_4.databinding.FragmentMonthViewBinding
import com.sscott.studytime_4.ui.week.WeekViewFragment
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.transition.MaterialContainerTransform
import com.sscott.studytime_4.data.local.entities.MonthlyGoal
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_month_view.*
import kotlinx.android.synthetic.main.fragment_week_view.*
import java.text.DecimalFormat

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

        //observe first so there is data for setGoal to set the axisLeft.axisMax with limitline
        //crashes is there is no data available
        viewModel.totalHours.observe(viewLifecycleOwner){
            it?.let {
                setTotalHours(it)
            }
        }

        viewModel.monthBarDataSet.observe(viewLifecycleOwner){
            it?.let {
                setMonthBarChart(it)
            }
        }

        viewModel.monthlyGoal.observe(viewLifecycleOwner){
            it?.let {
                setGoal(it)
            }
        }

        viewModel.monthLabels.observe(viewLifecycleOwner){
            it?.let {
                setLabels(it)
            }
        }

        binding.addMonthlyGoalChip.setOnClickListener {
            expandAddGoalCV()
        }

    }

    private fun hideSoftKeyboard(view: View) {
        val imm =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun setTotalHours(totalHours : BarDataSet) {
        binding.totalMonthHoursChart.apply {
            data = BarData(totalHours.apply {
                color = ResourcesCompat.getColor(resources, R.color.marigold, null)
            })
            data.barWidth = .25F
            axisLeft.valueFormatter = MyValueFormatter()
            axisLeft.axisMinimum = 0F
            axisRight.setDrawLabels(false)
            axisRight.setDrawGridLines(false)
            xAxis.setDrawLabels(false) //top labels
            description.isEnabled = false
            axisLeft.valueFormatter = WeekViewFragment.MyValueFormatter() //remove float decimals
            axisLeft.granularity = 1F //sets steps by one
            animateY(1000)
        }
    }

    private fun setGoal(goal: MonthlyGoal) {

        val label = setLabel(goal.hours.toFloat())
        val limitLine = LimitLine(goal.hours.toFloat(), label)

        binding.totalMonthHoursChart.apply {
            axisLeft.axisMinimum = 0F
            axisLeft.axisMaximum = (goal.hours.toFloat() + data.yMax)

            //if limit is zero dont draw it
            //if there a limit line remove the current and add new one
            if(goal.hours != 0){
                axisLeft.removeAllLimitLines()
                axisLeft.addLimitLine(limitLine)
            }
        }
    }

    private fun setLabels(labelList : List<String>) {
        binding.monthBarChart.apply {
            xAxis.valueFormatter = IndexAxisValueFormatter(labelList);
        }
    }

    private fun setLabel(hours : Float) : String{

        return when {
            hours == 1F -> "$hours hour"
            hours > 1F -> "$hours hours"
            else -> "$hours minutes"
        }
    }

    private fun setMonthBarChart(monthData : BarDataSet) {

        monthData.color = ResourcesCompat.getColor(resources, R.color.marigold, null)

        binding.monthBarChart.apply {
            data = BarData( monthData) // set the data and list of labels into chart

            xAxis.setLabelCount(monthData.entryCount,monthData.entryCount <= 1 )
            xAxis.setCenterAxisLabels(monthData.entryCount <= 1)

            data.barWidth = .25F
            axisLeft.valueFormatter = MyValueFormatter()
            axisLeft.axisMinimum = 0F
            axisRight.setDrawLabels(false)
            axisRight.setDrawGridLines(false)
            description.isEnabled = false
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


    private fun expandAddGoalCV(){
        binding.run {
            binding.cvTotalMonthlyHours.visibility = View.INVISIBLE
            binding.scrimView.visibility = View.VISIBLE

            binding.scrimView.setOnClickListener {
                hideSoftKeyboard(it)
                collapseAddGoalCV()
            }

            val transformation = MaterialContainerTransform().apply {
                startView = binding.addMonthlyGoalChip
                endView = binding.cvAddMonthlyGoal
                scrimColor = Color.TRANSPARENT

                addTarget(binding.cvAddMonthlyGoal) // transform does not run on starting and ending view
            }

            TransitionManager.beginDelayedTransition(binding.cvAddMonthlyGoal, transformation)
            binding.cvAddMonthlyGoal.visibility = View.VISIBLE
            binding.addMonthlyGoalChip.visibility = View.INVISIBLE

            setupSaveGoalButton()
        }
    }

    private fun collapseAddGoalCV() {
        binding.cvAddMonthlyGoal.visibility = View.GONE
        binding.cvTotalMonthlyHours.visibility = View.VISIBLE
        binding.scrimView.visibility = View.GONE

        val transformation = MaterialContainerTransform().apply {
            startView = binding.cvAddMonthlyGoal
            endView = binding.addMonthlyGoalChip
            scrimColor = Color.TRANSPARENT
            startElevation = 3F

            addTarget(binding.addMonthlyGoalChip)
        }

        TransitionManager.beginDelayedTransition(binding.clMonthBarCharts, transformation)
        binding.addMonthlyGoalChip.visibility = View.VISIBLE
        binding.cvAddMonthlyGoal.visibility = View.INVISIBLE
    }

    private fun setupSaveGoalButton(){
        binding.btnSaveGoal.setOnClickListener {
            if(etMonthlyGoal.text.isNullOrBlank()){
                Toast.makeText(requireActivity(), "Please enter a goal", Toast.LENGTH_SHORT).show()
            }else{
                viewModel.saveGoal(etMonthlyGoal.text.toString().toInt())
                hideSoftKeyboard(it)
                collapseAddGoalCV()
            }
        }
    }




}