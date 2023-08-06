package registrationboardroom;

/*
Class which provide basic SHA-1 encryption
Java class called SHA, which provides basic SHA-1 encryption functionality. 
The class contains two static methods: computeHash and byteArrayToHexString.

computeHash Method:
This method takes a string x as input and returns an array of bytes representing the SHA-1 hash of the input string.
It uses the java.security.MessageDigest class, which is part of the Java Cryptography Architecture (JCA), to compute the SHA-1 hash.
The method first gets an instance of the SHA-1 message digest using java.security.MessageDigest.getInstance("SHA-1").
It then resets the digest and updates it with the bytes of the input string x using d.update(x.getBytes()).
Finally, it returns the computed hash as an array of bytes using d.digest().

byteArrayToHexString Method:
This method takes an array of bytes b as input and returns a hexadecimal representation of the byte array as a string.
It creates a StringBuffer to build the resulting hexadecimal string.
The method iterates through each byte in the input array b.
For each byte, it converts the value to an integer v, ensuring that the value is treated as an unsigned byte by applying b[i] & 0xff.
If the integer v is less than 16 (single-digit hexadecimal), the method appends a leading '0' to ensure two-digit representation.
The method then converts the integer v to its hexadecimal representation using Integer.toHexString(v).
It appends the resulting hexadecimal string to the StringBuffer.
After processing all bytes in the input array, the method returns the final hexadecimal string 
as a capitalized string using sb.toString().toUpperCase().

Overall, the SHA class provides a simple implementation of SHA-1 encryption, which is a widely used cryptographic hash function. I
t can be used to compute the SHA-1 hash of a given string and convert the resulting hash bytes into a human-readable hexadecimal representation. 
Note that SHA-1 is considered weak for cryptographic purposes due to vulnerabilities, 
and modern applications should use stronger hash functions like SHA-256 or SHA-3 for secure hashing.
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
