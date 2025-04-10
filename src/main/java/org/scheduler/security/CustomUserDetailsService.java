package org.scheduler.security;

import lombok.RequiredArgsConstructor;
import org.scheduler.entity.User;
import org.scheduler.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByTelegramUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().toString()))
                .toList();

        // Поскольку мы используем JWT для аутентификации, пароль здесь не важен
        // Но UserDetails требует его наличия, поэтому устанавливаем пустую строку
        return new org.springframework.security.core.userdetails.User(
                user.getTelegramUserName(),
                "", // Пустой пароль, так как аутентификация через JWT
                !user.getNoActive(), // enabled
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                authorities
        );
    }
}
