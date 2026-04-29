package com.swp391.cclearly.repository;

import com.swp391.cclearly.entity.MasterLensTechnology;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MasterLensTechnologyRepository extends JpaRepository<MasterLensTechnology, UUID> {
    Optional<MasterLensTechnology> findByName(String name);
}
