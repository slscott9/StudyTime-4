package com.sscott.studytime_4.di

import android.content.Context
import androidx.room.Room
import com.sscott.studytime_4.data.local.database.StudyDao
import com.sscott.studytime_4.data.local.database.StudyDatabase
import com.sscott.studytime_4.data.repo.Repository
import com.sscott.studytime_4.data.repo.RepositoryImpl
import com.sscott.studytime_4.other.util.time.TimeUtil
import com.sscott.studytime_4.other.util.TimeUtilImpl
import com.sscott.studytime_4.ui.usecases.monthview.MonthUseCase
import com.sscott.studytime_4.ui.usecases.monthview.MonthUseCaseImpl
import com.sscott.studytime_4.ui.usecases.weekview.WeekUseCase
import com.sscott.studytime_4.ui.usecases.weekview.WeekUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDao(db : StudyDatabase) = db.studyDao()

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, StudyDatabase::class.java, "study_database_4")
            .fallbackToDestructiveMigration()
            .build()


    //Must tell hilt how to provide interfaces
    //cannot constructor inject an interface so tell hilt how to provide the dao
    @Singleton
    @Provides
    fun provideRepo(dao: StudyDao): Repository = RepositoryImpl(dao)

    @Provides
    fun provideTimeUtil(): TimeUtil = TimeUtilImpl()
    @Singleton
    @Provides
    fun provideWeekUseCase(repository: Repository, timeUtil: TimeUtil) : WeekUseCase = WeekUseCaseImpl(repository, timeUtil)


    @Singleton
    @Provides
    fun provideMonthUseCase(repository: Repository, timeUtil: TimeUtil) : MonthUseCase = MonthUseCaseImpl(repository, timeUtil)



}