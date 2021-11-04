package edu.ivytech.criminalintentfall2021.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import edu.ivytech.criminalintentfall2021.Crime
import java.util.*

@Dao
interface CrimeDao {

    @Query("Select * from crime")
    fun getCrimes(): LiveData<List<Crime>>

    //select * from crime where id = crimewewantid
    @Query("select * from crime where id = (:id)")
    fun getCrime(id:UUID):LiveData<Crime?>

    @Update
    fun updateCrime(crime:Crime)

    @Insert
    fun addCrime(crime: Crime)
}