import java.sql.*;
import java.time.LocalDate;
import java.util.Random;

public class DataPopulator {
    
    public static void populateDatabase() {
        try {
            Connection conn = Database.getConnection();
            Statement st = conn.createStatement();
            
            // Clear existing data (except admin user)
            st.execute("DELETE FROM issues");
            st.execute("DELETE FROM books WHERE id > 0");
            st.execute("DELETE FROM members WHERE id > 0");
            
            // Add 20 Books with diverse collection
            String[][] bookData = {
                {"To Kill a Mockingbird", "Harper Lee", "1960"},
                {"1984", "George Orwell", "1949"},
                {"Pride and Prejudice", "Jane Austen", "1813"},
                {"The Great Gatsby", "F. Scott Fitzgerald", "1925"},
                {"One Hundred Years of Solitude", "Gabriel García Márquez", "1967"},
                {"The Catcher in the Rye", "J.D. Salinger", "1951"},
                {"Lord of the Flies", "William Golding", "1954"},
                {"The Kite Runner", "Khaled Hosseini", "2003"},
                {"Life of Pi", "Yann Martel", "2001"},
                {"The Alchemist", "Paulo Coelho", "1988"},
                {"Harry Potter and the Philosopher Stone", "J.K. Rowling", "1997"},
                {"The Hobbit", "J.R.R. Tolkien", "1937"},
                {"Brave New World", "Aldous Huxley", "1932"},
                {"Animal Farm", "George Orwell", "1945"},
                {"The Chronicles of Narnia", "C.S. Lewis", "1950"},
                {"Dune", "Frank Herbert", "1965"},
                {"The Handmaid Tale", "Margaret Atwood", "1985"},
                {"Beloved", "Toni Morrison", "1987"},
                {"The God of Small Things", "Arundhati Roy", "1997"},
                {"Midnight Children", "Salman Rushdie", "1981"}
            };
            
            String bookSQL = "INSERT INTO books (title, author, year, quantity) VALUES (?, ?, ?, ?)";
            PreparedStatement bookStmt = conn.prepareStatement(bookSQL);
            
            for (int i = 0; i < bookData.length; i++) {
                int quantity = 3 + (i % 4); // 3-6 copies per book
                bookStmt.setString(1, bookData[i][0]);
                bookStmt.setString(2, bookData[i][1]);
                bookStmt.setInt(3, Integer.parseInt(bookData[i][2]));
                bookStmt.setInt(4, quantity);
                bookStmt.executeUpdate();
            }
            bookStmt.close();
            
            // Add 20 South Asian Members with authentic names
            String[] firstNames = {
                "Sajidha", "Priya", "Ahmed", "Kamala", "Ravi", "Fatima", "Suresh", "Amara", "Hassan", "Deepika",
                "Arun", "Zara", "Vikram", "Samira", "Rajesh", "Nisha", "Omar", "Kavitha", "Arjun", "Layla"
            };
            
            String[] lastNames = {
                "Fahim", "Wickramasinghe", "Rahman", "Perera", "Kumar", "Ali", "Sharma", "Fernando", "Khan", "Jayawardena",
                "Patel", "Ibrahim", "Singh", "De Silva", "Ahmed", "Ranasinghe", "Hassan", "Gunawardena", "Shah", "Mendis"
            };
            
            String[] memberTypes = {"STUDENT", "FACULTY", "PUBLIC"};
            
            String memberSQL = "INSERT INTO members (name, email, phone, address, member_type) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement memberStmt = conn.prepareStatement(memberSQL);
            
            for (int i = 0; i < 20; i++) {
                String name = firstNames[i] + " " + lastNames[i];
                // Create email in requested format: sajidhafajim@gmail.com
                String email = firstNames[i].toLowerCase() + lastNames[i].toLowerCase() + "@gmail.com";
                String phone = "077" + String.format("%07d", 1000000 + i);
                String address = (i + 1) + "/A, Main Street, Colombo " + String.format("%02d", (i % 15) + 1);
                String memberType = memberTypes[i % memberTypes.length];
                
                memberStmt.setString(1, name);
                memberStmt.setString(2, email);
                memberStmt.setString(3, phone);
                memberStmt.setString(4, address);
                memberStmt.setString(5, memberType);
                memberStmt.executeUpdate();
            }
            memberStmt.close();
            
            // Add Current Issues (12 books currently issued)
            LocalDate currentDate = LocalDate.now();
            Random random = new Random();
            
            String currentIssueSQL = "INSERT INTO issues (book_id, member_id, issue_date, due_date, status) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement currentIssueStmt = conn.prepareStatement(currentIssueSQL);
            
            for (int i = 1; i <= 12; i++) {
                int bookId = i;
                int memberId = i;
                LocalDate issueDate = currentDate.minusDays(random.nextInt(10) + 1); // Issued 1-10 days ago
                LocalDate dueDate = issueDate.plusDays(14); // 14 days loan period
                
                currentIssueStmt.setInt(1, bookId);
                currentIssueStmt.setInt(2, memberId);
                currentIssueStmt.setString(3, issueDate.toString());
                currentIssueStmt.setString(4, dueDate.toString());
                currentIssueStmt.setString(5, "ISSUED");
                currentIssueStmt.executeUpdate();
            }
            currentIssueStmt.close();
            
            // Add Overdue Issues (6 books overdue with varying periods)
            String overdueIssueSQL = "INSERT INTO issues (book_id, member_id, issue_date, due_date, status) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement overdueIssueStmt = conn.prepareStatement(overdueIssueSQL);
            
            for (int i = 13; i <= 18; i++) {
                int bookId = i;
                int memberId = i;
                LocalDate issueDate = currentDate.minusDays(random.nextInt(25) + 15); // Issued 15-40 days ago
                LocalDate dueDate = issueDate.plusDays(14); // 14 days loan period (now overdue)
                
                overdueIssueStmt.setInt(1, bookId);
                overdueIssueStmt.setInt(2, memberId);
                overdueIssueStmt.setString(3, issueDate.toString());
                overdueIssueStmt.setString(4, dueDate.toString());
                overdueIssueStmt.setString(5, "ISSUED");
                overdueIssueStmt.executeUpdate();
            }
            overdueIssueStmt.close();
            
            // Add some returned books for recent activity (with varying fine amounts for late returns)
            String returnSQL = "INSERT INTO issues (book_id, member_id, issue_date, due_date, return_date, status, fine_amount) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement returnStmt = conn.prepareStatement(returnSQL);
            
            for (int i = 1; i <= 5; i++) {
                int bookId = 19 + (i % 2); // Use books 19-20
                int memberId = 19 + (i % 2); // Use members 19-20
                LocalDate issueDate = currentDate.minusDays(random.nextInt(10) + 20); // Issued 20-30 days ago
                LocalDate dueDate = issueDate.plusDays(14);
                LocalDate returnDate = currentDate.minusDays(random.nextInt(5)); // Returned 0-5 days ago
                
                // Calculate fine if returned late
                double fineAmount = 0.0;
                if (returnDate.isAfter(dueDate)) {
                    long daysLate = java.time.temporal.ChronoUnit.DAYS.between(dueDate, returnDate);
                    fineAmount = daysLate * 10.0; // Rs 10 per day fine
                }
                
                returnStmt.setInt(1, bookId);
                returnStmt.setInt(2, memberId);
                returnStmt.setString(3, issueDate.toString());
                returnStmt.setString(4, dueDate.toString());
                returnStmt.setString(5, returnDate.toString());
                returnStmt.setString(6, "RETURNED");
                returnStmt.setDouble(7, fineAmount);
                returnStmt.executeUpdate();
            }
            returnStmt.close();
            
            st.close();
            conn.close();
            
            System.out.println("✅ Database populated successfully with South Asian data!");
            System.out.println("📚 Added 20 diverse books");
            System.out.println("👥 Added 20 South Asian members (format: sajidhafajim@gmail.com)");
            System.out.println("📋 Added 12 current issues");
            System.out.println("⚠️ Added 6 overdue books");
            System.out.println("🔄 Added 5 returned books with fine calculations");
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("❌ Error populating database: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        try {
            // Initialize database connection (this will trigger table creation via static block)
            Connection testConn = Database.getConnection();
            testConn.close();
            
            // Populate with sample data
            populateDatabase();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}