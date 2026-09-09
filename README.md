# MCWebApi

Un mod Minecraft Forge 1.20.1 qui expose une API web REST pour interagir avec le serveur Minecraft.

## Fonctionnalités

- API REST pour récupérer les informations des joueurs en ligne
- Récupération des coordonnées, santé, niveau et dimension des joueurs
- Port configurable (par défaut: 25580)
- Serveur HTTP intégré Java (pas de dépendances externes)
- Léger et performant (seulement 14K)
- Compatible avec CurseForge

## Installation

1. Compilez le mod avec Gradle:
   ```bash
   gradlew.bat build
   ```
   Ou sur Linux/Mac:
   ```bash
   ./gradlew build
   ```

2. Le fichier JAR sera généré dans `build/libs/mcwebapi-1.0.0.jar` (14K)

   Le mod utilise le serveur HTTP intégré de Java, donc aucune dépendance externe n'est nécessaire.

3. Placez le fichier JAR dans le dossier `mods` de votre serveur Minecraft Forge 1.20.1

## Configuration

Le fichier de configuration sera créé automatiquement au premier démarrage du serveur:
`config/mcwebapi.json`

```json
{
  "enabled": true,
  "port": 25580,
  "apiKey": ""
}
```

- `enabled`: Active/désactive l'API web
- `port`: Port sur lequel l'API sera accessible
- `apiKey`: Clé API pour sécuriser l'accès (à implémenter)

## Endpoints API

### GET /
Retourne les informations de base sur l'API

**Exemple de réponse:**
```json
{
  "name": "MCWebApi",
  "version": "1.0.0",
  "description": "Web API for Minecraft server communication"
}
```

### GET /api/players
Retourne la liste de tous les joueurs en ligne avec leurs informations

**Exemple de réponse:**
```json
{
  "online": 2,
  "max": 20,
  "players": [
    {
      "name": "Player1",
      "uuid": "12345678-1234-1234-1234-123456789012",
      "x": 100,
      "y": 64,
      "z": 200,
      "dimension": "minecraft:overworld",
      "health": 20.0,
      "foodLevel": 20,
      "level": 5
    }
  ]
}
```

### GET /api/players/{username}
Retourne les informations d'un joueur spécifique

**Exemple de réponse:**
```json
{
  "name": "Player1",
  "uuid": "12345678-1234-1234-1234-123456789012",
  "x": 100,
  "y": 64,
  "z": 200,
  "dimension": "minecraft:overworld",
  "health": 20.0,
  "foodLevel": 20,
  "level": 5
}
```

Si le joueur n'est pas trouvé, retourne un code 404:
```json
{
  "error": "Player not found: PlayerName"
}
```

## Développement

### Prérequis
- Java 17 ou supérieur
- Le Gradle wrapper est inclus dans le projet (gradlew/gradlew.bat)

### Structure du projet
```
src/main/java/com/mcwebapi/
├── MCWebApi.java              # Classe principale du mod
├── config/
│   └── Config.java            # Gestion de la configuration
└── web/
    ├── WebServer.java         # Serveur Javalin
    └── endpoints/
        └── PlayerEndpoint.java # Endpoints pour les joueurs
```

### Compiler et tester

**Windows:**
```bash
# Compiler le mod
gradlew.bat build

# Lancer le client de test
gradlew.bat runClient

# Lancer le serveur de test
gradlew.bat runServer
```

**Linux/Mac:**
```bash
# Compiler le mod
./gradlew build

# Lancer le client de test
./gradlew runClient

# Lancer le serveur de test
./gradlew runServer
```

## Licence

MIT
