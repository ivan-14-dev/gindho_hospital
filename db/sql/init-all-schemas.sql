-- ============================================================================
-- GinDHO — Script Maître d'Initialisation des Schémas PostgreSQL
-- Usage :
--   psql -U <user> -d gindho    -f hospital/db/sql/init-all-schemas.sql
--   psql -U <user> -d admin_zone -f admin-zone/db/sql/init-admin-zone.sql
--
-- Chaque section s'exécute UNIQUEMENT dans la base de données correspondante
-- grâce à la garde current_database(). Idempotent : CREATE TABLE IF NOT EXISTS.
-- Compatible PostgreSQL 16+
-- ============================================================================

-- ============================================================================
-- SECTION 1 — SCHÉMA GINDHO (backend-service + legacy backend)
-- S'exécute UNIQUEMENT dans la base "gindho"
-- ============================================================================
DO $$
BEGIN
  IF current_database() = 'gindho' THEN

    -- 1.1 Authentification & Utilisateurs
    CREATE TABLE IF NOT EXISTS users (
        id BIGSERIAL PRIMARY KEY,
        cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        nom VARCHAR(255) NOT NULL, prenom VARCHAR(255) NOT NULL,
        email VARCHAR(255) NOT NULL UNIQUE,
        mot_de_passe_hash VARCHAR(255) NOT NULL,
        role VARCHAR(50) NOT NULL DEFAULT 'UTILISATEUR_SECONDAIRE',
        actif BOOLEAN NOT NULL DEFAULT TRUE
    );
    CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
    CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);

    ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check;
    ALTER TABLE users ADD CONSTRAINT users_role_check
        CHECK (role::text = ANY (ARRAY[
            'ADMIN','SUPER_ADMIN','MEDECIN','PATIENT','UTILISATEUR_SECONDAIRE',
            'NURSE','RECEPTION','PHARMACIST','LABORATORY','HOSPITALIZATION_SERVICE',
            'ACCOUNTING','URGENCY'
        ]::text[]));

    -- 1.2 Patients & Médecins
    CREATE TABLE IF NOT EXISTS patients (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        numero_patient VARCHAR(100) UNIQUE, nom VARCHAR(100) NOT NULL,
        prenom VARCHAR(100) NOT NULL, email VARCHAR(254) UNIQUE,
        id_number VARCHAR(100) UNIQUE, date_naissance DATE, sexe VARCHAR(10),
        telephone VARCHAR(30), groupe_sanguin VARCHAR(20), taille VARCHAR(20),
        ville VARCHAR(255), antecedents TEXT, allergies VARCHAR(1000),
        adresse VARCHAR(1000), user_id BIGINT REFERENCES users(id),
        actif BOOLEAN NOT NULL DEFAULT TRUE, date_archivage TIMESTAMP
    );
    CREATE INDEX IF NOT EXISTS idx_patients_numero ON patients(numero_patient);
    CREATE INDEX IF NOT EXISTS idx_patients_user_id ON patients(user_id);

    CREATE TABLE IF NOT EXISTS medecins (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        numero_ordre VARCHAR(100) NOT NULL UNIQUE, specialisation VARCHAR(255),
        telephone_cabinet VARCHAR(100), disponible BOOLEAN NOT NULL DEFAULT TRUE,
        user_id BIGINT REFERENCES users(id)
    );
    CREATE INDEX IF NOT EXISTS idx_medecins_numero_ordre ON medecins(numero_ordre);
    CREATE INDEX IF NOT EXISTS idx_medecins_user_id ON medecins(user_id);

    -- 1.3 RBAC
    CREATE TABLE IF NOT EXISTS roles (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        code VARCHAR(100) UNIQUE NOT NULL, libelle VARCHAR(255), description VARCHAR(500)
    );
    CREATE TABLE IF NOT EXISTS permissions (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        code VARCHAR(255) UNIQUE NOT NULL, libelle VARCHAR(255) NOT NULL,
        module VARCHAR(255) NOT NULL, action VARCHAR(50) NOT NULL, description VARCHAR(500)
    );
    CREATE TABLE IF NOT EXISTS role_permissions (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        user_id BIGINT NOT NULL REFERENCES users(id),
        permission VARCHAR(255) NOT NULL, ressource VARCHAR(255), action VARCHAR(255),
        valid_from TIMESTAMP, valid_to TIMESTAMP, scope VARCHAR(255), condition_type VARCHAR(255)
    );
    CREATE INDEX IF NOT EXISTS idx_role_permissions_user_id ON role_permissions(user_id);

    CREATE TABLE IF NOT EXISTS role_templates (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        name VARCHAR(255) NOT NULL UNIQUE
    );
    CREATE TABLE IF NOT EXISTS role_template_permissions (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        template_id BIGINT NOT NULL REFERENCES role_templates(id),
        permission VARCHAR(255) NOT NULL, ressource VARCHAR(255), action VARCHAR(255),
        valid_from TIMESTAMP, valid_to TIMESTAMP, scope VARCHAR(255), condition_type VARCHAR(255)
    );
    CREATE INDEX IF NOT EXISTS idx_role_template_permissions_template_id ON role_template_permissions(template_id);

    -- 1.4 Hospitalisation
    CREATE TABLE IF NOT EXISTS chambres (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        numero_chambre VARCHAR(255) NOT NULL UNIQUE, actif BOOLEAN NOT NULL DEFAULT TRUE
    );
    CREATE INDEX IF NOT EXISTS idx_chambres_actif ON chambres(actif);

    CREATE TABLE IF NOT EXISTS lits (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        numero_lit VARCHAR(255) NOT NULL, actif BOOLEAN NOT NULL DEFAULT TRUE,
        statut VARCHAR(50) NOT NULL DEFAULT 'DISPONIBLE',
        chambre_id BIGINT NOT NULL REFERENCES chambres(id) ON DELETE RESTRICT
    );
    CREATE INDEX IF NOT EXISTS idx_lits_actif ON lits(actif);
    CREATE INDEX IF NOT EXISTS idx_lits_chambre_id ON lits(chambre_id);

    CREATE TABLE IF NOT EXISTS hospitalisations (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        patient_id BIGINT NOT NULL REFERENCES patients(id) ON DELETE RESTRICT,
        lit_id BIGINT NOT NULL REFERENCES lits(id) ON DELETE RESTRICT,
        date_admission TIMESTAMP NOT NULL, date_sortie TIMESTAMP,
        statut VARCHAR(50) NOT NULL DEFAULT 'EN_COURS', motif_admission VARCHAR(1000)
    );
    CREATE INDEX IF NOT EXISTS idx_hospitalisations_statut ON hospitalisations(statut);
    CREATE INDEX IF NOT EXISTS idx_hospitalisations_date_admission ON hospitalisations(date_admission);
    CREATE INDEX IF NOT EXISTS idx_hospitalisations_patient_id ON hospitalisations(patient_id);

    CREATE TABLE IF NOT EXISTS dossiers_hospitalisations (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        hospitalisation_id BIGINT NOT NULL UNIQUE REFERENCES hospitalisations(id) ON DELETE RESTRICT,
        diagnostic VARCHAR(2000), traitement VARCHAR(2000), observations VARCHAR(2000),
        rapport_sortie VARCHAR(4000), date_rapport_sortie DATE
    );

    -- 1.5 Dossier médical
    CREATE TABLE IF NOT EXISTS dossiers_medicaux (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        date_consultation DATE, diagnostic VARCHAR(2000), traitement VARCHAR(2000),
        observations VARCHAR(2000), rendez_vous_id BIGINT UNIQUE,
        patient_id BIGINT NOT NULL REFERENCES patients(id)
    );
    CREATE TABLE IF NOT EXISTS ordonnances (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        medicament VARCHAR(255) NOT NULL, posologie VARCHAR(255) NOT NULL,
        duree VARCHAR(100), date_emission DATE,
        dossier_medical_id BIGINT NOT NULL REFERENCES dossiers_medicaux(id)
    );
    CREATE TABLE IF NOT EXISTS analyses (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        type_analyse VARCHAR(255) NOT NULL, resultat VARCHAR(255) NOT NULL,
        observation TEXT, date_analyse TIMESTAMP,
        urgent BOOLEAN NOT NULL DEFAULT FALSE,
        patient_id BIGINT NOT NULL REFERENCES patients(id),
        medecin_id BIGINT NOT NULL REFERENCES medecins(id),
        dossier_medical_id BIGINT REFERENCES dossiers_medicaux(id)
    );
    CREATE INDEX IF NOT EXISTS idx_analyses_patient_id ON analyses(patient_id);
    CREATE INDEX IF NOT EXISTS idx_analyses_medecin_id ON analyses(medecin_id);

    -- 1.6 Rendez-vous & Disponibilités
    CREATE TABLE IF NOT EXISTS rendez_vous (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        date_heure_debut TIMESTAMP NOT NULL, date_heure_fin TIMESTAMP NOT NULL,
        statut VARCHAR(50) NOT NULL, motif VARCHAR(500), notes VARCHAR(1000),
        patient_id BIGINT NOT NULL REFERENCES patients(id),
        medecin_id BIGINT NOT NULL REFERENCES medecins(id),
        dossier_medical_id BIGINT UNIQUE REFERENCES dossiers_medicaux(id)
    );
    CREATE INDEX IF NOT EXISTS idx_rendez_vous_patient_id ON rendez_vous(patient_id);
    CREATE INDEX IF NOT EXISTS idx_rendez_vous_medecin_id ON rendez_vous(medecin_id);
    CREATE INDEX IF NOT EXISTS idx_rendez_vous_statut ON rendez_vous(statut);

    CREATE TABLE IF NOT EXISTS disponibilites (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        jour VARCHAR(50) NOT NULL, heure_debut TIMESTAMP NOT NULL,
        heure_fin TIMESTAMP NOT NULL, actif BOOLEAN NOT NULL DEFAULT TRUE,
        medecin_id BIGINT NOT NULL REFERENCES medecins(id)
    );
    CREATE INDEX IF NOT EXISTS idx_disponibilites_medecin_id ON disponibilites(medecin_id);

    -- 1.7 Maladies & Médicaments
    CREATE TABLE IF NOT EXISTS medicaments (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        nom VARCHAR(255) NOT NULL UNIQUE, description VARCHAR(2000),
        actif BOOLEAN NOT NULL DEFAULT TRUE
    );
    CREATE TABLE IF NOT EXISTS maladies (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        nom VARCHAR(255) NOT NULL UNIQUE, description VARCHAR(2000),
        actif BOOLEAN NOT NULL DEFAULT TRUE
    );
    CREATE TABLE IF NOT EXISTS maladie_symptomes (
        maladie_id BIGINT NOT NULL REFERENCES maladies(id) ON DELETE RESTRICT,
        symptome VARCHAR(255) NOT NULL,
        PRIMARY KEY (maladie_id, symptome)
    );
    CREATE TABLE IF NOT EXISTS maladie_analyse_criteres (
        maladie_id BIGINT NOT NULL REFERENCES maladies(id) ON DELETE RESTRICT,
        type_analyse VARCHAR(255) NOT NULL, resultat_requis VARCHAR(2000) NOT NULL,
        PRIMARY KEY (maladie_id, type_analyse, resultat_requis)
    );
    CREATE TABLE IF NOT EXISTS patient_maladies (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        patient_id BIGINT NOT NULL REFERENCES patients(id) ON DELETE RESTRICT,
        maladie_id BIGINT NOT NULL REFERENCES maladies(id) ON DELETE RESTRICT,
        date_diagnostic DATE, methode VARCHAR(255),
        actif BOOLEAN NOT NULL DEFAULT TRUE,
        CONSTRAINT uc_patient_maladie UNIQUE (patient_id, maladie_id)
    );
    CREATE INDEX IF NOT EXISTS idx_patient_maladies_patient_id ON patient_maladies(patient_id);

    -- 1.8 Paramètres & Sécurité
    CREATE TABLE IF NOT EXISTS hospital_settings (
        id BIGSERIAL PRIMARY KEY, nom_etablissement VARCHAR(255),
        logo_base64 TEXT, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );
    CREATE TABLE IF NOT EXISTS password_reset_otps (
        id BIGSERIAL PRIMARY KEY, email VARCHAR(255) NOT NULL,
        code_hash VARCHAR(128) NOT NULL, expires_at TIMESTAMP NOT NULL,
        consumed BOOLEAN NOT NULL DEFAULT FALSE
    );
    CREATE TABLE IF NOT EXISTS audit_logs (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        action VARCHAR(255) NOT NULL, entite VARCHAR(255) NOT NULL,
        entite_id VARCHAR(255), details VARCHAR(2000), ip_adresse VARCHAR(100)
    );

    -- 1.9 Modules métier additionnels
    CREATE TABLE IF NOT EXISTS assurances_patients (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        compagnie VARCHAR(255) NOT NULL, numero_police VARCHAR(100) NOT NULL UNIQUE,
        type_couverture VARCHAR(100) NOT NULL,
        taux_prise_en_charge DECIMAL(5,2) NOT NULL DEFAULT 80.00,
        plafond_annuel DECIMAL(12,2), montant_consomme DECIMAL(12,2) DEFAULT 0,
        date_debut DATE NOT NULL, date_fin DATE,
        actif BOOLEAN NOT NULL DEFAULT TRUE, patient_id BIGINT NOT NULL REFERENCES patients(id)
    );
    CREATE INDEX IF NOT EXISTS idx_assur_patient_id ON assurances_patients(patient_id);

    CREATE TABLE IF NOT EXISTS signes_vitaux (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        temperature DOUBLE PRECISION, tension_systolique INTEGER,
        tension_diastolique INTEGER, frequence_cardiaque INTEGER,
        frequence_respiratoire INTEGER, saturation_oxygen INTEGER,
        glycemie DOUBLE PRECISION, poids DOUBLE PRECISION,
        date_releve TIMESTAMP NOT NULL DEFAULT NOW(), notes VARCHAR(500),
        patient_id BIGINT NOT NULL REFERENCES patients(id),
        hospitalisation_id BIGINT REFERENCES hospitalisations(id)
    );
    CREATE INDEX IF NOT EXISTS idx_signe_patient_id ON signes_vitaux(patient_id);
    CREATE INDEX IF NOT EXISTS idx_signe_date_releve ON signes_vitaux(date_releve);

    CREATE TABLE IF NOT EXISTS plans_soins (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        type_soin VARCHAR(255) NOT NULL, description TEXT,
        date_soin TIMESTAMP NOT NULL, date_realisation TIMESTAMP,
        realise BOOLEAN NOT NULL DEFAULT FALSE, notes_infirmier TEXT,
        hospitalisation_id BIGINT NOT NULL REFERENCES hospitalisations(id),
        patient_id BIGINT NOT NULL REFERENCES patients(id)
    );
    CREATE INDEX IF NOT EXISTS idx_plan_soin_hosp ON plans_soins(hospitalisation_id);
    CREATE INDEX IF NOT EXISTS idx_plan_soin_date ON plans_soins(date_soin);

    CREATE TABLE IF NOT EXISTS administrations_medicaments (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        medicament VARCHAR(255) NOT NULL, posologie VARCHAR(255) NOT NULL,
        voie_administration VARCHAR(100),
        date_administration TIMESTAMP NOT NULL DEFAULT NOW(),
        administre BOOLEAN NOT NULL DEFAULT FALSE, notes VARCHAR(1000),
        patient_id BIGINT NOT NULL REFERENCES patients(id),
        hospitalisation_id BIGINT REFERENCES hospitalisations(id)
    );
    CREATE INDEX IF NOT EXISTS idx_admin_med_patient ON administrations_medicaments(patient_id);
    CREATE INDEX IF NOT EXISTS idx_admin_med_date ON administrations_medicaments(date_administration);

    CREATE TABLE IF NOT EXISTS pharmacie_stocks (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        medicament VARCHAR(255) NOT NULL, lot VARCHAR(100) NOT NULL,
        quantite INTEGER NOT NULL, prix_unitaire DECIMAL(10,2),
        date_peremption DATE, date_entree DATE DEFAULT CURRENT_DATE,
        actif BOOLEAN NOT NULL DEFAULT TRUE
    );
    CREATE TABLE IF NOT EXISTS gardes (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        type_garde VARCHAR(50) NOT NULL, date_debut TIMESTAMP NOT NULL,
        date_fin TIMESTAMP NOT NULL, confirmee BOOLEAN NOT NULL DEFAULT FALSE,
        medecin_id BIGINT NOT NULL REFERENCES medecins(id)
    );
    CREATE TABLE IF NOT EXISTS stocks_consommables (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        nom VARCHAR(255) NOT NULL, categorie VARCHAR(100),
        quantite INTEGER NOT NULL, seuil_alerte INTEGER NOT NULL DEFAULT 10,
        prix_unitaire DECIMAL(10,2), date_peremption DATE,
        actif BOOLEAN NOT NULL DEFAULT TRUE
    );
    CREATE TABLE IF NOT EXISTS personnel (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        nom VARCHAR(255) NOT NULL, prenom VARCHAR(255) NOT NULL,
        email VARCHAR(255) NOT NULL UNIQUE, telephone VARCHAR(100) NOT NULL,
        poste VARCHAR(100) NOT NULL, departement VARCHAR(100) NOT NULL,
        date_embauche DATE, salaire_base DECIMAL(12,2),
        actif BOOLEAN NOT NULL DEFAULT TRUE, user_id BIGINT REFERENCES users(id)
    );
    CREATE TABLE IF NOT EXISTS presences (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        date DATE NOT NULL, heure_arrivee TIME, heure_depart TIME,
        present BOOLEAN NOT NULL DEFAULT FALSE,
        personnel_id BIGINT NOT NULL REFERENCES personnel(id)
    );
    CREATE TABLE IF NOT EXISTS conges (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        date_debut DATE NOT NULL, date_fin DATE NOT NULL,
        type_conge VARCHAR(50) NOT NULL, motif VARCHAR(500),
        valide BOOLEAN NOT NULL DEFAULT FALSE,
        personnel_id BIGINT NOT NULL REFERENCES personnel(id)
    );
    CREATE TABLE IF NOT EXISTS evenements (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        titre VARCHAR(255) NOT NULL, description TEXT,
        type_evenement VARCHAR(100) NOT NULL, date_debut TIMESTAMP NOT NULL,
        date_fin TIMESTAMP, valide BOOLEAN NOT NULL DEFAULT FALSE
    );
    CREATE TABLE IF NOT EXISTS rondes_medicales (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        type_ronde VARCHAR(100) NOT NULL, date_debut TIMESTAMP NOT NULL,
        date_fin TIMESTAMP, validee BOOLEAN NOT NULL DEFAULT FALSE,
        compte_rendu TEXT, medecin_id BIGINT NOT NULL REFERENCES medecins(id)
    );
    CREATE TABLE IF NOT EXISTS bloc_operatoire (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        salle VARCHAR(50) NOT NULL, date_debut TIMESTAMP NOT NULL,
        date_fin TIMESTAMP NOT NULL, intervention VARCHAR(500),
        statut VARCHAR(50) NOT NULL DEFAULT 'PROGRAMME',
        patient_id BIGINT NOT NULL REFERENCES patients(id),
        chirurgien_id BIGINT NOT NULL REFERENCES medecins(id),
        anesthesiste_id BIGINT REFERENCES medecins(id)
    );
    CREATE TABLE IF NOT EXISTS audits_qualite (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        titre VARCHAR(255) NOT NULL, description TEXT,
        type_audit VARCHAR(100) NOT NULL, date_audit DATE,
        score INTEGER DEFAULT 0, recommandations TEXT
    );
    CREATE TABLE IF NOT EXISTS incidents (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        titre VARCHAR(255) NOT NULL, description TEXT,
        type_incident VARCHAR(100) NOT NULL, gravite VARCHAR(50) NOT NULL,
        date_declaration TIMESTAMP NOT NULL DEFAULT NOW(),
        action_corrective TEXT, resolu BOOLEAN NOT NULL DEFAULT FALSE
    );
    CREATE TABLE IF NOT EXISTS equipements (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        nom VARCHAR(255) NOT NULL, modele VARCHAR(255),
        numero_serie VARCHAR(255), statut VARCHAR(100) NOT NULL DEFAULT 'OPERATIONNEL',
        date_achat DATE, date_derniere_maintenance DATE, date_prochaine_maintenance DATE
    );
    CREATE TABLE IF NOT EXISTS ambulances (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        immatriculation VARCHAR(50) NOT NULL UNIQUE,
        statut VARCHAR(50) NOT NULL DEFAULT 'DISPONIBLE',
        derniere_latitude DOUBLE PRECISION, derniere_longitude DOUBLE PRECISION,
        derniere_mise_a_jour TIMESTAMP
    );
    CREATE TABLE IF NOT EXISTS examens_imagerie (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        type_examen VARCHAR(100) NOT NULL,
        date_examen TIMESTAMP NOT NULL DEFAULT NOW(),
        compte_rendu TEXT, fichier_dicom VARCHAR(500),
        patient_id BIGINT NOT NULL REFERENCES patients(id),
        medecin_id BIGINT NOT NULL REFERENCES medecins(id)
    );
    CREATE TABLE IF NOT EXISTS teleconsultations (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        date_debut TIMESTAMP NOT NULL, date_fin TIMESTAMP,
        statut VARCHAR(50) NOT NULL DEFAULT 'PLANIFIEE',
        lien_video VARCHAR(500), notes TEXT,
        patient_id BIGINT NOT NULL REFERENCES patients(id),
        medecin_id BIGINT NOT NULL REFERENCES medecins(id)
    );
    CREATE TABLE IF NOT EXISTS factures (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        numero_facture VARCHAR(255) UNIQUE, patient_id BIGINT,
        montant DECIMAL(12,2), montant_paye DECIMAL(12,2), remise DECIMAL(12,2),
        statut VARCHAR(50) NOT NULL DEFAULT 'EMISE', date_emission TIMESTAMP,
        date_echeance DATE, description TEXT, notes TEXT
    );
    CREATE TABLE IF NOT EXISTS paiements (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        facture_id BIGINT, montant DECIMAL(12,2), date_paiement TIMESTAMP,
        mode_paiement VARCHAR(100), reference VARCHAR(255), notes TEXT
    );
    CREATE TABLE IF NOT EXISTS revenus (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        date DATE, montant_total DECIMAL(12,2),
        consultations INTEGER DEFAULT 0, hospitalisations INTEGER DEFAULT 0,
        analyses INTEGER DEFAULT 0, pharmacie INTEGER DEFAULT 0, autres INTEGER DEFAULT 0
    );

  END IF;
END $$;

-- ============================================================================
-- SECTION 2 — SCHÉMAS MICROSERVICES
-- Chaque sous-section s'exécute UNIQUEMENT dans sa base respective
-- ============================================================================

-- 2.1 patient_service
-- ---------------------------------------------------------------------------
DO $$
BEGIN
  IF current_database() = 'patient_service' THEN

    CREATE TABLE IF NOT EXISTS patients (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        numero_patient VARCHAR(100) UNIQUE, nom VARCHAR(100) NOT NULL,
        prenom VARCHAR(100) NOT NULL, email VARCHAR(254) UNIQUE,
        id_number VARCHAR(100) UNIQUE, date_naissance DATE, sexe VARCHAR(10),
        telephone VARCHAR(30), groupe_sanguin VARCHAR(20), taille VARCHAR(20),
        ville VARCHAR(255), antecedents TEXT, allergies VARCHAR(1000),
        adresse VARCHAR(1000), user_id BIGINT,
        actif BOOLEAN NOT NULL DEFAULT TRUE, date_archivage TIMESTAMP
    );
    CREATE TABLE IF NOT EXISTS medecins (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        numero_ordre VARCHAR(100) NOT NULL UNIQUE, specialisation VARCHAR(255),
        telephone_cabinet VARCHAR(100), disponible BOOLEAN NOT NULL DEFAULT TRUE,
        user_id BIGINT
    );
    CREATE TABLE IF NOT EXISTS contacts (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        patient_id BIGINT NOT NULL, nom VARCHAR(255), telephone VARCHAR(100),
        relation VARCHAR(100), email VARCHAR(254)
    );
    CREATE TABLE IF NOT EXISTS identifiants_patient (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        patient_id BIGINT, type_identifiant VARCHAR(100), valeur VARCHAR(255)
    );
    CREATE TABLE IF NOT EXISTS documents_patient (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        patient_id BIGINT, type VARCHAR(100), nom VARCHAR(255),
        url VARCHAR(2000), uploaded_at TIMESTAMP
    );
    CREATE TABLE IF NOT EXISTS assurances_patients (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        compagnie VARCHAR(255) NOT NULL, numero_police VARCHAR(100) NOT NULL UNIQUE,
        type_couverture VARCHAR(100) NOT NULL,
        taux_prise_en_charge DECIMAL(5,2) NOT NULL DEFAULT 80.00,
        plafond_annuel DECIMAL(12,2), montant_consomme DECIMAL(12,2) DEFAULT 0,
        date_debut DATE NOT NULL, date_fin DATE,
        actif BOOLEAN NOT NULL DEFAULT TRUE, patient_id BIGINT NOT NULL
    );

  END IF;
END $$;

-- 2.2 appointment_service
-- ---------------------------------------------------------------------------
DO $$
BEGIN
  IF current_database() = 'appointment_service' THEN

    CREATE TABLE IF NOT EXISTS rendez_vous (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        date_heure_debut TIMESTAMP NOT NULL, date_heure_fin TIMESTAMP NOT NULL,
        statut VARCHAR(50) NOT NULL, motif VARCHAR(500), notes VARCHAR(1000),
        patient_id BIGINT NOT NULL, medecin_id BIGINT NOT NULL
    );
    CREATE TABLE IF NOT EXISTS disponibilites (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        jour VARCHAR(50) NOT NULL, heure_debut TIMESTAMP NOT NULL,
        heure_fin TIMESTAMP NOT NULL, actif BOOLEAN NOT NULL DEFAULT TRUE,
        medecin_id BIGINT NOT NULL
    );

  END IF;
END $$;

-- 2.3 medical_record_service
-- ---------------------------------------------------------------------------
DO $$
BEGIN
  IF current_database() = 'medical_record_service' THEN

    CREATE TABLE IF NOT EXISTS dossiers_medicaux (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        date_consultation DATE, diagnostic VARCHAR(2000), traitement VARCHAR(2000),
        observations VARCHAR(2000), rendez_vous_id BIGINT UNIQUE,
        patient_id BIGINT NOT NULL
    );
    CREATE TABLE IF NOT EXISTS ordonnances (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        medicament VARCHAR(255) NOT NULL, posologie VARCHAR(255) NOT NULL,
        duree VARCHAR(100), date_emission DATE,
        dossier_medical_id BIGINT NOT NULL
    );
    CREATE TABLE IF NOT EXISTS analyses (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        type_analyse VARCHAR(255) NOT NULL, resultat VARCHAR(255) NOT NULL,
        observation TEXT, date_analyse TIMESTAMP,
        urgent BOOLEAN NOT NULL DEFAULT FALSE,
        patient_id BIGINT NOT NULL, medecin_id BIGINT NOT NULL,
        dossier_medical_id BIGINT
    );

  END IF;
END $$;

-- 2.4 admission_service
-- ---------------------------------------------------------------------------
DO $$
BEGIN
  IF current_database() = 'admission_service' THEN

    CREATE TABLE IF NOT EXISTS hospitalisations (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        patient_id BIGINT NOT NULL, medecin_id BIGINT, lit_id BIGINT,
        date_admission TIMESTAMP NOT NULL, date_sortie TIMESTAMP,
        statut VARCHAR(50) NOT NULL DEFAULT 'ADMIS', motif TEXT, service VARCHAR(100), notes TEXT
    );
    CREATE INDEX IF NOT EXISTS idx_admission_statut ON hospitalisations(statut);
    CREATE INDEX IF NOT EXISTS idx_admission_patient_id ON hospitalisations(patient_id);

  END IF;
END $$;

-- 2.5 emergency_service
-- ---------------------------------------------------------------------------
DO $$
BEGIN
  IF current_database() = 'emergency_service' THEN

    CREATE TABLE IF NOT EXISTS emergency_triage (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        patient_id BIGINT, medecin_id BIGINT, niveau_triage VARCHAR(50),
        description TEXT, prise_en_charge TEXT, date_arrivee TIMESTAMP,
        statut VARCHAR(50) NOT NULL DEFAULT 'EN_ATTENTE'
    );
    CREATE INDEX IF NOT EXISTS idx_emergency_statut ON emergency_triage(statut);
    CREATE INDEX IF NOT EXISTS idx_emergency_patient_id ON emergency_triage(patient_id);

  END IF;
END $$;

-- 2.6 ward_service
-- ---------------------------------------------------------------------------
DO $$
BEGIN
  IF current_database() = 'ward_service' THEN

    CREATE TABLE IF NOT EXISTS wards (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        code VARCHAR(50) UNIQUE, nom VARCHAR(255) NOT NULL,
        specialite VARCHAR(255), capacite_lits INTEGER DEFAULT 0,
        chef_service VARCHAR(255), telephone VARCHAR(100)
    );

  END IF;
END $$;

-- 2.7 bed_service
-- ---------------------------------------------------------------------------
DO $$
BEGIN
  IF current_database() = 'bed_service' THEN

    CREATE TABLE IF NOT EXISTS chambres (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        code VARCHAR(50) UNIQUE, nom VARCHAR(255), etage VARCHAR(50),
        aile VARCHAR(50), capacite INTEGER DEFAULT 1, type_chambre VARCHAR(50)
    );
    CREATE TABLE IF NOT EXISTS lits (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        chambre_id BIGINT NOT NULL REFERENCES chambres(id),
        code VARCHAR(50), occupe BOOLEAN NOT NULL DEFAULT FALSE,
        patient_id BIGINT, observation TEXT
    );
    CREATE INDEX IF NOT EXISTS idx_lits_chambre_id ON lits(chambre_id);
    CREATE INDEX IF NOT EXISTS idx_lits_occupe ON lits(occupe);

  END IF;
END $$;

-- 2.8 round_service
-- ---------------------------------------------------------------------------
DO $$
BEGIN
  IF current_database() = 'round_service' THEN

    CREATE TABLE IF NOT EXISTS rondes (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        patient_id BIGINT, medecin_responsable_id BIGINT, date_ronde TIMESTAMP,
        service VARCHAR(100), statut VARCHAR(50), observations TEXT
    );
    CREATE TABLE IF NOT EXISTS checklists_ronde (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        ronde_id BIGINT, element VARCHAR(255) NOT NULL,
        effectue BOOLEAN NOT NULL DEFAULT FALSE, valide_par BIGINT, observations TEXT
    );
    CREATE TABLE IF NOT EXISTS comptes_rendus_ronde (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        ronde_id BIGINT, contenu TEXT, valide_par BIGINT, statut VARCHAR(50)
    );
    CREATE TABLE IF NOT EXISTS participants_ronde (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        ronde_id BIGINT, employe_id BIGINT, role VARCHAR(100),
        present BOOLEAN NOT NULL DEFAULT FALSE
    );

  END IF;
END $$;

-- 2.9 surgery_service
-- ---------------------------------------------------------------------------
DO $$
BEGIN
  IF current_database() = 'surgery_service' THEN

    CREATE TABLE IF NOT EXISTS interventions (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        patient_id BIGINT, medecin_id BIGINT, salle VARCHAR(50) NOT NULL,
        date_programmee TIMESTAMP, date_debut TIMESTAMP, date_fin TIMESTAMP,
        description TEXT, compte_rendu TEXT,
        statut VARCHAR(50) NOT NULL DEFAULT 'PROGRAMMEE', equipe VARCHAR(500)
    );
    CREATE INDEX IF NOT EXISTS idx_interventions_statut ON interventions(statut);
    CREATE INDEX IF NOT EXISTS idx_interventions_patient_id ON interventions(patient_id);

  END IF;
END $$;

-- 2.10 prescription_service
-- ---------------------------------------------------------------------------
DO $$
BEGIN
  IF current_database() = 'prescription_service' THEN

    CREATE TABLE IF NOT EXISTS ordonnances (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        patient_id BIGINT, medecin_id BIGINT, consultation_id BIGINT,
        date_prescription TIMESTAMP, diagnostic VARCHAR(500), instructions TEXT,
        statut VARCHAR(50)
    );
    CREATE TABLE IF NOT EXISTS medicaments_prescrits (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        ordonnance_id BIGINT NOT NULL, medicament VARCHAR(255) NOT NULL,
        posologie VARCHAR(255) NOT NULL, duree VARCHAR(100),
        quantite INTEGER DEFAULT 1, instructions TEXT
    );
    CREATE INDEX IF NOT EXISTS idx_medicaments_prescrits_ordonnance_id ON medicaments_prescrits(ordonnance_id);

  END IF;
END $$;

-- 2.11 pharmacy_service
-- ---------------------------------------------------------------------------
DO $$
BEGIN
  IF current_database() = 'pharmacy_service' THEN

    CREATE TABLE IF NOT EXISTS pharmacie_stocks (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        medicament VARCHAR(255) NOT NULL, lot VARCHAR(100) NOT NULL,
        quantite INTEGER NOT NULL, prix_unitaire DECIMAL(10,2),
        date_peremption DATE, date_entree DATE DEFAULT CURRENT_DATE,
        actif BOOLEAN NOT NULL DEFAULT TRUE
    );
    CREATE INDEX IF NOT EXISTS idx_pharmacie_stocks_medicament ON pharmacie_stocks(medicament);
    CREATE INDEX IF NOT EXISTS idx_pharmacie_stocks_actif ON pharmacie_stocks(actif);

  END IF;
END $$;

-- 2.12 laboratory_service
-- ---------------------------------------------------------------------------
DO $$
BEGIN
  IF current_database() = 'laboratory_service' THEN

    CREATE TABLE IF NOT EXISTS analyses (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        patient_id BIGINT, medecin_id BIGINT, type_analyse VARCHAR(255) NOT NULL,
        code VARCHAR(100), description TEXT,
        date_prescription TIMESTAMP, date_resultat TIMESTAMP, resultat TEXT,
        statut VARCHAR(50) NOT NULL DEFAULT 'EN_ATTENTE', urgent BOOLEAN NOT NULL DEFAULT FALSE
    );
    CREATE INDEX IF NOT EXISTS idx_lab_analyses_patient_id ON analyses(patient_id);
    CREATE INDEX IF NOT EXISTS idx_lab_analyses_statut ON analyses(statut);
    CREATE INDEX IF NOT EXISTS idx_lab_analyses_date_prescription ON analyses(date_prescription);

  END IF;
END $$;

-- 2.13 billing_service
-- ---------------------------------------------------------------------------
DO $$
BEGIN
  IF current_database() = 'billing_service' THEN

    CREATE TABLE IF NOT EXISTS factures (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        numero_facture VARCHAR(255) UNIQUE, patient_id BIGINT,
        montant DECIMAL(12,2), montant_paye DECIMAL(12,2), remise DECIMAL(12,2),
        statut VARCHAR(50) NOT NULL DEFAULT 'EMISE', date_emission TIMESTAMP,
        date_echeance DATE, description TEXT, notes TEXT
    );
    CREATE TABLE IF NOT EXISTS paiements (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        facture_id BIGINT, montant DECIMAL(12,2), date_paiement TIMESTAMP,
        mode_paiement VARCHAR(100), reference VARCHAR(255), notes TEXT
    );
    CREATE TABLE IF NOT EXISTS revenus (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        date DATE, montant_total DECIMAL(12,2),
        consultations INTEGER DEFAULT 0, hospitalisations INTEGER DEFAULT 0,
        analyses INTEGER DEFAULT 0, pharmacie INTEGER DEFAULT 0, autres INTEGER DEFAULT 0
    );
    CREATE INDEX IF NOT EXISTS idx_billing_factures_patient_id ON factures(patient_id);
    CREATE INDEX IF NOT EXISTS idx_billing_factures_statut ON factures(statut);
    CREATE INDEX IF NOT EXISTS idx_billing_paiements_facture_id ON paiements(facture_id);

  END IF;
END $$;

-- 2.14 insurance_service
-- ---------------------------------------------------------------------------
DO $$
BEGIN
  IF current_database() = 'insurance_service' THEN

    CREATE TABLE IF NOT EXISTS assurances_patients (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        compagnie VARCHAR(255) NOT NULL, numero_police VARCHAR(100) NOT NULL UNIQUE,
        type_couverture VARCHAR(100) NOT NULL,
        taux_prise_en_charge DECIMAL(5,2) NOT NULL DEFAULT 80.00,
        plafond_annuel DECIMAL(12,2), montant_consomme DECIMAL(12,2) DEFAULT 0,
        date_debut DATE NOT NULL, date_fin DATE,
        actif BOOLEAN NOT NULL DEFAULT TRUE, patient_id BIGINT NOT NULL
    );
    CREATE INDEX IF NOT EXISTS idx_insurance_patient_id ON assurances_patients(patient_id);

  END IF;
END $$;

-- 2.15 payment_service
-- ---------------------------------------------------------------------------
DO $$
BEGIN
  IF current_database() = 'payment_service' THEN

    CREATE TABLE IF NOT EXISTS payment_transactions (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        patient_id BIGINT, facture_id BIGINT, montant DECIMAL(12,2) NOT NULL,
        date_paiement TIMESTAMP, mode_paiement VARCHAR(100),
        reference VARCHAR(255), statut VARCHAR(50),
        code_transaction VARCHAR(255) UNIQUE, notes TEXT
    );
    CREATE TABLE IF NOT EXISTS remboursements (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        transaction_id BIGINT, montant DECIMAL(12,2) NOT NULL, motif VARCHAR(500),
        date_remboursement TIMESTAMP, valide_par BIGINT, statut VARCHAR(50)
    );
    CREATE INDEX IF NOT EXISTS idx_payment_transactions_patient_id ON payment_transactions(patient_id);
    CREATE INDEX IF NOT EXISTS idx_remboursements_transaction_id ON remboursements(transaction_id);

  END IF;
END $$;

-- 2.16 inventory_service
-- ---------------------------------------------------------------------------
DO $$
BEGIN
  IF current_database() = 'inventory_service' THEN

    CREATE TABLE IF NOT EXISTS produits (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        code VARCHAR(100) UNIQUE, nom VARCHAR(255) NOT NULL,
        categorie VARCHAR(100), quantite_stock INTEGER NOT NULL DEFAULT 0,
        seuil_alerte INTEGER NOT NULL DEFAULT 10, prix_unitaire DECIMAL(10,2),
        unite VARCHAR(50), actif BOOLEAN NOT NULL DEFAULT TRUE
    );
    CREATE TABLE IF NOT EXISTS mouvements_stock (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        produit_id BIGINT, type_mouvement VARCHAR(50),
        quantite INTEGER NOT NULL, reference VARCHAR(255), date_mouvement TIMESTAMP
    );
    CREATE INDEX IF NOT EXISTS idx_inventory_produit_id ON mouvements_stock(produit_id);

  END IF;
END $$;

-- 2.17 procurement_service
-- ---------------------------------------------------------------------------
DO $$
BEGIN
  IF current_database() = 'procurement_service' THEN

    CREATE TABLE IF NOT EXISTS fournisseurs (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        code VARCHAR(100) UNIQUE, nom VARCHAR(255) NOT NULL,
        contact VARCHAR(255), email VARCHAR(254), telephone VARCHAR(100),
        adresse TEXT, categorie VARCHAR(100), actif BOOLEAN NOT NULL DEFAULT TRUE
    );
    CREATE TABLE IF NOT EXISTS bons_commande (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        code VARCHAR(100) UNIQUE, fournisseur_id BIGINT,
        date_commande TIMESTAMP, date_livraison_prevue DATE,
        statut VARCHAR(50), montant_total DECIMAL(12,2),
        valide_par BIGINT, notes TEXT
    );
    CREATE TABLE IF NOT EXISTS lignes_commande (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        bon_commande_id BIGINT NOT NULL, produit_id BIGINT,
        libelle VARCHAR(255), quantite_commandee INTEGER NOT NULL,
        quantite_recue INTEGER, prix_unitaire DECIMAL(10,2), tva DECIMAL(5,2)
    );
    CREATE INDEX IF NOT EXISTS idx_procurement_bon_id ON lignes_commande(bon_commande_id);

  END IF;
END $$;

-- 2.18 asset_service
-- ---------------------------------------------------------------------------
DO $$
BEGIN
  IF current_database() = 'asset_service' THEN

    CREATE TABLE IF NOT EXISTS equipements (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        code VARCHAR(100) UNIQUE, nom VARCHAR(255) NOT NULL,
        type VARCHAR(100), marque VARCHAR(100), modele VARCHAR(255),
        numero_serie VARCHAR(255), date_acquisition DATE,
        valeur_achat DECIMAL(15,2), duree_vie INTEGER,
        statut VARCHAR(50) NOT NULL DEFAULT 'ACTIF',
        localisation VARCHAR(255), responsable_id BIGINT, notes TEXT
    );
    CREATE INDEX IF NOT EXISTS idx_equipements_statut ON equipements(statut);
    CREATE INDEX IF NOT EXISTS idx_equipements_code ON equipements(code);

  END IF;
END $$;

-- 2.19 ambulance_service
-- ---------------------------------------------------------------------------
DO $$
BEGIN
  IF current_database() = 'ambulance_service' THEN

    CREATE TABLE IF NOT EXISTS ambulances (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        code VARCHAR(100) UNIQUE NOT NULL, immatriculation VARCHAR(50) NOT NULL UNIQUE,
        type VARCHAR(50) NOT NULL, statut VARCHAR(50) NOT NULL DEFAULT 'DISPONIBLE',
        equipements TEXT, conducteur_id BIGINT, personnel TEXT,
        date_derniere_maintenance DATE, kilometrage INTEGER,
        latitude DOUBLE PRECISION, longitude DOUBLE PRECISION,
        derniere_mise_a_jour_position TIMESTAMP
    );
    CREATE INDEX IF NOT EXISTS idx_ambulances_statut ON ambulances(statut);

  END IF;
END $$;

-- 2.20 imaging_service
-- ---------------------------------------------------------------------------
DO $$
BEGIN
  IF current_database() = 'imaging_service' THEN

    CREATE TABLE IF NOT EXISTS imagerie (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        patient_id BIGINT, medecin_id BIGINT,
        type_examen VARCHAR(100) NOT NULL,
        date_prescription TIMESTAMP, date_realisation TIMESTAMP,
        compte_rendu TEXT, lien_dicom VARCHAR(500),
        dicom_disponible BOOLEAN NOT NULL DEFAULT FALSE,
        statut VARCHAR(50) NOT NULL DEFAULT 'PRESCRIT'
    );
    CREATE INDEX IF NOT EXISTS idx_imagerie_patient_id ON imagerie(patient_id);
    CREATE INDEX IF NOT EXISTS idx_imagerie_statut ON imagerie(statut);

  END IF;
END $$;

-- 2.21 event_service
-- ---------------------------------------------------------------------------
DO $$
BEGIN
  IF current_database() = 'event_service' THEN

    CREATE TABLE IF NOT EXISTS evenements_service (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        titre VARCHAR(255) NOT NULL, description TEXT,
        type_evenement VARCHAR(100) NOT NULL, date_debut TIMESTAMP NOT NULL,
        date_fin TIMESTAMP, valide BOOLEAN NOT NULL DEFAULT FALSE
    );

  END IF;
END $$;

-- 2.22 hr_service
-- ---------------------------------------------------------------------------
DO $$
BEGIN
  IF current_database() = 'hr_service' THEN

    CREATE TABLE IF NOT EXISTS employes (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        matricule VARCHAR(100) UNIQUE, nom VARCHAR(255) NOT NULL,
        prenom VARCHAR(255) NOT NULL, email VARCHAR(255) NOT NULL UNIQUE,
        telephone VARCHAR(100) NOT NULL,
        fonction VARCHAR(100) NOT NULL, specialite VARCHAR(255),
        departement VARCHAR(100) NOT NULL,
        date_embauche DATE, actif BOOLEAN NOT NULL DEFAULT TRUE,
        user_id BIGINT
    );
    CREATE TABLE IF NOT EXISTS conges (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        employe_id BIGINT NOT NULL, date_debut DATE NOT NULL,
        date_fin DATE NOT NULL, type_conge VARCHAR(50) NOT NULL,
        motif VARCHAR(500), statut VARCHAR(50) NOT NULL DEFAULT 'EN_ATTENTE'
    );
    CREATE TABLE IF NOT EXISTS presences (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        employe_id BIGINT NOT NULL, date_presence TIMESTAMP NOT NULL,
        type VARCHAR(50), heures_travaillees DECIMAL(4,2)
    );
    CREATE INDEX IF NOT EXISTS idx_hr_employes_departement ON employes(departement);
    CREATE INDEX IF NOT EXISTS idx_hr_presences_employe_id ON presences(employe_id);
    CREATE INDEX IF NOT EXISTS idx_hr_conges_employe_id ON conges(employe_id);

  END IF;
END $$;

-- 2.23 scheduling_service
-- ---------------------------------------------------------------------------
DO $$
BEGIN
  IF current_database() = 'scheduling_service' THEN

    CREATE TABLE IF NOT EXISTS gardes_scheduling (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        employe_id BIGINT NOT NULL, date_debut TIMESTAMP NOT NULL,
        date_fin TIMESTAMP NOT NULL,
        type_garde VARCHAR(50) NOT NULL, statut VARCHAR(50) NOT NULL,
        service VARCHAR(100), notes TEXT
    );
    CREATE TABLE IF NOT EXISTS plannings (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mois INTEGER NOT NULL, annee INTEGER NOT NULL,
        service VARCHAR(100) NOT NULL, valide BOOLEAN NOT NULL DEFAULT FALSE,
        valide_par BIGINT, date_validation TIMESTAMP
    );
    CREATE TABLE IF NOT EXISTS absences (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        employe_id BIGINT NOT NULL, date_debut DATE NOT NULL,
        date_fin DATE NOT NULL, motif VARCHAR(500),
        type_absence VARCHAR(50), approuve_par BIGINT,
        valide BOOLEAN NOT NULL DEFAULT FALSE
    );
    CREATE INDEX IF NOT EXISTS idx_scheduling_gardes_employe_id ON gardes_scheduling(employe_id);
    CREATE INDEX IF NOT EXISTS idx_scheduling_absences_employe_id ON absences(employe_id);

  END IF;
END $$;

-- 2.24 notification_service
-- ---------------------------------------------------------------------------
DO $$
BEGIN
  IF current_database() = 'notification_service' THEN

    CREATE TABLE IF NOT EXISTS notifications (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        user_id BIGINT, destinataire VARCHAR(255) NOT NULL,
        sujet VARCHAR(255) NOT NULL,
        contenu TEXT NOT NULL, canal VARCHAR(50) NOT NULL,
        statut VARCHAR(50) NOT NULL DEFAULT 'EN_ATTENTE',
        date_envoi TIMESTAMP, date_creation TIMESTAMP NOT NULL DEFAULT NOW()
    );
    CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON notifications(user_id);
    CREATE INDEX IF NOT EXISTS idx_notifications_statut ON notifications(statut);

  END IF;
END $$;

-- 2.25 audit_service
-- ---------------------------------------------------------------------------
DO $$
BEGIN
  IF current_database() = 'audit_service' THEN

    CREATE TABLE IF NOT EXISTS audit_logs_service (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        service_source VARCHAR(255), action VARCHAR(255) NOT NULL,
        resource VARCHAR(255), resource_id VARCHAR(255),
        acteur VARCHAR(255), ip_adresse VARCHAR(100),
        details TEXT, date_action TIMESTAMP NOT NULL DEFAULT NOW()
    );
    CREATE TABLE IF NOT EXISTS audits_qualite (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        titre VARCHAR(255) NOT NULL, domaine VARCHAR(255),
        description TEXT, auditeur VARCHAR(255),
        date_audit TIMESTAMP, resultat VARCHAR(255)
    );
    CREATE TABLE IF NOT EXISTS incidents (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        titre VARCHAR(255) NOT NULL, description TEXT,
        niveau_gravite VARCHAR(50) NOT NULL, declare_par VARCHAR(255),
        date_declaration TIMESTAMP NOT NULL DEFAULT NOW(),
        resolu BOOLEAN NOT NULL DEFAULT FALSE, date_resolution TIMESTAMP,
        action_corrective TEXT
    );
    CREATE INDEX IF NOT EXISTS idx_audit_logs_service_action ON audit_logs_service(action);
    CREATE INDEX IF NOT EXISTS idx_audit_logs_service_resource ON audit_logs_service(resource);

  END IF;
END $$;

-- 2.26 reporting_service
-- ---------------------------------------------------------------------------
DO $$
BEGIN
  IF current_database() = 'reporting_service' THEN

    CREATE TABLE IF NOT EXISTS rapports (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        code VARCHAR(255) UNIQUE, titre VARCHAR(255) NOT NULL,
        type VARCHAR(100), format VARCHAR(50), parametres TEXT,
        statut VARCHAR(50), date_generation TIMESTAMP, genere_par BIGINT
    );
    CREATE TABLE IF NOT EXISTS rapports_planifies (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        rapport_id BIGINT, cron_expression VARCHAR(255), destinataires TEXT,
        actif BOOLEAN NOT NULL DEFAULT TRUE
    );

  END IF;
END $$;

-- 2.27 interconnect_service
-- ---------------------------------------------------------------------------
DO $$
BEGIN
  IF current_database() = 'interconnect_service' THEN

    CREATE TABLE IF NOT EXISTS inter_hospital_transfers (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        transfer_ref VARCHAR(255) NOT NULL UNIQUE, patient_id VARCHAR(255) NOT NULL,
        patient_name VARCHAR(255), patient_national_id VARCHAR(255),
        source_hospital_id VARCHAR(255) NOT NULL,
        source_hospital_name VARCHAR(255) NOT NULL,
        target_hospital_id VARCHAR(255) NOT NULL,
        target_hospital_name VARCHAR(255) NOT NULL,
        transfer_type VARCHAR(100) NOT NULL,
        status VARCHAR(100) NOT NULL DEFAULT 'PENDING',
        reason TEXT, medical_summary TEXT,
        encrypted_patient_data TEXT, attachments_metadata TEXT,
        initiated_by VARCHAR(255) NOT NULL, approved_by VARCHAR(255),
        received_by VARCHAR(255), initiated_at TIMESTAMP NOT NULL,
        approved_at TIMESTAMP, completed_at TIMESTAMP, rejected_at TIMESTAMP,
        rejection_reason TEXT, consent_obtained BOOLEAN NOT NULL DEFAULT FALSE,
        consent_document_ref VARCHAR(500), retry_count INTEGER NOT NULL DEFAULT 0,
        acknowledged BOOLEAN NOT NULL DEFAULT FALSE, acknowledged_at TIMESTAMP
    );
    CREATE INDEX IF NOT EXISTS idx_interconnect_status ON inter_hospital_transfers(status);
    CREATE INDEX IF NOT EXISTS idx_interconnect_patient_id ON inter_hospital_transfers(patient_id);

  END IF;
END $$;

-- 2.28 authorization_service
-- ---------------------------------------------------------------------------
DO $$
BEGIN
  IF current_database() = 'authorization_service' THEN

    CREATE TABLE IF NOT EXISTS authorization_users (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        nom VARCHAR(255) NOT NULL, prenom VARCHAR(255) NOT NULL,
        email VARCHAR(255) NOT NULL UNIQUE,
        role VARCHAR(50) NOT NULL DEFAULT 'PATIENT',
        actif BOOLEAN NOT NULL DEFAULT TRUE
    );
    CREATE TABLE IF NOT EXISTS services_autorises (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        user_id BIGINT NOT NULL, service_name VARCHAR(100) NOT NULL,
        scope VARCHAR(255), expiration TIMESTAMP
    );
    CREATE INDEX IF NOT EXISTS idx_auth_users_email ON authorization_users(email);
    CREATE INDEX IF NOT EXISTS idx_services_autorises_user_id ON services_autorises(user_id);

  END IF;
END $$;

-- 2.29 identity_service
-- ---------------------------------------------------------------------------
DO $$
BEGIN
  IF current_database() = 'identity_service' THEN

    CREATE TABLE IF NOT EXISTS identity_users (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        nom VARCHAR(255) NOT NULL, prenom VARCHAR(255) NOT NULL,
        email VARCHAR(255) NOT NULL UNIQUE,
        password_hash VARCHAR(255) NOT NULL,
        role VARCHAR(50) NOT NULL DEFAULT 'PATIENT',
        actif BOOLEAN NOT NULL DEFAULT TRUE
    );
    CREATE TABLE IF NOT EXISTS identity_sessions (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        user_id BIGINT NOT NULL, token_hash VARCHAR(255) NOT NULL,
        date_expiration TIMESTAMP NOT NULL, actif BOOLEAN NOT NULL DEFAULT TRUE
    );
    CREATE INDEX IF NOT EXISTS idx_identity_users_email ON identity_users(email);
    CREATE INDEX IF NOT EXISTS idx_identity_sessions_user_id ON identity_sessions(user_id);

  END IF;
END $$;

-- 2.30 outgoing_service
-- ---------------------------------------------------------------------------
DO $$
BEGIN
  IF current_database() = 'outgoing_service' THEN

    CREATE TABLE IF NOT EXISTS evenements_publics (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        titre VARCHAR(255) NOT NULL, description TEXT,
        type_evenement VARCHAR(100) NOT NULL, date_debut TIMESTAMP NOT NULL,
        date_fin TIMESTAMP, lieu VARCHAR(500), organisateur VARCHAR(255),
        statut VARCHAR(50) NOT NULL DEFAULT 'PLANIFIE',
        contenu_publication TEXT, publique BOOLEAN NOT NULL DEFAULT FALSE,
        image_url VARCHAR(500), lien_inscription VARCHAR(500)
    );
    CREATE INDEX IF NOT EXISTS idx_outgoing_evenements_statut ON evenements_publics(statut);

    CREATE TABLE IF NOT EXISTS transferts_patients (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        patient_id BIGINT, patient_nom VARCHAR(255),
        hopital_source VARCHAR(255), hopital_destination VARCHAR(255),
        motif_transfert TEXT, statut VARCHAR(50) NOT NULL DEFAULT 'EN_ATTENTE',
        medecin_referent_id BIGINT, date_demande TIMESTAMP,
        date_validation TIMESTAMP, valide_par_id BIGINT,
        notes_transport TEXT, urgence BOOLEAN NOT NULL DEFAULT FALSE
    );
    CREATE INDEX IF NOT EXISTS idx_outgoing_transferts_statut ON transferts_patients(statut);

    CREATE TABLE IF NOT EXISTS medecin_disponibilites (
        id BIGSERIAL PRIMARY KEY, cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
        mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
        medecin_id BIGINT NOT NULL, jour_semaine VARCHAR(50) NOT NULL,
        heure_debut TIME, heure_fin TIME,
        duree_consultation_minutes INTEGER DEFAULT 30,
        notes VARCHAR(500), actif BOOLEAN NOT NULL DEFAULT TRUE
    );
    CREATE INDEX IF NOT EXISTS idx_medecin_disponibilites_medecin_id ON medecin_disponibilites(medecin_id);

  END IF;
END $$;

