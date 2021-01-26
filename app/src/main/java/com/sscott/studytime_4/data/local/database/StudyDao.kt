package com.sscott.studytime_4.data.local.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sscott.studytime_4.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyDao {


    //MONTHLY GOAL
    @Query("select * from monthly_goal_table where(year = :curYear and month = :curMonth)")
    fun monthlyGoal(curYear: Int, curMonth: Int) : Flow<MonthlyGoal?>

    @Update
    suspend fun updateMonthlyGoal(goal: MonthlyGoal) //tested

    @Insert(onConflict = OnConflictStrategy.IGNORE) //ignore if the goal exists so we can update the goal
    suspend fun insertMonthlyGoal(goal: MonthlyGoal) : Long

    @Transaction
    suspend fun upsertMonthlyGoal(goal: MonthlyGoal) : Long {
        val id = insertMonthlyGoal(goal)

        if(id == -1L){
            updateMonthlyGoal(goal)
        }

        return id
    }


    //WEEKLY GOAL
    @Query("select * from weekly_goal_table where (month = :curMonth and year = :curYear and dayOfMonth between :currentDayOfMonth - 6 and :currentDayOfMonth)") //tested
    fun weeklyGoal(curMonth: Int, curYear: Int, currentDayOfMonth: Int) : Flow<WeeklyGoal?>

    //SAVE AND UPDATE WEEKLY GOALS
    @Update
    suspend fun updateWeeklyGoal(goal: WeeklyGoal) //tested

    @Insert(onConflict = OnConflictStrategy.IGNORE) //ignore if the goal exists so we can update the goal
    suspend fun insertWeeklyGoal(goal: WeeklyGoal) : Long

    @Transaction
    suspend fun upsertWeeklyGoal(goal: WeeklyGoal) : Long {
        val id = insertWeeklyGoal(goal)

        if(id == -1L){
            updateWeeklyGoal(goal)
        }

        return id
    }


    //CHECK FOR MONTHLY AND WEEKLY GOALS
    @Query("select * from weekly_goal_table where (month = :curMonth and year = :curYear and dayOfMonth between :currentDayOfMonth - 6 and :currentDayOfMonth) ") //tested
    suspend fun checkForWeeklyGoal(curMonth: Int, curYear: Int, currentDayOfMonth: Int) : WeeklyGoal?

    @Query("select * from monthly_goal_table where (month = :curMonth and year = :curYear and dayOfMonth between :currentDayOfMonth - 6 and :currentDayOfMonth) ")
    suspend fun checkForMonthlyGoal(curMonth: Int, curYear: Int, currentDayOfMonth: Int) : MonthlyGoal?



    //GET WEEK'S HOURS AND STUDY SESSIONS
    @Query("select minutes from study_table_4 where month = :currentMonth and dayOfMonth between :currentDayOfMonth - 6 and :currentDayOfMonth order by dayOfMonth asc")
    fun weeklyHours(currentMonth: Int, currentDayOfMonth: Int): Flow<List<Float>>

    @Query("select * from study_table_4 where date(offsetDateTime) >= date('now', 'weekday 0', '-7 day') and date(offsetDateTime) <= date('now') order by dayOfMonth asc ")
    fun weeklyStudySessions(): Flow<List<StudySession>>


    //GET MONTH'S HOURS AND STUDY SESSIONS

    @Query("select minutes from study_table_4 where month= :monthSelected")
    fun monthlyHours(monthSelected: Int): Flow<List<Float>>

    @Query("select * from study_table_4 where(month= :monthSelected and year = :yearSelected) order by dayOfMonth asc")
    fun monthlyStudySessions(monthSelected: Int, yearSelected: Int): Flow<List<StudySession>>




    //GET YEARS AND MONTHS WITH STUDY SESSIONS

    @Query("select distinct year from study_table_4  order by year asc") //tested
    fun allYearsWithSessions(): Flow<List<Int>>

    @Query("select distinct month from study_table_4 where year = :yearSelected order by month asc") //tested
    fun monthsWithSessions(yearSelected : Int) : Flow<List<Int>>


    @Query("select distinct date from study_table_4 where(month = :month and year = :year)")
    fun getDateFromSelectedMonth(month: Int, year: Int) : LiveData<String>



    //SAVE AND UPDATE STUDY SESSION

    @Transaction
    suspend fun upsertStudySession(study: StudySession ) : Long{
        val inserted = insertStudySession(study)

        if(inserted == -1L){
            updateStudySession(study.month, study.dayOfMonth, study.minutes)
        }

        return inserted
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE) //tested
    suspend fun insertStudySession(study: StudySession): Long

    @Query("update study_table_4 set minutes = minutes + :minutes where dayOfMonth= :currentDayOfMonth and month= :currentMonth")
    suspend fun updateStudySession(currentMonth: Int, currentDayOfMonth: Int, minutes : Float)

//
//    @Insert
//    suspend fun insertStudySession(studySession: StudySession)

    //INSERT AND GET STUDY TIMES
    @Insert
    suspend fun insertStudyDuration(duration: Duration)


    @Transaction
    @Query("select * from study_table_4 where date = :date order by epochDate asc ")
    fun studyDurations(date : String) : LiveData<Durations>
}