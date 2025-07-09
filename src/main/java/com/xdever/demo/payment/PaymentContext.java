package com.xdever.demo.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

// 上下文类
public class PaymentContext {
    private String paymentId;
    private BigDecimal amount;
    private PaymentStatus status;
    private PaymentState currentState;
    private String transactionId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public PaymentContext(BigDecimal amount) {
        this.paymentId = UUID.randomUUID().toString();
        this.amount = amount;
        this.status = PaymentStatus.CREATED;
        this.currentState = new CreatedState();
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    // Getters and setters
    public String getPaymentId() {
        return paymentId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
        this.updateTime = LocalDateTime.now();
    }

    public PaymentState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(PaymentState currentState) {
        this.currentState = currentState;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    // 状态操作委托
    public void processPayment() {
        currentState.processPayment(this);
    }

    public void handleSuccess() {
        currentState.handleSuccess(this);
    }

    public void handleFailure() {
        currentState.handleFailure(this);
    }

    public void initiateRefund() {
        currentState.initiateRefund(this);
    }

    public void processRefund() {
        currentState.processRefund(this);
    }

    public void completeRefund() {
        currentState.completeRefund(this);
    }

    public void closePayment() {
        currentState.closePayment(this);
    }
}
