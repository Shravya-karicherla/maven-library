<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>

<%
    Integer id = (Integer) session.getAttribute("bookid");
    String contact_info = (String) session.getAttribute("contactt"); 
    String mail = (String) session.getAttribute("mail");
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Check Fine</title>
    <link rel="stylesheet" type="text/css" href="stylee.css">
    <%
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); 
        response.setHeader("Pragma", "no-cache"); 
        response.setDateHeader("Expires", 0); 
    %>
    <script>
    function updateFineAmount() {
        var selectedBook = document.getElementById('bookSelection').value;   
        // Directly set the book ID in the hidden field
        document.getElementById('bookId').value = selectedBook; 
    }


        setInterval(function() {
            if (sessionStorage.getItem('loggedOut') === 'true') {
                location.reload();
            }
        }, 5000); 

        window.onload = function() {
            var mail = '<%= mail %>';
            if (mail !== "null") {
                sessionStorage.removeItem('loggedOut');
            }
        }
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
        <center>
            <h1 style="color:white; font-size: 50px; margin:50px 0;">Check Fine</h1>
        </center>

        <h2 style="color:#FFD700;">Check Single Book Fine</h2>
        <%
            String message = (String) session.getAttribute("message");
            if (message != null) {
        %>
            <h3 style="color:white"><%= message %></h3>
        <%
                session.removeAttribute("message"); 
            }
        %>

        <form action="checkFineServlet" method="post">        
            <!-- Updated dropdown label -->
			<label for="bookSelection">Select Book Title & Author:</label>
			<select class="button" id="bookSelection" name="bookSelection" required onchange="updateFineAmount()">
			    <option value="" disabled selected>Select a Book</option>
			    <%
			        List<Map<String, Object>> unpaidBooks = (List<Map<String, Object>>) session.getAttribute("returnunpaidbooks");
			        if (unpaidBooks != null) {
			            for (Map<String, Object> book : unpaidBooks) {
			            	 int bookId = (Integer) book.get("book_id");
			                String title = (String) book.get("title");
			                String author = (String) book.get("author");
			                Double fineAmount = (Double) book.get("fine");
			    %>
			     <option value="<%= bookId %>" data-fine="<%= fineAmount %>">
			        <%= title %> - <%= author %>
			    </option>		    
			    <%
			            }
			        }
			    %>
			</select><br>
			<input type="hidden" id="bookId" name="bookId">
            <input type="hidden" name="action" value="singleFine">
            <button style="color:black; background-color: #98FB98;"  id="submitButton" type="submit" class="button">Check Single Book Fine</button>
        </form>

        <h2 style="color:#FFD700;">Check Total Fine</h2>
        <form action="checkFineServlet" method="post">
            <input type="hidden" name="action" value="TotalFine">
            <button style="color:black; background-color: #98FB98;"  id="submitButton"  type="submit" class="button">Check Total Fine</button>
        </form>

        <% session.removeAttribute("bookid"); %>

        <a style="color:black; background-color: #F08080" href="customerindex.jsp" class="button" id="backButton">Back to Home</a>
    </div>

    <%
        }
    %>
</body>
</html>
