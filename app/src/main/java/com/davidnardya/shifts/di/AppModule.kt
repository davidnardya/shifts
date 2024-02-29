package com.davidnardya.shifts.di

import android.content.Context
import com.davidnardya.shifts.dao.GuardsDao
import com.davidnardya.shifts.db.GuardsDataBase
import com.davidnardya.shifts.repositories.MainRepository
import com.davidnardya.shifts.viewmodels.MainViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideMainRepository(guardsDao: GuardsDao) = MainRepository(guardsDao)

    @Singleton
    @Provides
    fun provideMainViewModel(mainRepository: MainRepository) = MainViewModel(mainRepository)

    @Singleton
    @Provides
    fun provideGuardsDatabase(@ApplicationContext context: Context) =
        GuardsDataBase.getDatabase(context)

    @Singleton
    @Provides
    fun provideGuardsDao(dataBase: GuardsDataBase) = dataBase.guardsDao()
}