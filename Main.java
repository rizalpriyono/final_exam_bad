package finalexam;
import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {
    private JPanel panelContainer;
    private CardLayout cardLayout;
    Product product = new Product();
    Transaction transaction = new Transaction();
    Customer customer = new Customer();

    public Main() {
        // Set up the frame
        setTitle("Menu Example");
        setSize(1000, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();

        JMenu menuProduct = new JMenu("Produk");
        JMenu menuTransaction = new JMenu("Transaksi");
        JMenu menuCustomer = new JMenu("Pelanggan");

        menuBar.add(menuProduct);
        menuBar.add(menuTransaction); 
        menuBar.add(menuCustomer); 

        JMenuItem menuItemViewProduct = new JMenuItem("Lihat Produk");
        JMenuItem menuItemAddProduct = new JMenuItem("Tambah Produk");
        JMenuItem menuItemViewTransaction = new JMenuItem("Lihat Transaksi");
        JMenuItem menuItemAddTransaction = new JMenuItem("Tambah Transaksi");
        JMenuItem menuItemViewCustomer = new JMenuItem("Lihat Pelanggan");
        JMenuItem menuItemAddCustomer = new JMenuItem("Tambah Pelanggan");

        menuProduct.add(menuItemViewProduct);
        menuProduct.add(menuItemAddProduct);
        menuTransaction.add(menuItemViewTransaction);
        menuTransaction.add(menuItemAddTransaction);
        menuCustomer.add(menuItemViewCustomer);
        menuCustomer.add(menuItemAddCustomer);

        setJMenuBar(menuBar);

        cardLayout = new CardLayout();
        panelContainer = new JPanel(cardLayout);

        JPanel panelProduct = product.createProductPanel();
        JPanel panelAddProduct = product.createAddProductPanel();
        JPanel panelTransaction = transaction.createTransactionPanel();
        JPanel panelAddTransaction = transaction.createAddTransactionPanel();
        JPanel panelCustomer = customer.createCustomerPanel();
        JPanel panelAddCustomer = customer.createAddCustomerPanel();

        // Add panels to the panel container
        panelContainer.add(panelProduct, "Produk");
        panelContainer.add(panelAddProduct, "Tambah Produk");
        panelContainer.add(panelTransaction, "Transaksi");
        panelContainer.add(panelAddTransaction, "Tambah Transaksi");
        panelContainer.add(panelCustomer, "Pelanggan");
        panelContainer.add(panelAddCustomer, "Tambah Pelanggan");

        add(panelContainer);

        menuItemViewProduct.addActionListener(e -> {
            product.loadProductData(); 
            cardLayout.show(panelContainer, "Produk");
        });
        menuItemAddProduct.addActionListener(e -> cardLayout.show(panelContainer, "Tambah Produk"));

        menuItemViewTransaction.addActionListener(e -> {
            transaction.loadTransactionData();
            cardLayout.show(panelContainer, "Transaksi");
        });
        menuItemAddTransaction.addActionListener(e -> cardLayout.show(panelContainer, "Tambah Transaksi"));

        menuItemViewCustomer.addActionListener(e -> {
            customer.loadData();
            cardLayout.show(panelContainer, "Pelanggan");
        });
        menuItemAddCustomer.addActionListener(e -> cardLayout.show(panelContainer, "Tambah Pelanggan"));
    }

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}
