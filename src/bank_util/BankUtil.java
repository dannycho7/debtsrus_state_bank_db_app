package bank_util;

import java.util.Calendar;
import java.security.*;
import java.sql.*;
import java.text.SimpleDateFormat;

abstract public class BankUtil {
    private static Calendar cal = Calendar.getInstance();

    public static String getCurrentMonthYear() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-yyyy");
        return simpleDateFormat.format(cal.getTime());
    }
    public static String getCurrentYearMonth() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        return simpleDateFormat.format(cal.getTime());
    }
    public static String getMoneyString(int cents) {
        int dollars = (int ) (cents / 100);
        return String.format("$%01d.%02d", dollars, cents % 100);
    }
    public static int getNumDaysInCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }
    public static String getPinDigest(String pin) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(pin.getBytes());
            String pin_digest = new String(messageDigest.digest());
            return pin_digest;
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Pin Digest algorithm is not working");
        }
    }
    public static String getPinDigestForSQLInsert(String pin) {
        return getPinDigest(pin).replaceAll("'", "''");
    }
    public static String getSQLTimeStamp() {
        java.util.Date uDate = cal.getTime();
        java.sql.Date sDate = new java.sql.Date(uDate.getTime());
        String timestamp = sDate.toString();
        return timestamp;
    }
    public static int getUUID() {
        return (int) (Math.random() * Integer.MAX_VALUE); // low chance of collision
    }
    public static void setCurrentDate(int year, int month, int date) {
        // month is 0-indexed such that 0 => January and 11 => December
        cal.set(year, month, date);
    }
}