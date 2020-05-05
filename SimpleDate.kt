/*
 * Copyright (C) 2018-2020 Arondight, Inc. and Scott Fauerbach
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.efiia.logstat.utils

import java.util.*

class SimpleDate private constructor(_year: Int, _month: Int, _day: Int, _number: Number?) : Comparable<SimpleDate> {

    val year: Int
    val month: Int
    val day: Int
    val number: Int

    init {
        if (_number == null) {
            year = _year
            month = _month
            day = _day
            number = year * 10000 + month * 100 + day
        }
        else {
            number = _number.toInt()
            year = number / 10000
            month = ((number - (year * 10000)) / 100)
            day = (number - (year * 10000) - (month * 100))
        }
    }

    constructor(number: Number)  : this(-1, -1, -1, number)
    constructor(year: Int, month : Int, day: Int) : this(year, month, day, null)
    constructor(sd: SimpleDate)  : this(sd.year, sd.month, sd.day, null)
    constructor(cal: Calendar)   : this(cal[Calendar.YEAR], cal[Calendar.MONTH] + 1, cal[Calendar.DATE], null)
    constructor(date: Date)      : this(Calendar.getInstance().run { time = date; this })
    constructor()                : this(Calendar.getInstance())

    companion object {
        private const val DASH = "-"
        private const val EMPTY = ""
        private const val ZERO = "0"

        fun addDays(simpleDate: SimpleDate, increment: Int): SimpleDate {
            return add(simpleDate, Calendar.DATE, increment)
        }

        fun add(simpleDate: SimpleDate, field: Int, increment: Int): SimpleDate {
            val gc = simpleDate.toCalendar()
            gc.add(field, increment)
            gc.time
            return SimpleDate(gc)
        }

    }

    override fun compareTo(other: SimpleDate): Int {
        return number.compareTo(other.number)
    }

    fun before(o: SimpleDate): Boolean {
        return compareTo(o) < 0
    }

    fun beforeOrEqual(o: SimpleDate): Boolean {
        return compareTo(o) <= 0
    }

    fun after(o: SimpleDate): Boolean {
        return compareTo(o) > 0
    }

    fun afterOrEqual(o: SimpleDate): Boolean {
        return compareTo(o) >= 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as SimpleDate
        return number == that.number
    }

    override fun hashCode(): Int {
        return number
    }

    override fun toString(): String {
        return toDashFormat()
    }

    fun toDashFormat(): String {
        return EMPTY + year + DASH + month + DASH + day
    }

    fun toDashFullFormat(): String {
        return EMPTY + year + DASH + (if (month > 9) EMPTY else ZERO) + month + DASH + (if (day > 9) EMPTY else ZERO) + day
    }

    fun toMySqlFormat(): String {
        return EMPTY + year +
            (if (month > 9) EMPTY else ZERO) + month +
            (if (day > 9) EMPTY else ZERO) + day
    }

    fun toMdyFormat(sep: String): String {
        return EMPTY + month + sep + day + sep + year
    }

    fun toYyyyMmDD(sep: String?): String {
        return if (sep == null) {
            toMySqlFormat()
        } else EMPTY + year + sep +
            (if (month > 9) EMPTY else ZERO) + month + sep +
            (if (day > 9) EMPTY else ZERO) + day
    }

    fun toCalendar(): Calendar {
        return toCalendar(null)
    }

    fun toCalendar(tz: TimeZone?): Calendar {
        val cal = Calendar.getInstance()
        if (tz != null) {
            cal.timeZone = tz
        }
        cal.set(year, month - 1, day, 0, 0, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.time
        return cal
    }

    fun toDate(): Date {
        return toCalendar().time
    }

    fun toMilliseconds(): Long {
        return toCalendar().timeInMillis
    }
}
