package edu.ivytech.criminalintentfall2021

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.util.*

class CrimeDetailViewModel : ViewModel() {
    private val crimeRepository = CrimeRepository.get()
    private val crimeIdLiveData = MutableLiveData<UUID>()

    var crimeLiveData: LiveData<Crime> = Transformations.switchMap(crimeIdLiveData){
        crimeId -> crimeRepository.getCrime(crimeId)
    }

    fun loadCrime(crimeId:UUID) {
        crimeIdLiveData.value = crimeId
    }
    fun addCrime(crime:Crime){
        crimeRepository.addCrime(crime)
    }
    fun saveCrime(crime:Crime) {
        crimeRepository.updateCrime(crime)
    }

}