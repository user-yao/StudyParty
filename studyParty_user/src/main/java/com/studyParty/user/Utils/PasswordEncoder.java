package com.studyParty.user.Utils;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoder {

    public static String encode(String rawPassword) {
        System.out.println("加密"+rawPassword);
        return BCrypt.withDefaults().hashToString(12, rawPassword.toCharArray());
    }

    public static boolean matches(String rawPassword, String encodedPassword) {
        return BCrypt.verifyer().verify(rawPassword.toCharArray(), encodedPassword).verified;
    }
}