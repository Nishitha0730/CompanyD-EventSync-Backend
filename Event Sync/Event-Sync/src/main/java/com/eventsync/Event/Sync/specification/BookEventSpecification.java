package com.eventsync.event.sync.specification;

import org.springframework.data.jpa.domain.Specification;

import com.eventsync.event.sync.entity.BookEvent;
import com.eventsync.event.sync.entity.User;
import com.eventsync.event.sync.enums.BookingStatus;

import java.time.LocalDateTime;

public class BookEventSpecification {
    /**Specification to filter order items by status*/
    public static Specification<BookEvent> hasStatus(BookingStatus status){
        return ((root, query, criteriaBuilder) ->
                status != null ? criteriaBuilder.equal(root.get("status"), status) : null);

    }

    /**Specification to filter order items by data range*/
    public static Specification<BookEvent> createdBetween(LocalDateTime startDate, LocalDateTime endDate){
        return ((root, query, criteriaBuilder) -> {
            if (startDate != null && endDate != null){
                return criteriaBuilder.between(root.get("createdAt"), startDate, endDate);
            } else if (startDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate);
            } else if (endDate != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate);
            }else{
                return null;
            }
        });
    }

    /** Generate specification to filter order items by event id*/
    public static Specification<BookEvent> hasEventId(Long eventId){
        return ((root, query, criteriaBuilder) ->
                eventId != null ? criteriaBuilder.equal(root.get("id"), eventId) : null);
    }

    public static Specification<BookEvent> hasUser(User user) {
    return (root, query, cb) -> user != null ? cb.equal(root.get("user"), user) : null;
}
}
