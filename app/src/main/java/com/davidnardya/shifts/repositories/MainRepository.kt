package com.davidnardya.shifts.repositories

import com.davidnardya.shifts.dao.GuardsDao
import com.davidnardya.shifts.models.Guard
import com.davidnardya.shifts.models.OffTime
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
                    if (guards.size == assignedGuards.size) {
                        assignedGuards.clear()
                    }
                    if (
                        shift.guards?.isEmpty() == true &&
                        guard !in assignedGuards &&
                        isAtLeast8HrsGap(shift, shifts)
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


        //Adding one guard (optional between 06:00-20:00)
        ShiftDay.entries.forEach { day ->
            val filteredShifts = shifts.filter { it.shiftDay == day }

            filteredShifts.forEach { shift ->
                shift.guards?.forEach { guard ->
                    assignedGuards.add(guard)
                }
            }

            filteredShifts.forEach { shift ->
                val listWithoutShift = filteredShifts.toMutableList()
                listWithoutShift.remove(shift)
                if (guards?.size == assignedGuards.size) {
                    assignedGuards.clear()
                }
                guards?.shuffled()?.filter { !assignedGuards.contains(it) }?.forEach { guard ->
                    if (
                        if (requireTwoGuards) {
                            ShiftTime.entries.contains(shift.shiftTime)
                        } else {
                            shift.shiftTime?.ordinal !in 4..9
                        } &&
                        shift.guards?.size == 1 &&
                        guard !in assignedGuards &&
                        isAtLeast8HrsGap(shift, listWithoutShift)
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
                dayOff.day?.ordinal == day.ordinal &&
                shift.shiftTime?.ordinal in 6..11
            ) {
                isGuardOnVacation = true
            }
            if (
                dayOff.day?.ordinal?.plus(1) == day.ordinal &&
                shift.shiftTime?.ordinal in 0..5
            ) {
                isGuardOnVacation = true
            }
        }
        return isGuardOnVacation
    }

    suspend fun updateGuardDetails(oldGuard: Guard?, name: String?, offTime: List<OffTime>?) {
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

    suspend fun createNewGuard(name: String?, offTime: List<OffTime>?) {
        guardsDao.addGuard(Guard(null, name, offTime))
    }

    suspend fun deleteGuard(guard: Guard?) {
        guard?.let {
            guardsDao.deleteGuard(it)
        }
    }
//
//    private fun isAtLeast8HrsGap(guard: Guard, laterShift: Shift, shiftsForToday: List<Shift>): Boolean {
////        var result = false
//        if(shiftsForToday.isEmpty()) {
//            return true
//        }
//
//        shiftsForToday.forEach { earlierShift ->
//            return if(earlierShift.guards?.contains(guard) == true) {
//                val later = laterShift.shiftTime?.ordinal ?: 0
//                val earlier = earlierShift.shiftTime?.ordinal ?: 0
//                later - earlier > 4
//            } else {
//                true
//            }
////            Log.d("123321","Guard: ${guard.name}, shiftTime: ${earlierShift.shiftTime?.text} result: $result")
//        }
//        return false
//    }

//    private fun isAtLeast8HrsGap(laterShift: Shift, shiftsForToday: List<Shift>): Boolean {
//        val laterShiftOrdinal = laterShift.shiftTime?.ordinal ?: -1
//        var result = false
//        var isGuardInEarlierShift = false
//
//        shiftsForToday.forEach test@ { shift ->
//            if(shift.guards?.containsAll(laterShift.guards ?: emptyList()) == false) {
//                result = false
//                isGuardInEarlierShift = true
//                return@test
//            } else {
//                result = true
//            }
//        }
//
//        if(isGuardInEarlierShift) {
//            for (shift in shiftsForToday) {
//                val shiftOrdinal = shift.shiftTime?.ordinal ?: -1
//                val hoursGap = laterShiftOrdinal - shiftOrdinal
//
//                result = hoursGap >= 8
//            }
//        }
//
//        return result
//    }

    private fun isAtLeast8HrsGap(laterShift: Shift, shiftsForToday: List<Shift>): Boolean {
        if (laterShift.guards?.isEmpty() == true) {
            return true
        }
        val laterShiftOrdinal = laterShift.shiftTime?.ordinal ?: -1
        val guard = laterShift.guards?.firstOrNull()
        var result = true
        var isGuardInEarlierShift = false

        test@ for (shift in shiftsForToday) {
            if(shift.guards?.isEmpty() == true) {
                continue@test
            }
            if (shift.guards?.contains(guard) == true) {
                isGuardInEarlierShift = true
                break@test
            }
        }

        if (isGuardInEarlierShift) {
            test@ for (shift in shiftsForToday) {
                val shiftOrdinal = shift.shiftTime?.ordinal ?: -1
                val hoursGap = laterShiftOrdinal - shiftOrdinal
                if(hoursGap > 4) {
                    result = true
                    break@test
                } else {
                    result = false
                }
            }
        }

        return result
    }

//    private fun isAtLeast8HrsGap(laterShift: Shift, shiftsForToday: List<Shift>): Boolean {
//        val guard = laterShift.guards?.firstOrNull()
//        var shiftWithGuard: Shift? = null
//        val laterShiftOrdinal = laterShift.shiftTime?.ordinal ?: -1
//
//        shiftsForToday.forEach test@ { shift ->
//            if(shift.guards?.contains(guard) == true) {
//                shiftWithGuard = shift
//                return@test
//            }
//        }
//        return if(shiftWithGuard == null) {
//             true
//        } else {
//            val shiftOrdinal = shiftWithGuard?.shiftTime?.ordinal ?: -1
//            return laterShiftOrdinal - shiftOrdinal > 4
//        }
//    }
}