package com.validations;

public class CustomerValidations {
    // Validating password
    public static boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>])[A-Za-z\\d!@#$%^&*(),.?\":{}|<>]{8,}$";
        return password != null && password.matches(passwordRegex);
    }
    public static boolean isValidEmail(String mail) {
        if (mail == null) return false;
        return mail.matches("^[a-z](?:[a-z0-9]*(?:\\.[a-z0-9]+)?)@(gmail.com|yahoo.net|gameshastra.in)$");
    }
}
