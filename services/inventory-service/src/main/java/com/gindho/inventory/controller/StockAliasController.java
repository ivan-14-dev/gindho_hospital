package com.gindho.inventory.controller;

import com.gindho.base.ApiResponse;
import com.gindho.inventory.model.Produit;
import com.gindho.inventory.model.MouvementStock;
import com.gindho.inventory.repository.ProduitRepository;
import com.gindho.inventory.repository.MouvementStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class StockAliasController {

    private final ProduitRepository produitRepository;
    private final MouvementStockRepository mouvementRepository;

    @GetMapping("/api/stocks")
    public ResponseEntity<ApiResponse<List<Produit>>> list() {
        return ResponseEntity.ok(ApiResponse.okList(produitRepository.findAll()));
    }

    @GetMapping("/api/stocks/alertes/rupture")
    public ResponseEntity<ApiResponse<List<Produit>>> alerteRupture() {
        return ResponseEntity.ok(ApiResponse.okList(produitRepository.findByQuantiteStockLessThanEqual(10)));
    }

    @GetMapping("/api/stocks/alertes/peremption")
    public ResponseEntity<ApiResponse<List<MouvementStock>>> alertePeremption() {
        return ResponseEntity.ok(ApiResponse.okList(mouvementRepository.findAll()));
    }

    @PostMapping("/api/stocks")
    public ResponseEntity<ApiResponse<Produit>> create(@RequestBody Produit p) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Stock créé", produitRepository.save(p)));
    }
}
