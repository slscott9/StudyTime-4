package com.example.studytime_4.data.local.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.studytime_4.data.local.entities.Goal
import com.example.studytime_4.data.local.entities.StudySession
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyDao {

    //Get current goal
    @Query("select * from goal_table where month = :curMonth and year = :curYear and dayOfMonth between :currentDayOfMonth - 6 and :currentDayOfMonth ")
    fun getGoalForWeek(curMonth: Int, curYear: Int, currentDayOfMonth: Int) : Flow<Goal?>

    //Save user goals
    @Update
    suspend fun updateGoal(goal: Goal)

    @Insert
    suspend fun insertGoal(goal: Goal) : Long

    @Transaction
    suspend fun upsertGoal(goal: Goal) {
        val id = insertGoal(goal)

        if(id == -1L){
            updateGoal(goal)
        }
    }


    /*
        Add Goal entity with date like 2020-12-04 query database for a date in range of todays date and todays date - 7

        if there a goal and user wants to change it update the goal that was in this range

        This ensures that we only need one goal for a week instead of inserting new goals everytime user changes a goal
     */


    //Changes for transformations
    @Query("select hours from study_table_4 where month = :currentMonth and dayOfMonth between :currentDayOfMonth - 6 and :currentDayOfMonth order by dayOfMonth asc")
    fun getLastSevenSessionsHours(currentMonth: Int, currentDayOfMonth: Int): Flow<List<Float>>


    @Query("select hours from study_table_4 where month= :monthSelected")
    fun getSessionHoursForMonth(monthSelected: Int): Flow<List<Float>>


    /*
        Should we return live data straight form the dao? The repo methods can receive parameters that we can use to query the database
     */

    @Query("select * from study_table_4 where date= :currentDate ")
    suspend fun getCurrentStudySession(currentDate: String): StudySession


    @Query("select * from study_table_4 where month= :monthSelected")
    fun getAllSessionsWithMatchingMonth(monthSelected: Int): Flow<List<StudySession>>


    /*
        To get the current week's study sessions query database for current day of week
     */

    @Query("select * from study_table_4 where month= :currentMonth and dayOfMonth between :currentDayOfMonth - 6 and :currentDayOfMonth order by dayOfMonth asc")
    fun getLastSevenSessions(currentMonth: Int, currentDayOfMonth: Int): Flow<List<StudySession>>


    //Change to return study sessions
    @Query("select distinct year from study_table_4  order by year asc")
    fun getYearsWithSessions(): Flow<List<Int>>

    @Query("select  month from study_table_4 where year = :yearSelected order by month asc")
    fun getMonthsWithSelectedYear(yearSelected : Int) : Flow<List<Int>>




    /*
        The problem is when the database is empty how do you know when to call updateStudySession or insert a study session
     */

    @Transaction
    suspend fun upsertStudySession(study: StudySession ) : Long{
        val inserted = insertStudySession(study)

        if(inserted == -1L){
            updateStudySession(study.month, study.dayOfMonth, study.hours)
        }

        return inserted
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStudySession(study: StudySession): Long

    @Query("update study_table_4 set hours = hours + :a  where dayOfMonth= :currentDayOfMonth and month= :currentMonth")
    suspend fun updateStudySession(currentMonth: Int, currentDayOfMonth: Int, a: Float )
}