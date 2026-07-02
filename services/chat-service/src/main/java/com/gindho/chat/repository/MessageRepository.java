package com.gindho.chat.repository;

import com.gindho.chat.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByConversationIdOrderByDateEnvoiAsc(Long conversationId);
}
