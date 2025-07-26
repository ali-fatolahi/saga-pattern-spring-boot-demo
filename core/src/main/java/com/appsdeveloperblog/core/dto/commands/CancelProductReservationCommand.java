package com.appsdeveloperblog.core.dto.commands;

import java.util.UUID;

public class CancelProductReservationCommand {
    private UUID productId;
    private UUID orderId;
    private Integer productQuantity;

    public CancelProductReservationCommand() {
        // Default constructor for deserialization
    }

    public CancelProductReservationCommand(UUID productId, UUID orderId, Integer productQuantity) {
        this.productId = productId;
        this.orderId = orderId;
        this.productQuantity = productQuantity;
    }

    public UUID getProductId() {
        return productId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public Integer getProductQuantity() {
        return productQuantity;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public void setProductQuantity(Integer productQuantity) {
        this.productQuantity = productQuantity;
    }
}

