package com.swp391.cclearly.repository;

import com.swp391.cclearly.entity.StockMovement;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> {
}
