package com.eventsync.event.sync.service.interf;

import com.eventsync.event.sync.dto.AddressDto;
import com.eventsync.event.sync.dto.Response;

public interface AddressService {
    Response saveAndUpdateAddress(AddressDto addressDto);
}
