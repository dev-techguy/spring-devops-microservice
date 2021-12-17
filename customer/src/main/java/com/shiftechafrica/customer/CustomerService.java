package com.shiftechafrica.customer;

import org.springframework.stereotype.Service;

@Service
public record CustomerService() {
    public void register(CustomerRegistrationRequest request) {
        Customer customer = Customer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email());

        // todo check if email is valid
        // todo check if email not taken
        // store customer in db
    }
}
