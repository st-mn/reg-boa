package registrationboardroom;

/*
 *
 * Class which provide basic SHA-1 encryption
 *
 * @author cisary@gmail.com
 *
 */
public class SHA {

    /*
     *
     * Static method which returns array of bytes which are needed for byteArrayToHexString method
     *
     */
    public static byte[] computeHash(String x) throws Exception
    {
        java.security.MessageDigest d =null;
        d = java.security.MessageDigest.getInstance("SHA-1"); // basic usage of java API for SHA-1 encryption
        d.reset();
        d.update(x.getBytes()); // create hash
        return  d.digest(); // return hash
    }

    public static String byteArrayToHexString(byte[] b)
    {
        StringBuffer sb = new StringBuffer(b.length * 2);
        for (int i = 0; i < b.length; i++){
            int v = b[i] & 0xff;
            if (v < 16) {
            sb.append('0');
        }
        sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase();
    }

}
