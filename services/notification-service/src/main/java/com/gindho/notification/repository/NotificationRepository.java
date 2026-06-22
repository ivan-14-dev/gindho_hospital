package com.gindho.notification.repository;

import com.gindho.notification.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByDateEnvoiDesc(Long userId);
    List<Notification> findByUserIdAndLuOrderByDateEnvoiDesc(Long userId, boolean lu);

    @Modifying
    @Query("update Notification n set n.statut = 'ENVOYE' where n.id = :id")
    void markAsSent(@Param("id") Long id);
}