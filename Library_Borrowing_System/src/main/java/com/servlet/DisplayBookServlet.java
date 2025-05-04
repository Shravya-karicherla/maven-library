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
import com.validations.SessionValidator;

@WebServlet("/displayBooksServlet")
public class DisplayBookServlet extends HttpServlet {
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

        // Validate the session using SessionValidator
        if (!SessionValidator.validateSession(session)) {
            resp.sendRedirect("index.jsp");
            return;
        }

        // Fetch book records only once
        List<Map<String, Object>> allBooks = (List<Map<String, Object>>) session.getAttribute("allBooks");
        if (allBooks == null) {
            // Call the service method and handle the Response object
            Response response = commonService.getAllBooks();
            if (response.isStatus()) {
                allBooks = (List<Map<String, Object>>) response.getResponse(); // Extract book data from Response
                session.setAttribute("allBooks", allBooks); // Store data in session for reuse
            } else {
                // Handle error scenario
                session.setAttribute("message", "Error fetching books: " + response.getResponse());
                String whoIsLogin = (String) session.getAttribute("whoIsLogin");            
                if ("owner".equals(whoIsLogin)) {
                	resp.sendRedirect("ownerIndex.jsp");
                } else if ("customer".equals(whoIsLogin)) {
                	resp.sendRedirect("customerindex.jsp");
                }                         
                return;
            }
        }

        // Setup pagination parameters
        int[] paginationData = commonService.setupPagination(session, req);
        int page = paginationData[0];
        int recordsPerPage = paginationData[1];

        // Perform pagination logic
        int totalRecords = allBooks.size();
        int noOfPages = (int) Math.ceil(totalRecords * 1.0 / recordsPerPage);
        int start = (page - 1) * recordsPerPage;
        int end = Math.min(start + recordsPerPage, totalRecords);
        List<Map<String, Object>> paginatedBooks = allBooks.subList(start, end);

        // Set attributes for paginated results
        session.setAttribute("bookList", paginatedBooks);
        session.setAttribute("noOfPages", noOfPages);
        session.setAttribute("currentPage", page);

        // Redirect to the JSP to display books
        resp.sendRedirect("displayBooksList.jsp");
    }


    @Override
    public void destroy() {
        super.destroy();
        commonService.close();
    }
}
