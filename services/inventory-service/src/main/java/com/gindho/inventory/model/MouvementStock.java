package com.gindho.inventory.model;
import com.gindho.base.BaseEntity; import jakarta.persistence.*; import lombok.*;
import java.time.LocalDateTime;
@Entity @Table(name = "mouvements_stock") @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class MouvementStock extends BaseEntity {
    private Long produitId;
    @Enumerated(EnumType.STRING) private TypeMouvement type;
    private int quantite; private String reference;
    private LocalDateTime dateMouvement;
    public enum TypeMouvement { ENTREE, SORTIE, AJUSTEMENT }
}