package com.sscott.studytime_4.ui.sessions.sessionsdetail

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SessionDetailFragment : BottomSheetDialogFragment() {

//    private lateinit var binding : FragmentSessionDetailBinding
//    private val sessionDetailArgs : SessionDetailFragmentArgs by navArgs()
//    private val viewModel: SessionDetailViewModel by viewModels()
//
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_session_detail, container, false)
//        binding.lifecycleOwner = viewLifecycleOwner
//
//        binding.viewModel = viewModel
//
//
//
//        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
//        postponeEnterTransition(250, TimeUnit.MILLISECONDS)
//        return binding.root
//    }
//
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//
//
//        binding.clSessionDetail.apply {
//            transitionName = sessionDetailArgs.studySession.hours.toString()
//            tvMonthDay.text = sessionDetailArgs.studySession.month.toString()
////            tvMonthName.text = sessionDetailArgs.studySession.month.toString()
//            tvYear.text = sessionDetailArgs.studySession.year.toString()
//            tvHours.text = sessionDetailArgs.studySession.hours.toString()
//        }
//    }

}