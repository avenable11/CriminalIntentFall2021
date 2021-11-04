package edu.ivytech.criminalintentfall2021.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import edu.ivytech.criminalintentfall2021.Crime

@Database(entities=[Crime::class], version=1)
@TypeConverters(CrimeTypeConverters::class)
abstract class CrimeDatabase : RoomDatabase() {
    abstract fun crimeDao():CrimeDao
}