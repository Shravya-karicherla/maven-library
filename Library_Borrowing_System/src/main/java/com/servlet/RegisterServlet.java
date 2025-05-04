package com.servlet;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.model.Response;
import com.servcie.CustomerService;

@WebServlet("/registerServlet")
public class RegisterServlet extends HttpServlet {
    private CustomerService customerService = new CustomerService();
    //responsible for requests coming from body
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest(req, resp);
    }
    //responsible for requests coming from url
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest(req, resp);
    }
    private void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        String mail = req.getParameter("mail");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirm_password");
        String contact = req.getParameter("contact_info");
        String name = req.getParameter("name");

        // Store inputs in session for redisplay in case of an error
        session.setAttribute("inputMail", mail);
        session.setAttribute("inputContact", contact);
        session.setAttribute("inputName", name);

        if (!password.equals(confirmPassword)) {
            session.setAttribute("highlightpassword", true);
            session.setAttribute("message", "Passwords do not match.");
            resp.sendRedirect("register.jsp");
            return;
        }

        Response response = customerService.register(mail, password, contact, name, session);

        if (response.isStatus()) {
            session.setAttribute("color", "green");
            session.removeAttribute("inputMail");
            session.removeAttribute("inputName");
            session.removeAttribute("inputContact");
            session.removeAttribute("highlightContact");
            session.removeAttribute("highlightMail");
            session.removeAttribute("highlightpassword");
            session.setAttribute("message", response.getResponse());
            resp.sendRedirect("Login.jsp");
        } else {
            if ("Error".equals(response.getResponseType())) {
                session.setAttribute("message", response.getResponse());
            }
            resp.sendRedirect("register.jsp");
        }
    }

       
         @Override
  	   public void destroy() {
  	       super.destroy();
  	      customerService.close(); 
  	   }

}

