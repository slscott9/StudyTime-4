package com.example.studytime_4.ui.sessions.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.studytime_4.R
import com.example.studytime_4.databinding.FragmentSessionListBinding
import com.example.studytime_4.ui.adapters.MonthListAdapter
import com.example.studytime_4.ui.adapters.YearListAdapter
import com.example.studytime_4.ui.timer.TimerFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_session_list.*

@AndroidEntryPoint
class SessionListFragment : Fragment() {

    private lateinit var binding: FragmentSessionListBinding
    private val viewModel: SessionListViewModel by viewModels()
    private lateinit var yearListAdapter: YearListAdapter
    private lateinit var monthListAdapter: MonthListAdapter

    private var yearSelected = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_left)
            .setPopUpTo(R.id.homeFragment, true)


        val callBack = requireActivity().onBackPressedDispatcher.addCallback(this){
            findNavController().navigate(SessionListFragmentDirections.actionSessionListFragmentToHomeFragment())
        }


    }



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

        setupNav()


        yearListAdapter = YearListAdapter {
            viewModel.setYearSelected(it)
            yearSelected = it

        }


        monthListAdapter = MonthListAdapter {
            findNavController().navigate(SessionListFragmentDirections.actionSessionListFragmentToMonthDetailFragment(it, yearSelected))
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

        binding.rvMonths.adapter = monthListAdapter
        binding.rvYears.adapter = yearListAdapter


    }

    private fun setupNav(){
        binding.sessionListToolbar.setNavigationOnClickListener {
            findNavController().navigate(SessionListFragmentDirections.actionSessionListFragmentToHomeFragment())
        }
    }



    private fun redirectToHomeFragment() {
        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_left)
            .setPopUpTo(R.id.homeFragment, true)

        findNavController().navigate(
            R.id.homeFragment, null, navOptions.build()
        )
    }



}