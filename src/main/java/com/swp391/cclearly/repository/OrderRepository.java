package com.swp391.cclearly.repository;

import com.swp391.cclearly.entity.Order;
import com.swp391.cclearly.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {
  @EntityGraph(attributePaths = {
      "user",
      "address",
      "coupon",
      "payments",
      "orderItems",
      "orderItems.variant",
      "orderItems.variant.product",
      "orderItems.variant.images",
      "orderItems.prescription"
  })
  List<Order> findByUserOrderByOrderIdDesc(User user);

  @EntityGraph(attributePaths = {
      "user",
      "address",
      "coupon",
      "payments",
      "orderItems",
      "orderItems.variant",
      "orderItems.variant.product",
      "orderItems.variant.images",
      "orderItems.prescription"
  })
  Page<Order> findAllByOrderByOrderIdDesc(Pageable pageable);

  @EntityGraph(attributePaths = {
      "user",
      "address",
      "coupon",
      "payments",
      "orderItems",
      "orderItems.variant",
      "orderItems.variant.product",
      "orderItems.variant.images",
      "orderItems.prescription"
  })
  Page<Order> findByStatusOrderByOrderIdDesc(String status, Pageable pageable);

  @EntityGraph(attributePaths = {
      "user",
      "address",
      "coupon",
      "payments",
      "orderItems",
      "orderItems.variant",
      "orderItems.variant.product",
      "orderItems.variant.images",
      "orderItems.prescription"
  })
  Optional<Order> findDetailedByOrderId(UUID orderId);

  @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
  long countByStatus(String status);

  List<Order> findByStatus(String status);

  long countByCoupon_PromotionId(UUID promotionId);

  long countByCodeStartingWith(String prefix);

  boolean existsByCode(String code);
}
