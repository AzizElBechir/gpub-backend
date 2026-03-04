package com.example.gpub.service;

import com.example.gpub.entity.Chercheur;
import com.example.gpub.entity.Notification;
import com.example.gpub.repository.ChercheurRepository;
import com.example.gpub.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@Transactional
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ChercheurRepository chercheurRepository;

    // Create a notification
    public void createNotification(Long chercheurId, String type, String titre, String message, String lien) {
        Chercheur chercheur = chercheurRepository.findById(chercheurId).orElse(null);
        if (chercheur == null) return;

        Notification notification = new Notification();
        notification.setChercheur(chercheur);
        notification.setType(type);
        notification.setTitre(titre);
        notification.setMessage(message);
        notification.setLien(lien);
        notification.setLu(false);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    // Get notifications for a user
    public Page<Map<String, Object>> getNotifications(Long chercheurId, Boolean lu, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notifications;

        if (lu != null) {
            notifications = notificationRepository
                .findByChercheurIdAndLuOrderByCreatedAtDesc(chercheurId, lu, pageable);
        } else {
            notifications = notificationRepository
                .findByChercheurIdOrderByCreatedAtDesc(chercheurId, pageable);
        }

        return notifications.map(this::convertToMap);
    }

    // Get unread count
    public long getUnreadCount(Long chercheurId) {
        return notificationRepository.countByChercheurIdAndLu(chercheurId, false);
    }

    // Mark one as read
    public Map<String, Object> markAsRead(Long notificationId, Long chercheurId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getChercheur().getId().equals(chercheurId)) {
            throw new RuntimeException("You can only mark your own notifications as read");
        }

        notification.setLu(true);
        notificationRepository.save(notification);

        return Map.of("message", "Notification marked as read", "id", notificationId);
    }

    // Mark all as read
    public Map<String, Object> markAllAsRead(Long chercheurId) {
        notificationRepository.markAllAsRead(chercheurId);
        return Map.of("message", "All notifications marked as read");
    }

    // Delete a notification
    public Map<String, Object> deleteNotification(Long notificationId, Long chercheurId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getChercheur().getId().equals(chercheurId)) {
            throw new RuntimeException("You can only delete your own notifications");
        }

        notificationRepository.deleteById(notificationId);
        return Map.of("message", "Notification deleted");
    }

    private Map<String, Object> convertToMap(Notification n) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", n.getId());
        map.put("type", n.getType());
        map.put("titre", n.getTitre());
        map.put("message", n.getMessage());
        map.put("lien", n.getLien());
        map.put("lu", n.getLu());
        map.put("createdAt", n.getCreatedAt());
        return map;
    }
}