package com.appsdeveloperblog.core.dto.events;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductReservedEvent {
    private UUID productId;
    private UUID orderId;
    private BigDecimal productPrice;
    private Integer productQuantity;

    public ProductReservedEvent() {
    }

    public ProductReservedEvent(UUID productId, Integer productQuantity, UUID orderId, BigDecimal productPrice) {
        this.productId = productId;
        this.productQuantity = productQuantity;
        this.orderId = orderId;
        this.productPrice = productPrice;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public Integer getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(Integer productQuantity) {
        this.productQuantity = productQuantity;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }
}
