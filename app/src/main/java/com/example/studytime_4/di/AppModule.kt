package com.example.studytime_4.di

import android.content.Context
import androidx.room.Room
import com.example.studytime_4.data.local.database.StudyDao
import com.example.studytime_4.data.local.database.StudyDatabase
import com.example.studytime_4.data.repo.Repository
import com.example.studytime_4.data.repo.RepositoryImpl
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


    @Singleton
    @Provides
    fun provideRepo(dao: StudyDao): Repository = RepositoryImpl(dao)
}