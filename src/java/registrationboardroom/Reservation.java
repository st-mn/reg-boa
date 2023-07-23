/*
Class for mapping reservation object to memory for easier comparing with another records
Java class called Reservation, which is used to map reservation objects to memory for easier comparison with other records. 

Class Variables:
id: An integer variable representing the unique identifier of the reservation.
phone: An integer variable representing the phone associated with the reservation.
boardroom_id: An integer variable representing the ID of the boardroom where the reservation is made.
event_name: A string variable representing the name of the event associated with the reservation.
start: A Date object representing the start date and time of the reservation.
end: A Date object representing the end date and time of the reservation.
Constructor: The class has a constructor used to initialize the Reservation object with values for its member variables. 
Instead of using individual setter methods, the constructor directly sets the values.

Getters: 
The class provides getter methods to access the private member variables of the Reservation object. 
These methods allow external classes to retrieve information about the reservation object.

findFreeBoardrooms() Method: This method takes a DB object (representing the database connection) and a Logger object (for logging) as input parameters. 
The purpose of this method is to find free boardrooms (rooms without conflicting reservations) for the current reservation object.

The method retrieves all existing reservations from the database using the getReservations() method of the DB object.
It creates a list of Reservation objects (reservations) to store the existing reservations.
It then retrieves all existing boardrooms from the database using the getBoardrooms() method 
of the DB object and creates a list of boardroom IDs (freeboardrooms).
The method iterates through each reservation in the reservations list and compares its start and end dates 
with the start and end dates of the current Reservation object.
If there is a time overlap between the existing reservation and the current reservation, 
the boardroom associated with the existing reservation is removed from the freeboardrooms list, 
as it is not available for the current reservation.
After checking all existing reservations, the method returns the freeboardrooms list containing boardroom IDs 
that are available for the current reservation.

Overall, the Reservation class is designed to represent a single reservation record and provides methods to access its properties 
and find available boardrooms for the reservation.
 */
package registrationboardroom;

import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import org.apache.log4j.Logger;

public class Reservation {

    /*
     *
     * Private members which corresponds with table atributes
     *
     */
    private int id;
    private int phone;
    private int boardroom_id;
    private String event_name;
    private Date start;
    private Date end;


    /*
     *
     * Constructor used instead of Setters
     *
     */
    public Reservation(int i,int p,int b_id,String e_name,Date s,Date e)
    {
        id=i;
        phone=p;
        boardroom_id=b_id;
        event_name=e_name;
        start=s;
        end=e;
    }

    /*
     *
     * Getters :
     *
     */
    public int getId()
    {
        return id;
    }

    public int getPhone()
    {
        return phone;
    }

    public String getEventName()
    {
        return event_name;
    }

    public Date getStart()
    {
        return start;
    }

    public Date getEnd()
    {
        return end;
    }

    public int getBoardroomId()
    {
        return boardroom_id;
    }

    public ArrayList<Integer> findFreeBoardrooms(DB db, Logger logger)
    {
        try {
           ArrayList<Integer> freeboardrooms=new ArrayList<Integer>();
           ArrayList<Reservation> reservations = new ArrayList<Reservation>();
           ResultSet r=db.getReservations();
           reservations.clear();
           if (r!=null)
               while (r.next()) {
                   
                   Date rstart=new Date(r.getDate("start").getYear(),r.getDate("start").getMonth(),r.getDate("start").getDate(),r.getTime("start").getHours(),r.getTime("start").getMinutes(),r.getTime("start").getSeconds());
                   Date rend=new Date(r.getDate("end").getYear(),r.getDate("end").getMonth(),r.getDate("end").getDate(),r.getTime("end").getHours(),r.getTime("end").getMinutes(),r.getTime("end").getSeconds());
                   reservations.add(new Reservation(r.getInt("id"),db.getPhoneByUserId(r.getInt("user_id")),r.getInt("boardroom_id"),r.getString("event_name"),rstart,rend));
                   logger.debug("ok2"+r.getString("event_name"));
               }
           r=db.getBoardrooms();
           freeboardrooms.clear();
           if (r!=null)
               while (r.next()) {
                    int i = r.getInt("id");
                    freeboardrooms.add(new Integer(i));
               }
           // For all reservations
           for (int i=0;i<reservations.size();i++) {

               // If existing reservation start date is before new reservation start date
               if (reservations.get(i).getStart().compareTo(this.getStart()) < 0) {
                   // If existing reservation end date is after new reservation start date
                   if (reservations.get(i).getEnd().compareTo(this.getStart()) > 0) {
                       // If freboardrooms arraylist contains boardroom id
                       if (freeboardrooms.contains(new Integer(reservations.get(i).getBoardroomId()))) {
                           // Remove that id from freeboardrooms arraylist
                           freeboardrooms.remove(Integer.valueOf(reservations.get(i).getBoardroomId()));
                       }
                   }
               }
               // If existing reservation start date is after new reservation start date
               else {
                   // If existing reservation end date is before new reservation end date
                   if (reservations.get(i).getEnd().compareTo(this.getEnd()) < 0) {
                       // If freboardrooms arraylist contains boardroom id
                       if (freeboardrooms.contains(new Integer(reservations.get(i).getBoardroomId())))
                            // Remove that id from freeboardrooms arraylist
                            freeboardrooms.remove(Integer.valueOf(reservations.get(i).getBoardroomId()));
                   }

                   // If existing reservation start date is before new reservation end date
                   if (reservations.get(i).getStart().compareTo(this.getEnd()) < 0) {
                       // If freboardrooms arraylist contains boardroom id
                       if (freeboardrooms.contains(new Integer(reservations.get(i).getBoardroomId())))
                            // Remove that id from freeboardrooms arraylist
                            freeboardrooms.remove(Integer.valueOf(reservations.get(i).getBoardroomId()));
                   }
               }
           }
           r.close();
           return freeboardrooms;
        } catch (SQLException e) {
           return null;
        }
    }
}
