package com.denis.casajava.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;


@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 40)
    private String name;
    @NotBlank
    @Size(min = 2, max = 40)
    private String surname;

    @NotBlank(message = "{NotBlank.user.email}")
    @Size(min=5, message = "{Size.user.email}")
    private String email;
    @NotNull
    @Min(value = 1, message = "Number of guests must be at least 1")
    @Max(value = 4, message = "Number of guests cannot exceed 4")
    private int numOfGuests;

    @NotNull
    private String checkInDate;
    @NotNull
    private String checkOutDate;
    private String referenceNumber;
    private LocalDate bookingDate;

    private double totalPaid;
    @ElementCollection
    @CollectionTable(name = "booked_days", joinColumns = @JoinColumn(name = "booking_id"))
    @Column(name = "day")
    private List<String> bookedDays;

    private boolean isPaid = false;


    public Booking() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getNumOfGuests() {
        return numOfGuests;
    }

    public void setNumOfGuests(int numOfGuests) {
        this.numOfGuests = numOfGuests;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public List<String> getBookedDays() {
        return bookedDays;
    }

    public void setBookedDays(List<String> bookedDays) {
        this.bookedDays = bookedDays;
    }

    public double getTotalPaid() {
        return this.totalPaid;
    }

    public void setTotalPaid(double totalPaid) {
        this.totalPaid = totalPaid;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }
}