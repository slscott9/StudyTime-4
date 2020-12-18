package com.example.studytime_4.ui.sessions.sessionsdetail

import android.os.Bundle
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.studytime_4.R
import com.example.studytime_4.databinding.FragmentSessionDetailBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_session_detail.view.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class SessionDetailFragment : BottomSheetDialogFragment() {

    private lateinit var binding : FragmentSessionDetailBinding
    private val sessionDetailArgs : SessionDetailFragmentArgs by navArgs()
    private val viewModel: SessionDetailViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_session_detail, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = viewModel



        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        postponeEnterTransition(250, TimeUnit.MILLISECONDS)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.clSessionDetail.apply {
            transitionName = sessionDetailArgs.studySession.hours.toString()
            tvMonthDay.text = sessionDetailArgs.studySession.month.toString()
//            tvMonthName.text = sessionDetailArgs.studySession.month.toString()
            tvYear.text = sessionDetailArgs.studySession.year.toString()
            tvHours.text = sessionDetailArgs.studySession.hours.toString()
        }
    }

}