/**
Java class called Statistics, which is used to keep track of various statistics related to the application's usage and performance. 
The class contains static variables and methods for tracking the total number of sessions created, total number of sessions deleted, 
total memory usage, and uptime of the application.

totalSessionsCreated and totalSessionsDeleted:
These are static integer variables that keep track of the total number of sessions created and deleted, respectively.
The methods newSessionCreated() and sessionDeleted() are used to increment these counters when a new session is created or deleted, respectively.
The methods getTotalSessionsCreated() and getTotalSessionsDeleted() provide access to these counters.

getTotalMemoryUsageString():
This method calculates and returns a string representing the total memory usage of the application.
It uses the Runtime class to access information about the Java Virtual Machine's memory usage.
The method calculates the used memory by subtracting the free memory from the total memory.
It also retrieves the maximum memory available to the JVM.
The result is returned as a string in the format "usedMemory/totalMemory" in megabytes.

getUptimeString():
This method calculates and returns a string representing the uptime of the application since its creation.
It uses the DateFormat and SimpleDateFormat classes to format the time.
The method calculates the uptime by subtracting the application's creation time (obtained from Portal.getCreated()) from the current time.
The result is returned as a string in the format "HH:mm:ss", representing hours, minutes, and seconds.

Overall, the Statistics class provides a simple way to monitor and collect basic statistics about the application's usage and performance. 
It can be used to track the total number of sessions created and deleted, total memory usage, and the application's uptime. 
These statistics can be useful for monitoring application health and performance over time.
 */
package registrationboardroom;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class Statistics {
    private static int totalSessionsCreated=0;
    private static int totalSessionsDeleted=0;

    public static String getTotalMemoryUsageString() {
        Runtime s_runtime = Runtime.getRuntime();
        long b = s_runtime.totalMemory () - s_runtime.freeMemory ();
        long b2 = s_runtime.maxMemory();
        return String.valueOf((b/1024)/1024)+"/"+String.valueOf((b2/1024)/1024);
    }

    public static void newSessionCreated() {
        totalSessionsCreated++;
    }

    public static void sessionDeleted() {
        totalSessionsDeleted++;
    }

    public static int getTotalSessionsCreated() {
        return totalSessionsCreated;
    }

    public static int getTotalSessionsDeleted() {
        return totalSessionsDeleted;
    }

    public static String getUptimeString() {
        DateFormat time = new SimpleDateFormat("HH:mm:ss");
        Date uptime=new Date(new Date().getTime() - Portal.getCreated().getTime());
        return time.format(uptime).toString();
    }

}
