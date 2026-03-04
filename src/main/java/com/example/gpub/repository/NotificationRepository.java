package com.example.gpub.repository;

import com.example.gpub.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByChercheurIdOrderByCreatedAtDesc(Long chercheurId, Pageable pageable);

    Page<Notification> findByChercheurIdAndLuOrderByCreatedAtDesc(Long chercheurId, Boolean lu, Pageable pageable);

    long countByChercheurIdAndLu(Long chercheurId, Boolean lu);

    @Modifying
    @Query("UPDATE Notification n SET n.lu = true WHERE n.chercheur.id = :chercheurId")
    void markAllAsRead(@Param("chercheurId") Long chercheurId);
}