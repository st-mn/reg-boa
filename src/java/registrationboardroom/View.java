/*
Class for generating xml responses for Openstage phone by using getXML() method of IppForm or Ipplis
which are basic controls of phonescreengenerator library

Java class named View that generates XML responses for an Openstage phone application. 
It is used to create various forms and screens for managing boardroom reservations and administration.

Class Variables:
logger: An instance of the Logger class from the Apache log4j library.
url: A string representing the URL of the application to insert into each XML screen.
imageUrl: A string representing the URL of the folder that holds images required for the application.
version: A static string representing the version of the application.
phonenumber: A string representing the phone number of the phone for which the object is created.
cacheImages: A boolean variable to determine whether images are cached or not.
db: An instance of the DB class (custom database object) used to interact with the database.
freeboardrooms: An ArrayList<Integer> that holds a list of free boardrooms.
Constructor: The class has a constructor that initializes its member variables.

Methods: 
The class contains several methods to generate XML forms and screens for various functionalities such as managing reservations, 
boardrooms, user information, passwords, and more. These methods include:

Methods to generate forms for changing passwords, user information, and administrative tasks.
Methods to generate lists of boardrooms, reservations, and their related actions.
Methods to handle information screens, error screens, and confirmation screens.

Overall, the View class creates XML responses 
for an Openstage phone, facilitating boardroom reservations and administrative tasks. 
It uses the phonescreengenerator library to build the XML-based screens and forms. 
The application likely communicates with a database to manage boardroom reservations and user information.
 */

package registrationboardroom;

import phonescreengenerator.phonenumber.IppPhoneNumber;
import phonescreengenerator.phonenumber.PhoneNumberImageType;
import phonescreengenerator.IppImage;
import phonescreengenerator.ListOption;
import phonescreengenerator.actions.ActionType;
import phonescreengenerator.actions.IppAction;
import phonescreengenerator.commands.CommandType;
import phonescreengenerator.commands.IppCommand;
import phonescreengenerator.hiddens.HiddenType;
import phonescreengenerator.hiddens.IppHidden;
import phonescreengenerator.items.IppImageItem;
import phonescreengenerator.items.IppStringItem;
import phonescreengenerator.items.IppTextField;
import phonescreengenerator.items.TextConstraintType;
import phonescreengenerator.items.TextDefaultType;
import phonescreengenerator.screens.AlertType;
import phonescreengenerator.screens.IppAlert;
import phonescreengenerator.screens.IppForm;
import phonescreengenerator.screens.IppList;
import phonescreengenerator.screens.ListType;
import phonescreengenerator.screens.ScreenType;
import phonescreengenerator.screens.IppScreen;
import phonescreengenerator.items.IppDateField;
import phonescreengenerator.items.DateTimeModeType;
import phonescreengenerator.items.DateTimeDefaultType;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import java.util.ArrayList;
import java.sql.*;
import java.util.TimeZone;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;


public class View {
    
    private Logger logger = null;
    private String url = ""; // URL of this application to insert into each XML screen
    private String imageUrl = ""; // URL of folder that holds images required for the application
    private static String version = "1.0.0"; // Version of the application
    private String phonenumber; // Phonenumber of the phone this object is created for
    boolean cacheImages = true; // Variable to dictate whether images are cached or not
    private DB db = null; // Database object
    private ArrayList<Integer> freeboardrooms = null;

    public void setFreeBoardrooms(ArrayList<Integer> rooms) {
        freeboardrooms=rooms;
    }

    public View(Logger _logger,DB _db,String _url,String _imageUrl,String _phonenumber)
    {
        logger=_logger;
        db=_db;
        url=_url;
        imageUrl=_imageUrl;
        phonenumber=_phonenumber;
        //reservations=new ArrayList<Reservation>();
        
    }

    public String getAdministratorsPasswordForm()
    {
        IppForm form = new IppForm("Administrator's password", url);
        form.addItem(new IppTextField(30, TextConstraintType.PASSWORD, false, false,TextDefaultType.TEXT, "PASSWORD", "Password:", ""));
        form.addHidden(new IppHidden(HiddenType.PHONENUMBER, "phonenumber"));
        form.addCommand(new IppCommand(CommandType.SELECT, "Login", "command", "ADMINISTRATION_CHECK2"));
        form.addCommand(new IppCommand(CommandType.UPDATE, "Back", "command", "MAIN_FORM"));
        return form.getXML();
    }

    public String getUsersPasswordForm(boolean first)
    {
        if (!first) {
            IppForm form = new IppForm("User's password", url);
            form.addItem(new IppTextField(30, TextConstraintType.PASSWORD, false, false,TextDefaultType.TEXT, "PASSWORD", "Password:", ""));
            form.addHidden(new IppHidden(HiddenType.PHONENUMBER, "phonenumber"));
            form.addCommand(new IppCommand(CommandType.SELECT, "Login", "command", "RESERVATION_MENU_CHECK2"));
            form.addCommand(new IppCommand(CommandType.UPDATE, "Back", "command", "MAIN_FORM"));
            return form.getXML();
        }
        else {
            IppForm form = new IppForm("Register new user ", url);
            form.addHidden(new IppHidden(HiddenType.PHONENUMBER, "phonenumber"));
            form.addCommand(new IppCommand(CommandType.SELECT, "Register", "command", "RESERVATION_MENU_NEW_USER"));
            form.addCommand(new IppCommand(CommandType.UPDATE, "Back", "command", "MAIN_FORM"));
            return form.getXML();
        }
    }

    public String changeAdministratorsPasswordForm()
    {
        IppForm form = new IppForm("Administrator's password", url);
        form.addItem(new IppTextField(30, TextConstraintType.PASSWORD, false, false,TextDefaultType.TEXT, "CURRENT_PASSWORD", "Current Password:", ""));
        form.addItem(new IppTextField(30, TextConstraintType.PASSWORD, false, false,TextDefaultType.TEXT, "PASSWORD1", "New Password:", ""));
        form.addItem(new IppTextField(30, TextConstraintType.PASSWORD, false, false,TextDefaultType.TEXT, "PASSWORD2", "Again:", ""));
        form.addHidden(new IppHidden(HiddenType.PHONENUMBER, "phonenumber"));
        form.addCommand(new IppCommand(CommandType.SELECT, "Change", "command","ADMINISTRATION_CHANGE" ));
        form.addCommand(new IppCommand(CommandType.UPDATE, "Back", "command", "ADMINISTRATION_FORM"));
        return form.getXML();
    }

    public String changeUsersPasswordForm()
    {
        IppForm form = new IppForm("Change users's password", url);
        form.addItem(new IppTextField(30, TextConstraintType.PASSWORD, false, false,TextDefaultType.TEXT, "CURRENT_PASSWORD", "Current Password:", ""));
        form.addItem(new IppTextField(30, TextConstraintType.PASSWORD, false, false,TextDefaultType.TEXT, "PASSWORD1", "New Password:", ""));
        form.addItem(new IppTextField(30, TextConstraintType.PASSWORD, false, false,TextDefaultType.TEXT, "PASSWORD2", "Again:", ""));
        form.addHidden(new IppHidden(HiddenType.PHONENUMBER, "phonenumber"));
        form.addCommand(new IppCommand(CommandType.SELECT, "Change", "command", "RESERVATION_CHANGE_PASSWORD"));
        form.addCommand(new IppCommand(CommandType.UPDATE, "Back", "command", "RESERVATION_MENU_FORM"));
        return form.getXML();
    }

    public String UpdateUserNameForm(String name)
    {
        IppForm form = new IppForm("Change name", url);
        form.addHidden(new IppHidden(HiddenType.PHONENUMBER, "phonenumber"));
        form.addItem(new IppTextField(30, TextConstraintType.ANY, false, false,TextDefaultType.TEXT, "NAME", "Name", name));
        form.addCommand(new IppCommand(CommandType.SELECT, "Change", "command","UPDATE_USERNAME"));
        form.addCommand(new IppCommand(CommandType.UPDATE, "Back", "command","RESERVATION_MENU_FORM"));
        return form.getXML();
    }

    public String AboutForm()
    {
        IppForm form = new IppForm("About", url);
        form.addItem(new IppStringItem("", ""));
        form.addItem(new IppStringItem("Authors:", "cisary@gmail.com"));
        form.addItem(new IppStringItem("", "stanislav.toman@gmail.com"));
        form.addItem(new IppStringItem("URL:", "http://reg-boardroom.sourceforge.net"));
        form.addItem(new IppStringItem("License:", "GPL"));
        form.addItem(new IppStringItem("Version:", version));
        form.addHidden(new IppHidden(HiddenType.PHONENUMBER, "phonenumber"));
        form.addCommand(new IppCommand(CommandType.UPDATE, "Back", "command", "MAIN_FORM"));
        return form.getXML();
    }

    public String MainForm()
    {
        IppList list = new IppList(ListType.IMPLICIT, "RegistrationBoardroom", url);
        list.addHidden(new IppHidden(HiddenType.PHONENUMBER, "phonenumber"));
        list.addOption(new ListOption(0, false, "command","RESERVATION_MENU_CHECK", "Reservation", null, null));
        list.addOption(new ListOption(0, false, "command","OVERVIEW_FORM", "Overview", null, null));
        list.addOption(new ListOption(0, false, "command","ADMINISTRATION_CHECK", "Administration", null, null));
        list.addOption(new ListOption(0, false, "command","ABOUT_FORM", "About", null, null));
        return list.getXML();
    }

    public String ReservationNewUserForm()
    {
        IppForm form = new IppForm("New user ", url);
        form.addHidden(new IppHidden(HiddenType.PHONENUMBER, "phonenumber"));
        form.addItem(new IppTextField(30, TextConstraintType.ANY, false, false,TextDefaultType.TEXT, "NAME", "Name:", ""));
        form.addItem(new IppTextField(30, TextConstraintType.PASSWORD, false, false,TextDefaultType.TEXT, "PASSWORD", "Password:", ""));
        form.addItem(new IppTextField(30, TextConstraintType.PASSWORD, false, false,TextDefaultType.TEXT, "PASSWORD2", "Again:", ""));
        form.addCommand(new IppCommand(CommandType.SELECT, "Register", "command", "RESERVATION_NEW_USER"));
        form.addCommand(new IppCommand(CommandType.UPDATE, "Back", "command", "RESERVATION_MENU_CHECK2"));
        return form.getXML();
    }


    public String UpdateReservationsForm(boolean my)
    {
        IppList list = new IppList(ListType.IMPLICIT, "Edit Reservation", url);
	list.addHidden(new IppHidden(HiddenType.PHONENUMBER, "phonenumber"));
        try {
            ResultSet r=null;
            if (my)
                r=db.getReservationsByPhone(Integer.valueOf(phonenumber).intValue());
            else
                r=db.getReservations();
            if (r!=null) {
                while (r.next()) {
                    int id = r.getInt("id");
                    String name = r.getString("event_name");
                    list.addOption(new ListOption(0, false, "command","UPDATE_RESERVATION_FORM1*"+String.valueOf(id), name, null, null));
                }
            }
            else {
                return ErrorScreen("Error","There are no reservations!");
            }
            
            r.close();
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
            return WarningScreen("Warning","Database query failed!");
        }
        if (my)
            list.addOption(new ListOption(0, false, "command","RESERVATION_MENU_FORM", "Back", null, null));
        else
            list.addOption(new ListOption(0, false, "command","EDIT_RESERVATIONS_FORM", "Back", null, null));
        return list.getXML();
    }

    public String CancelReservationsForm(boolean my)
    {
        IppList list = new IppList(ListType.IMPLICIT, "Cancel Reservation", url);
	list.addHidden(new IppHidden(HiddenType.PHONENUMBER, "phonenumber"));
        try {
            ResultSet r=null;
            if (my)
                r=db.getReservationsByPhone(Integer.valueOf(phonenumber).intValue());
            else
                r=db.getReservations();
            if (r!=null) {
                while (r.next()) {
                    int id = r.getInt("id");
                    String name = r.getString("event_name");
                    list.addOption(new ListOption(0, false, "command","CANCEL_RESERVATION_FORM1*"+String.valueOf(id), name, null, null));
                }
            }
            else {
                return ErrorScreen("Error","There are no reservations!");
            }
            r.close();
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
            return WarningScreen("Warning","Database query failed!");
        }
        if (my)
            list.addOption(new ListOption(0, false, "command","RESERVATION_MENU_FORM", "Back", null, null));
        else
            list.addOption(new ListOption(0, false, "command","EDIT_RESERVATIONS_FORM", "Back", null, null));
        return list.getXML();
    }

    public String UpdateReservationForm1()
    {
        IppForm form = new IppForm("Edit Event", url);
        form.addHidden(new IppHidden(HiddenType.PHONENUMBER, "phonenumber"));
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dftime = new SimpleDateFormat("HH:mm:ss.SSS");
        form.addItem(new IppDateField(DateTimeModeType.DATE,DateTimeDefaultType.MODE,"DATE1","TIME1","Date:",TimeZone.getTimeZone("GMT+1"),dfdate.format(Phone.getReservation().getStart()).toString(),""));
        form.addItem(new IppDateField(DateTimeModeType.TIME,DateTimeDefaultType.MODE,"DATE2","TIME2","Start Time:",TimeZone.getTimeZone("GMT+1"),"",dftime.format(Phone.getReservation().getStart()).toString()));
        form.addItem(new IppDateField(DateTimeModeType.TIME,DateTimeDefaultType.MODE,"DATE3","TIME3","End Time:",TimeZone.getTimeZone("GMT+1"),"",dftime.format(Phone.getReservation().getEnd()).toString()));
        form.addItem(new IppTextField(30, TextConstraintType.ANY, false, false,TextDefaultType.TEXT, "NAME", "Name:", Phone.getReservation().getEventName()));
        form.addCommand(new IppCommand(CommandType.SELECT, "Change room", "command","UPDATE_RESERVATION_FORM2"));
        form.addCommand(new IppCommand(CommandType.SELECT, "Update Name", "command","UPDATE_RESERVATION_NAME"));
        form.addCommand(new IppCommand(CommandType.UPDATE, "Back", "command","UPDATE_MYRESERVATIONS_FORM"));
        return form.getXML();
    }

    public String UpdateReservationForm2()
    {
        IppList list = new IppList(ListType.IMPLICIT, "Free Boardrooms", url);
	list.addHidden(new IppHidden(HiddenType.PHONENUMBER, "phonenumber"));
        if (freeboardrooms.isEmpty())
            return InfoScreen("Information","Sorry, no boardroom is available");
        else
        for (int i=0;i<freeboardrooms.size();i++) {
            String name=null;
            try {
                ResultSet r=db.getBoardroom(freeboardrooms.get(i).intValue());
                r.next();
                name = r.getString("name");
                r.close();
            } catch (SQLException e) {
                logger.error("SQLException: " + e.getMessage());
                return WarningScreen("Warning","Database query failed!");
            }
            list.addOption(new ListOption(0, false, "command","RERESERVE_BOARDROOM*"+String.valueOf(freeboardrooms.get(i).intValue()), name, null, null));
        }
        list.addOption(new ListOption(0, false, "command","UPDATE_RESERVATION_FORM1*"+String.valueOf(Phone.getReservation().getId()), "Back", null, null));
        return list.getXML();
    }

    public String ReservationMenuForm()
    {
        IppList list = new IppList(ListType.IMPLICIT, "Reservation", url);
	list.addHidden(new IppHidden(HiddenType.PHONENUMBER, "phonenumber"));
        list.addOption(new ListOption(0, false, "command","NEW_RESERVATION_FORM1", "New", null, null));
        list.addOption(new ListOption(0, false, "command","UPDATE_MYRESERVATIONS_FORM", "Edit", null, null));
        list.addOption(new ListOption(0, false, "command","REMOVE_MYRESERVATIONS_FORM", "Remove", null, null));
        list.addOption(new ListOption(0, false, "command","RESERVATION_MENU_CHANGE_NAME", "Change Name", null, null));
        list.addOption(new ListOption(0, false, "command","RESERVATION_MENU_CHANGE_PASSWORD", "Change Password", null, null));
        list.addOption(new ListOption(0, false, "command","MAIN_FORM", "Back", null, null));
        return list.getXML();
    }

    public String NewReservationForm1(boolean saved)
    {
        IppForm form = new IppForm("New Event", url);
        form.addHidden(new IppHidden(HiddenType.PHONENUMBER, "phonenumber"));
        Date today=new Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dftime = new SimpleDateFormat("HH:mm:ss.SSS");
        if (saved) {
            form.addItem(new IppDateField(DateTimeModeType.DATE,DateTimeDefaultType.MODE,"DATE1","TIME1","Date:",TimeZone.getTimeZone("GMT+1"),dfdate.format(Phone.getReservation().getStart()).toString(),""));
            form.addItem(new IppDateField(DateTimeModeType.TIME,DateTimeDefaultType.MODE,"DATE2","TIME2","Start Time:",TimeZone.getTimeZone("GMT+1"),"",dftime.format(Phone.getReservation().getStart()).toString()));
            form.addItem(new IppDateField(DateTimeModeType.TIME,DateTimeDefaultType.MODE,"DATE3","TIME3","End Time:",TimeZone.getTimeZone("GMT+1"),"",dftime.format(Phone.getReservation().getEnd()).toString()));
            form.addItem(new IppTextField(30, TextConstraintType.ANY, false, false,TextDefaultType.TEXT, "NAME", "Name:", Phone.getReservation().getEventName()));
        }
        else {
            form.addItem(new IppDateField(DateTimeModeType.DATE,DateTimeDefaultType.MODE,"DATE1","TIME1","Date:",TimeZone.getTimeZone("GMT+1"),dfdate.format(today).toString(),""));
            form.addItem(new IppDateField(DateTimeModeType.TIME,DateTimeDefaultType.MODE,"DATE2","TIME2","Start Time:",TimeZone.getTimeZone("GMT+1"),"",dftime.format(today).toString()));
            form.addItem(new IppDateField(DateTimeModeType.TIME,DateTimeDefaultType.MODE,"DATE3","TIME3","End Time:",TimeZone.getTimeZone("GMT+1"),"",dftime.format(today).toString()));
            form.addItem(new IppTextField(30, TextConstraintType.ANY, false, false,TextDefaultType.TEXT, "NAME", "Name:", ""));
        }
        form.addCommand(new IppCommand(CommandType.SELECT, "Find free rooms", "command","NEW_RESERVATION_FORM2"));
        form.addCommand(new IppCommand(CommandType.UPDATE, "Back", "command","RESERVATION_MENU_FORM"));
        return form.getXML();
    }

    public String NewReservationForm2()
    {
        IppList list = new IppList(ListType.IMPLICIT, "Free Boardrooms", url);
	list.addHidden(new IppHidden(HiddenType.PHONENUMBER, "phonenumber"));
        if (freeboardrooms.isEmpty())
            return InfoScreen("Information","Sorry, no boardroom is available");
        else
        for (int i=0;i<freeboardrooms.size();i++) {
            String name=null;
            try {
                ResultSet r=db.getBoardroom(freeboardrooms.get(i).intValue());
                r.next();
                name = r.getString("name");
                r.close();
            } catch (SQLException e) {
                logger.error("SQLException: " + e.getMessage());
                return WarningScreen("Warning","Database query failed!");
            }
            list.addOption(new ListOption(0, false, "command","RESERVE_BOARDROOM*"+String.valueOf(freeboardrooms.get(i).intValue()), name, null, null));
        }
        list.addOption(new ListOption(0, false, "command","SAVED_RESERVATION_FORM1", "Back", null, null));
        return list.getXML();
    }


    public String AddBoardroomForm()
    {
        IppForm form = new IppForm("Add Boardroom", url);
        form.addHidden(new IppHidden(HiddenType.PHONENUMBER, "phonenumber"));
        form.addItem(new IppTextField(30, TextConstraintType.ANY, false, false,TextDefaultType.TEXT, "NAME", "Name", ""));
        form.addCommand(new IppCommand(CommandType.SELECT, "Add", "command","ADD_BOARDROOM"));
        form.addCommand(new IppCommand(CommandType.UPDATE, "Back", "command","EDIT_BOARDROOMS_FORM"));
        return form.getXML();
    }

    public String UpdateBoardroomsForm()
    {
        IppList list = new IppList(ListType.IMPLICIT, "Boardrooms administration", url);
	list.addHidden(new IppHidden(HiddenType.PHONENUMBER, "phonenumber"));
        try {
            ResultSet r=db.getBoardrooms();
            while (r.next()) {
                int id = r.getInt("id");
                String name = r.getString("name");
                list.addOption(new ListOption(0, false, "command","UPDATE_BOARDROOM_FORM*"+String.valueOf(id), name, null, null));
            }
            r.close();
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
            return WarningScreen("Warning","Database query failed!");
        }
        list.addOption(new ListOption(0, false, "command","EDIT_BOARDROOMS_FORM", "Back", null, null));
        return list.getXML();
    }

    public String UpdateBoardroomForm(int id,String name)
    {
        IppForm form = new IppForm("Edit Boardroom", url);
        form.addHidden(new IppHidden(HiddenType.PHONENUMBER, "phonenumber"));
        form.addItem(new IppTextField(30, TextConstraintType.ANY, false, false,TextDefaultType.TEXT, "NAME", "Name", name));
        form.addCommand(new IppCommand(CommandType.SELECT, "Edit", "command","UPDATE_BOARDROOM*"+String.valueOf(id)));
        form.addCommand(new IppCommand(CommandType.UPDATE, "Back", "command","EDIT_BOARDROOMS_FORM"));
        return form.getXML();
    }

    public String RemoveBoardroomsForm()
    {
        IppList list = new IppList(ListType.IMPLICIT, "Boardrooms administration", url);
	list.addHidden(new IppHidden(HiddenType.PHONENUMBER, "phonenumber"));
        try {
            ResultSet r=db.getBoardrooms();
            while (r.next()) {
                int id = r.getInt("id");
                String name = r.getString("name");
                list.addOption(new ListOption(0, false, "command","REMOVE_BOARDROOM_FORM*"+String.valueOf(id), name, null, null));
            }
            r.close();
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
            return WarningScreen("Warning","Database query failed!");
        }
        list.addOption(new ListOption(0, false, "command","EDIT_BOARDROOMS_FORM", "Back", null, null));
        return list.getXML();
    }

    public String EditBoardroomsForm()
    {
        IppList list = new IppList(ListType.IMPLICIT, "Boardrooms administration", url);
	list.addHidden(new IppHidden(HiddenType.PHONENUMBER, "phonenumber"));
        list.addOption(new ListOption(0, false, "command","ADD_BOARDROOM_FORM", "Add", null, null));
        list.addOption(new ListOption(0, false, "command","UPDATE_BOARDROOMS_FORM", "Edit", null, null));
        list.addOption(new ListOption(0, false, "command","REMOVE_BOARDROOMS_FORM", "Remove", null, null));
        list.addOption(new ListOption(0, false, "command","ADMINISTRATION_FORM", "Back", null, null));
        return list.getXML();
    }

    public String EditReservationsForm()
    {
        IppList list = new IppList(ListType.IMPLICIT, "Reservations administration", url);
	list.addHidden(new IppHidden(HiddenType.PHONENUMBER, "phonenumber"));
        list.addOption(new ListOption(0, false, "command","NEW_RESERVATION_FORM1", "Add", null, null));
        list.addOption(new ListOption(0, false, "command","UPDATE_RESERVATIONS_FORM", "Edit", null, null));
        list.addOption(new ListOption(0, false, "command","REMOVE_RESERVATIONS_FORM", "Remove", null, null));
        list.addOption(new ListOption(0, false, "command","ADMINISTRATION_FORM", "Back", null, null));
        return list.getXML();
    }


    public String AdministrationForm()
    {
        IppList list = new IppList(ListType.IMPLICIT, "Administration", url);
        list.addHidden(new IppHidden(HiddenType.PHONENUMBER, "phonenumber"));
        list.addOption(new ListOption(0, false, "command","EDIT_BOARDROOMS_FORM", "Boardrooms", null, null));
        list.addOption(new ListOption(0, false, "command","EDIT_RESERVATIONS_FORM", "Reservations", null, null));
        list.addOption(new ListOption(0, false, "command","CHANGE_PASSWORD_FORM", "Change password", null, null));
        list.addOption(new ListOption(0, false, "command","MAIN_FORM", "Back", null, null));
        return list.getXML();
    }

    public String OverviewForm()
    {
        IppList list = new IppList(ListType.IMPLICIT, "Boardrooms", url);
	list.addHidden(new IppHidden(HiddenType.PHONENUMBER, "phonenumber"));
        try {
            ResultSet r=db.getBoardrooms();
            while (r.next()) {
                int id = r.getInt("id");
                String name = r.getString("name");
                list.addOption(new ListOption(0, false, "command","BOARDROOM_EVENTS_FORM*"+String.valueOf(id), name, null, null));
            }
            r.close();
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
            return WarningScreen("Warning","Database query failed!");
        }
        list.addOption(new ListOption(0, false, "command","MAIN_FORM", "Back", null, null));
        return list.getXML();
    }

    public String BoardroomEventsForm(int i)
    {
        IppList list = new IppList(ListType.IMPLICIT, "Boardroom Events", url);
	list.addHidden(new IppHidden(HiddenType.PHONENUMBER, "phonenumber"));
        try {
            ResultSet r=db.getReservationsByBoardroom(i);
            while (r.next()) {
                int id = r.getInt("id");
                String name = r.getString("event_name");
                list.addOption(new ListOption(0, false, "command","BOARDROOM_EVENT*"+String.valueOf(id), name, null, null));
            }
            r.close();
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
            return WarningScreen("Warning","Database query failed!");
        }
        list.addOption(new ListOption(0, false, "command","OVERVIEW_FORM", "Back", null, null));
        return list.getXML();
    }

    public String BoardroomEvent(int id)
    {
 
        IppForm form = new IppForm("Event", url);
        form.addHidden(new IppHidden(HiddenType.PHONENUMBER, "phonenumber"));
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dftime = new SimpleDateFormat("HH:mm:ss.SSS");
        form.addItem(new IppDateField(DateTimeModeType.DATE,DateTimeDefaultType.MODE,"DATE1","TIME1","Date:",TimeZone.getTimeZone("GMT+1"),dfdate.format(Phone.getReservation().getStart()).toString(),""));
        form.addItem(new IppDateField(DateTimeModeType.TIME,DateTimeDefaultType.MODE,"DATE2","TIME2","Start Time:",TimeZone.getTimeZone("GMT+1"),"",dftime.format(Phone.getReservation().getStart()).toString()));
        form.addItem(new IppDateField(DateTimeModeType.TIME,DateTimeDefaultType.MODE,"DATE3","TIME3","End Time:",TimeZone.getTimeZone("GMT+1"),"",dftime.format(Phone.getReservation().getEnd()).toString()));
        form.addItem(new IppTextField(30, TextConstraintType.ANY, false, false,TextDefaultType.TEXT, "NAME", "Name:", Phone.getReservation().getEventName()));
        form.addCommand(new IppCommand(CommandType.UPDATE, "Back", "command","BOARDROOM_EVENTS_FORM*"+String.valueOf(Phone.getReservation().getBoardroomId())));
        form.addCommand(new IppCommand(CommandType.SELECT, "Call "+String.valueOf(Phone.getReservation().getPhone()), "command","MAKE_CALL"));
        form.addAction(IppAction.MakeCall(String.valueOf(Phone.getReservation().getPhone())));
        logger.debug(form.getXML());
 
        return form.getXML();
    }

    public String CallConfirmationScreen() {
        IppAlert alert = new IppAlert(AlertType.CONFIRMATION, "Calling", String.valueOf(Phone.getReservation().getPhone()), null , new IppPhoneNumber(), false, 0);
        String out=alert.getXML();
        String edited="";
  
        edited=out.substring( 0, out.length( ) - 26 );
        edited+="<IppAction Type=\"MAKECALL\"><Number>"+String.valueOf(Phone.getReservation().getPhone())+"</Number></IppAction>";
        edited+=out.substring( out.length( ) - 26,out.length() );
        logger.debug(edited);
        return edited;
    }

    public String WarningScreen(String title, String msg)
    {
        IppAlert alert = new IppAlert(AlertType.WARNING, title, msg, null, null, true, 0);
        return alert.getXML();
    }

    public String ErrorScreen(String title, String msg)
    {
        IppAlert alert = new IppAlert(AlertType.ERROR, title, msg, null, null, true, 0);
        return alert.getXML();
    }

    public String InfoScreen(String title, String msg)
    {
        IppAlert alert = new IppAlert(AlertType.INFO, title, msg, null, null, true, 0);
        return alert.getXML();
    }

}
