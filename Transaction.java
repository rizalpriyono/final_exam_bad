package finalexam;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Transaction {
  private DefaultTableModel transactionTableModel;
  private JComboBox<String> productComboBox;
  private JComboBox<String> cusomerComboBox;
  private JTextField qtyField;
  private JTextField noteField;
  private static final String DB_URL = "jdbc:mysql://localhost:3306/final_exam_bad";
  private static final String DB_USER = "root";
  private static final String DB_PASSWORD = "";

  JPanel panelAdd = new JPanel(new GridLayout(0, 2));


  public JPanel createTransactionPanel() {
    JPanel panel = new JPanel(new BorderLayout());

    JTable transactionTable = new JTable();
    transactionTableModel = new DefaultTableModel(
        new Object[] { "ID Transaksi", "Nama Produk", "ID Produk", "Jumlah", "Total", "Tanggal" }, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false; // Tidak mengizinkan pengeditan sel di tabel
      }
    };
    transactionTable.setModel(transactionTableModel);

    transactionTable.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        int row = transactionTable.rowAtPoint(evt.getPoint());
        if (row >= 0) {
          int transactionId = (int) transactionTableModel.getValueAt(row, 0);
          showTransactionDetail(transactionId);
        }
      }
    });

    JScrollPane scrollPane = new JScrollPane(transactionTable);
    panel.add(scrollPane, BorderLayout.CENTER);

    loadTransactionData();

    return panel;
  }

  private void showTransactionDetail(int transactionId) {
    JDialog detailDialog = new JDialog();
    detailDialog.setSize(400, 300);

    JPanel panel = new JPanel(new GridLayout(0, 2));

    // Add labels and values
    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        PreparedStatement pstmt = conn.prepareStatement(
            "SELECT t.*, p.nama as productName, c.nama as namaPelanggan FROM transaction t JOIN product p ON t.productId = p.productId JOIN customer c ON t.customerId = c.customerId WHERE t.transactionId = ?")) {

      pstmt.setInt(1, transactionId);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        panel.add(new JLabel("ID Transaksi:"));
        panel.add(new JLabel(String.valueOf(rs.getInt("transactionId"))));

        panel.add(new JLabel("Nama Produk:"));
        panel.add(new JLabel(rs.getString("productName")));

        panel.add(new JLabel("ID Produk:"));
        panel.add(new JLabel(String.valueOf(rs.getInt("productId"))));

        panel.add(new JLabel("Jumlah:"));
        panel.add(new JLabel(String.valueOf(rs.getInt("qty"))));

        panel.add(new JLabel("Total:"));
        panel.add(new JLabel(String.valueOf(rs.getDouble("total"))));

        panel.add(new JLabel("Tanggal:"));
        panel.add(new JLabel(rs.getTimestamp("date").toString()));

        panel.add(new JLabel("Nama Pelanggan:"));
        panel.add(new JLabel(rs.getString("namaPelanggan")));

        panel.add(new JLabel("Catatan:"));
        panel.add(new JLabel(rs.getString("note")));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    detailDialog.add(panel);
    detailDialog.setVisible(true);
  }

  public void loadTransactionData() {
    transactionTableModel.setRowCount(0);
    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt
            .executeQuery("SELECT t.*, p.nama FROM transaction t JOIN product p ON t.productId = p.productId")) {
      while (rs.next()) {
        int transactionId = rs.getInt("transactionId");
        int productId = rs.getInt("productId");
        int qty = rs.getInt("qty");
        double total = rs.getDouble("total");
        Timestamp date = rs.getTimestamp("date");
        String productName = rs.getString("nama");
        transactionTableModel.addRow(new Object[] { transactionId, productName, productId, qty, total, date });
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public JPanel createAddTransactionPanel() {
    productComboBox = new JComboBox<>();
    cusomerComboBox = new JComboBox<>();
    try (
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT productId, nama FROM product")) {
      while (rs.next()) {
        int productId = rs.getInt("productId");
        String productName = rs.getString("nama");
        productComboBox.addItem(productId + " - " + productName);
      }
          
    } catch (SQLException e) {
      e.printStackTrace();
    }

    try (
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT customerId, nama FROM customer")) {
      while (rs.next()) {
        int id = rs.getInt("customerId");
        String name = rs.getString("nama");
        cusomerComboBox.addItem(id + " - " + name);
      }
          
    } catch (SQLException e) {
      e.printStackTrace();
    }

    panelAdd.add(new JLabel("Produk:"));
    panelAdd.add(productComboBox);

    qtyField = new JTextField();
    panelAdd.add(new JLabel("Jumlah:"));
    panelAdd.add(qtyField);

    panelAdd.add(new JLabel("Pelanggan:"));
    panelAdd.add(cusomerComboBox);

    noteField = new JTextField();
    panelAdd.add(new JLabel("Catatan:"));
    panelAdd.add(noteField);

    JButton saveButton = new JButton("Tambah Transaksi");
    saveButton.addActionListener(e -> addTransaction());
    panelAdd.add(saveButton);

    return panelAdd;
  }

  private void addTransaction() {
    String selectedProduct = (String) productComboBox.getSelectedItem();
    int productId = Integer.parseInt(selectedProduct.split(" - ")[0]);
    int qty = Integer.parseInt(qtyField.getText());
    String selectedCustomer = (String) cusomerComboBox.getSelectedItem();
    int customerId = Integer.parseInt(selectedCustomer.split(" - ")[0]);
    String note = noteField.getText();

    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {

      double total = 0.0;
      try (PreparedStatement productStmt = conn
          .prepareStatement("SELECT harga, stock FROM product WHERE productId = ?")) {
        productStmt.setInt(1, productId);
        ResultSet rs = productStmt.executeQuery();
        if (rs.next()) {
          double harga = rs.getDouble("harga");
          int stock = rs.getInt("stock");

          if (stock < qty) {
            JOptionPane.showMessageDialog(panelAdd, "Stok tidak mencukupi.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
          }

          total = harga * qty;
        }
      }

      conn.setAutoCommit(false);

      try (PreparedStatement pstmt = conn.prepareStatement(
          "INSERT INTO transaction (productId, qty, total, customerId, note, date) VALUES (?, ?, ?, ?, ?, ?)")) {
        pstmt.setInt(1, productId);
        pstmt.setInt(2, qty);
        pstmt.setDouble(3, total);
        pstmt.setInt(4, customerId);
        pstmt.setString(5, note);
        pstmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
        pstmt.executeUpdate();
      }

      try (PreparedStatement updateStockStmt = conn
          .prepareStatement("UPDATE product SET stock = stock - ? WHERE productId = ?")) {
        updateStockStmt.setInt(1, qty);
        updateStockStmt.setInt(2, productId);
        updateStockStmt.executeUpdate();
      }

      conn.commit();
      JOptionPane.showMessageDialog(panelAdd, "Transaksi berhasil ditambahkan.");
    } catch (SQLException e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(panelAdd, "Terjadi kesalahan saat menambahkan transaksi.", "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }
}


