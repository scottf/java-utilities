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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class SimpleDate implements Comparable<SimpleDate> {
    private static final int MILLIS_PER_DAY = 1000 * 60 * 60 * 24;

    private int year = -1;
    private int month = -1;
    private int day = -1;
    private int number = -1;

    protected SimpleDate(SimpleDate sd) {
        year = sd.year;
        month = sd.month;
        day = sd.day;
        number = sd.number;
    }

    public SimpleDate(String str) {
        this(str, DASH);
    }

    public SimpleDate(String str, String sep) {
        if (str != null) {
            if (sep == null || sep.length() == 0) {
                if (str.length() == 8) {
                    try {
                        init(Integer.parseInt(str.substring(0, 4)), Integer.parseInt(str.substring(4, 6)), Integer.parseInt(str.substring(6)));
                    }
                    catch (Exception e) { /* fall through, validate will throw */ }
                }
            }
            else {
                String[] split = str.split(sep);
                if (split.length == 3) {
                    try {
                        init(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
                    }
                    catch (Exception e) { /* fall through, validate will throw */ }
                }
            }
        }
        validateConstruction("Invalid date string: " + str);
    }

    public SimpleDate(int number) {
        this.number = number;
        year = number / 10000;
        int temp = number - (year * 10000);
        month = temp / 100;
        day = temp - (month * 100);
        validateConstruction("Invalid number: " + number);
    }

    public SimpleDate(int year, int month, int day) {
        init(year, month, day);
        validateConstruction("Invalid year, month or day: " + year + " " + month + " " + day);
    }

    public SimpleDate(Calendar cal) {
        init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DATE));
    }

    public SimpleDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DATE));
    }

    private void init(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        number = (year * 10000) + (month * 100) + day;
    }

    public int getNumber() {
        return number;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getYear() {
        return year;
    }

    public int chronAge() {
        SimpleDate sdNow = new SimpleDate(Calendar.getInstance());
        return (sdNow.getNumber() - getNumber()) / 10000;
    }

    public SimpleDate getFirstDayOfWeek() {
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(year, month - 1, day, 0, 0, 0);
        gc.set(Calendar.MILLISECOND, 0);
        gc.getTime();

        int addAmount = 0;
        int dow = gc.get(Calendar.DAY_OF_WEEK);
        if (dow == Calendar.SUNDAY) {
            addAmount = -6;
        }
        else if (dow != Calendar.MONDAY) {
            addAmount = Calendar.MONDAY - dow;
        }
        gc.add(Calendar.DAY_OF_MONTH, addAmount);
        gc.getTime();

        return new SimpleDate(gc);
    }

    public GregorianCalendar getCalendar() {
        return getCalendar(null);
    }

    public GregorianCalendar getCalendar(TimeZone tz) {
        GregorianCalendar gc = tz == null ? new GregorianCalendar() : new GregorianCalendar(tz);
        gc.set(year, month - 1, day, 0, 0, 0);
        gc.set(Calendar.MILLISECOND, 0);
        gc.getTime();
        return gc;
    }

    public Date getDate() {
        return getCalendar().getTime();
    }

    public long getMilliseconds() {
        return getCalendar().getTimeInMillis();
    }

    public SimpleDate add(int field, int increment) {
        return new SimpleDate(addGetCalendar(field, increment));
    }

    public GregorianCalendar addGetCalendar(int field, int increment) {
        GregorianCalendar gc = getCalendar();
        gc.add(field, increment);
        gc.getTime();
        return gc;
    }

    private static final String DASH = "-";
    private static final String EMPTY = "";
    private static final String ZERO = "0";
    private static final String FWD_SLASH = "/";

    @Override
    public String toString() {
        return toDashFormat();
    }

    public String toDashFormat() {
        return EMPTY + year + DASH + month + DASH + day;
    }

    public String toDashFullFormat() {
        return EMPTY + year + DASH + (month > 9 ? EMPTY : ZERO) + month + DASH + (day > 9 ? EMPTY : ZERO) + day;
    }

    public String toMySqlFormat() {
        return EMPTY + year +
                (month > 9 ? EMPTY : ZERO) + month +
                (day > 9 ? EMPTY : ZERO) + day;
    }

    public String toMdyFormat(String sep) {
        return EMPTY + month + sep + day + sep + year;
    }

    public String toYyyyMmDD(String sep) {
        if (sep == null) {
            return toMySqlFormat();
        }
        return EMPTY + year + sep +
                (month > 9 ? EMPTY : ZERO) + month + sep +
                (day > 9 ? EMPTY : ZERO) + day;
    }

    @Override
    public int compareTo(SimpleDate o) {
        Integer thisNumber = number;
        return thisNumber.compareTo(o.getNumber());
    }

    public boolean before(SimpleDate o) {
        return compareTo(o) < 0;
    }

    public boolean beforeOrEqual(SimpleDate o) {
        return compareTo(o) <= 0;
    }

    public boolean after(SimpleDate o) {
        return compareTo(o) > 0;
    }

    public boolean afterOrEqual(SimpleDate o) {
        return compareTo(o) >= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleDate that = (SimpleDate) o;
        return number == that.number;
    }

    public boolean equals(String s) {
        return s != null && toString().equals(s);
    }

    public boolean equals(Number n) {
        return n != null && getNumber() == n.intValue();
    }

    @Override
    public int hashCode() {
        return number;
    }

    private void validateConstruction(String message) {
        if (year < 1000 || month < 1 || month > 12 || invalidDay() ) {
            throw new IllegalArgumentException(message);
        }
    }

    private boolean invalidDay() {
        if (day < 1) { return true; }

        switch (month) {
            case 1: case 3: case 5: case 7: case 8: case 10: case 12:
                return day > 31;
            case 4: case 6: case 9: case 11:
                return day > 30;
            case 2:
                return isLeapYear() ? (day > 29) : (day > 28);
        }
        return false;
    }

    private boolean isLeapYear() {
        if (year % 400 == 0) {
            return true;
        }
        if (year % 100 == 0) {
            return false;
        }
        return (year % 4 == 0);
    }

    public static SimpleDate instanceFromEpochDate(int epochDate) {
        Calendar cal = Calendar.getInstance();
        // set time back to 0 as a base
        cal.setTimeInMillis(0);
        // add the numbers of days
        cal.add(Calendar.DATE, epochDate);
        return new SimpleDate(cal);
    }

    public int getEpochDate() {
        return (int)(getMilliseconds() / MILLIS_PER_DAY) + 1;
    }

    public static void main(String[] args) {
        //15341
        for (int x = 15341; x < 16437; x++) {
            SimpleDate sd = instanceFromEpochDate(x);
            System.out.println(sd + " " + sd.getMilliseconds() + " " + sd.getEpochDate());
            if (x != sd.getEpochDate()) {
                return;
            }
        }
        System.out.println("COMPLETE");
    }
}
