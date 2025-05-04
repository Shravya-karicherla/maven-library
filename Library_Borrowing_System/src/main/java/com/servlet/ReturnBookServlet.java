package com.servlet;

import java.io.IOException;
import java.sql.Date;
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
import com.servcie.CustomerService;
import com.validations.SessionValidator;

@WebServlet("/returnBookServlet")
public class ReturnBookServlet extends HttpServlet {
    private CustomerService customerService = new CustomerService();
    //responsible for requests coming from body
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest(req, resp);
    }

    private void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String action = req.getParameter("action");

        if (!SessionValidator.validateSession(session)) { // Use the utility method
            resp.sendRedirect("index.jsp");
            return;
        }
        switch (action) {
            //this action is coming fromreturnBook.jsp page 
            case "returnBook":
                returnBook(session, req, resp);
                break;
            //this action is coming from customerindex.jsp page when use click on returnbook button
            case "getcurrentBorrowedBooks":
                getcurrentBorrowedBooks(session, req, resp);
                break;
            default:
            	resp.getWriter().println("Invalid action"); // Handle invalid or unsupported actions
                break;
        }
    }
    //this method handle to return book
    private void returnBook(HttpSession session, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        int bookid = Integer.parseInt(req.getParameter("bookid"));
        Integer user_id = (Integer) session.getAttribute("user_id");
        Date returnDate = Date.valueOf(LocalDate.now());

        // Call the updated returnBook method
        Response response = customerService.returnBook(bookid, returnDate, user_id);

        // Store the response message in the session
        session.setAttribute("message", response.getResponse());

        // Refresh the list of borrowed books
        getcurrentBorrowedBooks(session, req, resp);
    }

    // fetch currently borrowed books and not returned yet
    private void getcurrentBorrowedBooks(HttpSession session, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Fetch user ID from the session
        Integer user_id = (Integer) session.getAttribute("user_id");     
        // Call the service method and handle the Response object
        Response response = customerService.getcurrentborrowedbooks(user_id);    
        if (response.isStatus()) {
            // If the Response indicates success, extract and store the list of books in the session
            List<Map<String, Object>> getcurrentborrowedbooks = (List<Map<String, Object>>) response.getResponse();
            session.setAttribute("getcurrentborrowedbooks", getcurrentborrowedbooks);
            resp.sendRedirect("returnBook.jsp"); // Redirect to the appropriate JSP
        } else {
            // If the Response indicates an error, store the error message in the session
            session.setAttribute("message", "Error fetching currently borrowed books: " + response.getResponse());
            resp.sendRedirect("customerindex.jsp"); 
        }
    }



    @Override
	   public void destroy() {
	       super.destroy();
	       customerService.close(); 
	   }

}

