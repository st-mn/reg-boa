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

/**
 *
 * Servlet class to receive and delegate requests to phones and retrieve and return XML responses
 *
 * @author cisary@gmail.com
 * 
 */
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
