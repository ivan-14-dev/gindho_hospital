-- Migration Flyway: mise à jour de la contrainte CHECK sur users.role
-- IMPORTANT (FR) :
-- 1) Lancer la migration:
--    mvn -f backend/pom.xml flyway:migrate
-- 2) Vérifier la contrainte:
--    psql -h localhost -U mon_user1 -d gindho -c "SELECT conname, pg_get_constraintdef(oid) FROM pg_constraint WHERE conname='users_role_check';"

ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check;

ALTER TABLE users
ADD CONSTRAINT users_role_check
CHECK (
  (role)::text = ANY (
    ARRAY[
      'ADMIN',
      'SUPER_ADMIN',
      'MEDECIN',
      'PATIENT',
      'UTILISATEUR_SECONDAIRE',
      'NURSE',
      'RECEPTION',
      'PHARMACIST',
      'LABORATORY',
      'HOSPITALIZATION_SERVICE',
      'ACCOUNTING',
      'URGENCY'
    ]::text[]
  )
);
