import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MemberDAO {
    
    public void addMember(Member member) throws SQLException {
        String sql = "INSERT INTO members(name, email, phone, address, member_type) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, member.getName());
            ps.setString(2, member.getEmail());
            ps.setString(3, member.getPhone());
            ps.setString(4, member.getAddress());
            ps.setString(5, member.getMemberType());
            ps.executeUpdate();
        }
    }
    
    public void updateMember(Member member) throws SQLException {
        String sql = "UPDATE members SET name=?, email=?, phone=?, address=?, member_type=?, status=? WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, member.getName());
            ps.setString(2, member.getEmail());
            ps.setString(3, member.getPhone());
            ps.setString(4, member.getAddress());
            ps.setString(5, member.getMemberType());
            ps.setString(6, member.getStatus());
            ps.setInt(7, member.getId());
            ps.executeUpdate();
        }
    }
    
    public void deleteMember(int id) throws SQLException {
        String sql = "DELETE FROM members WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
    
    public List<Member> getAllMembers() throws SQLException {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members";
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Member member = new Member(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getString("member_type"),
                    rs.getString("join_date") != null ? LocalDate.parse(rs.getString("join_date")) : LocalDate.now(),
                    rs.getString("status")
                );
                members.add(member);
            }
        }
        return members;
    }
    
    public Member getMemberById(int id) throws SQLException {
        String sql = "SELECT * FROM members WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Member(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getString("member_type"),
                        rs.getString("join_date") != null ? LocalDate.parse(rs.getString("join_date")) : LocalDate.now(),
                        rs.getString("status")
                    );
                }
            }
        }
        return null;
    }
    
    public List<Member> getActiveMembers() throws SQLException {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members WHERE status='ACTIVE' ORDER BY name";
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Member member = new Member(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getString("member_type"),
                    rs.getString("join_date") != null ? LocalDate.parse(rs.getString("join_date")) : LocalDate.now(),
                    rs.getString("status")
                );
                members.add(member);
            }
        }
        return members;
    }
}
