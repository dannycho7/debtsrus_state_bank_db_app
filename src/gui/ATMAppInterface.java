package gui;

import bank_util.*;
import models.*;
import models.account.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class ATMAppInterface extends JPanel{
    private Connection conn;

    public ATMAppInterface(Connection conn) {
        super();
        this.conn = conn;
        this.add(new JLabel("ATM App Interface"));
    }
}