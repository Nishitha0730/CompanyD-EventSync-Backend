package com.eventsync.event.sync.service.interf;

import java.math.BigDecimal;
import org.springframework.web.multipart.MultipartFile;
import com.eventsync.event.sync.dto.Response;

public interface EventService {
    Response createEvent(Long categoryId, MultipartFile image, String name, String description, BigDecimal price);
    Response updateEvent(Long eventId, Long categoryId, MultipartFile image, String name, String description, BigDecimal price);
    Response deleteEvent(Long eventId);
    Response getEventById(Long eventId);
    Response getAllEvents();
    Response getEventsByCategory(Long categoryId);
    Response searchEvent(String searchValue);

    
}
