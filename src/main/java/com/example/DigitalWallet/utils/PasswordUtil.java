package com.example.DigitalWallet.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    // Hash a password
    public static String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(12)); // 12 is the workload factor
    }

    // Verify a password
    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
}