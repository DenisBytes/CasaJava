package com.denis.casajava.controllers;

import com.denis.casajava.components.BookingDTOConverter;
import com.denis.casajava.dto.BookingDTO;
import com.denis.casajava.models.Booking;
import com.denis.casajava.models.Pricing;
import com.denis.casajava.models.User;
import com.denis.casajava.services.BookingService;
import com.denis.casajava.services.PricingService;
import com.denis.casajava.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class AdminController{

    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private PricingService pricingService;

    @Autowired
    private BookingDTOConverter bookingDTOConverter;

    @GetMapping("/admin")
    public String adminGet(Principal principal, Model model){
        if (principal == null){
            return "redirect:/login";
        }
        String email = principal.getName();
        User currentUser = userService.findByEmail(email);
        Pricing pricing = pricingService.getOrCreatePricing();

        model.addAttribute("pricing", pricing);
        model.addAttribute("currentUser", currentUser);

        List<String> bookedDays = bookingService.getAllBookedDaysForAllBookings();
        ObjectMapper objectMapper = new ObjectMapper();
        String bookedDaysJson;
        try {
            bookedDaysJson = objectMapper.writeValueAsString(bookedDays);
        } catch (JsonProcessingException e) {
            // Handle the exception
            bookedDaysJson = "[]"; // Provide a default value in case of an error
        }

        model.addAttribute("bookedDays", bookedDays);
        model.addAttribute("bookedDaysJson", bookedDaysJson);

        List<Booking> normalBookings = bookingService.allBookings();
        List<BookingDTO> bookings = bookingDTOConverter.entityToDTOList(normalBookings);
        Map<String, Double> customPrices = pricingService.getCustomPrices();
        model.addAttribute("bookings", bookings);
        model.addAttribute("customPrices", customPrices);

        return "adminPage";
    }

    @PostMapping("/update-default-pricing")
    public String updateDefaultPricing(@RequestParam("defaultPrice") double defaultPrice) {
        Pricing pricing = pricingService.getOrCreatePricing();
        pricingService.setDefaultPrice(defaultPrice);

        return "redirect:/admin";
    }

    @PostMapping("/update-custom-prices")
    public String updateCustomPrices(@RequestParam("startDate") String startDate,
                                     @RequestParam("endDate") String endDate,
                                     @RequestParam("customPrice") double customPrice) {

        List<String> dateRange = calculateDateRange(startDate, endDate);

        for (String date : dateRange) {
            pricingService.addCustomPrice(date, customPrice);
        }

        return "redirect:/admin";
    }

    private List<String> calculateDateRange(String startDate, String endDate) {
        List<String> dateRange = new ArrayList<>();

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        LocalDate start = LocalDate.parse(startDate, inputFormatter);
        LocalDate end = LocalDate.parse(endDate, inputFormatter);

        while (!start.isAfter(end)) {
            dateRange.add(start.format(outputFormatter)); // Format as dd/MM/yyyy
            start = start.plusDays(1);
        }

        return dateRange;
    }

    @GetMapping("/delete-booking/{bookingId}")
    public String deleteBooking(@PathVariable(name = "bookingId")Long bookingId){

        Booking booking = bookingService.findById(bookingId);
        bookingService.deleteBooking(booking);
        bookingService.saveBooking(booking);

        return "redirect:/admin";
    }

    @GetMapping("/delete-custom-price")
    public String deleteCustomPrice(@RequestParam(name = "date") String date) {
        try {
            pricingService.deleteCustomPrice(date);
            // Redirect back to the admin page after successful deletion
            return "redirect:/admin";
        } catch (IllegalArgumentException e) {
            // Handle any exceptions or validation errors here
            return "redirect:/admin?error=" + e.getMessage();
        }
    }
    @PostMapping("/add-booked-days")
    public String addBookedDays(
            @RequestParam("dataIniziale") String startDate,
            @RequestParam("dataFinale") String endDate) {


            List<String> dateRange = calculateDateRange(startDate, endDate);
            Booking booking = new Booking();
            booking.setBookedDays(dateRange);
            booking.setNumOfGuests(2);
            booking.setEmail("falso@gmail.com");
            booking.setName("Falso");
            booking.setSurname("Faslo");
            booking.setCheckInDate("01/01/2000");
            booking.setCheckOutDate("01/01/2000");

            bookingService.saveBooking(booking);

            return "redirect:/admin";
    }

}
