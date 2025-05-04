<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<% 
Integer id = (Integer) session.getAttribute("bookid"); 
String title = (String) session.getAttribute("title");
String author = (String) session.getAttribute("author");
Integer availablequantity = (Integer) session.getAttribute("availablequantity"); 
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Update Book</title>
<link rel="stylesheet" type="text/css" href="stylee.css">
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
<center><h1 style="color:#FFD700;">Update Book</h1></center>
<div class="form-container">
<form action="updateBookServlet" method="post">
    <div class="form-row">
    <label for="bookid">Book ID:</label>
    <input type="number" id="bookid" class="button" name="bookid" value="<%= id != null ? id : "" %>" required  readonly><br>
    </div>
    <div class="form-row">
    <label for="title">Title:</label>
    <input type="text" id="title" class="button" name="title" pattern="[A-Za-z0-9 ]+" value="<%= title != null ? title : "" %>" maxlength="45" required><br>
    </div>
    <div class="form-row">
    <label for="author">Author:</label>
    <input type="text" id="author" class="button" name="author" pattern="[A-Za-z ]+" value="<%= author != null ? author : "" %>" maxlength="45" required><br>
    </div>
    <div class="form-row">
    <label for="availablequantity">Available Quantity:</label>
    <input type="number" id="availablequantity" class="button" name="availablequantity" value="<%= availablequantity != null ? availablequantity : "" %>" required min="0" max="1000"><br>
    </div>
    <input type="hidden" name="action" value="updateBook">
    <button type="submit" style="color:black;background-color: #98FB98;"  id="submitButton"  class="button">Update Book</button>
</form>
</div>
<a style="color:black;background-color: #F08080" href="updatedisplay.jsp" class="button" id="backButton">Back to Home</a>
</div>
<% 
session.removeAttribute("allBooks");
} %>
</body>
</html>
