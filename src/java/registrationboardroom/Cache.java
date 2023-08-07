/*
Cleaning thread class which can manipulate with logger and cache instances. After set these in constructor method
process() is enabled to go through all Browser instances saved in cache. Then find all browser instance which
inactivity duration is greater then sessionTimeoutMinutes variable and remove them by removeBrowser from class Cache.
3 classes (CleaningThread, CleaningService, and Cache) and some utility methods used for managing a cache of Browser instances 
and implementing a cleaning service to remove inactive sessions from the cache.

CleaningThread class:
This class implements the Runnable interface and is responsible for checking the inactivity duration of Browser instances
in the cache and removing inactive sessions.
It takes the Cache, sessionTimeoutMinutes, and Logger instances as parameters in its constructor.
The run() method is used to execute the cleaning process, which is done by invoking the process() method.
The process() method iterates through all Browser instances stored in the cache, calculates their inactivity duration, 
and removes sessions with inactivity exceeding the sessionTimeoutMinutes.

CleaningService class:
This class manages the CleaningThread and provides methods to activate and deactivate the cleaning process.
It uses a ScheduledExecutorService to schedule the cleaning thread with a fixed delay.
The activate() method takes the Cache, serviceRefreshMinutes, sessionTimeoutMinutes, 
and Logger instances as parameters to create a new CleaningThread and schedule it for periodic execution.
The deactivate() method is used to stop the cleaning service by shutting down the ScheduledExecutorService.

Cache class:
This is the main cache class responsible for storing and managing Browser instances in a Hashtable 
and providing methods for accessing and modifying the cache.
It includes methods for adding, retrieving, and removing Browser instances from the cache based on their session ID.
The class also tracks user sessions using a list of usernames and provides methods to check if a username is logged in or not.
The getStats() method generates a statistics table containing information about active sessions, memory usage, uptime, etc.
The class contains methods to start and stop the cleaning service using the CleaningService class.

Overall, it is implementation of a cache mechanism for managing Browser instances 
and a cleaning service that periodically removes inactive sessions from the cache. The cache is maintained using a Hashtable, 
and the cleaning service is scheduled for execution at regular intervals using a ScheduledExecutorService. 
The Cache class also provides methods to track active user sessions and gather statistics about the cache and user activity.
 
 */
package registrationboardroom;

import java.util.Hashtable;
import java.util.Date;
import org.apache.log4j.Logger;
import javax.servlet.http.HttpSession;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.Enumeration;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


class CleaningThread implements Runnable {
    private Cache cache;
    private Logger logger;
    private int sessionTimeoutMinutes;
    
    public CleaningThread(Cache cache,int sessionTimeoutMinutes,Logger logger) {
        this.cache=cache;
        this.sessionTimeoutMinutes=sessionTimeoutMinutes;
        this.logger=logger;
    }

    public void run() {
        process();
    }

    public void process() {
        Enumeration ids = cache.getBrowsers().keys();
        Date now=new Date();
        Date duration=new Date();
        String id;
        logger.debug("Cleanning cache..");
        while (ids.hasMoreElements()) {
            id = (String)ids.nextElement();
            Browser browser = (Browser) cache.getBrowsers().get(id);
            try {
                duration=new Date(now.getTime() - browser.getSession().getLastAccessedTime());
            }
            catch (IllegalStateException e){
                cache.removeUsername(browser.getUsername());
                cache.removeBrowser(browser.getSession().getId());
                logger.debug("Removing session "+browser.getSession().getId()+" because it was inactive for more than "+String.valueOf(sessionTimeoutMinutes)+" minutes");
            }

        }
    }
}

/*
 *
 * Class, which contains ScheduledExecutorService and can activate and deactivate CleaningThread threads.
 * By serviceRefreshMinutes it is possible to set delay for calling process() method of CleaningThread again and again.
 *
 *  
 */
class CleaningService {
    private CleaningThread myThread;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public void activate(Cache cache,int serviceRefreshMinutes,int sessionTimeoutMinutes,Logger logger) {
        myThread = new CleaningThread(cache,sessionTimeoutMinutes,logger);
        scheduler.scheduleWithFixedDelay
          (myThread, 0, serviceRefreshMinutes, TimeUnit.MINUTES);
    }

    public void deactivate() {
        scheduler.shutdown();
    }
}


/**
 *
 * Main cache class, which saves all browser instances in Hashtable<Stinng,Browser>
 *
 * 
 */
public class Cache {
    private static Hashtable<String,Browser> browsers = new Hashtable<String,Browser>(); // contains Browser instance for every connected user
    private static ArrayList<String> usernames = new ArrayList<String>();
    private static Logger logger;
    private static DB db;
    private static int sessionTimeoutMinutes;
    private static int serviceRefreshMinutes;
    private static CleaningService service;
    private static boolean cleaningServiceIsRunning;
    private static int sessions;

    public static void addUsername(String username) {
        usernames.add(username);
    }

    public static void removeUsername(String username) {
        usernames.remove((String) username);
    }

    public static boolean isUsernameLogged(String username) {
        return usernames.contains((String) username);
    }

    public boolean getCleaningServiceIsRunning() {
        return cleaningServiceIsRunning;
    }

    public int getSessions() {
        return sessions;
    }

    public String getStats() {
        String html="<table width=\"70%\"><tr><td style=\"height:10px;text-align:center;width:150px;\" >User Name</td><td style=\"height:10px;text-align:center;width:80px;\" >Session ID</td><td style=\"height:10px;text-align:center;width:100px;\" >Created</td><td style=\"height:10px;text-align:center;width:100px;\">Inactivity</td></tr>";
        int logged=0;
        int unlogged=0;
        Enumeration ids = browsers.keys();
        Date now=new Date();
        sessions=0;
        while (ids.hasMoreElements()) {
            String id = (String)ids.nextElement();
            Browser browser = (Browser) browsers.get(id);

            String username;
            if (browser.getUsername()!=null) {
                username=browser.getUsername();
                logged++;
            }
            else {
                username="Not logged in";
                unlogged++;
            }
            try {
                Date duration=new Date(now.getTime() - browser.getSession().getLastAccessedTime());
                DateFormat time = new SimpleDateFormat("HH:mm");
                html+="<tr><td style=\"height:10px;text-align:center;width:150px;\">"+username+"</td><td style=\"height:10px;text-align:center;width:80px;\" >"+browser.getSession().getId()+"</td><td style=\"height:10px;text-align:center;width:100px;\">"+time.format(browser.getSession().getCreationTime()).toString() +"</td><td style=\"height:10px;text-align:center;width:100px;\" >"+String.valueOf(duration.getMinutes())+" minutes <br>"+String.valueOf(duration.getSeconds())+" seconds </td></tr>";
                sessions++;
            }
            catch (IllegalStateException e) {
            }
        }

        html+="</table><br>";
        html+="<table><tr><td style=\"width:150px;height:20px;\"> Memory usage:</td><td class=\"zeros\" style=\"float:left;height:20px;width:100px;\">"+Statistics.getTotalMemoryUsageString()+" MB </td></tr>";
        html+="<tr><td class=\"zeros\" style=\"width:150px;height:20px;\"> Uptime:</td><td class=\"zeros\" style=\"float:left;height:20px;width:100px;\">"+Statistics.getUptimeString()+"</td></tr>";
        html+="<tr><td class=\"zeros\" style=\"width:150px;height:20px;\"> Sessions created:</td><td class=\"zeros\" style=\"float:left;height:20px;width:100px;\">"+String.valueOf(Statistics.getTotalSessionsCreated())+"</td></tr>";
        html+="<tr><td class=\"zeros\" style=\"width:150px;height:20px;\"> Sessions deleted:</td><td class=\"zeros\" style=\"float:left;height:20px;width:100px;\">"+String.valueOf(Statistics.getTotalSessionsDeleted())+"</td></tr>";
        html+="<tr><td class=\"zeros\" style=\"width:150px;height:20px;\"> Active logged users:</td><td class=\"zeros\" style=\"float:left;height:20px;width:100px;\">"+String.valueOf(logged)+"</td></tr>";
        html+="<tr><td class=\"zeros\" style=\"width:150px;height:20px;\"> Active Anonymous users:</td><td class=\"zeros\" style=\"float:left;height:20px;width:100px;\">"+String.valueOf(unlogged)+"</td></tr>";

        String cleaning;
        if (cleaningServiceIsRunning) {
            cleaning="<b>Running</b><br><input type=\"hidden\" name=\"command\" value=\"stopservice\"/> <br> Checking every "+String.valueOf(serviceRefreshMinutes)+" minutes. <br> Session Timeout is "+String.valueOf(this.sessionTimeoutMinutes)+" minutes <br> <input type=\"button\" onclick=\"this.form.submit();\" value=\"Stop\"/>";
        }
        else {
            cleaning="<b>Stopped</b><br><input type=\"hidden\" name=\"command\" value=\"startservice\"/> <br> Clean every: <input style=\"float:right;width:20px;\" type=\"text\" name=\"service\" value=\""+String.valueOf(serviceRefreshMinutes)+"\"/><br><br> Timeout: <input style=\"float:right;width:20px;\" type=\"text\" name=\"session\" value=\""+String.valueOf(this.sessionTimeoutMinutes)+"\"/> <br><br> (Minutes) <br><br><input type=\"button\" onclick=\"this.form.submit();\" value=\"Start\"/>";
        }
        html+="<form action=\"./portal\" method=\"post\"><tr><td>Session Cache Cleaning Sevice</td><td style=\"float:left;\">"+String.valueOf(cleaning)+"</td></tr>";
        html+="</form></table>";
        return html;
    }

    public Cache(Logger logger,DB db,int serviceRefreshMinutes,int sessionTimeoutMinutes) {
        this.logger=logger;
        this.db=db;
        this.sessionTimeoutMinutes=sessionTimeoutMinutes;
        this.serviceRefreshMinutes=serviceRefreshMinutes;
        this.cleaningServiceIsRunning=false;
        this.logger.debug(String.valueOf(this.sessionTimeoutMinutes));
    }

    public void setServiceRefreshMinutes(int minutes) {
        this.serviceRefreshMinutes=minutes;
    }

    public int getServiceRefreshMinutes() {
        return this.serviceRefreshMinutes;
    }

    public void setSessionTimeoutMinutes(int minutes) {
        sessionTimeoutMinutes=minutes;
    }

    public int getSessionTimeoutMinutes() {
        return this.sessionTimeoutMinutes;
    }

    public void startCleaningService() {
        if (!cleaningServiceIsRunning) {
            this.service=new CleaningService();
            this.service.activate(this, serviceRefreshMinutes,sessionTimeoutMinutes, logger);
            this.cleaningServiceIsRunning=true;
            logger.debug("Cache cleaning service has started!");
            logger.debug("Scheduled cleaning every "+String.valueOf(serviceRefreshMinutes)+" minutes.");
            logger.debug("sessionTimeout is "+String.valueOf(sessionTimeoutMinutes)+" minutes.");
        }
    }

    public void stopCleaningService() {
        if (cleaningServiceIsRunning) {
            this.service.deactivate();
            this.service=null;
            this.cleaningServiceIsRunning=false;
            logger.debug("Cache cleaning service is stopped!");
        }
    }

    public Hashtable<String,Browser> getBrowsers() {
        return browsers;
    }

    public static Browser addBrowser(HttpSession session) {
        Browser browser=new Browser(logger,db,session);
        browser.getBoardrooms();
        Cache.browsers.put(session.getId(), browser);
        Statistics.newSessionCreated();
        return browser;
    }

    public static Browser getBrowser(String sessionId) {
        return Cache.browsers.get(sessionId);
    }

    public static void removeBrowser(String sessionId) {
        logger.debug("removeBrowser");
        logger.debug(sessionId);
        Cache.browsers.remove(sessionId);
        Statistics.sessionDeleted();
    }

}
