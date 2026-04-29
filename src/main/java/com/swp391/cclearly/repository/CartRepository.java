package com.swp391.cclearly.repository;

import com.swp391.cclearly.entity.Cart;
import com.swp391.cclearly.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {
  Optional<Cart> findByUser(User user);
}
