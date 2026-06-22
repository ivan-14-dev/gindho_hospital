-- Ajout champs profil patient pour le dossier médical (taille, ville, antécédents)
ALTER TABLE patients
    ADD COLUMN IF NOT EXISTS taille VARCHAR(50),
    ADD COLUMN IF NOT EXISTS ville VARCHAR(255),
    ADD COLUMN IF NOT EXISTS antecedents TEXT;

-- Branding / settings hôpital (singleton)
CREATE TABLE IF NOT EXISTS hospital_settings (
    id BIGSERIAL PRIMARY KEY,
    nom_etablissement VARCHAR(255),
    logo_base64 TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
