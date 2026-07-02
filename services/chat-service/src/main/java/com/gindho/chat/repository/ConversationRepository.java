package com.gindho.chat.repository;

import com.gindho.chat.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    List<Conversation> findByPatientIdOrMedecinId(Long patientId, Long medecinId);
}
