package com.denis.casajava.validator;

import com.denis.casajava.models.Booking;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Component
public class BookingValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Booking.class.equals(clazz);
    }

    @Override
    public void validate(Object object, Errors errors) {
        Booking booking = (Booking) object;

        LocalDate checkInDate = parseDate(booking.getCheckInDate());
        LocalDate checkOutDate = parseDate(booking.getCheckOutDate());

        if (checkInDate != null && checkOutDate != null) {
            if (!isMinimumTwoNights(checkInDate, checkOutDate)) {
                // Custom error message for minimum two nights stay
                errors.rejectValue("checkOutDate", "booking.custom.minTwoNights", "Minimum 2 nights stay required");
            }
        }
    }

    private LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isMinimumTwoNights(LocalDate checkInDate, LocalDate checkOutDate) {
        long numberOfNights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        return numberOfNights >= 2;
    }
}
