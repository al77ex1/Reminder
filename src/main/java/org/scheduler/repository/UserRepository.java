package org.scheduler.repository;

import org.scheduler.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByTelegramUserName(String telegramUserName);
    Optional<User> findByTelegramUserId(Long telegramUserId);
    boolean existsByTelegramUserName(String telegramUserName);
    boolean existsByTelegramUserId(Long telegramUserId);
}
