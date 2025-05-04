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

@WebServlet("/generateOverdueListServlet")
public class GenerateOverDueListServlet extends HttpServlet {
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

        // Validate the session using SessionValidator
        if (!SessionValidator.validateSession(session)) {
            resp.sendRedirect("index.jsp");
            return;
        }

        // Fetch overdue records only once
        List<Map<String, Object>> allOverdueRecords = (List<Map<String, Object>>) session.getAttribute("allOverdueRecords");
        if (allOverdueRecords == null) {
            // Call the service method and handle the Response object
            Response response = ownerService.getAllOverdueRecords();
            if (response.isStatus()) {
                allOverdueRecords = (List<Map<String, Object>>) response.getResponse(); // Extract overdue records data from Response
                session.setAttribute("allOverdueRecords", allOverdueRecords); // Store data in session for reuse
            } else {
                // Handle error scenario
                session.setAttribute("message", "Error fetching overdue records: " + response.getResponse());
                resp.sendRedirect("ownerIndex.jsp");
                return;
            }
        }

        // Setup pagination parameters
        int[] paginationData = commonService.setupPagination(session, req);
        int page = paginationData[0];
        int recordsPerPage = paginationData[1];

        // Perform pagination logic
        int totalRecords = allOverdueRecords.size();
        int noOfPages = (int) Math.ceil(totalRecords * 1.0 / recordsPerPage);
        int start = (page - 1) * recordsPerPage;
        int end = Math.min(start + recordsPerPage, totalRecords);
        List<Map<String, Object>> paginatedOverdueRecords = allOverdueRecords.subList(start, end);

        // Store paginated results in session
        session.setAttribute("overdueList", paginatedOverdueRecords);
        session.setAttribute("noOfPages", noOfPages);
        session.setAttribute("currentPage", page);

        // Redirect to the JSP to display overdue records
        resp.sendRedirect("displayOverdueList.jsp");
    }

    @Override
	   public void destroy() {
	       super.destroy();
	       ownerService.close(); 
	   }

}

