<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Transaction List</title>
    <link rel="stylesheet" type="text/css" href="stylee.css">
    <link rel="stylesheet" type="text/css" href="table.css">
</head>
<body>
<%
	response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); 
	response.setHeader("Pragma", "no-cache"); 
	response.setDateHeader("Expires", 0);  
    // Retrieve session attributes
    Integer noOfPages = (Integer) session.getAttribute("noOfPages");
    Integer currentPage = (Integer) session.getAttribute("currentPage");
    List<Map<String, Object>> transactions = (List<Map<String, Object>>) session.getAttribute("transactions");

    // If session is not valid, redirect to the login page
    if (session.getAttribute("mail") == null || noOfPages == null || currentPage == null || transactions == null) {
        response.sendRedirect("Login.jsp");
        return;
    }
%>
<div class="container">
    <h1 style="color:#FFD700; ">Transaction List</h1>
    <table>
        <thead>
            <tr>
                <th>User ID</th>
                <th>Book ID</th>
                <th>Fine</th>
                <th>Fine Status</th>
            </tr>
        </thead>
        <tbody>
            <% 
                // Display transactions
                if (transactions != null && !transactions.isEmpty()) {
                    for (Map<String, Object> transaction : transactions) {
            %>
            <tr>
                <td><%= transaction.get("user_id") %></td>
                <td><%= transaction.get("book_id") %></td>
                <td><%= transaction.get("fine") %></td>
                <td><%= transaction.get("finestatus") %></td>
            </tr>
            <%
                    }
                } else {
            %>
            <tr>
                <td colspan="5">No transactions found.</td>
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
        <a href="transactionservlet?page=<%= currentPage - 1 %>">Previous</a>
        <%
            }
            if (currentPage != null && noOfPages != null && currentPage < noOfPages) {
        %>
        <a href="transactionservlet?page=<%= currentPage + 1 %>">Next</a>
        <%
            }
        %>
    </div>

    <!-- Back to Home Button -->
    <a class="button" style="color:black;background-color: #F08080;text-decoration: none" href="ownerIndex.jsp" id="backButton">Back to Home</a>
</div>
</body>
</html>
