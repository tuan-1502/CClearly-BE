package com.swp391.cclearly.repository;

import com.swp391.cclearly.entity.ContentBanner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContentBannerRepository extends JpaRepository<ContentBanner, UUID> {
    List<ContentBanner> findByPositionOrderByDisplayOrderAsc(String position);

    List<ContentBanner> findByIsActiveTrueOrderByPositionAscDisplayOrderAsc();

    List<ContentBanner> findByPositionAndIsActiveTrueOrderByDisplayOrderAsc(String position);

    List<ContentBanner> findAllByOrderByPositionAscDisplayOrderAsc();
}
