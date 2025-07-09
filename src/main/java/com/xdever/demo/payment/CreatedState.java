package com.xdever.demo.payment;

// 具体状态实现
class CreatedState implements PaymentState {
    @Override
    public void processPayment(PaymentContext context) {
        context.setStatus(PaymentStatus.PENDING);
        context.setCurrentState(new PendingState());
        System.out.println("支付处理中: " + context.getPaymentId());
        // 记录支付事件
        PaymentEventLogger.logEvent(context.getPaymentId(), PaymentEventType.PAYMENT_INITIATED);
    }

    @Override
    public void handleSuccess(PaymentContext context) {
        throw new IllegalStateException("支付尚未处理，无法标记为成功");
    }

    @Override
    public void handleFailure(PaymentContext context) {
        throw new IllegalStateException("支付尚未处理，无法标记为失败");
    }

    @Override
    public void initiateRefund(PaymentContext context) {
        throw new IllegalStateException("支付尚未处理，无法发起退款");
    }

    @Override
    public void processRefund(PaymentContext context) {
        throw new IllegalStateException("支付尚未处理，无法处理退款");
    }

    @Override
    public void completeRefund(PaymentContext context) {
        throw new IllegalStateException("支付尚未处理，无法完成退款");
    }

    @Override
    public void closePayment(PaymentContext context) {
        context.setStatus(PaymentStatus.CLOSED);
        context.setCurrentState(new ClosedState());
        System.out.println("订单已关闭: " + context.getPaymentId());
        PaymentEventLogger.logEvent(context.getPaymentId(), PaymentEventType.ORDER_CLOSED);
    }
}
