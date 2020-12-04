package com.example.studytime_4.ui.goal

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.studytime_4.R
import com.example.studytime_4.ui.week.WeekViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_add_goal.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AddGoalFragment : BottomSheetDialogFragment() {


    private val viewModel: AddGoalViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_goal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        btnSaveGoal.setOnClickListener {
            if(etWeeklyGoal.text.isNullOrBlank()){
                Toast.makeText(requireActivity(), "Please enter a goal", Toast.LENGTH_SHORT).show()
            }else{
                viewModel.addGoal( etWeeklyGoal.text.toString().toInt())
                dismiss()
            }
        }


    }



}