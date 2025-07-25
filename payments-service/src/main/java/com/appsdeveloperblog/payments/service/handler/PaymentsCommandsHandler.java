package com.appsdeveloperblog.payments.service.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.appsdeveloperblog.core.dto.Payment;
import com.appsdeveloperblog.core.dto.commands.ProcessPaymentCommand;
import com.appsdeveloperblog.core.dto.events.PaymentFailedEvent;
import com.appsdeveloperblog.core.dto.events.PaymentProcessedEvent;
import com.appsdeveloperblog.core.exceptions.CreditCardProcessorUnavailableException;
import com.appsdeveloperblog.payments.service.PaymentService;

@Component
@KafkaListener(topics = "${payments.commands.topic.name}")
public class PaymentsCommandsHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(PaymentsCommandsHandler.class);

  private final PaymentService paymentService;
  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final String paymentEventsTopicName;

  public PaymentsCommandsHandler(
    final PaymentService paymentService,
    final KafkaTemplate<String, Object> kafkaTemplate,
    @Value("${payments.events.topic.name}") String paymentEventsTopicName) {
      this.paymentService = paymentService;
      this.kafkaTemplate = kafkaTemplate;
      this.paymentEventsTopicName = paymentEventsTopicName;
  }

  @KafkaHandler
  public void handlePaymentCommand(@Payload ProcessPaymentCommand command) {
    Payment payment = new Payment(
      command.getOrderId(),
      command.getProductId(),
      command.getProductPrice(),
      command.getProductQuantity());

    try {
      Payment processedPayment = paymentService.process(payment);
      PaymentProcessedEvent paymentProcessedEvent = new PaymentProcessedEvent(
        processedPayment.getOrderId(),
        processedPayment.getId().toString());
      
      kafkaTemplate.send(paymentEventsTopicName, paymentProcessedEvent);
    } catch (CreditCardProcessorUnavailableException e) {
      LOGGER.error("Credit card processor is unavailable. Payment processing failed for order ID: {}", command.getOrderId(), e);
      PaymentFailedEvent paymentFailedEvent = new PaymentFailedEvent(
        command.getOrderId(),
        command.getProductId(),
        command.getProductQuantity());
      kafkaTemplate.send(paymentEventsTopicName, paymentFailedEvent);
    }
  }
}
