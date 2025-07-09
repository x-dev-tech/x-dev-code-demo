package com.xdever.demo.utils;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

public class SecureCommunicationExample {
    private static final String AES_ALGORITHM = "AES/GCM/NoPadding";
    private static final String RSA_ALGORITHM = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    private static final int AES_KEY_SIZE = 256;
    private static final int RSA_KEY_SIZE = 2048;
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;

    // 服务器端
    public static class Server {
        public static void main(String[] args) {
            try (ServerSocket serverSocket = new ServerSocket(8888)) {
                System.out.println("服务器已启动，等待连接...");

                // 生成RSA密钥对
                KeyPair rsaKeyPair = generateRSAKeyPair();
                PrivateKey rsaPrivateKey = rsaKeyPair.getPrivate();
                PublicKey rsaPublicKey = rsaKeyPair.getPublic();

                while (true) { // 支持多客户端连接
                    try (Socket socket = serverSocket.accept()) {
                        System.out.println("客户端已连接: " + socket.getInetAddress());

                        DataInputStream dis = new DataInputStream(socket.getInputStream());
                        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

                        // 1. 发送RSA公钥给客户端
                        byte[] publicKeyBytes = publicKeyToBytes(rsaPublicKey);
                        dos.writeInt(publicKeyBytes.length);
                        dos.write(publicKeyBytes);

                        // 2. 接收加密的AES密钥
                        int encryptedKeyLength = dis.readInt();
                        byte[] encryptedAesKey = new byte[encryptedKeyLength];
                        dis.readFully(encryptedAesKey);

                        // 3. 解密AES密钥
                        SecretKey aesKey = decryptAesKey(encryptedAesKey, rsaPrivateKey);

                        // 4. 接收加密消息
                        EncryptedMessage message = receiveEncryptedMessage(dis);
                        String plaintext = decrypt(aesKey, message.iv, message.ciphertext);
                        System.out.println("收到客户端消息: " + plaintext);

                        // 5. 发送响应
                        String response = "服务器已收到: " + plaintext;
                        byte[] encryptedResponse = encrypt(aesKey, response.getBytes(StandardCharsets.UTF_8));
                        sendEncryptedMessage(dos, encryptedResponse);

                        System.out.println("响应已发送");
                    } catch (Exception e) {
                        System.err.println("处理客户端连接时出错: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                System.err.println("服务器启动失败: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // 客户端
    public static class Client {
        public static void main(String[] args) {
            try (Socket socket = new Socket("localhost", 8888)) {
                System.out.println("已连接到服务器");

                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

                // 1. 接收服务器的RSA公钥
                int publicKeyLength = dis.readInt();
                byte[] publicKeyBytes = new byte[publicKeyLength];
                dis.readFully(publicKeyBytes);
                PublicKey rsaPublicKey = bytesToPublicKey(publicKeyBytes);

                // 2. 生成AES会话密钥并使用RSA公钥加密
                SecretKey aesKey = generateAesKey();
                byte[] encryptedAesKey = encryptAesKey(aesKey, rsaPublicKey);

                // 3. 发送加密的AES密钥给服务器
                dos.writeInt(encryptedAesKey.length);
                dos.write(encryptedAesKey);

                // 4. 发送消息
                String message = "Hello, Server! 这是一条加密消息";
                byte[] encryptedMessage = encrypt(aesKey, message.getBytes(StandardCharsets.UTF_8));
                sendEncryptedMessage(dos, encryptedMessage);
                System.out.println("消息已发送");

                // 5. 接收响应
                EncryptedMessage response = receiveEncryptedMessage(dis);
                String plaintextResponse = decrypt(aesKey, response.iv, response.ciphertext);
                System.out.println("收到服务器响应: " + plaintextResponse);

            } catch (Exception e) {
                System.err.println("客户端通信失败: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // 消息结构类
    static class EncryptedMessage {
        byte[] iv;
        byte[] ciphertext;
    }

    // 生成RSA密钥对
    private static KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(RSA_KEY_SIZE);
        return keyPairGenerator.generateKeyPair();
    }

    // 生成AES密钥
    private static SecretKey generateAesKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(AES_KEY_SIZE);
        return keyGenerator.generateKey();
    }

    // 使用RSA公钥加密AES密钥
    private static byte[] encryptAesKey(SecretKey aesKey, PublicKey rsaPublicKey)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
        return cipher.doFinal(aesKey.getEncoded());
    }

    // 使用RSA私钥解密AES密钥
    private static SecretKey decryptAesKey(byte[] encryptedAesKey, PrivateKey rsaPrivateKey)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
        byte[] aesKeyBytes = cipher.doFinal(encryptedAesKey);
        return new SecretKeySpec(aesKeyBytes, "AES");
    }

    // AES-GCM加密
    private static byte[] encrypt(SecretKey key, byte[] plaintext)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IOException, IllegalBlockSizeException, BadPaddingException {
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[GCM_IV_LENGTH];
        secureRandom.nextBytes(iv);

        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);

        byte[] ciphertext = cipher.doFinal(plaintext);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(iv);
        outputStream.write(ciphertext);
        return outputStream.toByteArray();
    }

    // AES-GCM解密
    private static String decrypt(SecretKey key, byte[] iv, byte[] ciphertext)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);

        byte[] decryptedBytes = cipher.doFinal(ciphertext);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    // 发送加密消息
    private static void sendEncryptedMessage(DataOutputStream dos, byte[] encryptedData) throws IOException {
        dos.writeInt(encryptedData.length);
        dos.write(encryptedData);
    }

    // 接收加密消息
    private static EncryptedMessage receiveEncryptedMessage(DataInputStream dis) throws IOException {
        int encryptedLength = dis.readInt();
        byte[] encryptedData = new byte[encryptedLength];
        dis.readFully(encryptedData);

        EncryptedMessage message = new EncryptedMessage();
        message.iv = Arrays.copyOfRange(encryptedData, 0, GCM_IV_LENGTH);
        message.ciphertext = Arrays.copyOfRange(encryptedData, GCM_IV_LENGTH, encryptedLength);
        return message;
    }

    // 公钥转字节数组
    private static byte[] publicKeyToBytes(PublicKey publicKey) {
        return publicKey.getEncoded();
    }

    // 字节数组转公钥
    private static PublicKey bytesToPublicKey(byte[] keyBytes) throws GeneralSecurityException {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    // 私钥转字节数组
    private static byte[] privateKeyToBytes(PrivateKey privateKey) {
        return privateKey.getEncoded();
    }

    // 字节数组转私钥
    private static PrivateKey bytesToPrivateKey(byte[] keyBytes) throws GeneralSecurityException {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }
}
