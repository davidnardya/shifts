package com.davidnardya.shifts.models

data class OffTime(
    val day: ShiftDay? = null,
    val didGuardAsk: Boolean? = false
)
