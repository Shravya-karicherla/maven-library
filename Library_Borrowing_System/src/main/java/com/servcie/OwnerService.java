package com.servcie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.mindrot.jbcrypt.BCrypt;

import com.db.DbConnection;
import com.model.Response;
import com.validations.Commonvalidations;

public class OwnerService {

    private Connection conn; // Database connection object

    // Constructor to establish a DB connection when OwnerService is instantiated
    public OwnerService() {
        this.conn = DbConnection.getConnection();
    }

    // Method to close the database connection (cleanup)
    public void close() {
        DbConnection.closeConnection(); // Closes the shared connection
    }

    // Shared response object to store and return the status of operations
    Response response = new Response();

    /**
     * Method to validate the owner's login credentials
     * 
     * @param session The current HTTP session to store user details upon login
     * @param mail  The owner's email address (used as the username)
     * @param password The password entered by the owner
     * @return boolean True if login is successful, false otherwise
     * @throws Exception Handles exceptions related to the SQL query
     */
    public Response Loginowner(HttpSession session, String mail, String password) {
        String sql = "SELECT password, role FROM Users WHERE mail = ? AND role = 'owner'";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, mail);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String dbpass = rs.getString("password");
                    if (BCrypt.checkpw(password, dbpass)) {
                        session.setAttribute("mail", mail);
                        session.setAttribute("password", password);
                        session.setAttribute("role", "owner");
                        return new Response(true, "Success", "Login successful as owner");
                    }
                }
            }
        } catch (SQLException e) {
            return new Response(false, "SQL Error", "something went wrong");
        } catch (Exception e) {
            return new Response(false, "Error", "something went wrong");
        }
        return new Response(false, "Error", "Invalid credentials for owner");
    }

    /**
     * Method to add a new book to the database
     * 
     * @param title The title of the book
     * @param author The author's name
     * @param availableQuantity Quantity of books to be added
     * @return Response An object containing the operation's status and message
     */
    public Response addBook(String title, String author, double availableQuantity) {
        
        // Validate the length of the title (max 45 characters)
        if (title.length() > 45) {
            response.setStatus(false);
            response.setResponseType("Error");
            response.setResponse("Title exceeds the maximum allowed length of 45 characters.");
            return response;
        }
        
        // Validate the length of the author's name (max 45 characters)
        if (author.length() > 45) {
            response.setStatus(false);
            response.setResponseType("Error");
            response.setResponse("Author exceeds the maximum allowed length of 45 characters.");
            return response;
        }

        // Validate author's name for correctness (non-empty and alphabets only)
        if (!Commonvalidations.isValidName(author) || author == null || author.trim().isEmpty()) {
            response.setStatus(false);
            response.setResponseType("Error");
            response.setResponse("Author name is invalid! Only alphabets are allowed.");
            return response;
        }

        // Validate book title for correctness (non-empty, alphanumeric only)
        if (!Commonvalidations.isValidTitle(title) || title == null || title.trim().isEmpty()) {
            response.setStatus(false);
            response.setResponseType("Error");
            response.setResponse("Title is invalid! Only alphabets and digits are allowed.");
            return response;
        }

        // Ensure the quantity is positive
        if (availableQuantity <= 0) {
            response.setStatus(false);
            response.setResponseType("Error");
            response.setResponse("Quantity must be greater than 0.");
            return response;
        }

        // Ensure the quantity does not exceed the upper limit
        if (availableQuantity > 1000) {
            response.setStatus(false);
            response.setResponseType("Error");
            response.setResponse("Quantity cannot be greater than 1000.");
            return response;
        }

        try {
            // Check if a book with the same title and author already exists
            Response existsResponse = checkTitleAuthorExists(title, author);
            if (!existsResponse.isStatus()) {
                return existsResponse;
            }

            // Insert the new book details into the database
            String sql = "INSERT INTO BOOKS(title, author, available_quantity) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, title);
                ps.setString(2, author);
                ps.setDouble(3, availableQuantity);
                ps.executeUpdate(); // Execute the insertion
            }

            // Return success response upon successful insertion
            response.setStatus(true);
            response.setResponseType("Success");
            response.setResponse("Book added successfully!");
        } catch (SQLException e) {
            // Handle any database exceptions
            response.setStatus(false);
            response.setResponseType("Error");
            response.setResponse("something went wrong");
        }

        return response; // Return the final response object
    }

 // Updated method to return Response instead of throwing an Exception
    private Response checkTitleAuthorExists(String title, String author) {
        String checkSql = "SELECT COUNT(*) FROM BOOKS WHERE title = ? and author=?";
        try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
            // Set title and author parameters in the query
            checkPs.setString(1, title);
            checkPs.setString(2, author);

            // Execute the query and check the results
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // If a matching record exists, return an error response
                    response.setStatus(false);
                    response.setResponseType("Error");
                    response.setResponse("Book with the title and author already exists.");
                    return response;
                }
            }
            // If no matching record exists, return a success response
            response.setStatus(true);
        } catch (SQLException e) {
            // Handle any database errors
            response.setStatus(false);
            response.setResponseType("Error");
            response.setResponse("something went wrong");
        }
        return response;
    }

    // Method to update book details
    public Response updateBook(int bookId, String title, String author, double availableQuantity) {
        // Validate the length of the title and author
        if (title.length() > 45 ) {
            response.setStatus(false);
            response.setResponseType("Error");
            response.setResponse("Title exceeds the maximum allowed length of 45 characters.");
            return response;
        }
        if (author.length() > 45 ) {
            response.setStatus(false);
            response.setResponseType("Error");
            response.setResponse("Author exceeds the maximum allowed length of 45 characters.");
            return response;
        }

        // Validate author and title correctness
        if (!Commonvalidations.isValidName(author) || author == null || author.trim().isEmpty()) {
            response.setStatus(false);
            response.setResponseType("Error");
            response.setResponse("Author name is invalid! Only alphabets are allowed.");
            return response;
        }
        if (!Commonvalidations.isValidTitle(title) || title == null || title.trim().isEmpty()) {
            response.setStatus(false);
            response.setResponseType("Error");
            response.setResponse("Title is invalid! Only alphabets and digits are allowed.");
            return response;
        }

        // Validate available quantity
        if (availableQuantity < 0) {
            response.setStatus(false);
            response.setResponseType("Error");
            response.setResponse("Quantity must be greater than or equal to 0.");
            return response;
        }
        if (availableQuantity > 1000) {
            response.setStatus(false);
            response.setResponseType("Error");
            response.setResponse("Quantity cannot be greater than 1000.");
            return response;
        }

        try {
            // Check if another book exists with the same title and author
            String checkSql = "SELECT COUNT(*) FROM BOOKS WHERE title = ? AND author = ? AND book_id != ?";
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setString(1, title);
                checkPs.setString(2, author);
                checkPs.setInt(3, bookId);

                // Execute query to check duplicates
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        response.setStatus(false);
                        response.setResponseType("Error");
                        response.setResponse("A book with the same title and author already exists. Update not allowed.");
                        return response;
                    }
                }
            }

            // Proceed with updating the book details
            String sql = "UPDATE BOOKS SET title = ?, author = ?, available_quantity = ? WHERE book_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, title);
                ps.setString(2, author);
                ps.setDouble(3, availableQuantity);
                ps.setInt(4, bookId);
                ps.executeUpdate(); // Execute the update query
            }

            // Return success response
            response.setStatus(true);
            response.setResponseType("Success");
            response.setResponse("Book updated successfully!");
        } catch (SQLException e) {
            // Handle any database errors
            response.setStatus(false);
            response.setResponseType("Error");
            response.setResponse("something went wrong");
        }

        return response;
    }

    // Method to delete a book
    public Response deleteBook(int bookId) {
        try {
            // Check if the book is currently borrowed and not returned
            String checkSql = "SELECT COUNT(*) FROM borrowers WHERE book_id = ? AND return_date IS NULL";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, bookId);

                // Execute query to check active borrowers
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        response.setStatus(false);
                        response.setResponseType("Error");
                        response.setResponse("Someone has borrowed this book and has not returned it yet. You cannot delete the book, but you can update the available quantity.");
                        return response;
                    }
                }
            }

            // Proceed with deletion if no active borrowers
            String deleteSql = "DELETE FROM books WHERE book_id = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, bookId);
                int rowsAffected = deleteStmt.executeUpdate();

                if (rowsAffected > 0) {
                    response.setStatus(true);
                    response.setResponseType("Success");
                    response.setResponse("Book deleted successfully!");
                } else {
                    response.setStatus(false);
                    response.setResponseType("Error");
                    response.setResponse("Book not found or could not be deleted.");
                }
            }
        } catch (SQLException e) {
            // Handle any database errors
            response.setStatus(false);
            response.setResponseType("Error");
            response.setResponse("something went wrong");
        }

        return response;
    }

    public Response getAllBooks() {
        List<Map<String, Object>> bookList = new ArrayList<>();
        String sql = "SELECT * FROM BOOKS"; // Corrected SQL syntax
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // Iterate through the result set and populate book details
            while (rs.next()) {
                Map<String, Object> book = new HashMap<>();
                book.put("book_id", rs.getInt("book_id")); // Fetch book ID
                book.put("title", rs.getString("title")); // Fetch book title
                book.put("author", rs.getString("author")); // Fetch author name
                book.put("available_quantity", rs.getInt("available_quantity")); // Fetch available quantity
                bookList.add(book); // Add book details to the list
            }
            return new Response(true, "Success", bookList);
        } catch (SQLException e) {
            return new Response(false, "SQL Error", "something went wrong");
        }
    }
    public Response getAllTransactions() {
        List<Map<String, Object>> transactions = new ArrayList<>();
        String sql = "SELECT T.transaction_id, T.user_id, B.book_id, T.fine, T.finestatus " +
                     "FROM Transactions T " +
                     "LEFT JOIN Borrowers B ON T.borrower_id = B.borrower_id";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Iterate through the result set and populate transaction details
            while (rs.next()) {
                Map<String, Object> transaction = new HashMap<>();
                transaction.put("transaction_id", rs.getInt("transaction_id"));
                transaction.put("user_id", rs.getInt("user_id"));
                transaction.put("book_id", rs.getInt("book_id"));
                transaction.put("fine", rs.getDouble("fine"));
                transaction.put("finestatus", rs.getString("finestatus"));
                transactions.add(transaction);
            }
            return new Response(true, "Success", transactions);
        } catch (SQLException e) {
            return new Response(false, "SQL Error", "something went wrong");
        }
    }
    public Response getAllBorrowerRecords() {
        List<Map<String, Object>> currTrans = new ArrayList<>();
        String sql = "SELECT B.borrower_id, B.user_id, B.book_id, B.borrow_date, B.due_date, B.return_date, B.fine, B.finestatus, U.name " +
                     "FROM Borrowers B " +
                     "JOIN Users U ON B.user_id = U.user_id " +
                     "ORDER BY B.borrower_id";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Iterate through the result set and populate borrower details
            while (rs.next()) {
                Map<String, Object> transaction = new HashMap<>();
                transaction.put("borrower_id", rs.getInt("borrower_id"));
                transaction.put("user_id", rs.getInt("user_id"));
                transaction.put("book_id", rs.getInt("book_id"));
                transaction.put("name", rs.getString("name"));
                transaction.put("borrow_date", rs.getDate("borrow_date"));
                transaction.put("due_date", rs.getDate("due_date"));
                transaction.put("return_date", rs.getDate("return_date"));
                transaction.put("fine", rs.getDouble("fine"));
                transaction.put("finestatus", rs.getString("finestatus"));
                currTrans.add(transaction);
            }
            return new Response(true, "Success", currTrans);
        } catch (SQLException e) {
            return new Response(false, "SQL Error","something went wrong");
        }
    }
    public Response getAllOverdueRecords() {
        List<Map<String, Object>> overdueList = new ArrayList<>();
        String sql = "SELECT t.user_id, b.title, t.borrow_date, t.due_date, t.return_date, t.fine " +
                     "FROM Borrowers t JOIN Books b ON t.book_id = b.book_id " +
                     "WHERE t.fine > 0 AND t.finestatus = 'UNPAID'";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Iterate through the result set and populate overdue record details
            while (rs.next()) {
                Map<String, Object> transaction = new HashMap<>();
                transaction.put("user_id", rs.getInt("user_id"));
                transaction.put("title", rs.getString("title"));
                transaction.put("borrow_date", rs.getDate("borrow_date"));
                transaction.put("due_date", rs.getDate("due_date"));
                transaction.put("return_date", rs.getDate("return_date"));
                transaction.put("fine", rs.getDouble("fine"));
                overdueList.add(transaction);
            }
            return new Response(true, "Success", overdueList);
        } catch (SQLException e) {
            return new Response(false, "SQL Error", "something went wrong");
        }
    }
    
}
