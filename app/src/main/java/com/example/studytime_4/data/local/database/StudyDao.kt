package com.example.studytime_4.data.local.database

import androidx.room.*
import com.example.studytime_4.data.local.entities.StudySession
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyDao {


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