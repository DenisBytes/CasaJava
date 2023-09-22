package com.denis.casajava.services;

import com.denis.casajava.models.Booking;
import com.denis.casajava.repositories.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;
    public List<String> getAllBookedDaysForAllBookings() {
        return bookingRepository.getAllDistinctBookedDaysForAllBookings();
    }

    public void saveBooking(Booking booking){
        bookingRepository.save(booking);
    }

    public List<Booking> allBookings(){
        return bookingRepository.findAll();
    };

    public Booking findById(Long id){
        return bookingRepository.findById(id).orElse(null);
    }

    public void deleteBooking(Booking booking){
        bookingRepository.delete(booking);
    }

    public Booking findBookingByReferenceNumber(String referenceNumber){
        return bookingRepository.findBookingByReferenceNumber(referenceNumber);
    }
}
