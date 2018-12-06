package gui;

import bank_util.*;
import models.*;
import models.account.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class Bank extends JFrame {
	private Connection conn;
	final static String ATM_PANEL = "ATM App Interface";
	final static String BANK_TELLER_PANEL = "Bank Teller Interface";
	public Bank() {
		Connection conn = JDBCConnectionManager.getConnection();
		if (conn == null) {
			throw new RuntimeException("Could not connect to database");
		}
		this.conn = conn;
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				JDBCConnectionManager.closeConnection(
						conn,
						null // statement
				);
			}
		});

		Container cp = getContentPane();
		cp.add(createInterfaceTabs(), BorderLayout.CENTER);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Debtsrus Bank");
		setSize(500, 500);
		setVisible(true);
	}

	private JTabbedPane createInterfaceTabs() {
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab(ATM_PANEL, new ATMAppInterface(this.conn));
		tabbedPane.addTab(BANK_TELLER_PANEL, new BankTellerInterface(this.conn));

		return tabbedPane;
	}

	public static void main(String[] args) {
		new Bank();
	}
}