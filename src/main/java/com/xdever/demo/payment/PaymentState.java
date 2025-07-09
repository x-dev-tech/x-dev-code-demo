package com.xdever.demo.payment;

// 状态接口
public interface PaymentState {
    void processPayment(PaymentContext context);

    void handleSuccess(PaymentContext context);

    void handleFailure(PaymentContext context);

    void initiateRefund(PaymentContext context);

    void processRefund(PaymentContext context);

    void completeRefund(PaymentContext context);

    void closePayment(PaymentContext context);
}