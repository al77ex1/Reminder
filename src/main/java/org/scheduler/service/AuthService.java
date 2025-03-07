package org.scheduler.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scheduler.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    
    public String generateAuthLink(String telegramUsername) {
        boolean userExists = userRepository.existsByTelegram(telegramUsername);
        
        if (userExists) {
            log.info("user exist: {}", telegramUsername);
            return "user exist";
        } else {
            log.info("user not found: {}", telegramUsername);
            return "user not found";
        }
    }
}
