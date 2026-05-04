package com.swp391.cclearly.repository;

import com.swp391.cclearly.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

  @Query("""
      SELECT u FROM User u
      LEFT JOIN u.role r
      WHERE (:search IS NULL OR :search = '' OR
             LOWER(COALESCE(u.fullName, '')) LIKE LOWER(CONCAT('%', :search, '%')) OR
             LOWER(COALESCE(u.email, '')) LIKE LOWER(CONCAT('%', :search, '%')))
        AND (:role IS NULL OR :role = '' OR UPPER(r.roleName) = UPPER(:role))
      """)
  Page<User> searchUsers(String search, String role, Pageable pageable);
}
