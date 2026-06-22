-- Flyway migration: maladies / medicaments / matching patient->maladie
-- NOTE:
-- - création tables avec IF NOT EXISTS (évite échecs si ddl-auto a partiellement généré)
-- - tables ElementCollection (symptômes & critères d’analyse) : PK composite sans id dédié

CREATE TABLE IF NOT EXISTS medicaments (
    id BIGSERIAL PRIMARY KEY,
    cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
    mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
    nom VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(2000) NULL,
    actif BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX IF NOT EXISTS idx_medicaments_actif ON medicaments(actif);

CREATE TABLE IF NOT EXISTS maladies (
    id BIGSERIAL PRIMARY KEY,
    cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
    mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
    nom VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(2000) NULL,
    actif BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX IF NOT EXISTS idx_maladies_actif ON maladies(actif);

-- Symptômes déclarés pour une maladie (liste de chaînes)
CREATE TABLE IF NOT EXISTS maladie_symptomes (
    maladie_id BIGINT NOT NULL,
    symptome VARCHAR(255) NOT NULL,
    CONSTRAINT fk_maladie_symptomes_maladie
        FOREIGN KEY (maladie_id) REFERENCES maladies(id)
        ON DELETE RESTRICT,
    PRIMARY KEY (maladie_id, symptome)
);

CREATE INDEX IF NOT EXISTS idx_maladie_symptomes_maladie_id ON maladie_symptomes(maladie_id);

-- Critères d’analyses pour une maladie (MVP: typeAnalyse + resultatRequis)
CREATE TABLE IF NOT EXISTS maladie_analyse_criteres (
    maladie_id BIGINT NOT NULL,
    type_analyse VARCHAR(255) NOT NULL,
    resultat_requis VARCHAR(2000) NOT NULL,
    CONSTRAINT fk_maladie_analyse_criteres_maladie
        FOREIGN KEY (maladie_id) REFERENCES maladies(id)
        ON DELETE RESTRICT,
    PRIMARY KEY (maladie_id, type_analyse, resultat_requis)
);

CREATE INDEX IF NOT EXISTS idx_maladie_analyses_criteres_maladie_id ON maladie_analyse_criteres(maladie_id);

-- Maladies assignées à un patient (calculées via matching MVP)
CREATE TABLE IF NOT EXISTS patient_maladies (
    id BIGSERIAL PRIMARY KEY,
    cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
    mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
    patient_id BIGINT NOT NULL,
    maladie_id BIGINT NOT NULL,
    date_diagnostic DATE NULL,
    methode VARCHAR(255) NULL,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_patient_maladies_patient
        FOREIGN KEY (patient_id) REFERENCES patients(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_patient_maladies_maladie
        FOREIGN KEY (maladie_id) REFERENCES maladies(id)
        ON DELETE RESTRICT,
    CONSTRAINT uc_patient_maladie UNIQUE (patient_id, maladie_id)
);

CREATE INDEX IF NOT EXISTS idx_patient_maladies_patient_id ON patient_maladies(patient_id);
