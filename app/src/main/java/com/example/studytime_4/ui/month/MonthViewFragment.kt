package com.example.studytime_4.ui.month

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.example.studytime_4.R
import com.example.studytime_4.databinding.FragmentMonthViewBinding
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

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


        viewModel.monthBarData.observe(viewLifecycleOwner){
            it?.let {
                setBarChart(it)
            }
        }

        //Flow will only emit when terminal operator is called such as collect
//        viewModel.viewModelScope.launch {
//            viewModel.monthBarData.collect {
//                setBarChart(it)
//            }
//        }
        return binding.root
    }

    private fun setBarChart(barData: BarData) {
        val xaxis = binding.monthBarChart.xAxis //sets the spacing between the x labels
        xaxis.valueFormatter = IndexAxisValueFormatter(viewModel.monthDayLabels)
        Timber.i(viewModel.monthDayLabels.toString())
        binding.monthBarChart.xAxis.axisMaximum = 31F
        binding.monthBarChart.setVisibleXRange(1F,viewModel.monthDayLabels.size.toFloat())

//        binding.monthBarChart.fitScreen()
        binding.monthBarChart.data = barData // set the data and list of lables into chart
//        binding.monthBarChart.setDescription(viewModel.month)

//        //barDataSet.setColors(ColorTemplate.COLORFUL_COLORS)
//        barDataSet.color = resources.getColor(R.color.colorAccent)

        binding.monthBarChart.animateY(1000)
    }


}