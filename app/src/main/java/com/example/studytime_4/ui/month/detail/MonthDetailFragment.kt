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
    var daysHours: HashMap<Int, TextView> = hashMapOf()
    var days : HashMap<Int, TextView> = hashMapOf()
    private lateinit var calendarAdapter: CalendarAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_month_detail, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

//        daysHours = hashMapOf<Int, TextView>(
//            0 to binding.tvDay0Hours, 1 to binding.tvDay1Hours, 2 to binding.tvDay2Hours, 3 to binding.tvDay3Hours, 4 to binding.tvDay4Hours, 5 to binding.tvDay5Hours, 6 to binding.tvDay6Hours, 7 to binding.tvDay7Hours, 8 to binding.tvDay8Hours,
//            9 to binding.tvDay9Hours, 10 to binding.tvDay10Hours, 11 to binding.tvDay11Hours, 12 to binding.tvDay12Hours, 13 to binding.tvDay13Hours, 14 to binding.tvDay14Hours, 15 to binding.tvDay15Hours, 16 to binding.tvDay16Hours, 17 to binding.tvDay17Hours,
//            18 to binding.tvDay18Hours, 19 to binding.tvDay19Hours, 20 to binding.tvDay21Hours, 21 to binding.tvDay22Hours, 22 to binding.tvDay22Hours, 23 to binding.tvDay23Hours, 24 to binding.tvDay24Hours, 25 to binding.tvDay25Hours, 26 to binding.tvDay26Hours,
//            27 to binding.tvDay27Hours, 28 to binding.tvDay28Hours, 29 to binding.tvDay29Hours, 30 to binding.tvDay30Hours, 31 to binding.tvDay31Hours, 32 to binding.tvDay32Hours, 33 to binding.tvDay33Hours, 34 to binding.tvDay34Hours, 35 to binding.tvDay35Hours,
//            36 to binding.tvDay36Hours, 37 to binding.tvDay37Hours
//        )
//
//        days = hashMapOf<Int, TextView>(
//            0 to binding.tvDay0, 1 to binding.tvDay1, 2 to binding.tvDay2, 3 to binding.tvDay3, 4 to binding.tvDay4, 5 to binding.tvDay5, 6 to binding.tvDay6, 7 to binding.tvDay7, 8 to binding.tvDay8,
//            9 to binding.tvDay9, 10 to binding.tvDay10, 11 to binding.tvDay11, 12 to binding.tvDay12, 13 to binding.tvDay13, 14 to binding.tvDay14, 15 to binding.tvDay15, 16 to binding.tvDay16, 17 to binding.tvDay17,
//            18 to binding.tvDay18, 19 to binding.tvDay19, 20 to binding.tvDay20, 21 to binding.tvDay21, 22 to binding.tvDay22, 23 to binding.tvDay23, 24 to binding.tvDay24, 25 to binding.tvDay25, 26 to binding.tvDay26,
//            27 to binding.tvDay27, 28 to binding.tvDay28, 29 to binding.tvDay29, 30 to binding.tvDay30, 31 to binding.tvDay31, 32 to binding.tvDay32, 33 to binding.tvDay33, 34 to binding.tvDay34, 35 to binding.tvDay35,
//            36 to binding.tvDay36, 37 to binding.tvDay37
//        )

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

        binding.rvCalendar.doOnPreDraw {
            startPostponedEnterTransition()
        }



//        drawDays(firstDayOfMonth, daysInMonth, days)
//        setupListeners()
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
//
//    private fun setupListeners() {
//        days.forEach {
//            it.value.setOnClickListener { textview ->
//                val tv = textview as TextView
//                Toast.makeText(requireActivity(), "${tv.text}", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//
//    fun setDayswithSessions(list : List<StudySession>) {
//        list.forEach {
//            days[it.dayOfMonth - 1]?.setBackgroundResource(R.drawable.circle)
//            days[it.dayOfMonth - 1]?.let { it1 -> ViewCompat.setTransitionName(it1, "hour") }
//
//        }
//    }


//    private fun setHours(hours : FloatArray, daysHours: HashMap<Int, TextView>) {
//
//        hours.forEachIndexed { index, hour ->
//            daysHours[index]?.apply {
//                if(hour != 0F){
//                    text = when {
//                        hour > 1F -> "$hour hours"
//                        hour == 1F -> "$hour hour"
//                        else -> "${(hour *  100).toInt()} mins"
//                    }
//                }
//
//            }
//        }
//    }

//    private fun drawDays(firstDayOfMonth: Int, daysInMonth: Int, days : HashMap<Int, TextView>) {
//
//        var day = 0
//
//        for(i in firstDayOfMonth .. daysInMonth + firstDayOfMonth){
//            day += 1
//            days[i]?.apply {
//                text = "$day"
//                visibility = View.VISIBLE
//            }
//        }
//    }

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