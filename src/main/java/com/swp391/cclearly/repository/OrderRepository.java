package com.swp391.cclearly.repository;

import com.swp391.cclearly.entity.Order;
import com.swp391.cclearly.entity.User;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {
  List<Order> findByUserOrderByOrderIdDesc(User user);

  Page<Order> findAllByOrderByOrderIdDesc(Pageable pageable);

  Page<Order> findByStatusOrderByOrderIdDesc(String status, Pageable pageable);

  @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
  long countByStatus(String status);
}
