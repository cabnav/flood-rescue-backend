package com.floodrescue.backend.citizen.repository;

import com.floodrescue.backend.citizen.model.RequestMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RequestMediaRepository
        extends JpaRepository<RequestMedia, Integer> {

    List<RequestMedia> findByRequestId(Integer requestId);
}