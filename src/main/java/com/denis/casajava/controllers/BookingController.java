package com.denis.casajava.controllers;

import com.denis.casajava.models.Booking;
import com.denis.casajava.models.Pricing;
import com.denis.casajava.services.BookingService;
import com.denis.casajava.services.PricingService;
import com.denis.casajava.validator.BookingValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private PricingService pricingService;

    @Autowired
    private BookingValidator bookingValidator;

    @GetMapping("/")
    public String home(@ModelAttribute("booking") Booking booking, Model model) {
        List<String> bookedDays = bookingService.getAllBookedDaysForAllBookings();
        ObjectMapper objectMapper = new ObjectMapper();
        String bookedDaysJson;
        try {
            bookedDaysJson = objectMapper.writeValueAsString(bookedDays);
        } catch (JsonProcessingException e) {
            // Handle the exception
            bookedDaysJson = "[]"; // Provide a default value in case of an error
        }

        Pricing pricing = pricingService.getOrCreatePricing();

        model.addAttribute("pricing", pricing);
        model.addAttribute("bookedDaysJson", bookedDaysJson);

        return "homePage";
    }

    @PostMapping("/bookings")
    public String book(@Valid @ModelAttribute("booking") Booking booking,
                       @RequestParam("totalPrice") double totalPrice,
                       BindingResult result, Model model, HttpSession session) {

        bookingValidator.validate(booking, result);

        if (result.hasErrors()) {
            List<String> bookedDays = bookingService.getAllBookedDaysForAllBookings();
            model.addAttribute("bookedDays", bookedDays);
            model.addAttribute("errors", result.getAllErrors());
            model.addAttribute("hasErrors", true);
            System.out.println(result.getAllErrors());
            return "redirect:/";
        }

        String referenceNumber = generateUniqueReferenceNumber();
        booking.setReferenceNumber(referenceNumber);

        booking.setTotalPaid(totalPrice);
        booking.setBookingDate(LocalDate.now());
        bookingService.saveBooking(booking);
        session.setAttribute("bookingId", booking.getId());

        return "redirect:/checkout/"+booking.getId();
    }

    private String generateUniqueReferenceNumber() {
        String referenceNumber;
        boolean isUnique = false;

        while (!isUnique) {
            referenceNumber = RandomStringUtils.randomAlphanumeric(8).toUpperCase();

            Booking existingBooking = bookingService.findBookingByReferenceNumber(referenceNumber);

            if (existingBooking == null) {
                isUnique = true;
                return referenceNumber;
            }
        }

        return null;
    }

    @GetMapping("/terms-and-conditions")
    public String termsAndAgreements(){
        return "termsAndAgreeements";
    }
}
