package bank_util;

import java.text.SimpleDateFormat;

abstract public class BankUtil {
    public static String getSQLTimeStamp() {
        java.util.Date uDate = new java.util.Date();
        java.sql.Date sDate = new java.sql.Date(uDate.getTime());
        String timestamp = sDate.toString();
        return timestamp;
    }

    public static String getCurrentMonthYear() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-yyyy");
        return simpleDateFormat.format(new java.util.Date());
    }

    public static int getUUID() {
        return (int) (Math.random() * Integer.MAX_VALUE); // low chance of collision
    }
}