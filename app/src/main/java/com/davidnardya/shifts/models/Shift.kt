package com.davidnardya.shifts.models

data class Shift(
    var guards: List<Guard>? = emptyList(),
    val shiftDay: ShiftDay? = ShiftDay.SUNDAY,
    val shiftTime: ShiftTime? = ShiftTime.SHIFT_00_TO_02
)

enum class ShiftDay(val text: String) {
    SUNDAY("יום ראשון"),
    MONDAY("יום שני"),
    TUESDAY("יום שלישי"),
    WEDNESDAY("יום רביעי"),
    THURSDAY("יום חמישי"),
    FRIDAY("יום שישי"),
    SATURDAY("יום שבת")
}

enum class ShiftTime(val text: String) {
    SHIFT_00_TO_02("00:00-02:00"), //0
    SHIFT_02_TO_04("02:00-04:00"), //1
    SHIFT_04_TO_06("04:00-06:00"), //2
    SHIFT_06_TO_08("06:00-08:00"), //3
    SHIFT_08_TO_19("08:00-10:00"), //4
    SHIFT_10_TO_12("10:00-12:00"), //5
    SHIFT_12_TO_14("12:00-14:00"), //6
    SHIFT_14_TO_16("14:00-16:00"), //7
    SHIFT_16_TO_18("16:00-18:00"), //8
    SHIFT_18_TO_20("18:00-20:00"), //9
    SHIFT_20_TO_22("20:00-22:00"), //10
    SHIFT_22_TO_00("22:00-00:00"), //11
}