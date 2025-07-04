package com.eventsync.event.sync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.eventsync.event.sync.entity.Address;


public interface AddressRepo extends JpaRepository<Address, Long> {
    

    
}
