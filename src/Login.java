// src/Login.java
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class Login extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    
    // Modern color scheme
    private final Color PRIMARY_COLOR = new Color(52, 152, 219);      // Modern Blue
    private final Color SECONDARY_COLOR = new Color(41, 128, 185);    // Darker Blue
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);      // Green
    private final Color WARNING_COLOR = new Color(243, 156, 18);      // Orange
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241);  // Light Gray
    private final Color WHITE = Color.WHITE;
    private final Color TEXT_COLOR = new Color(44, 62, 80);           // Dark Gray
    private final Color LIGHT_BLUE = new Color(174, 214, 241);        // Light Blue

    public Login() {
        initializeComponents();
        setupUI();
    }
    
    private void initializeComponents() {
        setTitle("LibraryFlow - Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        
        // Make it fullscreen
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Set modern look
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Use default look and feel
        }
    }
    
    private void setupUI() {
        // Main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(0, 0, PRIMARY_COLOR, 0, getHeight(), SECONDARY_COLOR);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());
        
        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        
        // Login Card Panel
        JPanel loginCard = createLoginCard();
        
        // Footer Panel
        JPanel footerPanel = createFooterPanel();
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(loginCard, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(30, 0, 20, 0));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        
        // Title
        JLabel titleLabel = new JLabel("📚 LibraryFlow");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Management System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(LIGHT_BLUE);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(subtitleLabel);
        
        return headerPanel;
    }
    
    private JPanel createLoginCard() {
        // Main card panel
        JPanel cardPanel = new JPanel();
        cardPanel.setOpaque(false);
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBorder(new EmptyBorder(0, 40, 0, 40));
        
        // Login card with rounded corners
        JPanel loginPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Rounded rectangle background
                g2d.setColor(WHITE);
                g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                
                // Subtle shadow
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
            }
        };
        
        loginPanel.setOpaque(false);
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        // Welcome text
        JLabel welcomeLabel = new JLabel("Welcome to LibraryFlow");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(TEXT_COLOR);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel descLabel = new JLabel("Please sign in to your account");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(new Color(127, 140, 141));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Username field
        JPanel usernamePanel = createInputPanel("👤 Username", "Enter your username");
        usernameField = (JTextField) usernamePanel.getComponent(2);  // Text field is now at index 2
        
        // Password field
        JPanel passwordPanel = createPasswordPanel("🔒 Password", "Enter your password");
        passwordField = (JPasswordField) passwordPanel.getComponent(2);  // Password field is now at index 2
        
        // Login button
        JButton loginBtn = createModernButton("GET STARTED", SUCCESS_COLOR, WHITE);
        loginBtn.addActionListener(e -> authenticateUser());
        
        // Signup link
        JPanel signupPanel = createSignupPanel();
        
        // Add components to login panel
        loginPanel.add(welcomeLabel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        loginPanel.add(descLabel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        loginPanel.add(usernamePanel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        loginPanel.add(passwordPanel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        loginPanel.add(loginBtn);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        loginPanel.add(signupPanel);
        
        cardPanel.add(loginPanel);
        return cardPanel;
    }
    
    private JPanel createInputPanel(String labelText, String placeholder) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_COLOR);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
            new EmptyBorder(12, 15, 12, 15)
        ));
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        textField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Add hover effect
        textField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                    new EmptyBorder(12, 15, 12, 15)
                ));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (!textField.hasFocus()) {
                    textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
                        new EmptyBorder(12, 15, 12, 15)
                    ));
                }
            }
        });
        
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(textField);
        
        return panel;
    }
    
    private JPanel createPasswordPanel(String labelText, String placeholder) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_COLOR);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
            new EmptyBorder(12, 15, 12, 15)
        ));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Add hover effect
        passwordField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                    new EmptyBorder(12, 15, 12, 15)
                ));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (!passwordField.hasFocus()) {
                    passwordField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
                        new EmptyBorder(12, 15, 12, 15)
                    ));
                }
            }
        });
        
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(passwordField);
        
        return panel;
    }
    
    private JButton createModernButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(textColor);
        button.setBackground(bgColor);
        button.setBorder(new EmptyBorder(15, 30, 15, 30));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private JPanel createSignupPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);
        
        JLabel textLabel = new JLabel("Don't have an account? ");
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textLabel.setForeground(new Color(127, 140, 141));
        
        JLabel signupLabel = new JLabel("Sign Up");
        signupLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        signupLabel.setForeground(PRIMARY_COLOR);
        signupLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel separatorLabel = new JLabel(" | ");
        separatorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        separatorLabel.setForeground(new Color(127, 140, 141));
        
        JLabel backLabel = new JLabel("← Back to Welcome");
        backLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backLabel.setForeground(WARNING_COLOR);
        backLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        signupLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new Signup().setVisible(true);
                dispose();
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                signupLabel.setForeground(SECONDARY_COLOR);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                signupLabel.setForeground(PRIMARY_COLOR);
            }
        });
        
        backLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new WelcomeScreen().setVisible(true);
                dispose();
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                backLabel.setForeground(WARNING_COLOR.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                backLabel.setForeground(WARNING_COLOR);
            }
        });
        
        panel.add(textLabel);
        panel.add(signupLabel);
        panel.add(separatorLabel);
        panel.add(backLabel);
        
        return panel;
    }
    
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel();
        footerPanel.setOpaque(false);
        footerPanel.setBorder(new EmptyBorder(10, 0, 20, 0));
        
        JLabel footerLabel = new JLabel("© 2025 LibraryFlow. All rights reserved.");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerLabel.setForeground(LIGHT_BLUE);
        footerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        footerPanel.add(footerLabel);
        return footerPanel;
    }

    private void authenticateUser() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            showModernMessage("Please fill all fields!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username=? AND password=?")) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                showModernMessage("Login successful! Welcome to LibraryFlow!", "Success", JOptionPane.INFORMATION_MESSAGE);
                new Dashboard().setVisible(true);
                dispose();
            } else {
                showModernMessage("Invalid credentials! Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            showModernMessage("Database connection error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showModernMessage(String message, String title, int messageType) {
        // Create custom styled message dialog
        UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("OptionPane.buttonFont", new Font("Segoe UI", Font.BOLD, 12));
        
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}
