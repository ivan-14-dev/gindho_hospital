package com.gindho.chat.service;

import com.gindho.chat.model.Conversation;
import com.gindho.chat.model.Message;
import com.gindho.chat.repository.ConversationRepository;
import com.gindho.chat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public List<Conversation> findAll() {
        return conversationRepository.findAll();
    }

    public List<Conversation> findByUser(Long userId) {
        return conversationRepository.findByPatientIdOrMedecinId(userId, userId);
    }

    public Conversation findById(Long id) {
        return conversationRepository.findById(id).orElseThrow();
    }

    public Conversation create(Conversation conversation) {
        conversation.setDerniereActivite(LocalDateTime.now());
        return conversationRepository.save(conversation);
    }

    public Conversation archive(Long id) {
        Conversation c = findById(id);
        c.setStatut(com.gindho.chat.model.StatutConversation.ARCHIVED);
        return conversationRepository.save(c);
    }

    public List<Message> findMessages(Long conversationId) {
        return messageRepository.findByConversationIdOrderByDateEnvoiAsc(conversationId);
    }

    public Message sendMessage(Message message) {
        message.setDateEnvoi(LocalDateTime.now());
        message.setLu(false);
        return messageRepository.save(message);
    }

    public List<Message> markAsRead(Long conversationId) {
        List<Message> messages = messageRepository.findByConversationIdOrderByDateEnvoiAsc(conversationId);
        messages.forEach(m -> m.setLu(true));
        messageRepository.saveAll(messages);
        return messages;
    }
}
