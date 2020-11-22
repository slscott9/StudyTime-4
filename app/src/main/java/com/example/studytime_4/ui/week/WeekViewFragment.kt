package com.example.studytime_4.ui.week

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.example.studytime_4.R
import com.example.studytime_4.databinding.FragmentWeekViewBinding
import com.github.mikephil.charting.data.BarData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeekViewFragment : Fragment() {

    private lateinit var binding : FragmentWeekViewBinding
    private val viewModel: WeekViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_week_view, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.weekBarData.observe(viewLifecycleOwner){
            it?.let {
                setBarChart(it)
            }
        }


        return binding.root

    }


    private fun setBarChart(barData: BarData) {
        binding.weekBarChart.fitScreen()
        binding.weekBarChart.data = barData // set the data and list of lables into chart
        binding.weekBarChart.setDescription("Sessions from last 7 days")

//        //barDataSet.setColors(ColorTemplate.COLORFUL_COLORS)
//        barDataSet.color = resources.getColor(R.color.colorAccent)

        binding.weekBarChart.animateY(1000)
    }


}