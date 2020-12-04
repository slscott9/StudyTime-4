package com.example.studytime_4.ui.timer

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.studytime_4.R
import com.example.studytime_4.data.local.entities.StudySession
import com.example.studytime_4.databinding.FragmentTimerBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_timer.*
import okhttp3.internal.format
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class TimerFragment : Fragment() {

    private lateinit var binding: FragmentTimerBinding
    private val viewModel: TimerViewModel by viewModels()


    private var START_MILLI_SECONDS = 0L
    private var countdown_timer: CountDownTimer? = null
    var isRunning: Boolean = false;
    private var time_in_milli_seconds = 0L
    private var time_in_hours = 0L
    private var start_time_in_mili = 0L

    private lateinit var studySession: StudySession

    private val currentDayOfMonth = LocalDateTime.now().dayOfMonth
    private val currentMonth = LocalDateTime.now().monthValue
    private val currentWeekDay = LocalDateTime.now().dayOfWeek
    private val currentDate = LocalDateTime.now()
    private val currentYear = LocalDateTime.now().year
    private val numericalDayOfWeek = currentWeekDay.value

    private val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

//
//        for(i in 5 until 15){
//            viewModel.upsertStudySession(
//                StudySession(
//                    hours = i.toFloat(),
//                    minutes = 60,
//                    date = "2020-12-$i", //formattedDate
//                    weekDay = "WEDNESDAY",
//                    month = 12,
//                    dayOfMonth = i,
//                    year = 2020,
//                )
//            )
//        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_timer, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.startButton.isEnabled = false


        binding.startButton.setOnClickListener {
            if (isRunning) {
                pauseTimer()
            } else {
                val time = etTimeInput.text.toString()
                time_in_hours = time.toLong()
                start_time_in_mili = TimeUnit.HOURS.toMillis(time_in_hours)
                time_in_milli_seconds = TimeUnit.HOURS.toMillis(time_in_hours)

                startTimer(time_in_milli_seconds)
            }
        }

        binding.etTimeInput.setOnClickListener {
            binding.startButton.isEnabled = true
        }

        viewModel.insertStatus.observe(viewLifecycleOwner){
            it?.let{
                redirectToHomeFragment()
            }
        }


        //Adding a study session needs to insert into database
        binding.addStudySessionChip.setOnClickListener {
            if (binding.etTimeInput.text.isNullOrEmpty()) {
                Toast.makeText(
                    requireActivity(),
                    "Please enter a study duration",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
//                binding.startButton.text = "start"
                countdown_timer?.cancel()
//                val minutesStudied = TimeUnit.MILLISECONDS.toMinutes(start_time_in_mili - time_in_milli_seconds)
//                Timber.i("The minutes studies is ${minutesStudied}")
//                val hoursStudied = (minutesStudied / 60.0).toFloat()

                Timber.i(formattedDate)

                studySession = StudySession(
                    hours = 1F,
                    minutes = 60,
                    date = "2020-12-02", //formattedDate
                    weekDay = "WEDNESDAY",
                    month = 12,
                    dayOfMonth = 2,
                    year = 2020,

//                    hours = 1F,
//                    minutes = 60,
//                    date = "2020-12-04", //formattedDate
//                    weekDay = currentWeekDay.toString(),
//                    month = currentMonth,
//                    dayOfMonth = currentDayOfMonth,
//                    year = currentYear,


                )

                viewModel.upsertStudySession(studySession)
            }
        }

        binding.btnReset.setOnClickListener {
            resetTimer()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("savedMiliseconds", time_in_milli_seconds)
        Toast.makeText(
            requireActivity(),
            "saved milli seconds is $time_in_milli_seconds",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun pauseTimer() {
        startButton.text = "Start"
        countdown_timer?.cancel()
        isRunning = false
    }

    private fun startTimer(time_in_seconds: Long) {
        countdown_timer = object : CountDownTimer(time_in_seconds, 1000) {
            override fun onFinish() {
                saveSessionDialog()
            }

            override fun onTick(p0: Long) {
                time_in_milli_seconds = p0
                updateTextUI()
            }
        }
        countdown_timer?.start()
        isRunning = true
        startButton.text = "Pause"
    }

    private fun resetTimer() {
        binding.startButton.text = "start"
        countdown_timer?.cancel()
        time_in_milli_seconds = START_MILLI_SECONDS
        updateTextUI()
    }

    private fun updateTextUI() {

        val time = String.format(
            "%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(time_in_milli_seconds),
            TimeUnit.MILLISECONDS.toMinutes(time_in_milli_seconds) -
                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time_in_milli_seconds)), // The change is in this line
            TimeUnit.MILLISECONDS.toSeconds(time_in_milli_seconds) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time_in_milli_seconds))
        );

        tvTimerCountDown.text = "$time"
    }


    private fun saveSessionDialog() {

        studySession = StudySession(
            date = formattedDate.toString(),
            hours = time_in_hours.toFloat(),
            minutes = 0,
            weekDay = currentWeekDay.toString(),
            dayOfMonth = currentDayOfMonth,
            month = currentMonth,
            year = currentYear
        )

        val dialogBuilder = AlertDialog.Builder(requireActivity())
        dialogBuilder.setMessage("Are you sure you want to delete this grave?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                viewModel.upsertStudySession(studySession)
            }
            .setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.cancel()
            }
        val alert = dialogBuilder.create()
        alert.setTitle("Save study session")
        alert.show()
    }


    private fun redirectToHomeFragment() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.homeFragment, true) //kills login fragment so when back button is pressed from cemetery list we do not go back to login fragment
            .build()
        findNavController().navigate(
            TimerFragmentDirections.actionTimerFragmentToHomeFragment(), navOptions
        )
    }

}