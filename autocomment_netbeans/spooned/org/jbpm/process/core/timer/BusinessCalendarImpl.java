/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.jbpm.process.core.timer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import java.util.GregorianCalendar;
import java.io.IOException;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.format.ISOPeriodFormat;
import java.io.InputStream;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.joda.time.Period;
import java.util.Properties;
import org.kie.api.time.SessionClock;
import java.text.SimpleDateFormat;
import org.drools.core.time.TimeUtils;
import java.util.TimeZone;

/**
 * Default implementation of BusinessCalendar interface that is configured with properties.
 * Following are supported properties:
 * <ul>
 *  <li>business.hours.per.week - specifies number of working days per week (default 5)</li>
 *  <li>business.hours.per.day - specifies number of working hours per day (default 8)</li>
 *  <li>business.start.hour - specifies starting hour of work day (default 9AM)</li>
 *  <li>business.end.hour - specifies ending hour of work day (default 5PM)</li>
 *  <li>business.holidays - specifies holidays (see format section for details on how to configure it)</li>
 *  <li>business.holiday.date.format - specifies holiday date format used (default yyyy-DD-mm)</li>
 *  <li>business.weekend.days - specifies days of the weekend (default Saturday and Sunday)</li>
 *  <li>business.cal.timezone - specifies time zone to be used (if not given uses default of the system it runs on)</li>
 * </ul>
 * 
 * <b>Format</b><br/>
 * 
 * Holidays can be given in two formats:
 * <ul>
 *  <li>as date range separated with colon - for instance 2012-05-01:2012-05-15</li>
 *  <li>single day holiday - for instance 2012-05-01</li>
 * </ul>
 * each holiday period should be separated from next one with comma: 2012-05-01:2012-05-15,2012-12-24:2012-12-27
 * <br/>
 * Holiday date format must be given in pattern that is supported by <code>java.text.SimpleDateFormat</code>.<br/>
 * 
 * Weekend days should be given as integer that corresponds to <code>java.util.Calendar</code> constants.
 * <br/>
 */
public class BusinessCalendarImpl implements BusinessCalendar {
    private static final Logger logger = LoggerFactory.getLogger(BusinessCalendarImpl.class);

    private Properties businessCalendarConfiguration;

    private static final long HOUR_IN_MILLIS = (60 * 60) * 1000;

    private int daysPerWeek;

    private int hoursInDay;

    private int startHour;

    private int endHour;

    private String timezone;

    private List<BusinessCalendarImpl.TimePeriod> holidays;

    private List<Integer> weekendDays = new ArrayList<Integer>();

    private SessionClock clock;

    private static final Pattern SIMPLE = Pattern.compile("([+-])?\\s*((\\d+)[Ww])?\\s*((\\d+)[Dd])?\\s*((\\d+)[Hh])?\\s*((\\d+)[Mm])?\\s*((\\d+)[Ss])?");

    private static final int SIM_WEEK = 3;

    private static final int SIM_DAY = 5;

    private static final int SIM_HOU = 7;

    private static final int SIM_MIN = 9;

    private static final int SIM_SEC = 11;

    public static final String DAYS_PER_WEEK = "business.hours.per.week";

    public static final String HOURS_PER_DAY = "business.hours.per.day";

    public static final String START_HOUR = "business.start.hour";

    public static final String END_HOUR = "business.end.hour";

    // holidays are given as date range and can have more than one value separated with comma
    public static final String HOLIDAYS = "business.holidays";

    public static final String HOLIDAY_DATE_FORMAT = "business.holiday.date.format";

    public static final String WEEKEND_DAYS = "business.weekend.days";

    public static final String TIMEZONE = "business.cal.timezone";

    private static final String DEFAULT_PROPERTIES_NAME = "/jbpm.business.calendar.properties";

    public BusinessCalendarImpl() {
        String propertiesLocation = System.getProperty("jbpm.business.calendar.properties");
        if (propertiesLocation == null) {
            propertiesLocation = BusinessCalendarImpl.DEFAULT_PROPERTIES_NAME;
        } 
        businessCalendarConfiguration = new Properties();
        InputStream in = BusinessCalendarImpl.this.getClass().getResourceAsStream(propertiesLocation);
        if (in != null) {
            try {
                businessCalendarConfiguration.load(in);
            } catch (IOException e) {
                BusinessCalendarImpl.logger.error("Error while loading properties for business calendar", e);
            }
        } 
        init();
    }

    public BusinessCalendarImpl(Properties configuration) {
        BusinessCalendarImpl.this.businessCalendarConfiguration = configuration;
        init();
    }

    public BusinessCalendarImpl(Properties configuration, SessionClock clock) {
        BusinessCalendarImpl.this.businessCalendarConfiguration = configuration;
        BusinessCalendarImpl.this.clock = clock;
        init();
    }

    protected void init() {
        if ((BusinessCalendarImpl.this.businessCalendarConfiguration) == null) {
            throw new IllegalArgumentException("BusinessCalendar configuration was not provided.");
        } 
        daysPerWeek = getPropertyAsInt(BusinessCalendarImpl.DAYS_PER_WEEK, "5");
        hoursInDay = getPropertyAsInt(BusinessCalendarImpl.HOURS_PER_DAY, "8");
        startHour = getPropertyAsInt(BusinessCalendarImpl.START_HOUR, "9");
        endHour = getPropertyAsInt(BusinessCalendarImpl.END_HOUR, "17");
        holidays = parseHolidays();
        parseWeekendDays();
        BusinessCalendarImpl.this.timezone = businessCalendarConfiguration.getProperty(BusinessCalendarImpl.TIMEZONE);
    }

    protected String adoptISOFormat(String timeExpression) {
        try {
            Period p = null;
            if (DateTimeUtils.isPeriod(timeExpression)) {
                p = ISOPeriodFormat.standard().parsePeriod(timeExpression);
            } else {
                DateTime dt = ISODateTimeFormat.dateTimeParser().parseDateTime(timeExpression);
                Duration duration = new Duration(System.currentTimeMillis(), dt.getMillis());
                p = duration.toPeriod();
            }
            int days = p.getDays();
            int hours = p.getHours();
            int minutes = p.getMinutes();
            int seconds = p.getSeconds();
            int milis = p.getMillis();
            StringBuffer time = new StringBuffer();
            if (days > 0) {
                time.append((days + "d"));
            } 
            if (hours > 0) {
                time.append((hours + "h"));
            } 
            if (minutes > 0) {
                time.append((minutes + "m"));
            } 
            if (seconds > 0) {
                time.append((seconds + "s"));
            } 
            if (milis > 0) {
                time.append((milis + "ms"));
            } 
            return time.toString();
        } catch (Exception e) {
            return timeExpression;
        }
    }

    public long calculateBusinessTimeAsDuration(String timeExpression) {
        timeExpression = adoptISOFormat(timeExpression);
        if ((businessCalendarConfiguration) == null) {
            return TimeUtils.parseTimeString(timeExpression);
        } 
        Date calculatedDate = calculateBusinessTimeAsDate(timeExpression);
        return (calculatedDate.getTime()) - (getCurrentTime());
    }

    public Date calculateBusinessTimeAsDate(String timeExpression) {
        timeExpression = adoptISOFormat(timeExpression);
        if ((businessCalendarConfiguration) == null) {
            return new Date(TimeUtils.parseTimeString(((getCurrentTime()) + timeExpression)));
        } 
        String trimmed = timeExpression.trim();
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int min = 0;
        int sec = 0;
        if ((trimmed.length()) > 0) {
            Matcher mat = BusinessCalendarImpl.SIMPLE.matcher(trimmed);
            if (mat.matches()) {
                weeks = ((mat.group(BusinessCalendarImpl.SIM_WEEK)) != null) ? Integer.parseInt(mat.group(BusinessCalendarImpl.SIM_WEEK)) : 0;
                days = ((mat.group(BusinessCalendarImpl.SIM_DAY)) != null) ? Integer.parseInt(mat.group(BusinessCalendarImpl.SIM_DAY)) : 0;
                hours = ((mat.group(BusinessCalendarImpl.SIM_HOU)) != null) ? Integer.parseInt(mat.group(BusinessCalendarImpl.SIM_HOU)) : 0;
                min = ((mat.group(BusinessCalendarImpl.SIM_MIN)) != null) ? Integer.parseInt(mat.group(BusinessCalendarImpl.SIM_MIN)) : 0;
                sec = ((mat.group(BusinessCalendarImpl.SIM_SEC)) != null) ? Integer.parseInt(mat.group(BusinessCalendarImpl.SIM_SEC)) : 0;
            } 
        } 
        int time = 0;
        Calendar c = new GregorianCalendar();
        if ((timezone) != null) {
            c.setTimeZone(TimeZone.getTimeZone(timezone));
        } 
        if ((BusinessCalendarImpl.this.clock) != null) {
            c.setTimeInMillis(BusinessCalendarImpl.this.clock.getCurrentTime());
        } 
        // calculate number of weeks
        int numberOfWeeks = (days / (daysPerWeek)) + weeks;
        if (numberOfWeeks > 0) {
            c.add(Calendar.WEEK_OF_YEAR, numberOfWeeks);
        } 
        handleWeekend(c);
        hours += (days - (numberOfWeeks * (daysPerWeek))) * (hoursInDay);
        // calculate number of days
        int numberOfDays = hours / (hoursInDay);
        if (numberOfDays > 0) {
            for (int i = 0; i < numberOfDays; i++) {
                c.add(Calendar.DAY_OF_YEAR, 1);
                handleWeekend(c);
                handleHoliday(c);
            }
        } 
        int currentCalHour = c.get(Calendar.HOUR_OF_DAY);
        if (currentCalHour >= (endHour)) {
            c.add(Calendar.DAY_OF_YEAR, 1);
            c.add(Calendar.HOUR_OF_DAY, ((startHour) - currentCalHour));
        } else if (currentCalHour < (startHour)) {
            c.add(Calendar.HOUR_OF_DAY, startHour);
        } 
        // calculate remaining hours
        time = hours - (numberOfDays * (hoursInDay));
        c.add(Calendar.HOUR, time);
        handleWeekend(c);
        handleHoliday(c);
        currentCalHour = c.get(Calendar.HOUR_OF_DAY);
        if (currentCalHour >= (endHour)) {
            c.add(Calendar.DAY_OF_YEAR, 1);
            // set hour to the starting one
            c.set(Calendar.HOUR_OF_DAY, startHour);
            c.add(Calendar.HOUR_OF_DAY, (currentCalHour - (endHour)));
        } else if (currentCalHour < (startHour)) {
            c.add(Calendar.HOUR_OF_DAY, startHour);
        } 
        // calculate minutes
        int numberOfHours = min / 60;
        if (numberOfHours > 0) {
            c.add(Calendar.HOUR, numberOfHours);
            min = min - (numberOfHours * 60);
        } 
        c.add(Calendar.MINUTE, min);
        // calculate seconds
        int numberOfMinutes = sec / 60;
        if (numberOfMinutes > 0) {
            c.add(Calendar.MINUTE, numberOfMinutes);
            sec = sec - (numberOfMinutes * 60);
        } 
        c.add(Calendar.SECOND, sec);
        currentCalHour = c.get(Calendar.HOUR_OF_DAY);
        if (currentCalHour >= (endHour)) {
            c.add(Calendar.DAY_OF_YEAR, 1);
            // set hour to the starting one
            c.set(Calendar.HOUR_OF_DAY, startHour);
            c.add(Calendar.HOUR_OF_DAY, (currentCalHour - (endHour)));
        } else if (currentCalHour < (startHour)) {
            c.add(Calendar.HOUR_OF_DAY, startHour);
        } 
        // take under consideration weekend
        handleWeekend(c);
        // take under consideration holidays
        handleHoliday(c);
        return c.getTime();
    }

    protected void handleHoliday(Calendar c) {
        if (!(holidays.isEmpty())) {
            Date current = c.getTime();
            for (BusinessCalendarImpl.TimePeriod holiday : holidays) {
                // check each holiday if it overlaps current date and break after first match
                if ((current.after(holiday.getFrom())) && (current.before(holiday.getTo()))) {
                    Calendar tmp = new GregorianCalendar();
                    tmp.setTime(holiday.getTo());
                    Calendar tmp2 = new GregorianCalendar();
                    tmp2.setTime(current);
                    tmp2.set(Calendar.HOUR_OF_DAY, 0);
                    tmp2.set(Calendar.MINUTE, 0);
                    tmp2.set(Calendar.SECOND, 0);
                    tmp2.set(Calendar.MILLISECOND, 0);
                    long difference = (tmp.getTimeInMillis()) - (tmp2.getTimeInMillis());
                    c.add(Calendar.HOUR_OF_DAY, ((int) (difference / (BusinessCalendarImpl.HOUR_IN_MILLIS))));
                    handleWeekend(c);
                    break;
                } 
            }
        } 
    }

    protected int getPropertyAsInt(String propertyName, String defaultValue) {
        String value = businessCalendarConfiguration.getProperty(propertyName, defaultValue);
        return Integer.parseInt(value);
    }

    protected List<BusinessCalendarImpl.TimePeriod> parseHolidays() {
        String holidaysString = businessCalendarConfiguration.getProperty(BusinessCalendarImpl.HOLIDAYS);
        List<BusinessCalendarImpl.TimePeriod> holidays = new ArrayList<BusinessCalendarImpl.TimePeriod>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        if (holidaysString != null) {
            String[] hPeriods = holidaysString.split(",");
            SimpleDateFormat sdf = new SimpleDateFormat(businessCalendarConfiguration.getProperty(BusinessCalendarImpl.HOLIDAY_DATE_FORMAT, "yyyy-MM-dd"));
            for (String hPeriod : hPeriods) {
                boolean addNextYearHolidays = false;
                String[] fromTo = hPeriod.split(":");
                if (fromTo[0].startsWith("*")) {
                    addNextYearHolidays = true;
                    fromTo[0] = fromTo[0].replaceFirst("\\*", (currentYear + ""));
                } 
                try {
                    if ((fromTo.length) == 2) {
                        Calendar tmpFrom = new GregorianCalendar();
                        if ((timezone) != null) {
                            tmpFrom.setTimeZone(TimeZone.getTimeZone(timezone));
                        } 
                        tmpFrom.setTime(sdf.parse(fromTo[0]));
                        if (fromTo[1].startsWith("*")) {
                            fromTo[1] = fromTo[1].replaceFirst("\\*", (currentYear + ""));
                        } 
                        Calendar tmpTo = new GregorianCalendar();
                        if ((timezone) != null) {
                            tmpTo.setTimeZone(TimeZone.getTimeZone(timezone));
                        } 
                        tmpTo.setTime(sdf.parse(fromTo[1]));
                        Date from = tmpFrom.getTime();
                        tmpTo.add(Calendar.DAY_OF_YEAR, 1);
                        if (((tmpFrom.get(Calendar.MONTH)) > (tmpTo.get(Calendar.MONTH))) && ((tmpFrom.get(Calendar.YEAR)) == (tmpTo.get(Calendar.YEAR)))) {
                            tmpTo.add(Calendar.YEAR, 1);
                        } 
                        Date to = tmpTo.getTime();
                        holidays.add(new BusinessCalendarImpl.TimePeriod(from, to));
                        holidays.add(new BusinessCalendarImpl.TimePeriod(from, to));
                        if (addNextYearHolidays) {
                            tmpFrom = new GregorianCalendar();
                            if ((timezone) != null) {
                                tmpFrom.setTimeZone(TimeZone.getTimeZone(timezone));
                            } 
                            tmpFrom.setTime(sdf.parse(fromTo[0]));
                            tmpFrom.add(Calendar.YEAR, 1);
                            from = tmpFrom.getTime();
                            tmpTo = new GregorianCalendar();
                            if ((timezone) != null) {
                                tmpTo.setTimeZone(TimeZone.getTimeZone(timezone));
                            } 
                            tmpTo.setTime(sdf.parse(fromTo[1]));
                            tmpTo.add(Calendar.YEAR, 1);
                            tmpTo.add(Calendar.DAY_OF_YEAR, 1);
                            if (((tmpFrom.get(Calendar.MONTH)) > (tmpTo.get(Calendar.MONTH))) && ((tmpFrom.get(Calendar.YEAR)) == (tmpTo.get(Calendar.YEAR)))) {
                                tmpTo.add(Calendar.YEAR, 1);
                            } 
                            to = tmpTo.getTime();
                            holidays.add(new BusinessCalendarImpl.TimePeriod(from, to));
                        } 
                    } else {
                        Calendar c = new GregorianCalendar();
                        c.setTime(sdf.parse(fromTo[0]));
                        c.add(Calendar.DAY_OF_YEAR, 1);
                        // handle one day holiday
                        holidays.add(new BusinessCalendarImpl.TimePeriod(sdf.parse(fromTo[0]), c.getTime()));
                        if (addNextYearHolidays) {
                            Calendar tmp = Calendar.getInstance();
                            tmp.setTime(sdf.parse(fromTo[0]));
                            tmp.add(Calendar.YEAR, 1);
                            Date from = tmp.getTime();
                            c.add(Calendar.YEAR, 1);
                            holidays.add(new BusinessCalendarImpl.TimePeriod(from, c.getTime()));
                        } 
                    }
                } catch (Exception e) {
                    BusinessCalendarImpl.logger.error("Error while parsing holiday in business calendar", e);
                }
            }
        } 
        return holidays;
    }

    protected void parseWeekendDays() {
        String weekendDays = businessCalendarConfiguration.getProperty(BusinessCalendarImpl.WEEKEND_DAYS);
        if (weekendDays == null) {
            BusinessCalendarImpl.this.weekendDays.add(Calendar.SATURDAY);
            BusinessCalendarImpl.this.weekendDays.add(Calendar.SUNDAY);
        } else {
            String[] days = weekendDays.split(",");
            for (String day : days) {
                BusinessCalendarImpl.this.weekendDays.add(Integer.parseInt(day));
            }
        }
    }

    private class TimePeriod {
        private Date from;

        private Date to;

        protected TimePeriod(Date from, Date to) {
            BusinessCalendarImpl.TimePeriod.this.from = from;
            BusinessCalendarImpl.TimePeriod.this.to = to;
        }

        protected Date getFrom() {
            return BusinessCalendarImpl.TimePeriod.this.from;
        }

        protected Date getTo() {
            return BusinessCalendarImpl.TimePeriod.this.to;
        }
    }

    protected long getCurrentTime() {
        if ((clock) != null) {
            return clock.getCurrentTime();
        } else {
            return System.currentTimeMillis();
        }
    }

    protected boolean isWorkingDay(int day) {
        if (weekendDays.contains(day)) {
            return false;
        } 
        return true;
    }

    protected void handleWeekend(Calendar c) {
        int dayOfTheWeek = c.get(Calendar.DAY_OF_WEEK);
        while (!(isWorkingDay(dayOfTheWeek))) {
            c.add(Calendar.DAY_OF_YEAR, 1);
            dayOfTheWeek = c.get(Calendar.DAY_OF_WEEK);
        }
    }
}

