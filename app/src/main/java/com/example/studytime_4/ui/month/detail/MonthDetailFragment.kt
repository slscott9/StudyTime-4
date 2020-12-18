package com.example.studytime_4.ui.month.detail

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.doOnPreDraw
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionManager
import com.example.studytime_4.R
import com.example.studytime_4.data.MonthData
import com.example.studytime_4.databinding.FragmentMonthDetailBinding
import com.example.studytime_4.databinding.FragmentSessionDetailBinding
import com.example.studytime_4.ui.adapters.CalendarAdapter
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_month_detail.view.*
import kotlinx.android.synthetic.main.fragment_session_detail.view.*
import java.util.*

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


        setupToolbar()
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
//                val directions = MonthDetailFragmentDirections.actionMonthDetailFragmentToSessionDetailFragment(studySession!!)
//
//            val extras = FragmentNavigatorExtras(
//                textView to studySession.hours.toString()
//            )
//            findNavController().navigate(directions, extras)

            viewModel.setStudySession(studySession!!)

            expandSessionDetail(textView)



        })

        setupObservers(firstDayOfMonth, daysInMonth)

        binding.rvCalendar.adapter = calendarAdapter


        postponeEnterTransition()
        binding.rvCalendar.doOnPreDraw {
            startPostponedEnterTransition()
        }
    }

    private fun expandSessionDetail(tv : TextView) {
        binding.run {
            binding.scrimView.visibility = View.VISIBLE

            binding.scrimView.setOnClickListener {
                collapseSessionDetail()
            }

            val transformation = MaterialContainerTransform().apply {
                startView = tv
//                endView = binding.root.sessionDetail
                endView = binding.cvSessionDetail
                scrimColor = Color.TRANSPARENT

//                addTarget(binding.root.sessionDetail)
                addTarget(binding.cvSessionDetail)
            }

            TransitionManager.beginDelayedTransition(binding.cvCalendar, transformation)
//            binding.root.sessionDetail.visibility = View.VISIBLE
            binding.cvSessionDetail.visibility = View.VISIBLE
            binding.rvCalendar.visibility = View.INVISIBLE
        }



    }


    private fun collapseSessionDetail() {
//        binding.root.sessionDetail.visibility = View.GONE
        binding.cvSessionDetail.visibility = View.GONE

        binding.rvCalendar.visibility = View.VISIBLE
        binding.scrimView.visibility = View.GONE

        val transition = MaterialContainerTransform().apply {
//            startView = binding.root.sessionDetail
            startView = binding.cvSessionDetail
            endView = binding.rvCalendar
            scrimColor = Color.TRANSPARENT

            addTarget(binding.cvCalendar)
        }

        TransitionManager.beginDelayedTransition(binding.cvCalendar, transition)

    }
    private fun setupToolbar() {
        binding.monthDetailToolbar.setNavigationOnClickListener {
            findNavController().navigate(MonthDetailFragmentDirections.actionMonthDetailFragmentToSessionListFragment())
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