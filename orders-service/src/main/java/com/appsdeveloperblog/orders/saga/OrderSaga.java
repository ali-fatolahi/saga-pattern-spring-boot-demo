package com.appsdeveloperblog.orders.saga;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.appsdeveloperblog.core.dto.events.OrderApprovedEvent;
import com.appsdeveloperblog.core.dto.events.OrderCreatedEvent;
import com.appsdeveloperblog.core.dto.events.PaymentFailedEvent;
import com.appsdeveloperblog.core.dto.events.PaymentProcessedEvent;
import com.appsdeveloperblog.core.dto.events.ProductReservedEvent;
import com.appsdeveloperblog.core.types.OrderStatus;
import com.appsdeveloperblog.orders.service.OrderHistoryService;
import com.appsdeveloperblog.core.dto.commands.ApproveOrderCommand;
import com.appsdeveloperblog.core.dto.commands.CancelProductReservationCommand;
import com.appsdeveloperblog.core.dto.commands.ProcessPaymentCommand;
import com.appsdeveloperblog.core.dto.commands.ProductReservationCancelledEvent;
import com.appsdeveloperblog.core.dto.commands.RejectOrderCommand;
import com.appsdeveloperblog.core.dto.commands.ReserveProductCommand;

@Component
@KafkaListener(topics = {
  "${orders.events.topic.name}",
  "${products.events.topic.name}",
  "${payments.events.topic.name}"
})
public class OrderSaga {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String productsCommandsTopicName;
    private final OrderHistoryService orderHistoryService;
    private final String paymentsCommandsTopicName;
    private final String orderCommandsTopicName;

    public OrderSaga(
      KafkaTemplate<String, Object> kafkaTemplate,
      @Value("${products.commands.topic.name}") String productsCommandsTopicName,
      OrderHistoryService orderHistoryService,
      @Value("${payments.commands.topic.name}") String paymentsCommandsTopicName,
      @Value("${orders.commands.topic.name}") String orderCommandsTopicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.productsCommandsTopicName = productsCommandsTopicName;
        this.orderHistoryService = orderHistoryService;
        this.paymentsCommandsTopicName = paymentsCommandsTopicName;
        this.orderCommandsTopicName = orderCommandsTopicName;
    }

    @KafkaHandler
    public void handleEvent(@Payload OrderCreatedEvent orderCreatedEvent) {
        ReserveProductCommand reserveProductCommand = new ReserveProductCommand(
                orderCreatedEvent.getProductId(),
                orderCreatedEvent.getProductQuantity(),
                orderCreatedEvent.getOrderId()
        );

        kafkaTemplate.send(productsCommandsTopicName, reserveProductCommand);
        orderHistoryService.add(orderCreatedEvent.getOrderId(), OrderStatus.CREATED);
    }

    @KafkaHandler
    public void handleEvent(@Payload ProductReservedEvent productReservedEvent) {
        ProcessPaymentCommand processPaymentCommand = new ProcessPaymentCommand(
                productReservedEvent.getOrderId(),
                productReservedEvent.getProductId(),
                productReservedEvent.getProductPrice(),
                productReservedEvent.getProductQuantity()
        );

        kafkaTemplate.send(paymentsCommandsTopicName, processPaymentCommand);
    }

    @KafkaHandler
    public void handleEvent(@Payload PaymentProcessedEvent paymentProcessed) {
        ApproveOrderCommand approveOrderCommand = new ApproveOrderCommand(paymentProcessed.getOrderId());

        kafkaTemplate.send(orderCommandsTopicName, approveOrderCommand);
    }
    @KafkaHandler
    public void handleEvent(@Payload OrderApprovedEvent orderApprovedEvent) {
        orderHistoryService.add(orderApprovedEvent.getOrderId(), OrderStatus.APPROVED);
    }

    @KafkaHandler
    public void handleEvent(@Payload PaymentFailedEvent paymentFailedEvent) {
        CancelProductReservationCommand cancelProductReservationCommand = new CancelProductReservationCommand(
                paymentFailedEvent.getProductId(),
                paymentFailedEvent.getOrderId(),
                paymentFailedEvent.getProductQuantity()
        );

        kafkaTemplate.send(productsCommandsTopicName, cancelProductReservationCommand);
    }

    @KafkaHandler
    public void handleEvent(@Payload ProductReservationCancelledEvent productReservationCancelledEvent) {
        RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(
                productReservationCancelledEvent.getOrderId()
        );

        kafkaTemplate.send(orderCommandsTopicName, rejectOrderCommand);
        orderHistoryService.add(productReservationCancelledEvent.getOrderId(), OrderStatus.REJECTED);
    }    
}
