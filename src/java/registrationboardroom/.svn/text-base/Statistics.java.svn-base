package registrationboardroom;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 *
 * @author cisary@gmail.com
 *
 */
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
