package com.xdever.demo.payment;


// 具体状态实现
class SuccessState implements PaymentState {
    @Override
    public void processPayment(PaymentContext context) {
        throw new IllegalStateException("支付已成功，不能重复处理");
    }

    @Override
    public void handleSuccess(PaymentContext context) {
        throw new IllegalStateException("支付已成功");
    }

    @Override
    public void handleFailure(PaymentContext context) {
        throw new IllegalStateException("支付已成功，不能标记为失败");
    }

    @Override
    public void initiateRefund(PaymentContext context) {
        context.setStatus(PaymentStatus.REFUNDING);
        context.setCurrentState(new RefundingState());
        System.out.println("退款处理中: " + context.getPaymentId());
        PaymentEventLogger.logEvent(context.getPaymentId(), PaymentEventType.REFUND_INITIATED);
    }

    @Override
    public void processRefund(PaymentContext context) {
        throw new IllegalStateException("请先发起退款");
    }

    @Override
    public void completeRefund(PaymentContext context) {
        throw new IllegalStateException("请先发起退款");
    }

    @Override
    public void closePayment(PaymentContext context) {
        context.setStatus(PaymentStatus.CLOSED);
        context.setCurrentState(new ClosedState());
        System.out.println("订单已关闭: " + context.getPaymentId());
        PaymentEventLogger.logEvent(context.getPaymentId(), PaymentEventType.ORDER_CLOSED);
    }
}
