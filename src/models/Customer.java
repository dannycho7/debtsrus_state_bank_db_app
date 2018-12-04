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


   public static Customer find(
           Connection conn,
           String customer_tax_id
   ) throws SQLException, IllegalArgumentException {
      String get_customer_sql = String.format("SELECT %s FROM Customer C WHERE C.tax_id = %s"
              , "C.tax_id", "C.name", "C.address", "C.pin"
              , customer_tax_id
      );

      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(get_customer_sql);
      while (rs.next()) {
         String tax_id = rs.getString("tax_id");
         String name = rs.getString("name");
         String address = rs.getString("address");
         String pin = rs.getString("pin");

         return new Customer(
                 tax_id,
                 name,
                 address,
                 pin
         );
      }

      throw new IllegalArgumentException(String.format("Could not find Customer %s", customer_tax_id));
   }

   public String getTaxId() {
      return tax_id;
   }
   public String getName() {
      return name;
   }
   public String getAddress() {
      return address;
   }
   public String getPin() {
      return pin;
   }
}