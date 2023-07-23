/**
Class for initialization mySQL connection by JDBC driver and creating needed tables.
Also serve all database operations, which are public methods with all needed parameters.
Java class named DB, which is responsible for handling the connection and operations with a MySQL database 
in a system related to boardroom reservations. It includes methods for initializing the connection, retrieving, updating, 
and deleting data from various database tables.

Class Members:
jdbcurl: A private string variable used to store the JDBC URL for connecting to the MySQL server.
logger: A private instance of the Logger class from the log4j library used for logging.
con: A Connection variable used in each method to perform database operations.
mysqlDateTimeFormat: A string variable representing the date-time format used in MySQL database queries.

Constructor:
The class has a constructor that takes a Logger instance and a JDBC URL as parameters.
The constructor initializes the logger, jdbcurl, and the con (connection) variables by calling the getConnection method.
Database Connection and Initialization:

The getConnection method is used to initialize the JDBC driver and establish a connection to the MySQL server using the provided JDBC URL.
The connection is established by loading the MySQL JDBC driver class and using the DriverManager.getConnection() method.
The method returns the connection object or null if an error occurs during the connection process.

Database Operation Methods:
The class includes several public methods that perform various database operations such as retrieving reservations, 
boardrooms, users, and their settings, adding new records, updating records, and deleting records.
Each method executes an SQL query and returns the results as a ResultSet object, which can be used to access the retrieved data.

SQL Queries:
The SQL queries are constructed using string concatenation, which may pose a risk of SQL injection if not handled properly. 
Ideally, the code should use prepared statements to prevent SQL injection attacks.

Initialization and Dropping of Tables:
The class has private methods dbinit and dbdrop, which are used for creating and dropping the necessary tables, respectively. 
These methods are commented out to prevent unintentional table creation or deletion.
It's important to note that this code is designed to be used with a specific MySQL database schema, 
which includes tables for boardroom, user, reservation, and sequences. The code also assumes the existence of other supporting tables like setting. 
Additionally, it performs some basic hashing of passwords using the SHA-1 algorithm before storing them in the database.

Overall, the code provides a basic database connection and operations interface for managing boardroom reservations and related user data. 
However, it may need further improvements to enhance security and adapt it to specific application requirements.
 */

package registrationboardroom;

import java.sql.*;
import org.apache.log4j.Logger;
import java.text.DateFormat;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;


public class DB {
    private String jdbcurl; // internal url string for connecting to mySQL server
    private Logger logger; // logger instance created in RB.java
    private Connection con; // connection variable used in every method for database operations
    private final String mysqlDateTimeFormat = "yyyy-MM-dd HH:mm:ss";

    /**
     *
     * Constructor calls getConnection initialize "con" and "logger" variables
     *
     * @param l
     * @param url   JDBC url for connecing to database
     */
    public DB(Logger l,String url)
    {
        logger = l;
        jdbcurl = url;
        con = getConnection();
        //dbdrop();
        //dbinit();            // !!! for the first time it's necessary to uncomment this line to create tables
    }

    /**
     *
     * Method for initialize JDBC driver with
     *
     * @return
     */
    public Connection getConnection() {
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(jdbcurl);
            logger.debug("Connecion o.k");
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
        } catch (ClassNotFoundException cE) {
            logger.error("ClassNotFoundException");
        }
        return con;
    }


    /*
     *
     * Method for retrieving all reservation records
     *
     */
    public ResultSet getReservations()
    {
        try {
            Statement query = con.createStatement();
            ResultSet rs = query.executeQuery("SELECT `id`,`user_id`,`boardroom_id`,`event_name`, `start`,`end` FROM `registrationboardroom`.`reservation` ORDER BY `start`;");
            
            return rs;
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
            return null;
        }
    }

    public ResultSet getReservationsOfBoardroomByRange(int boardroom_id,Date start,Date end)
    {
        try {
            Statement query = con.createStatement();
            DateFormat sqldatetime = new SimpleDateFormat(mysqlDateTimeFormat);
            ResultSet rs = query.executeQuery("SELECT `id`,`user_id`,`boardroom_id`,`event_name`,`start`,`end` FROM `registrationboardroom`.`reservation` WHERE `start` >= '"+sqldatetime.format(start).toString()+"' AND `end` <= '"+sqldatetime.format(end).toString()+"' AND `boardroom_id`="+String.valueOf(boardroom_id)+" ORDER BY `start`;");
            return rs;
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
            return null;
        }
    }

    public ResultSet getUserReservationsOfBoardroomByRange(int user_id,int boardroom_id,Date start,Date end)
    {
        try {
            Statement query = con.createStatement();
            DateFormat sqldatetime = new SimpleDateFormat(mysqlDateTimeFormat);
            ResultSet rs = query.executeQuery("SELECT `id`,`user_id`,`boardroom_id`,`event_name`,`start`,`end` FROM `registrationboardroom`.`reservation` WHERE `start` >= '"+sqldatetime.format(start).toString()+"' AND `end` <= '"+sqldatetime.format(end).toString()+"' AND `boardroom_id`="+String.valueOf(boardroom_id)+" AND `user_id`="+String.valueOf(user_id)+" ORDER BY `start`;");
            return rs;
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
            return null;
        }
    }


    public ResultSet getReservationsByPhone(int phone)
    {
        try {
            Statement query = con.createStatement();
            ResultSet rs = query.executeQuery("SELECT `id`,`user_id`,`boardroom_id`,`event_name`,`start`,`end` FROM `registrationboardroom`.`reservation` WHERE `user_id`="+String.valueOf(getUserIdByPhone(String.valueOf(phone)))+" ORDER BY `start`;");
            return rs;
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
            return null;
        }
    }

    public ResultSet getReservationsByBoardroom(int boardroom_id)
    {
        try {
            Statement query = con.createStatement();
            ResultSet rs = query.executeQuery("SELECT `id`,`user_id`,`boardroom_id`,`event_name`,`start`,`end` FROM `registrationboardroom`.`reservation` WHERE `boardroom_id`="+String.valueOf(boardroom_id)+";");
            return rs;
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
            return null;
        }
    }

    public ResultSet getReservation(int id) {
        try {
            Statement query = con.createStatement();
            ResultSet rs = query.executeQuery("SELECT `id`,`user_id`,`boardroom_id`,`event_name`,`start`,`end` FROM `registrationboardroom`.`reservation` WHERE `id`="+String.valueOf(id)+";");
            return rs;
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
            return null;
        }
    }

    public boolean deleteReservation(int id) {
        try {
            Statement query = con.createStatement();
            query.executeUpdate("DELETE FROM `registrationboardroom`.`reservation` WHERE `id`="+String.valueOf(id)+";");
            query.close();
            return true;
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
            return false;
        }
    }

    /**
     *
     * Method returns all record from table "boardroom"
     *
     * @return
     */
    public ResultSet getBoardrooms()
    {
        try {
            Statement query = con.createStatement();
            ResultSet rs = query.executeQuery("SELECT `id`,`name` FROM `registrationboardroom`.`boardroom`;");
            //logger.debug("OOKK");
            return rs;
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
            return null;
        }
    }

    /*
     *
     * Method used for getting single boardroom record with id
     *
     * @param id  primary key of record
     */
    public ResultSet getBoardroom(int id)
    {
        try {
            Statement query = con.createStatement();
            ResultSet rs = query.executeQuery("SELECT `id`,`name` FROM `registrationboardroom`.`boardroom` WHERE `id`="+String.valueOf(id)+";");
            return rs;
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
            return null;
        }
    }

    public String getBoardroomName(int id)
    {
        try {
            Statement query = con.createStatement();
            ResultSet rs = query.executeQuery("SELECT `id`,`name` FROM `registrationboardroom`.`boardroom` WHERE `id`="+String.valueOf(id)+";");
            rs.next();
            return rs.getString("name");
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
            return null;
        }
    }

    public boolean boardroomNameExist(String name)
    {
        try {
            Statement query = con.createStatement();
            logger.debug(name);
            ResultSet rs = query.executeQuery("SELECT `id`,`name` FROM `registrationboardroom`.`boardroom` WHERE `name`=\""+name+"\";");
            if (rs.next()) {
                logger.debug("rs.next true!");
                return true;
            }
            else
                return false;
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
            return false;
        }
    }

    /**
     *
     * Method add new record to table "boardroom" with name from parameter
     *
     * @param name
     */
    public boolean addBoardroom(String name)
    {
        try {
            Statement query = con.createStatement();
            ResultSet rs = query.executeQuery("SELECT `boardrooms` FROM `registrationboardroom`.`sequences` WHERE `id`=1;");
            rs.next();
            int boardrooms = rs.getInt("boardrooms");
            boardrooms+=1;
            query.executeUpdate("UPDATE `registrationboardroom`.`sequences` SET `boardrooms`="+String.valueOf(boardrooms)+" WHERE `id`=1;");
            query.executeUpdate("INSERT INTO `registrationboardroom`.`boardroom` (`id`,`name`) VALUES ("+String.valueOf(boardrooms)+",\""+name+"\");");
            query.close();
            return true;
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
            return false;
        }
    }

    /*
     *
     * Method for insert new reservation record to database
     *
     */
    public boolean addReservation(String phone,String event_name,int boardroom_id,Date start,Date end)
    {
        try {
            Statement query = con.createStatement();
            ResultSet rs = query.executeQuery("SELECT `reservations` FROM `registrationboardroom`.`sequences` WHERE `id`=1;");
            rs.next();
            int reservations = rs.getInt("reservations");
            reservations+=1;
            DateFormat sqldatetime = new SimpleDateFormat(mysqlDateTimeFormat);
            query.executeUpdate("UPDATE `registrationboardroom`.`sequences` SET `reservations`="+String.valueOf(reservations)+" WHERE `id`=1;");
            query.executeUpdate("INSERT INTO `registrationboardroom`.`reservation`(`id`,`user_id`,`event_name`,`boardroom_id`,`start`,`end`) VALUES("+String.valueOf(reservations)+","+String.valueOf(getUserIdByPhone(phone))+",\""+event_name+"\","+String.valueOf(boardroom_id)+",\""+sqldatetime.format(start).toString()+"\",\""+sqldatetime.format(end).toString()+"\");");
            query.close();
            return true;
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
            return false;
        }
    }

    public boolean updateReservation(int id,int phone,String event_name,int boardroom_id,Date start,Date end)
    {
        try {
            Statement query = con.createStatement();
            DateFormat sqldatetime = new SimpleDateFormat(mysqlDateTimeFormat);
            query.executeUpdate("UPDATE `registrationboardroom`.`reservation` SET `user_id`="+String.valueOf(getUserIdByPhone(String.valueOf(phone)))+", `event_name`=\""+event_name+"\", `boardroom_id`="+String.valueOf(boardroom_id)+", `start`=\""+sqldatetime.format(start).toString()+"\", `end`=\""+sqldatetime.format(end).toString()+"\" WHERE `id`="+String.valueOf(id)+";");
            query.close();
            return true;
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
            return false;
        }
    }

    /**
     *
     * Method updates name column of table "boardroom" with id from parameter
     *
     * @param id
     * @param name
     */
    public boolean updateBoardroom(int id,String name)
    {
        try {
            Statement query = con.createStatement();
            query.executeUpdate("UPDATE `registrationboardroom`.`boardroom` SET `name`=\""+name+"\" WHERE `id`="+String.valueOf(id)+";");
            query.close();
            return true;
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
            return false;
        }
    }

    /**
     *
     * Method deletes record from table "boardroom" with id from parameter
     *
     * @param id
     */
    public boolean deleteBoardroom(int id)
    {
        try {
            Statement query = con.createStatement();
            query.executeUpdate("DELETE FROM `registrationboardroom`.`boardroom` WHERE `id`="+String.valueOf(id)+";");
            query.close();
            return true;
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteUser(int id)
    {
        try {
            Statement query = con.createStatement();
            query.executeUpdate("DELETE FROM `registrationboardroom`.`user` WHERE `id`="+String.valueOf(id)+";");
            query.close();
            return true;
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
            return false;
        }
    }

    public boolean addUser(String phone,String name,String password)
    {
        try {
            Statement query = con.createStatement();
            ResultSet rs = query.executeQuery("SELECT `users` FROM `registrationboardroom`.`sequences` WHERE `id`=1;");
            rs.next();
            int users = rs.getInt("users");
            users+=1;

            String outputHash="";
            try {
                outputHash = SHA.byteArrayToHexString(SHA.computeHash(password));
            } catch (Exception e) {
                logger.error("Error computing SHA-1 hash of new password: ", e);
                return false;
            }

            query.executeUpdate("UPDATE `registrationboardroom`.`sequences` SET `users`="+String.valueOf(users)+" WHERE `id`=1;");
            query.executeUpdate("INSERT INTO `registrationboardroom`.`user` (`id`,`phone`,`name`,`password`) VALUES ("+users+",\""+phone+"\",\""+name+"\",\""+outputHash+"\");"); // password is "000"query.close();
          
            String timezone=TimeZone.getDefault().getID();
            query.executeUpdate("INSERT INTO `registrationboardroom`.`setting` (`id`,`user_id`,`timezone`,`starthour`,`endhour`,`precision`,`firstday`,`weekend`,`ampm`) VALUES ("+users+","+users+",\""+timezone+"\",7,17,1,1,0,0)");

            return true;
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
            return false;
        }
    }
    
    public String changeUsersPassword(String phone,String password) {
        try {
            Statement query = con.createStatement();
            String outputHash="";
            try {
                outputHash = SHA.byteArrayToHexString(SHA.computeHash(password));
            } catch (Exception e) {
                logger.error("Error computing SHA-1 hash of new password: ", e);
                return null;
            }
            query.executeUpdate("UPDATE `registrationboardroom`.`user` SET `password`=\""+outputHash+"\" WHERE `phone`="+String.valueOf(phone)+";");
            query.close();
            return outputHash;
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
            return null;
        }
    }

    public boolean changeUserName(String phone,String name) {
        try {
            Statement query = con.createStatement();
            query.executeUpdate("UPDATE `registrationboardroom`.`user` SET `name`=\""+name+"\" WHERE `phone`="+String.valueOf(phone)+";");
            query.close();
            return true;
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
            return false;
        }
    }

    public String changeAdministratorsPassword(String password) {
        try {
            Statement query = con.createStatement();
            String outputHash="";
            try {
                outputHash = SHA.byteArrayToHexString(SHA.computeHash(password));
            } catch (Exception e) {
                logger.error("Error computing SHA-1 hash of new password: ", e);
                return null;
            }
            query.executeUpdate("UPDATE `registrationboardroom`.`user` SET `password`=\""+outputHash+"\" WHERE `id`=1;");
            query.close();
            return outputHash;
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
            return null;
        }
    }

    public String getAdministratorPassword() {
        try {
            Statement query = con.createStatement();
            ResultSet rs = query.executeQuery("SELECT `password`,`id` FROM `registrationboardroom`.`user` WHERE `id`=1;");
            rs.next();
            return rs.getString("password");
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
            return null;
        }
    }

    public String getUserPassword(int user_id) {
        try {
            Statement query = con.createStatement();
            ResultSet rs = query.executeQuery("SELECT `id`,`password`,`phone` FROM `registrationboardroom`.`user` WHERE `id`="+String.valueOf(user_id)+";");
            rs.next();
            return rs.getString("password");
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
            return null;
        }
    }

    public boolean resetUserPassword(int user_id) {
        try {
            Statement query = con.createStatement();
            query.executeUpdate("UPDATE `registrationboardroom`.`user` SET `password`=\"8AEFB06C426E07A0A671A1E2488B4858D694A730\" WHERE `id`="+String.valueOf(user_id)+";");
            query.close();
            return true;
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
            return false;
        }
    }

    public String getUserNameByPhone(String phone) {
        try {
            Statement query = con.createStatement();
            ResultSet rs = query.executeQuery("SELECT `name`,`phone` FROM `registrationboardroom`.`user` WHERE `phone`="+phone+";");
            rs.next();
            return rs.getString("name");
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
            return null;
        }
    }

    public int getUserIdByPhone(String phone) {
        try {
            Statement query = con.createStatement();
            ResultSet rs = query.executeQuery("SELECT `id`,`phone` FROM `registrationboardroom`.`user` WHERE `phone`="+phone+";");
            rs.next();
            return rs.getInt("id");
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
            return 0;
        }
    }

    public int getUserIdByName(String name) {
        try {
            Statement query = con.createStatement();
            ResultSet rs = query.executeQuery("SELECT `id`,`name` FROM `registrationboardroom`.`user` WHERE `name`="+name+";");
            if (rs.next())
                logger.debug("userIdfound!");
            return rs.getInt("id");
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
            return 0;
        }
    }
    
    public ResultSet getUsers() {
        try {
            Statement query = con.createStatement();
            ResultSet rs = query.executeQuery("SELECT `id`,`phone`,`name` FROM `registrationboardroom`.`user`;");
            return rs;
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
            return null;
        }
    }


    public boolean phoneInUsers(int phone) {
        try {
            Statement query = con.createStatement();
            ResultSet rs = query.executeQuery("SELECT `id` FROM `registrationboardroom`.`user` WHERE `phone`="+String.valueOf(phone)+";");
            if (rs.next())
                return true;
            else
                return false;
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
            return false;
        }
    }

    public int getPhoneByUserId(int id) {
        try {
            Statement query = con.createStatement();
            ResultSet rs = query.executeQuery("SELECT `id`,`phone` FROM `registrationboardroom`.`user` WHERE `id`="+String.valueOf(id)+";");
            rs.next();
            return rs.getInt("phone");
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
            return 0;
        }
    }

    public TableSetting getUserSetting(int user_id) {
        try {
            Statement query = con.createStatement();
            ResultSet rs = query.executeQuery("SELECT `timezone`,`user_id`,`starthour`,`endhour`,`precision`,`firstday`,`weekend`,`ampm` FROM `registrationboardroom`.`setting` WHERE `user_id`="+String.valueOf(user_id)+";");
            rs.next();
            return new TableSetting(rs.getString("timezone"),rs.getInt("starthour"),rs.getInt("endhour"),rs.getInt("precision"),rs.getInt("firstday"),rs.getInt("weekend"),rs.getInt("ampm"));
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
            return null;
        }
    }

    public boolean updateUserSetting(TableSetting s,int user_id) {
    try {
            Statement query = con.createStatement();
            query.executeUpdate("UPDATE `registrationboardroom`.`setting` SET `timezone`=\""+s.getTimezone()+"\", `starthour`="+String.valueOf(s.getStartHour())+", `endhour`="+String.valueOf(s.getEndHour())+", `precision`="+String.valueOf(s.getPrecision())+", `firstday`="+String.valueOf(s.getFirstDay())+", `weekend`="+String.valueOf(s.getWeekend())+", ampm="+String.valueOf(s.getAMPM())+" WHERE `user_id`="+String.valueOf(user_id)+";");
            query.close();
            return true;
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
            return false;
        }
    }

    public boolean updateUserNameAndNumber(int user_id,String name,String number) {
    try {
            Statement query = con.createStatement();
            String utf=new String("ISO8859_1");
            utf=name;
            logger.debug(utf);

            query.executeUpdate("UPDATE `registrationboardroom`.`user` SET `name`=\""+name+"\", `phone`="+number+" WHERE `id`="+String.valueOf(user_id)+";");
            query.close();
            return true;
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
            return false;
        }
    }



    /**
     *
     * Method for creating all needed tables. Is necessary just first time of building and launching
     *
     */
    private void dbinit() {
        try {
            Statement query = con.createStatement();
            query.executeUpdate("CREATE TABLE  `registrationboardroom`.`sequences` (`id` INT NOT NULL ,`boardrooms` INT NOT NULL ,`users` INT NOT NULL,`reservations` INT NOT NULL ,PRIMARY KEY (  `id` )) ENGINE = MYISAM CHARACTER SET utf8 COLLATE utf8_unicode_ci;");
            query.executeUpdate("CREATE TABLE  `registrationboardroom`.`boardroom` (`id` INT NOT NULL ,`name` VARCHAR( 30 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL ,PRIMARY KEY (  `id` )) ENGINE = MYISAM CHARACTER SET utf8 COLLATE utf8_unicode_ci;");
            query.executeUpdate("CREATE TABLE  `registrationboardroom`.`user` (`id` INT NOT NULL,`phone` INT NOT NULL, `name` VARCHAR( 30 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL, `password` VARCHAR( 47 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,PRIMARY KEY (  `id` ))");
            query.executeUpdate("CREATE TABLE  `registrationboardroom`.`reservation` (`id` INT NOT NULL ,`user_id` INT NOT NULL ,`event_name` VARCHAR( 30 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL ,`boardroom_id` INT NOT NULL ,`start` DATETIME NOT NULL ,`end` DATETIME NOT NULL ,PRIMARY KEY (  `id` ),FOREIGN KEY (`boardroom_id`) REFERENCES boardroom(`id`),FOREIGN KEY (`user_id`) REFERENCES user(`id`)) ENGINE = MYISAM CHARACTER SET utf8 COLLATE utf8_unicode_ci;");
            query.executeUpdate("CREATE TABLE  `registrationboardroom`.`setting` (`id` INT NOT NULL , `user_id` INT NOT NULL, `timezone` VARCHAR( 30 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL, `starthour` INT NOT NULL,`endhour` INT NOT NULL, `precision` INT NOT NULL, `firstday` INT NOT NULL, `weekend` INT NOT NULL, `ampm` INT NOT NULL, PRIMARY KEY (  `id` ), FOREIGN KEY (`user_id`) REFERENCES user(`id`)) ENGINE = MYISAM CHARACTER SET utf8 COLLATE utf8_unicode_ci;");
            query.executeUpdate("INSERT INTO `registrationboardroom`.`sequences` (`id`,`boardrooms`,`reservations`,`users`) VALUES (1,0,0,1);");
            query.executeUpdate("INSERT INTO `registrationboardroom`.`user` (`id`,`phone`,`name`,`password`) VALUES (1,0,\"admin\",\"8AEFB06C426E07A0A671A1E2488B4858D694A730\");"); // password is "000"
            String timezone=TimeZone.getDefault().getID();
            query.executeUpdate("INSERT INTO `registrationboardroom`.`setting` (`id`,`user_id`,`timezone`,`starthour`,`endhour`,`precision`,`firstday`,`weekend`,`ampm`) VALUES (1,1,\""+timezone+"\",7,17,1,1,0,0)");
            query.close();
            con.commit();
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
        }
    }

    private void dbdrop() {
        try {

            Statement query = con.createStatement();
            query.executeUpdate("DROP TABLE `registrationboardroom`.`sequences` ;");
            query.executeUpdate("DROP TABLE `registrationboardroom`.`boardroom` ;");
            query.executeUpdate("DROP TABLE `registrationboardroom`.`reservation` ;");
            query.executeUpdate("DROP TABLE `registrationboardroom`.`user` ");
            query.executeUpdate("DROP TABLE `registrationboardroom`.`setting` ");
            query.close();
            con.commit();
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
        }
    }
}
