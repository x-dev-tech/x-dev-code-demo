package com.xdever.demo.utils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * 网络通信加密（AES-256-GCM）
 * 使用AES-GCM模式实现安全通信的示例，包括服务器端和客户端
 */
public class SecureCommunicationServer extends BasedSecureCommunication{

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8888)){
            System.out.println("服务器已启动，等待连接...");
            Socket socket = serverSocket.accept();
            System.out.println("客户端已连接");

            // 生成共享密钥（实际场景中需通过安全通道交换）
            SecretKey key = generateKey();

            // 接收加密消息
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            int ivLength = dis.readInt();
            byte[] iv = new byte[ivLength];
            dis.readFully(iv);

            int ciphertextLength = dis.readInt();
            byte[] ciphertext = new byte[ciphertextLength];
            dis.readFully(ciphertext);

            //解密消息
            String decryptedMessage = decrypt(key, iv, ciphertext);
            System.out.println("接收到客户消息：" + decryptedMessage);

            // 发送响应消息
            String response = "消息已收到：" + decryptedMessage;
            byte[] encryptedResponse = encrypt(key, response.getBytes(StandardCharsets.UTF_8));

//            // 发送消息
//            String message = "Hello, Server! 这是一条加密消息";
//            byte[] encryptedMessage = encrypt(key, message.getBytes(StandardCharsets.UTF_8));
//
//            DataOutputStream dos = new DataOutputStream(so)


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
