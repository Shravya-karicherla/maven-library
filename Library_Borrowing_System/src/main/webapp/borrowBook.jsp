<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>

<%
    String message = (String) session.getAttribute("message");
    String mail = (String) session.getAttribute("mail");
    Integer noOfPages = (Integer) session.getAttribute("noOfPages");
    Integer currentPage = (Integer) session.getAttribute("currentPage");
    List<Map<String, Object>> books = (List<Map<String, Object>>) session.getAttribute("getbooks");
    if (session.getAttribute("mail") == null || books == null) {
        RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
        dispatcher.forward(request, response);
        return;
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Borrow Book</title>
    <link rel="stylesheet" type="text/css" href="stylee.css">
    <link rel="stylesheet" type="text/css" href="common.css">
    <link rel="stylesheet" type="text/css" href="table.css">
    <style>
    table {
    border-collapse: collapse; 
    border: none;
    }

    th, td {
    border: none;
     }
    
    </style>
        <script>
        window.onload = function() {
            var message = '<%= message %>';
            if (message !== "null" && message !== "") {
                document.getElementById('popup-message').innerText = message;
                document.getElementById('popup').style.display = 'block';
                document.getElementById('overlay').style.display = 'block';
            }
            
            var mail = '<%= mail %>';
            if (mail !== "null") {
                sessionStorage.removeItem('loggedOut');
            }
        }

        function closePopup() {
            document.getElementById('popup').style.display = 'none';
            document.getElementById('overlay').style.display = 'none';
            <% session.removeAttribute("message"); %>
        }

        setInterval(function() {
            if (sessionStorage.getItem('loggedOut') === 'true') {
                <%
                response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); 
                response.setHeader("Pragma", "no-cache"); 
                response.setDateHeader("Expires", 0); 
                %>
                location.reload();
            }
        }, 5000);
    </script>
</head>
<body>
<div class="container">
    <center><h1 style="color:#FFD700;">Available Books</h1></center>
    <table>
        <thead>
            <tr>
                <th>Title</th>
                <th>Author</th>
                <th>Quantity Available</th>
                <th>Action</th>
            </tr>
        </thead>
        <tbody>
            <%
                if (books != null && !books.isEmpty()) {
                    for (Map<String, Object> book : books) {
            %>
            <tr>
                <td><%= book.get("title") %></td>
                <td><%= book.get("author") %></td>
                <td><%= book.get("available_quantity") %></td>
                <td>
                    <form action="borrowBookServlet" method="post">
                        <input type="hidden" name="bookid" value="<%= book.get("book_id") %>">
                        <input type="hidden" name="action" value="borrowBook">
                        <button type="submit" class="button" style="background-color: #98FB98;">Borrow Book</button>
                    </form>
                </td>
            </tr>
            <%
                    }
                } else {
            %>
            <tr>
                <td colspan="5">No books available.</td>
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
        <a href="borrowBookServlet?action=getBooks&page=<%= currentPage - 1 %>">Previous</a>
        <%
            }
            if (currentPage != null && noOfPages != null && currentPage < noOfPages) {
        %>
        <a href="borrowBookServlet?action=getBooks&page=<%= currentPage + 1 %>">Next</a>
        <%
            }
        %>
    </div>
    <a href="customerindex.jsp" style="color:black;background-color: #F08080;text-decoration: none" class="button">Back to Home</a>
</div>
<!-- Popup Message -->
<div id="overlay" class="overlay"></div>
<div id="popup" class="popup">
    <p id="popup-message"></p>
    <button onclick="closePopup()">OK</button>
</div>
</body>
</html>
