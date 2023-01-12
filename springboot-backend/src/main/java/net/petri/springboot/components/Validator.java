package net.petri.springboot.components;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {
    public static boolean emailCheck(String email) {
        String regex = "^(?=.{1,64}@)[A-Za-z\\d_-]+(\\.[A-Za-z\\d_-]+)*@"
                + "[^-][A-Za-z\\d-]+(\\.[A-Za-z\\d-]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();

    }
}
