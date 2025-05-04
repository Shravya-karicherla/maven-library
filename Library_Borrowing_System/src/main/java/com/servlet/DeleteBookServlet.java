package com.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.model.Response;
import com.servcie.CommonService;
import com.servcie.OwnerService;
import com.validations.SessionValidator;

// Servlet for managing book deletion-related actions
@WebServlet("/deleteBookServlet") // URL mapping for this servlet
public class DeleteBookServlet extends HttpServlet {
    private OwnerService ownerService = new OwnerService(); // Instance of OwnerService for business logic
    private CommonService commonService = new CommonService(); // Instance of CommonService for shared functionality

    // Handles HTTP POST requests (typically form submissions)
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest(req, resp);
    }

    // Handles HTTP GET requests (actions triggered via URL)
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest(req, resp);
    }

    // Centralized method to handle different actions
    private void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(); // Fetch the current session
        String action = req.getParameter("action"); // Get the requested action

        // Step 1: Validate session before processing the request
        if (!SessionValidator.validateSession(session)) { // Verify user session is active
            resp.sendRedirect("index.jsp"); // Redirect to login page if session is invalid
            return;
        }

        // Step 2: Process the requested action
        switch (action) {
            case "deletedisplay": // Action triggered by ownerIndex.jsp (Display Books for Deletion button)
                deletedisplay(session, req, resp);
                break;

            case "deleteBook": // Action triggered by deletedisplay.jsp (Delete Book button)
                deleteBook(session, req, resp);
                break;

            default: // Handle invalid or unsupported actions
                resp.getWriter().println("Invalid action");
                break;
        }
    }

    // Fetch and display all book details for deletion
    private void deletedisplay(HttpSession session, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Check if all book records are already stored in the session
        List<Map<String, Object>> allBooks = (List<Map<String, Object>>) session.getAttribute("allBooks");
        if (allBooks == null) {
            // Call the service method and handle the Response object
            Response response = ownerService.getAllBooks();
            if (response.isStatus()) {
                allBooks = (List<Map<String, Object>>) response.getResponse(); // Extract books data from Response
                session.setAttribute("allBooks", allBooks); // Store data in session for reuse
            } else {
                // Handle error scenario
                session.setAttribute("message", "Error fetching books: " + response.getResponse());
                resp.sendRedirect("ownerIndex.jsp");
                return;
            }
        }

        // Setup pagination parameters
        int[] paginationData = commonService.setupPagination(session, req);
        int page = paginationData[0];
        int recordsPerPage = paginationData[1];

        // Perform pagination
        int totalRecords = allBooks.size();
        int noOfPages = (int) Math.ceil(totalRecords * 1.0 / recordsPerPage);
        int start = (page - 1) * recordsPerPage;
        int end = Math.min(start + recordsPerPage, totalRecords);
        List<Map<String, Object>> paginatedBooks = allBooks.subList(start, end);

        // Store pagination details and records in session
        session.setAttribute("bookList", paginatedBooks);
        session.setAttribute("noOfPages", noOfPages);
        session.setAttribute("currentPage", page);

        // Redirect to JSP to display books
        resp.sendRedirect("deletedisplay.jsp");
    }


    // Method to delete a specific book
    private void deleteBook(HttpSession session, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        Response response = new Response(); // Initialize response object to store operation result

        try {
            // Step 1: Fetch the book ID from the request
            int bookId = Integer.parseInt(req.getParameter("bookid")); // Parse book ID

            // Step 2: Call service method to delete the book
            response = ownerService.deleteBook(bookId);

            // Step 3: Clear cached book data from session if deletion is successful
            if (response.isStatus()) {
                session.removeAttribute("allBooks"); // Remove book data from session to refresh records
            }
        } catch (NumberFormatException e) {
            // Handle invalid book ID format
            response.setStatus(false);
            response.setResponseType("Error");
            response.setResponse("Invalid book ID format! Please provide a valid number.");
        } catch (Exception e) {
            // Handle any unexpected errors
            response.setStatus(false);
            response.setResponseType("Error");
            response.setResponse("An unexpected error occurred");
        }

        // Step 4: Store response message in session and refresh book list display
        session.setAttribute("message", response.getResponse());
        deletedisplay(session, req, resp);
    }

    @Override
    public void destroy() {
        // Cleanup resources when the servlet is destroyed
        super.destroy(); // Perform default cleanup tasks
        ownerService.close(); // Close resources in OwnerService
    }
}
