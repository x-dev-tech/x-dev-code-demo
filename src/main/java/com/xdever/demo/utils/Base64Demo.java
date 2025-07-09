package com.xdever.demo.utils;

import java.util.Base64;

public class Base64Demo {

    public static void main(String[] args) {
        // 待编码的原始数据
        String originalData = "Hello, Base64!";

        // 编码
        String encodedData = Base64.getEncoder().encodeToString(originalData.getBytes());
        System.out.println("编码结果：" + encodedData);

        // 解码
        byte[] decodedData = Base64.getDecoder().decode(encodedData);
        System.out.println("解码结果：" + new String(decodedData));

    }

}
