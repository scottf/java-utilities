/*
 * Copyright (C) 2010-2019 Arondight, Inc. and Scott Fauerbach
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

class SimpleDate(val year: Int, val month: Int, val day: Int) : Comparable<SimpleDate> {

    val number = year * 10000 + month * 100 + day

    companion object Factory {
        fun newInstance(sd: SimpleDate): SimpleDate {
            return SimpleDate(sd.year, sd.month, sd.day)
        }

        fun newInstance(number: Int): SimpleDate {
            var year = number / 10000
            var month = ((number - (year * 10000)) / 100)
            val day = (number - (year * 10000) - (month * 100))
            return SimpleDate(year, month, day)
        }

        fun newInstance(cal: Calendar): SimpleDate {
            return SimpleDate(cal[Calendar.YEAR], cal[Calendar.MONTH] + 1, cal[Calendar.DATE])
        }

        fun newInstance(date: Date): SimpleDate {
            val cal = Calendar.getInstance()
            cal.time = date
            return newInstance(cal)
        }
    }

    private val DASH = "-"
    private val EMPTY = ""
    private val ZERO = "0"

    // ----------------------------------------------------------------------------------------------------
    // COMPARATOR
    // ----------------------------------------------------------------------------------------------------
    override fun compareTo(other: SimpleDate): Int {
        return number.compareTo(other.number)
    }

    fun before(o: SimpleDate?): Boolean {
        return compareTo(o!!) < 0
    }

    fun beforeOrEqual(o: SimpleDate?): Boolean {
        return compareTo(o!!) <= 0
    }

    fun after(o: SimpleDate?): Boolean {
        return compareTo(o!!) > 0
    }

    fun afterOrEqual(o: SimpleDate?): Boolean {
        return compareTo(o!!) >= 0
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as SimpleDate
        return number == that.number
    }

    fun equals(s: String?): Boolean {
        return s != null && toString() == s
    }

    fun equals(n: Number?): Boolean {
        return n != null && number == n
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

    fun getCalendar(): GregorianCalendar {
        return getCalendar(null)
    }

    fun getCalendar(tz: TimeZone?): GregorianCalendar {
        val gc = tz?.let { GregorianCalendar(it) } ?: GregorianCalendar()
        gc.set(year, month - 1, day, 0, 0, 0)
        gc.set(Calendar.MILLISECOND, 0)
        gc.time
        return gc
    }

    fun getDate(): Date? {
        return getCalendar().time
    }

    fun getMilliseconds(): Long {
        return getCalendar().timeInMillis
    }

    fun add(field: Int, increment: Int): SimpleDate {
        return newInstance(addGetCalendar(field, increment))
    }

    fun addGetCalendar(field: Int, increment: Int): GregorianCalendar {
        val gc = getCalendar()
        gc.add(field, increment)
        gc.time
        return gc
    }
}
