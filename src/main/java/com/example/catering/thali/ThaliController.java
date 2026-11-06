package com.example.catering.thali;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ThaliController {
    private final ThaliService service;
    public ThaliController(ThaliService service) { this.service = service; }

    @GetMapping("/thalis") public List<ThaliResponse> publicList() { return service.publicList(); }
    @GetMapping("/thalis/{id}") public ThaliResponse publicGet(@PathVariable Long id) { return service.publicGet(id); }
    @GetMapping("/admin/thalis") public List<ThaliResponse> adminList() { return service.adminList(); }
    @PostMapping("/admin/thalis") @ResponseStatus(HttpStatus.CREATED)
    public ThaliResponse create(@Valid @RequestBody ThaliRequest request) { return service.create(request); }
    @PutMapping("/admin/thalis/{id}")
    public ThaliResponse update(@PathVariable Long id, @Valid @RequestBody ThaliRequest request) { return service.update(id, request); }
    @DeleteMapping("/admin/thalis/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
}
