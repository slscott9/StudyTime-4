package com.sscott.studytime_4.ui.timer

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.sscott.studytime_4.MainViewModel
import com.sscott.studytime_4.R
import com.sscott.studytime_4.data.local.entities.Duration
import com.sscott.studytime_4.data.local.entities.StudySession
import com.sscott.studytime_4.databinding.FragmentTimerBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_timer.*
import timber.log.Timber
import java.text.DecimalFormat
import java.time.LocalDateTime
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

        binding.viewModel = viewModel

        binding.btnStudy.setOnClickListener {
            if(etTimeInput.text.isNullOrBlank()){
                Toast.makeText(requireContext(), "Please enter a study duration.", Toast.LENGTH_SHORT).show()
            }else{
                viewModel.setIsRunning(true)
            }
        }

        binding.btnStart.setOnClickListener {
            if(btnStart.text == getString(R.string.start_timer_button_pause)){
                viewModel.setIsRunning(false)
            }else{
                viewModel.setIsRunning(true)
            }
        }

        binding.btnReset.setOnClickListener {
            viewModel.resetTime()
        }

        viewModel.isRunning.observe(viewLifecycleOwner){
            if(it){
                startTime()
            }else{
                viewModel.stopTimer()
            }
        }

        binding.addStudySessionChip.setOnClickListener {
            viewModel.saveSession()
            redirectToHomeFragment()
        }
    }

    private fun startTime() {

        viewModel.setStartTime(TimeUnit.HOURS.toMillis(etTimeInput.text.toString().toLong()))
        viewModel.startTimer()
    }

    private fun hideTimerButtons() {
        binding.btnReset.visibility = View.VISIBLE
        binding.addStudySessionChip.visibility = View.VISIBLE
        binding.btnStudy.visibility = View.VISIBLE
    }


    private fun saveSessionDialog() {

//        Timber.i(decimalFormat.format(viewModel.getStartTimeHours()/60).toFloat().toString())
        val dialogBuilder = AlertDialog.Builder(requireActivity())
        dialogBuilder.setMessage("Do you want to save this study session?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->

                //CLEAR CLOCK
                viewModel.saveSession()
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