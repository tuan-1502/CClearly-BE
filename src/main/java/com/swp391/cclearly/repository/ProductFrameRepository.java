package com.swp391.cclearly.repository;

import com.swp391.cclearly.entity.ProductFrame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductFrameRepository extends JpaRepository<ProductFrame, UUID> {
}
