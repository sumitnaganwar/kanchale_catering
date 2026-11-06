package com.example.catering.cateringbooking;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CateringBookingService {
    private final CateringBookingRepository bookings;
    public CateringBookingService(CateringBookingRepository bookings) { this.bookings = bookings; }

    @Transactional
    public CateringBookingResponse submit(CateringBookingRequest request) {
        return CateringBookingResponse.from(bookings.save(new CateringBooking(request)));
    }

    @Transactional(readOnly = true)
    public List<CateringBookingResponse> findAll() {
        return bookings.findAll().stream()
            .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
            .map(CateringBookingResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public CateringBookingResponse findById(Long id) { return CateringBookingResponse.from(getBooking(id)); }

    @Transactional
    public CateringBookingResponse updateStatus(Long id, BookingStatus status) {
        CateringBooking booking = getBooking(id);
        booking.updateStatus(status);
        return CateringBookingResponse.from(bookings.save(booking));
    }

    @Transactional
    public CateringBookingResponse addQuotation(Long id, BigDecimal amount) {
        CateringBooking booking = getBooking(id);
        booking.addQuotation(amount);
        return CateringBookingResponse.from(bookings.save(booking));
    }

    private CateringBooking getBooking(Long id) {
        return bookings.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Catering booking not found"));
    }
}
