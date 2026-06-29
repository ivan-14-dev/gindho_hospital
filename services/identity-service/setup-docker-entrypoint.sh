#!/bin/bash
set -euo pipefail

# Variables d'environnement HOME autorisées mais non utilisées ici
export HOME=/tmp

# Fonction pour charger .env en respectant le format KEY=VAL(simple)
# Ne fait pas de traitement de vérification - juste exporte dans le shell environ
load_env_file() {
  local env_file=$1
  if [[ -f "$env_file" ]]; then
    # Définir IFS uniquement pour cette commande de lecture
    while IFS='=' read -r key value; do
      # Ignorer les lignes vides ou commençant par #
      [[ -z "$key" || "$key" =~ ^# ]] && continue
      # Supprimer les caractères de commentaire en fin de ligne (échappés)
      # Regex simple pour traiter # comme comment sauf si échappé
      key=$(echo "$key" | sed -E 's/(.*)#.*$/\1/')
      value=$(echo "$value" | sed -E 's/^[[:space:]]*//; s/[[:space:]]*$//')
      export "$key=$value"
    done < "$env_file"
  fi
}

# Charger les variables d'environnement depuis l'emplacement du .env local
ENV_FILE="$(cd "$(dirname "${BASH_SOURCE[0]}")"; pwd)/.env"
load_env_file "$ENV_FILE"

# Définir le port serveur à partir de SERVER_PORT (défaut 9001 pour cohérence avec application.yml)
# Si __RESOURCE_PORTS__ est défini (de flask), utiliser cette valeur pour le port de l'hôte
if [[ -n "${__RESOURCE_PORTS__:-}" ]]; then
  # Parser l'objet imbriqué JSON:
  # "__RESOURCE_PORTS__": {"identity-service": {"6010": "9001"}}
  # simple parser : trouver 0000": "9001"
  export SERVER_PORT=$(echo "$__RESOURCE_PORTS__" | grep -o '[0-9]\{4\}\": \"[0-9]\{4\}\)' | cut -d: -f4 | sed 's/"//g' | tail -1)
fi

# Exécuter l'initialisation de setup si IDENTITY_SERVICE_SETUP="setup"
if [[ "$IDENTITY_SERVICE_SETUP" == "setup" ]]; then
  echo "-> Exécution de IDENTITY_SERVICE_SETUP=$IDENTITY_SERVICE_SETUP"
  if [[ -f "target/*.jar" ]]; then
    java -Dspring.main.banner-mode=off -Dserver.port=${SERVER_PORT} -Djava.security.egd=file:/dev/./urandom -jar "target/*.jar" --spring.profiles.active=setup
  else
    echo "JAR non trouvé dans target/ - pas d'exécution Spring"
  fi
fi

# Enregistrer le port final pour reference dans les logs (Kilo utilisera /media/ivan/.config/kilo/state)
echo "-> Identity Service démarré sur le port $SERVER_PORT"
echo "   .env chargé depuis $ENV_FILE"

# Lancement réel de l'application : essayer le jar s'il est construit, sinon java -jar approprié
if [[ -f "target/*.jar" ]]; then
  echo "-> Démarrage de Identity Service via jar"
  exec java -Dspring.main.banner-mode=off -Dserver.port=${SERVER_PORT} -Djava.security.egd=file:/dev/./urandom -jar "target/*.jar"
else
  echo "-> JAR non construit, rien à exécuter"
  exit 1
fi