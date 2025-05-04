package com.servcie;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.mindrot.jbcrypt.BCrypt;

import com.db.DbConnection;
import com.model.Response;
import com.validations.Commonvalidations;
import com.validations.CustomerValidations;

public class CustomerService {
	 private Connection conn;

	    // Constructor to fetch DB connection
	    public CustomerService() {
	        this.conn = DbConnection.getConnection();
	    }

	    // Method to close the connection (cleanup)
	    public void close() {
	        DbConnection.closeConnection();
	    }
	    Response response = new Response();
			 // Check customer login details
			    public Response Logincustomer(HttpSession session, String mail, String password) {
			        String sql = "SELECT * FROM Users WHERE mail = ? AND role = 'customer'";
			        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			            stmt.setString(1, mail);
			            try (ResultSet rs = stmt.executeQuery()) {
			                if (rs.next()) {
			                    String dbpass = rs.getString("password");
			                    if (BCrypt.checkpw(password, dbpass)) {
			                        session.setAttribute("mail", mail);
			                        session.setAttribute("password", password);
			                        session.setAttribute("name", rs.getString("name"));
			                        session.setAttribute("user_id", rs.getInt("user_id"));
			                        session.setAttribute("contact_info", rs.getString("contact_info"));
			                        session.setAttribute("role", "customer");
			                        return new Response(true, "Success", "Login successful as customer");
			                    }
			                }
			            }
			        } catch (SQLException e) {
			            return new Response(false, "SQL Error", "something went wrong");
			        } catch (Exception e) {
			            return new Response(false, "Error", "something went wrong");
			        }
			        return new Response(false, "Error", "Invalid credentials for customer");
			    }
	      
		
			    // Display pending fee for a customer when they log in
			    public Response displayPendingFeeToCustomer(Integer user_id) {
			        double totalFine = 0.0; // Initialize fine amount
			        String getFinesSQL = "SELECT SUM(fine) AS totalFine FROM Borrowers WHERE user_id = ? AND fine > 0 AND finestatus = 'UNPAID'";
			        try (PreparedStatement getFinesStmt = conn.prepareStatement(getFinesSQL)) {
			            getFinesStmt.setInt(1, user_id); // Set user ID parameter
			            try (ResultSet finesRS = getFinesStmt.executeQuery()) {
			                if (finesRS.next()) { // If fines are found
			                    totalFine = finesRS.getDouble("totalFine"); // Retrieve the total fine amount
			                }
			            }
			            // Return a successful response with the total fine amount
			            return new Response(true, "Success", totalFine);
			        } catch (SQLException e) {
			            // Handle SQL exceptions and return an error response
			            return new Response(false, "SQL Error", "Database error occurred " );
			        } catch (Exception e) {
			            // Handle any other exceptions and return an error response
			            return new Response(false, "Error", "An unexpected error occurred" );
			        }
			    }

		
			    // Register a new user
			    public Response register(String mail, String password, String contact, String name, HttpSession session) {
			    	 // Validate the length of the mail (max 45 characters)
			        if (mail.length() > 45) {
			            response.setStatus(false);
			            response.setResponseType("Error");
			            response.setResponse("Mail exceeds the maximum allowed length of 45 characters.");
			            return response;
			        }
			        // Validate the length of the name (max 45 characters)
			        if (name.length() > 45) {
			            response.setStatus(false);
			            response.setResponseType("Error");
			            response.setResponse("Name exceeds the maximum allowed length of 45 characters.");
			            return response;
			        }
			       // Validate the length of the name (min 3 characters)
			        if (name.length() < 3) {
			            response.setStatus(false);
			            response.setResponseType("Error");
			            response.setResponse("Name Must contains atleast 3 characters long.");
			            return response;
			        }
			        // Validate the user's name
			        if (!Commonvalidations.isValidName(name)) {
			            response.setStatus(false);
			            response.setResponseType("Error");
			            response.setResponse("Name is incorrect. Must contain only alphabets.");
			            return response;
			        }
		
			        // Validate the contact information
			        if (!Commonvalidations.isValidContactInfo(contact)) {
			            response.setStatus(false);
			            response.setResponseType("Error");
			            response.setResponse("Contact information is incorrect. Must contain 10 digits and start with 6-9.");
			            return response;
			        }
		
			        // Validate the email
			        if (!CustomerValidations.isValidEmail(mail)) {
			            response.setStatus(false);
			            response.setResponseType("Error");
			            response.setResponse("Email is incorrect.");
			            return response;
			        }
		
			        // Validate the password
			        if (!CustomerValidations.isValidPassword(password)) {
			            response.setStatus(false);
			            response.setResponseType("Error");
			            response.setResponse("Password incorrect. It should contain one capital letter, one digit, one special character, and be at least 8 characters long.");
			            return response;
			        }
		
			        try {
			            // Check if the email is already registered
			            String registersql = "SELECT COUNT(*) FROM users WHERE mail = ?";
			            try (PreparedStatement checkStmt = conn.prepareStatement(registersql)) {
			                checkStmt.setString(1, mail);
			                try (ResultSet rs = checkStmt.executeQuery()) {
			                    if (rs.next() && rs.getInt(1) > 0) { // If email already exists
			                        session.setAttribute("highlightMail", true);
			                        response.setStatus(false);
			                        response.setResponseType("Error");
			                        response.setResponse("Email is already registered.");
			                        return response;
			                    }
			                }
			            }
			           
			            // Check if the contact information is already registered
			            registersql = "SELECT COUNT(*) FROM users WHERE contact_info = ?";
			            try (PreparedStatement checkStmt = conn.prepareStatement(registersql)) {
			                checkStmt.setString(1, contact);
			                try (ResultSet rs = checkStmt.executeQuery()) {
			                    if (rs.next() && rs.getInt(1) > 0) { 
                                    // If contact info already exists
			                        session.setAttribute("highlightContact", true);
			                        response.setStatus(false);
			                        response.setResponseType("Error");
			                        response.setResponse("Contact information is already registered.");
			                        return response;
			                    }
			                }
			            }
		
			            // Proceed with user registration
			            String hashedPassword = hashpassword(password); // Hash the user's password
			            registersql = "INSERT INTO users (contact_info, name, mail, password, role) VALUES (?, ?, ?, ?, ?)";
			            try (PreparedStatement pstmt = conn.prepareStatement(registersql)) {
			                pstmt.setString(1, contact);
			                pstmt.setString(2, name);
			                pstmt.setString(3, mail);
			                pstmt.setString(4, hashedPassword); // Store the hashed password
			                pstmt.setString(5, "customer");
		
			                int rowsAffected = pstmt.executeUpdate();
			                if (rowsAffected > 0) { // If registration is successful
			                    response.setStatus(true);
			                    response.setResponseType("Success");
			                    response.setResponse("Registration successful!");
			                } else {
			                    response.setStatus(false);
			                    response.setResponseType("Error");
			                    response.setResponse("Registration failed. Please try again.");
			                }
			            }
			        } catch (SQLException e) {
			            // Handle database errors
			            response.setStatus(false);
			            response.setResponseType("Error");
			            response.setResponse("something went wrong");
			        }
		
			        return response; // Return the response object
			    }
		
			    // Hash the user's password
			    private String hashpassword(String password) {
			        return BCrypt.hashpw(password, BCrypt.gensalt()); // Use BCrypt to hash the password securely
			    }
	
			    // Method for borrowing a book
			    public Response borrowBook(int user_id, String borrowerName, int bookId, LocalDate borrowDate, LocalDate due_date) {
			        try {
			            // Step 1: Check if the book exists in the database
			            Response bookExistsResponse = checkBookExists(bookId);
			            if (!bookExistsResponse.isStatus()) return bookExistsResponse;

			            // Step 2: Retrieve available quantity for the book
			            String borrowbooksql = "SELECT available_quantity FROM Books WHERE book_id = ?";
			            try (PreparedStatement ps1 = conn.prepareStatement(borrowbooksql)) {
			                ps1.setInt(1, bookId); // Set book ID parameter
			                try (ResultSet rs1 = ps1.executeQuery()) {
			                    if (rs1.next()) {
			                        int availableQuantity = rs1.getInt("available_quantity"); // Fetch available quantity
			                        if (availableQuantity > 0) {

			                            // Step 3: Check if the user has already borrowed the book and not returned it
			                            Response borrowedResponse = checkIfThatBookIsBorrowedOrNot(bookId, user_id);
			                            if (!borrowedResponse.isStatus()) return borrowedResponse;

			                            // Step 4: Check if the user has outstanding fines exceeding 1000
			                            Response fineResponse = checkIfTheUserHavePendingFeeMoreThanthousand(user_id);
			                            if (!fineResponse.isStatus()) return fineResponse;

			                            // Step 5: Check for pending fines related to this book
			                            Response pendingFineResponse = checkPendingFineForBook(bookId, user_id);
			                            if (!pendingFineResponse.isStatus()) return pendingFineResponse;

			                            // Step 6: Ensure the user has not borrowed more than 3 books and failed to return them
			                            Response borrowLimitResponse = checkCustomerBorrowMoreThanThreeBooksNNotReturned(user_id);
			                            if (!borrowLimitResponse.isStatus()) return borrowLimitResponse;
			                            
			                            // Step 7: Update the available quantity of the book
			                            String updateBookSql = "UPDATE Books SET available_quantity = ? WHERE book_id = ?";			                         
			                            try (PreparedStatement ps2 = conn.prepareStatement(updateBookSql)) {		                            	
				                            availableQuantity--;
			                                ps2.setInt(1, availableQuantity); // Update quantity
			                                ps2.setInt(2, bookId);
			                                ps2.executeUpdate(); // Execute the update
			                            }

			                            // Step 8: Record the borrow transaction in the Borrowers table
			                            String insertTransactionSql = "INSERT INTO Borrowers (user_id, book_id, borrow_date, due_date) VALUES (?, ?, ?, ?)";
			                            try (PreparedStatement ps3 = conn.prepareStatement(insertTransactionSql)) {
			                                ps3.setInt(1, user_id); // Set user ID
			                                ps3.setInt(2, bookId); // Set book ID
			                                ps3.setDate(3, java.sql.Date.valueOf(borrowDate)); // Set borrow date
			                                ps3.setDate(4, java.sql.Date.valueOf(due_date)); // Set due date
			                                ps3.executeUpdate(); // Execute the insertion
			                            }

			                            // Set success response
			                            response.setStatus(true);
			                            response.setResponseType("Success");
			                            response.setResponse("Book borrowed successfully!");
			                        } else {
			                            // Handle case where book is out of stock
			                            response.setStatus(false);
			                            response.setResponseType("Error");
			                            response.setResponse("Book is out of stock.");
			                        }
			                    } else {
			                        // Handle case where book does not exist
			                        response.setStatus(false);
			                        response.setResponseType("Error");
			                        response.setResponse("Book not found.");
			                    }
			                }
			            }
			        } catch (SQLException e) {
			            // Handle SQL exceptions
			            response.setStatus(false);
			            response.setResponseType("Error");
			            response.setResponse("something went wrong");
			        }

			        return response; // Return response object
			    }

			    // Method to check if a book exists
			    private Response checkBookExists(int bookId) {
			        String checkSql = "SELECT COUNT(*) FROM Books WHERE book_id = ?";
			        try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
			            checkPs.setInt(1, bookId); // Set book ID parameter
			            ResultSet rs = checkPs.executeQuery();
			            if (rs.next() && rs.getInt(1) == 0) {
			                // Handle case where book does not exist
			                response.setStatus(false);
			                response.setResponseType("Error");
			                response.setResponse("Book with that ID does not exist.");
			            } else {
			                response.setStatus(true); // Book exists
			            }
			        } catch (SQLException e) {
			            // Handle SQL exception
			            response.setStatus(false);
			            response.setResponseType("Error");
			            response.setResponse("something went wrong");
			        }
			        return response; // Return response object
			    }

			    // Method to check if a user has already borrowed a book and not returned it
			    private Response checkIfThatBookIsBorrowedOrNot(int bookId, int user_id) throws SQLException {
			        String checkTransactionSql = "SELECT COUNT(*) FROM Borrowers WHERE user_id = ? AND book_id = ? AND return_date IS NULL";
			        try (PreparedStatement psCheck = conn.prepareStatement(checkTransactionSql)) {
			            psCheck.setInt(1, user_id); // Set user ID parameter
			            psCheck.setInt(2, bookId); // Set book ID parameter
			            ResultSet rsCheck = psCheck.executeQuery();
			            if (rsCheck.next() && rsCheck.getInt(1) > 0) {
			                // Handle case where user already borrowed the book
			                response.setStatus(false);
			                response.setResponseType("Error");
			                response.setResponse("User has already borrowed this book.");
			            } else {
			                response.setStatus(true); // User has not borrowed the book
			            }
			        }
			        catch (SQLException e) {
			            // Handle SQL exception
			            response.setStatus(false);
			            response.setResponseType("Error");
			            response.setResponse("something went wrong");
			        }
			        return response; // Return response object
			    }

			    // Method to check pending fines for a specific book
			    private Response checkPendingFineForBook(int bookId, int user_id) throws SQLException {
			        String checkSql = "SELECT fine, finestatus FROM Borrowers WHERE book_id = ? AND user_id = ? ORDER BY borrower_id DESC";
			        try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
			            checkPs.setInt(1, bookId); // Set book ID parameter
			            checkPs.setInt(2, user_id); // Set user ID parameter
			            ResultSet rs = checkPs.executeQuery();
			            if (rs.next()) {
			                double fine = rs.getDouble("fine");
			                String fineStatus = rs.getString("finestatus");
			                if (fine > 0 && "UNPAID".equalsIgnoreCase(fineStatus)) {
			                    // Handle case where pending fine exists
			                    response.setStatus(false);
			                    response.setResponseType("Error");
			                    response.setResponse("Pending fine for this book. Please clear before borrowing.");
			                } else {
			                    response.setStatus(true); // No pending fines
			                }
			            } else {
			                response.setStatus(true); // No records found
			            }
			        }catch (SQLException e) {
			            // Handle SQL exception
			            response.setStatus(false);
			            response.setResponseType("Error");
			            response.setResponse("something went wrong");
			        }
			        return response; // Return response object
			    }

			    // Method to check if a user has fines exceeding 1000
			    private Response checkIfTheUserHavePendingFeeMoreThanthousand(int user_id) throws SQLException {
			        String sql = "SELECT SUM(fine) AS total_fine FROM Borrowers WHERE user_id = ? AND fine > 0 AND finestatus = 'UNPAID'";
			        try (PreparedStatement psFine = conn.prepareStatement(sql)) {
			            psFine.setInt(1, user_id); // Set user ID parameter
			            ResultSet rsFine = psFine.executeQuery();
			            if (rsFine.next() && rsFine.getDouble("total_fine") > 1000) {
			                // Handle case where user has fines exceeding 1000
			                response.setStatus(false);
			                response.setResponseType("Error");
			                response.setResponse("User has pending fees over 1000. Clear them before borrowing.");
			            } else {
			                response.setStatus(true); // No excessive fines
			            }
			        }catch (SQLException e) {
			            // Handle SQL exception
			            response.setStatus(false);
			            response.setResponseType("Error");
			            response.setResponse("something went wrong");
			        }
			        return response; // Return response object
			    }

			    // Method to check if a user has borrowed more than 3 books and not returned them
			    private Response checkCustomerBorrowMoreThanThreeBooksNNotReturned(int user_id) throws SQLException {
			        String sql = "SELECT COUNT(*) FROM Borrowers WHERE user_id = ? AND return_date IS NULL";
			        try (PreparedStatement ps = conn.prepareStatement(sql)) {
			            ps.setInt(1, user_id); // Set user ID parameter
			            try (ResultSet rs = ps.executeQuery()) {
			                if (rs.next() && rs.getInt(1) >= 3) {
			                    // Handle case where user exceeded the borrow limit
			                    response.setStatus(false);
			                    response.setResponseType("Error");
			                    response.setResponse("User already borrowed three books and not returned them.");
			                } else {
			                    response.setStatus(true); // Borrow limit not exceeded
			                }
			            }
			        }catch (SQLException e) {
			            // Handle SQL exception
			            response.setStatus(false);
			            response.setResponseType("Error");
			            response.setResponse("something went wrong");
			        }
			        return response; // Return response object
			    }


			 // Method to fetch all borrowed books of a single customer
			    public Response borrowedbooksforsinglecustomer(int userId) {
			        List<Map<String, Object>> borrowedBooksList = new ArrayList<>(); // Initialize the list to hold borrowed books
			        String sql = "SELECT b.user_id, b.book_id, bk.title, b.borrow_date, b.due_date, b.return_date, b.fine, b.finestatus " +
			                     "FROM borrowers b " +
			                     "JOIN books bk ON b.book_id = bk.book_id " +
			                     "WHERE b.user_id = ?";

			        try (PreparedStatement ps = conn.prepareStatement(sql)) {
			            ps.setInt(1, userId); // Set the user ID parameter
			            try (ResultSet rs = ps.executeQuery()) {
			                while (rs.next()) {
			                    // Create a map to store book details
			                    Map<String, Object> borrowedBook = new HashMap<>();
			                    borrowedBook.put("user_id", rs.getInt("user_id")); // User ID
			                    borrowedBook.put("book_id", rs.getInt("book_id")); // Book ID
			                    borrowedBook.put("book_title", rs.getString("title")); // Book title
			                    borrowedBook.put("borrow_date", rs.getDate("borrow_date").toLocalDate()); // Borrow date
			                    borrowedBook.put("due_date", rs.getDate("due_date").toLocalDate()); // Due date
			                    borrowedBook.put("return_date", rs.getDate("return_date") != null ? rs.getDate("return_date").toLocalDate() : null); // Return date
			                    borrowedBook.put("fine", rs.getDouble("fine")); // Fine amount
			                    borrowedBook.put("finestatus", rs.getString("finestatus")); // Fine status
			                    borrowedBooksList.add(borrowedBook); // Add book details to the list
			                }
			            }
			            // Return a successful Response with the borrowed books list
			            return new Response(true, "Success", borrowedBooksList);
			        } catch (SQLException e) {
			            // Handle SQL exception and return an error Response
			            return new Response(false, "SQL Error", "something went wrong");
			        } catch (Exception e) {
			            // Handle any other exceptions and return an error Response
			            return new Response(false, "Error", "something went wrong");
			        }
			    }

			    // Method to fetch books with available quantity greater than zero
			    public Response getBooksQuantityGreaterThanZero() {
			        List<Map<String, Object>> books = new ArrayList<>(); // Initialize the list to hold book details
			        String sql = "SELECT * FROM books WHERE available_quantity > 0"; // SQL query to fetch books with quantity > 0
			        
			        try (PreparedStatement stmt = conn.prepareStatement(sql);
			             ResultSet rs = stmt.executeQuery()) {

			            while (rs.next()) {
			                // Create a map to store book details
			                Map<String, Object> book = new HashMap<>();
			                book.put("book_id", rs.getInt("book_id")); // Book ID
			                book.put("title", rs.getString("title")); // Book title
			                book.put("author", rs.getString("author")); // Author name
			                book.put("available_quantity", rs.getInt("available_quantity")); // Available quantity
			                books.add(book); // Add book details to the list
			            }
			            // Return a successful Response with the list of books
			            return new Response(true, "Success", books);

			        } catch (SQLException e) {
			            // Handle SQL exception and return an error Response
			            return new Response(false, "SQL Error", "something went wrong");
			        } catch (Exception e) {
			            // Handle other exceptions and return an error Response
			            return new Response(false, "Error","something went wrong");
			        }
			    }

		       
			    // Method to handle book return process
			    public Response returnBook(Integer bookId, Date returnDate, int user_id) {
			        try {
			            // Step 1: Fetch borrow record for the given book and user
			            String sql = "SELECT * FROM Borrowers WHERE book_id = ? AND user_id = ? ORDER BY borrower_id DESC";
			            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			                stmt.setInt(1, bookId); // Set book ID parameter
			                stmt.setInt(2, user_id); // Set user ID parameter
			                try (ResultSet rs = stmt.executeQuery()) {
			                    if (rs.next()) {
			                        // Check if the book is already returned
			                        Date date = rs.getDate("return_date");
			                        double fineAmount = rs.getDouble("fine");
			                        if (date != null && fineAmount > 0) {
			                            response.setStatus(false);
			                            response.setResponseType("Error");
			                            response.setResponse("Book has already been returned.");
			                            return response;
			                        }

			                        // Step 2: Validate return date (must be after borrow date)
			                        Date borrow_date = rs.getDate("borrow_date");
			                        if (returnDate.before(borrow_date)) {
			                            response.setStatus(false);
			                            response.setResponseType("Error");
			                            response.setResponse("Return date is incorrect. It cannot be before the borrow date.");
			                            return response;
			                        }

			                        // Step 3: Calculate fine for late return
			                        Date due_date = rs.getDate("due_date");
			                        double fine = calculateFine(due_date, returnDate);
			                        int borrowerId = rs.getInt("borrower_id");

			                        // Step 4: Update the borrow record with return details
			                        String updateSql = "UPDATE Borrowers SET return_date = ?, fine = ? WHERE book_id = ? AND user_id = ? AND borrower_id = ?";
			                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
			                            updateStmt.setDate(1, returnDate);
			                            updateStmt.setDouble(2, fine);
			                            updateStmt.setInt(3, bookId);
			                            updateStmt.setInt(4, user_id);
			                            updateStmt.setInt(5, borrowerId);
			                            updateStmt.executeUpdate();

			                            // Increase the book's available quantity after return
			                            increaseAvailableQuantityOfBookBy1(bookId);
			                        }

			                        // Step 5: Update fine status based on fine amount
			                        if (fine > 0) {
			                            String fineSql = "UPDATE Borrowers SET finestatus = 'UNPAID' WHERE book_id = ? AND user_id = ? AND borrower_id = ?";
			                            try (PreparedStatement fineStmt = conn.prepareStatement(fineSql)) {
			                                fineStmt.setInt(1, bookId);
			                                fineStmt.setInt(2, user_id);
			                                fineStmt.setInt(3, borrowerId);
			                                fineStmt.executeUpdate();
			                            }
			                            response.setStatus(false);
			                            response.setResponseType("Error");
			                            response.setResponse("Book returned late. Fine added: " + fine);
			                        } else {
			                            String fineStatusSql = "UPDATE Borrowers SET finestatus = 'Returned in time' WHERE book_id = ? AND user_id = ? AND borrower_id = ?";
			                            try (PreparedStatement fineStmt = conn.prepareStatement(fineStatusSql)) {
			                                fineStmt.setInt(1, bookId);
			                                fineStmt.setInt(2, user_id);
			                                fineStmt.setInt(3, borrowerId);
			                                fineStmt.executeUpdate();
			                            }
			                            response.setStatus(true);
			                            response.setResponseType("Success");
			                            response.setResponse("Book returned successfully! Returned In time No fine");
			                        }
			                    } else {
			                        // Handle case where borrow record is not found
			                        response.setStatus(false);
			                        response.setResponseType("Error");
			                        response.setResponse("Borrow record not found.");
			                    }
			                }
			            }
			        } catch (SQLException e) {
			            // Handle SQL exceptions
			            response.setStatus(false);
			            response.setResponseType("Error");
			            response.setResponse("something went wrong");
			        }

			        return response; // Return response object
			    }

			    /*
			     * Method to calculate fine if the book is returned after the due date.
			     * Late fee: ₹10 per day
			     */
			    public double calculateFine(Date dueDate, Date returnDate) {
			        if (returnDate.after(dueDate)) { // Check if return date is after due date
			            long differenceInMillis = returnDate.getTime() - dueDate.getTime();
			            long differenceInDays = differenceInMillis / (1000 * 60 * 60 * 24); // Calculate difference in days
			            return differenceInDays * 10; // ₹10 per day late fee
			        }
			        return 0; // No fine if returned on or before due date
			    }

			    /*
			     * Method to increase available quantity of a book by 1 after return
			     */
			    private Response increaseAvailableQuantityOfBookBy1(int bookId) {
			        String selectSql = "SELECT available_quantity FROM Books WHERE book_id = ?";
			        String updateSql = "UPDATE Books SET available_quantity = ? WHERE book_id = ?";

			        try (PreparedStatement psSelect = conn.prepareStatement(selectSql);
			             PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
			            psSelect.setInt(1, bookId); // Set book ID parameter for select query
			            try (ResultSet rs = psSelect.executeQuery()) {
			                if (rs.next()) {
			                    int availableQuantity = rs.getInt("available_quantity") + 1; // Increment available quantity
			                    psUpdate.setInt(1, availableQuantity);
			                    psUpdate.setInt(2, bookId);
			                    psUpdate.executeUpdate(); // Execute update query
			                    response.setStatus(true);
			                    response.setResponseType("Success");
			                    response.setResponse("Available quantity updated.");
			                } else {
			                    // Handle case where book ID is not found
			                    response.setStatus(false);
			                    response.setResponseType("Error");
			                    response.setResponse("Book with ID " + bookId + " not found.");
			                }
			            }
			        } catch (SQLException e) {
			            // Handle SQL exceptions
			            response.setStatus(false);
			            response.setResponseType("Error");
			            response.setResponse("something went wrong");
			        }

			        return response; // Return response object
			    }

			 // Method to fetch all currently borrowed books for a specific user
			    public Response getcurrentborrowedbooks(Integer user_id) {
			        List<Map<String, Object>> books = new ArrayList<>(); // Initialize list to hold borrowed books
			        String sql = "SELECT b.book_id, b.title, b.author " +
			                     "FROM borrowers br " +
			                     "JOIN books b ON br.book_id = b.book_id " +
			                     "WHERE br.user_id = ? AND br.return_date IS NULL";

			        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			            stmt.setInt(1, user_id); // Set user ID parameter
			            try (ResultSet rs = stmt.executeQuery()) {
			                while (rs.next()) {
			                    // Create a map to store book details
			                    Map<String, Object> book = new HashMap<>();
			                    book.put("book_id", rs.getInt("book_id")); // Book ID
			                    book.put("title", rs.getString("title")); // Book title
			                    book.put("author", rs.getString("author")); // Author name
			                    books.add(book); // Add book details to the list
			                }
			            }
			            // Return a successful Response with the list of currently borrowed books
			            return new Response(true, "Success", books);
			        } catch (SQLException e) {
			            // Handle SQL exception and return an error Response
			            return new Response(false, "SQL Error", "something went wrong");
			        } catch (Exception e) {
			            // Handle any other exceptions and return an error Response
			            return new Response(false, "Error", "something went wrong");
			        }
			    }

			    // Method to fetch all books with unpaid fines for a specific user
			    public Response returnunpaidbooks(Integer user_id) {
			        List<Map<String, Object>> unpaidBooks = new ArrayList<>(); // Initialize list to hold books with unpaid fines
			        String sql = "SELECT b.book_id, b.title, b.author, br.fine " +
			                     "FROM Borrowers br " +
			                     "JOIN Books b ON br.book_id = b.book_id " +
			                     "WHERE br.user_id = ? AND br.fine > 0.00 " +
			                     "AND br.finestatus = 'UNPAID' AND br.return_date IS NOT NULL";

			        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			            stmt.setInt(1, user_id); // Set user ID parameter
			            try (ResultSet rs = stmt.executeQuery()) {
			                while (rs.next()) {
			                    // Create a map to store book and fine details
			                    Map<String, Object> book = new HashMap<>();
			                    book.put("book_id", rs.getInt("book_id")); // Book ID
			                    book.put("title", rs.getString("title")); // Book title
			                    book.put("author", rs.getString("author")); // Author name
			                    book.put("fine", rs.getDouble("fine")); // Unpaid fine amount
			                    unpaidBooks.add(book); // Add book details to the list
			                }
			            }
			            // Return a successful Response with the list of unpaid books
			            return new Response(true, "Success", unpaidBooks);
			        } catch (SQLException e) {
			            // Handle SQL exception and return an error Response
			            return new Response(false, "SQL Error", "something went wrong");
			        } catch (Exception e) {
			            // Handle any other exceptions and return an error Response
			            return new Response(false, "Error", "something went wrong");
			        }
			    }

			    // Method to calculate and display the total unpaid fine for a user
			    public Response displayTotalFine(Integer user_id) {
			        // SQL query to check if the user has unpaid fines
			        String checkSql = "SELECT COUNT(*) FROM Borrowers WHERE user_id = ? AND finestatus = 'UNPAID'";
			        
			        // SQL query to fetch details of books with unpaid fines for the user
			        String sql = "SELECT b.book_id, b.title, b.author, br.fine FROM Borrowers br " +
			                     "JOIN Books b ON br.book_id = b.book_id " +
			                     "WHERE br.user_id = ? AND br.fine > 0 AND br.finestatus = 'UNPAID'";

			        List<Map<String, Object>> finesList = new ArrayList<>(); // Initialize list to store fine details

			        try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
			            checkPs.setInt(1, user_id); // Set user ID parameter
			            try (ResultSet rs = checkPs.executeQuery()) {
			                if (rs.next() && rs.getInt(1) == 0) {
			                    // If no unpaid fines found, return a response indicating no fines
			                    return new Response(false, "No fine", null);
			                }
			            }
			        } catch (SQLException e) {
			            // Handle SQL exception
			            response.setStatus(false);
			            response.setResponseType("Error");
			            response.setResponse("something went wrong");
			        }

			        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			            stmt.setInt(1, user_id); // Set user ID parameter
			            try (ResultSet rs = stmt.executeQuery()) {
			                double totalFine = 0.0; // Initialize total fine amount
			                while (rs.next()) {
			                    // Create a map to store book and fine details
			                    Map<String, Object> fineDetails = new HashMap<>();
			                    fineDetails.put("book_id", rs.getInt("book_id")); // Book ID
			                    fineDetails.put("title", rs.getString("title")); // Book title
			                    fineDetails.put("author", rs.getString("author")); // Author name
			                    fineDetails.put("fine", rs.getDouble("fine")); // Fine amount
			                    totalFine += rs.getDouble("fine"); // Add fine to the total
			                    finesList.add(fineDetails); // Add book details to the fine list
			                }
			                // Add total fine to the result
			                Map<String, Object> totalFineMap = new HashMap<>();
			                totalFineMap.put("totalFine", totalFine); // Total fine amount
			                finesList.add(totalFineMap);
			            }
			            // Return response with the fines list
			            return new Response(true, "FinesList", finesList);
			        } catch (Exception e) {
			            // Handle exceptions when fetching fine details
			            return new Response(false, "Error", "something went wrong");
			        }
			    }

			 // Method to get the fine amount for a specific book and user
			    public Response getFine(Integer user_id, int bookId) {
			        // SQL query to retrieve the return date and fine amount for the specified book and user
			        String sql = "SELECT return_date, fine FROM Borrowers WHERE book_id = ? AND user_id = ? AND finestatus = 'UNPAID'";
			        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			            stmt.setInt(1, bookId); // Set book ID parameter
			            stmt.setInt(2, user_id); // Set user ID parameter
			            try (ResultSet rs = stmt.executeQuery()) {
			                if (rs.next()) {
			                    Date returnDate = rs.getDate("return_date"); // Fetch return date
			                    if (returnDate == null) {
			                        // If return date is null, indicate that the book is not yet returned
			                        return new Response(false, "Error", "The book is still not returned.");
			                    }
			                    double fine = rs.getDouble("fine"); // Fetch fine amount
			                    // Return success response with the fine amount
			                    return new Response(true, "FineAmount", fine);
			                } else {
			                    // If no fine record is found for the book and user, return an error response
			                    return new Response(false, "Error", "No fine record found for the book.");
			                }
			            }
			        } catch (Exception e) {
			            // Handle any exceptions and return an error response
			            return new Response(false, "Error", "something went wrong");
			        }
			    }

			    // Method to pay the fine for a specific book and user
			    public Response payFine(Integer user_id, int bookid, double amount) {
			        try {
			            // SQL query to retrieve borrowing details for the specified book and user
			            String sql = "SELECT * FROM Borrowers WHERE book_id = ? AND user_id = ? ORDER BY borrower_id DESC";
			            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			                stmt.setInt(1, bookid); // Set book ID parameter
			                stmt.setInt(2, user_id); // Set user ID parameter
			                try (ResultSet rs = stmt.executeQuery()) {
			                    if (rs.next()) {
			                        double fine = rs.getDouble("fine"); // Fetch fine amount
			                        if (fine > 0) {
			                            if (amount >= fine) {
			                                int borrowerid = rs.getInt("borrower_id"); // Fetch borrower ID

			                                // Step 1: Update the transaction to mark the fine as paid
			                                String updateSql = "UPDATE Borrowers SET finestatus = 'PAID' WHERE book_id = ? AND user_id = ? AND borrower_id = ?";
			                                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
			                                    updateStmt.setInt(1, bookid);
			                                    updateStmt.setInt(2, user_id);
			                                    updateStmt.setInt(3, borrowerid);
			                                    updateStmt.executeUpdate(); // Execute the update query
			                                }		
			                                
			                               // Step 2: Store cleared transaction in a separate table
			                                Response transactionResponse = insertClearedTransaction(rs);
			                                if (!transactionResponse.isStatus()) {
			                                    // Handle error from insertClearedTransaction
			                                    return transactionResponse; // Return the error response directly
			                                }

			                                // Return success response indicating the fine is paid
			                                response.setStatus(true);
			                                response.setResponseType("Success");
			                                response.setResponse("Fine paid successfully.");
			                            } else {
			                                // Return error response for insufficient payment amount
			                                response.setStatus(false);
			                                response.setResponseType("Error");
			                                response.setResponse("Insufficient amount to pay the fine. Fine remains UNPAID.");
			                            }
			                        } else {
			                            // Return error response if no fine is found for the book and user
			                            response.setStatus(false);
			                            response.setResponseType("Error");
			                            response.setResponse("No fine found for the given book and user.");
			                        }
			                    } else {
			                        // Return error response if no borrowing record is found for the book
			                        response.setStatus(false);
			                        response.setResponseType("Error");
			                        response.setResponse("No borrowing record found for this book.");
			                    }
			                }
			            }
			        } catch (SQLException e) {
			            // Handle SQL exceptions and return an error response
			            response.setStatus(false);
			            response.setResponseType("Error");
			            response.setResponse("something went wrong");
			        }

			        return response; // Return response object
			    }


			    /*
			     * Method to insert data into the 'Transactions' table for records where the fine has been cleared 
			     * and the book has been returned on time.
			     */
			    public Response insertClearedTransaction(ResultSet rs) {
			        // SQL query to insert a new transaction record into the 'Transactions' table
			        String ctSql = "INSERT INTO Transactions (borrower_id, user_id, fine, finestatus) VALUES (?, ?, ?, ?)";

			        try (PreparedStatement ct = conn.prepareStatement(ctSql)) {
			            // Set parameters for the prepared statement using the data from the ResultSet
			            ct.setInt(1, rs.getInt("borrower_id")); // Set borrower ID
			            ct.setInt(2, rs.getInt("user_id")); // Set user ID
			            ct.setDouble(3, rs.getDouble("fine")); // Set fine amount
			            ct.setString(4, "PAID"); // Set fine status to 'PAID'

			            // Execute the SQL insert query to save the transaction record
			            ct.executeUpdate();
			            // Return a successful Response after transaction insertion
			            return new Response(true, "Success", "Transaction inserted successfully.");
			        } catch (SQLException e) {
			            // Handle SQL exception and return an error Response
			            return new Response(false, "SQL Error", "fine paid sucesssfully but not inserted data in transaction table");
			        } catch (Exception e) {
			            // Handle any other exceptions and return an error Response
			            return new Response(false, "Error", "An unexpected error occurred ");
			        }
			    }

			   


}
