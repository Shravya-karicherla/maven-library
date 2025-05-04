package com.servlet;

import java.io.IOException;
import java.time.LocalDate;
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
import com.servcie.CustomerService;
import com.validations.SessionValidator;

// Servlet class to handle book borrowing-related actions
@WebServlet("/borrowBookServlet") // URL mapping for this servlet
public class BorrowBookServlet extends HttpServlet {
    private CustomerService customerService = new CustomerService(); // CustomerService instance for user-related logic
    private CommonService commonService = new CommonService(); // CommonService instance for shared functionality

    // Handles HTTP POST requests (form submissions)
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest(req, resp);
    }

    // Handles HTTP GET requests (URL-based actions)
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest(req, resp);
    }

    // Central method to handle various actions based on user input
    private void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(); // Fetch the current session
        String action = req.getParameter("action"); // Determine the action requested       
        // Validate session before proceeding
        if (!SessionValidator.validateSession(session)) {
            resp.sendRedirect("index.jsp"); // Redirect to login page if session is invalid
            return;
        }

        // Switch to handle different actions
        switch (action) {
            case "borrowBook": // Action triggered by borrowBook.jsp (Borrow Book button)
                borrowBook(session, req, resp);
                break;
            case "getBooks": // Action triggered by customerindex.jsp (Browse Borrow Book button)
                getBooksQuantityGreaterThanZero(session, req, resp);
                break;
            case "borrowedbooksforsinglecustomer": // Action triggered by customerindex.jsp (Check Borrowed Books button)
                borrowedbooksforsinglecustomer(session, req, resp);
                break;
            default:
                resp.getWriter().println("Invalid action"); // Handle unsupported actions
                break;
        }
    }

    // Fetch borrowed books for a single customer
    private void borrowedbooksforsinglecustomer(HttpSession session, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int userId = (Integer) session.getAttribute("user_id"); // Get user_id from session

        // Fetch borrowed books from session or service method
        List<Map<String, Object>> borrowedBooksCustomer = (List<Map<String, Object>>) session.getAttribute("borrowedBooksCustomer");
        if (borrowedBooksCustomer == null) {
            // Use the service method and handle the Response object
            Response response = customerService.borrowedbooksforsinglecustomer(userId);
            if (response.isStatus()) {
                borrowedBooksCustomer = (List<Map<String, Object>>) response.getResponse(); // Extract borrowed books from Response
                session.setAttribute("borrowedBooksCustomer", borrowedBooksCustomer); // Store data in session for reuse
            } else {
                // Handle error scenario
                session.setAttribute("message", "something went wrong");
                resp.sendRedirect("customerindex.jsp"); 
                return;
            }
        }

        // Setup pagination parameters
        int[] paginationData = commonService.setupPagination(session, req);
        int page = paginationData[0];
        int recordsPerPage = paginationData[1];

        // Perform pagination
        int totalRecords = borrowedBooksCustomer.size();
        int noOfPages = (int) Math.ceil(totalRecords * 1.0 / recordsPerPage);
        int start = (page - 1) * recordsPerPage;
        int end = Math.min(start + recordsPerPage, totalRecords);
        List<Map<String, Object>> paginatedBorrowedBooks = borrowedBooksCustomer.subList(start, end);

        // Store pagination details and records in session
        session.setAttribute("borrowedBooksForCustomer", paginatedBorrowedBooks);
        session.setAttribute("noOfPages", noOfPages);
        session.setAttribute("currentPage", page);

        // Redirect to JSP for displaying borrowed books
        resp.sendRedirect("displaysinglecustborrowedbooks.jsp");
    }


    // Fetch books with available quantity greater than zero
    private void getBooksQuantityGreaterThanZero(HttpSession session, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Fetch available books from session or service method
        List<Map<String, Object>> getBooks = (List<Map<String, Object>>) session.getAttribute("getBooksQuantityGreaterThanZero");

        if (getBooks == null) {
            // Call the service and handle the Response object
            Response response = customerService.getBooksQuantityGreaterThanZero();
            if (response.isStatus()) {
                getBooks = (List<Map<String, Object>>) response.getResponse(); // Extract the list of books from Response
                session.setAttribute("getBooksQuantityGreaterThanZero", getBooks); // Store data in session for reuse
            } else {
                // Handle error scenario
                session.setAttribute("message", "Error fetching books: " + response.getResponse());
                resp.sendRedirect("customerindex.jsp"); 
                return;
            }
        }

        // Setup pagination parameters
        int[] paginationData = commonService.setupPagination(session, req);
        int page = paginationData[0];
        int recordsPerPage = paginationData[1];

        // Perform pagination
        int totalRecords = getBooks.size();
        int noOfPages = (int) Math.ceil(totalRecords * 1.0 / recordsPerPage);
        int start = (page - 1) * recordsPerPage;
        int end = Math.min(start + recordsPerPage, totalRecords);
        List<Map<String, Object>> paginatedBooks = getBooks.subList(start, end);

        // Store pagination details and records in session
        session.setAttribute("getbooks", paginatedBooks);
        session.setAttribute("noOfPages", noOfPages);
        session.setAttribute("currentPage", page);

        // Redirect to JSP for displaying available books
        resp.sendRedirect("borrowBook.jsp");
    }

    // Borrow a book for the customer
    private void borrowBook(HttpSession session, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        Integer user_id = (Integer) session.getAttribute("user_id"); // Get user ID from session
        String borrowerName = (String) session.getAttribute("namee"); // Get customer name from session
        int bookid = Integer.parseInt(req.getParameter("bookid")); // Get book ID from request
        LocalDate borrowDate = LocalDate.now(); // Set current date as borrow date
        LocalDate due_date = borrowDate; // Set due date (modify if needed)

        // Call service method to borrow the book
        Response response = customerService.borrowBook(user_id, borrowerName, bookid, borrowDate, due_date);
        if (response.isStatus()) {
            // Clear session attributes if borrowing succeeds
            session.removeAttribute("getBooksQuantityGreaterThanZero");
            session.setAttribute("message", response.getResponse());
        } else {
            // Store error details in session if borrowing fails
            session.setAttribute("bookid", bookid);
            session.setAttribute("borrowerName", borrowerName);
            session.setAttribute("message", response.getResponse());
        }

        // Refresh the list of available books
        getBooksQuantityGreaterThanZero(session, req, resp);
    }

    @Override
    public void destroy() {
        super.destroy(); // Perform default cleanup
        customerService.close(); // Close resources in CustomerService
    }
}
