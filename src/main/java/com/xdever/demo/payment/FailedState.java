package com.xdever.demo.payment;

public class FailedState implements PaymentState {
    @Override
    public void processPayment(PaymentContext context) {

    }

    @Override
    public void handleSuccess(PaymentContext context) {

    }

    @Override
    public void handleFailure(PaymentContext context) {

    }

    @Override
    public void initiateRefund(PaymentContext context) {

    }

    @Override
    public void processRefund(PaymentContext context) {

    }

    @Override
    public void completeRefund(PaymentContext context) {

    }

    @Override
    public void closePayment(PaymentContext context) {

    }
}
