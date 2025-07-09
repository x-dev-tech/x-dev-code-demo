package com.xdever.demo.utils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 文件加密工具（AES-256-CBC）
 * 使用 AES-256-CBC 模式加密文件程序，包含文件读取、加密处理和写入操作
 * 这个例子使用 CBC 模式（需要初始化向量 IV）和 PKCS5Padding 填充方式，适合加密大文件。
 * 注意 IV 需要随密文一起存储或传输。
 */
public class FileEncryptor {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    // 生成随机密钥
    public static SecretKey generateKey(int keySize) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(keySize);
        return keyGenerator.generateKey();
    }

    // 生成初始化向量
    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        SecureRandom secureRandom = new SecureRandom();
        return new IvParameterSpec(iv);
    }

    // 加密文件
    public static void encryptFile(SecretKey key, IvParameterSpec iv, File inputFile, File outputFile) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IOException {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile);
             CipherOutputStream cos = new CipherOutputStream(fos, cipher)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                cos.write(buffer, 0, bytesRead);
            }
        }
    }

    // 解密文件
    public static void decryptFile(SecretKey key, IvParameterSpec iv, File inputFile, File outputFile) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);

        try (FileInputStream fis = new FileInputStream(inputFile);
             CipherInputStream cis = new CipherInputStream(fis, cipher);
             FileOutputStream fos = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = cis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }
    }

    // 把密钥转换成字符串（用于存储或传输密钥）
    public static String keyToString(SecretKey secretKey) {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    // 把字符串转换成密钥（用于后续加解密）
    public static SecretKey stringToKey(String keyString) {
        byte[] keyBytes = Base64.getDecoder().decode(keyString);
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

    public static void main(String[] args) {
        try {
            // 生成密钥
            SecretKey key = generateKey(256);

            // 生成IV
            IvParameterSpec iv = generateIv();

            // 文件路径
            File originalFile = new File("D:\\x-dev\\github\\x-dev-code-demo\\src\\main\\resources\\example.txt");
            File encryptedFile = new File("D:\\x-dev\\github\\x-dev-code-demo\\src\\main\\resources\\example.encrypted");
            File decryptedFile = new File("D:\\x-dev\\github\\x-dev-code-demo\\src\\main\\resources\\example.decrypted");

            // 加密文件
            encryptFile(key, iv, originalFile, encryptedFile);
            System.out.println("文件加密完成");

            // 解密文件
            decryptFile(key, iv, encryptedFile, decryptedFile);
            System.out.println("文件解密完成");

            // 保存密钥
            String keyString = keyToString(key);
            System.out.println("密钥=" + keyString);

            // 加载密钥
            SecretKey loadKey = stringToKey(keyString);
            System.out.println("密钥转换成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
