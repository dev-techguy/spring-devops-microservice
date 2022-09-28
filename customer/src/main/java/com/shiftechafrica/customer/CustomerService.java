package com.shiftechafrica.customer;

import com.shiftechafrica.amqp.RabbitMqMessageProducer;
import com.shiftechafrica.clients.fraud.FraudCheckResponse;
import com.shiftechafrica.clients.fraud.FraudClient;
import com.shiftechafrica.clients.notification.NotificationRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class CustomerService {

    private RestTemplate restTemplate;
    private final CustomerRepository customerRepository;
    private final FraudClient fraudClient;
    private final RabbitMqMessageProducer rabbitMqMessageProducer;

    public void registerCustomer(CustomerRegistrationRequest request) {
        Customer customer = Customer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        // todo: check if email valid
        // todo: check if email not taken
        customerRepository.saveAndFlush(customer);

        FraudCheckResponse fraudCheckResponse = fraudClient.isFraudster(customer.getId());

        if (fraudCheckResponse.isFraudster()) {
            throw new IllegalStateException("fraudster");
        }

        NotificationRequest notificationRequest = new NotificationRequest(
                customer.getId(),
                customer.getEmail(),
                String.format("Hi %s, welcome to shiftechafrica...", customer.getFirstName())
        );

        rabbitMqMessageProducer.publish(
                notificationRequest,
                "internal.exchange",
                "internal.notification.routing-key"
        );

    }
}
