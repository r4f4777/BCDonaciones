package com.tfg.backend.repository;

import com.tfg.backend.model.JoinRequest;
import com.tfg.backend.model.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JoinRequestRepository extends JpaRepository<JoinRequest, Long> {
    List<JoinRequest> findByStatus(RequestStatus status);
}