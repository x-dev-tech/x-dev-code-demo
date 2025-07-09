package com.xdever.demo.payment;

import java.time.LocalDateTime;

// 事件日志记录器
public class PaymentEventLogger {
    public static void logEvent(String paymentId, PaymentEventType eventType) {
        // 实际实现中会将事件保存到数据库
        System.out.printf("[%s] 支付ID: %s, 事件: %s%n", LocalDateTime.now(), paymentId, eventType);
    }
}
