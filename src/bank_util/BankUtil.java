package bank_util;

import java.util.Calendar;
import java.text.SimpleDateFormat;

abstract public class BankUtil {
    public static String getCurrentMonthYear() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-yyyy");
        return simpleDateFormat.format(new java.util.Date());
    }
    public static String getCurrentYearMonth() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        return simpleDateFormat.format(new java.util.Date());
    }
    public static String getMoneyString(int cents) {
        int dollars = (int ) (cents / 100);
        return String.format("$%01d.%02d", dollars, cents % 100);
    }
    public static int getNumDaysInCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }
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