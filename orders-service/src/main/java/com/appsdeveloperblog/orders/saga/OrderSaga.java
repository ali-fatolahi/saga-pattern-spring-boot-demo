package com.appsdeveloperblog.orders.saga;

import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.appsdeveloperblog.core.dto.events.OrderCreatedEvent;
import com.appsdeveloperblog.core.dto.events.ProductReservedEvent;
import com.appsdeveloperblog.core.types.OrderStatus;
import com.appsdeveloperblog.orders.service.OrderHistoryService;
import com.appsdeveloperblog.core.dto.commands.ProcessPaymentCommand;
import com.appsdeveloperblog.core.dto.commands.ReserveProductCommand;

@Component
@KafkaListener(topics = {
  "${orders.events.topic.name}",
  "${products.events.topic.name}"
})
public class OrderSaga {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String productsCommandsTopicName;
    private final OrderHistoryService orderHistoryService;
    private final String paymentsCommandsTopicName;

    public OrderSaga(
      KafkaTemplate<String, Object> kafkaTemplate,
      @Value("${products.commands.topic.name}") String productsCommandsTopicName,
      OrderHistoryService orderHistoryService,
      @Value("${payments.commands.topic.name}") String paymentsCommandsTopicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.productsCommandsTopicName = productsCommandsTopicName;
        this.orderHistoryService = orderHistoryService;
        this.paymentsCommandsTopicName = paymentsCommandsTopicName;
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
}
