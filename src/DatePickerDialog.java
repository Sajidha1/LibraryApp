import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DatePickerDialog extends JDialog {
    private LocalDate selectedDate;
    private boolean confirmed;
    private DatePickerPanel calendarPanel;
    private final Color PRIMARY_COLOR = new Color(52, 152, 219);
    
    public DatePickerDialog(JFrame parent, String title, LocalDate initialDate) {
        super(parent, title, true);
        this.selectedDate = initialDate;
        this.confirmed = false;
        initializeComponents();
        setupDialog();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        
        // Calendar panel
        calendarPanel = new DatePickerPanel(selectedDate);
        calendarPanel.addActionListener(e -> selectedDate = calendarPanel.getSelectedDate());
        
        // Buttons panel
        JPanel buttonPanel = createButtonPanel();
        
        add(calendarPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton todayButton = new JButton("Today");
        styleButton(todayButton, Color.GRAY);
        todayButton.addActionListener(e -> {
            LocalDate today = LocalDate.now();
            calendarPanel.setSelectedDate(today);
            selectedDate = today;
        });
        
        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton, Color.GRAY);
        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
        
        JButton okButton = new JButton("OK");
        styleButton(okButton, PRIMARY_COLOR);
        okButton.addActionListener(e -> {
            confirmed = true;
            selectedDate = calendarPanel.getSelectedDate();
            dispose();
        });
        
        panel.add(todayButton);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(cancelButton);
        panel.add(Box.createHorizontalStrut(5));
        panel.add(okButton);
        
        return panel;
    }
    
    private void styleButton(JButton button, Color backgroundColor) {
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(backgroundColor);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private void setupDialog() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setResizable(false);
        setLocationRelativeTo(getParent());
    }
    
    public void setMinDate(LocalDate minDate) {
        calendarPanel.setMinDate(minDate);
    }
    
    public void setMaxDate(LocalDate maxDate) {
        calendarPanel.setMaxDate(maxDate);
    }
    
    public LocalDate getSelectedDate() {
        return confirmed ? selectedDate : null;
    }
    
    public static LocalDate showDialog(JFrame parent, String title, LocalDate initialDate) {
        DatePickerDialog dialog = new DatePickerDialog(parent, title, initialDate);
        dialog.setVisible(true);
        return dialog.getSelectedDate();
    }
    
    public static LocalDate showDialog(JFrame parent, String title, LocalDate initialDate, 
                                     LocalDate minDate, LocalDate maxDate) {
        DatePickerDialog dialog = new DatePickerDialog(parent, title, initialDate);
        if (minDate != null) dialog.setMinDate(minDate);
        if (maxDate != null) dialog.setMaxDate(maxDate);
        dialog.setVisible(true);
        return dialog.getSelectedDate();
    }
}