package com.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

@WebServlet("/updateBookServlet")
public class UpdateBookServlet extends HttpServlet {
    private OwnerService ownerService = new OwnerService();
    private CommonService commonService = new CommonService();
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
        String action = req.getParameter("action");

        if (!SessionValidator.validateSession(session)) { // Use the utility method
            resp.sendRedirect("index.jsp");
            return;
        }
        

        switch (action) {
            //this action is coming from ownerindex.jsp page 
            case "updatedisplay":
                updatedisplay(session, req, resp);
                break;
            //this action is coming from updateBook.jsp page 
            case "updateBook":
                updateBook(session, req, resp);
                break;
            //this action is coming from updatedisplay.jsp page 
            case "updateBookload":
                updateBookload(session, req, resp);
                break;
          
            default:
            	resp.getWriter().println("Invalid action");
                break;
        }
    }

    // get all books 
    private void updatedisplay(HttpSession session, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Check if all records are already stored in the session
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

        // Remove session attributes for outdated book details
        session.removeAttribute("title");
        session.removeAttribute("author");
        session.removeAttribute("availablequantity");

        // Redirect to JSP to display books
        resp.sendRedirect("updatedisplay.jsp");
    }

    //fetch data from jsp page and store in session and redirect to updateBook.jsp page 
    private void updateBookload(HttpSession session, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int bookid = Integer.parseInt(req.getParameter("bookid"));
        String title = req.getParameter("title");
        String author = req.getParameter("author");
        Double availableQuantitydouble = Double.parseDouble(req.getParameter("availablequantity"));     
        //converting double to int bcz while displaying it has to show integer only 
        int availableQuantity = (int) Math.floor(availableQuantitydouble);
        session.setAttribute("bookid", bookid);
        session.setAttribute("title", title);
        session.setAttribute("author", author);
        session.setAttribute("availablequantity", availableQuantity);
        resp.sendRedirect("updateBook.jsp");
    }
    //this method handles to update book details in db
    private void updateBook(HttpSession session, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {   	
        Response response = new Response(); // Initialize response object

        try {
            // Fetch book details from request
            int bookId = Integer.parseInt(req.getParameter("bookid"));
            String title = req.getParameter("title");
            String author = req.getParameter("author");
            double availableQuantity = Double.parseDouble(req.getParameter("availablequantity"));          
            // Retrieve existing book details from session
            String existingTitle = (String) session.getAttribute("title");
            String existingAuthor = (String) session.getAttribute("author");
            Integer existingQuantity = (Integer) session.getAttribute("availablequantity");
            // Check if any values have changed
            if (title.equals(existingTitle) && author.equals(existingAuthor) && availableQuantity == existingQuantity) {
                response.setStatus(false);
                response.setResponseType("Info");
                response.setResponse("No changes were made to the book details.");
            } else {
                response = ownerService.updateBook(bookId, title, author, availableQuantity);
            }
        } catch (NumberFormatException e) {
            response.setStatus(false);
            response.setResponseType("Error");
            response.setResponse("Invalid input format! Please enter valid values.");
           
        } catch (Exception e) {
            response.setStatus(false);
            response.setResponseType("Error");
            response.setResponse("An unexpected error occurred ");
        }

        session.setAttribute("message", response.getResponse());
        updatedisplay(session, req, resp);
    }


    @Override
	   public void destroy() {
	       super.destroy();
	       ownerService.close(); 
	   }

}
