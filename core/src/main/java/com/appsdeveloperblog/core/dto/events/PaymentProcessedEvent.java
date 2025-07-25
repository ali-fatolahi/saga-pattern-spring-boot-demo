package com.appsdeveloperblog.core.dto.events;

import java.util.UUID;

public class PaymentProcessedEvent {
  private UUID orderId;
  private String paymentId;

  public PaymentProcessedEvent() {
    // Default constructor for deserialization
  }

  public PaymentProcessedEvent(UUID orderId, String paymentId) {
    this.orderId = orderId;
    this.paymentId = paymentId;
  }

  public UUID getOrderId() {
    return orderId;
  }

  public void setOrderId(UUID orderId) {
    this.orderId = orderId;
  }

  public String getPaymentId() {
    return paymentId;
  }

  public void setPaymentId(String paymentId) {
    this.paymentId = paymentId;
  }
}

