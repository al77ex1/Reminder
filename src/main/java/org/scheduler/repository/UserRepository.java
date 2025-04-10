package org.scheduler.repository;

import org.scheduler.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByTelegramUserName(String telegramUserName);
    Optional<User> findByTelegramUserId(Long telegramUserId);
    boolean existsByTelegramUserName(String telegramUserName);
    boolean existsByTelegramUserId(Long telegramUserId);
}
