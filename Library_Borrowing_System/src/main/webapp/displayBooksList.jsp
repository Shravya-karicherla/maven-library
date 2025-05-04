<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%
    String whoIsLogin = (String) session.getAttribute("whoIsLogin");
    String href = "ownerIndex.jsp"; 
    if ("owner".equals(whoIsLogin)) {
        href = "ownerIndex.jsp";
    } else if ("customer".equals(whoIsLogin)) {
        href = "customerindex.jsp";
    }
    List<Map<String, Object>> bookList = (List<Map<String, Object>>) session.getAttribute("bookList");
    Integer noOfPages = (Integer) session.getAttribute("noOfPages");
    Integer currentPage = (Integer) session.getAttribute("currentPage");

    if (session.getAttribute("mail") == null || noOfPages == null || currentPage == null || bookList == null) {
        RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
        dispatcher.forward(request, response);
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Book List</title>
    <link rel="stylesheet" type="text/css" href="stylee.css">
    <link rel="stylesheet" type="text/css" href="table.css">
    <%
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
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
    <h1 style="color:#FFD700;">Book List</h1>
    <table>
        <thead>
            <tr>
             <% if ("owner".equals(whoIsLogin)){%>
                <th>Book ID</th>
              <%   } %>
                <th>Title</th>
                <th>Author</th>
                <th>Available Quantity</th>
            </tr>
        </thead>
        <tbody>
            <%
                if (bookList != null && !bookList.isEmpty()) {
                    for (Map<String, Object> book : bookList) {
            %>
            <tr>
            <% if ("owner".equals(whoIsLogin)){%>
                 <td><%= book.get("book_id") %></td>
              <%   } %>              
                <td><%= book.get("title") %></td>
                <td><%= book.get("author") %></td>
                <td><%= book.get("available_quantity") %></td>
            </tr>
            <%
                    }
                } else {
            %>
            <tr>
            <% if ("owner".equals(whoIsLogin)){%>
                <td colspan="4">No books found.</td>
              <%   }
            else
            { %> 
                <td colspan="3">No books found.</td>
                <% } %>
            </tr>
            <%
                }
                session.removeAttribute("message");
            %>
        </tbody>
    </table>  
    <div class="pagination">
        <%
            if (currentPage != null && currentPage > 1) {
        %>
        <a href="displayBooksServlet?action=displayBooks&page=<%= currentPage - 1 %>">Previous</a>
        <%
            }
            if (currentPage != null && noOfPages != null && currentPage < noOfPages) {
        %>
        <a href="displayBooksServlet?action=displayBooks&page=<%= currentPage + 1 %>">Next</a>
        <%
            }
        %>
    </div>
    <a href="<%= href %>" style="color:black;background-color: #F08080;text-decoration: none" class="button" id="backButton">Back to Home</a>
</div>  
<%
    }
%> 
</body>
</html>
