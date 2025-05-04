package com.validations;

import javax.servlet.http.HttpSession;

public class SessionValidator {
    // Common utility method for session validation
    public static boolean validateSession(HttpSession session) {
        return session != null && session.getAttribute("mail") != null && session.getAttribute("password") != null;
    }
}
