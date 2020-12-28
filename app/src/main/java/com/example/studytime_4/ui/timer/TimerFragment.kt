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


    private val decimalFormat = DecimalFormat("#,00")


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
                Timber.i("is running is true start timer")
                binding.startButton.text = getString(R.string.start_timer_button_pause)
                viewModel.startTimer(viewModel.getCurrentTimeMilli())
            }else{
                Timber.i("is running is false cancel timer")
                binding.startButton.text = getString(R.string.start_timer_button)
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
            viewModel.setCurrentTimeMilli(0L)
            viewModel.setStartTimeHours(0)
            viewModel.setIsRunning(false)
            viewModel.setIsTimeAvailable(false)
            binding.timerTextInputLayout.visibility = View.VISIBLE
        }
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
                        viewModel.setIsTimeAvailable(true)
                        viewModel.setIsRunning(true)
                        viewModel.setStartTimeHours(etTimeInput.text.toString().toInt())
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
            if (viewModel.getStartTimeHours() == 0) {
                Toast.makeText(
                    requireActivity(),
                    "Please enter a study duration",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                val minutesStudied = minutesStudied()
                val hoursStudied =  decimalFormat.format(minutesStudied / 60.0).toFloat() //minutesStudied / 60.0

                studySession = StudySession(
                    hours = hoursStudied,
                    minutes = minutesStudied/60,
                    date = formattedDate, //formattedDate
                    weekDay = weekDayMap[currentWeekDay]!!, //current weekday
                    month = currentMonth,
                    dayOfMonth = currentDayOfMonth,
                    year = currentYear,
                    epochDate = OffsetDateTime.now().toEpochSecond(),
                    startTime = viewModel.getStartTime(),
                    endTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")),
                    offsetDateTime = OffsetDateTime.now()
                )
                viewModel.upsertStudySession(studySession)
                redirectToHomeFragment()
            }
        }
    }

    private fun minutesStudied() : Long {
        return TimeUnit.MILLISECONDS.toMinutes(
            TimeUnit.HOURS.toMillis(viewModel.getStartTimeHours().toLong()) - viewModel.getCurrentTimeMilli()
        )
    }


    private fun saveSessionDialog() {
        studySession = StudySession(
            date = formattedDate.toString(),
            hours = decimalFormat.format(minutesStudied()/60).toFloat(),
            minutes = minutesStudied(),
            weekDay = currentWeekDay,
            dayOfMonth = currentDayOfMonth,
            month = currentMonth,
            year = currentYear,
            epochDate = OffsetDateTime.now().toEpochSecond(),
            startTime = viewModel.getStartTime(),
            endTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")),
            offsetDateTime = OffsetDateTime.now()

        )

        val dialogBuilder = AlertDialog.Builder(requireActivity())
        dialogBuilder.setMessage("Do you want to save this study session?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                viewModel.upsertStudySession(studySession)
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