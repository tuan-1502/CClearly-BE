package com.swp391.cclearly.repository;

import com.swp391.cclearly.entity.ProductVariant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {
  Optional<ProductVariant> findBySku(String sku);

  List<ProductVariant> findByProduct_ProductId(UUID productId);
}
