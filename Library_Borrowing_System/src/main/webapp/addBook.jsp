<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<% 
    String title = (String) session.getAttribute("title");
    String author = (String) session.getAttribute("author");
    Integer availableQuantity = (Integer) session.getAttribute("availablequantity");
    String message = (String) session.getAttribute("message");
    String mail = (String) session.getAttribute("mail");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Library Borrowing System</title>
    <link rel="stylesheet" type="text/css" href="stylee.css">
    <link rel="stylesheet" type="text/css" href="common.css">   
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
<%
    if (session.getAttribute("mail") == null) {
        RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
        dispatcher.forward(request, response);
    } else {
%>
    <div class="container">
        <center><h1 style="color:#FFD700;">Add Book</h1></center>
        <div class="form-container">        
        <form action="addBook" method="post">
        <div class="form-row">
            <label for="title">Title:</label>
            <input class="button" type="text" id="title" name="title" placeholder="enter title" value="<%= title != null ? title : "" %>" pattern="[A-Za-z0-9 ]+" maxlength="45" required><br>
            </div>
            <div class="form-row">
            <label for="author">Author:</label>
            <input class="button" type="text" id="author" name="author" placeholder="enter author" value="<%= author != null ? author : "" %>" pattern="[A-Za-z ]+" maxlength="45" required><br>
            </div>
            <div class="form-row">
            <label for="availablequantity">Available Quantity:</label>
            <input class="button" type="number" id="availablequantity" name="availablequantity" placeholder="enter quantity" value="<%= availableQuantity != null ? availableQuantity : "" %>" min="0" max="1000" required><br>
            </div>
            <input type="hidden" name="action" value="addBook">
            <input style="color:black;background-color: #98FB98;"  id="submitButton"  class="button" type="submit" value="Add Book">
        </form>
        </div>
        <a style="color:black;background-color: #F08080" href="ownerIndex.jsp" class="button" id="backButton">Back to Home</a>
    </div>
    <div id="overlay" class="overlay"></div>
    <div id="popup" class="popup">
        <p id="popup-message"></p>
        <button onclick="closePopup()">OK</button>
    </div>
<% 
} 
session.removeAttribute("title");
session.removeAttribute("author");
session.removeAttribute("availablequantity");
session.removeAttribute("message"); %>
</body>
</html>
