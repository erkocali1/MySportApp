package com.muzo.mysportapp.repositories

import com.muzo.mysportapp.db.Run
import com.muzo.mysportapp.db.RunDao
import javax.inject.Inject

class MainRepository@Inject constructor(

    val runDao:RunDao

) {
    suspend fun instertRun(run:Run)=runDao.insertRun(run)

    suspend fun deleteRun(run:Run)=runDao.deleteRun(run)

    fun getAllRunsSortedByDate()=runDao.getAllRunSortedByDate()

    fun getAllRunsSortedByDistance()=runDao.getAllRunSortedByDistance()

    fun getAllRunsSortedByTimeInMillis()=runDao.getAllRunSortedByTimeInMillis()

    fun getAllRunsSortedByAvgSpeed()=runDao.getAllRunSortedByAvgSpeed()

    fun getAllRunsSortedByCaloriesBurned()=runDao.getAllRunSortedByCaloriesBurned()

    fun getTotalAvgSpeed()=runDao.getTotalTAvgSpeed()

    fun getTotalDistance()=runDao.getTotalTDistance()

    fun getTotalCaloriesBurned()=runDao.getTotalTCaloriesBurned()

    fun getTotalTimeInMillis()=runDao.getTotalTimeInMillis()



}