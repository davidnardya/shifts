package com.davidnardya.shifts.repositories

import com.davidnardya.shifts.dao.GuardsDao
import com.davidnardya.shifts.models.Guard
import com.davidnardya.shifts.models.Shift
import com.davidnardya.shifts.models.ShiftDay
import com.davidnardya.shifts.models.ShiftTime
import javax.inject.Inject

class MainRepository @Inject constructor(private val guardsDao: GuardsDao) {
    suspend fun getGuardList() = guardsDao.fetchGuards()

    suspend fun createShifts(requireTwoGuards: Boolean = false): List<Shift> {
        val shifts = mutableListOf<Shift>()
        val guards = getGuardList()
        val assignedGuards = mutableListOf<Guard>()

        //Creating the shifts per week
        ShiftDay.entries.forEach { day ->
            ShiftTime.entries.forEach { time ->
                shifts.add(
                    Shift(
                        shiftDay = day,
                        shiftTime = time
                    )
                )
            }
        }

        //Populating list
        ShiftDay.entries.forEach { day ->
            shifts.filter { it.shiftDay == day }.forEach { shift ->
                guards?.shuffled()?.forEach { guard ->
                    if (
                        shift.guards?.isEmpty() == true &&
                        guard !in assignedGuards
                    ) {
                        if (!isGuardOnVacation(guard, day, shift)) {
                            shift.guards = listOf(guard)
                            assignedGuards.add(guard)
                        }
                    }
                }
            }
            assignedGuards.clear()
        }


        //Adding one guard (optional between 20:00-20:00)
        ShiftDay.entries.forEach { day ->
            val filteredShifts = shifts.filter { it.shiftDay == day }

            filteredShifts.forEach { shift ->
                shift.guards?.forEach { guard ->
                    assignedGuards.add(guard)
                }
            }

            filteredShifts.forEach { shift ->
                guards?.shuffled()?.forEach { guard ->
                    if (
                        if (requireTwoGuards) {
                            ShiftTime.entries.contains(shift.shiftTime)
                        } else {
                            shift.shiftTime?.ordinal !in 4..9
                        } &&
                        shift.guards?.size == 1 &&
                        guard !in assignedGuards
                    ) {
                        if (!isGuardOnVacation(guard, day, shift)) {
                            val existingGuard = shift.guards?.first()
                            existingGuard?.let {
                                shift.guards = listOf(it, guard)
                                assignedGuards.add(guard)
                            }
                        }
                    }
                }
            }
            assignedGuards.clear()
        }

        return shifts
    }

    private fun isGuardOnVacation(
        guard: Guard,
        day: ShiftDay,
        shift: Shift
    ): Boolean {
        var isGuardOnVacation = false
        guard.offTime?.forEach { dayOff ->
            if (
                dayOff.ordinal == day.ordinal &&
                shift.shiftTime?.ordinal in 6..11
            ) {
                isGuardOnVacation = true
            }
            if (
                dayOff.ordinal + 1 == day.ordinal &&
                shift.shiftTime?.ordinal in 0..5
            ) {
                isGuardOnVacation = true
            }
        }
        return isGuardOnVacation
    }

    suspend fun updateGuardDetails(oldGuard: Guard?, name: String?, offTime: List<ShiftDay>?) {
        getGuardList()?.forEach { guardToUpdate ->
            if (guardToUpdate.name == oldGuard?.name) {
                name?.let {
                    guardToUpdate.name = it
                }
                offTime?.let {
                    guardToUpdate.offTime = it
                }
                oldGuard?.let {
                    guardsDao.deleteGuard(it)
                }
                guardsDao.addGuard(guardToUpdate)
            }

        }
    }

    suspend fun createNewGuard(name: String?, offTime: List<ShiftDay>?) {
        guardsDao.addGuard(Guard(null, name, offTime))
    }

    suspend fun deleteGuard(guard: Guard?) {
        guard?.let {
            guardsDao.deleteGuard(it)
        }
    }
}