package com.gindho.inventory.controller;
import com.gindho.inventory.model.Produit; import com.gindho.inventory.model.MouvementStock;
import com.gindho.inventory.repository.ProduitRepository; import com.gindho.inventory.repository.MouvementStockRepository;
import com.gindho.base.ApiResponse; import com.gindho.kafka.BaseEvent; import com.gindho.kafka.EventProducer;
import com.gindho.kafka.EventType; import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus; import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;
@RestController @RequiredArgsConstructor
public class InventoryController {
    private final ProduitRepository produitRepository;
    private final MouvementStockRepository mouvementRepository;
    private final EventProducer eventProducer;

    @GetMapping("/api/inventory/products")
    public ResponseEntity<ApiResponse<?>> findAll() { return ResponseEntity.ok(ApiResponse.of(produitRepository.findAll())); }

    @PostMapping("/api/inventory/products")
    public ResponseEntity<ApiResponse<?>> create(@RequestBody Produit p) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(produitRepository.save(p)));
    }

    @PostMapping("/api/inventory/movements")
    public ResponseEntity<ApiResponse<?>> mouvement(@RequestBody Map<String, Object> body) {
        Long produitId = Long.valueOf(body.get("produitId").toString());
        String type = body.get("type").toString();
        int quantite = Integer.parseInt(body.get("quantite").toString());

        Produit p = produitRepository.findById(produitId).orElseThrow();
        if ("ENTREE".equals(type)) p.setQuantiteStock(p.getQuantiteStock() + quantite);
        else if ("SORTIE".equals(type)) p.setQuantiteStock(p.getQuantiteStock() - quantite);
        produitRepository.save(p);

        MouvementStock m = new MouvementStock();
        m.setProduitId(produitId); m.setType(MouvementStock.TypeMouvement.valueOf(type));
        m.setQuantite(quantite); m.setDateMouvement(LocalDateTime.now());
        mouvementRepository.save(m);

        if (p.getQuantiteStock() <= p.getSeuilAlerte()) {
            eventProducer.publish("inventory", BaseEvent.builder().eventType(EventType.INVENTORY_LOW).source("inventory-service").build());
        }
        return ResponseEntity.ok(ApiResponse.of("Mouvement enregistre"));
    }

    @GetMapping("/api/inventory/alerts")
    public ResponseEntity<ApiResponse<?>> alerts() {
        return ResponseEntity.ok(ApiResponse.of(produitRepository.findByQuantiteStockLessThanEqual(10)));
    }
}