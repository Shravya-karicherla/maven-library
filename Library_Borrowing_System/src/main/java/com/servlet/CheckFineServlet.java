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
import com.servcie.CustomerService;
import com.validations.SessionValidator;

// Servlet for handling fine-related actions for customers
@WebServlet("/checkFineServlet") // URL mapping for this servlet
public class CheckFineServlet extends HttpServlet {
    private CustomerService customerService = new CustomerService(); // CustomerService instance for fine-related business logic

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Handle POST requests (form submissions)
        handleRequest(req, resp);
    }

    private void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(); // Fetch the current session
        String action = req.getParameter("action"); // Get the action parameter from the request

        // Validate the session before proceeding
        if (!SessionValidator.validateSession(session)) {
            resp.sendRedirect("index.jsp"); // Redirect to the login page if the session is invalid
            return;
        }

        // Handle the requested action
        switch (action) {
            case "returnunpaidbooksforcheckfine": // Action triggered by customerindex.jsp (Check Fine button)
                returnunpaidbooksforcheckfine(session, req, resp);
                break;

            case "singleFine": // Action triggered by checkFine.jsp (Single Fine button)
                singleFine(session, req, resp);
                break;

            case "TotalFine": // Action triggered by checkFine.jsp (Total Fine button)
                TotalFine(session, req, resp);
                break;

            case "displayfinestopay": // Action triggered by displayFines.jsp (Pay Fine button)
                displayfinetopay(req, resp);
                break;

            default: // Handle invalid actions
                resp.getWriter().println("Invalid action");
                break;
        }
    }

    // Fetch unpaid fine books for the customer
    private void returnunpaidbooksforcheckfine(HttpSession session, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Get the user ID from the session
        Integer user_id = (Integer) session.getAttribute("user_id");

        // Call the service method and handle the Response object
        Response response = customerService.returnunpaidbooks(user_id);
        
        if (response.isStatus()) {
            // If the Response indicates success, extract and store the list of unpaid books in the session
            List<Map<String, Object>> unpaidBooks = (List<Map<String, Object>>) response.getResponse();
            session.setAttribute("returnunpaidbooks", unpaidBooks);
            resp.sendRedirect("checkFine.jsp"); // Redirect to the appropriate JSP to display unpaid books
        } else {
            // If the Response indicates an error, store the error message in the session
            session.setAttribute("message", "Error fetching books with unpaid fines: " + response.getResponse());
            resp.sendRedirect("customerindex.jsp"); // Redirect to an error page
        }
    }


    // Fetch fine amount for a single book
    private void singleFine(HttpSession session, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        Integer user_id = (Integer) session.getAttribute("user_id"); // Get user ID from the session
        int bookId = Integer.parseInt(req.getParameter("bookId")); // Get book ID from the request
        try {
            // Fetch fine amount and store details in session
            double fine = (double) customerService.getFine(user_id, bookId).getResponse();
            session.setAttribute("messagee", "Fine Amount: " + fine);
            session.setAttribute("fine", fine);
            session.setAttribute("bookid", bookId);
        } catch (Exception e) {
            // Handle exceptions and store the error message in session
            session.setAttribute("messagee", "Message: " + e.getMessage());
            session.setAttribute("fine", null);
            session.setAttribute("bookid", bookId);
        }
        resp.sendRedirect("displayFines.jsp"); // Redirect to JSP to display fine details
    }

    // Calculate and display the total unpaid fines for the customer
    private void TotalFine(HttpSession session, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer user_id = (Integer) session.getAttribute("user_id"); // Get user ID from the session
        Response response = customerService.displayTotalFine(user_id); // Fetch total fine details

        if (response.isStatus()) {
            // If fines are found, store the response details in the session
            session.setAttribute("finesList", response.getResponse());
            session.setAttribute("messagee", "totalfine");
        } else {
            // If no fines, store the appropriate message in the session
            session.setAttribute("messagee", "No Fines");
        }
        resp.sendRedirect("displayFines.jsp"); // Redirect to JSP to display fine details
    }

    // Redirect to Pay Fine page with fine details from displayFines.jsp
    private void displayfinetopay(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String bookid = req.getParameter("bookid"); // Get book ID from request
        String amount = req.getParameter("amount"); // Get fine amount from request
        HttpSession session = req.getSession(); // Fetch current session

        if (bookid != null && amount != null) {
            // Store fine details in session and redirect to Pay Fine page
            session.setAttribute("bookid", Integer.parseInt(bookid));
            session.setAttribute("amount", Double.parseDouble(amount));
            session.setAttribute("displayfines", "displayfines");
            resp.sendRedirect("payFine.jsp");
        } else {
            // Redirect back to Display Fines page if no details are provided
            resp.sendRedirect("displayFines.jsp");
        }
    }

    @Override
    public void destroy() {
        // Cleanup resources when the servlet is destroyed
        super.destroy();
        customerService.close(); // Close resources in CustomerService
    }
}
