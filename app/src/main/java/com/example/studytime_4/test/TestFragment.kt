package com.example.studytime_4.test

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.example.studytime_4.R
import com.example.studytime_4.data.local.entities.StudySession
import com.example.studytime_4.databinding.FragmentTestBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_test.*
import java.time.OffsetDateTime

@AndroidEntryPoint
class TestFragment : Fragment() {

    private lateinit var binding: FragmentTestBinding
    private val viewModel : TestViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_test, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.status.observe(viewLifecycleOwner){
            it?.let {
                Toast.makeText(requireActivity(), "Inserted study session" , Toast.LENGTH_SHORT).show()

            }
        }

        binding.btnSave.setOnClickListener {

            val studySession = StudySession(
                date = etDate.text.toString(),
                hours = etHours.text.toString().toFloat(),
                minutes = etMinutes.text.toString().toLong(),
                weekDay = etWeekDay.text.toString().toInt(),
                dayOfMonth = etDayOfMonth.text.toString().toInt(),
                month = etMonth.text.toString().toInt(),
                year = etYear.text.toString().toInt(),
                epochDate = etEpochDate.text.toString().toLong(),
                startTime = etStartTime.text.toString(),
                endTime = etEndTime.text.toString(),
                offsetDateTime = OffsetDateTime.now()
            )

            viewModel.insertStudySession(studySession)


        }
    }


}