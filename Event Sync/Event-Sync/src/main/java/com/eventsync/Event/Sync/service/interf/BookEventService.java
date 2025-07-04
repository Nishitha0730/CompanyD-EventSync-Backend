package com.eventsync.event.sync.service.interf;

import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;

//import com.eventsync.event.sync.dto.BookEventRequest;
import com.eventsync.event.sync.dto.PurchaseRequest;
import com.eventsync.event.sync.dto.Response;
import com.eventsync.event.sync.enums.BookingStatus;

public interface BookEventService {
    Response placeBook(PurchaseRequest purchaseRequest);
    Response updateBookEventStatus(Long bookEventId, String status);
    Response filterBookEvents(BookingStatus status, LocalDateTime startDate, LocalDateTime endDate, Long eventId, Pageable pageable);

}
