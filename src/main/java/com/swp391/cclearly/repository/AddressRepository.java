package com.swp391.cclearly.repository;

import com.swp391.cclearly.entity.Address;
import com.swp391.cclearly.entity.User;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {
  List<Address> findByUser(User user);
}
