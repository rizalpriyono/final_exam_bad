package finalexam;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class Product {
  private JTable productTable;
  private DefaultTableModel tableModel;
  private static final String DB_URL = "jdbc:mysql://localhost:3306/final_exam_bad";
  private static final String DB_USER = "root";
  private static final String DB_PASSWORD = ""; 
    
  public JPanel createProductPanel() {
    JPanel panel = new JPanel(new BorderLayout());

    productTable = new JTable();
    tableModel = new DefaultTableModel(new Object[] { "Product ID", "Nama", "Harga", "Stock" }, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
          return false; // Tidak mengizinkan pengeditan sel di tabel
      }
    };

    productTable.setModel(tableModel);

    JScrollPane scrollPane = new JScrollPane(productTable);
    panel.add(scrollPane, BorderLayout.CENTER);

    loadProductData();

    return panel;
  }
  
  public void loadProductData() {
    tableModel.setRowCount(0);

    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
          Statement stmt = conn.createStatement();
          ResultSet rs = stmt.executeQuery("SELECT * FROM product")) {

        while (rs.next()) {
            int productId = rs.getInt("productId");
            String nama = rs.getString("nama");
            double harga = rs.getDouble("harga");
            int stock = rs.getInt("stock");
            tableModel.addRow(new Object[]{productId, nama, harga, stock});
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
  }
  
  
  public JPanel createAddProductPanel() {
    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);

    // Form components
    JLabel labelName = new JLabel("Nama:");
    JLabel labelPrice = new JLabel("Harga:");
    JLabel labelStock = new JLabel("Stock:");

    JTextField textName = new JTextField(20);
    JTextField textPrice = new JTextField(20);
    JTextField textStock = new JTextField(20);

    JButton buttonSubmit = new JButton("Tambah Produk");

    // Layout
    gbc.gridx = 0;
    gbc.gridy = 0;
    panel.add(labelName, gbc);

    gbc.gridx = 1;
    gbc.gridy = 0;
    panel.add(textName, gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    panel.add(labelPrice, gbc);

    gbc.gridx = 1;
    gbc.gridy = 1;
    panel.add(textPrice, gbc);

    gbc.gridx = 0;
    gbc.gridy = 2;
    panel.add(labelStock, gbc);

    gbc.gridx = 1;
    gbc.gridy = 2;
    panel.add(textStock, gbc);

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
        String priceText = textPrice.getText();
        String stockText = textStock.getText();

        if (name.isEmpty() || priceText.isEmpty() || stockText.isEmpty()) {
          JOptionPane.showMessageDialog(panel, "Semua field harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
          return;
        }

        double price;
        int stock;
        try {
          price = Double.parseDouble(priceText);
          stock = Integer.parseInt(stockText);
        } catch (NumberFormatException ex) {
          JOptionPane.showMessageDialog(panel, "Harga dan Stock harus berupa angka!", "Error",
              JOptionPane.ERROR_MESSAGE);
          return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement pstmt = conn
                .prepareStatement("INSERT INTO product (nama, harga, stock) VALUES (?, ?, ?)")) {
          pstmt.setString(1, name);
          pstmt.setDouble(2, price);
          pstmt.setInt(3, stock);

          int rowsAffected = pstmt.executeUpdate();

          if (rowsAffected > 0) {

            JOptionPane.showMessageDialog(panel, "Produk berhasil ditambahkan!");
            textName.setText("");
            textPrice.setText("");
            textStock.setText("");
          } else {
            JOptionPane.showMessageDialog(panel, "Gagal menambahkan produk!", "Error", JOptionPane.ERROR_MESSAGE);
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
