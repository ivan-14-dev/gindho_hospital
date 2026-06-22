package com.gindho.event.repository;

import com.gindho.event.model.Evenement;
import com.gindho.base.BaseRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface EvenementRepository extends BaseRepository<Evenement> {
    List<Evenement> findByTypeEvenement(String typeEvenement);
    List<Evenement> findByDateDebutBetweenOrderByDateDebutAsc(LocalDateTime debut, LocalDateTime fin);
}
