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
      String address,
      String pin,
      boolean should_commit
   ) throws SQLException {
      Statement stmt = conn.createStatement();
      Pin.create(
              conn,
              pin,
              false // should_commit
      );

      String sql = String.format("INSERT INTO Customer %s VALUES ('%s', '%s', '%s', '%s')"
                  , "(tax_id, name, address, pin)"
                  , tax_id
                  , name
                  , address
                  , pin
      );
      int n = stmt.executeUpdate(sql);
      System.out.println(n + " rows affected");
      if (should_commit)
         conn.commit();
   }
}