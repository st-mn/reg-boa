/*
Individual to each phone that accesses the application, contains methods to process requests 
received from the phone and construct appropriate XML responses
class named Phone, which appears to be a part of a web application designed to handle requests from phones 
and generate appropriate XML responses. It contains methods and properties to process phone requests, manage reservations, 
and handle user-related functionality. 

Class Members:
Several private instance variables (thisMachine, separator, programDir, administratorsPasswordFile, userDir, etc.) 
represent file paths and URLs related to the application's configuration and user data storage.
phonenumber: A string variable representing the phone number associated with the current Phone object.
url: A string variable holding the URL of the application to be inserted into each XML screen.
imageUrl: A string variable holding the URL of the folder that contains images required for the application.
administratorsPasswordHash and usersPasswordHash: String variables containing SHA-1 hashed passwords of administrators and users, respectively.
firstTimeNumber: A boolean flag indicating whether the phone number is being used for the first time or not.
logger, db, and view: Instances of Logger, DB, and View classes, used for logging, database interaction, and constructing views for XML responses.

Constructor:
The class has a constructor that initializes the Phone object. It sets up various configuration parameters, 
such as url, imageUrl, thisMachine, and initializes the View instance.

generateXML(HttpServletRequest request) Method:
This method takes an HTTP request from a phone as input and generates an appropriate XML response.
It first extracts the parameters from the request and processes them based on the command provided by the phone.
Depending on the command, different XML responses are generated using the View instance and returned as strings.

Command Handling:
The generateXML method processes various commands such as MAIN_FORM, ABOUT_FORM, ADD_BOARDROOM_FORM, UPDATE_BOARDROOMS_FORM, REMOVE_BOARDROOMS_FORM, etc.,
to generate different XML responses for different views or actions.
For example, when the command is MAIN_FORM, it calls view.MainForm() to generate an XML representation of the main form for the application.

Reservation Handling:
The class manages reservation-related commands and functionality such as creating, updating, and canceling reservations.
It uses a Reservation object to store reservation information and utilizes the DB class to interact with the underlying database.

User Handling:
The class handles user-related commands, such as changing user passwords, managing user data, and user authentication using SHA-1 hashed passwords.
Overall, this class seems to be an integral part of a web application that handles phone-based interactions and provides XML responses 
based on the received requests. It appears to facilitate user authentication, reservation management, 
and administrative functionality for a boardroom reservation system.
 */

package registrationboardroom;

import java.io.File;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import java.sql.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;



public class Phone {

    public String thisMachine = null; // IP address of the local host
    private static final String separator = System.getProperty("file.separator"); // String representing this platform's file separator
    private static final String programDir = System.getProperty("catalina.home")+ separator + "webapps" + separator + "RB" + separator; // Directory of this applications required folders and files
    private static final String administratorsPasswordFile = programDir + "admin.txt"; // Administrator's password configuration file
    private static final String userDir = programDir + "UserInfo" + separator; // Directory for saved user information
    private String phonenumber; // Phonenumber of the phone this object is created for
    private String url = ""; // URL of this application to insert into each XML screen
    private String imageUrl = ""; // URL of folder that holds images required for the application
    private String administratorsPasswordHash=""; // String containing SHA-1 hashed administrator's password
    private String usersPasswordHash=""; // String containing SHA-1 hashed administrator's password
    private boolean firstTimeNumber;
    private Logger logger = null;
    private DB db = null;
    private View view = null;
    private static Reservation reservation = null;

    /**
     *
     * Constructor for Phone object
     *
     */
    public Phone(Logger _logger,DB _db,String _phonenumber)
    {
        try {
            File dir = new File(userDir);
            if (!dir.isDirectory()) { // Check if user directory exists and create if not
                dir.mkdir();
            }
            logger = Logger.getLogger("RegistrationBoardroom"); //Initialise logger
            thisMachine = RB.HOST_IP; //Get IP address of local host from RB class
            url = "http://" + thisMachine + ":" + RB.TOMCAT_PORT + "/rb/rb"; //Create URL for this application
            imageUrl = "http://" + thisMachine + ":" + RB.TOMCAT_PORT + "/"; //Create URL for images of this application
            phonenumber=_phonenumber;
            logger=_logger;
            db=_db;
            administratorsPasswordHash=db.getAdministratorPassword();

            if (db.phoneInUsers(Integer.parseInt(phonenumber))) {
                usersPasswordHash=db.getUserPassword(db.getUserIdByPhone(phonenumber));
                firstTimeNumber=false;
            }
            else {
                firstTimeNumber=true;
            }
            logger.debug("Phone");
            logger.debug(String.valueOf(firstTimeNumber));
            view=new View(logger,db,url,imageUrl,phonenumber);
        } catch (Exception e) {
            logger.error("Error initialising Phone object: ", e);
        }
    }

    public static Reservation getReservation() {
        return reservation;
    }

    /**
     * Process the request received from the phone and construct an appropriate XML response
     *
     * @param request	HTTP request from phone
     * @return              XML string response
     */
    public String generateXML(HttpServletRequest request)
    {
        String xml = "";
        
        logger.debug("Generating XML started");
        Enumeration params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String name = (String) params.nextElement();
            String value = request.getParameter(name);
            logger.debug("Name: " + name);
            logger.debug("Value: " + value);
        }

        String tmpPhone = request.getParameter("phonenumber");
        String ipaddress = request.getParameter("ipaddress");
        String command = request.getParameter("command");
        phonenumber = tmpPhone;

        if (tmpPhone != null && ipaddress != null) //First request made
        {
            return view.MainForm();
        } else if (command != null) {
            if (command.equals("MAIN_FORM"))
                return view.MainForm();

            else if (command.equals("ABOUT_FORM"))
                return view.AboutForm();

            else if (command.equals("ADD_BOARDROOM_FORM"))
                return view.AddBoardroomForm();

            else if (command.equals("UPDATE_BOARDROOMS_FORM"))
                return view.UpdateBoardroomsForm();

            else if (command.equals("REMOVE_BOARDROOMS_FORM"))
                return view.RemoveBoardroomsForm();

            else if(command.equals("ADMINISTRATION_FORM"))
                return view.AdministrationForm();

            else if (command.equals("EDIT_BOARDROOMS_FORM"))
                return view.EditBoardroomsForm();

            else if (command.startsWith("UPDATE_BOARDROOM_FORM")) {
                int id = Integer.parseInt(command.substring(22));
                String name=null;
                try {
                    ResultSet r=db.getBoardroom(id);
                    r.next();
                    name = r.getString("name");
                    r.close();
                } catch (SQLException e) {
                    logger.error("SQLException: " + e.getMessage());
                    return view.WarningScreen("Warning","Database query failed!");
                }
                return view.UpdateBoardroomForm(id,name);
            }

            else if (command.startsWith("UPDATE_BOARDROOM")) {
                int id = Integer.parseInt(command.substring(17));
		String name=request.getParameter("NAME");
                if (name.equals(""))
                    return view.ErrorScreen("Error","You have to set Name!");
                else
                    if (db.updateBoardroom(id,name))
                        return view.EditBoardroomsForm();
                    else
                        return view.WarningScreen("Warning","Adding failed!");
            }

            else if (command.equals("ADD_BOARDROOM"))
            {
                String name=request.getParameter("NAME");
                if (name.equals(""))
                    return view.ErrorScreen("Error","You have to set Name!");
                else
                    if (db.addBoardroom(name))
                        return view.EditBoardroomsForm();
                    else
                        return view.WarningScreen("Warning","Adding failed!");
            }

            else if (command.startsWith("REMOVE_BOARDROOM_FORM")) {
                int id = Integer.parseInt(command.substring(22));
                if (db.deleteBoardroom(id))
                    return view.EditBoardroomsForm();
                else
                    return view.WarningScreen("Warning","Removing failed!");
            }

            else if (command.equals("EDIT_RESERVATIONS_FORM"))
                return view.EditReservationsForm();

            else if (command.equals("RESERVATION_MENU_FORM")) {
                return view.ReservationMenuForm();
            }

            else if (command.equals("NEW_RESERVATION_FORM1")) {
                return view.NewReservationForm1(false);
            }

            else if (command.equals("SAVED_RESERVATION_FORM1")) {
                return view.NewReservationForm1(true);
            }

            else if (command.equals("NEW_RESERVATION_FORM2")) {
                Date start=null;
                Date end=null;
                try {
                    DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                    DateFormat dftime = new SimpleDateFormat("HH:mm:ss.SSS");
                    Date startend=dfdate.parse(request.getParameter("DATE1").toString());
                    Date starttime=dftime.parse(request.getParameter("TIME2").toString());
                    Date endtime=dftime.parse(request.getParameter("TIME3").toString());
                    start=new Date(startend.getYear(),startend.getMonth(),startend.getDate(),starttime.getHours(),starttime.getMinutes(),starttime.getSeconds());
                    end=new Date(startend.getYear(),startend.getMonth(),startend.getDate(),endtime.getHours(),endtime.getMinutes(),endtime.getSeconds());
                }
                catch (ParseException e) {
                    logger.error("ParseException :"+e.getStackTrace());
                }
                reservation=new Reservation(0,Integer.valueOf(phonenumber),0,request.getParameter("NAME").toString(),start,end);
                view.setFreeBoardrooms(reservation.findFreeBoardrooms(db,logger));
                return view.NewReservationForm2();
            }

            else if (command.startsWith("RESERVE_BOARDROOM")) {
                int id = Integer.parseInt(command.substring(18));
                if (db.addReservation(String.valueOf(reservation.getPhone()),reservation.getEventName(),id,reservation.getStart(),reservation.getEnd()))
                    return view.ReservationMenuForm();
                else
                    return view.WarningScreen("Warning","Reservation failed!");
            }

            else if (command.equals("UPDATE_MYRESERVATIONS_FORM")) 
                return view.UpdateReservationsForm(true);
            
            else if (command.equals("UPDATE_RESERVATIONS_FORM"))
                return view.UpdateReservationsForm(false);
            

            else if (command.startsWith("UPDATE_RESERVATION_FORM1")) {
                int id = Integer.parseInt(command.substring(25));
                try {
                    ResultSet r=db.getReservation(id);
                    r.next();
                    Date rstart=new Date(r.getDate("start").getYear(),r.getDate("start").getMonth(),r.getDate("start").getDate(),r.getTime("start").getHours(),r.getTime("start").getMinutes(),r.getTime("start").getSeconds());
                    Date rend=new Date(r.getDate("end").getYear(),r.getDate("end").getMonth(),r.getDate("end").getDate(),r.getTime("end").getHours(),r.getTime("end").getMinutes(),r.getTime("end").getSeconds());
                    reservation=new Reservation(r.getInt("id"),Integer.valueOf(phonenumber),r.getInt("boardroom_id"),r.getString("event_name"),rstart,rend);
                    r.close();
                } catch (SQLException e) {
                    logger.error("SQLException: " + e.getMessage());
                    return view.WarningScreen("Warning","Database query failed!");
                }
                return view.UpdateReservationForm1();
            }

            else if (command.equals("UPDATE_RESERVATION_FORM2")) {
                int oldid=reservation.getId();
                int oldboardroomid=reservation.getBoardroomId();
                Date start=null;
                Date end=null;
                try {
                    DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                    DateFormat dftime = new SimpleDateFormat("HH:mm:ss.SSS");
                    Date startend=dfdate.parse(request.getParameter("DATE1").toString());
                    Date starttime=dftime.parse(request.getParameter("TIME2").toString());
                    Date endtime=dftime.parse(request.getParameter("TIME3").toString());
                    start=new Date(startend.getYear(),startend.getMonth(),startend.getDate(),starttime.getHours(),starttime.getMinutes(),starttime.getSeconds());
                    end=new Date(startend.getYear(),startend.getMonth(),startend.getDate(),endtime.getHours(),endtime.getMinutes(),endtime.getSeconds());
                }
                catch (ParseException e) {
                    logger.error("ParseException :"+e.getStackTrace());
                }
                reservation=new Reservation(oldid,Integer.valueOf(phonenumber).intValue(),oldboardroomid,request.getParameter("NAME").toString(),start,end);
                view.setFreeBoardrooms(reservation.findFreeBoardrooms(db,logger));
                return view.UpdateReservationForm2();
            }

            else if (command.startsWith("RERESERVE_BOARDROOM")) {
                int id = Integer.parseInt(command.substring(20));
                if (db.updateReservation(reservation.getId(),reservation.getPhone(),reservation.getEventName(),id,reservation.getStart(),reservation.getEnd()))
                    return view.ReservationMenuForm();
                else
                    return view.WarningScreen("Warning","Reservation failed!");
            }

            else if (command.equals("UPDATE_RESERVATION_NAME")) {
                if (request.getParameter("NAME").toString().equals(""))
                    return view.ErrorScreen("Error","You have to set new Name!");
                if (db.updateReservation(reservation.getId(),reservation.getPhone(),request.getParameter("NAME").toString(),reservation.getBoardroomId(),reservation.getStart(),reservation.getEnd()))
                    return view.ReservationMenuForm();
                else
                    return view.WarningScreen("Warning","Reservation failed!");
            }

            else if (command.startsWith("CANCEL_RESERVATION_FORM1")) {
                int id = Integer.parseInt(command.substring(25));
                if (db.deleteReservation(id))
                    return view.ReservationMenuForm();
                else
                    return view.WarningScreen("Warning","Removing failed!");
            }

            else if (command.equals("REMOVE_MYRESERVATIONS_FORM")) 
                return view.CancelReservationsForm(true);

            else if (command.equals("REMOVE_RESERVATIONS_FORM"))
                return view.CancelReservationsForm(false);

            else if (command.equals("OVERVIEW_FORM"))
                return view.OverviewForm();

            else if (command.startsWith("BOARDROOM_EVENTS_FORM")) {
                int id = Integer.parseInt(command.substring(22));
                return view.BoardroomEventsForm(id);
            }

            else if (command.startsWith("BOARDROOM_EVENT")) {
                int id = Integer.parseInt(command.substring(16));
                try {
                    ResultSet r=db.getReservation(id);
                    r.next();
                    Date rstart=new Date(r.getDate("start").getYear(),r.getDate("start").getMonth(),r.getDate("start").getDate(),r.getTime("start").getHours(),r.getTime("start").getMinutes(),r.getTime("start").getSeconds());
                    Date rend=new Date(r.getDate("end").getYear(),r.getDate("end").getMonth(),r.getDate("end").getDate(),r.getTime("end").getHours(),r.getTime("end").getMinutes(),r.getTime("end").getSeconds());
                    reservation=new Reservation(r.getInt("id"),db.getPhoneByUserId(r.getInt("user_id")),r.getInt("boardroom_id"),r.getString("event_name"),rstart,rend);
                    r.close();
                } catch (SQLException e) {
                    logger.error("SQLException: " + e.getMessage());
                    return view.WarningScreen("Warning","Database query failed!");
                }
                return view.BoardroomEvent(id);
            }
            
            else if (command.equals("ADMINISTRATION_CHECK")) {
                return view.getAdministratorsPasswordForm();
            }
            
            else if (command.equals("ADMINISTRATION_CHECK2")) {
                if (request.getParameter("PASSWORD").toString().equals(""))
                    return view.ErrorScreen("Error","You have to set password!");
                else {
                    try {
                        String inputHash = SHA.byteArrayToHexString(SHA.computeHash(request.getParameter("PASSWORD")));
                        if (administratorsPasswordHash.equals(inputHash))
                            return view.AdministrationForm();
                        else
                            return view.ErrorScreen("Error","Invalid password!");
                    } catch (Exception e){
                        logger.error("SHA-1 Exception " + e.getMessage());
                        return view.WarningScreen("Warning","Password checking failed!");
                    }
                }
            }
            
            else if (command.equals("RESERVATION_MENU_CHECK2")) {
                if (request.getParameter("PASSWORD").toString().equals(""))
                    return view.ErrorScreen("Error","You have to set password!");
                else {
                    try {
                        String inputHash = SHA.byteArrayToHexString(SHA.computeHash(request.getParameter("PASSWORD")));
                        if (usersPasswordHash.equals(inputHash))
                            return view.ReservationMenuForm();
                        else
                            return view.ErrorScreen("Error","Invalid password!");
                    } catch (Exception e){
                        logger.error("SHA-1 Exception " + e.getMessage());
                        return view.WarningScreen("Warning","Password checking failed!");
                    }
                }
            }
            else if (command.equals("MAKE_CALL")) {
             
                return view.CallConfirmationScreen();
            }
            
            else if (command.equals("RESERVATION_MENU_CHECK")) {
                return view.getUsersPasswordForm(firstTimeNumber);
            }

            else if (command.equals("RESERVATION_MENU_NEW_USER")) {
                return view.ReservationNewUserForm();
            }

            else if (command.equals("RESERVATION_NEW_USER")) {
               
                if (request.getParameter("PASSWORD").toString().equals(""))
                    return view.ErrorScreen("Error","You have to set password!");
                if (request.getParameter("PASSWORD2").toString().equals(""))
                    return view.ErrorScreen("Error","You have to repeat password!");
                if (request.getParameter("NAME").toString().equals(""))
                    return view.ErrorScreen("Error","You have to set the name!");

                if (request.getParameter("PASSWORD").toString().equals(request.getParameter("PASSWORD2").toString())) {
                    if (db.addUser(phonenumber, request.getParameter("NAME").toString(), request.getParameter("PASSWORD").toString())) {
                        firstTimeNumber=false;
                        usersPasswordHash=db.getUserPassword(db.getUserIdByPhone(phonenumber));
                        return view.getUsersPasswordForm(firstTimeNumber);
                    }
                    else {
                        return view.WarningScreen("Warning","Database query failed!");
                    }
                }
            }

            else if (command.equals("RESERVATION_MENU_CHANGE_NAME")) {
                String name=db.getUserNameByPhone(phonenumber);
                return view.UpdateUserNameForm(name);
            }

            else if (command.equals("RESERVATION_MENU_CHANGE_PASSWORD")) {
                return view.changeUsersPasswordForm();
            }

            else if (command.equals("CHANGE_PASSWORD_FORM")) {
                return view.changeAdministratorsPasswordForm();
            }

            else if (command.equals("RESERVATION_CHANGE_PASSWORD")) {
                if (request.getParameter("CURRENT_PASSWORD").toString().equals(""))
                    return view.ErrorScreen("Error","You have to set password!");
                else {
                    try {
                        String inputHash = SHA.byteArrayToHexString(SHA.computeHash(request.getParameter("CURRENT_PASSWORD").toString()));

                        if (usersPasswordHash.equals(inputHash)){
                            if (request.getParameter("PASSWORD1").equals(request.getParameter("PASSWORD2"))){

                                String temp=db.changeUsersPassword(phonenumber,request.getParameter("PASSWORD1").toString());

                                if (temp!=null){
                                    usersPasswordHash=temp;
                                    return view.ReservationMenuForm();
                                }
                                else
                                    return view.WarningScreen("Warning","Password changing failed!");
                            }
                            else
                                return view.ErrorScreen("Error","Passwords didn't match!");
                        }
                        else
                            return view.ErrorScreen("Error","Invalid password!");
                    } catch (Exception e){
                        logger.error("SHA-1 Exception " + e.getMessage());
                        return view.WarningScreen("Warning","Password checking failed!");
                    }
                }
            }

            else if (command.equals("UPDATE_USERNAME")) {
        
		String name=request.getParameter("NAME");
                if (name.equals(""))
                    return view.ErrorScreen("Error","You have to set Name!");
                else
                    if (db.changeUserName(phonenumber,name))
                        return view.ReservationMenuForm();
                    else
                        return view.WarningScreen("Warning","Adding failed!");
            }

            else if (command.equals("ADMINISTRATION_CHANGE")) {
                if (request.getParameter("CURRENT_PASSWORD").toString().equals(""))
                    return view.ErrorScreen("Error","You have to set password!");
                else {
                    try {
                        logger.debug("SHA.byteArrayToHexString");
                        logger.debug(request.getParameter("CURRENT_PASSWORD"));
                        String inputHash = SHA.byteArrayToHexString(SHA.computeHash(request.getParameter("CURRENT_PASSWORD").toString()));
                        logger.debug(inputHash);
                        logger.debug(administratorsPasswordHash);
                        if (administratorsPasswordHash.equals(inputHash)){
                            if (request.getParameter("PASSWORD1").equals(request.getParameter("PASSWORD2"))){
                                logger.debug("RB.changeAdministratorsPassword");
                                String temp=db.changeAdministratorsPassword(request.getParameter("PASSWORD1"));
                                logger.debug(temp);
                                if (temp!=null){
                                    administratorsPasswordHash=temp;
                                    return view.AdministrationForm();
                                }
                                else
                                    return view.WarningScreen("Warning","Password changing failed!");
                            }
                            else
                                return view.ErrorScreen("Error","Passwords didn't match!");
                        }
                        else
                            return view.ErrorScreen("Error","Invalid password!");
                    } catch (Exception e){
                        logger.error("SHA-1 Exception " + e.getMessage());
                        return view.WarningScreen("Warning","Password checking failed!");
                    }
                }
            }
        }
        return xml;
    }
}
