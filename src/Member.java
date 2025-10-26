import java.time.LocalDate;

public class Member {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String memberType; // STUDENT, TEACHER, STAFF
    private LocalDate joinDate;
    private String status; // ACTIVE, SUSPENDED, INACTIVE

    public Member() {}

    public Member(int id, String name, String email, String phone, String address, 
                  String memberType, LocalDate joinDate, String status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.memberType = memberType;
        this.joinDate = joinDate;
        this.status = status;
    }

    public Member(String name, String email, String phone, String address, String memberType) {
        this(0, name, email, phone, address, memberType, LocalDate.now(), "ACTIVE");
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getMemberType() { return memberType; }
    public void setMemberType(String memberType) { this.memberType = memberType; }
    
    public LocalDate getJoinDate() { return joinDate; }
    public void setJoinDate(LocalDate joinDate) { this.joinDate = joinDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    @Override
    public String toString() {
        return name + " (" + memberType + ")";
    }
}
