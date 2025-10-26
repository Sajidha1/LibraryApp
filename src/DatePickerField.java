import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DatePickerField extends JPanel {
    private LocalDate selectedDate;
    private LocalDate minDate;
    private LocalDate maxDate;
    private JTextField dateField;
    private JButton calendarButton;
    private List<ActionListener> listeners;
    private final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    public DatePickerField() {
        this(LocalDate.now());
    }
    
    public DatePickerField(LocalDate initialDate) {
        this.selectedDate = initialDate;
        this.listeners = new ArrayList<>();
        initializeComponents();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        
        // Text field to display selected date
        dateField = new JTextField();
        dateField.setFont(new Font("Arial", Font.PLAIN, 14));
        dateField.setEditable(false);
        dateField.setBackground(Color.WHITE);
        dateField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(8, 10, 8, 5)
        ));
        
        // Calendar button
        calendarButton = new JButton("📅");
        calendarButton.setFont(new Font("Arial", Font.PLAIN, 16));
        calendarButton.setBackground(PRIMARY_COLOR);
        calendarButton.setForeground(Color.WHITE);
        calendarButton.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        calendarButton.setFocusPainted(false);
        calendarButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        calendarButton.setPreferredSize(new Dimension(45, 35));
        
        calendarButton.addActionListener(e -> showDatePicker());
        
        add(dateField, BorderLayout.CENTER);
        add(calendarButton, BorderLayout.EAST);
        
        updateDisplay();
    }
    
    private void showDatePicker() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        LocalDate newDate = DatePickerDialog.showDialog(
            parentFrame, 
            "Select Date", 
            selectedDate,
            minDate,
            maxDate
        );
        
        if (newDate != null) {
            setSelectedDate(newDate);
            notifyListeners();
        }
    }
    
    private void updateDisplay() {
        if (selectedDate != null) {
            dateField.setText(selectedDate.format(formatter));
        } else {
            dateField.setText("");
        }
    }
    
    public LocalDate getSelectedDate() {
        return selectedDate;
    }
    
    public void setSelectedDate(LocalDate date) {
        this.selectedDate = date;
        updateDisplay();
    }
    
    public void setMinDate(LocalDate minDate) {
        this.minDate = minDate;
    }
    
    public void setMaxDate(LocalDate maxDate) {
        this.maxDate = maxDate;
    }
    
    public void addActionListener(ActionListener listener) {
        listeners.add(listener);
    }
    
    private void notifyListeners() {
        java.awt.event.ActionEvent event = new java.awt.event.ActionEvent(
            this, java.awt.event.ActionEvent.ACTION_PERFORMED, "dateChanged"
        );
        for (ActionListener listener : listeners) {
            listener.actionPerformed(event);
        }
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        dateField.setEnabled(enabled);
        calendarButton.setEnabled(enabled);
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(180, 35);
    }
}