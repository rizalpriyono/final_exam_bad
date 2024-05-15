package finalexam;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Customer {
  private JTable productTable;
  private DefaultTableModel tableModel;
  private static final String DB_URL = "jdbc:mysql://localhost:3306/final_exam_bad";
  private static final String DB_USER = "root";
  private static final String DB_PASSWORD = ""; 
    
  public JPanel createCustomerPanel() {
    JPanel panel = new JPanel(new BorderLayout());

    productTable = new JTable();
    tableModel = new DefaultTableModel(new Object[] { "Customer ID", "Nama", "Alamat", "No HP" }, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
          return false;
      }
    };

    productTable.setModel(tableModel);

    JScrollPane scrollPane = new JScrollPane(productTable);
    panel.add(scrollPane, BorderLayout.CENTER);

    loadData();

    return panel;
  }
  
  public void loadData() {
    tableModel.setRowCount(0);

    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM customer")) {

      while (rs.next()) {
        int customerId = rs.getInt("customerId");
        String nama = rs.getString("nama");
        String alamat = rs.getString("alamat");
        String noTelp = rs.getString("noTelp");

        tableModel.addRow(new Object[] { customerId, nama, alamat, noTelp });
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public JPanel createAddCustomerPanel() {
    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);

    // Form components
    JLabel labelName = new JLabel("Nama:");
    JLabel labelAddress = new JLabel("Alamat:");
    JLabel labelPhoneNumber = new JLabel("No HP:");

    JTextField textName = new JTextField(20);
    JTextField textAddress = new JTextField(20);
    JTextField textPhoneNumber = new JTextField(20);

    JButton buttonSubmit = new JButton("Tambah Pelanggan");

    // Layout
    gbc.gridx = 0;
    gbc.gridy = 0;
    panel.add(labelName, gbc);

    gbc.gridx = 1;
    gbc.gridy = 0;
    panel.add(textName, gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    panel.add(labelAddress, gbc);

    gbc.gridx = 1;
    gbc.gridy = 1;
    panel.add(textAddress, gbc);

    gbc.gridx = 0;
    gbc.gridy = 2;
    panel.add(labelPhoneNumber, gbc);

    gbc.gridx = 1;
    gbc.gridy = 2;
    panel.add(textPhoneNumber, gbc);

    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.CENTER;
    panel.add(buttonSubmit, gbc);

    // Add action listener to button
    buttonSubmit.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String name = textName.getText();
        String addressText = textAddress.getText();
        String phoneNumberText = textPhoneNumber.getText();

        if (name.isEmpty() || addressText.isEmpty() || phoneNumberText.isEmpty()) {
          JOptionPane.showMessageDialog(panel, "Semua field harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
          return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement pstmt = conn
                .prepareStatement("INSERT INTO customer (nama, alamat, noTelp) VALUES (?, ?, ?)")) {
          pstmt.setString(1, name);
          pstmt.setString(2, addressText);
          pstmt.setString(3, phoneNumberText);

          int rowsAffected = pstmt.executeUpdate();

          if (rowsAffected > 0) {

            JOptionPane.showMessageDialog(panel, "Pelanggan berhasil ditambahkan!");
            textName.setText("");
            textAddress.setText("");
            textPhoneNumber.setText("");
          } else {
            JOptionPane.showMessageDialog(panel, "Gagal menambahkan pelanggan!", "Error", JOptionPane.ERROR_MESSAGE);
          }
        } catch (SQLException ex) {
          ex.printStackTrace();
          JOptionPane.showMessageDialog(panel, "Gagal terhubung ke database!", "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    });
    
    return panel;
    }
    
  
}
