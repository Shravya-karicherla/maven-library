<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Customer Dashboard</title>
    <link rel="stylesheet" type="text/css" href="stylee.css">
    <link rel="stylesheet" type="text/css" href="common.css">
    <style>
        .button {
            padding: 10px 20px;
            color: white;
            background-color: #000080;
        }
        .button:hover {
            background-color: #6495ED;
        } 
           
    </style>
    <%
        String mail = (String) session.getAttribute("mail");
        String message = (String) session.getAttribute("message");
    %>
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
        };

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
      <center><h1  style="color:#FFD700; border-bottom: 3px solid #FFD700; display: inline-block; padding-bottom: 10px;">Welcome to Haven Library <%= session.getAttribute("name") %></h1></center>
      <h2 style="color:#FFD700; border-bottom: 3px solid #FFD700; display: inline-block; padding-bottom: 10px;">
	    Customer Dashboard	    
	</h2>
        <form action="borrowBookServlet" method="post">
            <input type="hidden" name="action" value="getBooks">
            <button type="submit" class="button">Borrow Book</button>
        </form>
        <form action="returnBookServlet" method="post">
            <input type="hidden" name="action" value="getcurrentBorrowedBooks">
            <button type="submit" class="button">Return Book</button>
        </form>
        <form action="displayBooksServlet" method="post">
            <button type="submit" class="button">Display Books</button>
        </form>
        <form action="checkFineServlet" method="post">
            <input type="hidden" name="action" value="returnunpaidbooksforcheckfine">
            <button type="submit" class="button">Check Fine</button>
        </form>
        <form action="payFineServlet" method="post">
            <input type="hidden" name="action" value="returnunpaidbooks">
            <button type="submit" class="button">Pay Fine</button>
        </form>
        <form action="borrowBookServlet" method="post">
            <input type="hidden" name="action" value="borrowedbooksforsinglecustomer">
            <button type="submit" class="button">Borrowed Books</button>
        </form>
        <form action="loginLogoutServlet" method="post">
            <input type="hidden" name="action" value="logout">
            <button type="submit"  style="background-color:red" class="button">Logout</button>
        </form>
    </div>
    <%
    //removing sessions
    if(session.getAttribute("recordsPerPage")!=null){session.removeAttribute("recordsPerPage");}
    if(session.getAttribute("startPage")!=null){session.removeAttribute("startPage");}
    if(session.getAttribute("getBooksQuantityGreaterThanZero")!=null){session.removeAttribute("getBooksQuantityGreaterThanZero");}
    if(session.getAttribute("allBooks")!=null){session.removeAttribute("allBooks");}
    if(session.getAttribute("borrowedBooksCustomer")!=null){session.removeAttribute("borrowedBooksCustomer");}
    if(session.getAttribute("getbooks")!=null){session.removeAttribute("getbooks");}
    if(session.getAttribute("allOverdueRecords")!=null){session.removeAttribute("allOverdueRecords");}
    if(session.getAttribute("allBorrowersRecords")!=null){session.removeAttribute("allBorrowersRecords");}
    }
    %>
   <!-- Popup Message -->
    <div id="overlay" class="overlay" style="display: none;"></div>
    <div id="popup" class="popup" style="display: none;">
        <p id="popup-message" style="color:black"></p>
        <button onclick="closePopup()">OK</button>
    </div>

</body>
</html>
