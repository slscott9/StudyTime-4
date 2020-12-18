package com.example.studytime_4

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.studytime_4.ui.home.HomeFragmentDirections
import com.example.studytime_4.ui.month.detail.MonthDetailFragmentDirections
import com.example.studytime_4.ui.sessions.list.SessionListFragmentDirections
import com.example.studytime_4.ui.timer.TimerFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)



//        val navController = Navigation.findNavController(this, R.id.navHostFragment)
//        bottom_nav.setupWithNavController(navController)

//            .setExitAnim(R.anim.slide_in_right).setPopEnterAnim(R.anim.fade_in).setPopExitAnim(R.anim.fade_out);


//        val navOptionsBuilder = NavOptions.Builder()
//
//        bottom_nav.setOnNavigationItemSelectedListener {
//            when(it.itemId){
//                R.id.timerFragment -> {
//                    navOptionsBuilder.setExitAnim(R.anim.slide_in_right)
//                    findNavController(this, R.id.navHostFragment).navigate(R.id.timerFragment, null, navOptionsBuilder.build())
//                    true
//                }
//                R.id.homeFragment -> {
//                    navOptionsBuilder.setExitAnim(R.anim.slide_in_right)
//                    findNavController(this, R.id.navHostFragment).navigate(R.id.homeFragment, null, navOptionsBuilder.build())
//                    true
//                }
//                R.id.sessionListFragment -> {
//                    navOptionsBuilder.setExitAnim(R.anim.slide_in_right)
//                    findNavController(this, R.id.navHostFragment).navigate(R.id.sessionListFragment, null, navOptionsBuilder.build())
//                    true
//                }
//                else -> true
//            }
//        }



//
//        bottom_nav.setOnNavigationItemSelectedListener {
//            when(it.itemId){
//                R.id.timerFragment -> {
//
//                    when(findNavController(this, R.id.navHostFragment).currentDestination?.id){
//                        R.id.homeFragment -> {
//                            findNavController(this, R.id.navHostFragment).navigate(HomeFragmentDirections.actionHomeFragmentToTimerFragment())
//                        }
//                        R.id.monthDetailFragment -> {
//                            findNavController(this, R.id.navHostFragment).navigate(MonthDetailFragmentDirections.actionMonthDetailFragmentToHomeFragment())
//                        }
//                        R.id.sessionListFragment -> {
//                            findNavController(this, R.id.navHostFragment).navigate(SessionListFragmentDirections.actionSessionListFragmentToHomeFragment())
//                        }
//                        R.id.timerFragment -> {
////                            bottom_nav.visibility = View.GONE
//                        }
//                    }
//
//                    true
//                }
//
//
//                R.id.homeFragment -> {
//
//                    when(findNavController(this, R.id.navHostFragment).currentDestination?.id){
//                        R.id.timerFragment -> {
//                            findNavController(this, R.id.navHostFragment).navigate(TimerFragmentDirections.actionTimerFragmentToHomeFragment())
//                        }
//
//                        R.id.monthDetailFragment -> {
//                            findNavController(this, R.id.navHostFragment).navigate(MonthDetailFragmentDirections.actionMonthDetailFragmentToHomeFragment())
//                        }
//
//                        R.id.sessionListFragment -> {
//                            findNavController(this, R.id.navHostFragment).navigate(SessionListFragmentDirections.actionSessionListFragmentToHomeFragment())
//
//                        }
//                        R.id.homeFragment -> {}
//                    }
//                    true
//                }
//
//                R.id.sessionListFragment -> {
//                    when(findNavController(this, R.id.navHostFragment).currentDestination?.id){
//
//
//                        R.id.monthDetailFragment -> {
//                            findNavController(this, R.id.navHostFragment).navigate(MonthDetailFragmentDirections.actionMonthDetailFragmentToSessionListFragment())
//                        }
//
//                        R.id.sessionListFragment -> {
//                            findNavController(this, R.id.navHostFragment).navigate(SessionListFragmentDirections.actionSessionListFragmentToHomeFragment())
//
//                        }
//                        R.id.timerFragment -> {
//                            findNavController(this, R.id.navHostFragment).navigate(TimerFragmentDirections.actionses())
//
//                        }
//                        R.id.homeFragment -> {}
//                    }
//                    true
//                }
//                else -> false
//            }
//        }


    }





}