# ApocalypseTower

Jeu développé avec [libGDX](https://libgdx.com/).

## Prérequis

- JDK 8+ installé
- Gradle Wrapper inclus (`./gradlew` / `gradlew.bat`)
- Accès Internet au premier lancement pour télécharger les dépendances

## Exécution rapide

Script fourni (Linux/macOS) :

```bash
chmod +x run.sh
./run.sh
```

Sous Windows (sans script) :

```bat
gradlew.bat lwjgl3:run
```

## Compilation rapide

Construire un JAR exécutable :

```bash
./gradlew lwjgl3:jar
```

Le JAR est généré dans `lwjgl3/build/libs`.

## Contrôles

| Touche | Action |
| --- | --- |
| `ZQSD` | Déplacer le joueur (clavier AZERTY) |
| `WASD` | Déplacer le joueur (clavier QWERTY) |
| `Flèches` | Déplacer la caméra |
| `Espace` | Recentrer la caméra sur le joueur |
| `Molette` | Zoom caméra |
| `Échap` | Pause |
