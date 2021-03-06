/*
 * Copyright (c) 2013 - 2015 http://static-interface.de and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.static_interface.sinklibrary.util;

import de.static_interface.sinklibrary.configuration.GeneralLanguage;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Most parts are from Essentials: https://github.com/essentials/Essentials/
 */

public class DateUtil {

    public static long parseDateDiff(String time, boolean future) throws Exception {
        Pattern
                timePattern =
                Pattern.compile("(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?"
                                + "(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?"
                                + "(?:([0-9]+)\\s*(?:s[a-z]*)?)?", Pattern.CASE_INSENSITIVE);
        Matcher m = timePattern.matcher(time);
        int years = 0;
        int months = 0;
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        boolean found = false;
        while (m.find()) {
            if (m.group() == null || m.group().isEmpty()) {
                continue;
            }
            for (int i = 0; i < m.groupCount(); i++) {
                if (m.group(i) != null && !m.group(i).isEmpty()) {
                    found = true;
                    break;
                }
            }
            if (found) {
                if (m.group(1) != null && !m.group(1).isEmpty()) {
                    years = Integer.parseInt(m.group(1));
                }
                if (m.group(2) != null && !m.group(2).isEmpty()) {
                    months = Integer.parseInt(m.group(2));
                }
                if (m.group(3) != null && !m.group(3).isEmpty()) {
                    weeks = Integer.parseInt(m.group(3));
                }
                if (m.group(4) != null && !m.group(4).isEmpty()) {
                    days = Integer.parseInt(m.group(4));
                }
                if (m.group(5) != null && !m.group(5).isEmpty()) {
                    hours = Integer.parseInt(m.group(5));
                }
                if (m.group(6) != null && !m.group(6).isEmpty()) {
                    minutes = Integer.parseInt(m.group(6));
                }
                if (m.group(7) != null && !m.group(7).isEmpty()) {
                    seconds = Integer.parseInt(m.group(7));
                }
                break;
            }
        }
        if (!found) {
            throw new IllegalArgumentException();
        }
        Calendar c = new GregorianCalendar();
        if (years > 0) {
            c.add(Calendar.YEAR, years * (future ? 1 : -1));
        }
        if (months > 0) {
            c.add(Calendar.MONTH, months * (future ? 1 : -1));
        }
        if (weeks > 0) {
            c.add(Calendar.WEEK_OF_YEAR, weeks * (future ? 1 : -1));
        }
        if (days > 0) {
            c.add(Calendar.DAY_OF_MONTH, days * (future ? 1 : -1));
        }
        if (hours > 0) {
            c.add(Calendar.HOUR_OF_DAY, hours * (future ? 1 : -1));
        }
        if (minutes > 0) {
            c.add(Calendar.MINUTE, minutes * (future ? 1 : -1));
        }
        if (seconds > 0) {
            c.add(Calendar.SECOND, seconds * (future ? 1 : -1));
        }
        Calendar max = new GregorianCalendar();
        max.add(Calendar.YEAR, 10);
        if (c.after(max)) {
            return max.getTimeInMillis();
        }
        return c.getTimeInMillis();
    }

    public static String formatDateDiff(long date) {
        Calendar c = new GregorianCalendar();
        c.setTimeInMillis(date);
        Calendar now = new GregorianCalendar();
        return DateUtil.formatDateDiff(now, c);
    }

    public static String formatDateDiff(long date1, long date2) {
        Calendar c = new GregorianCalendar();
        c.setTimeInMillis(date1);
        Calendar c2 = new GregorianCalendar();
        c2.setTimeInMillis(date2);
        return DateUtil.formatDateDiff(c, c2);
    }

    public static String formatDateDiff(Calendar fromDate, Calendar toDate) {
        boolean future = false;
        if (toDate.equals(fromDate)) {
            return "Jetzt";
        }
        if (toDate.after(fromDate)) {
            future = true;
        }
        StringBuilder sb = new StringBuilder();
        int[] types = new int[]
                {
                        Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND
                };

        String years = GeneralLanguage.TIMEUNIT_YEARS.format();
        String months = GeneralLanguage.TIMEUNIT_MONTHS.format();
        String days = GeneralLanguage.TIMEUNIT_DAYS.format();
        String hours = GeneralLanguage.TIMEUNIT_HOURS.format();
        String minutes = GeneralLanguage.TIMEUNIT_MINUTES.format();
        String seconds = GeneralLanguage.TIMEUNIT_SECONDS.format();
        String[] names = new String[]
                {
                        years, years, months, months, days, days, hours, hours, minutes, minutes, seconds, seconds
                };
        int accuracy = 0;
        for (int i = 0; i < types.length; i++) {
            if (accuracy > 2) {
                break;
            }
            int diff = dateDiff(types[i], fromDate, toDate, future);
            if (diff > 0) {
                accuracy++;
                int index = i * 2 + (diff > 1 ? 1 : 0);
                if (index == 10 || index == 11) { // skip seconds
                    continue;
                }
                sb.append(" ").append(diff).append(" ").append(names[index]);
            }
        }
        if (sb.length() == 0) {
            return "now";
        }
        return sb.toString().trim();
    }

    public static String formatTimeLeft(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int years = calendar.get(Calendar.YEAR) - 1970;
        int months = calendar.get(Calendar.MONTH) - 1;
        int days = calendar.get(Calendar.DAY_OF_MONTH) - 1;
        int hours = calendar.get(Calendar.HOUR_OF_DAY) - 1;
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        boolean showHours = true;
        boolean showMinutes = true;
        boolean showSeconds = true;

        String out = "";

        if (years > 0) {
            out += " " + years + " " + GeneralLanguage.TIMEUNIT_YEARS.format();
            showMinutes = false;
            showHours = false;
            showSeconds = false;
        }

        if (months > 0) {
            out += " " + months + " " + GeneralLanguage.TIMEUNIT_MONTHS.format();
            showMinutes = false;
            showHours = false;
            showSeconds = false;
        }

        if (days > 0) {
            showMinutes = false;
            showSeconds = false;
            out += " " + days + " " + GeneralLanguage.TIMEUNIT_DAYS.format();
        }

        if (hours > 0 && showHours) {
            showSeconds = false;
            out += " " + hours + " " + GeneralLanguage.TIMEUNIT_HOURS.format();
        }

        if (minutes > 0 && showMinutes) {
            out += " " + minutes + " " + GeneralLanguage.TIMEUNIT_MINUTES.format();
        }

        if (seconds > 0 && showSeconds) {
            out += " " + seconds + " " + GeneralLanguage.TIMEUNIT_SECONDS.format();
        }

        return out.trim();
    }

    static int dateDiff(int type, Calendar fromDate, Calendar toDate, boolean future) {
        int diff = 0;
        long savedDate = fromDate.getTimeInMillis();
        while ((future && !fromDate.after(toDate)) || (!future && !fromDate.before(toDate))) {
            savedDate = fromDate.getTimeInMillis();
            fromDate.add(type, future ? 1 : -1);
            diff++;
        }
        diff--;
        fromDate.setTimeInMillis(savedDate);
        return diff;
    }
}