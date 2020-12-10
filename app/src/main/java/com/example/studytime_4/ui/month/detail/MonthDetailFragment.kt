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
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_month_detail.view.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class MonthDetailFragment : Fragment() {

//    val list = List<String>()

    private val days = hashMapOf<Int, String>(
        0 to "tvDay0", 1 to "tvDay1", 2 to "tvDay2", 3 to "tvDay3", 4 to "tvDay4", 5 to "tvDay5", 6 to "tvDay6", 7 to "tvDay7", 8 to "tvDay8",
        9 to "tvDay9", 10 to "tvDay10", 11 to "tvDays11", 12 to "tvDay12", 13 to "tvDay13", 14 to "tvDay14", 15 to "tvDay15", 16 to "tvDay16",
        17 to "tvDay17", 18 to "tvDay18", 19 to "tvDay19", 20 to "tvDay20", 21 to "tvDay21", 22 to "tvDay22", 23 to "tvDay23", 24 to "tvDay24", 25 to "tvDay25",
        26 to "tvDay26", 27 to "tvDay27", 28 to "tvDay28", 29 to "tvDay29", 30 to "tvDay30", 31 to "tvDay31", 32 to "tvDay32", 33 to "tvDay33", 34 to "tvDay34"
    )

    private lateinit var binding: FragmentMonthDetailBinding
    private val viewModel: MonthDetailViewModel by viewModels()
    private val args : MonthDetailFragmentArgs by navArgs()

    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_month_detail, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        savedInstanceState?.putInt("monthSelected", args.monthSelected)
        savedInstanceState?.putInt("yearSelected", args.yearSelected)

        viewModel.setMonth(args.monthSelected)

        viewModel.setDays(1)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.monthBarData.observe(viewLifecycleOwner, Observer {
            it?.let {
                setBarChart(it)
            }
        })

        calendar.set(Calendar.MONTH, args.monthSelected - 1)
        calendar.set(Calendar.YEAR, args.yearSelected)
        calendar.set(Calendar.DAY_OF_MONTH, 1)


        val firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK)

        val start = firstDayOfMonth - 1

        val monthDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        var count = 0



        /*
            Order in xml does matter loop is parallel with xml views within clMonthDetail

            Need to fix the last 2 view that are S M they are being set to numbers
         */
        for(i in start until 35){
                val tv = binding.clMonthDetail[i] as TextView

                count +=1

                tv.text = "$count"

        }



    }

    private fun drawDays(tableRowDays: TableRow, tableRowHours: TableRow, index: Int, firstDayOfMonth: Int) {


        val tvDayNum = tableRowDays.getChildAt(firstDayOfMonth) as TextView
        tvDayNum.text = "${index + 1}"


        val tvHour = tableRowHours.getChildAt(firstDayOfMonth) as TextView
        tvHour.text = "${5} hours"


    }

    private fun setBarChart(monthData: MonthData) {

        val force: Boolean

        binding.monthDetailBarChart.data =
            monthData.monthBarData // set the data and list of labels into chart

        if(monthData.labels.size > 1) {
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