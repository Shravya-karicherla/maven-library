<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String role = request.getParameter("role");
    if (role != null) {
        if ("owner".equals(role)) {
            session.setAttribute("whoIsLogin", "owner");
        } else if ("customer".equals(role)) {
            session.setAttribute("whoIsLogin", "customer");
        } else {
            session.setAttribute("message", "Invalid role selected");
        }
        response.sendRedirect("Login.jsp");
        return; 
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Welcome to Library</title>
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600&display=swap" rel="stylesheet">
    <!-- Font Awesome Icons -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="index.css"> 
</head>
<body>

    <!-- Navigation Bar -->
    <div class="navbar">
        <h1><i class="fas fa-book"></i>   Haven Library</h1>
        <ul>
            <li><a href="#"><i class="fas fa-home"></i> Home</a></li>
            <li><a href="#"><i class="fas fa-info-circle"></i> About</a></li>
            <li><a href="#"><i class="fas fa-phone-alt"></i> Contact</a></li>
        </ul>
    </div>

    <!-- Main Container -->
    <div class="container">
        <h1>Welcome to the Library</h1>
        <p>Explore books, manage accounts, and dive into a world of knowledge. Select your role below to continue:</p>
        <form action="" method="post">
            <input type="hidden" name="role" value="owner">
            <button type="submit" class="button"><i class="fas fa-user-shield"></i> I am an Owner</button>
        </form>
        <form action="" method="post">
            <input type="hidden" name="role" value="customer">
            <button type="submit" class="button"><i class="fas fa-user"></i> I am a Customer</button>
        </form>
    </div>


</body>
</html>
