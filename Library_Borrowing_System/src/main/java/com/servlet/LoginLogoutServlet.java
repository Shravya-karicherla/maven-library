package com.servlet;
import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.model.Response;
import com.servcie.CustomerService;
import com.servcie.OwnerService;

@WebServlet("/loginLogoutServlet")
public class LoginLogoutServlet extends HttpServlet {
    private OwnerService ownerService = new OwnerService();
    private CustomerService customerService = new CustomerService();
    //responsible for requests coming from body
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest(req, resp);
    }

    private void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String action = req.getParameter("action");
      
        switch (action) {
            //this action is coming from Login.jsp page when cust click on login page  
            case "Login":
                Login(session, req, resp);
                break;
            //this action is coming from customerindex.jsp and ownerindex.jsp page when user click on logout button
            case "logout":
                logout(session, req, resp);
                break;
            default:
            	resp.getWriter().println("Invalid action");
                break;
        }
    }
    //handles both customer login and owner login 
    private void Login(HttpSession session, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String mail = req.getParameter("mail");
        String password = req.getParameter("pass");
        Response loginResponse;
        try {
            if ("owner".equals(session.getAttribute("whoIsLogin"))) {
                loginResponse = ownerService.Loginowner(session, mail, password);
            }else {
                loginResponse = customerService.Logincustomer(session, mail, password);
                if (loginResponse.isStatus() && "Success".equals(loginResponse.getResponseType())) {
                    Integer user_id = (Integer) session.getAttribute("user_id");
                    Response fineResponse = customerService.displayPendingFeeToCustomer(user_id); // Call the updated service method
                    if (fineResponse.isStatus() && fineResponse.getResponse() instanceof Double) {
                        double totalFine = (double) fineResponse.getResponse();
                        if (totalFine > 0) {
                            loginResponse = new Response(true, "Warning", "Login successful, but you have a pending fine of " + totalFine);
                        }
                    } else {
                        // Handle any error scenario from the displayPendingFeeToCustomer service
                        loginResponse = new Response(false, "Error", "Error fetching pending fine: " + fineResponse.getResponse());
                    }
                }
            }


            // Handling response based on login status
            session.setAttribute("message", loginResponse.getResponse());
            if (loginResponse.isStatus()) {
                if ("owner".equals(session.getAttribute("whoIsLogin"))) {
                    resp.sendRedirect("ownerIndex.jsp");
                } else {
                    resp.sendRedirect("customerindex.jsp");
                }
            } else {
                resp.sendRedirect("Login.jsp");
            }
        } catch (Exception e) {
            loginResponse = new Response(false, "Error", "something went wrong");
            session.setAttribute("message", loginResponse.getResponse());
            resp.sendRedirect("Login.jsp");
        }
    }

    //handles logout
    private void logout(HttpSession session, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        session.invalidate();
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        resp.setHeader("Pragma", "no-cache");
        resp.setDateHeader("Expires", 0);
        resp.getWriter().println("<script>sessionStorage.setItem('loggedOut', 'true');</script>");
        resp.sendRedirect("index.jsp");
    }

   
    @Override
	   public void destroy() {
	       super.destroy();
	       customerService.close(); 
	   }

}
