package com.example.bank_service.utils;
import org.apache.commons.codec.digest.DigestUtils;

public class HashUtil {
    public static String hashPassword(String password) {
        return DigestUtils.sha256Hex(password);
    }
}
