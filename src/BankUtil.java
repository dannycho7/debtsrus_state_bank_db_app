package BankUtil;

abstract public class BankUtil {
    public static String getSQLTimeStamp() {
        java.util.Date uDate = new java.util.Date();
        java.sql.Date sDate = new java.sql.Date(uDate.getTime());
        String timestamp = sDate.toString();
        return timestamp;
    }

    public static int getUUID() {
        return (int) (Math.random() * Integer.MAX_VALUE); // low chance of collision
    }
}