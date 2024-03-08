package com.davidnardya.shifts.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidnardya.shifts.models.Guard
import com.davidnardya.shifts.models.OffTime
import com.davidnardya.shifts.models.Shift
import com.davidnardya.shifts.repositories.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    val shiftListLiveData = MutableLiveData<List<Shift>>()
    val guardsListLiveData = MutableLiveData<List<Guard>>()
    val currentGuardLiveData = MutableLiveData<Guard?>(null)
    val guardsOnVacationLiveData = MutableLiveData<List<Guard>>()

    fun getGuardList() {
        viewModelScope.launch(Dispatchers.IO) {
            guardsListLiveData.postValue(mainRepository.getGuardList())
        }
    }

    fun createShifts(requireTwoGuards: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            shiftListLiveData.postValue(mainRepository.createShifts(requireTwoGuards))
        }
    }

    fun updateCurrentGuard(guard: Guard?) {
        guard?.let {
            currentGuardLiveData.value = it
        }
    }

    fun resetCurrentGuard() {
        currentGuardLiveData.value = null
    }

    fun getGuardsOnVacation() {
        viewModelScope.launch(Dispatchers.IO) {
            guardsOnVacationLiveData.postValue(
                mainRepository.getGuardList()?.filter { it.offTime?.isNotEmpty() == true })
        }

    }

    suspend fun updateGuardDetails(oldGuard: Guard?, name: String?, offTime: List<OffTime>?) =
        mainRepository.updateGuardDetails(oldGuard, name, offTime)

    suspend fun createNewGuard(name: String?, offTime: List<OffTime>?) =
        mainRepository.createNewGuard(name, offTime)

    suspend fun deleteGuard(guard: Guard?) = mainRepository.deleteGuard(guard)
}