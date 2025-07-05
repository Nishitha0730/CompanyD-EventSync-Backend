package com.eventsync.event.sync.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.eventsync.event.sync.dto.EventDto;
import com.eventsync.event.sync.dto.Response;
import com.eventsync.event.sync.entity.Category;
import com.eventsync.event.sync.entity.Event;
import com.eventsync.event.sync.exception.NotFoundException;
import com.eventsync.event.sync.mapper.EntityDtoMapper;
import com.eventsync.event.sync.repository.CategoryRepo;
import com.eventsync.event.sync.repository.EventRepo;
import com.eventsync.event.sync.service.AwsS3Service;
import com.eventsync.event.sync.service.interf.EventService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepo eventRepo;
    private final CategoryRepo categoryRepo;
    private final EntityDtoMapper entityDtoMapper;
    private final AwsS3Service awsS3Service;

    @Override
    public Response createEvent(Long categoryId, MultipartFile image, String name, String description, BigDecimal price) {
        Category category = categoryRepo.findById(categoryId).orElseThrow(()-> new NotFoundException("Category not found"));
        String eventImageUrl = awsS3Service.saveImageToS3(image);

        Event event = new Event();
        event.setCategory(category);
        event.setPrice(price);
        event.setName(name);
        event.setDescription(description);
        event.setImageUrl(eventImageUrl);

        eventRepo.save(event);
        return Response.builder()
                .status(200)
                .message("Event successfully created")
                .build();
    }

    @Override
    public Response updateEvent(Long eventId, Long categoryId, MultipartFile image, String name, String description, BigDecimal price) {
        Event event = eventRepo.findById(eventId).orElseThrow(()-> new NotFoundException("Event Not Found"));

        Category category = null;
        String eventImageUrl = null;

        if(categoryId != null ){
            category = categoryRepo.findById(categoryId).orElseThrow(()-> new NotFoundException("Category not found"));
        }
        if (image != null && !image.isEmpty()){
            eventImageUrl = awsS3Service.saveImageToS3(image);
        }

        if (category != null) event.setCategory(category);
        if (name != null) event.setName(name);
        if (price != null) event.setPrice(price);
        if (description != null) event.setDescription(description);
        if (eventImageUrl != null) event.setImageUrl(eventImageUrl);

        eventRepo.save(event);
        return Response.builder()
                .status(200)
                .message("Event updated successfully")
                .build();

    }

    @Override
    public Response deleteEvent(Long eventId) {
        Event event = eventRepo.findById(eventId).orElseThrow(()-> new NotFoundException("Event Not Found"));
        eventRepo.delete(event);

        return Response.builder()
                .status(200)
                .message("Event deleted successfully")
                .build();
    }

    @Override
    public Response getEventById(Long eventId) {
        Event event = eventRepo.findById(eventId).orElseThrow(()-> new NotFoundException("Event Not Found"));
        EventDto eventDto = entityDtoMapper.mapEventToDtoBasic(event);

        return Response.builder()
                .status(200)
                .event(eventDto)
                .build();
    }

    @Override
    public Response getAllEvents() {
        List<EventDto> eventList = eventRepo.findAll(Sort.by(Sort.Direction.DESC, "id"))
                .stream()
                .map(entityDtoMapper::mapEventToDtoBasic)
                .collect(Collectors.toList());

        return Response.builder()
                .status(200)
                .eventList(eventList)
                .build();

    }

    @Override
    public Response getEventsByCategory(Long categoryId) {
        List<Event> events = eventRepo.findByCategoryId(categoryId);
        if(events.isEmpty()){
            throw new NotFoundException("No Events found for this category");
        }
        List<EventDto> eventDtoList = events.stream()
                .map(entityDtoMapper::mapEventToDtoBasic)
                .collect(Collectors.toList());

        return Response.builder()
                .status(200)
                .eventList(eventDtoList)
                .build();

    }

    @Override
    public Response searchEvent(String searchValue) {
        List<Event> events = eventRepo.findByNameContainingOrDescriptionContaining(searchValue, searchValue);

        if (events.isEmpty()){
            throw new NotFoundException("No Events Found");
        }
        List<EventDto> eventDtoList = events.stream()
                .map(entityDtoMapper::mapEventToDtoBasic)
                .collect(Collectors.toList());


        return Response.builder()
                .status(200)
                .eventList(eventDtoList)
                .build();
    }
    
}
