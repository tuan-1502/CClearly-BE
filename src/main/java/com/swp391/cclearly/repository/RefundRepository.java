package com.swp391.cclearly.repository;

import com.swp391.cclearly.entity.Refund;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefundRepository extends JpaRepository<Refund, UUID> {
  List<Refund> findByOrderOrderIdOrderByCreatedAtDesc(UUID orderId);

  List<Refund> findAllByOrderByCreatedAtDesc();

  List<Refund> findByStatusOrderByCreatedAtDesc(String status);
}
