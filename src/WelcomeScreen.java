import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class WelcomeScreen extends JFrame {
    
    // Modern color scheme
    private final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private final Color SECONDARY_COLOR = new Color(41, 128, 185);
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private final Color WHITE = Color.WHITE;
    private final Color TEXT_COLOR = new Color(44, 62, 80);
    private final Color LIGHT_BLUE = new Color(174, 214, 241);

    public WelcomeScreen() {
        initializeComponents();
        setupUI();
    }
    
    private void initializeComponents() {
        setTitle("LibraryFlow - Welcome");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Make it fullscreen
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(true);
        
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
        
        // Center Panel with options
        JPanel centerPanel = createCenterPanel();
        
        // Footer Panel
        JPanel footerPanel = createFooterPanel();
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(80, 0, 40, 0));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        
        // Main Title
        JLabel titleLabel = new JLabel("📚 LibraryFlow");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 72));
        titleLabel.setForeground(WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Modern Library Management System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        subtitleLabel.setForeground(LIGHT_BLUE);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Description
        JLabel descLabel = new JLabel("Manage books, members, and library operations with ease");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        descLabel.setForeground(LIGHT_BLUE);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        headerPanel.add(subtitleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        headerPanel.add(descLabel);
        
        return headerPanel;
    }
    
    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(new EmptyBorder(50, 0, 50, 0));
        
        // Welcome message
        JLabel welcomeLabel = new JLabel("Choose an option to get started");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(WHITE);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        centerPanel.add(welcomeLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 50)));
        
        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 20));
        buttonsPanel.setOpaque(false);
        
        // Login Button
        JButton loginBtn = createWelcomeButton("🔐 SIGN IN", "Access your existing account", PRIMARY_COLOR);
        loginBtn.addActionListener(e -> {
            new Login().setVisible(true);
            dispose();
        });
        
        // Signup Button
        JButton signupBtn = createWelcomeButton("👤 SIGN UP", "Create a new account", SUCCESS_COLOR);
        signupBtn.addActionListener(e -> {
            new Signup().setVisible(true);
            dispose();
        });
        
        buttonsPanel.add(loginBtn);
        buttonsPanel.add(signupBtn);
        
        centerPanel.add(buttonsPanel);
        
        return centerPanel;
    }
    
    private JButton createWelcomeButton(String title, String description, Color bgColor) {
        JPanel buttonPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Rounded rectangle background
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 25, 25);
            }
        };
        
        buttonPanel.setBackground(bgColor);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        buttonPanel.setPreferredSize(new Dimension(280, 150));
        buttonPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(WHITE);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        buttonPanel.add(titleLabel);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(descLabel);
        
        // Create a wrapper button to handle clicks
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.add(buttonPanel, BorderLayout.CENTER);
        button.setBorder(null);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effects
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                buttonPanel.setBackground(bgColor.darker());
                buttonPanel.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                buttonPanel.setBackground(bgColor);
                buttonPanel.repaint();
            }
        });
        
        return button;
    }
    
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel();
        footerPanel.setOpaque(false);
        footerPanel.setBorder(new EmptyBorder(20, 0, 40, 0));
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        
        JLabel footerLabel = new JLabel("© 2025 LibraryFlow. All rights reserved.");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        footerLabel.setForeground(LIGHT_BLUE);
        footerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel versionLabel = new JLabel("Version 1.0 - Modern Library Management Solution");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        versionLabel.setForeground(LIGHT_BLUE);
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        footerPanel.add(footerLabel);
        footerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        footerPanel.add(versionLabel);
        
        return footerPanel;
    }
}