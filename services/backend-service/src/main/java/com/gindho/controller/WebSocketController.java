package com.gindho.controller;

import com.gindho.dto.RendezVousDto;
import com.gindho.service.RendezVousService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final RendezVousService rendezVousService;

    @MessageMapping("/rdv")
    @SendTo("/topic/rdvs")
    public RendezVousDto sendRendezVous(RendezVousDto rdv) {
        return rdv;
    }

    public void notifyNewRendezVous(RendezVousDto rdv) {
        messagingTemplate.convertAndSend("/topic/rdvs", rdv);
    }

    public void notifyUpdateRendezVous(Long rdvId) {
        messagingTemplate.convertAndSend("/topic/rdvs/update", rdvId);
    }
}
