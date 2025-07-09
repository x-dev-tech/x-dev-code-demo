package com.xdever.demo.payment;

// 具体状态实现
class PendingState implements PaymentState {
    @Override
    public void processPayment(PaymentContext context) {
        throw new IllegalStateException("支付已在处理中");
    }

    @Override
    public void handleSuccess(PaymentContext context) {
        context.setStatus(PaymentStatus.SUCCESS);
        context.setCurrentState(new SuccessState());
        System.out.println("支付成功: " + context.getPaymentId());
        PaymentEventLogger.logEvent(context.getPaymentId(), PaymentEventType.PAYMENT_SUCCESS);
    }

    @Override
    public void handleFailure(PaymentContext context) {
        context.setStatus(PaymentStatus.FAILED);
        context.setCurrentState(new FailedState());
        System.out.println("支付失败: " + context.getPaymentId());
        PaymentEventLogger.logEvent(context.getPaymentId(), PaymentEventType.PAYMENT_FAILED);
    }

    @Override
    public void initiateRefund(PaymentContext context) {
        throw new IllegalStateException("支付未完成，无法发起退款");
    }

    @Override
    public void processRefund(PaymentContext context) {
        throw new IllegalStateException("支付未完成，无法处理退款");
    }

    @Override
    public void completeRefund(PaymentContext context) {
        throw new IllegalStateException("支付未完成，无法完成退款");
    }

    @Override
    public void closePayment(PaymentContext context) {
        context.setStatus(PaymentStatus.CLOSED);
        context.setCurrentState(new ClosedState());
        System.out.println("订单已关闭: " + context.getPaymentId());
        PaymentEventLogger.logEvent(context.getPaymentId(), PaymentEventType.ORDER_CLOSED);
    }
}
