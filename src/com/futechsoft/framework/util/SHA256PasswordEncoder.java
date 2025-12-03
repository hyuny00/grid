package com.futechsoft.framework.util;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.apache.commons.codec.binary.Base64;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256PasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return encryptPassword(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        String encryptedInput = encryptPassword(rawPassword.toString());
        return encryptedInput.equals(encodedPassword);
    }

    /**
     * 비밀번호를 암호화하는 기능(복호화가 되면 안되므로 SHA-256 인코딩 방식 적용)
     *
     * @param data 암호화할 비밀번호
     * @return String result 암호화된 비밀번호
     */
    private String encryptPassword(String data) {
        if (data == null) {
            return "";
        }

        byte[] plainText = data.getBytes();
        byte[] hashValue;
        MessageDigest md;
        String encryptData = null;

        try {
            md = MessageDigest.getInstance("SHA-256");
            hashValue = md.digest(plainText);
            encryptData = new String(Base64.encodeBase64(hashValue));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return encryptData;
    }
}
