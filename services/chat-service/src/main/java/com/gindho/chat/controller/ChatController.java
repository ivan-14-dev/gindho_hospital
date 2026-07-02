package com.gindho.chat.controller;

import com.gindho.base.ApiResponse;
import com.gindho.chat.model.Conversation;
import com.gindho.chat.model.Message;
import com.gindho.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/chat-service/conversations")
    public ResponseEntity<ApiResponse<List<Conversation>>> list(@RequestParam(required = false) Long userId) {
        List<Conversation> list = userId != null ? chatService.findByUser(userId) : chatService.findAll();
        return ResponseEntity.ok(ApiResponse.okList(list));
    }

    @GetMapping("/chat-service/conversations/{id}")
    public ResponseEntity<ApiResponse<Conversation>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Conversation récupérée", chatService.findById(id)));
    }

    @PostMapping("/chat-service/conversations")
    public ResponseEntity<ApiResponse<Conversation>> create(@RequestBody Conversation conversation) {
        return ResponseEntity.ok(ApiResponse.ok("Conversation créée", chatService.create(conversation)));
    }

    @PutMapping("/chat-service/conversations/{id}/archive")
    public ResponseEntity<ApiResponse<Conversation>> archive(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Conversation archivée", chatService.archive(id)));
    }

    @PutMapping("/chat-service/conversations/{id}/read")
    public ResponseEntity<ApiResponse<List<Message>>> markRead(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Messages marqués comme lus", chatService.markAsRead(id)));
    }

    @GetMapping("/chat-service/messages/{conversationId}")
    public ResponseEntity<ApiResponse<List<Message>>> messages(@PathVariable Long conversationId) {
        return ResponseEntity.ok(ApiResponse.okList(chatService.findMessages(conversationId)));
    }

    @PostMapping("/chat-service/messages")
    public ResponseEntity<ApiResponse<Message>> send(@RequestBody Map<String, Object> body) {
        Message message = new Message();
        message.setConversationId(Long.valueOf(body.get("conversationId").toString()));
        message.setExpediteurId(Long.valueOf(body.get("expediteurId").toString()));
        message.setRole(body.getOrDefault("role", "USER").toString());
        message.setContenu(body.get("contenu").toString());
        return ResponseEntity.ok(ApiResponse.ok("Message envoyé", chatService.sendMessage(message)));
    }
}
