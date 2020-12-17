package com.example.studytime_4.ui.month.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import androidx.core.view.doOnPreDraw
import androidx.core.view.get
import androidx.core.view.marginTop
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.studytime_4.R
import com.example.studytime_4.data.MonthData
import com.example.studytime_4.data.local.entities.StudySession
import com.example.studytime_4.databinding.FragmentMonthDetailBinding
import com.example.studytime_4.other.MONTH_SELECTED
import com.example.studytime_4.other.YEAR_SELECTED
import com.example.studytime_4.ui.adapters.CalendarAdapter
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_month_detail.view.*
import org.w3c.dom.Text
import timber.log.Timber
import timber.log.Timber.i
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

@AndroidEntryPoint
class MonthDetailFragment : Fragment() {

    private lateinit var binding: FragmentMonthDetailBinding
    private val viewModel: MonthDetailViewModel by viewModels()
    private val args: MonthDetailFragmentArgs by navArgs()
    private val calendar = Calendar.getInstance()
    private var firstDayOfMonth = 0
    private lateinit var calendarAdapter: CalendarAdapter

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

        binding.monthDetailToolbar.setupWithNavController(findNavController())
        //set the calendar to the year and month that was selected by user
        binding.viewModel = viewModel


        calendar.apply {
            set(Calendar.MONTH, args.monthSelected - 1)
            set(Calendar.YEAR, args.yearSelected)
            set(Calendar.DAY_OF_MONTH, 1)
        }

        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - 1 //how many days to loop for in the month

        firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK) - 1 //what view to start at


        calendarAdapter = CalendarAdapter(firstDayOfMonth, daysInMonth, CalendarAdapter.CalendarListener {studySession, textView ->
                val directions = MonthDetailFragmentDirections.actionMonthDetailFragmentToSessionDetailFragment(studySession!!)

            val extras = FragmentNavigatorExtras(
                textView to studySession.hours.toString()
            )
            findNavController().navigate(directions, extras)

        })

        setupObservers(firstDayOfMonth, daysInMonth)

        binding.rvCalendar.adapter = calendarAdapter

//        val gridLayoutManager = GridLayoutManager(requireActivity(), 7, GridLayoutManager.VERTICAL,  false)
//        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup(){
//            override fun getSpanSize(position: Int): Int {
//                return when(position){
//                    0 -> {
//                        2
//                    }
//                    else -> 1
//                }
//            }
//
//        }
//        binding.rvCalendar.layoutManager = gridLayoutManager

        postponeEnterTransition()
        binding.rvCalendar.doOnPreDraw {
            startPostponedEnterTransition()
        }
    }

    private fun setupObservers(firstDayOfMonth: Int, daysInMonth: Int) {
        viewModel.monthBarData.observe(viewLifecycleOwner, Observer {
            it?.let {
                setBarChart(it)
            }
        })

        viewModel.monthStudySessions.observe(viewLifecycleOwner){
            it?.let {
                Timber.i(it.asList().toString())
                calendarAdapter.submitList(it.asList())
            }
        }
    }



    private fun setBarChart(monthData: MonthData) {

        val description = Description()

        description.text = "Monthly total hours ${monthData.totalHours}"

        binding.monthDetailBarChart.data =
            monthData.monthBarData // set the data and list of labels into chart

        val force: Boolean = if (monthData.labels.size > 1) {
            binding.monthDetailBarChart.xAxis.setCenterAxisLabels(false)
            false
        } else {
            binding.monthDetailBarChart.xAxis.setCenterAxisLabels(true)
            true
        }

        binding.monthDetailBarChart.apply {
            xAxis.setLabelCount(
                monthData.labels.size,
                force
            ) //force = false aligns values with labels
            xAxis.valueFormatter = IndexAxisValueFormatter(monthData.labels)
            setDescription(description)
            animateY(1000)
        }
    }


}