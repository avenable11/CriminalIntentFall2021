package edu.ivytech.criminalintentfall2021

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import edu.ivytech.criminalintentfall2021.database.CrimeDatabase
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.Executors

private const val DATABASE_NAME = "crime_database"
class CrimeRepository private constructor(context : Context) {
    private val database:CrimeDatabase = Room.databaseBuilder(
        context,
        CrimeDatabase::class.java,
        DATABASE_NAME
    ).build()
    private val crimeDao = database.crimeDao()
    private val executor = Executors.newSingleThreadExecutor()

    fun getCrimes() : LiveData<List<Crime>> = crimeDao.getCrimes()
    fun getCrime(id: UUID):LiveData<Crime?> = crimeDao.getCrime(id)
    fun updateCrime(crime:Crime) {
        executor.execute {crimeDao.updateCrime(crime)}
    }
    fun addCrime(crime:Crime) {
        executor.execute { crimeDao.addCrime(crime) }
    }





    companion object {
        private var INSTANCE: CrimeRepository? = null
        fun initialize(context: Context) {
            INSTANCE = CrimeRepository(context)
        }
        fun get():CrimeRepository {
            return INSTANCE?: throw IllegalStateException("Crime Reposiotry must be initialized")
        }
    }

}