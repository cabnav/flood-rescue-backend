package com.floodrescue.backend.citizen.repository;

import com.floodrescue.backend.citizen.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Integer> {
    List<Request> findByUserId(Integer userId);
    List<Request> findByStatus(String status);
    List<Request> findByRequestType(Request.RequestType requestType);
}
