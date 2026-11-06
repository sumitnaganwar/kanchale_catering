package com.example.catering.cateringbooking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CateringBookingController {
    public record StatusRequest(@NotNull BookingStatus status) {}
    public record QuotationRequest(@NotNull @DecimalMin("0.01") java.math.BigDecimal quotationAmount) {}
    private final CateringBookingService service;
    public CateringBookingController(CateringBookingService service) { this.service = service; }

    @PostMapping("/catering-bookings")
    @ResponseStatus(HttpStatus.CREATED)
    public CateringBookingResponse submit(@Valid @RequestBody CateringBookingRequest request) { return service.submit(request); }

    @GetMapping("/admin/catering-bookings")
    public java.util.List<CateringBookingResponse> all() { return service.findAll(); }

    @GetMapping("/admin/catering-bookings/{id}")
    public CateringBookingResponse byId(@PathVariable Long id) { return service.findById(id); }

    @PatchMapping("/admin/catering-bookings/{id}/status")
    public CateringBookingResponse updateStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest request) {
        return service.updateStatus(id, request.status());
    }

    @PatchMapping("/admin/catering-bookings/{id}/quotation")
    public CateringBookingResponse quotation(@PathVariable Long id, @Valid @RequestBody QuotationRequest request) {
        return service.addQuotation(id, request.quotationAmount());
    }
}
