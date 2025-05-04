<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Current Transactions List</title>
<link rel="stylesheet" type="text/css" href="stylee.css">
<link rel="stylesheet" type="text/css" href="table.css">
<style>

html,body {
             margin: 0;
        }     
    .container {
        width: 92%;
    }
</style>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); 
    response.setHeader("Pragma", "no-cache"); 
    response.setDateHeader("Expires", 0);  

    Integer noOfPages = (Integer) session.getAttribute("noOfPages");
    Integer currentPage = (Integer) session.getAttribute("currentPage");
    List<Map<String, Object>> borrowers = (List<Map<String, Object>>) session.getAttribute("borrowers");

    if (session.getAttribute("mail") == null || noOfPages == null || currentPage == null || borrowers == null) {
        RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
        dispatcher.forward(request, response);
        return;
    }
%>
</head>
<body>
<%
    if (session.getAttribute("mail") == null) {
        RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
        dispatcher.forward(request, response);
    } else {
%>
<div class="container">
<h1 style="color:#FFD700; ">Borrowers List</h1>
    <table>
        <thead>
            <tr>
                <th>user_id</th>
                <th>book_id</th>
                <th>name</th>
                <th>Borrow Date</th>
                <th>due_date</th>
                <th>return_date</th>
                <th>fine</th>
                <th>finestatus</th>
            </tr>
        </thead>
        <tbody>
            <%               
                if (borrowers != null && !borrowers.isEmpty()) {
                    for (Map<String, Object> borrowerss : borrowers) {
            %>
            <tr>
                <td><%= borrowerss.get("user_id") %></td>
                <td><%= borrowerss.get("book_id") %></td>
                <td><%= borrowerss.get("name") %></td>
                <td><%= borrowerss.get("borrow_date") %></td>
                <td><%= borrowerss.get("due_date") %></td>
                <td><%= borrowerss.get("return_date") != null ? borrowerss.get("return_date") : "Not Returned" %></td>
                <td><%= borrowerss.get("fine") %></td>
                <td><%= borrowerss.get("finestatus") != null ? borrowerss.get("finestatus") : "Not Returned" %></td>
            </tr>
            <%
                    }
                } else {
                    %>
                    <tr>
                        <td colspan="8">No Borrowers  found.</td>
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
        <a href="displayBorrowersServlet?action=borrowers&page=<%= currentPage - 1 %>">Previous</a>
        <%
            }
            if (currentPage != null && noOfPages != null && currentPage < noOfPages) {
        %>
        <a href="displayBorrowersServlet?action=borrowers&page=<%= currentPage + 1 %>">Next</a>
        <%
            }
        %>
    </div>
    <a class="button" style="color:black;background-color: #F08080;text-decoration: none" href="ownerIndex.jsp" id="backButton">Back to Home</a>
</div>
<% } %>
</body>
</html>
