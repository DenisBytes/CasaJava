package com.denis.casajava.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class BookingDTO {

    private Long id;
    private String name;
    private String surname;
    private String email;
    private int numOfGuests;
    private String checkInDate;
    private String checkOutDate;
    private String referenceNumber;
    private List<String> bookedDays;
    private double totalPaid;
    private boolean isPaid;

}