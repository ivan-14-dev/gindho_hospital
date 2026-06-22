package com.gindho.asset.service;
import com.gindho.asset.model.*; import com.gindho.asset.repository.*;
import lombok.RequiredArgsConstructor; import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service @RequiredArgsConstructor @Transactional @Slf4j
public class AssetService {
    private final EquipementRepository equipRepo;
    private final MaintenanceRepository mainRepo;
    @Transactional(readOnly=true) public List<Equipement> findAll() { return equipRepo.findAll(); }
    @Transactional(readOnly=true) public Equipement findById(Long id) { return equipRepo.findById(id).orElse(null); }
    public Equipement create(Equipement e) { return equipRepo.save(e); }
    public Equipement update(Long id, Equipement e) { e.setId(id); return equipRepo.save(e); }
    @Transactional(readOnly=true) public List<Equipement> findByStatus(StatutEquipement stat) { return equipRepo.findByStatut(stat); }
    public Maintenance planifierMaintenance(Long id, Maintenance m) { m.setEquipementId(id); return mainRepo.save(m); }
    @Transactional(readOnly=true) public List<Maintenance> getMaintenances(Long id) { return mainRepo.findByEquipementIdOrderByDateDebutDesc(id); }
}