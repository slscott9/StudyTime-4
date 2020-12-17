package com.example.studytime_4.ui.sessions.sessionsdetail

import android.os.Bundle
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import com.example.studytime_4.R
import com.example.studytime_4.databinding.FragmentSessionDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class SessionDetailFragment : Fragment() {

    private lateinit var binding : FragmentSessionDetailBinding
    private val sessionDetailArgs : SessionDetailFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_session_detail, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        postponeEnterTransition(250, TimeUnit.MILLISECONDS)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvHourDetail.apply {
            transitionName = sessionDetailArgs.studySession.hours.toString()
            text = sessionDetailArgs.studySession.date
        }
    }

}