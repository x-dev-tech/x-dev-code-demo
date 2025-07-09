package com.xdever.demo.payment;

// 支付事件类型
public enum PaymentEventType {
    PAYMENT_INITIATED,
    PAYMENT_SUCCESS,
    PAYMENT_FAILED,
    REFUND_INITIATED,
    REFUND_SUCCESS,
    REFUND_FAILED,
    ORDER_CLOSED
}
