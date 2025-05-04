package com.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.model.Response;
import com.servcie.OwnerService;
import com.validations.SessionValidator;

// Servlet class responsible for adding a book to the system
@WebServlet("/addBook") // URL mapping for this servlet
public class AddBookServlet extends HttpServlet {
    private OwnerService ownerService = new OwnerService(); // Instance of OwnerService for business logic

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Handle POST requests
        handleRequest(req, resp);
    }

    private void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Step 1: Validate session before proceeding
        HttpSession session = req.getSession();
        if (!SessionValidator.validateSession(session)) {
            resp.sendRedirect("index.jsp"); // Redirect to the login page if the session is invalid
            return;
        }

        Response response = new Response(); // Create a Response object to store the result of the operation

        try {
            // Step 2: Fetch book details from the JSP form
            String title = req.getParameter("title"); // Retrieve book title
            String author = req.getParameter("author"); // Retrieve author name
            double availableQuantitydouble = Double.parseDouble(req.getParameter("availablequantity")); // Retrieve quantity
            // Convert quantity from double to integer for consistency in display
            int availableQuantity = (int) Math.floor(availableQuantitydouble);

            // Step 3: Store book details in session attributes (temporary storage)
            session.setAttribute("title", title);
            session.setAttribute("author", author);
            session.setAttribute("availablequantity", availableQuantity);

            // Step 4: Call the business logic to add the book
            response = ownerService.addBook(title, author, availableQuantity);

            // Step 5: Clear session attributes if the book is successfully added
            if (response.isStatus()) {
                session.removeAttribute("title");
                session.removeAttribute("author");
                session.removeAttribute("availablequantity");
            }
        } catch (Exception e) {
            // Handle unexpected errors during execution
            response.setStatus(false);
            response.setResponseType("Error");
            response.setResponse("An unexpected error occurred");
        }
        // Step 6: Store the response message in the session and redirect to the JSP page
        session.setAttribute("message", response.getResponse());
        resp.sendRedirect("addBook.jsp");
    }

    @Override
    public void destroy() {
        // Cleanup resources when the servlet is destroyed
        super.destroy();
        ownerService.close(); // Close database connections or other resources in OwnerService
    }
}
