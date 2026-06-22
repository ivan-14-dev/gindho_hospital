-- =============================================================
-- GinDHO — Migration V2.0 : Nouvelles tables (modules 1-15)
-- =============================================================

-- Module 1 : Assurances Patient
CREATE TABLE IF NOT EXISTS assurances_patients (
    id BIGSERIAL PRIMARY KEY,
    compagnie VARCHAR(255) NOT NULL,
    numero_police VARCHAR(100) NOT NULL,
    type_couverture VARCHAR(100) NOT NULL,
    taux_prise_en_charge DECIMAL(5,2) NOT NULL DEFAULT 80.00,
    plafond_annuel DECIMAL(12,2),
    montant_consomme DECIMAL(12,2) DEFAULT 0,
    date_debut DATE NOT NULL,
    date_fin DATE,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    patient_id BIGINT NOT NULL REFERENCES patients(id),
    cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
    mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_assur_numero_police UNIQUE (numero_police)
);
CREATE INDEX IF NOT EXISTS idx_assur_patient_id ON assurances_patients(patient_id);

-- Module 2 : Signes Vitaux
CREATE TABLE IF NOT EXISTS signes_vitaux (
    id BIGSERIAL PRIMARY KEY,
    temperature DOUBLE PRECISION,
    tension_systolique INTEGER,
    tension_diastolique INTEGER,
    frequence_cardiaque INTEGER,
    frequence_respiratoire INTEGER,
    saturation_oxygen INTEGER,
    glycemie DOUBLE PRECISION,
    poids DOUBLE PRECISION,
    date_releve TIMESTAMP NOT NULL DEFAULT NOW(),
    notes VARCHAR(500),
    patient_id BIGINT NOT NULL REFERENCES patients(id),
    hospitalisation_id BIGINT REFERENCES hospitalisations(id),
    cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
    mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_signe_patient_id ON signes_vitaux(patient_id);
CREATE INDEX IF NOT EXISTS idx_signe_date_releve ON signes_vitaux(date_releve);

-- Module 2 : Plans de Soins
CREATE TABLE IF NOT EXISTS plans_soins (
    id BIGSERIAL PRIMARY KEY,
    type_soin VARCHAR(255) NOT NULL,
    description TEXT,
    date_soin TIMESTAMP NOT NULL,
    date_realisation TIMESTAMP,
    realise BOOLEAN NOT NULL DEFAULT FALSE,
    notes_infirmier TEXT,
    hospitalisation_id BIGINT NOT NULL REFERENCES hospitalisations(id),
    patient_id BIGINT NOT NULL REFERENCES patients(id),
    cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
    mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_plan_soin_hosp ON plans_soins(hospitalisation_id);
CREATE INDEX IF NOT EXISTS idx_plan_soin_date ON plans_soins(date_soin);

-- Module 2 : Administration Médicaments
CREATE TABLE IF NOT EXISTS administrations_medicaments (
    id BIGSERIAL PRIMARY KEY,
    medicament VARCHAR(255) NOT NULL,
    posologie VARCHAR(255) NOT NULL,
    voie_administration VARCHAR(100),
    date_administration TIMESTAMP NOT NULL DEFAULT NOW(),
    administre BOOLEAN NOT NULL DEFAULT FALSE,
    notes VARCHAR(1000),
    patient_id BIGINT NOT NULL REFERENCES patients(id),
    hospitalisation_id BIGINT REFERENCES hospitalisations(id),
    cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
    mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_admin_med_patient ON administrations_medicaments(patient_id);
CREATE INDEX IF NOT EXISTS idx_admin_med_date ON administrations_medicaments(date_administration);

-- Module 3 : Pharmacie Stock
CREATE TABLE IF NOT EXISTS pharmacie_stocks (
    id BIGSERIAL PRIMARY KEY,
    medicament VARCHAR(255) NOT NULL,
    lot VARCHAR(100) NOT NULL,
    quantite INTEGER NOT NULL,
    prix_unitaire DECIMAL(10,2),
    date_peremption DATE,
    date_entree DATE DEFAULT CURRENT_DATE,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
    mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Module 4 : Gardes (Plannings)
CREATE TABLE IF NOT EXISTS gardes (
    id BIGSERIAL PRIMARY KEY,
    type_garde VARCHAR(50) NOT NULL,
    date_debut TIMESTAMP NOT NULL,
    date_fin TIMESTAMP NOT NULL,
    confirmee BOOLEAN NOT NULL DEFAULT FALSE,
    medecin_id BIGINT NOT NULL REFERENCES medecins(id),
    cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
    mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Module 5 : Stocks Consommables
CREATE TABLE IF NOT EXISTS stocks_consommables (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    categorie VARCHAR(100),
    quantite INTEGER NOT NULL,
    seuil_alerte INTEGER NOT NULL DEFAULT 10,
    prix_unitaire DECIMAL(10,2),
    date_peremption DATE,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
    mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Module 6 : RH — Personnel
CREATE TABLE IF NOT EXISTS personnel (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    prenom VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    telephone VARCHAR(100) NOT NULL,
    poste VARCHAR(100) NOT NULL,
    departement VARCHAR(100) NOT NULL,
    date_embauche DATE,
    salaire_base DECIMAL(12,2),
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    user_id BIGINT REFERENCES users(id),
    cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
    mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Module 6 : RH — Présence
CREATE TABLE IF NOT EXISTS presences (
    id BIGSERIAL PRIMARY KEY,
    date DATE NOT NULL,
    heure_arrivee TIME,
    heure_depart TIME,
    present BOOLEAN NOT NULL DEFAULT FALSE,
    personnel_id BIGINT NOT NULL REFERENCES personnel(id),
    cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
    mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Module 6 : RH — Congés
CREATE TABLE IF NOT EXISTS conges (
    id BIGSERIAL PRIMARY KEY,
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    type_conge VARCHAR(50) NOT NULL,
    motif VARCHAR(500),
    valide BOOLEAN NOT NULL DEFAULT FALSE,
    personnel_id BIGINT NOT NULL REFERENCES personnel(id),
    cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
    mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Module 7 : Événements
CREATE TABLE IF NOT EXISTS evenements (
    id BIGSERIAL PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    description TEXT,
    type_evenement VARCHAR(100) NOT NULL,
    date_debut TIMESTAMP NOT NULL,
    date_fin TIMESTAMP,
    valide BOOLEAN NOT NULL DEFAULT FALSE,
    cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
    mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Module 8 : Rondes Médicales
CREATE TABLE IF NOT EXISTS rondes_medicales (
    id BIGSERIAL PRIMARY KEY,
    type_ronde VARCHAR(100) NOT NULL,
    date_debut TIMESTAMP NOT NULL,
    date_fin TIMESTAMP,
    validee BOOLEAN NOT NULL DEFAULT FALSE,
    compte_rendu TEXT,
    medecin_id BIGINT NOT NULL REFERENCES medecins(id),
    cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
    mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Module 9 : Bloc Opératoire
CREATE TABLE IF NOT EXISTS bloc_operatoire (
    id BIGSERIAL PRIMARY KEY,
    salle VARCHAR(50) NOT NULL,
    date_debut TIMESTAMP NOT NULL,
    date_fin TIMESTAMP NOT NULL,
    intervention VARCHAR(500),
    statut VARCHAR(50) NOT NULL DEFAULT 'PROGRAMME',
    patient_id BIGINT NOT NULL REFERENCES patients(id),
    chirurgien_id BIGINT NOT NULL REFERENCES medecins(id),
    anesthesiste_id BIGINT REFERENCES medecins(id),
    cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
    mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Module 10 : Qualité — Audits
CREATE TABLE IF NOT EXISTS audits_qualite (
    id BIGSERIAL PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    description TEXT,
    type_audit VARCHAR(100) NOT NULL,
    date_audit DATE,
    score INTEGER DEFAULT 0,
    recommandations TEXT,
    cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
    mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Module 11 : Incidents
CREATE TABLE IF NOT EXISTS incidents (
    id BIGSERIAL PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    description TEXT,
    type_incident VARCHAR(100) NOT NULL,
    gravite VARCHAR(50) NOT NULL,
    date_declaration TIMESTAMP NOT NULL DEFAULT NOW(),
    action_corrective TEXT,
    resolu BOOLEAN NOT NULL DEFAULT FALSE,
    cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
    mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Module 12 : Équipements
CREATE TABLE IF NOT EXISTS equipements (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    modele VARCHAR(255),
    numero_serie VARCHAR(255),
    statut VARCHAR(100) NOT NULL DEFAULT 'OPERATIONNEL',
    date_achat DATE,
    date_derniere_maintenance DATE,
    date_prochaine_maintenance DATE,
    cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
    mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Module 13 : Ambulances
CREATE TABLE IF NOT EXISTS ambulances (
    id BIGSERIAL PRIMARY KEY,
    immatriculation VARCHAR(50) NOT NULL,
    statut VARCHAR(50) NOT NULL DEFAULT 'DISPONIBLE',
    derniere_latitude DOUBLE PRECISION,
    derniere_longitude DOUBLE PRECISION,
    derniere_mise_a_jour TIMESTAMP,
    cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
    mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Module 13 : Imagerie Médicale
CREATE TABLE IF NOT EXISTS examens_imagerie (
    id BIGSERIAL PRIMARY KEY,
    type_examen VARCHAR(100) NOT NULL,
    date_examen TIMESTAMP NOT NULL DEFAULT NOW(),
    compte_rendu TEXT,
    fichier_dicom VARCHAR(500),
    patient_id BIGINT NOT NULL REFERENCES patients(id),
    medecin_id BIGINT NOT NULL REFERENCES medecins(id),
    cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
    mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Module 15 : Téléconsultation
CREATE TABLE IF NOT EXISTS teleconsultations (
    id BIGSERIAL PRIMARY KEY,
    date_debut TIMESTAMP NOT NULL,
    date_fin TIMESTAMP,
    statut VARCHAR(50) NOT NULL DEFAULT 'PLANIFIEE',
    lien_video VARCHAR(500),
    notes TEXT,
    patient_id BIGINT NOT NULL REFERENCES patients(id),
    medecin_id BIGINT NOT NULL REFERENCES medecins(id),
    cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
    mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW()
);
