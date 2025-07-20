 
import java.util.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;

public class Library {
    private Map<String, Book> books;

    public Library() {
        books = new HashMap<>();
    }

    public boolean addBook(String title, String author) {
        if (books.containsKey(title)) {
            System.out.println("Book already exists.");
            return false;
        }
        Book book = new Book(title, author);
        books.put(title, book);
        syncWithAPI(book);
        return true;
    }

    public void displayBooks() {
        if (books.isEmpty()) {
            System.out.println("No books available.");
        } else {
            for (Book book : books.values()) {
                System.out.println(book);
            }
        }
    }

    public void showBorrowedBooks() {
        boolean found = false;
        for (Book book : books.values()) {
            if (book.isBorrowed()) {
                System.out.println(book);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No borrowed books.");
        }
    }

    public boolean borrowBook(String title) {
        Book book = books.get(title);
        if (book == null) {
            System.out.println("Book not found.");
            return false;
        }
        if (book.isBorrowed()) {
            System.out.println("Book is already borrowed.");
            return false;
        }
        book.borrow();
        syncWithAPI(book);
        return true;
    }

    public boolean returnBook(String title) {
        Book book = books.get(title);
        if (book == null) {
            System.out.println("Book not found.");
            return false;
        }
        if (!book.isBorrowed()) {
            System.out.println("Book was not borrowed.");
            return false;
        }
        book.returnBook();
        syncWithAPI(book);
        return true;
    }

    private void syncWithAPI(Book book) {
        try {
            URL url = new URL("https://example.com/api/books"); // Replace with your real API URL
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            String jsonInputString = String.format(
                "{\"title\": \"%s\", \"author\": \"%s\", \"isBorrowed\": %b}",
                book.getTitle(), book.getAuthor(), book.isBorrowed()
            );

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int code = con.getResponseCode();
            System.out.println("API Response Code: " + code);

        } catch (Exception e) {
            System.out.println("Failed to sync with API: " + e.getMessage());
        }
    }
}
