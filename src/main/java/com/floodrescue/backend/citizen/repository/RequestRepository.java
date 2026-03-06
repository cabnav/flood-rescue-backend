package com.floodrescue.backend.citizen.repository;

import com.floodrescue.backend.citizen.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Integer> {
    List<Request> findByUserId(Integer userId);

    List<Request> findByStatus(Request.RequestStatus status);

    List<Request> findByRequestType(Request.RequestType requestType);

    List<Request> findByUserIdAndStatusIn(Integer userId, List<Request.RequestStatus> statuses);

    List<Request> findByUserIdAndStatusInAndRequestType(Integer userId, List<Request.RequestStatus> statuses,
            Request.RequestType requestType);

    @Query("select distinct r from Request r left join fetch r.medias where r.id = :id")
    Optional<Request> findByIdWithMedias(@Param("id") Integer id);

    @Query("select distinct r from Request r left join fetch r.medias")
    List<Request> findAllWithMedias();

    @Query("select distinct r from Request r left join fetch r.medias where r.user.id = :userId")
    List<Request> findByUserIdWithMedias(@Param("userId") Integer userId);
}
