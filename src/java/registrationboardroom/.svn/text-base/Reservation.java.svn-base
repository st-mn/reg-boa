package registrationboardroom;

import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import org.apache.log4j.Logger;
/**
 *
 * Class for mapping reservation object to memory for easier comparing with another records
 *
 * @author cisary@gmail.com
 */
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