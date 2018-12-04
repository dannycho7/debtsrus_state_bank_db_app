package models;

import java.sql.*;

public class Pin {
   private String pin_no;
   private String customer_tax_id;

   Pin(
      String pin_no,
      String customer_tax_id
   ) {
      this.pin_no = pin_no;
      this.customer_tax_id = customer_tax_id;
   }

   public static void create(
      Connection conn,
      String pin_no,
      boolean should_commit
   ) throws SQLException {
      Statement stmt = conn.createStatement();
      String sql = String.format("INSERT INTO Pin %s VALUES ('%s')"
                  , "(pin_no)"
                  , pin_no
      );
      int n = stmt.executeUpdate(sql);
      System.out.println(n + " rows affected.");
      if (should_commit)
         conn.commit();
   }

   public String getPinNo() {
      return pin_no;
   }
   public String getCustomerTaxId() {
      return customer_tax_id;
   }
}