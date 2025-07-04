package com.eventsync.event.sync.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.eventsync.event.sync.dto.PurchaseRequest;
import com.eventsync.event.sync.dto.Response;
import com.eventsync.event.sync.enums.BookingStatus;
import com.eventsync.event.sync.service.interf.BookEventService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/purchase")
@RequiredArgsConstructor
public class BookEventController {

    private final BookEventService bookEventService;

    @PostMapping("/create")
    public ResponseEntity<Response> placeOrder(@RequestBody PurchaseRequest purchaseRequest){

        return ResponseEntity.ok(bookEventService.placeBook(purchaseRequest));
    }

    @PutMapping("/update-event-status/{bookEventId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> updateBookEventStatus(@PathVariable Long bookEventId,  @RequestParam String status){
        return ResponseEntity.ok(bookEventService.updateBookEventStatus(bookEventId, status));
    }


    @GetMapping("/filter")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> filterOrderItems(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime endDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long eventId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") int size

            ){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        BookingStatus bookingStatus = status != null ? BookingStatus.valueOf(status.toUpperCase()) : null;

        return ResponseEntity.ok(bookEventService.filterBookEvents(bookingStatus, startDate, endDate, eventId, pageable));

    }

}
