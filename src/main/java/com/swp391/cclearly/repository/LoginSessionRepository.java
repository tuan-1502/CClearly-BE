package com.swp391.cclearly.repository;

import com.swp391.cclearly.entity.LoginSession;
import com.swp391.cclearly.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginSessionRepository extends JpaRepository<LoginSession, UUID> {

  Optional<LoginSession> findByRefreshTokenAndIsActiveTrue(String refreshToken);

  List<LoginSession> findByUserAndIsActiveTrue(User user);

  @Modifying
  @Query("UPDATE LoginSession ls SET ls.isActive = false WHERE ls.user = :user")
  void deactivateAllSessionsByUser(User user);

  @Modifying
  @Query("UPDATE LoginSession ls SET ls.isActive = false WHERE ls.sessionId = :sessionId")
  void deactivateSession(UUID sessionId);
}
