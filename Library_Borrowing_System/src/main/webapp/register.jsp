<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Register</title>
<link rel="stylesheet" type="text/css" href="stylee.css">
<style> 
html,body
{
margin:0;
width:100%;
} 
    h1 {
        color: #FFD700;
        margin-bottom: 20px;
    }

    .form-group {
        color:black;
        margin-bottom: 15px;
        text-align: left;
        
    }

    label {
        display: block;
        margin-bottom: 5px;
        font-weight: bold;
    }

    .button {
        width: 95%;
        padding: 7px;
        margin-bottom: 10px;
        border: 1px solid #ccc;
        border-radius: 5px;
        box-sizing: border-box;
    }

    .button:focus {
        border-color: #007BFF;
    }

    .submit-button {
        background-color: #98FB98;
        color: black;
        border: none;
        cursor: pointer;
        margin-top: 10px;
    }

    .submit-button:hover {
        background-color: #32CD32;
    }
    .validation-message {
        color: #FF4500;
        font-size: 15px;
        display: none;
    }
        .error-highlight {
        border: 2px solid red;
        background-color: #ffcccc;
    }
   
</style>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);
    String mail = (String) session.getAttribute("mail");
%>
<script>
function validateContact() {
    var contact = document.getElementById('contact_info').value;
    var contactMessageLength = document.getElementById('contact-validation-length');
    var contactMessageStart = document.getElementById('contact-validation-start');

    // Check if it contains exactly 10 digits
    if (!/^\d{10}$/.test(contact)) {
        contactMessageLength.style.display = 'block'; 
    } else {
        contactMessageLength.style.display = 'none'; 
    }

    // Check if it starts with 6, 7, 8, or 9
    if (!/^[6-9]/.test(contact)) {
        contactMessageStart.style.display = 'block'; 
    } else {
        contactMessageStart.style.display = 'none'; 
    }
}



function validateEmail() {
    var email = document.getElementById('mail').value;
    var emailMessage = document.getElementById('email-validation');

    // Regular expression matching the Java validation logic
    var emailRegex = /^[a-z](?:[a-z0-9]*(?:\.[a-z0-9]+)?)@(gmail\.com|yahoo\.net|gameshastra\.in)$/;

    // Validate email
    if (!emailRegex.test(email)) {
        emailMessage.style.display = 'block';
    } else {
        emailMessage.style.display = 'none';
    }
}

function validateName() {
    var name = document.getElementById('name').value;
    var nameAlphabet = document.getElementById('alphabet-validation');
    var nameChar = document.getElementById('char-validation');

    // Check if name meets the validation criteria
    if (name.length < 3) {
    	nameChar.style.display = 'block';
    	nameChar.textContent = "Name must be at least 3 characters long.";
    }else {
    	nameChar.style.display = 'none'; // Hide the message when valid
    } 
    if (!/^[A-Za-z]+$/.test(name)) {
    	nameAlphabet.style.display = 'block';
    	nameAlphabet.textContent = "Name must contain only alphabets.";
    } else {
    	nameAlphabet.style.display = 'none'; // Hide the message when valid
    }
}


function validatePassword() {
    var password = document.getElementById('password').value;   
    var capitalMessage = document.getElementById('capital-validation');
    var digitMessage = document.getElementById('digit-validation');
    var specialMessage = document.getElementById('special-validation');
    var lengthMessage = document.getElementById('length-validation');

    // Check for at least one capital letter
    if (/[A-Z]/.test(password)) {
        capitalMessage.style.display = 'none'; 
    } else {
        capitalMessage.style.display = 'block'; 
    }

    // Check for at least one digit
    if (/\d/.test(password)) {
        digitMessage.style.display = 'none';
    } else {
        digitMessage.style.display = 'block';
    }

    // Check for at least one special character
    if (/[!@#$%^&*]/.test(password)) {
        specialMessage.style.display = 'none';
    } else {
        specialMessage.style.display = 'block';
    }

    // Check for minimum length of 8 characters
    if (password.length >= 8) {
        lengthMessage.style.display = 'none';
    } else {
        lengthMessage.style.display = 'block';
    }
}

</script>
</head>
<body>

<div class="container">
<center><h1>Register</h1></center>
<%
    String message = (String) session.getAttribute("message");
    if (message != null) {
%>
    <h3 style="color:#dd7474;"><%= message %></h3>
<%
        session.removeAttribute("message"); // Clear message after displaying
    }
%>
<div class="form-container">
<form action="registerServlet" method="post">
	   <div class="form-group">
	    <label for="contact_info">Contact Info:</label>
	    <input class="button <%= session.getAttribute("highlightContact") != null ? "error-highlight" : "" %>" 
	           type="text" id="contact_info" name="contact_info"  
	           value="<%= session.getAttribute("inputContact") != null ? session.getAttribute("inputContact") : "" %>"  
	           required oninput="validateContact()">
	
		    <!-- Separate validation messages -->
		    <span id="contact-validation-length" class="validation-message" style="display:none; color:red;">
		        Must contain exactly 10 digits.
		    </span>
		    <span id="contact-validation-start" class="validation-message" style="display:none; color:red;">
		        Must start with 6, 7, 8, or 9.
		    </span>
		</div>
		   <div class="form-group">
		    <label for="name">Name:</label>
		    <input class="button" type="text" id="name" name="name"  
		           value="<%= session.getAttribute("inputName") != null ? session.getAttribute("inputName") : "" %>"  
		           required oninput="validateName()" maxlength="45" minlength="3">
		    <span id="alphabet-validation" class="validation-message" style="display:none; color:red;">
		        Name must contain only alphabets.
		    </span>
		    <span id="char-validation" class="validation-message" style="display:none; color:red;">
		        Name must be at least 3 characters.
		    </span>
		</div>

	    <div class="form-group">
	        <label for="mail">Email:</label>
	        <input class="button <%= session.getAttribute("highlightMail") != null ? "error-highlight" : "" %>" type="email" id="mail" name="mail" 
	         value="<%= session.getAttribute("inputMail") != null ? session.getAttribute("inputMail") : "" %>" required oninput="validateEmail()" maxlength="45">
	        <span id="email-validation" class="validation-message">Email should end with @gmail.com or @yahoo.net or @gameshastra.in</span>
	    </div>

	    <div class="form-group">
	        <label for="password">Password:</label>
	        <input class="button  <%= session.getAttribute("highlightpassword") != null ? "error-highlight" : "" %>" type="password" id="password" name="password" 
	        required oninput="validatePassword()">
		    <!-- Validation messages -->
		    <span id="capital-validation" class="validation-message">Must contain at least 1 capital letter.</span>
		    <span id="digit-validation" class="validation-message">Must contain at least 1 digit.</span>
		    <span id="special-validation" class="validation-message">Must contain at least 1 special character (!@#$%^&*).</span>
		    <span id="length-validation" class="validation-message">Must be at least 8 characters long.</span>
	     </div>
	    <div class="form-group">
	        <label for="confirm_password">Confirm Password:</label>
	        <input class="button  <%= session.getAttribute("highlightpassword") != null ? "error-highlight" : "" %>" type="password" id="confirm_password"
	         name="confirm_password" required>
	    </div>
	    <input type="hidden" name="action" value="Register">
	    <input class="button submit-button" type="submit" value="Register">
</form><br>
</div>
<p>If you already have an account? <a style="color:#FFD700;" href="Login.jsp">LogIn</a></p><br>
<a href="index.jsp" id="backButton">Back to Home</a>
</div>
<%  session.removeAttribute("inputMail");
	session.removeAttribute("inputName");
	session.removeAttribute("inputContact");
    session.removeAttribute("highlightContact");
    session.removeAttribute("highlightMail");
    session.removeAttribute("highlightpassword");
%>
</body>
</html>
