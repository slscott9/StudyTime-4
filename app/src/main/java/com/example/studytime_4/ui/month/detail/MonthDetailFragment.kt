package com.example.studytime_4.ui.month.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TableRow
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.get
import androidx.core.view.marginTop
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.example.studytime_4.R
import com.example.studytime_4.data.MonthData
import com.example.studytime_4.databinding.FragmentMonthDetailBinding
import com.example.studytime_4.other.MONTH_SELECTED
import com.example.studytime_4.other.YEAR_SELECTED
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_month_detail.view.*
import timber.log.Timber
import timber.log.Timber.i
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class MonthDetailFragment : Fragment() {

    /*
        42 day text views (41 zero based)
        38 hour text views (37 zero based)

        the first hour text view starts on index 42 in xml

     */

    private lateinit var binding: FragmentMonthDetailBinding
    private val viewModel: MonthDetailViewModel by viewModels()
    private val args: MonthDetailFragmentArgs by navArgs()
    private val calendar = Calendar.getInstance()
    private var firstDayOfMonth = 0

    val STARTING_HOUR_VIEW = 42 //first text view for hour starts at index 42 in month detail xml's constraint layout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_month_detail, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //set the calendar to the year and month that was selected by user

        calendar.apply {
            set(Calendar.MONTH, args.monthSelected - 1)
            set(Calendar.YEAR, args.yearSelected)
            set(Calendar.DAY_OF_MONTH, 1)
        }

        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - 1 //how many days to loop for in the month

        firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK) - 1 //what view to start at

        drawDays(firstDayOfMonth, daysInMonth)


        setupObservers()
    }

    private fun setupObservers() {
        viewModel.monthBarData.observe(viewLifecycleOwner, Observer {
            it?.let {
                setBarChart(it)
            }
        })

        viewModel.monthlyHours.observe(viewLifecycleOwner){
            it?.let {
                setHours(it)
            }
        }
    }

    private fun setHours(hours : IntArray) {

        //hour text view starts at 42
        val cl = binding.clMonthDetail

        hours.forEachIndexed {i, hour ->
            if(hour != 0){
                val tv = cl[i + STARTING_HOUR_VIEW + firstDayOfMonth] as TextView //finds tv

                tv.text = if(hour > 1) "$hour hours" else "$hour hour"
            }
        }
    }

    private fun drawDays(firstDayOfMonth: Int, daysInMonth: Int) {

        var day = 0
        //loop for hour many days are in this month and use indices to find correct text view index in xml
        for (i in firstDayOfMonth..daysInMonth + firstDayOfMonth) {

            day += 1

            val tv = binding.clMonthDetail[i] as TextView

            tv.apply{
                tv.text = "$day"
                visibility = View.VISIBLE
            }
        }
    }

    private fun setBarChart(monthData: MonthData) {

        val force: Boolean

        binding.monthDetailBarChart.data =
            monthData.monthBarData // set the data and list of labels into chart

        if (monthData.labels.size > 1) {
            binding.monthDetailBarChart.xAxis.setCenterAxisLabels(false)
            force = false
        } else {
            binding.monthDetailBarChart.xAxis.setCenterAxisLabels(true)
            force = true
        }

        binding.monthDetailBarChart.apply {
            xAxis.setLabelCount(
                monthData.labels.size,
                force
            ) //force = false aligns values with labels
            xAxis.valueFormatter = IndexAxisValueFormatter(monthData.labels);
            animateY(1000)
        }
    }

}