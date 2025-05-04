package com.servcie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.db.DbConnection;
import com.model.Response;

public class CommonService {
    private Connection conn;

    // Constructor to fetch DB connection
    public CommonService() {
        this.conn = DbConnection.getConnection();
    }

    // Method to close the connection (cleanup)
    public void close() {
        DbConnection.closeConnection();
    }
    // Shared response object to store and return the status of operations
    Response response = new Response();
	// Fetch all books from the database
    public Response getAllBooks() {
        List<Map<String, Object>> bookList = new ArrayList<>();
        String sql = "SELECT * FROM BOOKS";
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

            // Return a successful Response with the list of books
            return new Response(true, "Success", bookList);

        } catch (SQLException e) {
            // Handle SQL exceptions and return an error Response
            return new Response(false, "SQL Error", "something went wrong");
        } catch (Exception e) {
            // Handle unexpected exceptions and return an error Response
            return new Response(false, "Error", "something went wrong");
        }
    }

   
	    // fetch records per page
	    public int getRecordsPerPageFromDB() {
	        int recordsPerPage = 10; // Default fallback
	        try (PreparedStatement ps = conn.prepareStatement("SELECT value FROM configure WHERE attribute = 'recordsPerPage'");
	             ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                recordsPerPage = Integer.parseInt(rs.getString("value"));
	            }
	        } catch (SQLException e) {
	            
	        }
	        return recordsPerPage;
	    }
       //fetch start page from db
		public int getSavedPage() {
			 int startpage = 1; // Default fallback
		        try (PreparedStatement ps = conn.prepareStatement("SELECT value FROM configure WHERE attribute = 'startpage'");
		             ResultSet rs = ps.executeQuery()) {
		            if (rs.next()) {
		            	startpage = Integer.parseInt(rs.getString("value"));
		            }
		        } catch (SQLException e) {
		        	 e.printStackTrace(); 
		        }
		        return startpage;
		}
		
	    // Pagination logic (Reusable method)
	    public int[] setupPagination(HttpSession session, HttpServletRequest req) {
	    	// Fetch records per page  only once from db
	        int recordsPerPage = session.getAttribute("recordsPerPage") != null ? 
	                (Integer) session.getAttribute("recordsPerPage") : getRecordsPerPageFromDB();
	        session.setAttribute("recordsPerPage", recordsPerPage);
	        // Fetch startPage only once
	        int startPage = session.getAttribute("startPage") != null ? 
	                (Integer) session.getAttribute("startPage") : getSavedPage();
	        session.setAttribute("startPage", startPage);
            //fetch customer defined page details  
	        int page = startPage;
	        if (req.getParameter("page") != null) {
	            try {
	                page = Integer.parseInt(req.getParameter("page"));
	                session.setAttribute("startPage", page);
	            } catch (NumberFormatException e) {
	                page = 1;
	            }
	        }        
	        return new int[]{page, recordsPerPage};
	    }
}
