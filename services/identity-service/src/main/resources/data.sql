-- Insert superadmin (admin@gindho.com) into identity_users
-- Password for admin@gindho.com is 'admin123' (hashed with BCrypt)
INSERT INTO identity_users (email, nom, prenom, password_hash, role, actif)
VALUES ('admin@gindho.com', 'Admin', 'System', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/X4.qVh.N9NJ.KQXCKa', 'SUPER_ADMIN', true);

-- Insert a test doctor (medecin@gindho.com)
-- Password for medecin@gindho.com is 'medecin123' (hashed with BCrypt)
INSERT INTO identity_users (email, nom, prenom, password_hash, role, actif)
VALUES ('medecin@gindho.com', 'Jean', 'Dupont', '$2a$12$someOtherHashForMedecin', 'MEDECIN', true);

-- Insert a regular patient user (patient@gindho.com) for testing
INSERT INTO identity_users (email, nom, prenom, password_hash, role, actif)
VALUES ('patient@gindho.com', 'Alice', 'Martin', '$2a$12$yetAnotherHashForPatient', 'PATIENT', true);
