package com.swp391.cclearly.repository;

import com.swp391.cclearly.entity.AuditLog;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

  Page<AuditLog> findAllByOrderByCreatedAtDesc(Pageable pageable);

  Page<AuditLog> findByActionOrderByCreatedAtDesc(String action, Pageable pageable);

  Page<AuditLog> findByCreatedAtBetweenOrderByCreatedAtDesc(
      Instant from, Instant to, Pageable pageable);

  Page<AuditLog> findByActionAndCreatedAtBetweenOrderByCreatedAtDesc(
      String action, Instant from, Instant to, Pageable pageable);
}
