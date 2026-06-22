package com.gindho.procurement.service;
import com.gindho.procurement.model.*; import com.gindho.procurement.repository.*;
import lombok.RequiredArgsConstructor; import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime; import java.util.List;
@Service @RequiredArgsConstructor @Transactional @Slf4j
public class ProcurementService {
    private final FournisseurRepository fourRepo;
    private final BonCommandeRepository bcRepo;
    private final LigneCommandeRepository lcRepo;
    private long seq = 1000L;
    @Transactional(readOnly=true) public List<Fournisseur> findAll() { return fourRepo.findAll(); }
    public Fournisseur createSupplier(Fournisseur f) { return fourRepo.save(f); }
    @Transactional(readOnly=true) public List<BonCommande> findAllOrders() { return bcRepo.findAll(); }
    public BonCommande createOrder(BonCommande bc, List<LigneCommande> lignes) {
        bc.setCode("CMD-" + java.time.Year.now().getValue() + "-" + (++seq));
        bc.setDateCommande(LocalDateTime.now()); bc.setStatut(StatutCommande.BROUILLON);
        BonCommande saved = bcRepo.save(bc);
        for (LigneCommande l : lignes) { l.setBonCommandeId(saved.getId()); }
        lcRepo.saveAll(lignes);
        return saved;
    }
    public BonCommande receiveDelivery(Long id) {
        BonCommande bc = bcRepo.findById(id).orElseThrow();
        bc.setStatut(StatutCommande.RECU);
        return bcRepo.save(bc);
    }
}