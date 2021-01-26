package com.sscott.studytime_4.ui.goal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.sscott.studytime_4.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_add_goal.*

@AndroidEntryPoint
class AddGoalFragment : BottomSheetDialogFragment() {


    private val viewModel: AddGoalViewModel by viewModels()
    private val navArgs : AddGoalFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_goal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.insertStatus.observe(viewLifecycleOwner){
            it?.let {
                dismiss()
            }
        }


        btnSaveGoal.setOnClickListener {
            if(etWeeklyGoal.text.isNullOrBlank()){
                Toast.makeText(requireActivity(), "Please enter a goal", Toast.LENGTH_SHORT).show()
            }else{

                viewModel.addGoal( etWeeklyGoal.text.toString().toInt(), navArgs.monthlyGoal)
            }
        }


    }



}