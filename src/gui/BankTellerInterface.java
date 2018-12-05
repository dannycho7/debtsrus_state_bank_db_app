package gui;

import bank_util.*;
import models.*;
import models.account.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class BankTellerInterface extends JPanel{
    public Connection conn;

    public BankTellerInterface(Connection conn) {
        this.conn = conn;
        this.add(new JLabel("Bank Teller Interface"));
    }
}