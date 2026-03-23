package com.course.registration.service;

import com.course.registration.dto.RegisterRequest;
import java.util.Map;

public interface AuthService {
    Map<String, Object> login(String username, String password);
    Map<String, Object> register(RegisterRequest request);
}
