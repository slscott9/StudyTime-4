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
import com.example.studytime_4.databinding.FragmentMonthDetailBinding
import com.github.mikephil.charting.data.BarData
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

        viewModel.setMonthSelected(args.sessionId)

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

    private fun setBarChart(barData: BarData) {
        val xaxis = binding.monthDetailBarChart.xAxis //sets the spacing between the x labels
        xaxis.spaceBetweenLabels = 0

//        binding.monthBarChart.fitScreen()
        binding.monthDetailBarChart.data = barData // set the data and list of lables into chart
        binding.monthDetailBarChart.setDescription(viewModel.month)

//        //barDataSet.setColors(ColorTemplate.COLORFUL_COLORS)
//        barDataSet.color = resources.getColor(R.color.colorAccent)

        binding.monthDetailBarChart.animateY(2000)
    }

}