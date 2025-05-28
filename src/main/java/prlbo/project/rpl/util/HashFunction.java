package prlbo.project.rpl.util;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashFunction {
    public static String getHash(String passwd) {

         byte[] ASCII = passwd.getBytes();

         try {
             byte[] d = MessageDigest.getInstance("MD5").digest(ASCII);

             String res = "";
             for (byte b : d) {
                 res += Integer.toHexString(Byte.toUnsignedInt(b));
             } return res;

         } catch (NoSuchAlgorithmException e) {
             throw new SecurityException();
         }
    }

    public static void main(String[] args) {
        String s = "Aku sayang kamu";
        System.out.println(HashFunction.getHash(s));
    }
}
