package registrationboardroom;

import org.apache.log4j.Logger;
import java.util.Date;
import java.util.GregorianCalendar;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TimeZone;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.servlet.http.HttpSession;

/**
 *
 * @author cisary@gmail.com
 */
public class Browser {
    public static final int LOGIN_OK = 0;
    public static final int WRONG_USERNAME = 1;
    public static final int WRONG_PASSWORD = 2;
    public static final int ALREADY_LOGGED = 3;
    public static final int NONE = -1;
    public static final int USERS = 0;
    public static final int BOARDROOMS = 1;
    private int activeCategory = NONE;
    public Hashtable<Integer,String> boardrooms;
    public Hashtable<Integer,String> freeboardrooms;
    public int boardroom_id;
    public int deltaweeks;
    private String action;
    public int user_id;
    private String username;
    private Reservation reservation;
    public TableSetting tableSetting;
    private int editReservationId;
    private String newUsername;
    private String error;
    private Hashtable<Integer,String> users;
    private String history;
    private int history_user_id;
    private Logger logger;
    private DB db;
    private String sessionId;
    private String section;
    public HttpSession session;
    
    public Browser(Logger logger,DB db,HttpSession session) {
        this.boardrooms=new Hashtable<Integer,String>();
        this.freeboardrooms=new Hashtable<Integer,String>();
        this.users=new Hashtable<Integer,String>();
        this.sessionId=session.getId();
        this.session=session;
        this.logger=logger;
        this.db=db;
        this.tableSetting=db.getUserSetting(1);
        this.boardroom_id=0;
        this.deltaweeks=0;
        this.action="";
        this.editReservationId=0;
        this.newUsername=null;
        this.username=null;
        this.history_user_id=0;
        this.section=null;
    }

    public String getSection() {
        return this.section;
    }

    public void setSection(String section) {
        this.section=section;
    }

    public void setActiveCategory(int category) {
        activeCategory=category;
    }

    public int getActiveCategory() {
        return activeCategory;
    }

    public HttpSession getSession() {
        return session;
    }

    public void setHistoryUserId(int id) {
        this.history_user_id=id;
    }

    public int getHistoryUserId() {
        return this.history_user_id;
    }

    public void setHistory(String s) {
        this.history=s;
    }

    public String getHistory() {
        return this.history;
    }

    public void setNewUsername(String newusername) {
        this.newUsername=newusername;
    }

    public String getNewUsername() {
        return this.newUsername;
    }

    public void setError(String error) {
        this.error=error;
    }

    public String getError() {
        return this.error;
    }

    public void setEditReservationId(int id) {
        this.editReservationId=id;
    }

    public int getEditReservationId() {
        return this.editReservationId;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation=reservation;
    }

    public void loadUsers() {
        try {
            users.clear();
            ResultSet rs=db.getUsers();
            while (rs.next()) {
                users.put(rs.getInt("id"),rs.getString("name")+" - "+rs.getString("phone"));
            }
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
        }
    }

    public Hashtable<Integer,String> getUsers() {
        return users;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action=action;
    }

    public void cancelAction() {
        this.action = "";
    }


    public int getUserId() {
        return user_id;
    }

    public String getUsername(){
        return username;
    }

    public Hashtable<Integer,String> getFreeBoardrooms() {
        return freeboardrooms;
    }

    public void findFreeBoardrooms() {
        freeboardrooms.clear();
        ArrayList<Integer> rooms=reservation.findFreeBoardrooms(db,logger);
        for (int i=0;i<rooms.size();i++) {
            freeboardrooms.put((Integer)rooms.get(i), db.getBoardroomName(rooms.get(i)));
        }
    }

    public String getWeekReservations(boolean admin,int user_id,int deltaWeeksFromNow,TableSetting setting,int boardroom_id) {
        Table table=new Table(db,logger,boardroom_id);
        Calendar calendar = new GregorianCalendar();
        int weeks=calendar.get(Calendar.WEEK_OF_YEAR);

        calendar.set(Calendar.WEEK_OF_YEAR, weeks);
        calendar.set(Calendar.DAY_OF_WEEK, 1);

        if (setting.getFirstDay()==Table.MONDAY)
            calendar.add(Calendar.DATE, 1);

        Date start = calendar.getTime();

        if (setting.getWeekend())
            calendar.add(Calendar.DAY_OF_WEEK, 6);
        else
            if (setting.getFirstDay()==Table.SUNDAY)
                calendar.add(Calendar.DAY_OF_WEEK, 5);
            else
                calendar.add(Calendar.DAY_OF_WEEK, 4);
        Date end = calendar.getTime();

        calendar.setTime(start);
        calendar.add(Calendar.DAY_OF_MONTH, deltaWeeksFromNow*7);
        start=calendar.getTime();

        calendar.setTime(end);
        calendar.add(Calendar.DAY_OF_MONTH, deltaWeeksFromNow*7);
        end=calendar.getTime();
        table.computeReservations(admin,user_id, TimeZone.getTimeZone(setting.getTimezone()) , start, end, setting.getStartHour(),setting.getEndHour(), setting.getPrecision(),setting.getAMPM());
        if (admin)
            return table.generateAdminReservations();
        else
            return table.generateUserReservations();
    }

    public String getWeekTable(int deltaWeeksFromNow,TableSetting setting,int boardroom_id) {
        Table table=new Table(db,logger,boardroom_id);
        Calendar calendar = new GregorianCalendar();
        int weeks=calendar.get(Calendar.WEEK_OF_YEAR);

        calendar.set(Calendar.WEEK_OF_YEAR, weeks);
        calendar.set(Calendar.DAY_OF_WEEK, 1);

        if (setting.getFirstDay()==Table.MONDAY)
            calendar.add(Calendar.DATE, 1);

        Date start = calendar.getTime();

        if (setting.getWeekend())
            calendar.add(Calendar.DAY_OF_WEEK, 6);
        else
            if (setting.getFirstDay()==Table.SUNDAY)
                calendar.add(Calendar.DAY_OF_WEEK, 5);
            else
                calendar.add(Calendar.DAY_OF_WEEK, 4);
        Date end = calendar.getTime();

        calendar.setTime(start);
        calendar.add(Calendar.DAY_OF_MONTH, deltaWeeksFromNow*7);
        start=calendar.getTime();

        calendar.setTime(end);
        calendar.add(Calendar.DAY_OF_MONTH, deltaWeeksFromNow*7);
        end=calendar.getTime();

        table.computeTable(TimeZone.getTimeZone(setting.getTimezone()),start,end,setting.getStartHour(),setting.getEndHour(),setting.getPrecision(),setting.getAMPM());
        return table.generateTable(db.getBoardroomName(boardroom_id));
    }

    public String getUserHistory(int user_id) {
        String html="<table width=\"70%\"><tr><td style=\"height:10px;text-align:center;width:200px;\" bgcolor=\"#E6EDF5\">User Name</td><td style=\"height:10px;text-align:center;width:200px;\" bgcolor=\"#E6EDF5\">Phone Number</td><td style=\"height:10px;text-align:center;width:200px;\" bgcolor=\"#E6EDF5\">Event Name</td><td style=\"height:10px;text-align:center;width:120px;\" bgcolor=\"#E6EDF5\">Start</td><td style=\"height:10px;text-align:center;width:120px;\" bgcolor=\"#E6EDF5\">End</td><td style=\"height:10px;width:20px;\" bgcolor=\"#E6EDF5\"></td><td style=\"height:10px;width:20px;\" bgcolor=\"#E6EDF5\"></td></tr>";
        try {
            ResultSet r=db.getReservationsByPhone(db.getPhoneByUserId(user_id));
            DateFormat dfdate = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            DateFormat date = new SimpleDateFormat("dd.MM.yyyy");
            DateFormat time = new SimpleDateFormat("HH:mm");
            String editcommand;
            String deletecommand;

            Date rstart;
            Date rend;
            Reservation reservation;
            if (!r.next()) {
                return "";
            }
            else
                while (r.next()) {
                    rstart=new Date(r.getDate("start").getYear(),r.getDate("start").getMonth(),r.getDate("start").getDate(),r.getTime("start").getHours(),r.getTime("start").getMinutes(),r.getTime("start").getSeconds());
                    rend=new Date(r.getDate("end").getYear(),r.getDate("end").getMonth(),r.getDate("end").getDate(),r.getTime("end").getHours(),r.getTime("end").getMinutes(),r.getTime("end").getSeconds());

                    reservation=new Reservation(r.getInt("id"),db.getPhoneByUserId(r.getInt("user_id")),r.getInt("boardroom_id"),r.getString("event_name"),rstart,rend);
                    editcommand="editEvent('"+reservation.getEventName()+"',"+String.valueOf(reservation.getId())+",'"+date.format(reservation.getStart()).toString()+"','"+time.format(reservation.getStart()).toString()+"','"+time.format(reservation.getEnd()).toString()+"')\"";
                    deletecommand="deleteEventConfirmation('"+reservation.getEventName()+"',"+String.valueOf(reservation.getId())+")\"";
                    html+="<tr><td style=\"height:10px;text-align:center;width:200px;\">"+db.getUserNameByPhone(String.valueOf(reservation.getPhone()))+"</td><td style=\"height:10px;text-align:center;width:200px;\">"+String.valueOf(reservation.getPhone())+"</td><td style=\"height:10px;text-align:center;width:200px;\">"+reservation.getEventName()+"</td><td style=\"height:10px;text-align:center;width:120px;\" >"+dfdate.format(reservation.getStart())+"</td><td style=\"height:10px;text-align:center;width:120px;\">"+dfdate.format(reservation.getEnd())+"</td><td style=\"height:10px;text-align:center;\" > <a href=\"#\" onclick=\"javascript:"+editcommand+"> <img src=\"edit.gif\" alt=\"edit\"/></a></td><td style=\"height:10px;text-align:center;vertical-align:top;\" > <a href=\"#\" onclick=\"javascript:"+deletecommand+"> <img src=\"delete.gif\" alt=\"remove\"/></a> </td></tr>";
                }
            html+="</table>";
            return html;
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
            return "";
        }
    }
    public void getBoardrooms() {
        try {
            boardrooms.clear();
            ResultSet rs=db.getBoardrooms();
            while (rs.next()) {
                boardrooms.put(rs.getInt("id"),rs.getString("name"));
            }
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
        }
    }

    public int login(String phone,String password){
        try {
            if (phone.equals("admin")) {
                user_id=1;
                username="Admin";
                if (SHA.byteArrayToHexString(SHA.computeHash(password)).equals(db.getAdministratorPassword())) {
                    tableSetting=db.getUserSetting(1);
                    if (Portal.getCache().isUsernameLogged("Admin"))
                        return Browser.ALREADY_LOGGED;
                    else {
                        Portal.getCache().addUsername("Admin");
                        return Browser.LOGIN_OK;
                    }
                }
                else
                    return Browser.WRONG_PASSWORD;
            }
            else {
                user_id=db.getUserIdByPhone(phone);
                username=db.getUserNameByPhone(phone);
                if (user_id==0)
                    return Browser.WRONG_USERNAME;

                if (SHA.byteArrayToHexString(SHA.computeHash(password)).equals(db.getUserPassword(db.getUserIdByPhone(phone)))) {
                    tableSetting=db.getUserSetting(user_id);
                    
                    if (Portal.getCache().isUsernameLogged(username))
                        return Browser.ALREADY_LOGGED;
                    else {
                        Portal.getCache().addUsername(username);
                        return Browser.LOGIN_OK;
                    }
                }
                else
                    return Browser.WRONG_PASSWORD;
            }
        } catch (Exception e) {
            logger.error("SQLException: " + e.getMessage());
            return Browser.WRONG_USERNAME;
        }
    }
}
