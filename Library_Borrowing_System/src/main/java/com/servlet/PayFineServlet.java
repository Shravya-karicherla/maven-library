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
import com.validations.SessionValidator;
import com.model.Response;
import com.servcie.CustomerService;

@WebServlet("/payFineServlet")
public class PayFineServlet extends HttpServlet {
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
            //this action is coming from payFine.jsp page  when cust click on payfine button
            case "payFine":
                payFine(session, req, resp);
                break;
            //this action is coming from customerindex.jsp page 
            case "returnunpaidbooks":
                returnunpaidbooks(session, req, resp);
                break;
            default:
            	resp.getWriter().println("Invalid action"); // // Handle invalid or unsupported actions
                break;
        }
    }
    // return unpaid fine books
    private void returnunpaidbooks(HttpSession session, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Fetch user ID from the session
        Integer user_id = (Integer) session.getAttribute("user_id");

        // Call the service method and handle the Response object
        Response response = customerService.returnunpaidbooks(user_id);
        
        if (response.isStatus()) {
            // If the Response indicates success, extract and store the list of unpaid books in the session
            List<Map<String, Object>> unpaidBooks = (List<Map<String, Object>>) response.getResponse();
            session.setAttribute("returnunpaidbooks", unpaidBooks);
            resp.sendRedirect("payFine.jsp"); // Redirect to the appropriate JSP
        } else {
            // If the Response indicates an error, store the error message in the session
            session.setAttribute("message", "Error fetching books with unpaid fines: " + response.getResponse());
            resp.sendRedirect("customerindex.jsp"); // Redirect to an error page
        }
    }

    //this method handle pay fine
    private void payFine(HttpSession session, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {      
        Integer user_id = (Integer) session.getAttribute("user_id");
        int bookid = Integer.parseInt(req.getParameter("bookId")); // Get the unique book ID
        double amount = Double.parseDouble(req.getParameter("amount"));

        // Call the updated payFine method
        Response response = customerService.payFine(user_id, bookid, amount);

        if (response.isStatus()) {
            // Remove session attributes after successful fine payment
            session.removeAttribute("bookid");
            session.removeAttribute("amount");
            session.removeAttribute("displayfines");
        } else {
            // Store book selection in session for future reference in case of error
            session.setAttribute("bookid", bookid);
            session.setAttribute("amount", amount);
        }

        // Set the response message in the session
        session.setAttribute("message", response.getResponse());
        session.setAttribute("page", "payFine.jsp");
        
        returnunpaidbooks(session, req, resp);
    }




    @Override
	   public void destroy() {
	       super.destroy();
	       customerService.close(); 
	   }

}
