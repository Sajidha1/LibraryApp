import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;

public class LibraryGUI extends JFrame {
    private JTextField tfId, tfTitle, tfAuthor, tfYear, tfQuantity;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh, btnClear;
    private JTable table;
    private DefaultTableModel tableModel;
    private BookDAO dao = new BookDAO();

    public LibraryGUI() {
        setTitle("Library Management");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        loadTable();
    }

    private void initComponents() {
        tfId = new JTextField(5); tfId.setEditable(false);
        tfTitle = new JTextField(20);
        tfAuthor = new JTextField(20);
        tfYear = new JTextField(5);
        tfQuantity = new JTextField(5);

        btnAdd = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnRefresh = new JButton("Refresh");
        btnClear = new JButton("Clear");

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; inputPanel.add(tfId, gbc);
        gbc.gridx = 0; gbc.gridy = 1; inputPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1; inputPanel.add(tfTitle, gbc);
        gbc.gridx = 0; gbc.gridy = 2; inputPanel.add(new JLabel("Author:"), gbc);
        gbc.gridx = 1; inputPanel.add(tfAuthor, gbc);
        gbc.gridx = 0; gbc.gridy = 3; inputPanel.add(new JLabel("Year:"), gbc);
        gbc.gridx = 1; inputPanel.add(tfYear, gbc);
        gbc.gridx = 0; gbc.gridy = 4; inputPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1; inputPanel.add(tfQuantity, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnAdd); buttonPanel.add(btnUpdate); buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh); buttonPanel.add(btnClear);

        String[] cols = {"ID","Title","Author","Year","Quantity"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(table);

        add(inputPanel, BorderLayout.WEST);
        add(scroll, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Events
        btnAdd.addActionListener(e -> addBook());
        btnUpdate.addActionListener(e -> updateBook());
        btnDelete.addActionListener(e -> deleteBook());
        btnRefresh.addActionListener(e -> loadTable());
        btnClear.addActionListener(e -> clearForm());

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int r = table.getSelectedRow();
                if (r >= 0) {
                    tfId.setText(tableModel.getValueAt(r, 0).toString());
                    tfTitle.setText(tableModel.getValueAt(r, 1).toString());
                    tfAuthor.setText(tableModel.getValueAt(r, 2).toString());
                    tfYear.setText(tableModel.getValueAt(r, 3).toString());
                    tfQuantity.setText(tableModel.getValueAt(r, 4).toString());
                }
            }
        });
    }

    private void addBook() {
        try {
            String title = tfTitle.getText().trim();
            String author = tfAuthor.getText().trim();
            int year = Integer.parseInt(tfYear.getText().trim());
            int qty = Integer.parseInt(tfQuantity.getText().trim());
            if (title.isEmpty() || author.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Title and Author required");
                return;
            }
            dao.addBook(new Book(title, author, year, qty));
            loadTable();
            clearForm();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Year and Quantity must be numbers");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void updateBook() {
        try {
            int id = Integer.parseInt(tfId.getText().trim());
            String title = tfTitle.getText().trim();
            String author = tfAuthor.getText().trim();
            int year = Integer.parseInt(tfYear.getText().trim());
            int qty = Integer.parseInt(tfQuantity.getText().trim());
            Book b = new Book(id, title, author, year, qty);
            dao.updateBook(b);
            loadTable();
            clearForm();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Select a book and ensure numbers are valid");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void deleteBook() {
        try {
            int id = Integer.parseInt(tfId.getText().trim());
            int confirm = JOptionPane.showConfirmDialog(this, "Delete this book?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dao.deleteBook(id);
                loadTable();
                clearForm();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Select a book to delete");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        try {
            List<Book> list = dao.getAllBooks();
            for (Book b : list) {
                tableModel.addRow(new Object[] {
                    b.getId(), b.getTitle(), b.getAuthor(), b.getYear(), b.getQuantity()
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void clearForm() {
        tfId.setText("");
        tfTitle.setText("");
        tfAuthor.setText("");
        tfYear.setText("");
        tfQuantity.setText("");
    }
}
