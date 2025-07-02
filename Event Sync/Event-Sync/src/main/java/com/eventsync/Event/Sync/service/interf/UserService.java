package com.eventsync.event.sync.service.interf;

import com.eventsync.event.sync.dto.LoginRequest;
import com.eventsync.event.sync.dto.Response;
import com.eventsync.event.sync.dto.UserDto;
import com.eventsync.event.sync.entity.User;

public interface UserService {
    Response registerUser(UserDto registrationRequest);
    Response loginUser(LoginRequest loginRequest);
    Response getAllUsers();
    User getLoginUser();
    Response getUserInfoAndPurchaseHistory();
}
s