import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;

public class Dashboard extends JFrame {
    // Fine calculation configuration
    // Business Logic: fine = days_overdue × FINE_PER_DAY
    // Automatic overdue detection based on due_date vs current_date
    private final double FINE_PER_DAY = 10.0; // Rs 10 per day overdue
    
    // Modern color scheme
    private final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private final Color SECONDARY_COLOR = new Color(41, 128, 185);
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private final Color WARNING_COLOR = new Color(243, 156, 18);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color INFO_COLOR = new Color(52, 73, 94);
    private final Color WHITE = Color.WHITE;
    private final Color LIGHT_GRAY = new Color(248, 249, 250);
    private final Color DARK_GRAY = new Color(44, 62, 80);
    private final Color SIDEBAR_COLOR = new Color(33, 37, 43);
    
    // Current active panel
    private String currentPanel = "dashboard";
    
    // Sidebar components
    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private JPanel mainContentArea;
    
    // Dashboard tab components
    private JTextField tfBookId, tfTitle, tfAuthor, tfYear, tfQuantity;
    private JTable booksTable;
    private DefaultTableModel booksTableModel;
    private BookDAO bookDAO = new BookDAO();
    
    // Member Management components  
    private JTextField tfMemberId, tfMemberName, tfEmail, tfPhone, tfAddress;
    private JComboBox<String> cbMemberType, cbMemberStatus;
    private JTable membersTable;
    private DefaultTableModel membersTableModel;
    private MemberDAO memberDAO = new MemberDAO();
    
    // Issue Management components
    private JComboBox<Book> cbBooks;
    private JComboBox<Member> cbMembers;
    private JSpinner dueDateSpinner;
    private JTable issuesTable;
    private DefaultTableModel issuesTableModel;
    private IssueDAO issueDAO = new IssueDAO();

    public Dashboard() {
        setTitle("📚 LibraryFlow - Management Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Set modern look and feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            customizeUIComponents();
        } catch (Exception e) {
            // Use default look and feel
        }
        
        initComponents();
        loadAllData();
    }
    
    private void customizeUIComponents() {
        // Button styling
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 12));
        UIManager.put("Button.foreground", WHITE);
        
        // Table styling
        UIManager.put("Table.gridColor", new Color(220, 220, 220));
        UIManager.put("Table.selectionBackground", PRIMARY_COLOR);
        UIManager.put("Table.selectionForeground", WHITE);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Create main container
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(LIGHT_GRAY);
        
        // Create header
        JPanel headerPanel = createModernHeader();
        
        // Create sidebar
        sidebarPanel = createSidebar();
        
        // Create content area
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(LIGHT_GRAY);
        
        // Create main content area
        mainContentArea = new JPanel(new BorderLayout());
        mainContentArea.setBackground(LIGHT_GRAY);
        mainContentArea.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        contentPanel.add(mainContentArea, BorderLayout.CENTER);
        
        // Add components to main container
        mainContainer.add(headerPanel, BorderLayout.NORTH);
        mainContainer.add(sidebarPanel, BorderLayout.WEST);
        mainContainer.add(contentPanel, BorderLayout.CENTER);
        
        add(mainContainer);
        
        // Show dashboard by default
        showDashboard();
    }
    
    private JPanel createModernHeader() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(0, 80));
        headerPanel.setLayout(new BorderLayout());
        
        // Title section
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("📚 LibraryFlow Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 20));
        
        titlePanel.add(titleLabel);
        
        // Action section
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setOpaque(false);
        
        JButton refreshBtn = createHeaderButton("🔄 Refresh", SUCCESS_COLOR);
        refreshBtn.addActionListener(e -> {
            loadAllData();
            showModernNotification("Data refreshed successfully!", "Success");
        });
        
        JButton logoutBtn = createHeaderButton("🚪 Logout", DANGER_COLOR);
        logoutBtn.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to logout?", 
                "Confirm Logout", 
                JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                new WelcomeScreen().setVisible(true);
                dispose();
            }
        });
        
        actionPanel.add(refreshBtn);
        actionPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        actionPanel.add(logoutBtn);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 30));
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(actionPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setPreferredSize(new Dimension(280, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Sidebar header
        JLabel sidebarTitle = new JLabel("Navigation");
        sidebarTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        sidebarTitle.setForeground(WHITE);
        sidebarTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarTitle.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        sidebar.add(sidebarTitle);
        
        // Menu items
        sidebar.add(createMenuButton("📊 Dashboard", "dashboard", true));
        sidebar.add(createMenuButton("📚 Books", "books", false));
        sidebar.add(createMenuButton("👥 Members", "members", false));
        sidebar.add(createMenuButton("📋 Issue Books", "issue", false));
        sidebar.add(createMenuButton("🔄 Current Issues", "current", false));
        sidebar.add(createMenuButton("⚠️ Overdue Books", "overdue", false));
        
        // Add spacing at bottom
        sidebar.add(Box.createVerticalGlue());
        
        return sidebar;
    }
    
    private JButton createMenuButton(String text, String panelName, boolean isActive) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(isActive ? PRIMARY_COLOR : new Color(189, 195, 199));
        button.setBackground(isActive ? WHITE : SIDEBAR_COLOR);
        button.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        button.setMaximumSize(new Dimension(280, 50));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        
        // Click handler
        button.addActionListener(e -> {
            switchPanel(panelName);
            updateSidebarSelection(panelName);
        });
        
        // Hover effect (only for inactive buttons)
        if (!isActive) {
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!currentPanel.equals(panelName)) {
                        button.setBackground(new Color(60, 70, 80));
                        button.setForeground(WHITE);
                    }
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    if (!currentPanel.equals(panelName)) {
                        button.setBackground(SIDEBAR_COLOR);
                        button.setForeground(new Color(189, 195, 199));
                    }
                }
            });
        }
        
        return button;
    }
    
    private void updateSidebarSelection(String selectedPanel) {
        currentPanel = selectedPanel;
        
        // Update all sidebar buttons
        Component[] components = sidebarPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                String btnText = btn.getText();
                boolean isSelected = 
                    (selectedPanel.equals("dashboard") && btnText.contains("Dashboard")) ||
                    (selectedPanel.equals("books") && btnText.contains("Books")) ||
                    (selectedPanel.equals("members") && btnText.contains("Members")) ||
                    (selectedPanel.equals("issue") && btnText.contains("Issue Books")) ||
                    (selectedPanel.equals("current") && btnText.contains("Current Issues")) ||
                    (selectedPanel.equals("overdue") && btnText.contains("Overdue Books"));
                
                if (isSelected) {
                    btn.setBackground(WHITE);
                    btn.setForeground(PRIMARY_COLOR);
                } else {
                    btn.setBackground(SIDEBAR_COLOR);
                    btn.setForeground(new Color(189, 195, 199));
                }
            }
        }
    }
    
    private void switchPanel(String panelName) {
        mainContentArea.removeAll();
        
        switch (panelName) {
            case "dashboard":
                mainContentArea.add(createDashboardPanel(), BorderLayout.CENTER);
                break;
            case "books":
                mainContentArea.add(createBooksPanel(), BorderLayout.CENTER);
                break;
            case "members":
                mainContentArea.add(createMembersPanel(), BorderLayout.CENTER);
                break;
            case "issue":
                mainContentArea.add(createIssuePanel(), BorderLayout.CENTER);
                break;
            case "current":
                mainContentArea.add(createCurrentIssuesPanel(), BorderLayout.CENTER);
                break;
            case "overdue":
                mainContentArea.add(createOverduePanel(), BorderLayout.CENTER);
                break;
        }
        
        mainContentArea.revalidate();
        mainContentArea.repaint();
    }
    
    private void showDashboard() {
        switchPanel("dashboard");
    }
    
    // Create Beautiful Dashboard Panel with Stats and Charts
    private JPanel createDashboardPanel() {
        JPanel dashboardPanel = new JPanel(new BorderLayout());
        dashboardPanel.setBackground(LIGHT_GRAY);
        
        // Dashboard title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(LIGHT_GRAY);
        
        JLabel titleLabel = new JLabel("📊 Dashboard Overview");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(INFO_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        titlePanel.add(titleLabel);
        
        // Stats cards panel
        JPanel statsPanel = createStatsCardsPanel();
        
        // Charts panel
        JPanel chartsPanel = createChartsPanel();
        
        // Recent activity panel
        JPanel recentPanel = createRecentActivityPanel();
        
        // Layout dashboard
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(LIGHT_GRAY);
        contentPanel.add(statsPanel, BorderLayout.NORTH);
        contentPanel.add(chartsPanel, BorderLayout.CENTER);
        contentPanel.add(recentPanel, BorderLayout.SOUTH);
        
        dashboardPanel.add(titlePanel, BorderLayout.NORTH);
        dashboardPanel.add(contentPanel, BorderLayout.CENTER);
        
        return dashboardPanel;
    }
    
    private JPanel createStatsCardsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setBackground(LIGHT_GRAY);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        // Get stats data
        int totalBooks = 0, totalMembers = 0, currentIssues = 0, overdueBooks = 0;
        try {
            totalBooks = bookDAO.getAllBooks().size();
            totalMembers = memberDAO.getAllMembers().size();
            currentIssues = issueDAO.getCurrentIssues().size();
            overdueBooks = issueDAO.getOverdueIssues().size();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Create stat cards
        statsPanel.add(createStatCard("📚 Total Books", String.valueOf(totalBooks), PRIMARY_COLOR));
        statsPanel.add(createStatCard("👥 Total Members", String.valueOf(totalMembers), SUCCESS_COLOR));
        statsPanel.add(createStatCard("📋 Current Issues", String.valueOf(currentIssues), WARNING_COLOR));
        statsPanel.add(createStatCard("⚠️ Overdue Books", String.valueOf(overdueBooks), DANGER_COLOR));
        
        return statsPanel;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Rounded rectangle background
                g2d.setColor(WHITE);
                g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
                
                // Colored left border
                g2d.setColor(color);
                g2d.fillRoundRect(0, 0, 5, getHeight()-1, 5, 5);
            }
        };
        
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));
        card.setPreferredSize(new Dimension(0, 120));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(108, 117, 125));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(valueLabel);
        
        return card;
    }
    
    private JPanel createChartsPanel() {
        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        chartsPanel.setBackground(LIGHT_GRAY);
        chartsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        // Books by category chart (mock)
        JPanel booksChart = createChartCard("📊 Books by Category", createBooksChart());
        
        // Issues over time chart (mock)
        JPanel issuesChart = createChartCard("📈 Issues This Month", createIssuesChart());
        
        chartsPanel.add(booksChart);
        chartsPanel.add(issuesChart);
        
        return chartsPanel;
    }
    
    private JPanel createChartCard(String title, JPanel chart) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Rounded rectangle background
                g2d.setColor(WHITE);
                g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
            }
        };
        
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setPreferredSize(new Dimension(0, 300));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(INFO_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(chart, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createBooksChart() {
        JPanel chart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Get real book data by analyzing authors/categories
                Map<String, Integer> categoryData = new HashMap<>();
                try {
                    List<Book> books = bookDAO.getAllBooks();
                    if (books.isEmpty()) {
                        // Show "No Data" message
                        g2d.setColor(Color.GRAY);
                        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                        String message = "No books available";
                        FontMetrics fm = g2d.getFontMetrics();
                        int x = (getWidth() - fm.stringWidth(message)) / 2;
                        int y = getHeight() / 2;
                        g2d.drawString(message, x, y);
                        return;
                    }
                    
                    // Categorize books by author or year ranges
                    for (Book book : books) {
                        String category;
                        if (book.getYear() >= 2000) {
                            category = "Modern";
                        } else if (book.getYear() >= 1950) {
                            category = "Classic";
                        } else if (book.getYear() >= 1900) {
                            category = "Vintage";
                        } else {
                            category = "Antique";
                        }
                        categoryData.put(category, categoryData.getOrDefault(category, 0) + book.getQuantity());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    // Show error message
                    g2d.setColor(Color.RED);
                    g2d.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    g2d.drawString("Error loading book data", 10, getHeight() / 2);
                    return;
                }
                
                // Draw bars for categories
                Color[] colors = {PRIMARY_COLOR, SUCCESS_COLOR, WARNING_COLOR, DANGER_COLOR};
                String[] categories = categoryData.keySet().toArray(new String[0]);
                
                if (categories.length == 0) {
                    g2d.setColor(Color.GRAY);
                    g2d.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                    String message = "No book categories";
                    FontMetrics fm = g2d.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(message)) / 2;
                    int y = getHeight() / 2;
                    g2d.drawString(message, x, y);
                    return;
                }
                
                int width = getWidth();
                int height = getHeight();
                int barWidth = width / (categories.length + 1);
                int maxValue = categoryData.values().stream().mapToInt(Integer::intValue).max().orElse(1);
                
                for (int i = 0; i < categories.length && i < colors.length; i++) {
                    String category = categories[i];
                    int value = categoryData.get(category);
                    int barHeight = (value * (height - 40)) / maxValue;
                    int x = (i + 1) * barWidth - barWidth/2;
                    int y = height - barHeight - 30;
                    
                    g2d.setColor(colors[i]);
                    g2d.fillRect(x, y, barWidth - 10, barHeight);
                    
                    // Category label
                    g2d.setColor(Color.BLACK);
                    g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(category);
                    g2d.drawString(category, x + (barWidth - 10 - textWidth) / 2, height - 10);
                    
                    // Value label
                    String valueStr = String.valueOf(value);
                    int valueWidth = fm.stringWidth(valueStr);
                    g2d.drawString(valueStr, x + (barWidth - 10 - valueWidth) / 2, y - 5);
                }
            }
        };
        chart.setBackground(WHITE);
        chart.setPreferredSize(new Dimension(0, 200));
        return chart;
    }
    
    private JPanel createIssuesChart() {
        JPanel chart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Get real issues data for the current month
                Map<Integer, Integer> dailyIssues = new HashMap<>();
                try {
                    List<Issue> issues = issueDAO.getAllIssues();
                    LocalDate now = LocalDate.now();
                    LocalDate monthStart = now.withDayOfMonth(1);
                    
                    // Count issues per day this month
                    for (Issue issue : issues) {
                        LocalDate issueDate = issue.getIssueDate();
                        if (issueDate.isAfter(monthStart.minusDays(1)) && issueDate.isBefore(now.plusDays(1))) {
                            int day = issueDate.getDayOfMonth();
                            dailyIssues.put(day, dailyIssues.getOrDefault(day, 0) + 1);
                        }
                    }
                    
                    if (dailyIssues.isEmpty()) {
                        // Show "No Data" message
                        g2d.setColor(Color.GRAY);
                        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                        String message = "No issues this month";
                        FontMetrics fm = g2d.getFontMetrics();
                        int x = (getWidth() - fm.stringWidth(message)) / 2;
                        int y = getHeight() / 2;
                        g2d.drawString(message, x, y);
                        return;
                    }
                    
                } catch (SQLException e) {
                    e.printStackTrace();
                    // Show error message
                    g2d.setColor(Color.RED);
                    g2d.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    g2d.drawString("Error loading issues data", 10, getHeight() / 2);
                    return;
                }
                
                // Draw line chart for this month's issues
                int width = getWidth();
                int height = getHeight();
                
                // Get last 7 days with data
                LocalDate today = LocalDate.now();
                int[] values = new int[7];
                String[] days = new String[7];
                
                for (int i = 0; i < 7; i++) {
                    LocalDate date = today.minusDays(6 - i);
                    days[i] = date.getDayOfWeek().toString().substring(0, 3);
                    values[i] = dailyIssues.getOrDefault(date.getDayOfMonth(), 0);
                }
                
                int maxValue = java.util.Arrays.stream(values).max().orElse(1);
                if (maxValue == 0) maxValue = 1; // Prevent division by zero
                
                // Draw line
                g2d.setColor(PRIMARY_COLOR);
                g2d.setStroke(new BasicStroke(3f));
                
                for (int i = 0; i < values.length - 1; i++) {
                    int x1 = (i + 1) * width / 8;
                    int y1 = height - 30 - (values[i] * (height - 60)) / maxValue;
                    int x2 = (i + 2) * width / 8;
                    int y2 = height - 30 - (values[i + 1] * (height - 60)) / maxValue;
                    
                    g2d.drawLine(x1, y1, x2, y2);
                    
                    // Draw points
                    g2d.fillOval(x1 - 4, y1 - 4, 8, 8);
                    
                    // Day labels
                    g2d.setColor(Color.BLACK);
                    g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(days[i]);
                    g2d.drawString(days[i], x1 - textWidth / 2, height - 10);
                    
                    // Value labels
                    String valueStr = String.valueOf(values[i]);
                    int valueWidth = fm.stringWidth(valueStr);
                    g2d.drawString(valueStr, x1 - valueWidth / 2, y1 - 10);
                    
                    g2d.setColor(PRIMARY_COLOR);
                }
                
                // Last point
                int lastX = values.length * width / 8;
                int lastY = height - 30 - (values[values.length - 1] * (height - 60)) / maxValue;
                g2d.fillOval(lastX - 4, lastY - 4, 8, 8);
                
                g2d.setColor(Color.BLACK);
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(days[days.length - 1]);
                g2d.drawString(days[days.length - 1], lastX - textWidth / 2, height - 10);
                
                String valueStr = String.valueOf(values[values.length - 1]);
                int valueWidth = fm.stringWidth(valueStr);
                g2d.drawString(valueStr, lastX - valueWidth / 2, lastY - 10);
            }
        };
        chart.setBackground(WHITE);
        chart.setPreferredSize(new Dimension(0, 200));
        return chart;
    }
    
    private JPanel createRecentActivityPanel() {
        JPanel activityPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Rounded rectangle background
                g2d.setColor(WHITE);
                g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
            }
        };
        
        activityPanel.setOpaque(false);
        activityPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        activityPanel.setPreferredSize(new Dimension(0, 200));
        
        JLabel titleLabel = new JLabel("🕒 Recent Activity");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(INFO_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        // Activity list (real data from recent issues)
        JPanel activityList = new JPanel();
        activityList.setLayout(new BoxLayout(activityList, BoxLayout.Y_AXIS));
        activityList.setBackground(WHITE);
        
        try {
            // Get recent issues (last 10)
            List<Issue> recentIssues = issueDAO.getAllIssues();
            if (recentIssues.isEmpty()) {
                JLabel noActivityLabel = new JLabel("📭 No recent activity");
                noActivityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                noActivityLabel.setForeground(Color.GRAY);
                noActivityLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
                activityList.add(noActivityLabel);
            } else {
                // Show last 5 issues as activity
                int count = 0;
                for (int i = recentIssues.size() - 1; i >= 0 && count < 5; i--, count++) {
                    Issue issue = recentIssues.get(i);
                    String activity;
                    Color color;
                    String timeAgo = getTimeAgo(issue.getIssueDate());
                    
                    if ("RETURNED".equals(issue.getStatus())) {
                        activity = "🔄 Book '" + issue.getBookTitle() + "' returned by " + issue.getMemberName();
                        color = SUCCESS_COLOR;
                    } else {
                        activity = "📋 Book '" + issue.getBookTitle() + "' issued to " + issue.getMemberName();
                        color = WARNING_COLOR;
                    }
                    
                    activityList.add(createActivityItem(activity, timeAgo, color));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JLabel errorLabel = new JLabel("❌ Error loading recent activity");
            errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            errorLabel.setForeground(Color.RED);
            errorLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
            activityList.add(errorLabel);
        }
        
        JScrollPane scrollPane = new JScrollPane(activityList);
        scrollPane.setBorder(null);
        scrollPane.setBackground(WHITE);
        
        activityPanel.add(titleLabel, BorderLayout.NORTH);
        activityPanel.add(scrollPane, BorderLayout.CENTER);
        
        return activityPanel;
    }
    
    private JPanel createActivityItem(String activity, String time, Color color) {
        JPanel item = new JPanel(new BorderLayout());
        item.setBackground(WHITE);
        item.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        JLabel activityLabel = new JLabel(activity);
        activityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        activityLabel.setForeground(INFO_COLOR);
        
        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timeLabel.setForeground(new Color(108, 117, 125));
        
        // Color indicator
        JPanel colorIndicator = new JPanel();
        colorIndicator.setBackground(color);
        colorIndicator.setPreferredSize(new Dimension(4, 20));
        
        item.add(colorIndicator, BorderLayout.WEST);
        item.add(activityLabel, BorderLayout.CENTER);
        item.add(timeLabel, BorderLayout.EAST);
        
        return item;
    }
    
    private String getTimeAgo(LocalDate date) {
        LocalDate now = LocalDate.now();
        long daysDiff = java.time.temporal.ChronoUnit.DAYS.between(date, now);
        
        if (daysDiff == 0) {
            return "Today";
        } else if (daysDiff == 1) {
            return "Yesterday";
        } else if (daysDiff < 7) {
            return daysDiff + " days ago";
        } else if (daysDiff < 30) {
            long weeks = daysDiff / 7;
            return weeks + (weeks == 1 ? " week ago" : " weeks ago");
        } else {
            long months = daysDiff / 30;
            return months + (months == 1 ? " month ago" : " months ago");
        }
    }
    
    // Create Beautiful Books Management Panel
    private JPanel createBooksPanel() {
        JPanel booksPanel = new JPanel(new BorderLayout());
        booksPanel.setBackground(LIGHT_GRAY);
        
        // Header panel with title and add button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(LIGHT_GRAY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("📚 Books Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(INFO_COLOR);
        
        JButton addButton = createModernButton("+ Add New Book", PRIMARY_COLOR);
        addButton.addActionListener(e -> showAddBookDialog());
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(addButton, BorderLayout.EAST);
        
        // Search panel
        JPanel searchPanel = createBooksSearchPanel();
        
        // Books table
        String[] columns = {"ID", "Title", "Author", "Year", "Stock Status", "Actions"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only actions column editable
            }
        };
        
        JTable table = createModernTable(model);
        
        // Store reference to books table and model for search functionality
        this.booksTable = table;
        this.booksTableModel = model;
        
        loadBooks(model);
        
        // Add table click handler for Actions column
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                
                if (row >= 0 && col == 5) { // Actions column
                    int bookId = (Integer) table.getValueAt(row, 0);
                    String bookTitle = (String) table.getValueAt(row, 1);
                    
                    String[] options = {"✏️ Edit", "🗑️ Delete", "❌ Cancel"};
                    int choice = JOptionPane.showOptionDialog(
                        Dashboard.this,
                        "Choose action for: " + bookTitle,
                        "Book Actions",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]
                    );
                    
                    if (choice == 0) { // Edit
                        showEditBookDialog(bookId);
                    } else if (choice == 1) { // Delete
                        deleteBook(bookId, bookTitle);
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(WHITE);
        
        // Layout
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(LIGHT_GRAY);
        contentPanel.add(searchPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        booksPanel.add(headerPanel, BorderLayout.NORTH);
        booksPanel.add(contentPanel, BorderLayout.CENTER);
        
        return booksPanel;
    }
    
    // Create Beautiful Members Management Panel
    private JPanel createMembersPanel() {
        JPanel membersPanel = new JPanel(new BorderLayout());
        membersPanel.setBackground(LIGHT_GRAY);
        
        // Header panel with title and add button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(LIGHT_GRAY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("👥 Members Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(INFO_COLOR);
        
        JButton addButton = createModernButton("+ Add New Member", SUCCESS_COLOR);
        addButton.addActionListener(e -> showAddMemberDialog());
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(addButton, BorderLayout.EAST);
        
        // Search panel
        JPanel searchPanel = createMembersSearchPanel();
        
        // Members table
        String[] columns = {"ID", "Name", "Email", "Phone", "Address", "Join Date", "Actions"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only actions column editable
            }
        };
        
        JTable table = createModernTable(model);
        
        // Store reference to members table and model for search functionality  
        this.membersTable = table;
        this.membersTableModel = model;
        
        loadMembers(model);
        
        // Add table click handler for Actions column
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                
                if (row >= 0 && col == 6) { // Actions column
                    int memberId = (Integer) table.getValueAt(row, 0);
                    String memberName = (String) table.getValueAt(row, 1);
                    
                    String[] options = {"✏️ Edit", "🗑️ Delete", "❌ Cancel"};
                    int choice = JOptionPane.showOptionDialog(
                        Dashboard.this,
                        "Choose action for: " + memberName,
                        "Member Actions",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]
                    );
                    
                    if (choice == 0) { // Edit
                        showEditMemberDialog(memberId);
                    } else if (choice == 1) { // Delete
                        deleteMember(memberId, memberName);
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(WHITE);
        
        // Layout
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(LIGHT_GRAY);
        contentPanel.add(searchPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        membersPanel.add(headerPanel, BorderLayout.NORTH);
        membersPanel.add(contentPanel, BorderLayout.CENTER);
        
        return membersPanel;
    }
    
    // Create Beautiful Issue Books Panel
    private JPanel createIssuePanel() {
        JPanel issuePanel = new JPanel(new BorderLayout());
        issuePanel.setBackground(LIGHT_GRAY);
        
        // Header panel with title and issue button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(LIGHT_GRAY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("📋 Issue Books");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(INFO_COLOR);
        
        JButton issueButton = createModernButton("📤 Issue New Book", WARNING_COLOR);
        issueButton.addActionListener(e -> showIssueBookDialog());
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(issueButton, BorderLayout.EAST);
        
        // Issue form panel
        JPanel formPanel = createIssueFormPanel();
        
        issuePanel.add(headerPanel, BorderLayout.NORTH);
        issuePanel.add(formPanel, BorderLayout.CENTER);
        
        return issuePanel;
    }
    
    private JPanel createIssueFormPanel() {
        JPanel formPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Rounded rectangle background
                g2d.setColor(WHITE);
                g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
            }
        };
        
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Member selection
        JLabel memberLabel = new JLabel("Select Member:");
        memberLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        memberLabel.setForeground(INFO_COLOR);
        
        JComboBox<String> memberCombo = new JComboBox<>();
        memberCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        memberCombo.setPreferredSize(new Dimension(0, 40));
        loadMembersCombo(memberCombo);
        
        // Book selection
        JLabel bookLabel = new JLabel("Select Book:");
        bookLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bookLabel.setForeground(INFO_COLOR);
        
        JComboBox<String> bookCombo = new JComboBox<>();
        bookCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        bookCombo.setPreferredSize(new Dimension(0, 40));
        loadAvailableBooksCombo(bookCombo);
        
        // Issue Date selection
        JLabel issueDateLabel = new JLabel("Issue Date:");
        issueDateLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        issueDateLabel.setForeground(INFO_COLOR);
        
        DatePickerField issueDatePicker = new DatePickerField(LocalDate.now());
        issueDatePicker.setMinDate(LocalDate.now().minusDays(7)); // Allow up to 7 days back
        issueDatePicker.setMaxDate(LocalDate.now()); // Cannot select future dates
        
        // Due Date selection
        JLabel dueDateLabel = new JLabel("Due Date:");
        dueDateLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        dueDateLabel.setForeground(INFO_COLOR);
        
        DatePickerField dueDatePicker = new DatePickerField(LocalDate.now().plusDays(14));
        dueDatePicker.setMinDate(LocalDate.now().plusDays(1)); // At least tomorrow
        dueDatePicker.setMaxDate(LocalDate.now().plusDays(90)); // Max 90 days loan
        
        // Auto-update due date when issue date changes
        issueDatePicker.addActionListener(e -> {
            LocalDate issueDate = issueDatePicker.getSelectedDate();
            LocalDate suggestedDueDate = issueDate.plusDays(14);
            dueDatePicker.setSelectedDate(suggestedDueDate);
            dueDatePicker.setMinDate(issueDate.plusDays(1)); // Due date must be after issue date
        });
        
        // Issue button
        JButton issueBtn = createModernButton("📤 Issue Book", WARNING_COLOR);
        issueBtn.addActionListener(e -> {
            if (memberCombo.getSelectedItem() != null && bookCombo.getSelectedItem() != null) {
                LocalDate issueDate = issueDatePicker.getSelectedDate();
                LocalDate dueDate = dueDatePicker.getSelectedDate();
                processBookIssueWithLocalDates(memberCombo, bookCombo, issueDate, dueDate);
            } else {
                showModernNotification("Please select both member and book!", "Error");
            }
        });
        
        // Layout
        formPanel.add(memberLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(memberCombo);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(bookLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(bookCombo);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(issueDateLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(issueDatePicker);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(dueDateLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(dueDatePicker);
        formPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        formPanel.add(issueBtn);
        formPanel.add(Box.createVerticalGlue());
        
        return formPanel;
    }
    
    // Create Beautiful Current Issues Panel
    private JPanel createCurrentIssuesPanel() {
        JPanel currentPanel = new JPanel(new BorderLayout());
        currentPanel.setBackground(LIGHT_GRAY);
        
        // Header panel with title and return button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(LIGHT_GRAY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("📋 Current Issues");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(INFO_COLOR);
        
        JButton refreshButton = createModernButton("🔄 Refresh", SUCCESS_COLOR);
        refreshButton.addActionListener(e -> {
            loadAllData();
            showModernNotification("Data refreshed!", "Success");
        });
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(refreshButton, BorderLayout.EAST);
        
        // Search panel
        JPanel searchPanel = createCurrentIssuesSearchPanel();
        
        // Current issues table
        String[] columns = {"Issue ID", "Book Title", "Member Name", "Issue Date", "Due Date", "Days", "Actions"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only actions column editable
            }
        };
        
        JTable table = createModernTable(model);
        loadCurrentIssues(model);
        
        // Add click handler for return action
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                int col = table.getSelectedColumn();
                if (row >= 0 && col == 6) { // Actions column
                    int issueId = (Integer) table.getValueAt(row, 0);
                    String bookTitle = (String) table.getValueAt(row, 1);
                    String memberName = (String) table.getValueAt(row, 2);
                    processBookReturn(issueId, bookTitle, memberName);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(WHITE);
        
        // Layout
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(LIGHT_GRAY);
        contentPanel.add(searchPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        currentPanel.add(headerPanel, BorderLayout.NORTH);
        currentPanel.add(contentPanel, BorderLayout.CENTER);
        
        return currentPanel;
    }
    
    // Create Beautiful Overdue Panel
    private JPanel createOverduePanel() {
        JPanel overduePanel = new JPanel(new BorderLayout());
        overduePanel.setBackground(LIGHT_GRAY);
        
        // Header panel with title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(LIGHT_GRAY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("⚠️ Overdue Books");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(DANGER_COLOR);
        
        JButton notifyButton = createModernButton("📧 Send Reminders", DANGER_COLOR);
        notifyButton.addActionListener(e -> {
            sendOverdueReminders();
            showModernNotification("Reminder emails sent!", "Success");
        });
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(notifyButton, BorderLayout.EAST);
        
        // Search panel
        JPanel searchPanel = createOverdueBooksSearchPanel();
        
        // Overdue table
        String[] columns = {"Issue ID", "Book Title", "Member Name", "Issue Date", "Due Date", "Days Overdue", "Fine Amount", "Actions"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Only actions column editable
            }
        };
        
        JTable table = createModernTable(model);
        loadOverdueBooks(model);
        
        // Add click handler for return/notify actions
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                int col = table.getSelectedColumn();
                if (row >= 0 && col == 7) { // Actions column (now column 7)
                    int issueId = (Integer) table.getValueAt(row, 0);
                    String bookTitle = (String) table.getValueAt(row, 1);
                    String memberName = (String) table.getValueAt(row, 2);
                    String fineAmount = (String) table.getValueAt(row, 6); // Get fine amount
                    
                    // Show options for overdue books
                    String[] options = {"Return Book", "Send Reminder", "Cancel"};
                    int choice = JOptionPane.showOptionDialog(
                        Dashboard.this,
                        "Select action for overdue book:\nFine: " + fineAmount,
                        "Overdue Book Actions",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]
                    );
                    
                    if (choice == 0) { // Return Book
                        processOverdueBookReturn(issueId, bookTitle, memberName, fineAmount);
                    } else if (choice == 1) { // Send Reminder
                        sendOverdueReminder(memberName, bookTitle);
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(WHITE);
        
        // Layout
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(LIGHT_GRAY);
        contentPanel.add(searchPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        overduePanel.add(headerPanel, BorderLayout.NORTH);
        overduePanel.add(contentPanel, BorderLayout.CENTER);
        
        return overduePanel;
    }
    
    // Utility methods for modern UI components
    private JButton createModernButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(WHITE);
        button.setBackground(bgColor);
        button.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
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
    
    private JPanel createSearchPanel(String placeholder) {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(LIGHT_GRAY);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JTextField searchField = new JTextField(placeholder, 30);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(300, 40));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        JButton searchBtn = createModernButton("🔍 Search", PRIMARY_COLOR);
        
        searchPanel.add(searchField);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(searchBtn);
        
        return searchPanel;
    }
    
    private JPanel createBooksSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(LIGHT_GRAY);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JTextField searchField = new JTextField("🔍 Search books...", 30);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(300, 40));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        searchField.setForeground(new Color(108, 117, 125));
        
        // Add focus listener for placeholder text
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals("🔍 Search books...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("🔍 Search books...");
                    searchField.setForeground(new Color(108, 117, 125));
                }
            }
        });
        
        JButton searchBtn = createModernButton("🔍 Search", PRIMARY_COLOR);
        JButton clearBtn = createModernButton("✖ Clear", DANGER_COLOR);
        
        // Search functionality
        searchBtn.addActionListener(e -> {
            String searchText = searchField.getText().trim();
            if (!searchText.isEmpty() && !searchText.equals("🔍 Search books...")) {
                searchBooks(searchText);
            }
        });
        
        // Clear functionality
        clearBtn.addActionListener(e -> {
            searchField.setText("🔍 Search books...");
            searchField.setForeground(new Color(108, 117, 125));
            loadBooks(booksTableModel); // Reload all books
        });
        
        // Enter key search
        searchField.addActionListener(e -> {
            String searchText = searchField.getText().trim();
            if (!searchText.isEmpty() && !searchText.equals("🔍 Search books...")) {
                searchBooks(searchText);
            }
        });
        
        searchPanel.add(searchField);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(searchBtn);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(clearBtn);
        
        return searchPanel;
    }
    
    private JPanel createMembersSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(LIGHT_GRAY);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JTextField searchField = new JTextField("🔍 Search members...", 30);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(300, 40));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        searchField.setForeground(new Color(108, 117, 125));
        
        // Add focus listener for placeholder text
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals("🔍 Search members...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("🔍 Search members...");
                    searchField.setForeground(new Color(108, 117, 125));
                }
            }
        });
        
        JButton searchBtn = createModernButton("🔍 Search", PRIMARY_COLOR);
        JButton clearBtn = createModernButton("✖ Clear", DANGER_COLOR);
        
        // Search functionality
        searchBtn.addActionListener(e -> {
            String searchText = searchField.getText().trim();
            if (!searchText.isEmpty() && !searchText.equals("🔍 Search members...")) {
                searchMembers(searchText);
            }
        });
        
        // Clear functionality
        clearBtn.addActionListener(e -> {
            searchField.setText("🔍 Search members...");
            searchField.setForeground(new Color(108, 117, 125));
            loadMembers(membersTableModel); // Reload all members
        });
        
        // Enter key search
        searchField.addActionListener(e -> {
            String searchText = searchField.getText().trim();
            if (!searchText.isEmpty() && !searchText.equals("🔍 Search members...")) {
                searchMembers(searchText);
            }
        });
        
        searchPanel.add(searchField);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(searchBtn);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(clearBtn);
        
        return searchPanel;
    }
    
    private JPanel createCurrentIssuesSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(LIGHT_GRAY);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JTextField searchField = new JTextField("🔍 Search current issues...", 30);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(300, 40));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        searchField.setForeground(new Color(108, 117, 125));
        
        // Add focus listener for placeholder text
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals("🔍 Search current issues...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("🔍 Search current issues...");
                    searchField.setForeground(new Color(108, 117, 125));
                }
            }
        });
        
        JButton searchBtn = createModernButton("🔍 Search", PRIMARY_COLOR);
        JButton clearBtn = createModernButton("✖ Clear", DANGER_COLOR);
        
        // Search functionality
        searchBtn.addActionListener(e -> searchCurrentIssues(searchField.getText()));
        clearBtn.addActionListener(e -> {
            searchField.setText("🔍 Search current issues...");
            searchField.setForeground(new Color(108, 117, 125));
            searchCurrentIssues(""); // Show all
        });
        
        // Real-time search on Enter key
        searchField.addActionListener(e -> searchCurrentIssues(searchField.getText()));
        
        searchPanel.add(searchField);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(searchBtn);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(clearBtn);
        
        return searchPanel;
    }
    
    private JPanel createOverdueBooksSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(LIGHT_GRAY);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JTextField searchField = new JTextField("🔍 Search overdue books...", 30);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(300, 40));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        searchField.setForeground(new Color(108, 117, 125));
        
        // Add focus listener for placeholder text
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals("🔍 Search overdue books...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("🔍 Search overdue books...");
                    searchField.setForeground(new Color(108, 117, 125));
                }
            }
        });
        
        JButton searchBtn = createModernButton("🔍 Search", PRIMARY_COLOR);
        JButton clearBtn = createModernButton("✖ Clear", DANGER_COLOR);
        
        // Search functionality
        searchBtn.addActionListener(e -> searchOverdueBooks(searchField.getText()));
        clearBtn.addActionListener(e -> {
            searchField.setText("🔍 Search overdue books...");
            searchField.setForeground(new Color(108, 117, 125));
            searchOverdueBooks(""); // Show all
        });
        
        // Real-time search on Enter key
        searchField.addActionListener(e -> searchOverdueBooks(searchField.getText()));
        
        searchPanel.add(searchField);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(searchBtn);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(clearBtn);
        
        return searchPanel;
    }
    
    private JTable createModernTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(45);
        table.setGridColor(new Color(240, 240, 240));
        table.setSelectionBackground(new Color(52, 152, 219, 50));
        table.setSelectionForeground(INFO_COLOR);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(248, 249, 250));
        table.getTableHeader().setForeground(INFO_COLOR);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(220, 220, 220)));
        
        // Custom cell renderer for alternating row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? WHITE : new Color(248, 249, 250));
                }
                return c;
            }
        });
        
        return table;
    }
    
    // Data loading methods
    private void loadBooks(DefaultTableModel model) {
        try {
            List<Book> books = bookDAO.getAllBooks();
            model.setRowCount(0);
            for (Book book : books) {
                Object[] row = {
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getYear(),
                    book.getQuantity() > 0 ? "✅ Available (" + book.getQuantity() + ")" : "❌ Out of Stock",
                    "Edit | Delete"
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showModernNotification("Error loading books: " + e.getMessage(), "Error");
        }
    }
    
    private void loadMembers(DefaultTableModel model) {
        try {
            List<Member> members = memberDAO.getAllMembers();
            model.setRowCount(0);
            for (Member member : members) {
                Object[] row = {
                    member.getId(),
                    member.getName(),
                    member.getEmail(),
                    member.getPhone(),
                    member.getAddress(),
                    member.getJoinDate(),
                    "Edit | Delete"
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showModernNotification("Error loading members: " + e.getMessage(), "Error");
        }
    }
    
    private void loadCurrentIssues(DefaultTableModel model) {
        try {
            List<Issue> issues = issueDAO.getCurrentIssues();
            model.setRowCount(0);
            for (Issue issue : issues) {
                long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(
                    issue.getIssueDate(),
                    java.time.LocalDate.now()
                );
                Object[] row = {
                    issue.getId(),
                    issue.getBookTitle(),
                    issue.getMemberName(),
                    issue.getIssueDate(),
                    issue.getDueDate(),
                    daysBetween + " days",
                    "Return"
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showModernNotification("Error loading current issues: " + e.getMessage(), "Error");
        }
    }
    
    private void loadOverdueBooks(DefaultTableModel model) {
        try {
            List<Issue> overdueIssues = issueDAO.getOverdueIssues();
            model.setRowCount(0);
            
            for (Issue issue : overdueIssues) {
                long daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(
                    issue.getDueDate(),
                    java.time.LocalDate.now()
                );
                
                // Calculate fine amount using configurable rate
                double fineAmount = daysOverdue * FINE_PER_DAY;
                String fineDisplay = String.format("Rs %.2f", fineAmount);
                
                Object[] row = {
                    issue.getId(),
                    issue.getBookTitle(),
                    issue.getMemberName(),
                    issue.getIssueDate(),
                    issue.getDueDate(),
                    daysOverdue + " days",
                    fineDisplay,
                    "Return | Remind"
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showModernNotification("Error loading overdue books: " + e.getMessage(), "Error");
        }
    }
    
    private void searchBooks(String searchText) {
        try {
            List<Book> allBooks = bookDAO.getAllBooks();
            booksTableModel.setRowCount(0);
            
            String searchLower = searchText.toLowerCase();
            int foundCount = 0;
            
            for (Book book : allBooks) {
                // Search in title, author, and year
                boolean matches = book.getTitle().toLowerCase().contains(searchLower) ||
                                book.getAuthor().toLowerCase().contains(searchLower) ||
                                String.valueOf(book.getYear()).contains(searchText) ||
                                String.valueOf(book.getId()).contains(searchText);
                
                if (matches) {
                    Object[] row = {
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getYear(),
                        book.getQuantity() > 0 ? "✅ Available (" + book.getQuantity() + ")" : "❌ Out of Stock",
                        "Edit | Delete"
                    };
                    booksTableModel.addRow(row);
                    foundCount++;
                }
            }
            
            // Show notification with results
            if (foundCount == 0) {
                showModernNotification("No books found matching '" + searchText + "'", "Info");
            } else {
                showModernNotification("Found " + foundCount + " book(s) matching '" + searchText + "'", "Success");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            showModernNotification("Error searching books: " + e.getMessage(), "Error");
        }
    }
    
    private void searchMembers(String searchText) {
        try {
            List<Member> allMembers = memberDAO.getAllMembers();
            membersTableModel.setRowCount(0);
            
            String searchLower = searchText.toLowerCase();
            int foundCount = 0;
            
            for (Member member : allMembers) {
                // Search in name, email, phone, and address
                boolean matches = member.getName().toLowerCase().contains(searchLower) ||
                                member.getEmail().toLowerCase().contains(searchLower) ||
                                member.getPhone().toLowerCase().contains(searchLower) ||
                                member.getAddress().toLowerCase().contains(searchLower) ||
                                String.valueOf(member.getId()).contains(searchText);
                
                if (matches) {
                    Object[] row = {
                        member.getId(),
                        member.getName(),
                        member.getEmail(),
                        member.getPhone(),
                        member.getAddress(),
                        member.getJoinDate(),
                        "Edit | Delete"
                    };
                    membersTableModel.addRow(row);
                    foundCount++;
                }
            }
            
            // Show notification with results
            if (foundCount == 0) {
                showModernNotification("No members found matching '" + searchText + "'", "Info");
            } else {
                showModernNotification("Found " + foundCount + " member(s) matching '" + searchText + "'", "Success");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            showModernNotification("Error searching members: " + e.getMessage(), "Error");
        }
    }
    
    private void loadMembersCombo(JComboBox<String> combo) {
        try {
            List<Member> members = memberDAO.getAllMembers();
            combo.removeAllItems();
            for (Member member : members) {
                combo.addItem(member.getId() + " - " + member.getName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showModernNotification("Error loading members: " + e.getMessage(), "Error");
        }
    }
    
    private void loadAvailableBooksCombo(JComboBox<String> combo) {
        try {
            List<Book> books = bookDAO.getAllBooks();
            combo.removeAllItems();
            for (Book book : books) {
                if (book.getQuantity() > 0) {
                    combo.addItem(book.getId() + " - " + book.getTitle());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showModernNotification("Error loading books: " + e.getMessage(), "Error");
        }
    }
    
    // Dialog methods
    private void showAddBookDialog() {
        JDialog dialog = new JDialog(this, "Add New Book", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        
        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(WHITE);
        
        // Title
        JLabel titleLabel = new JLabel("📚 Add New Book");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Title field
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JTextField titleField = new JTextField(20);
        titleField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(titleField, gbc);
        
        // Author field
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Author:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JTextField authorField = new JTextField(20);
        authorField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(authorField, gbc);
        
        // Year field
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Year:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JTextField yearField = new JTextField(20);
        yearField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(yearField, gbc);
        
        // Quantity field
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JTextField quantityField = new JTextField(20);
        quantityField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(quantityField, gbc);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(WHITE);
        
        JButton saveButton = createModernButton("💾 Save Book", SUCCESS_COLOR);
        JButton cancelButton = createModernButton("❌ Cancel", DANGER_COLOR);
        
        saveButton.addActionListener(e -> {
            try {
                String title = titleField.getText().trim();
                String author = authorField.getText().trim();
                String yearStr = yearField.getText().trim();
                String quantityStr = quantityField.getText().trim();
                
                // Validation 1: Check for empty fields
                if (title.isEmpty() || author.isEmpty() || yearStr.isEmpty() || quantityStr.isEmpty()) {
                    showModernNotification("Book title and author must not be empty!", "Error");
                    return;
                }
                
                // Validation 2: Check numeric fields
                int year, quantity;
                try {
                    year = Integer.parseInt(yearStr);
                    quantity = Integer.parseInt(quantityStr);
                } catch (NumberFormatException ex) {
                    showModernNotification("Year and Quantity must be valid numbers!", "Error");
                    return;
                }
                
                // Validation 3: Quantity cannot be negative
                if (quantity < 0) {
                    showModernNotification("Quantity cannot be negative!", "Error");
                    return;
                }
                
                // Validation 4: Year should be reasonable
                if (year < 1000 || year > java.time.LocalDate.now().getYear() + 1) {
                    showModernNotification("Please enter a valid publication year!", "Error");
                    return;
                }
                
                // Validation 5: Check for duplicate book (title + author combination)
                List<Book> existingBooks = bookDAO.getAllBooks();
                for (Book book : existingBooks) {
                    if (book.getTitle().equalsIgnoreCase(title) && book.getAuthor().equalsIgnoreCase(author)) {
                        showModernNotification("A book with this title and author already exists!", "Error");
                        return;
                    }
                }
                
                Book newBook = new Book(title, author, year, quantity);
                bookDAO.addBook(newBook);
                
                // Refresh the books table
                refreshBooksTable();
                
                dialog.dispose();
                showModernNotification("Book added successfully!", "Success");
                
            } catch (SQLException ex) {
                showModernNotification("Error adding book: " + ex.getMessage(), "Error");
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Layout
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    private void showAddMemberDialog() {
        JDialog dialog = new JDialog(this, "Add New Member", true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        
        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(WHITE);
        
        // Title
        JLabel titleLabel = new JLabel("👤 Add New Member");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(SUCCESS_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Name field
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JTextField nameField = new JTextField(20);
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(nameField, gbc);
        
        // Email field
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JTextField emailField = new JTextField(20);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(emailField, gbc);
        
        // Phone field
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JTextField phoneField = new JTextField(20);
        phoneField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(phoneField, gbc);
        
        // Address field
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JTextField addressField = new JTextField(20);
        addressField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(addressField, gbc);
        
        // Member Type field
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Member Type:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"STUDENT", "TEACHER", "STAFF"});
        typeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(typeCombo, gbc);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(WHITE);
        
        JButton saveButton = createModernButton("💾 Save Member", SUCCESS_COLOR);
        JButton cancelButton = createModernButton("❌ Cancel", DANGER_COLOR);
        
        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String phone = phoneField.getText().trim();
                String address = addressField.getText().trim();
                String memberType = (String) typeCombo.getSelectedItem();
                
                // Validation 1: Check for empty fields
                if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                    showModernNotification("Please fill all fields!", "Error");
                    return;
                }
                
                // Validation 2: Email format validation
                if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                    showModernNotification("Please enter a valid email address!", "Error");
                    return;
                }
                
                // Validation 3: Phone number format validation (10 digits)
                if (!phone.matches("\\d{3}-\\d{3}-\\d{4}") && !phone.matches("\\d{10}")) {
                    showModernNotification("Phone number should be 10 digits or in format XXX-XXX-XXXX!", "Error");
                    return;
                }
                
                // Validation 4: Check for unique email
                List<Member> existingMembers = memberDAO.getAllMembers();
                for (Member member : existingMembers) {
                    if (member.getEmail().equalsIgnoreCase(email)) {
                        showModernNotification("A member with this email already exists!", "Error");
                        return;
                    }
                }
                
                Member newMember = new Member(name, email, phone, address, memberType);
                memberDAO.addMember(newMember);
                
                // Refresh the members table
                refreshMembersTable();
                
                dialog.dispose();
                showModernNotification("Member added successfully!", "Success");
                
            } catch (SQLException ex) {
                showModernNotification("Error adding member: " + ex.getMessage(), "Error");
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Layout
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    private void showIssueBookDialog() {
        showModernNotification("Use the Issue Books panel to issue books!", "Info");
    }
    
    private void processBookIssue(JComboBox<String> memberCombo, JComboBox<String> bookCombo) {
        try {
            // Extract member ID from combo selection (format: "ID - Name")
            String memberSelection = (String) memberCombo.getSelectedItem();
            if (memberSelection == null) {
                showModernNotification("Please select a member!", "Error");
                return;
            }
            int memberId = Integer.parseInt(memberSelection.split(" - ")[0]);
            
            // Extract book ID from combo selection (format: "ID - Title")
            String bookSelection = (String) bookCombo.getSelectedItem();
            if (bookSelection == null) {
                showModernNotification("Please select a book!", "Error");
                return;
            }
            int bookId = Integer.parseInt(bookSelection.split(" - ")[0]);
            
            // Validation 1: Check if book is available (stock > 0)
            Book book = bookDAO.getBookById(bookId);
            if (book == null || book.getQuantity() <= 0) {
                showModernNotification("This book is currently unavailable. All copies are issued.", "Error");
                return;
            }
            
            // Validation 2: Check if member already has this book issued
            List<Issue> currentIssues = issueDAO.getCurrentIssues();
            for (Issue issue : currentIssues) {
                if (issue.getMemberId() == memberId && issue.getBookId() == bookId) {
                    showModernNotification("This member already has this book issued. Cannot reissue until returned.", "Error");
                    return;
                }
            }
            
            // Validation 3: Check member's issued book limit (max 3 books)
            int memberBookCount = 0;
            for (Issue issue : currentIssues) {
                if (issue.getMemberId() == memberId) {
                    memberBookCount++;
                }
            }
            
            if (memberBookCount >= 3) {
                showModernNotification("This member has reached the maximum number of issued books (3).", "Error");
                return;
            }
            
            // Validation 4: Check if member has unpaid fines
            List<Issue> allIssues = issueDAO.getAllIssues();
            double totalFines = 0.0;
            for (Issue issue : allIssues) {
                if (issue.getMemberId() == memberId && issue.getFineAmount() > 0) {
                    totalFines += issue.getFineAmount();
                }
            }
            
            if (totalFines > 0) {
                int choice = JOptionPane.showConfirmDialog(
                    this,
                    "This member has unpaid fines of Rs " + String.format("%.2f", totalFines) + 
                    ".\nDo you still want to issue the book?",
                    "Unpaid Fines Warning",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                
                if (choice != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            
            // All validations passed - proceed with issue
            LocalDate dueDate = LocalDate.now().plusDays(14);
            Issue issue = new Issue(bookId, memberId, dueDate);
            
            // Issue the book
            issueDAO.issueBook(issue);
            
            // Refresh the display
            loadAllData();
            switchPanel("current"); // Switch to current issues view
            
            showModernNotification("Book issued successfully! Due date: " + dueDate, "Success");
            
        } catch (SQLException e) {
            e.printStackTrace();
            showModernNotification("Error issuing book: " + e.getMessage(), "Error");
        } catch (NumberFormatException e) {
            showModernNotification("Invalid selection format!", "Error");
        } catch (ArrayIndexOutOfBoundsException e) {
            showModernNotification("Please select valid member and book!", "Error");
        }
    }
    
    private void processBookIssueWithLocalDates(JComboBox<String> memberCombo, JComboBox<String> bookCombo, LocalDate issueDate, LocalDate dueDate) {
        try {
            // Validate dates first
            if (issueDate == null || dueDate == null) {
                showModernNotification("Please select both issue date and due date!", "Error");
                return;
            }
            
            // Enhanced date validations
            LocalDate today = LocalDate.now();
            
            // Validation: Issue date cannot be more than 7 days in the past
            if (issueDate.isBefore(today.minusDays(7))) {
                showModernNotification("Issue date cannot be more than 7 days in the past!", "Error");
                return;
            }
            
            // Validation: Issue date cannot be in the future
            if (issueDate.isAfter(today)) {
                showModernNotification("Issue date cannot be in the future!", "Error");
                return;
            }
            
            // Validation: Due date must be after issue date
            if (dueDate.isBefore(issueDate) || dueDate.isEqual(issueDate)) {
                showModernNotification("Due date must be after issue date!", "Error");
                return;
            }
            
            // Validation: Due date cannot be more than 90 days from issue date
            if (dueDate.isAfter(issueDate.plusDays(90))) {
                showModernNotification("Maximum loan period is 90 days!", "Error");
                return;
            }
            
            // Call the existing method with Date objects
            java.util.Date issueDateUtil = java.util.Date.from(issueDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
            java.util.Date dueDateUtil = java.util.Date.from(dueDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
            
            processBookIssueWithDates(memberCombo, bookCombo, issueDateUtil, dueDateUtil);
            
        } catch (Exception e) {
            e.printStackTrace();
            showModernNotification("Error processing book issue: " + e.getMessage(), "Error");
        }
    }
    
    private void processBookIssueWithDates(JComboBox<String> memberCombo, JComboBox<String> bookCombo, java.util.Date issueDate, java.util.Date dueDate) {
        try {
            // Extract member ID from combo selection (format: "ID - Name")
            String memberSelection = (String) memberCombo.getSelectedItem();
            if (memberSelection == null) {
                showModernNotification("Please select a member!", "Error");
                return;
            }
            int memberId = Integer.parseInt(memberSelection.split(" - ")[0]);
            
            // Extract book ID from combo selection (format: "ID - Title")
            String bookSelection = (String) bookCombo.getSelectedItem();
            if (bookSelection == null) {
                showModernNotification("Please select a book!", "Error");
                return;
            }
            int bookId = Integer.parseInt(bookSelection.split(" - ")[0]);
            
            // Convert Date to LocalDate
            LocalDate issueLocalDate = issueDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            LocalDate dueLocalDate = dueDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            
            // Date validations
            LocalDate today = LocalDate.now();
            
            // Validation: Issue date cannot be in the future
            if (issueLocalDate.isAfter(today)) {
                showModernNotification("Issue date cannot be in the future!", "Error");
                return;
            }
            
            // Validation: Due date must be after issue date
            if (dueLocalDate.isBefore(issueLocalDate) || dueLocalDate.isEqual(issueLocalDate)) {
                showModernNotification("Due date must be after issue date!", "Error");
                return;
            }
            
            // Validation 1: Check if book is available (stock > 0)
            Book book = bookDAO.getBookById(bookId);
            if (book == null || book.getQuantity() <= 0) {
                showModernNotification("This book is currently unavailable. All copies are issued.", "Error");
                return;
            }
            
            // Validation 2: Check if member already has this book issued
            List<Issue> currentIssues = issueDAO.getCurrentIssues();
            for (Issue issue : currentIssues) {
                if (issue.getMemberId() == memberId && issue.getBookId() == bookId) {
                    showModernNotification("This member already has this book issued. Cannot reissue until returned.", "Error");
                    return;
                }
            }
            
            // Validation 3: Check member's issued book limit (max 3 books)
            int memberBookCount = 0;
            for (Issue issue : currentIssues) {
                if (issue.getMemberId() == memberId) {
                    memberBookCount++;
                }
            }
            
            if (memberBookCount >= 3) {
                showModernNotification("This member has reached the maximum number of issued books (3).", "Error");
                return;
            }
            
            // Validation 4: Check if member has unpaid fines
            List<Issue> allIssues = issueDAO.getAllIssues();
            double totalFines = 0.0;
            for (Issue issue : allIssues) {
                if (issue.getMemberId() == memberId && issue.getFineAmount() > 0) {
                    totalFines += issue.getFineAmount();
                }
            }
            
            if (totalFines > 0) {
                int choice = JOptionPane.showConfirmDialog(
                    this,
                    "This member has unpaid fines of Rs " + String.format("%.2f", totalFines) + 
                    ".\nDo you still want to issue the book?",
                    "Unpaid Fines Warning",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                
                if (choice != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            
            // All validations passed - proceed with issue using custom dates
            Issue issue = new Issue(bookId, memberId, dueLocalDate);
            issue.setIssueDate(issueLocalDate); // Set custom issue date
            
            // Issue the book
            issueDAO.issueBookWithDates(issue, issueLocalDate);
            
            // Refresh the display
            loadAllData();
            switchPanel("current"); // Switch to current issues view
            
            showModernNotification("Book issued successfully!\nIssue date: " + issueLocalDate + "\nDue date: " + dueLocalDate, "Success");
            
        } catch (SQLException e) {
            e.printStackTrace();
            showModernNotification("Error issuing book: " + e.getMessage(), "Error");
        } catch (NumberFormatException e) {
            showModernNotification("Invalid selection format!", "Error");
        } catch (ArrayIndexOutOfBoundsException e) {
            showModernNotification("Please select valid member and book!", "Error");
        }
    }
    
    private void processBookReturn(int issueId, String bookTitle, String memberName) {
        try {
            // Validation 1: Check if the book was actually issued
            Issue issue = issueDAO.getIssueById(issueId);
            if (issue == null) {
                showModernNotification("Issue record not found!", "Error");
                return;
            }
            
            if ("RETURNED".equals(issue.getStatus())) {
                showModernNotification("This book has already been returned!", "Error");
                return;
            }
            
            // Validation 2: Calculate fine if overdue
            double fineAmount = 0.0;
            LocalDate today = LocalDate.now();
            if (today.isAfter(issue.getDueDate())) {
                long daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(issue.getDueDate(), today);
                fineAmount = daysOverdue * FINE_PER_DAY; // Rs 10 per day
            }
            
            // Validation 3: Prevent backdated returns
            if (issue.getIssueDate().isAfter(today)) {
                showModernNotification("Cannot return a book before it was issued!", "Error");
                return;
            }
            
            // Show confirmation dialog with fine details
            String message = "Return book \"" + bookTitle + "\" borrowed by " + memberName + "?";
            if (fineAmount > 0) {
                message += "\n\nOverdue fine: Rs " + String.format("%.2f", fineAmount);
                message += "\nDays overdue: " + java.time.temporal.ChronoUnit.DAYS.between(issue.getDueDate(), today);
            }
            
            int choice = JOptionPane.showConfirmDialog(
                this,
                message,
                "Confirm Return",
                JOptionPane.YES_NO_OPTION,
                fineAmount > 0 ? JOptionPane.WARNING_MESSAGE : JOptionPane.QUESTION_MESSAGE
            );
            
            if (choice == JOptionPane.YES_OPTION) {
                // Return the book with calculated fine
                issueDAO.returnBook(issueId, fineAmount);
                
                // Refresh the display
                loadAllData();
                switchPanel("current"); // Refresh current issues view
                
                if (fineAmount > 0) {
                    showModernNotification("Book returned successfully! Fine applied: Rs " + String.format("%.2f", fineAmount), "Warning");
                } else {
                    showModernNotification("Book returned successfully!", "Success");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showModernNotification("Error returning book: " + e.getMessage(), "Error");
        }
    }
    
    private void processOverdueBookReturn(int issueId, String bookTitle, String memberName, String fineAmountString) {
        try {
            // Parse fine amount from string (remove Rs and convert to double)
            double fineAmount = Double.parseDouble(fineAmountString.replace("Rs ", ""));
            
            // Show confirmation dialog with fine calculation
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Return overdue book \"" + bookTitle + "\" borrowed by " + memberName + "?\n" +
                "Fine to be applied: " + fineAmountString,
                "Confirm Overdue Return",
                JOptionPane.YES_NO_OPTION
            );
            
            if (choice == JOptionPane.YES_OPTION) {
                // Return the book with fine
                issueDAO.returnBook(issueId, fineAmount);
                
                // Refresh the display
                loadAllData();
                switchPanel("overdue"); // Refresh overdue view
                
                showModernNotification("Overdue book returned! Fine applied: " + fineAmountString, "Warning");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showModernNotification("Error returning overdue book: " + e.getMessage(), "Error");
        } catch (NumberFormatException e) {
            showModernNotification("Error parsing fine amount: " + e.getMessage(), "Error");
        }
    }
    
    private void sendOverdueReminder(String memberName, String bookTitle) {
        // Simulate sending reminder (email, SMS, etc.)
        showModernNotification("Reminder sent to " + memberName + " for book: " + bookTitle, "Info");
        
        // In a real application, you would:
        // 1. Get member's email/phone from database
        // 2. Send actual email/SMS notification
        // 3. Log the reminder in database
        // 4. Update reminder count/date
    }
    
    private void searchCurrentIssues(String searchTerm) {
        try {
            if (searchTerm.equals("🔍 Search current issues...") || searchTerm.trim().isEmpty()) {
                // Show all current issues
                switchPanel("current");
                return;
            }
            
            // Filter current issues based on search term
            List<Issue> allIssues = issueDAO.getCurrentIssues();
            List<Issue> filteredIssues = new ArrayList<>();
            
            for (Issue issue : allIssues) {
                if (issue.getBookTitle().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    issue.getMemberName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    String.valueOf(issue.getId()).contains(searchTerm)) {
                    filteredIssues.add(issue);
                }
            }
            
            // Update the current issues table with filtered results
            updateCurrentIssuesTable(filteredIssues);
            showModernNotification("Found " + filteredIssues.size() + " current issues matching: " + searchTerm, "Info");
            
        } catch (SQLException e) {
            e.printStackTrace();
            showModernNotification("Error searching current issues: " + e.getMessage(), "Error");
        }
    }
    
    private void searchOverdueBooks(String searchTerm) {
        try {
            if (searchTerm.equals("🔍 Search overdue books...") || searchTerm.trim().isEmpty()) {
                // Show all overdue books
                switchPanel("overdue");
                return;
            }
            
            // Filter overdue books based on search term
            List<Issue> allOverdue = issueDAO.getOverdueIssues();
            List<Issue> filteredOverdue = new ArrayList<>();
            
            for (Issue issue : allOverdue) {
                if (issue.getBookTitle().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    issue.getMemberName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    String.valueOf(issue.getId()).contains(searchTerm)) {
                    filteredOverdue.add(issue);
                }
            }
            
            // Update the overdue books table with filtered results
            updateOverdueBooksTable(filteredOverdue);
            showModernNotification("Found " + filteredOverdue.size() + " overdue books matching: " + searchTerm, "Info");
            
        } catch (SQLException e) {
            e.printStackTrace();
            showModernNotification("Error searching overdue books: " + e.getMessage(), "Error");
        }
    }
    
    private void updateCurrentIssuesTable(List<Issue> issues) {
        // Find the current issues table and update it
        // This method updates the table model with filtered data
        try {
            DefaultTableModel model = new DefaultTableModel(
                new String[]{"Issue ID", "Book Title", "Member Name", "Issue Date", "Due Date", "Days", "Actions"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 6;
                }
            };
            
            for (Issue issue : issues) {
                long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(
                    issue.getIssueDate(),
                    java.time.LocalDate.now()
                );
                Object[] row = {
                    issue.getId(),
                    issue.getBookTitle(),
                    issue.getMemberName(),
                    issue.getIssueDate(),
                    issue.getDueDate(),
                    daysBetween + " days",
                    "Return"
                };
                model.addRow(row);
            }
            
            // This would need access to the current table - simplified for now
            switchPanel("current"); // Refresh the panel
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void updateOverdueBooksTable(List<Issue> issues) {
        // Similar implementation for overdue books
        try {
            DefaultTableModel model = new DefaultTableModel(
                new String[]{"Issue ID", "Book Title", "Member Name", "Issue Date", "Due Date", "Days Overdue", "Actions"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 6;
                }
            };
            
            for (Issue issue : issues) {
                long daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(
                    issue.getDueDate(),
                    java.time.LocalDate.now()
                );
                Object[] row = {
                    issue.getId(),
                    issue.getBookTitle(),
                    issue.getMemberName(),
                    issue.getIssueDate(),
                    issue.getDueDate(),
                    daysOverdue + " days",
                    "Action"
                };
                model.addRow(row);
            }
            
            // This would need access to the current table - simplified for now
            switchPanel("overdue"); // Refresh the panel
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void sendOverdueReminders() {
        showModernNotification("Reminder feature coming soon!", "Info");
    }
    
    private void showModernNotification(String message, String type) {
        Color bgColor = SUCCESS_COLOR;
        if ("Error".equals(type)) {
            bgColor = DANGER_COLOR;
        } else if ("Warning".equals(type)) {
            bgColor = WARNING_COLOR;
        } else if ("Info".equals(type)) {
            bgColor = INFO_COLOR;
        }
        
        JOptionPane optionPane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = optionPane.createDialog(this, type);
        dialog.setVisible(true);
    }
    
    private JButton createHeaderButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(WHITE);
        button.setBackground(bgColor);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
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
    
    private void loadAllData() {
        // Refresh all panels
        repaint();
    }
    
    // Book management helper methods
    private void refreshBooksTable() {
        // Find and refresh the books table
        switchPanel("books");
    }
    
    private void showEditBookDialog(int bookId) {
        try {
            // Get book details from database
            Book book = bookDAO.getBookById(bookId);
            if (book == null) {
                showModernNotification("Book not found!", "Error");
                return;
            }
            
            JDialog dialog = new JDialog(this, "Edit Book", true);
            dialog.setSize(500, 400);
            dialog.setLocationRelativeTo(this);
            
            // Main panel with padding
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            mainPanel.setBackground(WHITE);
            
            // Title
            JLabel titleLabel = new JLabel("✏️ Edit Book");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            titleLabel.setForeground(PRIMARY_COLOR);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
            
            // Form panel
            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBackground(WHITE);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            
            // Title field
            gbc.gridx = 0; gbc.gridy = 0;
            formPanel.add(new JLabel("Title:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            JTextField titleField = new JTextField(book.getTitle(), 20);
            titleField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            formPanel.add(titleField, gbc);
            
            // Author field
            gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            formPanel.add(new JLabel("Author:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            JTextField authorField = new JTextField(book.getAuthor(), 20);
            authorField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            formPanel.add(authorField, gbc);
            
            // Year field
            gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            formPanel.add(new JLabel("Year:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            JTextField yearField = new JTextField(String.valueOf(book.getYear()), 20);
            yearField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            formPanel.add(yearField, gbc);
            
            // Quantity field
            gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            formPanel.add(new JLabel("Quantity:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            JTextField quantityField = new JTextField(String.valueOf(book.getQuantity()), 20);
            quantityField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            formPanel.add(quantityField, gbc);
            
            // Buttons panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBackground(WHITE);
            
            JButton saveButton = createModernButton("💾 Update Book", SUCCESS_COLOR);
            JButton cancelButton = createModernButton("❌ Cancel", DANGER_COLOR);
            
            saveButton.addActionListener(e -> {
                try {
                    String title = titleField.getText().trim();
                    String author = authorField.getText().trim();
                    String yearStr = yearField.getText().trim();
                    String quantityStr = quantityField.getText().trim();
                    
                    if (title.isEmpty() || author.isEmpty() || yearStr.isEmpty() || quantityStr.isEmpty()) {
                        showModernNotification("Please fill all fields!", "Error");
                        return;
                    }
                    
                    int year = Integer.parseInt(yearStr);
                    int quantity = Integer.parseInt(quantityStr);
                    
                    // Update book object
                    book.setTitle(title);
                    book.setAuthor(author);
                    book.setYear(year);
                    book.setQuantity(quantity);
                    
                    bookDAO.updateBook(book);
                    
                    // Refresh the books table
                    refreshBooksTable();
                    
                    dialog.dispose();
                    showModernNotification("Book updated successfully!", "Success");
                    
                } catch (NumberFormatException ex) {
                    showModernNotification("Year and Quantity must be numbers!", "Error");
                } catch (SQLException ex) {
                    showModernNotification("Error updating book: " + ex.getMessage(), "Error");
                }
            });
            
            cancelButton.addActionListener(e -> dialog.dispose());
            
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            
            // Layout
            mainPanel.add(titleLabel, BorderLayout.NORTH);
            mainPanel.add(formPanel, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            dialog.add(mainPanel);
            dialog.setVisible(true);
            
        } catch (SQLException e) {
            showModernNotification("Error loading book details: " + e.getMessage(), "Error");
        }
    }
    
    private void deleteBook(int bookId, String bookTitle) {
        try {
            // Validation 1: Check if book has available stock > 0
            Book book = bookDAO.getBookById(bookId);
            if (book == null) {
                showModernNotification("Book not found!", "Error");
                return;
            }
            
            if (book.getQuantity() > 0) {
                showModernNotification("This book still has copies available. Please remove all copies before deleting.", "Warning");
                return;
            }
            
            // Validation 2: Check if book is currently issued
            List<Issue> currentIssues = issueDAO.getCurrentIssues();
            for (Issue issue : currentIssues) {
                if (issue.getBookId() == bookId) {
                    showModernNotification("This book is currently issued to a member. Wait until all copies are returned before deleting.", "Warning");
                    return;
                }
            }
            
            // All validations passed, show confirmation dialog
            int option = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete the book:\n\"" + bookTitle + "\"?\n\nThis action cannot be undone!",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (option == JOptionPane.YES_OPTION) {
                bookDAO.deleteBook(bookId);
                refreshBooksTable();
                showModernNotification("Book deleted successfully!", "Success");
            }
        } catch (SQLException e) {
            showModernNotification("Error validating book deletion: " + e.getMessage(), "Error");
        }
    }
    
    // Member management helper methods
    private void refreshMembersTable() {
        // Find and refresh the members table
        switchPanel("members");
    }
    
    private void showEditMemberDialog(int memberId) {
        try {
            // Get member details from database
            Member member = memberDAO.getMemberById(memberId);
            if (member == null) {
                showModernNotification("Member not found!", "Error");
                return;
            }
            
            JDialog dialog = new JDialog(this, "Edit Member", true);
            dialog.setSize(600, 500);
            dialog.setLocationRelativeTo(this);
            
            // Main panel with padding
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            mainPanel.setBackground(WHITE);
            
            // Title
            JLabel titleLabel = new JLabel("✏️ Edit Member");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            titleLabel.setForeground(SUCCESS_COLOR);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
            
            // Form panel
            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBackground(WHITE);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            
            // Name field
            gbc.gridx = 0; gbc.gridy = 0;
            formPanel.add(new JLabel("Name:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            JTextField nameField = new JTextField(member.getName(), 20);
            nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            formPanel.add(nameField, gbc);
            
            // Email field
            gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            formPanel.add(new JLabel("Email:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            JTextField emailField = new JTextField(member.getEmail(), 20);
            emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            formPanel.add(emailField, gbc);
            
            // Phone field
            gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            formPanel.add(new JLabel("Phone:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            JTextField phoneField = new JTextField(member.getPhone(), 20);
            phoneField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            formPanel.add(phoneField, gbc);
            
            // Address field
            gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            formPanel.add(new JLabel("Address:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            JTextField addressField = new JTextField(member.getAddress(), 20);
            addressField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            formPanel.add(addressField, gbc);
            
            // Member Type field
            gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            formPanel.add(new JLabel("Member Type:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            JComboBox<String> typeCombo = new JComboBox<>(new String[]{"STUDENT", "TEACHER", "STAFF"});
            typeCombo.setSelectedItem(member.getMemberType());
            typeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            formPanel.add(typeCombo, gbc);
            
            // Status field
            gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            formPanel.add(new JLabel("Status:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            JComboBox<String> statusCombo = new JComboBox<>(new String[]{"ACTIVE", "SUSPENDED", "INACTIVE"});
            statusCombo.setSelectedItem(member.getStatus());
            statusCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            formPanel.add(statusCombo, gbc);
            
            // Buttons panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBackground(WHITE);
            
            JButton saveButton = createModernButton("💾 Update Member", SUCCESS_COLOR);
            JButton cancelButton = createModernButton("❌ Cancel", DANGER_COLOR);
            
            saveButton.addActionListener(e -> {
                try {
                    String name = nameField.getText().trim();
                    String email = emailField.getText().trim();
                    String phone = phoneField.getText().trim();
                    String address = addressField.getText().trim();
                    String memberType = (String) typeCombo.getSelectedItem();
                    String status = (String) statusCombo.getSelectedItem();
                    
                    if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                        showModernNotification("Please fill all fields!", "Error");
                        return;
                    }
                    
                    // Update member object
                    member.setName(name);
                    member.setEmail(email);
                    member.setPhone(phone);
                    member.setAddress(address);
                    member.setMemberType(memberType);
                    member.setStatus(status);
                    
                    memberDAO.updateMember(member);
                    
                    // Refresh the members table
                    refreshMembersTable();
                    
                    dialog.dispose();
                    showModernNotification("Member updated successfully!", "Success");
                    
                } catch (SQLException ex) {
                    showModernNotification("Error updating member: " + ex.getMessage(), "Error");
                }
            });
            
            cancelButton.addActionListener(e -> dialog.dispose());
            
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            
            // Layout
            mainPanel.add(titleLabel, BorderLayout.NORTH);
            mainPanel.add(formPanel, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            dialog.add(mainPanel);
            dialog.setVisible(true);
            
        } catch (SQLException e) {
            showModernNotification("Error loading member details: " + e.getMessage(), "Error");
        }
    }
    
    private void deleteMember(int memberId, String memberName) {
        try {
            // Validation 1: Check if member has unreturned books
            List<Issue> currentIssues = issueDAO.getCurrentIssues();
            for (Issue issue : currentIssues) {
                if (issue.getMemberId() == memberId) {
                    showModernNotification("This member still has unreturned books. Please process all returns before deleting.", "Warning");
                    return;
                }
            }
            
            // Validation 2: Check if member has unpaid fines
            List<Issue> allIssues = issueDAO.getAllIssues();
            double totalFines = 0.0;
            for (Issue issue : allIssues) {
                if (issue.getMemberId() == memberId && issue.getFineAmount() > 0) {
                    totalFines += issue.getFineAmount();
                }
            }
            
            if (totalFines > 0) {
                showModernNotification("This member has unpaid fines totaling Rs " + String.format("%.2f", totalFines) + 
                    ". Please clear fines before deleting.", "Warning");
                return;
            }
            
            // All validations passed, show confirmation dialog
            int option = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete the member:\n\"" + memberName + "\"?\n\nThis action cannot be undone!",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (option == JOptionPane.YES_OPTION) {
                memberDAO.deleteMember(memberId);
                refreshMembersTable();
                showModernNotification("Member deleted successfully!", "Success");
            }
        } catch (SQLException e) {
            showModernNotification("Error validating member deletion: " + e.getMessage(), "Error");
        }
    }
}