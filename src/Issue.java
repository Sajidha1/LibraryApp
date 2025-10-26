import java.time.LocalDate;

public class Issue {
    private int id;
    private int bookId;
    private int memberId;
    private String bookTitle;  // For display purposes
    private String memberName; // For display purposes
    private LocalDate issueDate;
    private LocalDate returnDate;
    private LocalDate dueDate;
    private String status; // ISSUED, RETURNED, OVERDUE
    private double fineAmount;

    public Issue() {}

    public Issue(int id, int bookId, int memberId, LocalDate issueDate, 
                 LocalDate returnDate, LocalDate dueDate, String status, double fineAmount) {
        this.id = id;
        this.bookId = bookId;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.returnDate = returnDate;
        this.dueDate = dueDate;
        this.status = status;
        this.fineAmount = fineAmount;
    }

    public Issue(int bookId, int memberId, LocalDate dueDate) {
        this(0, bookId, memberId, LocalDate.now(), null, dueDate, "ISSUED", 0.0);
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    
    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }
    
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    
    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    
    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
    
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public double getFineAmount() { return fineAmount; }
    public void setFineAmount(double fineAmount) { this.fineAmount = fineAmount; }
    
    public boolean isOverdue() {
        return LocalDate.now().isAfter(dueDate) && !"RETURNED".equals(status);
    }
    
    public long getDaysOverdue() {
        if (!isOverdue()) return 0;
        return java.time.temporal.ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }
}
