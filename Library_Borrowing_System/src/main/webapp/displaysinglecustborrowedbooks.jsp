<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%
    List<Map<String, Object>> borrowedBooksForCustomer = (List<Map<String, Object>>) session.getAttribute("borrowedBooksForCustomer");
    Integer noOfPages = (Integer) session.getAttribute("noOfPages");
    Integer currentPage = (Integer) session.getAttribute("currentPage");

    if (session.getAttribute("mail") == null || borrowedBooksForCustomer == null || noOfPages == null || currentPage == null) {
        RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
        dispatcher.forward(request, response);
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Borrowed Books</title>
    <link rel="stylesheet" type="text/css" href="stylee.css">
    <link rel="stylesheet" type="text/css" href="table.css">
    <%
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    %>
</head>
<body>
<div class="container">
    <h2 style="color:#FFD700;">Borrowed Books By <%= session.getAttribute("name") %></h2>
    <table>
        <thead>
            <tr>
                <th>Book Title</th>
                <th>Borrow Date</th>
                <th>Due Date</th>
                <th>Return Date</th>
                <th>Fine</th>
                <th>Fine Status</th>
            </tr>
        </thead>
        <tbody>
            <%
                if (borrowedBooksForCustomer != null && !borrowedBooksForCustomer.isEmpty()) {
                    for (Map<String, Object> book : borrowedBooksForCustomer) {
            %>
            <tr>
                 <td><%=book.get("book_title") %></td>
                <td><%= book.get("borrow_date") %></td>
                <td><%= book.get("due_date") %></td>
                <td><%= book.get("return_date") != null ? book.get("return_date") : "Not Returned" %></td>
                <td><%= book.get("fine") %></td>
                <td><%= book.get("finestatus") != null ? book.get("finestatus") : "Book Not Returned" %></td>
            </tr>
            <%
                    }
                } else {
            %>
            <tr>
                <td colspan="8">No borrowed books found.</td>
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
        <a href="borrowBookServlet?action=borrowedbooksforsinglecustomer&page=<%= currentPage - 1 %>">Previous</a>
        <%
            }
            if (currentPage != null && noOfPages != null && currentPage < noOfPages) {
        %>
        <a href="borrowBookServlet?action=borrowedbooksforsinglecustomer&page=<%= currentPage + 1 %>">Next</a>
        <%
            }
        %>
    </div>
    <a href="customerindex.jsp" style="color:black;background-color: #F08080;text-decoration: none" class="button" id="backButton">Back to Home</a>
</div>
</body>
</html>
