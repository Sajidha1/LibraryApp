import java.sql.*;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:library.db";
    
    static {
        // create DB file and tables if not exists
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            createTables(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static void createTables(Connection conn) throws SQLException {
        Statement st = conn.createStatement();
        
        // Books table
        String booksTable = "CREATE TABLE IF NOT EXISTS books (" +
                           "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                           "title TEXT NOT NULL," +
                           "author TEXT NOT NULL," +
                           "year INTEGER," +
                           "quantity INTEGER DEFAULT 0" +
                           ");";
        st.execute(booksTable);
        
        // Users table (for login/signup)
        String usersTable = "CREATE TABLE IF NOT EXISTS users (" +
                           "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                           "username TEXT UNIQUE NOT NULL," +
                           "password TEXT NOT NULL," +
                           "role TEXT DEFAULT 'ADMIN'" +
                           ");";
        st.execute(usersTable);
        
        // Members table (students/teachers who borrow books)
        String membersTable = "CREATE TABLE IF NOT EXISTS members (" +
                             "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                             "name TEXT NOT NULL," +
                             "email TEXT UNIQUE NOT NULL," +
                             "phone TEXT," +
                             "address TEXT," +
                             "member_type TEXT DEFAULT 'STUDENT'," +
                             "join_date DATE DEFAULT CURRENT_DATE," +
                             "status TEXT DEFAULT 'ACTIVE'" +
                             ");";
        st.execute(membersTable);
        
        // Issues table (book borrowing records)
        String issuesTable = "CREATE TABLE IF NOT EXISTS issues (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "book_id INTEGER NOT NULL," +
                            "member_id INTEGER NOT NULL," +
                            "issue_date DATE DEFAULT CURRENT_DATE," +
                            "return_date DATE," +
                            "due_date DATE NOT NULL," +
                            "status TEXT DEFAULT 'ISSUED'," +
                            "fine_amount REAL DEFAULT 0.0," +
                            "FOREIGN KEY (book_id) REFERENCES books(id)," +
                            "FOREIGN KEY (member_id) REFERENCES members(id)" +
                            ");";
        st.execute(issuesTable);
        
        // Insert default admin user if not exists
        String checkAdmin = "SELECT COUNT(*) FROM users WHERE username='admin'";
        ResultSet rs = st.executeQuery(checkAdmin);
        if (rs.next() && rs.getInt(1) == 0) {
            String insertAdmin = "INSERT INTO users (username, password, role) VALUES ('admin', 'admin', 'ADMIN')";
            st.execute(insertAdmin);
        }
        rs.close();
        
        st.close();
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}
