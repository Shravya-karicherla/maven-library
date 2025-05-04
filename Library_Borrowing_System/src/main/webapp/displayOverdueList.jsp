<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Overdue List</title>
<link rel="stylesheet" type="text/css" href="stylee.css">
<link rel="stylesheet" type="text/css" href="table.css">
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    Integer noOfPages = (Integer) session.getAttribute("noOfPages");
    Integer currentPage = (Integer) session.getAttribute("currentPage");
    List<Map<String, Object>> overdueList = (List<Map<String, Object>>) session.getAttribute("overdueList");

    if (session.getAttribute("mail") == null || noOfPages == null || currentPage == null || overdueList == null) {
        RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
        dispatcher.forward(request, response);
        return;
    }
%>
</head>
<body>
<div class="container">
<h1 style="color:#FFD700;">Overdue List</h1>
    <table>
        <thead>
            <tr>
                <th>User ID</th>
                <th>Book Title</th>
                <th>Borrow Date</th>
                <th>Due Date</th>
                <th>Return Date</th>               
                <th>Fine</th>
            </tr>
        </thead>
        <tbody>
            <%
                if (overdueList != null && !overdueList.isEmpty()) {
                    for (Map<String, Object> transaction : overdueList) {
            %>
            <tr>
                <td><%= transaction.get("user_id") %></td>
                <td><%= transaction.get("title") %></td>                
                <td><%= transaction.get("borrow_date") %></td>
                <td><%= transaction.get("due_date") %></td>
                <td><%= transaction.get("return_date") %></td>
                <td><%= transaction.get("fine") %></td>
            </tr>
            <%
                    }
                } else {
            %>
            <tr>
                <td colspan="6">No overdues found.</td>
            </tr>
            <%
                }
            %>
        </tbody>
    </table>
    <div class="pagination">
        <%
            if (currentPage != null && currentPage > 1) {
        %>
        <a href="generateOverdueListServlet?action=generateOverdueList&page=<%= currentPage - 1 %>">Previous</a>
        <%
            }
            if (currentPage != null && noOfPages != null && currentPage < noOfPages) {
        %>
        <a href="generateOverdueListServlet?action=generateOverdueList&page=<%= currentPage + 1 %>">Next</a>
        <%
            }
        %>
    </div>
    <a class="button" style="color:black;background-color: #F08080;text-decoration: none" href="ownerIndex.jsp" id="backButton">Back to Home</a>
</div>
</body>
</html>