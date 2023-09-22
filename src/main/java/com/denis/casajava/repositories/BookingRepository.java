package com.denis.casajava.repositories;

import com.denis.casajava.models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT DISTINCT day FROM Booking b JOIN b.bookedDays day")
    List<String> getAllDistinctBookedDaysForAllBookings();

    Booking findBookingByReferenceNumber(String referenceNumber);
}