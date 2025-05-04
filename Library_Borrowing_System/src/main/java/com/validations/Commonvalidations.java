package com.validations;

public class Commonvalidations {
	//checking valid name 
    public static boolean isValidTitle(String name) {	
        return name != null && name.matches("[A-Za-z0-9 ]+" );
       }
  //checking valid author name 
    public static boolean isValidName(String name) {	
        return name != null && name.matches("[A-Za-z ]+" );
       }
	//validating contact
    public static boolean isValidContactInfo(String contact_info) {	
	        return contact_info != null && contact_info.matches("^[6-9]\\d{9}$"); 
	       }
}
