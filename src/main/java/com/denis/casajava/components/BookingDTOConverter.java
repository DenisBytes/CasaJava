package com.denis.casajava.components;

import com.denis.casajava.dto.BookingDTO;
import com.denis.casajava.models.Booking;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BookingDTOConverter {

    public BookingDTO entityToDTO(Booking booking){
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setName(booking.getName());
        dto.setSurname(booking.getSurname());
        dto.setEmail(booking.getEmail());
        dto.setNumOfGuests(booking.getNumOfGuests());
        dto.setCheckInDate(booking.getCheckInDate());
        dto.setCheckOutDate(booking.getCheckOutDate());
        dto.setReferenceNumber(booking.getReferenceNumber());
        dto.setBookedDays(booking.getBookedDays());
        dto.setTotalPaid(booking.getTotalPaid());
        dto.setPaid(booking.isPaid());
        return dto;
    }

    public List<BookingDTO> entityToDTOList(List<Booking> bookings){
        List<BookingDTO> dtoList =new ArrayList<>();

        for (Booking booking: bookings){
            dtoList.add(entityToDTO(booking));
        }

        return dtoList;
    }
}
