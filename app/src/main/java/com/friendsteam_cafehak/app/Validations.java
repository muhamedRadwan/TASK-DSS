/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.friendsteam_cafehak.app;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author moroclash
 */
public class Validations {
    private static Validations validate = null;
    private final String Password_PATTERN = "^((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%])(?=\\S+$).{6,20})";
    /*
            (?=.*\d)		#   must contains one digit from 0-9
            (?=.*[a-z])		#   must contains one lowercase characters
            (?=.*[A-Z])		#   must contains one uppercase characters
            (?=.*[@#$%])		#   must contains one special symbols in the list "@#$%"
            .		#     match anything with previous condition checking
            {6,20}	#        length at least 6 characters and maximum of 20
       */
    private final String Date_PATTERN = "(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\\\d\\\\d)";
    /*
    (			#start of group #1
 0?[1-9]		#  01-09 or 1-9
 |                  	#  ..or
 [12][0-9]		#  10-19 or 20-29
 |			#  ..or
 3[01]			#  30, 31
) 			#end of group #1
  /			#  follow by a "/"
   (			#    start of group #2
    0?[1-9]		#	01-09 or 1-9
    |			#	..or
    1[012]		#	10,11,12
    )			#    end of group #2
     /			#	follow by a "/"
      (			#	  start of group #3
       (19|20)\\d\\d	#	    19[0-9][0-9] or 20[0-9][0-9]
       )		#	  end of group #3
    */
    private final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private Pattern pattern;
    private Matcher matcher;

    private Validations() {
    }

    public static Validations Get_Validations() {
        if (validate == null)
            validate = new Validations();
        return validate;
    }

    public Pattern Email_compile() {
        return pattern = Pattern.compile(EMAIL_PATTERN);
    }

    public Pattern Date_compile() {
        return pattern = Pattern.compile(Date_PATTERN);
    }

    public Pattern Password_compile() {
        return pattern = Pattern.compile(Password_PATTERN);
    }

    public boolean Is_digit(String Word) {
        pattern = Pattern.compile("^[0-9]*$");
        matcher = pattern.matcher(Word);
        return matcher.matches();
    }

    public boolean Is_alpha(String Word) {
        pattern = Pattern.compile("([a-zA-Z]{2,30}\\s*)+");
        matcher = pattern.matcher(Word);
        return matcher.matches();
    }


    public boolean Is_email(String Word) {    //anything@3digit at least .com
        pattern = Email_compile();
        matcher = pattern.matcher(Word);
        return matcher.matches();
    }

    /*       ^                 # start-of-string
            (?=.*[0-9])       # a digit must occur at least once
            (?=.*[a-z])       # a lower case letter must occur at least once
            (?=.*[A-Z])       # an upper case letter must occur at least once
            (?=.*[@#$%^&+=])  # a special character must occur at least once
            (?=\S+$)          # no whitespace allowed in the entire string
            .{8,}             # anything, at least eight places though
            $                 # end-of-string*/
    public boolean Is_passord(String Word) {   //9 digit
        if (Word != null) {
            String s = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,}$";
            pattern = Pattern.compile(s);
            matcher = pattern.matcher(Word);
            return matcher.matches();
        } else return false;


    }

    public boolean Is_date(String Word) {
        //1920-02-30 OR 30-02-1964
        pattern = Date_compile();
        matcher = pattern.matcher(Word);
        return matcher.matches();
    }

    public boolean Is_double(String Word) {
        try {
            Double.parseDouble(Word);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //omar
    public boolean Is_alphanumaric(String string) {
        pattern = Pattern.compile("^[a-zA-Z0-9]*$");
        matcher = pattern.matcher(string);
        return matcher.matches();
    }

    //omar
    public boolean Is_gender(String gender) {
        return gender.equalsIgnoreCase("1") || gender.equalsIgnoreCase("2");
    }


}