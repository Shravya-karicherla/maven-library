<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.List"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Books List</title>
    <link rel="stylesheet" type="text/css" href="stylee.css">
    <link rel="stylesheet" type="text/css" href="table.css">
    <link rel="stylesheet" type="text/css" href="common.css">
    <%
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    %>
    <script>
    window.onload = function() {
        var message = '<%= session.getAttribute("message") %>';
        if (message !== "null" && message !== "") {
            document.getElementById('popup-message').innerText = message;
            document.getElementById('popup').style.display = 'block';
            document.getElementById('overlay').style.display = 'block';
        }
    };

    function closePopup() {
        document.getElementById('popup').style.display = 'none';
        document.getElementById('overlay').style.display = 'none';
        window.location.href = "deletedisplay.jsp"; // Redirect after user clicks OK
    }


        function confirmDelete() {
            return confirm("Are you sure you want to delete this book?");
        }
    </script>
</head>
<body>
    <%
        Integer noOfPages = (Integer) session.getAttribute("noOfPages");
        Integer currentPage = (Integer) session.getAttribute("currentPage");
        if (session.getAttribute("mail") == null) {
            RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
            dispatcher.forward(request, response);
        } else {
    %>
    <div class="container">
        <center>
            <h1 style="color:#FFD700;">Books List</h1>
        </center>
        <table>
            <thead>
                <tr>
                    <th>Book ID</th>
                    <th>Title</th>
                    <th>Author</th>
                    <th>Available Quantity</th>
                    <th>Delete</th>
                </tr>
            </thead>
            <tbody>
                <%
                    List<Map<String, Object>> booksList = (List<Map<String, Object>>) session.getAttribute("bookList");
                    if (booksList != null  && !booksList.isEmpty()) {
                        for (Map<String, Object> book : booksList) {
                %>
                <tr>
                    <td><%= book.get("book_id") %></td>
                    <td><%= book.get("title") %></td>
                    <td><%= book.get("author") %></td>
                    <td><%= book.get("available_quantity") %></td>
                    <td>
                        <form action="deleteBookServlet" method="post" onsubmit="return confirmDelete();">
                            <input type="hidden" name="bookid" value="<%= book.get("book_id") %>">
                            <input type="hidden" name="action" value="deleteBook">
                            <input style="background-color: #FF6666"  id="submitButton"  type="submit" value="Delete">
                        </form>
                    </td>
                </tr>
                <%
                        }
                    } else {
                %>
                <tr>
                    <td colspan="5">No books found.</td>
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
            <a href="deleteBookServlet?action=deletedisplay&page=<%= currentPage - 1 %>">Previous</a>
            <%
                }
                if (currentPage != null && noOfPages != null && currentPage < noOfPages) {
            %>
            <a href="deleteBookServlet?action=deletedisplay&page=<%= currentPage + 1 %>">Next</a>
            <%
                }
            %>
        </div>
        <a style="color:black;background-color: #F08080" href="ownerIndex.jsp" class="button" id="backButton">Back to Home</a>
    </div>

    <!-- Popup Message -->
<div id="overlay" class="overlay"></div>
<div id="popup" class="popup">
    <p id="popup-message"></p>
    <button onclick="closePopup()">OK</button>
</div>
    <%
        session.removeAttribute("message");
        }
    %>
</body>
</html>
