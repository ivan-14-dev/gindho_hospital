-- RBAC dynamique complet: validité temporelle, scope, conditions
-- Colonnes ajoutées avec NULL autorisé pour garantir la rétro-compatibilité.

ALTER TABLE role_permissions
  ADD COLUMN IF NOT EXISTS valid_from TIMESTAMP WITHOUT TIME ZONE,
  ADD COLUMN IF NOT EXISTS valid_to TIMESTAMP WITHOUT TIME ZONE,
  ADD COLUMN IF NOT EXISTS scope VARCHAR(255),
  ADD COLUMN IF NOT EXISTS condition_type VARCHAR(255);

ALTER TABLE role_template_permissions
  ADD COLUMN IF NOT EXISTS valid_from TIMESTAMP WITHOUT TIME ZONE,
  ADD COLUMN IF NOT EXISTS valid_to TIMESTAMP WITHOUT TIME ZONE,
  ADD COLUMN IF NOT EXISTS scope VARCHAR(255),
  ADD COLUMN IF NOT EXISTS condition_type VARCHAR(255);
