package com.eventsync.event.sync.service.impl;

import com.eventsync.event.sync.dto.BookEventDto;
import com.eventsync.event.sync.dto.PurchaseRequest;
import com.eventsync.event.sync.dto.Response;
import com.eventsync.event.sync.entity.BookEvent;
import com.eventsync.event.sync.entity.Purchase;
import com.eventsync.event.sync.entity.User;
import com.eventsync.event.sync.entity.Event;
import com.eventsync.event.sync.enums.BookingStatus;
import com.eventsync.event.sync.exception.NotFoundException;
import com.eventsync.event.sync.repository.BookEventRepo;
import com.eventsync.event.sync.repository.EventRepo;
import com.eventsync.event.sync.mapper.EntityDtoMapper;
import com.eventsync.event.sync.repository.PurchaseRepo;
//import com.eventsync.event.sync.service.interf.AddressService;
import com.eventsync.event.sync.service.interf.BookEventService;
import com.eventsync.event.sync.service.interf.UserService;
import com.eventsync.event.sync.specification.BookEventSpecification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookEventServiceImpl implements BookEventService {

    private final PurchaseRepo purchaseRepo;
    private final BookEventRepo bookEventRepo;
    private final EventRepo eventRepo;
    private final UserService userService;
    private final EntityDtoMapper entityDtoMapper;
@Transactional 
@Override
public Response placeBook(PurchaseRequest purchaseRequest) {
    log.info("Incoming purchase request: TotalPrice={}, Events={}",
        purchaseRequest.getTotalPrice(),
        purchaseRequest.getEvents() != null ? purchaseRequest.getEvents().size() : "null");

    User user = userService.getLoginUser();

    List<BookEvent> bookEvents = (purchaseRequest.getEvents() == null)
            ? List.of()
            : purchaseRequest.getEvents().stream().map(bookEventRequest -> {
                Event event = eventRepo.findById(bookEventRequest.getEventId())
                        .orElseThrow(() -> new NotFoundException("Event not found"));

                BookEvent bookEvent = new BookEvent();
                bookEvent.setEvent(event);
                bookEvent.setQuantity(bookEventRequest.getQuantity());
                bookEvent.setPrice(event.getPrice().multiply(BigDecimal.valueOf(bookEventRequest.getQuantity())));
                bookEvent.setStatus(BookingStatus.PENDING);
                bookEvent.setUser(user);
                return bookEvent;
            }).collect(Collectors.toList());

    BigDecimal totalPrice = purchaseRequest.getTotalPrice() != null && purchaseRequest.getTotalPrice().compareTo(BigDecimal.ZERO) > 0
            ? purchaseRequest.getTotalPrice()
            : bookEvents.stream().map(BookEvent::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);

    Purchase purchase = new Purchase();
    purchase.setTotalPrice(totalPrice);

    // Link purchase to book events
    bookEvents.forEach(bookEvent -> {
        bookEvent.setPurchase(purchase);
        bookEvent.setUser(user);  // Ensure user is linked
        bookEvent.setStatus(BookingStatus.PENDING);  // Ensure status is set
    });

    purchase.setBookEventList(bookEvents);

    // Log before saving
    log.info("Purchase about to save with {} book events", bookEvents.size());
    bookEvents.forEach(b -> log.info("BookEvent: EventId={}, Quantity={}, UserId={}, PurchaseLinked={}",
        b.getEvent().getId(), b.getQuantity(), b.getUser().getId(), b.getPurchase() != null));

    // ✅ Ensure full persistence and flush to DB immediately
    purchaseRepo.saveAndFlush(purchase);

    return Response.builder()
            .status(200)
            .message("Booking successfully created")
            .build();
}



    @Override
    public Response updateBookEventStatus(Long bookEventId, String status){
        BookEvent bookEvent = bookEventRepo.findById(bookEventId)
                .orElseThrow(() -> new NotFoundException("BookEvent not found"));

        bookEvent.setStatus(BookingStatus.valueOf(status.toUpperCase()));
        bookEventRepo.save(bookEvent);
        return Response.builder()
                .status(200)
                .message("Booking status updated successfully")
                .build();
    }

    
    @Override
    public Response filterBookEvents(BookingStatus status, LocalDateTime startDate, LocalDateTime endDate, Long eventId, Pageable pageable) {
        User user = userService.getLoginUser();  // 👈 Get current user

        Specification<BookEvent> spec = Specification.where(BookEventSpecification.hasUser(user))  // 👈 Add user filter
                .and(BookEventSpecification.hasStatus(status))
                .and(BookEventSpecification.createdBetween(startDate, endDate))
                .and(BookEventSpecification.hasEventId(eventId));


        Page<BookEvent> bookEventPage = bookEventRepo.findAll(spec, pageable);

        if(bookEventPage.isEmpty()){
            throw new NotFoundException("No BookEvents found");

        }
        List<BookEventDto> bookEventDtos = bookEventPage.getContent().stream()
                .map(entityDtoMapper::mapBookEventToDtoPlusEventAndUser)
                .collect(Collectors.toList());

        return Response.builder()
                .status(200)
                .bookEventList(bookEventDtos)
                .totalPage(bookEventPage.getTotalPages())
                .totalElements(bookEventPage.getTotalElements())
                .build();


    }
    

        
    

    
    
}
