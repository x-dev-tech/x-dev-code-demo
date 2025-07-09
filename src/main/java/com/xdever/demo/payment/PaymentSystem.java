package com.xdever.demo.payment;


import java.math.BigDecimal;

// 主类 - 演示使用
public class PaymentSystem {
    public static void main(String[] args) {
        // 创建支付订单
        PaymentContext payment = new PaymentContext(new BigDecimal("100.00"));
        System.out.println("创建支付订单: " + payment.getPaymentId());

        // 处理支付
        payment.processPayment();

        // 模拟支付成功
        payment.handleSuccess();

        // 发起退款
        payment.initiateRefund();

        // 处理退款
        // payment.processRefund(); // 假设处理退款的方法

        // 完成退款
        // payment.completeRefund(); // 假设完成退款的方法

        // 关闭订单
        payment.closePayment();
    }
}
