package com.example.studytime_4.ui.month

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
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionManager
import com.example.studytime_4.R
import com.example.studytime_4.data.GoalData
import com.example.studytime_4.data.MonthData
import com.example.studytime_4.data.WeekData
import com.example.studytime_4.databinding.FragmentMonthViewBinding
import com.example.studytime_4.ui.goal.AddGoalFragment
import com.example.studytime_4.ui.home.HomeFragmentDirections
import com.example.studytime_4.ui.week.WeekViewFragment
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_month_view.*
import kotlinx.android.synthetic.main.fragment_week_view.*
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
                setMonthBarChart(it)
            }
        }

        viewModel.goalData.observe(viewLifecycleOwner){
            it?.let {
                setMonthGoalBarChart(it)
            }
        }

        binding.addMonthlyGoalChip.setOnClickListener {
            expandAddGoalCV()
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
                viewModel.addGoal(etMonthlyGoal.text.toString().toInt())
                hideSoftKeyboard(it)
                collapseAddGoalCV()
            }
        }
    }

    private fun hideSoftKeyboard(view: View) {
        val imm =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


    private fun setMonthGoalBarChart(goalData: GoalData) {

        val label = setLabel(goalData.limit.toFloat())
        val limitLine = LimitLine(goalData.limit.toFloat(), label)

        goalData.totalHours.color = ResourcesCompat.getColor(resources, R.color.marigold, null)

        binding.totalMonthHoursChart.apply {
            data = BarData(goalData.totalHours)
            axisLeft.axisMaximum = (goalData.limit.toFloat() + goalData.totalHours.yMax)
            axisLeft.axisMinimum = 0F

            //if limit is zero dont draw it
            //if there a limit line remove the current and add new one
            if(goalData.limit != 0){
                axisLeft.removeAllLimitLines()
                axisLeft.addLimitLine(limitLine)
            }

            //Both must be set to false or double gridlines will be drawn for the goalbarchart
            data.barWidth = .25F
            axisRight.setDrawLabels(false)
            axisRight.setDrawGridLines(false)
            description.isEnabled = false
            axisLeft.valueFormatter = WeekViewFragment.MyValueFormatter() //remove float decimals
            axisLeft.granularity = 1F //sets steps by ones
            xAxis.setDrawLabels(false) //disable labels for x axis
        }
    }

    private fun setLabel(hours : Float) : String{

        return when {
            hours == 1F -> "$hours hour"
            hours > 1F -> "$hours hours"
            else -> "$hours minutes"
        }
    }

    private fun setMonthBarChart(monthData: MonthData) {

        monthData.monthBarData.color = ResourcesCompat.getColor(resources, R.color.marigold, null)

        val force: Boolean = if(monthData.labels.size > 1) {
            binding.monthBarChart.xAxis.setCenterAxisLabels(false)
            false
        } else {
            binding.monthBarChart.xAxis.setCenterAxisLabels(true)
            true
        }

        binding.monthBarChart.apply {
            data = BarData( monthData.monthBarData) // set the data and list of labels into chart
            xAxis.setLabelCount(
                monthData.labels.size,
                force
            ) //force = false aligns values with labels
            data.barWidth = .25F
            xAxis.valueFormatter = IndexAxisValueFormatter(monthData.labels);
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




}