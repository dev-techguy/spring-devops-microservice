package com.shiftechafrica.customer;

public record CustomerRegistrationRequest(
        String firstName,
        String lastName,
        String email) {
}
