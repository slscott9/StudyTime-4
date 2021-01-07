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
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.studytime_4.MainViewModel
import com.example.studytime_4.R
import com.example.studytime_4.data.local.entities.Duration
import com.example.studytime_4.data.local.entities.StudySession
import com.example.studytime_4.databinding.FragmentTimerBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_timer.*
import kotlinx.coroutines.launch
import okhttp3.internal.format
import timber.log.Timber
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class TimerFragment : Fragment() {

    private lateinit var binding: FragmentTimerBinding
    private val viewModel: MainViewModel by activityViewModels()

    //local time weekday starts with monday as 1 and sunday as 7
    //strftime sqlite function starts with sunday as 0 and saturday as 6
    //map will convert local time weekdays to weekday numbering that strftime expects so the weekday comparison matches in the dao for getLastSevenSessions function
    private val weekDayMap = hashMapOf<Int, Int>(7 to 0, 1 to 1, 2 to 2, 3 to 3, 4 to 4, 5 to 5, 6 to 6)

    private lateinit var studySession: StudySession
    private val currentDayOfMonth = LocalDateTime.now().dayOfMonth
    private val currentMonth = LocalDateTime.now().monthValue
    private val currentWeekDay = LocalDateTime.now().dayOfWeek.value
    private val currentDate = LocalDateTime.now()
    private val currentYear = LocalDateTime.now().year
    private val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))


    private val decimalFormat = DecimalFormat("#.00")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_timer, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //insert this study session and the one after it
        viewModel.upsertStudySession(
                    StudySession(
            date = "2021-01-11",
            minutes = 50F,
            weekDay = 1,
            dayOfMonth = 11,
            month = 1,
            year = 2021,
            epochDate =  OffsetDateTime.now().toEpochSecond(),
            startTime = "2:34 pm",
            endTime = "3:26 pm",
            offsetDateTime = OffsetDateTime.now()
        )
        )

//        StudySession(
//            date = "2021-01-08",
//            minutes = 120F,
//            weekDay = 5,
//            dayOfMonth = 8,
//            month = 1,
//            year = 2021,
//            epochDate =  OffsetDateTime.now().toEpochSecond(),
//            startTime = "2:34 pm",
//            endTime = "3:26 pm",
//            offsetDateTime = OffsetDateTime.now()
//        )



//        StudySession(
//            date = "2021-01-11",
//            minutes = 50F,
//            weekDay = 1,
//            dayOfMonth = 11,
//            month = 1,
//            year = 2021,
//            epochDate =  OffsetDateTime.now().toEpochSecond(),
//            startTime = "2:34 pm",
//            endTime = "3:26 pm",
//            offsetDateTime = OffsetDateTime.now()
//        )
//
//        StudySession(
//            date = "2021-01-15",
//            minutes = 180F,
//            weekDay = 5,
//            dayOfMonth = 15,
//            month = 1,
//            year = 2021,
//            epochDate =  OffsetDateTime.now().toEpochSecond(),
//            startTime = "2:34 pm",
//            endTime = "3:26 pm",
//            offsetDateTime = OffsetDateTime.now()
//        )
//
//        StudySession(
//            date = "2021-01-27",
//            minutes = 90F,
//            weekDay = 3,
//            dayOfMonth = 27,
//            month = 1,
//            year = 2021,
//            epochDate =  OffsetDateTime.now().toEpochSecond(),
//            startTime = "2:34 pm",
//            endTime = "3:26 pm",
//            offsetDateTime = OffsetDateTime.now()
//        )

        if(viewModel.isTimeAvailable()){
            binding.timerTextInputLayout.visibility = View.GONE
        }

        setupStartButton()
        setupResetButton()
        setupNav()
        setupSaveSessionButton()

        binding.viewModel = viewModel

        viewModel.isRunning.observe(viewLifecycleOwner){
            if(it){
                binding.startButton.text = getString(R.string.start_timer_button_pause)
                viewModel.startTimer(viewModel.getCurrentTimeMilli())
            }else{
                binding.startButton.text = getString(R.string.start_timer_button)
                viewModel.setEndTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")))
                viewModel.cancelTimer()
            }
        }

        viewModel.timerFinished.observe(viewLifecycleOwner){
            if (it == true){
                saveSessionDialog()
            }
        }
    }

    private fun setupResetButton() {
        binding.btnReset.setOnClickListener {
            resetTimer()
        }
    }

    private fun resetTimer() {
        viewModel.setCurrentTimeMilli(0L)
        viewModel.setStartTimeHours(0)
        viewModel.setIsRunning(false)
        viewModel.setIsTimeAvailable(false)
        viewModel.setTimerFinished(false)
        binding.timerTextInputLayout.visibility = View.VISIBLE
    }

    private fun setupStartButton() {

        binding.startButton.setOnClickListener {

            if (binding.timerTextInputLayout.isVisible && binding.etTimeInput.text.isNullOrBlank() ) {
                Toast.makeText(
                    requireActivity(),
                    "Please enter a study duration",
                    Toast.LENGTH_SHORT
                ).show()

            }else{ //either time is left on previous timer or user is starting timer for first time

                if(binding.startButton.text == getString(R.string.start_timer_button_pause)){
                    viewModel.setIsRunning(false)
                }else{
                    if(viewModel.isTimeAvailable()){ //previous time on clock continue running timer
                        viewModel.setIsRunning(true)

                    }else{ //Clock is set for first time

                        viewModel.setCurrentTimeMilli(TimeUnit.HOURS.toMillis(etTimeInput.text.toString().toLong()))
//                        viewModel.setCurrentTimeMilli(100000)
                        viewModel.setIsTimeAvailable(true)
                        viewModel.setIsRunning(true)
                        viewModel.setStartTimeHours(etTimeInput.text.toString().toLong())
                        viewModel.setStartTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")))
                        binding.timerTextInputLayout.visibility = View.GONE

                    }
                }
                hideSoftKeyboard(it)
            }

        }
    }

    private fun setupSaveSessionButton() {

        //Adding a study session needs to insert into database
        binding.addStudySessionChip.setOnClickListener {
            if (viewModel.getStartTimeHours() == 0L) {
                Toast.makeText(
                    requireActivity(),
                    "Please enter a study duration",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                viewModel.setIsRunning(false)
                val minutesStudied = minutesStudied()
                Timber.i("minutes studied is ${minutesStudied.toString()}")
                val hoursStudied =  decimalFormat.format(minutesStudied / 60.0).toFloat() //minutesStudied / 60.0
                Timber.i("hours studied is $hoursStudied")

                studySession = StudySession(
                    minutes = decimalFormat.format(minutesStudied).toFloat(),
                    date = formattedDate, //formattedDate
                    weekDay = weekDayMap[currentWeekDay]!!, //current weekday
                    month = currentMonth,
                    dayOfMonth = currentDayOfMonth,
                    year = currentYear,
                    epochDate = OffsetDateTime.now().toEpochSecond(),
                    startTime = viewModel.getStartTime(),
                    endTime = viewModel.getEndTime(),
                    offsetDateTime = OffsetDateTime.now()
                )

                val duration = Duration(
                    date = formattedDate.toString(),
                    startTime = viewModel.getStartTime(),
                    endTime = viewModel.getEndTime(),
                    epochDate = OffsetDateTime.now().toEpochSecond(),
                    minutes = decimalFormat.format(minutesStudied).toFloat(),
                )

                resetTimer()
                viewModel.upsertStudySession(studySession)
                viewModel.insertStudyDuration(duration)
                redirectToHomeFragment()
            }
        }
    }

    private fun minutesStudied() : Float {
        Timber.i("from minutes studied function minutes studied are ${ TimeUnit.HOURS.toMillis(viewModel.getStartTimeHours()) - viewModel.getCurrentTimeMilli()}")
        Timber.i("view models currentTimeMilli is ${viewModel.getCurrentTimeMilli()}")
        Timber.i("helllloooooooooo")

        return TimeUnit.MILLISECONDS.toMinutes(
            TimeUnit.HOURS.toMillis(viewModel.getStartTimeHours()) - viewModel.getCurrentTimeMilli()
        ).toFloat()
    }


    private fun saveSessionDialog() {

        Timber.i(decimalFormat.format(viewModel.getStartTimeHours()/60).toFloat().toString())

        studySession = StudySession(
            date = formattedDate.toString(),
//            hours = decimalFormat.format(viewModel.getStartTimeHours()).toFloat(),
            minutes = decimalFormat.format(minutesStudied()).toFloat(),
            weekDay = currentWeekDay,
            dayOfMonth = currentDayOfMonth,
            month = currentMonth,
            year = currentYear,
            epochDate = OffsetDateTime.now().toEpochSecond(),
            startTime = viewModel.getStartTime(),
            endTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")),
            offsetDateTime = OffsetDateTime.now()

        )

        val duration = Duration(
            date = formattedDate.toString(),
            startTime = viewModel.getStartTime(),
            endTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")),
            epochDate = OffsetDateTime.now().toEpochSecond(),
//            hours = decimalFormat.format(viewModel.getStartTimeHours()).toFloat(),
            minutes = decimalFormat.format(minutesStudied()).toFloat(),
        )

        val dialogBuilder = AlertDialog.Builder(requireActivity())
        dialogBuilder.setMessage("Do you want to save this study session?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                resetTimer()
                viewModel.upsertStudySession(studySession)
                viewModel.insertStudyDuration(duration)
                redirectToHomeFragment()
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
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.homeFragment, false)
            .build()
        findNavController().navigate(
            TimerFragmentDirections.actionTimerFragmentToHomeFragment()
        )
    }

    private fun hideSoftKeyboard(view: View) {
        val imm =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


    override fun onPause() {
        super.onPause()
        viewModel.setIsRunning(false)
    }





}