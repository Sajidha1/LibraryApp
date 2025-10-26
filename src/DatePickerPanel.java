import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DatePickerPanel extends JPanel {
    private LocalDate selectedDate;
    private LocalDate minDate;
    private LocalDate maxDate;
    private JLabel monthYearLabel;
    private JPanel calendarPanel;
    private List<ActionListener> listeners;
    private final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private final Color LIGHT_BLUE = new Color(174, 214, 241);
    private final Color LIGHT_GRAY = new Color(236, 240, 241);
    private final Color DARK_GRAY = new Color(52, 73, 94);
    
    public DatePickerPanel() {
        this(LocalDate.now());
    }
    
    public DatePickerPanel(LocalDate initialDate) {
        this.selectedDate = initialDate;
        this.listeners = new ArrayList<>();
        this.minDate = null; // No minimum by default
        this.maxDate = null; // No maximum by default
        initializeComponents();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 2));
        
        // Header with month/year and navigation
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Days of week header
        JPanel daysHeaderPanel = createDaysHeaderPanel();
        add(daysHeaderPanel, BorderLayout.CENTER);
        
        // Calendar grid
        calendarPanel = new JPanel(new GridLayout(6, 7, 1, 1));
        calendarPanel.setBackground(Color.WHITE);
        
        JPanel calendarContainer = new JPanel(new BorderLayout());
        calendarContainer.add(daysHeaderPanel, BorderLayout.NORTH);
        calendarContainer.add(calendarPanel, BorderLayout.CENTER);
        add(calendarContainer, BorderLayout.CENTER);
        
        updateCalendar();
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Previous month button
        JButton prevButton = new JButton("◀");
        styleNavigationButton(prevButton);
        prevButton.addActionListener(e -> {
            selectedDate = selectedDate.minusMonths(1);
            updateCalendar();
        });
        
        // Next month button
        JButton nextButton = new JButton("▶");
        styleNavigationButton(nextButton);
        nextButton.addActionListener(e -> {
            selectedDate = selectedDate.plusMonths(1);
            updateCalendar();
        });
        
        // Month/Year label
        monthYearLabel = new JLabel();
        monthYearLabel.setFont(new Font("Arial", Font.BOLD, 16));
        monthYearLabel.setForeground(Color.WHITE);
        monthYearLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        headerPanel.add(prevButton, BorderLayout.WEST);
        headerPanel.add(monthYearLabel, BorderLayout.CENTER);
        headerPanel.add(nextButton, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private void styleNavigationButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private JPanel createDaysHeaderPanel() {
        JPanel daysPanel = new JPanel(new GridLayout(1, 7));
        daysPanel.setBackground(LIGHT_BLUE);
        
        String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String day : dayNames) {
            JLabel dayLabel = new JLabel(day);
            dayLabel.setFont(new Font("Arial", Font.BOLD, 12));
            dayLabel.setHorizontalAlignment(SwingConstants.CENTER);
            dayLabel.setForeground(DARK_GRAY);
            dayLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
            daysPanel.add(dayLabel);
        }
        
        return daysPanel;
    }
    
    private void updateCalendar() {
        calendarPanel.removeAll();
        
        YearMonth yearMonth = YearMonth.of(selectedDate.getYear(), selectedDate.getMonth());
        monthYearLabel.setText(yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        
        // Get first day of month and calculate starting position
        LocalDate firstDay = yearMonth.atDay(1);
        int dayOfWeek = firstDay.getDayOfWeek().getValue() % 7; // Sunday = 0
        
        // Add empty cells for days before the first day of month
        for (int i = 0; i < dayOfWeek; i++) {
            calendarPanel.add(new JLabel(""));
        }
        
        // Add days of the month
        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            LocalDate date = yearMonth.atDay(day);
            JButton dayButton = createDayButton(date);
            calendarPanel.add(dayButton);
        }
        
        // Fill remaining cells
        int totalCells = calendarPanel.getComponentCount();
        for (int i = totalCells; i < 42; i++) { // 6 rows × 7 days = 42 cells
            calendarPanel.add(new JLabel(""));
        }
        
        revalidate();
        repaint();
    }
    
    private JButton createDayButton(LocalDate date) {
        JButton button = new JButton(String.valueOf(date.getDayOfMonth()));
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        button.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Check if date is selectable
        boolean isSelectable = isDateSelectable(date);
        
        if (!isSelectable) {
            button.setEnabled(false);
            button.setBackground(LIGHT_GRAY);
            button.setForeground(Color.GRAY);
        } else if (date.equals(selectedDate)) {
            // Selected date
            button.setBackground(PRIMARY_COLOR);
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Arial", Font.BOLD, 12));
        } else if (date.equals(LocalDate.now())) {
            // Today
            button.setBackground(LIGHT_BLUE);
            button.setForeground(DARK_GRAY);
        } else {
            // Regular date
            button.setBackground(Color.WHITE);
            button.setForeground(DARK_GRAY);
        }
        
        if (isSelectable) {
            button.addActionListener(e -> {
                selectedDate = date;
                updateCalendar();
                notifyListeners();
            });
        }
        
        return button;
    }
    
    private boolean isDateSelectable(LocalDate date) {
        if (minDate != null && date.isBefore(minDate)) {
            return false;
        }
        if (maxDate != null && date.isAfter(maxDate)) {
            return false;
        }
        return true;
    }
    
    public LocalDate getSelectedDate() {
        return selectedDate;
    }
    
    public void setSelectedDate(LocalDate date) {
        this.selectedDate = date;
        updateCalendar();
    }
    
    public void setMinDate(LocalDate minDate) {
        this.minDate = minDate;
        updateCalendar();
    }
    
    public void setMaxDate(LocalDate maxDate) {
        this.maxDate = maxDate;
        updateCalendar();
    }
    
    public void addActionListener(ActionListener listener) {
        listeners.add(listener);
    }
    
    private void notifyListeners() {
        ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "dateSelected");
        for (ActionListener listener : listeners) {
            listener.actionPerformed(event);
        }
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(280, 220);
    }
}