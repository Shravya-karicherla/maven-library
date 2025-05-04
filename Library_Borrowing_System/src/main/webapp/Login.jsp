<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Insert title here</title>
   <link rel="stylesheet" type="text/css" href="stylee.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css" rel="stylesheet">
    <%
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); 
        response.setHeader("Pragma", "no-cache"); 
        response.setDateHeader("Expires", 0); 
    %>
</head>
<body>
    <div class="container">
        <center>
            <h1 style="color:#FFD700;"><i class="fas fa-sign-in-alt"></i> Login</h1>
        </center>
        <%
            String message = (String) session.getAttribute("message");
            if (message != null) {
                String col = (String) session.getAttribute("color");
                if (col != null) {
        %>
        <p style="color:#00FF00;">


            <i class="fa fa-check" aria-hidden="true"></i> <%= message %>
        </p>
        <%
                    session.removeAttribute("color");
                } else {
        %>
        <p style="color:red;">
            <i class="fa fa-times-circle" aria-hidden="true"></i> <%= message %>
        </p>
        <%
                }
                session.removeAttribute("message");
            }
        %>
        
     <div class="form-container">
        <form action="loginLogoutServlet" method="post">
            <div class="form-row">
            <label for="title">Mail:</label>
            <input class="button" type="text" id="mail" name="mail" required><br>
            </div>
            <div class="form-row">
            <label for="author">Password:</label>
            <input class="button" type="password" id="pass" name="pass" required><br>
            </div>
            <input type="hidden" name="action" value="Login">
            <input style="color:black;background-color: #98FB98;" class="button" id="submitButton" type="submit" value="Login">
        </form>
      </div>    
        <%
            if (session.getAttribute("whoIsLogin") != null && session.getAttribute("whoIsLogin").equals("customer")) {
        %>
        <p>Not a regular customer? <a style="color:#FFD700;" href="register.jsp">Sign Up</a></p>
        <%
            }
        %>
        <a style="color:black;background-color: #F08080" href="index.jsp" class="button" id="backButton">Back to Home</a><br>
    </div>
</body>
</html>
