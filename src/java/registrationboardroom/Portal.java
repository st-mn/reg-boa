/**
Class which provide Administration by web browser
Java servlet class called Portal, which is part of a web application for handling administration tasks related to boardroom reservations. 

Class Variables:

Several private constants like cleaningService, defaultServiceRefreshMinutes, defaultSessionTimeoutMinutes, mysqlDateTimeFormat, tableDateTimeFormat, etc., 
are defined to store configuration values and date/time formats.
sessionTimeoutMinutes and serviceRefreshMinutes are static variables to store the session timeout and service refresh interval values, respectively.
created is a static variable holding the date when the servlet was created.
logger is an instance of the Logger class from Apache Log4j library used for logging.
db is an instance of the DB class representing the database connection and operations.
timezones is an array holding available time zone IDs.
Init() Method: This method is executed when the servlet is initialized. 
It sets up the logger, gets available timezones, and initializes the database connection.

doGet() Method: This method is executed when a GET request is received from the web browser. 
It forwards the request to the appropriate JSP (JavaServer Pages) file (index.jsp or print.jsp) based on the request parameters.

doPost() Method: This method is executed when a POST request is received from the web browser. 
It handles various operations based on the values of the command parameter received from the request. 
The operations include logging in, logging out, changing user settings, managing reservations, managing users, and boardrooms.

Database Operations: The code interacts with the database using the DB class (not provided in the code snippet) for various tasks, 
including retrieving user data, boardrooms, reservations, updating user settings, adding reservations, etc.

Other Utility Methods: There are some utility methods like getCache() and getCreated() to access the cache and created variables, respectively.

Cache Management: The code utilizes a Cache class (not provided in the code snippet) for caching data and 
starting/stopping a cleaning service for cache management.

Portal Class provides servlet that does Administration of Registration boardroom in the web browser.


 */

package registrationboardroom;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import java.util.Enumeration;
import java.util.Date;
import java.util.TimeZone;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.sql.*;


public class Portal extends HttpServlet {
    private static final long serialVersionUID = -31646456290060948L;
    private static final boolean cleaningService = true;
    private static final int defaultServiceRefreshMinutes = 1;
    private static final int defaultSessionTimeoutMinutes = 1;
    public static final String mysqlDateTimeFormat = "yyyy-MM-dd HH:mm:ss";
    public static final String tableDateTimeFormat = "dd.MM yyyy <\'br\'><\'b\'>EE</\'b'>";
    public static final String tableTimeFormat = "H:mm";
    public static final String ampmtableTimeFormat = "hh:mm aa";
    private static int sessionTimeoutMinutes;
    private static int serviceRefreshMinutes;
    private static Date created;
    private static Logger logger = null; // Logger
    private static DB db = null;
    public static String[] timezones;
    private static Cache cache;

    public static Cache getCache() {
        return cache;
    }

    public static Date getCreated() {
        return created;
    }


    @Override
    public void init() throws ServletException {
        PatternLayout logLayout = new PatternLayout("[%p][%d{dd/MM/yyyy HH:mm:ss}][%C{1}] %m%n");

        // Logger initialization
        try {
            logger = Logger.getLogger("RegistrationBoardroom");
            logger.addAppender(new FileAppender(logLayout, RB.logFile));
            logger.addAppender(new ConsoleAppender(logLayout));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Getting timezones
        String[] idsa = TimeZone.getAvailableIDs();
        timezones=new String[35];
        int y=0;
        for (int i=0;i<idsa.length;i++) {
            if (idsa[i].startsWith("Etc")){
                timezones[y]=idsa[i];
                y++;
            }
        }

        // Check config file for proxy details and apply them
        boolean proxyboolean = false;
        String proxyAddress = "";
        String proxyPort = "";
        try {
            FileReader input = new FileReader(RB.proxyConfigFile);
            BufferedReader reader = new BufferedReader(input);
            String temp = "";
            int index = 0;
            while (temp != null) {
                temp = reader.readLine();
                if (index == 0) { // First line is true if useProxy
                    proxyboolean = Boolean.parseBoolean(temp);
                    if (!proxyboolean)
                        break;
                }
                if (index == 1) { // Second line is proxy address
                    proxyAddress = temp;
                }
                if (index == 2) { // Third line is proxy port
                    proxyPort = temp;
                }
                index++;
            }
            reader.close();
        } catch (IOException e) {
            logger.error("Error loading proxy config details: ", e);
        }
        // Set proxy if necessary
        if (proxyboolean) {
            System.getProperties().put("proxySet", "true");
            System.getProperties().put("proxyHost", proxyAddress);
            System.getProperties().put("proxyPort", proxyPort);
        }
        
        db=new DB(logger,RB.JDBC_url);
        sessionTimeoutMinutes=this.defaultSessionTimeoutMinutes;
        serviceRefreshMinutes=this.defaultServiceRefreshMinutes;
        cache=new Cache(logger,db,serviceRefreshMinutes,sessionTimeoutMinutes);
        created=new Date();
        if (cleaningService)
            cache.startCleaningService();
    }

    /**
     *
     * Method executed each time a GET request is received from the browser
     *
     */
    @Override
    public void doGet (HttpServletRequest request,HttpServletResponse response) {
	try {
            request.setAttribute("portal", this);
            String printpreview=(String) request.getParameter("printpreview");
            if (printpreview==null)
                getServletConfig().getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
            else
                getServletConfig().getServletContext().getRequestDispatcher("/print.jsp").forward(request, response);
	} catch (Exception ex) {
	    ex.printStackTrace ();
	}
    }

    /**
    *
    * Method executed each time a POST request is received from the browser
    *
    */
    @Override
    public void doPost (HttpServletRequest request,HttpServletResponse response) {
	try {
            try {
                logger.debug("doPost");
                String sessionId=(String)request.getSession().getAttribute("sessionId");
                Browser browser=null;
                if (sessionId==null) {
                   sessionId=(String)request.getSession().getId();
                   request.getSession().setAttribute("sessionId",sessionId);
                   browser=Portal.getCache().addBrowser(request.getSession());
                }
                else {
                   browser=Portal.getCache().getBrowser(sessionId);
                }
                
                Enumeration params = request.getParameterNames();
                browser.getBoardrooms();
                
                while (params.hasMoreElements()) {
                    String name = (String) params.nextElement();
                    String value = request.getParameter(name);

                    if (name.equals("command")) {

                        if (value.equals("checklogin")) {
                            browser.setAction("checklogin");
                            break;
                        }
                        else if (value.equals("logout")) {
                            browser.setAction("logout");
                            break;
                        }
                        else if (value.equals("changeboardroom")) {
                            browser.setActiveCategory(Browser.BOARDROOMS);
                            browser.boardroom_id=Integer.valueOf(request.getParameter("boardrooms"));
                            break;
                        }
                        else if (value.equals("next_week")) {
                            browser.deltaweeks+=1;
                            break;
                        }
                        else if (value.equals("previous_week")) {
                            browser.deltaweeks-=1;
                            break;
                        }
                        else if (value.equals("settings")) {
                            String timezone=request.getParameter("timezone").toString();
                            int startHour=Integer.valueOf(request.getParameter("starthour").toString()).intValue();
                            int endHour=Integer.valueOf(request.getParameter("endhour"));
                            int precision=Integer.valueOf(request.getParameter("precision"));
                            int firstday=Integer.valueOf(request.getParameter("firstday"));
                            int weekend=0;
                            int ampm=0;
                            Object weekendstr = request.getParameter("weekend");
                            if (weekendstr!=null)
                                weekend=1;
                            Object ampmstr = request.getParameter("ampm");
                            if (ampmstr!=null)
                                ampm=1;
                            TableSetting se=new TableSetting(timezone,startHour,endHour,precision,firstday,weekend,ampm);
                            
                            if (db.updateUserSetting(se, browser.user_id)) {
                                browser.tableSetting=se;
                            }
                            else {
                                browser.setError("Error in changing user settings!");
                                browser.setAction("error");
                                logger.error("Error in changing user settings!");
                            }
                            break;
                        }
                        else if (value.equals("deleteevent")) {
                            db.deleteReservation(Integer.valueOf(request.getParameter("reservation_id")));
                            if (browser.getHistoryUserId()!=0) {
                                browser.setAction("showhistory");
                            }
                            break;
                        }
                        else if (value.equals("changeeventname")) {
                            try {
                                ResultSet r=db.getReservation(Integer.valueOf(request.getParameter("reservation_id")));
                                r.next();
                                Date rstart=new Date(r.getDate("start").getYear(),r.getDate("start").getMonth(),r.getDate("start").getDate(),r.getTime("start").getHours(),r.getTime("start").getMinutes(),r.getTime("start").getSeconds());
                                Date rend=new Date(r.getDate("end").getYear(),r.getDate("end").getMonth(),r.getDate("end").getDate(),r.getTime("end").getHours(),r.getTime("end").getMinutes(),r.getTime("end").getSeconds());
                                db.updateReservation(Integer.valueOf(request.getParameter("reservation_id")),db.getPhoneByUserId(r.getInt("user_id")),request.getParameter("event_name"),r.getInt("boardroom_id"),rstart,rend);
                                r.close();
                            } catch (SQLException e) {
                                logger.error("SQLException: " + e.getMessage());
                            }
                            break;
                        }
                        else if (value.equals("findfreeroomsfornewreservation")) {
                            Date rstart=null;
                            Date rend=null;
                            try {
                                DateFormat dfdate = new SimpleDateFormat("dd.MM.yyyy");
                                DateFormat dftime = new SimpleDateFormat("HH:mm");
                                Date dat=dfdate.parse(request.getParameter("date"));
                                Date start=dftime.parse(request.getParameter("starttime"));
                                Date end=dftime.parse(request.getParameter("endtime"));
                                rstart=new Date(dat.getYear(),dat.getMonth(),dat.getDate(),start.getHours(),start.getMinutes(),start.getSeconds());
                                rend=new Date(dat.getYear(),dat.getMonth(),dat.getDate(),end.getHours(),end.getMinutes(),end.getSeconds());
                            } catch (ParseException e) {
                                logger.error("SQLException: " + e.getMessage());
                            }
                            if (browser.getUserId()==1) {
                                logger.debug(request.getParameter("name").toString());
                                browser.setReservation(new Reservation(0,0,0,request.getParameter("name"),rstart,rend));
                            }
                            else
                            {
                                logger.debug("db.getPhoneByUserId(browser.getUserId()");
                                logger.debug(String.valueOf(db.getPhoneByUserId(browser.getUserId())));
                                browser.setReservation(new Reservation(0,db.getPhoneByUserId(browser.getUserId()),0,request.getParameter("name"),rstart,rend));
                            }
                            browser.findFreeBoardrooms();
                            browser.setAction("freeboardroomsfinded");
                            break;
                        }
                        else if (value.equals("selectfreeboardroom")) {
                            int b_id=Integer.valueOf(request.getParameter("boardrooms").toString());

                            if (browser.getEditReservationId()!=0) {
                                if (!db.updateReservation(browser.getEditReservationId(), browser.getReservation().getPhone(), browser.getReservation().getEventName(), b_id, browser.getReservation().getStart(),browser.getReservation().getEnd())) {
                                    browser.setError("Reservation update failed!!");
                                    browser.setAction("error");
                                    logger.error("Reservation update failed!");
                                }
                                browser.setEditReservationId(0);
                            }
                            else {
                                db.addReservation(String.valueOf(browser.getReservation().getPhone()), browser.getReservation().getEventName(), b_id, browser.getReservation().getStart(),browser.getReservation().getEnd());
                            }
                            break;
                        }
                        else if (value.equals("findfreerooms")) {
                            Date rstart=null;
                            Date rend=null;
                            try {
                                DateFormat dfdate = new SimpleDateFormat("dd.MM.yyyy");
                                DateFormat dftime = new SimpleDateFormat("HH:mm");
                                Date dat=dfdate.parse(request.getParameter("date"));
                                Date start=dftime.parse(request.getParameter("starttime"));
                                Date end=dftime.parse(request.getParameter("endtime"));
                                rstart=new Date(dat.getYear(),dat.getMonth(),dat.getDate(),start.getHours(),start.getMinutes(),start.getSeconds());
                                rend=new Date(dat.getYear(),dat.getMonth(),dat.getDate(),end.getHours(),end.getMinutes(),end.getSeconds());
                            } catch (ParseException e) {
                                logger.error("SQLException: " + e.getMessage());
                            }
                            if (browser.getUserId()==1)
                                browser.setReservation(new Reservation(Integer.valueOf(request.getParameter("reservation_id")),0,0,request.getParameter("name"),rstart,rend));
                            else
                                browser.setReservation(new Reservation(Integer.valueOf(request.getParameter("reservation_id")),db.getPhoneByUserId(browser.getUserId()),0,request.getParameter("name"),rstart,rend));

                            browser.findFreeBoardrooms();

                            browser.setAction("freeboardroomsfinded");
                            browser.setEditReservationId(Integer.valueOf(request.getParameter("reservation_id")));
                            break;
                        }
                        else if (value.equals("changename")) {
                            if (db.changeUserName(String.valueOf(db.getPhoneByUserId(browser.getUserId())), request.getParameter("new_name"))) {
                                browser.setAction("newusername");
                                browser.setNewUsername(request.getParameter("new_name").toString());
                            }
                            else {
                                browser.setError("Error in changing name!");
                                browser.setAction("error");
                                logger.error("Error in changing name!");
                            }
                            break;
                        }
                        else if (value.equals("changepassword")) {
                            String inputHash = SHA.byteArrayToHexString(SHA.computeHash(request.getParameter("current_password").toString()));
                            if (db.getUserPassword(browser.getUserId()).equals(inputHash)) {
                                if (request.getParameter("new_password1").equals(request.getParameter("new_password2"))){
                                    db.changeUsersPassword(String.valueOf(db.getPhoneByUserId(browser.getUserId())) , request.getParameter("new_password2"));
                                    browser.setAction("logout");
                                }
                                else {
                                    browser.setError("Passwords aren't the same!");
                                    browser.setAction("error");
                                    logger.error("Passwords aren't the same!");
                                }
                            }
                            else {
                                browser.setError("Wrong current password!");
                                browser.setAction("error");
                                logger.error("Wrong current password!");
                            }
                            break;
                        }
                        else if (value.equals("history")) {
                            browser.setHistory(browser.getUserHistory(Integer.valueOf(request.getParameter("user"))));
                            browser.setAction("showhistory");
                            browser.setActiveCategory(Browser.USERS);
                            int user=Integer.valueOf(request.getParameter("user")).intValue();
                            browser.setHistoryUserId(user);
                            browser.getHistory();
                            break;
                        }
                        else if (value.equals("stopservice")) {
                            cache.stopCleaningService();
                            break;
                        }
                        else if (value.equals("startservice")) {
                            cache.setServiceRefreshMinutes(Integer.valueOf(request.getParameter("service")));
                            cache.setSessionTimeoutMinutes(Integer.valueOf(request.getParameter("session")));
                            cache.startCleaningService();
                            break;
                        }
                        else if (value.equals("resetuser")) {
                            logger.debug(request.getParameter("id"));
                            if (!db.resetUserPassword(Integer.valueOf(request.getParameter("id"))))
                                logger.error("Error while resetting user password");
                            break;
                        }
                        else if (value.equals("edituser")) {
                            if (db.getUserNameByPhone(request.getParameter("phone").toString())!=null) {
                                browser.setAction("error");
                                browser.setError("User with phone number "+request.getParameter("phone").toString()+" already exist!");
                            }
                            else if (!db.updateUserNameAndNumber(Integer.valueOf(request.getParameter("id")).intValue(), request.getParameter("name").toString(), request.getParameter("phone").toString()))
                                logger.error("Error while update name and number of table user");
                            break;
                        }
                        else if (value.equals("editboardroom")) {
                            if (db.boardroomNameExist(request.getParameter("name").toString())) {
                                browser.setAction("error");
                                browser.setError("Boardroom "+request.getParameter("name").toString()+" already exist!");
                            }
                            else if (!db.updateBoardroom(Integer.valueOf(request.getParameter("id")), request.getParameter("name"))) {
                                logger.error("Error while update name of table boardroom");
                            }
                            browser.getBoardrooms();
                            break;
                        }
                        else if (value.equals("deleteuser")) {
                            if (!db.deleteUser(Integer.valueOf(request.getParameter("user_id")))) {
                                logger.error("Error while deleting user");
                            }
                            break;
                        }
                        else if (value.equals("newuser")) {
                            if (request.getParameter("name").toString().equals("") || (request.getParameter("phone").toString().equals(""))) {
                                browser.setAction("error");
                                browser.setError("You have to fill name and phone number!");
                            }
                            if (db.getUserNameByPhone(request.getParameter("phone").toString())!=null) {
                                browser.setAction("error");
                                browser.setError("User with phone number "+request.getParameter("phone").toString()+" already exist!");
                            }
                            else if (!db.addUser(request.getParameter("phone").toString(), request.getParameter("name").toString(),"000")) {
                                logger.error("Error while adding user");
                            }
                            break;
                        }
                        else if (value.equals("newboardroom")) {
                            if (request.getParameter("name").toString().equals("")) {
                                browser.setAction("error");
                                browser.setError("You have to fill name!");
                            }
                            if (db.boardroomNameExist(request.getParameter("name").toString())) {
                                browser.setAction("error");
                                browser.setError("Boardroom "+request.getParameter("name").toString()+" already exist!");
                            }
                            else if (!db.addBoardroom(request.getParameter("name").toString())) {
                                logger.error("Error while adding user");
                            }
                            else
                                browser.getBoardrooms();
                            break;
                            
                        }
                        else if (value.equals("deleteboardroom")) {
                            if (!db.deleteBoardroom(Integer.valueOf(request.getParameter("boardroom_id")))) {
                                logger.error("Error while deleting boardroom");
                            }
                            else {
                                browser.getBoardrooms();
                                browser.setActiveCategory(Browser.NONE);
                            }
                            break;
                        }
                    }
                }
                request.setAttribute("portal", this);
                getServletConfig().getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
            }
            catch (NullPointerException e) {
                logger.error("NullPointerException "+e.getMessage().toString());
            }
        }
        catch (Exception ex) {
	    logger.error("Exception "+ex.getMessage().toString());
	}
    }
}

