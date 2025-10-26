public class Book {
    private int id;
    private String title;
    private String author;
    private int year;
    private int quantity;

    public Book() {}

    public Book(int id, String title, String author, int year, int quantity) {
        this.id = id; this.title = title; this.author = author; this.year = year; this.quantity = quantity;
    }
    public Book(String title, String author, int year, int quantity) {
        this(0, title, author, year, quantity);
    }

    // getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    @Override
    public String toString() {
        return title + " by " + author + " (" + year + ")";
    }
}
