package com.example.studytime_4.ui.timer

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.studytime_4.R
import com.example.studytime_4.data.local.entities.StudySession
import com.example.studytime_4.databinding.FragmentTimerBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_timer.*
import okhttp3.internal.format
import timber.log.Timber
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class TimerFragment : Fragment() {

    private lateinit var binding: FragmentTimerBinding
    private val viewModel: TimerViewModel by viewModels()


    //local time weekday starts with monday as 1 and sunday as 7
    //strftime sqlite function starts with sunday as 0 and saturday as 6
    //map will convert local time weekdays to weekday numbering that strftime expects so the weekday comparison matches in the dao for getLastSevenSessions function
    private val weekDayMap = hashMapOf<Int, Int>(7 to 0, 1 to 1, 2 to 2, 3 to 3, 4 to 4, 5 to 5, 6 to 6)



    private var START_MILLI_SECONDS = 0L
    private var countdown_timer: CountDownTimer? = null
    var isRunning: Boolean = false;
    private var time_in_milli_seconds = 0L
    private var time_in_hours = 0L
    private var start_time_in_mili = 0L

    private lateinit var studySession: StudySession
    private val currentDayOfMonth = LocalDateTime.now().dayOfMonth
    private val currentMonth = LocalDateTime.now().monthValue
    private val currentWeekDay = LocalDateTime.now().dayOfWeek.value
    private val currentDate = LocalDateTime.now()
    private val currentYear = LocalDateTime.now().year
    private val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

    private var startTime = ""
    private var endTime = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callBack = requireActivity().onBackPressedDispatcher.addCallback(this){
            findNavController().navigate(TimerFragmentDirections.actionTimerFragmentToHomeFragment())
        }



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

        setupNav()

        Timber.i("weekday is $currentWeekDay")

        binding.startButton.isEnabled = false


        binding.startButton.setOnClickListener {


            if (isRunning) {
                pauseTimer()
            } else {
                startTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"))

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

                endTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"))

                hideSoftKeyboard(it)
                binding.startButton.text = "start"
                countdown_timer?.cancel()
                val minutesStudied = TimeUnit.MILLISECONDS.toMinutes(start_time_in_mili - time_in_milli_seconds)
                val hoursStudied = (minutesStudied / 60.0).toFloat()
                Timber.i("Current weekday is $currentWeekDay")
                studySession = StudySession(
                    hours = 6F,
                    minutes = 100,
                    date = formattedDate, //formattedDate
                    weekDay = weekDayMap[currentWeekDay]!!, //current weekday
                    month = currentMonth,
                    dayOfMonth = currentDayOfMonth,
                    year = currentYear,
                    epochDate = OffsetDateTime.now().toEpochSecond(),
                    startTime = startTime,
                    endTime = endTime

//                    hours = hoursStudied,
//                    minutes = minutesStudied,
//                    date = formattedDate, //formattedDate
//                    weekDay = currentWeekDay,
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
        )

        tvTimerCountDown.text = time
    }


    private fun saveSessionDialog() {

        endTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"))


        studySession = StudySession(
            date = formattedDate.toString(),
            hours = time_in_hours.toFloat(),
            minutes = time_in_hours/60,
            weekDay = currentWeekDay,
            dayOfMonth = currentDayOfMonth,
            month = currentMonth,
            year = currentYear,
            epochDate = OffsetDateTime.now().toEpochSecond(),
            startTime = startTime,
            endTime = endTime
        )

        val dialogBuilder = AlertDialog.Builder(requireActivity())
        dialogBuilder.setMessage("Do you want to save this study session?")
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

    private fun setupNav(){
        binding.toolbar.setNavigationOnClickListener {
            redirectToHomeFragment()
        }
    }


    private fun redirectToHomeFragment() {
//        val navOptions = NavOptions.Builder()
//            .setPopUpTo(R.id.homeFragment, true)
//            .build()
        findNavController().navigate(
            TimerFragmentDirections.actionTimerFragmentToHomeFragment()
        )
    }

    private fun hideSoftKeyboard(view: View) {
        val imm =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onResume() {
        super.onResume()

//        viewModel.upsertStudySession(
//            StudySession(
//                date = "2020-12-17",
//                dayOfMonth = 17,
//                hours = 1F,
//                minutes = 60,
//                weekDay = 4,
//                month = 12,
//                year = 2020,
//                epochDate = OffsetDateTime.now().toEpochSecond() //today
//            )
//        )

//        viewModel.upsertStudySession(
//            StudySession(
//                date = "2020-12-14",
//                dayOfMonth = 14,
//                hours = 2F,
//                minutes = 120,
//                weekDay = 1,
//                month = 12,
//                year = 2020,
//                epochDate = 1607915076
//            )
//        )

//        viewModel.upsertStudySession(
//            StudySession(
//                date = "2020-12-13",
//                dayOfMonth = 13,
//                hours = 5F,
//                minutes = 120,
//                weekDay = 0,
//                month = 12,
//                year = 2020,
//                epochDate = 1607828676
//
//            )
//        )

//        viewModel.upsertStudySession(
//            StudySession(
//                date = "2020-12-14",
//                dayOfMonth = 14,
//                hours = 3F,
//                minutes = 180,
//                weekDay = 1,
//                month = 12,
//                year = 2020
//            )
//        )
//
//        viewModel.upsertStudySession(
//            StudySession(
//                date = "2020-12-19",
//                dayOfMonth = 19,
//                hours = 10F,
//                minutes = 600,
//                weekDay = 6,
//                month = 12,
//                year = 2020
//            )
//        )
    }

}