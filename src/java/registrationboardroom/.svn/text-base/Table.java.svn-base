package registrationboardroom;

import java.util.Hashtable;
import org.apache.log4j.Logger;
import java.util.Iterator;
import java.sql.*;
import java.util.Date;
import java.util.Map;
import java.util.Arrays;
import java.util.Vector;
import java.util.TimeZone;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Comparator;

/**
 *
 * @author cisary@gmail.com
 */
class Event {
    public int rowspan;
    public String description;

    public Event(int _rowspan,String _description){
        rowspan=_rowspan;
        description=_description;
    }
}

class Day {
    public Hashtable<Integer,Event> daytable;
    public int rows;

    public Day(){
        daytable=new Hashtable<Integer,Event>();
    }

    public void setRows(int _rows) {
        rows=_rows;
    }
}

class DateComparator implements Comparator
{
    public int compare(Object o1,Object o2)
    {
        if (((Date)((Map.Entry)o1).getKey()).before((Date)((Map.Entry)o2).getKey())){
                return(-1);
        }else if (((Date)((Map.Entry)o1).getKey()).after((Date)((Map.Entry)o2).getKey())){
                return(1);
        }else{
                return(0);
        }
    }
}


public class Table {
    public static final int BY_HOURS = 0;
    public static final int BY_30MIN = 1;
    public static final int BY_15MIN = 2;
    public static final int SUNDAY = 0;
    public static final int MONDAY = 1;

    public Hashtable<Date,Day> weektable;
    public Hashtable<Integer,String> rowtable;
    public Vector<Reservation> reservations;
    private DB db;
    static public Logger logger;
    private Table table;
    private int boardroom_id;
    private boolean ampm;

    public Table(DB db,Logger logger,int boardroom_id) {
        this.db=db;
        this.logger=logger;
        this.weektable=new Hashtable<Date,Day>();
        this.rowtable=new Hashtable<Integer,String>();
        this.boardroom_id=boardroom_id;
        this.reservations=new Vector<Reservation>();
        this.ampm=false;
    }

    public void computeReservations(boolean admin,int user_id,TimeZone timezone,Date start,Date end,int startHour,int endHour,int method,boolean _ampm) {
       try {
            Calendar calendarstart = new GregorianCalendar();
            start.setHours(startHour);
            start.setMinutes(0);
            start.setSeconds(0);
            calendarstart.setTime(start);
            calendarstart.setTimeZone(timezone);

            Calendar calendarend = new GregorianCalendar();
            end.setHours(endHour);
            end.setMinutes(0);
            end.setSeconds(0);
            calendarend.setTime(end);
            calendarend.setTimeZone(timezone);

            Calendar calendar =calendarstart;
            Date calendarday=null;
            Calendar dayend=new GregorianCalendar();
            Calendar day=new GregorianCalendar();
            //Calendar dayduration=new GregorianCalendar();

            while (calendar.before(calendarend)) {

                calendarday=calendar.getTime();
                calendarday.setHours(endHour);

                ResultSet r;
                if (admin) {
                    r=db.getReservationsOfBoardroomByRange(boardroom_id, calendar.getTime(), calendarday);
                }
                else
                    r=db.getUserReservationsOfBoardroomByRange(user_id,boardroom_id,calendar.getTime(),calendarday);

                while (r.next()) {

                    day.setTime(calendar.getTime());
                    dayend.setTime(calendarday);

                    Date rstart=new Date(r.getDate("start").getYear(),r.getDate("start").getMonth(),r.getDate("start").getDate(),r.getTime("start").getHours(),r.getTime("start").getMinutes(),r.getTime("start").getSeconds());
                    Date rend=new Date(r.getDate("end").getYear(),r.getDate("end").getMonth(),r.getDate("end").getDate(),r.getTime("end").getHours(),r.getTime("end").getMinutes(),r.getTime("end").getSeconds());

                    reservations.add(new Reservation(r.getInt("id"),db.getPhoneByUserId(r.getInt("user_id")),r.getInt("boardroom_id"),r.getString("event_name"),rstart,rend));
                }

                calendar.add(Calendar.DATE,1);
           }
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
        }
    }

    //function editEvent(name,reservation_id,date,starttime,endtime)
    //deleteEventConfirmation(name,reservation_id)

    public String generateUserReservations() {
        String html="<table width=\"70%\"><tr><td style=\"height:10px;text-align:center;width:200px;\" bgcolor=\"#E6EDF5\">Event Name</td><td style=\"height:10px;text-align:center;width:120px;\" bgcolor=\"#E6EDF5\">Start</td><td style=\"height:10px;text-align:center;width:120px;\" bgcolor=\"#E6EDF5\">End</td><td style=\"height:10px;width:20px;\" bgcolor=\"#E6EDF5\"></td><td style=\"height:10px;width:20px;\" bgcolor=\"#E6EDF5\"></td></tr>";
        Iterator it = reservations.iterator();
        Reservation r;
        DateFormat dfdate = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        DateFormat date = new SimpleDateFormat("dd.MM.yyyy");
        DateFormat time = new SimpleDateFormat("HH:mm");
        String editcommand;
        String deletecommand;
        if (!it.hasNext()) {
            return "";
        }
        else
            while (it.hasNext()) {
                r=(Reservation)it.next();
                editcommand="editEvent('"+r.getEventName()+"',"+String.valueOf(r.getId())+",'"+date.format(r.getStart()).toString()+"','"+time.format(r.getStart()).toString()+"','"+time.format(r.getEnd()).toString()+"')\"";
                deletecommand="deleteEventConfirmation('"+r.getEventName()+"',"+String.valueOf(r.getId())+")\"";
                html+="<tr><td style=\"height:10px;text-align:center;width:200px;\">"+r.getEventName()+"</td><td style=\"height:10px;text-align:center;width:120px;\" >"+dfdate.format(r.getStart())+"</td><td style=\"height:10px;text-align:center;width:120px;\">"+dfdate.format(r.getEnd())+"</td><td style=\"height:10px;text-align:center;\" > <a href=\"#\" onclick=\"javascript:"+editcommand+"> <img src=\"edit.gif\" alt=\"edit\"/></a></td><td style=\"height:10px;text-align:center;vertical-align:top;\" > <a href=\"#\" onclick=\"javascript:"+deletecommand+"> <img src=\"delete.gif\" alt=\"remove\"/></a> </td></tr>";
            }
        html+="</table>";
        return html;
    }

    public String generateAdminReservations() {
        String html="<table width=\"70%\"><tr><td style=\"height:10px;text-align:center;width:200px;\" bgcolor=\"#E6EDF5\">User Name</td><td style=\"height:10px;text-align:center;width:200px;\" bgcolor=\"#E6EDF5\">Phone Number</td><td style=\"height:10px;text-align:center;width:200px;\" bgcolor=\"#E6EDF5\">Event Name</td><td style=\"height:10px;text-align:center;width:120px;\" bgcolor=\"#E6EDF5\">Start</td><td style=\"height:10px;text-align:center;width:120px;\" bgcolor=\"#E6EDF5\">End</td><td style=\"height:10px;width:20px;\" bgcolor=\"#E6EDF5\"></td><td style=\"height:10px;width:20px;\" bgcolor=\"#E6EDF5\"></td></tr>";
        Iterator it = reservations.iterator();
        Reservation r;
        DateFormat dfdate = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        DateFormat date = new SimpleDateFormat("dd.MM.yyyy");
        DateFormat time = new SimpleDateFormat("HH:mm");
        String editcommand;
        String deletecommand;
        if (!it.hasNext()) {
            return "";
        }
        else
            while (it.hasNext()) {
                r=(Reservation)it.next();
                editcommand="editEvent('"+r.getEventName()+"',"+String.valueOf(r.getId())+",'"+date.format(r.getStart()).toString()+"','"+time.format(r.getStart()).toString()+"','"+time.format(r.getEnd()).toString()+"')\"";
                deletecommand="deleteEventConfirmation('"+r.getEventName()+"',"+String.valueOf(r.getId())+")\"";
                html+="<tr><td style=\"height:10px;text-align:center;width:200px;\">"+db.getUserNameByPhone(String.valueOf(r.getPhone()))+"</td><td style=\"height:10px;text-align:center;width:200px;\">"+String.valueOf(r.getPhone())+"</td><td style=\"height:10px;text-align:center;width:200px;\">"+r.getEventName()+"</td><td style=\"height:10px;text-align:center;width:120px;\" >"+dfdate.format(r.getStart())+"</td><td style=\"height:10px;text-align:center;width:120px;\">"+dfdate.format(r.getEnd())+"</td><td style=\"height:10px;text-align:center;\" > <a href=\"#\" onclick=\"javascript:"+editcommand+"> <img src=\"edit.gif\" alt=\"edit\"/></a></td><td style=\"height:10px;text-align:center;vertical-align:top;\" > <a href=\"#\" onclick=\"javascript:"+deletecommand+"> <img src=\"delete.gif\" alt=\"remove\"/></a> </td></tr>";
            }
        html+="</table>";
        return html;
    }

    // Recompile with -Xlint:unchecked
    //@SuppressWarnings("unchecked")  // fix Table.java uses unchecked or unsafe operations bug
    public void computeTable(TimeZone timezone,Date start,Date end,int startHour,int endHour,int method,boolean ampm){
        try {
            Calendar calendarstart = new GregorianCalendar();
            start.setHours(startHour);
            start.setMinutes(0);
            start.setSeconds(0);
            calendarstart.setTime(start);
            calendarstart.setTimeZone(timezone);

            Calendar calendarend = new GregorianCalendar();
            end.setHours(endHour);
            end.setMinutes(0);
            end.setSeconds(0);
            calendarend.setTime(end);
            calendarend.setTimeZone(timezone);

            Calendar calendar =calendarstart;
            Date calendarday=null;
            Calendar dayend=new GregorianCalendar();
            Calendar day=new GregorianCalendar();
            Calendar dayduration=new GregorianCalendar();

            int rows=0;

            while (calendar.before(calendarend)) {

                calendarday=calendar.getTime();
                calendarday.setHours(endHour);
                Day d=new Day();
                ResultSet r=db.getReservationsOfBoardroomByRange(boardroom_id,calendar.getTime(),calendarday);
                DateFormat datetimeformat = new SimpleDateFormat(Portal.tableTimeFormat);
                DateFormat ampmdatetimeformat = new SimpleDateFormat(Portal.ampmtableTimeFormat);

                boolean next=true;
                // If there are no reservations for boardroom
                if (!r.next()) {
                    day.setTime(calendar.getTime());
                    dayend.setTime(calendarday);
 
                    int i=1;
                    rowtable.put(Integer.valueOf(i), datetimeformat.format(day.getTime()).toString());
                    
                    while (day.before(dayend) || day.equals(dayend)) {
                        dayduration.setTime(day.getTime());

                        switch(method){
                            case Table.BY_HOURS: day.add(Calendar.HOUR, 1);
                                break;
                            case Table.BY_30MIN: day.add(Calendar.MINUTE, 30);
                                break;
                            case Table.BY_15MIN: day.add(Calendar.MINUTE, 15);
                                break;
                        }
                        i++;
                        if (!ampm)
                            rowtable.put(Integer.valueOf(i), datetimeformat.format(day.getTime()).toString());
                        else
                            rowtable.put(Integer.valueOf(i), ampmdatetimeformat.format(day.getTime()).toString());
                    }
                    rows=i;
                    next=false;
                } 
                while (next) {

                    day.setTime(calendar.getTime());
                    dayend.setTime(calendarday);

                    Date rstart=new Date(r.getDate("start").getYear(),r.getDate("start").getMonth(),r.getDate("start").getDate(),r.getTime("start").getHours(),r.getTime("start").getMinutes(),r.getTime("start").getSeconds());
                    Date rend=new Date(r.getDate("end").getYear(),r.getDate("end").getMonth(),r.getDate("end").getDate(),r.getTime("end").getHours(),r.getTime("end").getMinutes(),r.getTime("end").getSeconds());

                    int istart=0;
                    int iend=0;
                    int i=1;
                    boolean startfinded=false;

                    if (!ampm)
                        rowtable.put(Integer.valueOf(i), datetimeformat.format(day.getTime()).toString());
                    else
                        rowtable.put(Integer.valueOf(i), ampmdatetimeformat.format(day.getTime()).toString());

                    while (day.before(dayend) || day.equals(dayend)) {
                        dayduration.setTime(day.getTime());

                        switch(method){
                            case Table.BY_HOURS: day.add(Calendar.HOUR, 1);
                                break;
                            case Table.BY_30MIN: day.add(Calendar.MINUTE, 30);
                                break;
                            case Table.BY_15MIN: day.add(Calendar.MINUTE, 15);
                                break;
                        }

                        if ((rstart.before(dayduration.getTime()) || rstart.equals(dayduration.getTime())) && (rstart.before(day.getTime()) || rstart.equals(day.getTime())) && startfinded==false) {
                            istart=i;
                            startfinded=true;
                        }
                        if (startfinded && (rend.after(dayduration.getTime()) || rend.equals(dayduration.getTime())) && (rend.before(day.getTime()) || rend.equals(day.getTime()))) {
                            iend=i;
                        }
                        i++;
                    }
                    rows=i;
                    Event event;
                    if (r.getInt("user_id")==1) {
                        event=new Event(iend-istart+1,"Admin<br>"+ r.getString("event_name"));
                    }
                    else {
                        event=new Event(iend-istart+1,db.getUserNameByPhone(String.valueOf(db.getPhoneByUserId(r.getInt("user_id"))))+" - "+String.valueOf(db.getPhoneByUserId(r.getInt("user_id")))+"<br>"+ r.getString("event_name"));
                    }
                    d.daytable.put(Integer.valueOf(istart), event);
                    if (!r.next())
                        next=false;
                }

                d.setRows(rows);
                weektable.put(calendar.getTime(), d);
                r.close();
                calendar.add(Calendar.DATE,1);
            }
            table=this;
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
        }
    }


    @SuppressWarnings("unchecked")   //fix Table.java uses unchecked or unsafe operations bug
    public String generateTable(String boardroom_name) {
        String html="<table><tr><td style=\"vertical-align:bottom;\"><a style=\"float:left;\" href=\"javascript:void(0)\" onclick=\"window.open('./portal?printpreview=true','print','height=842, width=595,scrollbars=no')\">print</a></td><td colspan=7><center><b>"+boardroom_name+"</b></center><a style=\"float:left;\" href=\"#\" onclick=\"javascript: document.getElementById('previous').submit()\">< previous</a><a style=\"float:right;\" href=\"#\" onclick=\"javascript: document.getElementById('next').submit()\">next ></a></td> </tr><tr><td bgcolor=\"#E6EDF5\" width = \"50px\"></td>";
        if (table!=null) {
            
            Object[] array;
            DateComparator comparator = new DateComparator();
            array = table.weektable.entrySet().toArray();
            Arrays.sort(array,(Comparator)comparator);
            DateFormat sqldatetime = new SimpleDateFormat(Portal.tableDateTimeFormat);

            int rows=0;
            for(int i=0;i<array.length;++i) {
                Date element = (Date)((Map.Entry)array[i]).getKey();
                Day day=(Day)((Map.Entry)array[i]).getValue();
                String ele = sqldatetime.format(element).toString();
                
                html+="<td bgcolor=\"#E6EDF5\"  width = \"120px\"><center><b>"+ele+"</b></center></td>";
                rows=day.rows;
            }
            html+="</tr>";

            for (int i=1;i<rows;i++) {

                html+="<tr>";
                html+="<td width = \"50px\">"+table.rowtable.get(Integer.valueOf(i))+"</td>";

                for(int j=0;j<array.length;++j) {
                    Day day=(Day)((Map.Entry)array[j]).getValue();

                    if (day.daytable.containsKey(Integer.valueOf(i))) {
                        Event event=day.daytable.get(Integer.valueOf(i));
                        if (!event.description.equals("ROWSPAN"))
                            html+="<th style=\"border-color:black;\" color=\"black\" bgcolor=\"#99CCFF\" rowspan="+event.rowspan+">"+event.description+"</th>";
                        for (int y=i+1;y<event.rowspan+i;y++)
                            day.daytable.put(Integer.valueOf(y), new Event(0,"ROWSPAN"));
                    }
                    else
                        html+="<th bgcolor=\"white\"></th>";
                }
                html+="</tr>";
            }
        }
        html+="</table>";
        return html;
    }
}