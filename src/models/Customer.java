package models;

import java.sql.*;

public class Customer {
   private String tax_id;
   private String name;
   private String address;
   private String pin;

   Customer(
      String tax_id,
      String name,
      String address,
      String pin
   ) {
      this.tax_id = tax_id;
      this.name = name;
      this.address = address;
      this.pin = pin;
   }

   public static void create(
      Connection conn,
      String tax_id,
      String name,
      String address
   ) throws SQLException {
      Statement stmt = null;
      System.out.println("Creating statement...");
      stmt = conn.createStatement();

      Pin.create(conn, "1717");

      String sql = String.format("INSERT INTO Customer %s VALUES ('%s', '%s', '%s', '%s')"
                  , "(tax_id, name, address, pin)"
                  , tax_id
                  , name
                  , address
                  , "1717" // default pin
      );
      System.out.println(sql);
      int n = stmt.executeUpdate(sql);
      System.out.println(n + " rows affected");
   }
}