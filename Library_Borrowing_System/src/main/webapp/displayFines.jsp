<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Insert title here</title>
    <link rel="stylesheet" type="text/css" href="stylee.css">
    <link rel="stylesheet" type="text/css" href="table.css">
    <%
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); 
        response.setHeader("Pragma", "no-cache"); 
        response.setDateHeader("Expires", 0); 
        List<Map<String, Object>> finesList = (List<Map<String, Object>>) session.getAttribute("finesList");
        String message = (String) session.getAttribute("messagee");
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
        <%
            if (message != null && message.startsWith("No Fines")) {
        %>
        <h1 style="color:white"><%= message %></h1>
        <%
                session.removeAttribute("messagee");
            } else if (message != null && message.startsWith("totalfine")) {
        %>
        <center><h1 style="color:#FFD700;">Fine Details</h1></center>
        <table>
	    <thead>
	        <tr>
	            <th>Title</th>
	            <th>Author</th>
	            <th>Fine Amount</th>
	            <th>Clear Payment</th>
	        </tr>
	    </thead>
	    <tbody>
	        <%
	            if (finesList != null && !finesList.isEmpty()) {
	                for (Map<String, Object> fineDetails : finesList) {
	                    if (fineDetails.containsKey("title")) {
	        %>
	        <tr>
	            <td><%= fineDetails.get("title") %></td>
	            <td><%= fineDetails.get("author") %></td>
	            <td><%= fineDetails.get("fine") %></td>
	            <td>
	                <form action="checkFineServlet" method="post">
	                    <input type="hidden" name="bookid" value="<%= fineDetails.get("book_id") %>">
	                    <input type="hidden" name="title" value="<%= fineDetails.get("title") %>">
	                    <input type="hidden" name="author" value="<%= fineDetails.get("author") %>">
	                    <input type="hidden" name="amount" value="<%= fineDetails.get("fine") %>">
	                    <input type="hidden" name="action" value="displayfinestopay">
	                    <input style="background-color: darkseagreen" type="submit" value="Pay">
	                </form>
	            </td>
	        </tr>
	        <%
	                    } else if (fineDetails.containsKey("totalFine")) {
	        %>
	        <tr>
	            <td colspan="4" style="color:red"><strong>Total Fine: <%= fineDetails.get("totalFine") %></strong></td>
	        </tr>
	        <%
	                    }
	                }
	            } else {
	        %>
	        <tr>
	            <td colspan="4">No fines found.</td>
	        </tr>
	        <%
	            }
	        %>
	    </tbody>
	</table>

        <%
            } else if (message != null && message.startsWith("Fine")) {
        %>
        <h1 style="color:white"><%= message %></h1>
        <form action="checkFineServlet" method="post">
            <input type="hidden" name="bookid" value="<%= session.getAttribute("bookid") %>">
            <input type="hidden" name="amount" value="<%= session.getAttribute("fine") %>">
            <input type="hidden" name="action" value="displayfinestopay">
            <input style="background-color: darkseagreen" type="submit" value="Pay">
        </form><br>
        <%
            }
        %>
        
        <a class="button" style="color:black;background-color: #F08080;text-decoration: none" href="checkFine.jsp" id="backButton">Back to Home</a>
    </div>
<%
    }
%>
</body>
</html>
