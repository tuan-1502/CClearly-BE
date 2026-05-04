package com.swp391.cclearly.repository;

import com.swp391.cclearly.entity.InventoryStock;
import com.swp391.cclearly.entity.InventoryStockId;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryStockRepository extends JpaRepository<InventoryStock, InventoryStockId> {
  List<InventoryStock> findByVariant_VariantId(UUID variantId);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("""
      SELECT s FROM InventoryStock s
      WHERE s.variant.variantId = :variantId
      ORDER BY s.warehouse.name
      """)
  List<InventoryStock> findByVariantIdForUpdate(UUID variantId);
}
