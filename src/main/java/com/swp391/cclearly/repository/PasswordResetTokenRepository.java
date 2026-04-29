package com.swp391.cclearly.repository;

import com.swp391.cclearly.entity.PasswordResetToken;
import com.swp391.cclearly.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

  Optional<PasswordResetToken> findByTokenAndUsedFalse(String token);

  Optional<PasswordResetToken> findTopByUserOrderByCreatedAtDesc(User user);

  void deleteByUser(User user);
}
