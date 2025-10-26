import java.sql.*;
import java.time.LocalDate;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class IssueDAO {
    
    // Helper method to safely parse dates from database
    private LocalDate safeParseDateString(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        
        try {
            // Try to parse as timestamp (milliseconds)
            if (dateStr.matches("\\d+")) {
                long timestamp = Long.parseLong(dateStr);
                return Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate();
            }
            
            // Try to parse as date string (YYYY-MM-DD)
            return LocalDate.parse(dateStr);
            
        } catch (Exception e) {
            // If all parsing fails, return null for return_date or current date for others
            System.err.println("Warning: Could not parse date '" + dateStr + "', returning null");
            return null;
        }
    }
    
    // Helper method for issue dates with default fallback
    private LocalDate safeParseIssueDate(String dateStr) {
        LocalDate result = safeParseDateString(dateStr);
        return result != null ? result : LocalDate.now();
    }
    
    // Helper method for due dates with default fallback
    private LocalDate safeParseDueDate(String dateStr) {
        LocalDate result = safeParseDateString(dateStr);
        return result != null ? result : LocalDate.now().plusDays(14);
    }
    
    public void issueBook(Issue issue) throws SQLException {
        String sql = "INSERT INTO issues(book_id, member_id, due_date) VALUES (?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, issue.getBookId());
            ps.setInt(2, issue.getMemberId());
            ps.setDate(3, Date.valueOf(issue.getDueDate()));
            ps.executeUpdate();
        }
    }
    
    public void issueBookWithDates(Issue issue, LocalDate issueDate) throws SQLException {
        String sql = "INSERT INTO issues(book_id, member_id, issue_date, due_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, issue.getBookId());
            ps.setInt(2, issue.getMemberId());
            ps.setDate(3, Date.valueOf(issueDate));
            ps.setDate(4, Date.valueOf(issue.getDueDate()));
            ps.executeUpdate();
        }
    }
    
    public void returnBook(int issueId, double fineAmount) throws SQLException {
        String sql = "UPDATE issues SET return_date=?, status='RETURNED', fine_amount=? WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(LocalDate.now()));
            ps.setDouble(2, fineAmount);
            ps.setInt(3, issueId);
            ps.executeUpdate();
        }
    }
    
    public List<Issue> getAllIssues() throws SQLException {
        List<Issue> issues = new ArrayList<>();
        String sql = "SELECT i.*, b.title as book_title, m.name as member_name " +
                    "FROM issues i " +
                    "JOIN books b ON i.book_id = b.id " +
                    "JOIN members m ON i.member_id = m.id " +
                    "ORDER BY i.id DESC";
        
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Issue issue = new Issue(
                    rs.getInt("id"),
                    rs.getInt("book_id"),
                    rs.getInt("member_id"),
                    safeParseIssueDate(rs.getString("issue_date")),
                    safeParseDateString(rs.getString("return_date")), // can be null
                    safeParseDueDate(rs.getString("due_date")),
                    rs.getString("status"),
                    rs.getDouble("fine_amount")
                );
                issue.setBookTitle(rs.getString("book_title"));
                issue.setMemberName(rs.getString("member_name"));
                issues.add(issue);
            }
        }
        return issues;
    }
    
    public List<Issue> getCurrentIssues() throws SQLException {
        List<Issue> issues = new ArrayList<>();
        String sql = "SELECT i.*, b.title as book_title, m.name as member_name " +
                    "FROM issues i " +
                    "JOIN books b ON i.book_id = b.id " +
                    "JOIN members m ON i.member_id = m.id " +
                    "WHERE i.status = 'ISSUED' " +
                    "ORDER BY i.due_date ASC";
        
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Issue issue = new Issue(
                    rs.getInt("id"),
                    rs.getInt("book_id"),
                    rs.getInt("member_id"),
                    safeParseIssueDate(rs.getString("issue_date")),
                    safeParseDateString(rs.getString("return_date")), // can be null
                    safeParseDueDate(rs.getString("due_date")),
                    rs.getString("status"),
                    rs.getDouble("fine_amount")
                );
                issue.setBookTitle(rs.getString("book_title"));
                issue.setMemberName(rs.getString("member_name"));
                issues.add(issue);
            }
        }
        return issues;
    }
    
    public List<Issue> getOverdueIssues() throws SQLException {
        List<Issue> issues = new ArrayList<>();
        String sql = "SELECT i.*, b.title as book_title, m.name as member_name " +
                    "FROM issues i " +
                    "JOIN books b ON i.book_id = b.id " +
                    "JOIN members m ON i.member_id = m.id " +
                    "WHERE i.status = 'ISSUED' AND i.due_date < date('now') " +
                    "ORDER BY i.due_date ASC";
        
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Issue issue = new Issue(
                    rs.getInt("id"),
                    rs.getInt("book_id"),
                    rs.getInt("member_id"),
                    safeParseIssueDate(rs.getString("issue_date")),
                    safeParseDateString(rs.getString("return_date")), // can be null
                    safeParseDueDate(rs.getString("due_date")),
                    rs.getString("status"),
                    rs.getDouble("fine_amount")
                );
                issue.setBookTitle(rs.getString("book_title"));
                issue.setMemberName(rs.getString("member_name"));
                issues.add(issue);
            }
        }
        return issues;
    }
    
    public boolean isBookAvailable(int bookId) throws SQLException {
        String sql = "SELECT b.quantity, " +
                    "(SELECT COUNT(*) FROM issues WHERE book_id = ? AND status = 'ISSUED') as issued_count " +
                    "FROM books b WHERE b.id = ?";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ps.setInt(2, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int quantity = rs.getInt("quantity");
                    int issuedCount = rs.getInt("issued_count");
                    return quantity > issuedCount;
                }
            }
        }
        return false;
    }
    
    public double calculateFine(Issue issue) {
        if (issue.isOverdue()) {
            long daysOverdue = issue.getDaysOverdue();
            return daysOverdue * 1.0; // $1 per day fine
        }
        return 0.0;
    }
    
    public Issue getIssueById(int issueId) throws SQLException {
        String sql = "SELECT i.*, b.title as book_title, m.name as member_name " +
                    "FROM issues i " +
                    "JOIN books b ON i.book_id = b.id " +
                    "JOIN members m ON i.member_id = m.id " +
                    "WHERE i.id = ?";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, issueId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Issue issue = new Issue(
                        rs.getInt("id"),
                        rs.getInt("book_id"),
                        rs.getInt("member_id"),
                        safeParseIssueDate(rs.getString("issue_date")),
                        safeParseDateString(rs.getString("return_date")), // can be null
                        safeParseDueDate(rs.getString("due_date")),
                        rs.getString("status"),
                        rs.getDouble("fine_amount")
                    );
                    issue.setBookTitle(rs.getString("book_title"));
                    issue.setMemberName(rs.getString("member_name"));
                    return issue;
                }
            }
        }
        return null; // Issue not found
    }
}
