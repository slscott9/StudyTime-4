package com.example.studytime_4.ui.month.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

@AndroidEntryPoint
class MonthDetailFragment : Fragment() {

    private lateinit var binding: FragmentMonthDetailBinding
    private val viewModel: MonthDetailViewModel by viewModels()
    private val args : MonthDetailFragmentArgs by navArgs()




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_month_detail, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.setMonth(args.sessionId)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.monthBarData.observe(viewLifecycleOwner, Observer {
            it?.let {
                setBarChart(it)
            }
        })
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