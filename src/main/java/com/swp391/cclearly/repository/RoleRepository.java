package com.swp391.cclearly.repository;

import com.swp391.cclearly.entity.Role;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

  Optional<Role> findByRoleName(String roleName);
}
