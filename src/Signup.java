
// src/Signup.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

// src/Signup.java
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class Signup extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    
    // Modern color scheme matching Login
    private final Color PRIMARY_COLOR = new Color(52, 152, 219);      
    private final Color SECONDARY_COLOR = new Color(41, 128, 185);    
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);      
    private final Color ERROR_COLOR = new Color(231, 76, 60);         
    private final Color WARNING_COLOR = new Color(243, 156, 18);      // Orange
    private final Color WHITE = Color.WHITE;
    private final Color TEXT_COLOR = new Color(44, 62, 80);           
    private final Color LIGHT_BLUE = new Color(174, 214, 241);        

    public Signup() {
        initializeComponents();
        setupUI();
    }
    
    private void initializeComponents() {
        setTitle("LibraryFlow - Create Account");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        
        // Make it fullscreen
        setExtendedState(JFrame.MAXIMIZED_BOTH);
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
        
        // Signup Card Panel
        JPanel signupCard = createSignupCard();
        
        // Footer Panel
        JPanel footerPanel = createFooterPanel();
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(signupCard, BorderLayout.CENTER);
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
        JLabel subtitleLabel = new JLabel("Create New Account");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(LIGHT_BLUE);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(subtitleLabel);
        
        return headerPanel;
    }
    
    private JPanel createSignupCard() {
        // Main card panel
        JPanel cardPanel = new JPanel();
        cardPanel.setOpaque(false);
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBorder(new EmptyBorder(0, 40, 0, 40));
        
        // Signup card with rounded corners
        JPanel signupPanel = new JPanel() {
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
        
        signupPanel.setOpaque(false);
        signupPanel.setLayout(new BoxLayout(signupPanel, BoxLayout.Y_AXIS));
        signupPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        // Welcome text
        JLabel welcomeLabel = new JLabel("Join LibraryFlow");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(TEXT_COLOR);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel descLabel = new JLabel("Create your account to get started");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(new Color(127, 140, 141));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Input fields
        JPanel usernamePanel = createInputPanel("👤 Username", "Choose a username");
        usernameField = (JTextField) usernamePanel.getComponent(2);
        
        JPanel passwordPanel = createPasswordPanel("🔒 Password", "Create a password");
        passwordField = (JPasswordField) passwordPanel.getComponent(2);
        
        JPanel confirmPasswordPanel = createPasswordPanel("🔒 Confirm Password", "Confirm your password");
        confirmPasswordField = (JPasswordField) confirmPasswordPanel.getComponent(2);
        
        // Signup button
        JButton signupBtn = createModernButton("CREATE ACCOUNT", SUCCESS_COLOR, WHITE);
        signupBtn.addActionListener(e -> signupUser());
        
        // Login link
        JPanel loginPanel = createLoginPanel();
        
        // Add components
        signupPanel.add(welcomeLabel);
        signupPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        signupPanel.add(descLabel);
        signupPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        signupPanel.add(usernamePanel);
        signupPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        signupPanel.add(passwordPanel);
        signupPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        signupPanel.add(confirmPasswordPanel);
        signupPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        signupPanel.add(signupBtn);
        signupPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        signupPanel.add(loginPanel);
        
        cardPanel.add(signupPanel);
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
    
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);
        
        JLabel textLabel = new JLabel("Already have an account? ");
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textLabel.setForeground(new Color(127, 140, 141));
        
        JLabel loginLabel = new JLabel("Sign In");
        loginLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginLabel.setForeground(PRIMARY_COLOR);
        loginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel separatorLabel = new JLabel(" | ");
        separatorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        separatorLabel.setForeground(new Color(127, 140, 141));
        
        JLabel backLabel = new JLabel("← Back to Welcome");
        backLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backLabel.setForeground(WARNING_COLOR);
        backLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        loginLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new Login().setVisible(true);
                dispose();
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                loginLabel.setForeground(SECONDARY_COLOR);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                loginLabel.setForeground(PRIMARY_COLOR);
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
        panel.add(loginLabel);
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

    private void signupUser() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showModernMessage("Please fill all fields!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            showModernMessage("Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.length() < 6) {
            showModernMessage("Password must be at least 6 characters long!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)")) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            
            showModernMessage("Account created successfully! You can now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
            new Login().setVisible(true);
            dispose();

        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint")) {
                showModernMessage("Username already exists! Please choose a different username.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                showModernMessage("Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showModernMessage(String message, String title, int messageType) {
        // Create custom styled message dialog
        UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("OptionPane.buttonFont", new Font("Segoe UI", Font.BOLD, 12));
        
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}
