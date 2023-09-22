package com.denis.casajava.controllers;

import com.denis.casajava.components.BookingDTOConverter;
import com.denis.casajava.dto.BookingDTO;
import com.denis.casajava.models.Booking;
import com.denis.casajava.services.BookingService;
import com.denis.casajava.stripe.ChargeRequest;
import com.denis.casajava.stripe.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CheckoutController {

    @Autowired
    private StripeService paymentsService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private BookingDTOConverter bookingDTOConverter;

    @Value("${STRIPE_PUBLIC_KEY}")
    private String stripePublicKey;

    @GetMapping("/checkout/{bookingId}")
    public String checkout(@PathVariable(name = "bookingId") Long bookingId,
                           Model model) {
        Booking normalBooking = bookingService.findById(bookingId);
        BookingDTO booking = bookingDTOConverter.entityToDTO(normalBooking);

        int total = (int) booking.getTotalPaid() * 100;

        model.addAttribute("acceptTerms", false);
        model.addAttribute("bookingId", bookingId);
        model.addAttribute("booking", booking);
        model.addAttribute("amount", total * 100);
        model.addAttribute("stripePublicKey", stripePublicKey);
        model.addAttribute("currency", ChargeRequest.Currency.EUR);

        return "checkout";
    }

    @PostMapping("/charge/{bookingId}")
    public String charge(@PathVariable (name = "bookingId")Long bookingId,
                         ChargeRequest chargeRequest, HttpSession session)
            throws StripeException {

        chargeRequest.setDescription("Vacation StayInMilan");
        chargeRequest.setCurrency(ChargeRequest.Currency.EUR);
        Integer amountInteger = chargeRequest.getAmount();
        System.out.println("Amount to charge: " + amountInteger + " (data type: " + amountInteger.getClass() + ")");

        Charge charge = paymentsService.charge(chargeRequest);

        if ("succeeded".equals(charge.getStatus())){
            Booking booking = bookingService.findById(bookingId);
            booking.setPaid(true);
            List<String> bookedDays = calculateBookedDays(booking.getCheckInDate(), booking.getCheckOutDate());
            booking.setBookedDays(bookedDays);
            sendPaymentConfirmationEmail(booking);
            bookingService.saveBooking(booking);
        }
        session.setAttribute("bookingRedirected", true);

        return "redirect:/success";
    }

    private List<String> calculateBookedDays(String checkInDate, String checkOutDate) {
        List<String> bookedDays = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate startDate = LocalDate.parse(checkInDate, formatter);
        LocalDate endDate = LocalDate.parse(checkOutDate, formatter);

        LocalDate currentDate = startDate.plusDays(1);
        LocalDate lastDate = endDate;

        while (currentDate.isBefore(lastDate)) {
            bookedDays.add(currentDate.format(DateTimeFormatter.ofPattern("d/M/yyyy"))); // Format as d/M/yyyy
            currentDate = currentDate.plusDays(1);
        }

        return bookedDays;
    }

    private void sendPaymentConfirmationEmail(Booking booking) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setTo(booking.getEmail());
            helper.setSubject("Booking Confirmation");

            String messageContent = "Dear " + booking.getSurname() + ",\n\n"
                    + "Thank you for choosing StayInMilan for your vacation. We are delighted to have you as our guest and look forward to providing you with a memorable stay.\n\n"
                    + "Here is a summary of your booking details:\n\n"
                    + "Name: " + booking.getName() + "\n"
                    + "Surname: " + booking.getSurname() + "\n"
                    + "Email: " + booking.getEmail() + "\n"
                    + "Number of Guests: " + booking.getNumOfGuests() + "\n"
                    + "Check-in Date: " + booking.getCheckInDate() + "\n"
                    + "Check-out Date: " + booking.getCheckOutDate() + "\n\n"
                    + "We hope you enjoy your time in Milan and have a fantastic experience exploring the city's rich culture, history, and cuisine.\n\n"
                    + "If you have any questions or need further assistance, please do not hesitate to contact us. We are here to ensure that your stay is comfortable and enjoyable.\n\n"
                    + "Kind Regards,\n\n"
                    + "StayInMilan";

            helper.setText(messageContent);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


    @GetMapping("/success")
    public String success(Model model, HttpSession session){
        Boolean redirected = (Boolean) session.getAttribute("bookingRedirected");
        if (redirected != null && redirected) {
            session.removeAttribute("bookingRedirected");

            Long bookingId = (Long) session.getAttribute("bookingId");
            Booking normalBooking = bookingService.findById(bookingId);
            BookingDTO booking = bookingDTOConverter.entityToDTO(normalBooking);

            model.addAttribute("bookingId", bookingId);
            model.addAttribute("booking", booking);
            return "success";
        }
        else {
            return "redirect:/";
        }
    }

    @ExceptionHandler(StripeException.class)
    public String handleError(Model model,HttpSession session, StripeException ex) {
        model.addAttribute("error", ex.getMessage());
        Long bookingId = (Long) session.getAttribute("bookingId");
        model.addAttribute("bookingId", bookingId);
        return "success";
    }
}

