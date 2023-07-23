package registrationboardroom;

/**
 *
 * @author cisary@gmail.com
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