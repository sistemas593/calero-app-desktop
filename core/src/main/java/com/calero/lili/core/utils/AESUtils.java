package com.calero.lili.core.utils;


import com.calero.lili.core.errors.exceptions.GeneralException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESUtils {


    private static final String KEY_ACCESS = "a5cab76dd2f64c0abf2ec7df13723604";

    public static String encrypt(String password) {

        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec secretKey = new SecretKeySpec(KEY_ACCESS.getBytes(), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(password.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception ex) {
            throw new GeneralException("Err: " + ex.getMessage());
        }
    }

    public static String decrypt(String passEncrypt) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec secretKey = new SecretKeySpec(KEY_ACCESS.getBytes(), "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(passEncrypt));
            return new String(decrypted);
        } catch (Exception ex) {
            throw new GeneralException("Err: " + ex.getMessage());
        }
    }

}
