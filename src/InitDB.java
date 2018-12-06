import bank_util.*;
import models.*;
import models.account.*;

import java.sql.*;

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
            String GEORGE_BUSH_ID = "201674933";
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

            CheckingAccount.createForStudent(conn, 17431, 20000, "San Francisco", JOE_PEPSI_ID, IVAN_LENDME_ID, false);
            CheckingAccount.createForStudent(conn, 54321, 2100000, "Los Angeles", HURRYSON_FORD_ID, HURRYSON_FORD_ID, false);
            CheckingAccount.createForStudent(conn, 12121, 120000, "Goleta", DAVID_COPPERFILL_ID, DAVID_COPPERFILL_ID, false);

            CheckingAccount.createForInterest(conn, 41725, 1500000, "Los Angeles", GEORGE_BUSH_ID, GEORGE_BUSH_ID, false);
            CheckingAccount.createForInterest(conn, 76543, 845600, "Santa Barbara", LI_KUNG_ID, MAGIC_JORDAN_ID, false);
            CheckingAccount.createForInterest(conn, 93156, 200000000, "Goleta", KELVIN_COSTNER_ID, ELIZABETH_SAILOR_ID, false);

            SavingsAccount.create(conn, 43942, 128900, "Santa Barbara", ALFRED_HITCHCOCK_ID, HURRYSON_FORD_ID, false);
            SavingsAccount.create(conn, 29107, 3400000, "Los Angeles", KELVIN_COSTNER_ID, KELVIN_COSTNER_ID, false);
            SavingsAccount.create(conn, 19023, 230000, "San Fransisco", CINDY_LAUGHER_ID, CINDY_LAUGHER_ID, false);
            SavingsAccount.create(conn, 32156, 100000, "Goleta", MAGIC_JORDAN_ID, JOE_PEPSI_ID, false);

            PocketAccount.create(conn, 53027, 5000, "Goleta", DAVID_COPPERFILL_ID, DAVID_COPPERFILL_ID, 12121, false);
            PocketAccount.create(conn, 43947, 3000, "Isla Vista", LI_KUNG_ID, LI_KUNG_ID, 29107, false);
            PocketAccount.create(conn, 60413, 2000, "Santa Cruz", ALFRED_HITCHCOCK_ID, PIT_WILSON_ID, 43942, false);
            PocketAccount.create(conn, 67521, 10000, "Santa Barbara", KELVIN_COSTNER_ID, FATAL_CASTRO_ID, 19023, false);

            System.out.println("Rolling back..");
            conn.rollback();
        } catch (SQLException se) {
            se.printStackTrace();
            JDBCConnectionManager.rollbackConn(conn);
        } finally {
            JDBCConnectionManager.closeConnection(
                    conn,
                    null // statement
            );
        }
    }
}