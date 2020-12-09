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

        viewModel.setMonth(args.monthSelected)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendar.set(Calendar.MONTH, args.monthSelected - 1)
        calendar.set(Calendar.YEAR, args.yearSelected)
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        val firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK)
        val monthDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        Timber.i(" first day of month is $firstDayOfMonth")

        val hashMap = hashMapOf(
            0 to binding.tvSunday,
            binding.tvMonday to 1,
            binding.tvTuesday to 2,
            binding.tvWednesday to 3,
            binding.tvThursday to 4,
            binding.tvFriday to 5,
            binding.tvSaturday to 6
        )


        for(i in 0 until monthDays){

            val tv = TextView(requireActivity())

            when{
                i < 7 -> {
//                    Timber.i(i.toString())
                    drawDays(binding.tableLayout.trDayNums, i, firstDayOfMonth)
                }
                i >= 7 -> {
//                    Timber.i(i.toString())

                    drawDays(binding.tableLayout.trDayNums2, i, firstDayOfMonth)
                }
                i >= 14 -> {
                    Timber.i(i.toString())

                    drawDays(binding.tableLayout.trDayNums3, i, firstDayOfMonth)
                }
            }
        }







        viewModel.monthBarData.observe(viewLifecycleOwner, Observer {
            it?.let {
                setBarChart(it)
            }
        })
    }

    private fun drawDays(tableRow: TableRow, index: Int, firstDayOfMonth: Int) {
        val b = binding.tableLayout.trDayNums
        Timber.i("${firstDayOfMonth  + index - 1}")

        val a = b.getChildAt(firstDayOfMonth - 1 + index - 1) as TextView
        a.text = "${index + 1}"


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