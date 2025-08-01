package com.appsdeveloperblog.core.dto.commands;

import java.math.BigDecimal;
import java.util.UUID;

public class ProcessPaymentCommand {
  private UUID orderId;
  private UUID productId;
  private BigDecimal productPrice;
  private Integer productQuantity;

  public ProcessPaymentCommand() {
    // Default constructor for deserialization
  }

  public ProcessPaymentCommand(UUID orderId, UUID productId, BigDecimal productPrice, Integer productQuantity) {
    this.orderId = orderId;
    this.productId = productId;
    this.productPrice = productPrice;
    this.productQuantity = productQuantity;
  }

  public UUID getOrderId() {
    return orderId;
  }

  public UUID getProductId() {
    return productId;
  }

  public BigDecimal getProductPrice() {
    return productPrice;
  }

  public Integer getProductQuantity() {
    return productQuantity;
  }

  public void setOrderId(UUID orderId) {
    this.orderId = orderId;
  }

  public void setProductId(UUID productId) {
    this.productId = productId;
  }

  public void setProductPrice(BigDecimal productPrice) {
    this.productPrice = productPrice;
  }

  public void setProductQuantity(Integer productQuantity) {
    this.productQuantity = productQuantity;
  }
}

