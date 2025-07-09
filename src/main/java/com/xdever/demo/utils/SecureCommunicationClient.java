package com.xdever.demo.utils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * 网络通信加密（AES-256-GCM）
 * 使用AES-GCM模式实现安全通信的示例，包括服务器端和客户端
 */
public class SecureCommunicationClient extends BasedSecureCommunication {

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 9999);
            System.out.println("已连接到服务器");

            // 生成共享密钥（实际场景中需通过安全通道交换）
            SecretKey key = generateKey();

            // 发送消息
            String message = "Hello, Server! 这是一条加密消息";
            byte[] encryptedMessage = encrypt(key, message.getBytes(StandardCharsets.UTF_8));

            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeInt(encryptedMessage.length);
            dos.write(encryptedMessage);

            // 接收响应
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            int responseLength = dis.readInt();
            byte[] encryptedResponse = new byte[responseLength];
            dis.readFully(encryptedResponse);

            String decryptedResponse = decrypt(key, encryptedResponse);
            System.out.println("收到服务器响应: " + decryptedResponse);
        } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException | IOException |
                 NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
            e.printStackTrace();;
            throw new RuntimeException(e);
        }
    }

}
