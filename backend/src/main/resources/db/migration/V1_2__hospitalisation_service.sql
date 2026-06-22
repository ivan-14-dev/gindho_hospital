-- Flyway migration: socle hospitalisation (chambres/lits/hospitalisations/dossiers)
-- NOTE:
-- - tables créées avec IF NOT EXISTS pour éviter les échecs si ddl-auto a déjà généré le schéma
-- - contraintes FK / index ajoutés via CREATE INDEX IF NOT EXISTS

CREATE TABLE IF NOT EXISTS chambres (
    id BIGSERIAL PRIMARY KEY,
    cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
    mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
    numero_chambre VARCHAR(255) NOT NULL UNIQUE,
    actif BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX IF NOT EXISTS idx_chambres_actif ON chambres(actif);

CREATE TABLE IF NOT EXISTS lits (
    id BIGSERIAL PRIMARY KEY,
    cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
    mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
    numero_lit VARCHAR(255) NOT NULL,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    statut VARCHAR(50) NOT NULL DEFAULT 'DISPONIBLE',
    chambre_id BIGINT NOT NULL,
    CONSTRAINT fk_lits_chambre
        FOREIGN KEY (chambre_id) REFERENCES chambres(id)
        ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_lits_actif ON lits(actif);
CREATE INDEX IF NOT EXISTS idx_lits_numero_chambre ON lits(chambre_id);

CREATE TABLE IF NOT EXISTS hospitalisations (
    id BIGSERIAL PRIMARY KEY,
    cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
    mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
    patient_id BIGINT NOT NULL,
    lit_id BIGINT NOT NULL,
    date_admission TIMESTAMP NOT NULL,
    date_sortie TIMESTAMP NULL,
    statut VARCHAR(50) NOT NULL DEFAULT 'EN_COURS',
    motif_admission VARCHAR(1000) NULL,
    CONSTRAINT fk_hospitalisations_patient
        FOREIGN KEY (patient_id) REFERENCES patients(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_hospitalisations_lit
        FOREIGN KEY (lit_id) REFERENCES lits(id)
        ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_hosp_statut ON hospitalisations(statut);
CREATE INDEX IF NOT EXISTS idx_hosp_date_admission ON hospitalisations(date_admission);

CREATE TABLE IF NOT EXISTS dossiers_hospitalisations (
    id BIGSERIAL PRIMARY KEY,
    cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
    mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
    hospitalisation_id BIGINT NOT NULL UNIQUE,
    diagnostic VARCHAR(2000) NULL,
    traitement VARCHAR(2000) NULL,
    observations VARCHAR(2000) NULL,
    rapport_sortie VARCHAR(4000) NULL,
    date_rapport_sortie DATE NULL,
    CONSTRAINT fk_dossiers_hospitalisations_hospitalisation
        FOREIGN KEY (hospitalisation_id) REFERENCES hospitalisations(id)
        ON DELETE RESTRICT
);
