/*
Servlet class to receive and delegate requests to phones and retrieve and return XML responses
Java servlet class called RB (short for RegistrationBoardroom), which handles requests from phones, generates XML responses, 
and manages phone objects. Let's break down the code and understand its main components:

Package and Imports: The class is defined within the package registrationboardroom. 
It imports various classes required for servlets, logging, and database operations.

Class Variables:

separator: A constant storing the operating system's file separator character (e.g., \ for Windows or / for Unix-based systems).
progFolder: A constant storing the location of the configuration and log folders in the Tomcat web application directory (catalina.home).
proxyConfigFile: A constant storing the path to the proxy configuration file.
logFile: A constant storing the path to the log file where log messages will be written.
TOMCAT_PORT: A variable to store the port number on which Tomcat is listening. 
It is initialized to zero and is later set to the local port if not already assigned.
Phones: A Hashtable (a synchronized hash table) that stores Phone objects referenced by their phone numbers (ext).
HOST_IP: A variable to store the IP address of the local host. 
It is initialized to null and later set to the local IP address if not already assigned.
version: A constant representing the version of the application.
JDBC_url: A constant storing the JDBC URL for connecting to the MySQL database.
logger: An instance of the Logger class from Apache Log4j library used for logging.
db: An instance of the DB class representing the database connection and operations.
Init() Method: This method is executed when the servlet is initialized. 
It sets up the logger and checks the proxy configuration file for details. 
If proxy configuration is enabled, it sets the system properties for proxy.

doGet() Method: This method is executed when a GET request is received from a phone. 
It handles phone-related parameters such as IP address and phone number (ipaddress and phonenumber in the request). 
Based on these parameters, it creates or retrieves the corresponding Phone object from the Phones hash table. 
Then, it calls the generateXML() method on the Phone object to obtain the XML response, which is sent back to the phone in the HTTP response.

Phone Management: The code handles phone-related tasks by interacting with the Phone class (not provided in the code snippet). 
The Phone class seems to represent phone objects and is used to generate XML responses for phone requests.

Database Operations: The code interacts with the database using the DB class for various tasks (e.g., retrieving phone data). 

Overall, this servlet handles communication with phones, manage phone objects, and generate XML responses based on phone requests. 
The XML responses likely contain data related to boardroom reservations or other relevant information. 
  
 */
package registrationboardroom;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;


public class RB extends HttpServlet {

    private static final long serialVersionUID = -31646456290060949L;
    private static final String separator = System.getProperty("file.separator"); // Operating system's file separator character
    private static final String progFolder = System.getProperty("catalina.home")+ separator + "webapps" + separator + "RB" + separator; // Configs and log folder location
    public static final String proxyConfigFile = progFolder + "config.txt"; // Proxy configuration file
    public static final String logFile = progFolder + "RegistrationBoardroom.log"; // Log file path
    public static int TOMCAT_PORT = 0; // Port tomcat is listening on
    private static final Hashtable<String, Phone> Phones = new Hashtable<String, Phone>(); // Hashtable of phone objects referenced by their phonenumber
    public static String HOST_IP = null; // IP address of the local host
    private static final String version = "1.0.0"; // Version of application
    public static final String JDBC_url = "jdbc:mysql://localhost:8889/registrationboardroom?user=root&password=root";
    private static Logger logger = null; // Logger
    private static DB db = null;

    /**
     *
     * Initialise servlet
     *
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        PatternLayout logLayout = new PatternLayout("[%p][%d{dd/MM/yyyy HH:mm:ss}][%C{1}] %m%n");

        // Logger initialization
        try {
            logger = Logger.getLogger("RegistrationBoardroom");
            logger.addAppender(new FileAppender(logLayout, logFile));
            logger.addAppender(new ConsoleAppender(logLayout));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Check config file for proxy details and apply them
        boolean proxyboolean = false;
        String proxyAddress = "";
        String proxyPort = "";

        try {
            FileReader input = new FileReader(proxyConfigFile);
            BufferedReader reader = new BufferedReader(input);
            String temp = "";
            int index = 0;
            while (temp != null) {
                temp = reader.readLine();
                if (index == 0) { // First line is true if useProxy
                    //logger.error("Test index == 0");
                    proxyboolean = Boolean.parseBoolean(temp);
                    if (!proxyboolean)
                        break;
                }
                if (index == 1) { // Second line is proxy address
                    //logger.error("Test index == 1");
                    proxyAddress = temp;
                }
                if (index == 2) { // Third line is proxy port
                    //logger.error("Test index == 2");
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
        db=new DB(logger,JDBC_url);
    }

    /**
     *
     * Method executed each time a request is received from the phone
     *
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Phone p = null; //Current phone object

        // Get parameters relating to phone
        String phoneIP = request.getParameter("ipaddress");
        String ext = request.getParameter("phonenumber");

        logger.debug("doGet");
        logger.debug(request.getRemoteAddr());

        // Assign values to local IP and port if they have none
        if (TOMCAT_PORT == 0) {
            TOMCAT_PORT = request.getLocalPort();
        }

        if (HOST_IP == null) {
            HOST_IP = request.getLocalAddr();
        }

        // First request from phone
        if (phoneIP != null && ext != null) {
            if (Phones.containsKey(ext)) { // Existing object for this phone so remove and null
                p = Phones.remove(ext);
                p = null;
            }

            p = new Phone(logger,db,ext);
            Phones.put(ext, p);
        } else { // Subsequent request so retrieve relevant phone object
            p = Phones.get(ext);

            if (p == null) { // Would only happen if server shut down and phone app not
                p = new Phone(logger,db,ext); // Calling constructor needed
            }
        }

        String xml = p.generateXML(request); //Get XML response
        response.setCharacterEncoding("UTF-8"); //Set response to encode XML
        PrintWriter out = response.getWriter();
        
        out.println(xml);
        out.flush();
        out.close();
    }
}
