<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>

<%
    Integer id = (Integer) session.getAttribute("bookid");
    Double dd = (Double) session.getAttribute("amount");
    String displayfines = (String) session.getAttribute("displayfines"); 
    String message = (String) session.getAttribute("message");
    String mail = (String) session.getAttribute("mail");

    // Lookup book details based on book ID
    List<Map<String, Object>> unpaidBooks = (List<Map<String, Object>>) session.getAttribute("returnunpaidbooks");
    String selectedTitle = "";
    String selectedAuthor = "";

    if (id != null && unpaidBooks != null) {
        for (Map<String, Object> book : unpaidBooks) {
            Integer bookId = (Integer) book.get("book_id");
            if (bookId != null && bookId.equals(id)) {
                selectedTitle = (String) book.get("title");
                selectedAuthor = (String) book.get("author");
                break;
            }
        }
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Pay Fine</title>
    <link rel="stylesheet" type="text/css" href="stylee.css">
    <link rel="stylesheet" type="text/css" href="common.css">
    <script>    
        function updateFineAmount() {
        var selectedBook = document.getElementById('bookSelection').value;
        var selectedOption = document.querySelector(`#bookSelection option[value='${selectedBook}']`);
        var fineAmount = selectedOption ? selectedOption.getAttribute('data-fine') : '';
        document.getElementById('amount').value = fineAmount;
        // Directly set the book ID in the hidden field
        document.getElementById('bookId').value = selectedBook; 
		    }
		
		
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
		    }

		    function closePopup() {
		        document.getElementById('popup').style.display = 'none';
		        document.getElementById('overlay').style.display = 'none';
		        <% session.removeAttribute("message"); %>
		    }
    </script>
</head>
<body>
    <% if (session.getAttribute("mail") == null) { %>
        <% RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp"); %>
        <% dispatcher.forward(request, response); %>
    <% } else { %>

    <div class="container">
        <center><h1 style="font-size: 50px; color: #FFD700;">Pay Fine</h1></center>
        <form action="payFineServlet" method="post">     

		        <% if (displayfines != null && id != null) { %>
		    <label for="bookSelection">Selected Book Title & Author:</label>
		    <input class="button" type="text" id="bookSelection" name="bookSelectionDisplay" 
		           placeholder="Book Title - Author" value="<%= selectedTitle + " - " + selectedAuthor %>" required readonly><br>
		
		    <!-- Hidden field to store the book ID -->
		    <input type="hidden" id="bookId" name="bookId" value="<%= id %>">
		
		    <label for="amount">Fine Amount:</label>
		    <input class="button" type="number" id="amount" name="amount" 
		           placeholder="Enter Amount" value="<%= dd != null ? dd : "" %>" required readonly><br>
		<% } 
		else { %>

            <label for="bookSelection">Select Book Title & Author:</label>
           <select class="button" id="bookSelection" name="bookSelection" required onchange="updateFineAmount()">
			    <option value="" disabled selected>Select a Book</option>
			    <%
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
			</select>
			<br>
            <input type="hidden" id="bookId" name="bookId">
            <label for="amount">Fine Amount:</label>
            <input class="button" type="number" id="amount" name="amount" placeholder="Enter Amount" required readonly><br>

        <% } %>

        <input type="hidden" name="action" value="payFine">
        <button style="color: black; background-color: #98FB98;" id="submitButton" type="submit" class="button">
            Pay Fine
        </button>
        </form><br>

        <a style="color: black; background-color: #F08080" href="customerindex.jsp" class="button" id="backButton">Back to Home</a>
    </div>

    <!-- Popup Message -->
    <div id="overlay" class="overlay"></div>
    <div id="popup" class="popup">
        <p id="popup-message"></p>
        <button onclick="closePopup()">OK</button>   
    </div>

    <% 
    session.removeAttribute("bookid");
    session.removeAttribute("amount");
    }
    session.removeAttribute("message"); %>
</body>
</html>

