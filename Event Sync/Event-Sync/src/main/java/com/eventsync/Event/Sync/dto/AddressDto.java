package com.eventsync.event.sync.dto;


import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor


public class AddressDto {

    private Long id;

    private String street;
    private String city;
    private String state;
    private String country;
    private String zipCode;

    
    private UserDto user;

    
    private LocalDateTime createdAt;

    
    
}
