package com.swp391.cclearly.repository;

import com.swp391.cclearly.entity.EmailVerification;
import com.swp391.cclearly.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, UUID> {

  Optional<EmailVerification> findByUserAndOtpCodeAndVerifiedFalse(User user, String otpCode);

  Optional<EmailVerification> findTopByUserOrderByExpiredAtDesc(User user);

  void deleteByUser(User user);
}
