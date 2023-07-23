package registrationboardroom;

/**
Class named TableSetting, which is used to store settings related to the display of a table for boardroom reservations. 

TableSetting:
Represents settings for displaying the reservation table.
Contains private fields to store different settings such as timezone, startHour, endHour, precision, firstDay, weekend, and ampm.
The constructor TableSetting is used to initialize these settings when creating an object of this class.
Parameters passed to the constructor set the corresponding settings.
The _weekend and _ampm parameters are integers (0 or 1), and they are used to set boolean values (weekend and ampm, respectively) based on the integer input.
Provides getter methods to access the values of the settings.

Fields in the TableSetting class:
timezone: A string representing the timezone for the table (e.g., "GMT", "UTC", "America/New_York", etc.).
startHour: An integer representing the starting hour for the table display (e.g., 0 for midnight, 12 for noon, etc.).
endHour: An integer representing the ending hour for the table display.
precision: An integer representing the time precision of the table (e.g., by hours, 30 minutes, 15 minutes, etc.).
firstDay: An integer representing the first day of the week for the table (e.g., 0 for Sunday, 1 for Monday, etc.).
weekend: A boolean indicating whether the weekends should be included in the table or not.
ampm: A boolean indicating whether the table should display time in AM/PM format or 24-hour format.

This class is designed to hold user-defined settings for the table, 
and it can be used to configure the table's appearance and behavior according to specific preferences.
 */
public class TableSetting {
    private String timezone;
    private int startHour;
    private int endHour;
    private int precision;
    private int firstDay;
    private boolean weekend;
    private boolean ampm;

    public TableSetting(String _timezone,int _startHour,int _endHour,int _precision,int _firstDay,int _weekend,int _ampm) {
        timezone=_timezone;
        startHour=_startHour;
        endHour=_endHour;
        precision=_precision;
        firstDay=_firstDay;
        if (_weekend==0)
            weekend=false;
        else
            weekend=true;
        if (_ampm==0)
            ampm=false;
        else
            ampm=true;
    }

    public String getTimezone() {
        return timezone;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public int getFirstDay() {
        return firstDay;
    }

    public int getPrecision() {
        return precision;
    }

    public boolean getWeekend() {
        return weekend;
    }

    public boolean getAMPM() {
        return ampm;
    }
}
