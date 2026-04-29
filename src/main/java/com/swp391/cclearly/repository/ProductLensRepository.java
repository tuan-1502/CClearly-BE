package com.swp391.cclearly.repository;

import com.swp391.cclearly.entity.ProductLens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductLensRepository extends JpaRepository<ProductLens, UUID> {
}
