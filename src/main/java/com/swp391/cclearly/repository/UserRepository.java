package com.swp391.cclearly.repository;

import com.swp391.cclearly.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByEmail(String email);

  @Query("SELECT u FROM User u JOIN FETCH u.role WHERE u.userId = :userId")
  Optional<User> findByIdWithRole(UUID userId);

  boolean existsByEmail(String email);

  boolean existsByPhoneNumber(String phoneNumber);

  Optional<UserDetails> findUserDetailsByEmail(String email);

  List<User> findByRole_RoleName(String roleName);
}
