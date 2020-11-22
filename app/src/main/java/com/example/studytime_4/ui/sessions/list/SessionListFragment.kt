package com.example.studytime_4.ui.sessions.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.studytime_4.R
import com.example.studytime_4.databinding.FragmentSessionListBinding
import com.example.studytime_4.ui.adapters.MonthListAdapter
import com.example.studytime_4.ui.adapters.YearListAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SessionListFragment : Fragment() {

    private lateinit var binding: FragmentSessionListBinding
    private val viewModel: SessionListViewModel by viewModels()
    private lateinit var yearListAdapter: YearListAdapter
    private lateinit var monthListAdapter: MonthListAdapter



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_session_list, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        yearListAdapter = YearListAdapter {
            viewModel.setYearSelected(it)

        }


        monthListAdapter = MonthListAdapter {
            findNavController().navigate(SessionListFragmentDirections.actionSessionListFragmentToMonthDetailFragment(it))
        }


        viewModel.monthsWithSessions.observe(viewLifecycleOwner, Observer {
            it?.let {
                monthListAdapter.setData(it)
            }
        })

        viewModel.yearsWithSessions.observe(viewLifecycleOwner, Observer {
            it?.let {
                yearListAdapter.setData(it)
            }
        })

        binding.monthRecyclerView.adapter = monthListAdapter
        binding.yearsListRecyclerView.adapter = yearListAdapter
    }


}