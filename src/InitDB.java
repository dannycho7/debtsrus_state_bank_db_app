import bank_util.*;
import models.*;
import models.account.*;
import models.transaction.*;

import java.sql.*;
import java.util.Calendar;

public class InitDB {
    public static void main(String[] args) {
        Connection conn = JDBCConnectionManager.getConnection();
        if (conn == null) {
            throw new RuntimeException("Could not connect to database");
        }
        try {
            Customer.create(conn, "361721022", "Alfred Hitchcock", "6667 El Colegio #40", "1234", false);
            Customer.create(conn, "231403227", "Billy Clinton", "5777 Hollister", "1468", false);
            Customer.create(conn, "412231856", "Cindy Laugher", "7000 Hollister", "3764", false);
            Customer.create(conn, "207843218", "David Copperfill", "1357 State St", "8582", false);
            Customer.create(conn, "122219876", "Elizabeth Sailor", "4321 State St", "3856", false);
            Customer.create(conn, "401605312", "Fatal Castro", "3756 La Cumbre Plaza", "8193", false);
            Customer.create(conn, "201674933", "George Brush", "5346 Foothill Av", "9824", false);
            Customer.create(conn, "212431965", "Hurryson Ford", "678 State St", "3532", false);
            Customer.create(conn, "322175130", "Ivan Lendme", "1235 Johnson Dr", "8471", false);
            Customer.create(conn, "344151573", "Joe Pepsi", "3210 State St", "3692", false);
            Customer.create(conn, "209378521", "Kelvin Costner", "Santa Cruz #3579", "4659", false);
            Customer.create(conn, "212116070", "Li Kung", "2 People''s Rd Beijing", "9173", false);
            Customer.create(conn, "188212217", "Magic Jordon", "3852 Court Rd", "7351", false);
            Customer.create(conn, "203491209", "Nam-Hoi Chung", "1997 People''s St HK", "5340", false);
            Customer.create(conn, "210389768", "Olive Stoner", "6689 El Colegio #151", "8452", false);
            Customer.create(conn, "400651982", "Pit Wilson", "911 State St", "1821", false);

            String ALFRED_HITCHCOCK_ID = "361721022";
            String BILLY_CLINTON_ID = "231403227";
            String CINDY_LAUGHER_ID = "412231856";
            String DAVID_COPPERFILL_ID = "207843218";
            String ELIZABETH_SAILOR_ID = "122219876";
            String FATAL_CASTRO_ID = "401605312";
            String GEORGE_BRUSH_ID = "201674933";
            String HURRYSON_FORD_ID = "212431965";
            String IVAN_LENDME_ID = "322175130";
            String JOE_PEPSI_ID = "344151573";
            String KELVIN_COSTNER_ID = "209378521";
            String LI_KUNG_ID = "212116070";
            String MAGIC_JORDAN_ID = "188212217";
            String NAM_HOI_CHUNG_ID = "203491209";
            String OLIVE_STONER_ID = "210389768";
            String PIT_WILSON_ID = "400651982";

            // TODO: Reorder these operations and figure out a way to programmatically change date

            BankUtil.setCurrentDate(2011, Calendar.MARCH, 2);
            CheckingAccount.createForStudent(conn, 17431, 20000, "San Francisco", JOE_PEPSI_ID, new String[] {
                    CINDY_LAUGHER_ID,
                    IVAN_LENDME_ID
            }, IVAN_LENDME_ID, false);
            TransactionFactory.createDeposit(conn, 880000, JOE_PEPSI_ID, 17431, false);

            BankUtil.setCurrentDate(2011, Calendar.MARCH, 3);
            CheckingAccount.createForStudent(conn, 54321, 2100000, "Los Angeles", HURRYSON_FORD_ID, new String[]{
                    CINDY_LAUGHER_ID,
                    ELIZABETH_SAILOR_ID,
                    NAM_HOI_CHUNG_ID
            }, HURRYSON_FORD_ID, false);
            CheckingAccount.createForStudent(conn, 12121, 120000, "Goleta", DAVID_COPPERFILL_ID, new String[] {}, DAVID_COPPERFILL_ID, false);
            TransactionFactory.createWithdrawal(conn, 300000, ELIZABETH_SAILOR_ID, 54321, false);

            CheckingAccount.createForInterest(conn, 41725, 1500000, "Los Angeles", GEORGE_BRUSH_ID, new String[] {
                    FATAL_CASTRO_ID,
                    BILLY_CLINTON_ID
            }, GEORGE_BRUSH_ID, false);
            CheckingAccount.createForInterest(conn, 93156, 200000000, "Goleta", KELVIN_COSTNER_ID, new String[] {
                    MAGIC_JORDAN_ID,
                    OLIVE_STONER_ID,
                    ELIZABETH_SAILOR_ID,
                    NAM_HOI_CHUNG_ID
            }, ELIZABETH_SAILOR_ID, false);

            BankUtil.setCurrentDate(2011, Calendar.MARCH, 4);
            PocketAccount.create(conn, 53027, 5000, "Goleta", DAVID_COPPERFILL_ID, new String[] {}, DAVID_COPPERFILL_ID, 12121, false);
            SavingsAccount.create(conn, 43942, 128900, "Santa Barbara", ALFRED_HITCHCOCK_ID, new String[] {
                    PIT_WILSON_ID,
                    HURRYSON_FORD_ID,
                    IVAN_LENDME_ID
            }, HURRYSON_FORD_ID, false);
            SavingsAccount.create(conn, 29107, 3400000, "Los Angeles", KELVIN_COSTNER_ID, new String[] {
                    LI_KUNG_ID,
                    OLIVE_STONER_ID
            }, KELVIN_COSTNER_ID, false);

            BankUtil.setCurrentDate(2011, Calendar.MARCH, 5);
            SavingsAccount.create(conn, 19023, 230000, "San Fransisco", CINDY_LAUGHER_ID, new String[] {
                    GEORGE_BRUSH_ID,
                    FATAL_CASTRO_ID
            }, CINDY_LAUGHER_ID, false);
            PocketAccount.create(conn, 60413, 2000, "Santa Cruz", ALFRED_HITCHCOCK_ID, new String[] {
                    PIT_WILSON_ID,
                    ELIZABETH_SAILOR_ID,
                    BILLY_CLINTON_ID
            }, PIT_WILSON_ID, 43942, false);
            SavingsAccount.create(conn, 32156, 100000, "Goleta", MAGIC_JORDAN_ID, new String[] {
                    DAVID_COPPERFILL_ID,
                    ELIZABETH_SAILOR_ID,
                    JOE_PEPSI_ID,
                    NAM_HOI_CHUNG_ID,
                    OLIVE_STONER_ID
            }, JOE_PEPSI_ID, false);
            CheckingAccount.createForInterest(conn, 76543, 845600, "Santa Barbara", LI_KUNG_ID, new String[] {
                    MAGIC_JORDAN_ID
            }, MAGIC_JORDAN_ID, false);
            PocketAccount.create(conn, 43947, 3000, "Isla Vista", LI_KUNG_ID, new String[] {
                    OLIVE_STONER_ID
            }, LI_KUNG_ID, 29107, false);
            TransactionFactory.createWithdrawal(conn, 200000, LI_KUNG_ID, 76543, false);
            TransactionFactory.createPurchase(conn, 500, DAVID_COPPERFILL_ID, 53027, false);

            BankUtil.setCurrentDate(2011, Calendar.MARCH, 6);
            PocketAccount.create(conn, 67521, 10000, "Santa Barbara", KELVIN_COSTNER_ID, new String[] {
                    FATAL_CASTRO_ID,
                    HURRYSON_FORD_ID
            }, FATAL_CASTRO_ID, 19023, false);
            TransactionFactory.createWithdrawal(conn, 100000000, MAGIC_JORDAN_ID, 93156, false);
            TransactionFactory.createWriteCheck(conn, 95000000, KELVIN_COSTNER_ID, 93156, false);
            TransactionFactory.createWithdrawal(conn, 400000, LI_KUNG_ID, 29107, false);
            TransactionFactory.createCollect(conn, 1000, OLIVE_STONER_ID, 43947, 29107, false);
            TransactionFactory.createTopUp(conn, 3000, LI_KUNG_ID, 43947, 29107, false);

            BankUtil.setCurrentDate(2011, Calendar.MARCH, 7);
            TransactionFactory.createTransfer(conn, 28900, IVAN_LENDME_ID, 43942, 17431, false);
            TransactionFactory.createWithdrawal(conn, 28900, PIT_WILSON_ID, 43942, false);

            BankUtil.setCurrentDate(2011, Calendar.MARCH, 8);
            TransactionFactory.createPayFriend(conn, 1000, PIT_WILSON_ID, 60413, 67521, false);
            TransactionFactory.createDeposit(conn, 5000000, OLIVE_STONER_ID, 93156, false);
            TransactionFactory.createWriteCheck(conn, 20000, DAVID_COPPERFILL_ID, 12121, false);
            TransactionFactory.createTransfer(conn, 100000, GEORGE_BRUSH_ID, 41725, 19023, false);

            BankUtil.setCurrentDate(2011, Calendar.MARCH, 9);
            TransactionFactory.createWire(conn, 400000, FATAL_CASTRO_ID, 41725, 32156, false);
            TransactionFactory.createPayFriend(conn, 1000, DAVID_COPPERFILL_ID, 53027, 60413, false);

            BankUtil.setCurrentDate(2011, Calendar.MARCH, 10);
            TransactionFactory.createPurchase(conn, 1500, ELIZABETH_SAILOR_ID, 60413, false);

            BankUtil.setCurrentDate(2011, Calendar.MARCH, 12);
            TransactionFactory.createWithdrawal(conn, 2000000, NAM_HOI_CHUNG_ID, 93156, false);
            TransactionFactory.createWriteCheck(conn, 45600, MAGIC_JORDAN_ID, 76543, false);
            TransactionFactory.createTopUp(conn, 5000, FATAL_CASTRO_ID, 67521, 19023, false);

            BankUtil.setCurrentDate(2011, Calendar.MARCH, 14);
            TransactionFactory.createPayFriend(conn, 2000, HURRYSON_FORD_ID, 67521, 53027, false);
            TransactionFactory.createCollect(conn, 1500, OLIVE_STONER_ID, 43947, 29107, false);

            conn.commit();
        } catch (Exception se) {
            se.printStackTrace();
            System.out.println("Rolling back...");
            JDBCConnectionManager.rollbackConn(conn);
        } finally {
            JDBCConnectionManager.closeConnection(
                    conn,
                    null // statement
            );
        }
    }
}