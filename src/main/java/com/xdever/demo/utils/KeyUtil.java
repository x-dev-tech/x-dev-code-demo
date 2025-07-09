package com.xdever.demo.utils;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class KeyUtil {

    private static final String ALGORITHM = "AES";

    private static SecretKey key = null;

    // 生成密钥
    static {
        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        keyGenerator.init(256);
        key = keyGenerator.generateKey();
    }

    public static SecretKey getKey() throws NoSuchAlgorithmException {
        System.out.println(Base64.getEncoder().encodeToString(key.getEncoded()));
        return key;
    }
}
