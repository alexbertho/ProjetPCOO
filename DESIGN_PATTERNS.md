# Design Patterns Utilisés

## 1. Flyweight Pattern (Projectiles)

### Concept
Le pattern Flyweight optimise la mémoire en séparant les données **immuables partagées** (intrinsic state) des données **uniques par instance** (extrinsic state).

### Implémentation

#### `ProjectileType.java` (Flyweight intrinsic state)
```java
public enum ProjectileType {
    SIMPLE(25f, 200f, 5f, false),
    AOE(40f, 180f, 10f, true);
    
    private final float damage;
    private final float speed;
    private final float explosionRadius;
    private final boolean isAOE;
    // getters...
}
```

#### `Projectile.java` (extrinsic state)
```java
public class Projectile extends GameObject {
    private final ProjectileType type;  // intrinsic
    private Vector2 velocity;           // extrinsic
    private Enemy target;               // extrinsic
    private Array<Enemy> allEnemies;   // pour AOE
    private boolean dead;               // extrinsic
    
    public Projectile(Vector2 start, Enemy target, ProjectileType type, Array<Enemy> allEnemies) {
        // ...
    }
}
```

#### `ProjectileTypeFactory.java`
```java
public class ProjectileTypeFactory {
    private static final Map<String, ProjectileType> typeCache = new HashMap<>();
    
    static {
        typeCache.put("SIMPLE", ProjectileType.SIMPLE);
        typeCache.put("AOE", ProjectileType.AOE);
    }
    
    public static ProjectileType getType(String typeName) {
        return typeCache.getOrDefault(typeName.toUpperCase(), ProjectileType.SIMPLE);
    }
}
```

### Avantages
- **Économie mémoire** : une seule instance de `ProjectileType.SIMPLE` partagée par tous les projectiles SIMPLE
- **Performance** : pas de création/destruction répétée d'objets identiques
- **Flexibilité** : facile d'ajouter de nouveaux types

### Utilisation
```java
Projectile simple = new Projectile(start, target, ProjectileType.SIMPLE, enemies);
Projectile aoe = new Projectile(start, target, ProjectileType.AOE, enemies);
```

---

## 2. Builder Pattern (Tours)

### Concept
Le Builder Pattern simplifie la création d'objets complexes avec de nombreux paramètres optionnels.

### Implémentation

#### `TowerBuilder.java`
```java
public class TowerBuilder {
    private float x;
    private float y;
    private float range = 150f;
    private float fireRate = 1f;
    private ProjectileType projectileType = ProjectileType.SIMPLE;
    private Array<Enemy> enemies;
    private Array<Projectile> projectiles;
    
    public TowerBuilder(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    public TowerBuilder withRange(float range) {
        this.range = range;
        return this;
    }
    
    public TowerBuilder withProjectileType(ProjectileType type) {
        this.projectileType = type;
        return this;
    }
    
    public Tower build() {
        Tower tower = new Tower(x, y, range, fireRate, enemies, projectiles);
        tower.setProjectileType(projectileType);
        return tower;
    }
}
```

### Utilisation
```java
Tower simpleTower = new TowerBuilder(400, 300)
    .withRange(200f)
    .withFireRate(1.5f)
    .withProjectileType(ProjectileType.SIMPLE)
    .withEnemies(enemies)
    .withProjectiles(projectiles)
    .build();

Tower aoeTower = new TowerBuilder(600, 200)
    .withRange(180f)
    .withFireRate(1.0f)
    .withProjectileType(ProjectileType.AOE)
    .withEnemies(enemies)
    .withProjectiles(projectiles)
    .build();
```

### Avantages
- **Lisibilité** : construction explicite et facile à lire
- **Flexibilité** : configuration variable sans surcharges multiples
- **Maintenabilité** : ajout facile de nouvelles propriétés

---

## 3. Manager Pattern (Gestion des projectiles)

### Concept
Centraliser la gestion d'un ensemble d'objets pour appliquer des règles globales.

### Implémentation

#### `ProjectileManager.java`
```java
public class ProjectileManager {
    private Array<Projectile> projectiles;
    
    public void update(float delta) {
        for (int i = projectiles.size - 1; i >= 0; i--) {
            Projectile p = projectiles.get(i);
            p.update(delta);
            
            if (p.isDead()) {
                projectiles.removeIndex(i);  // Suppression itérative sûre
            }
        }
    }
    
    public void render(SpriteBatch batch) {
        for (Projectile p : projectiles) {
            p.render(batch);
        }
    }
}
```

### Utilisation dans `GameScreen`
```java
projectileManager = new ProjectileManager(projectiles);

// Dans render():
projectileManager.update(delta);  // Gère la suppression auto des projectiles morts
projectileManager.render(batch);
```

### Avantages
- **Centralisation** : logique de nettoyage centralisée et sûre
- **Extensibilité** : facile d'ajouter des effets globaux (friction, explosions, etc.)
- **Performance** : peut implémenter du spatial hashing, pooling, etc.

---

## 4. Extension Future : Object Pool Pattern

Pour optimiser davantage les performances avec beaucoup de projectiles :

```java
public class ProjectilePool {
    private Queue<Projectile> available = new LinkedList<>();
    private int maxPoolSize = 100;
    
    public Projectile acquire(Vector2 start, Enemy target, ProjectileType type) {
        Projectile p = available.poll();
        if (p == null) {
            p = new Projectile(start, target, type);
        } else {
            p.reset(start, target, type);
        }
        return p;
    }
    
    public void release(Projectile p) {
        if (available.size() < maxPoolSize) {
            available.offer(p);
        }
    }
}
```

---

## Résumé des changements

| Fichier | Pattern | Description |
|---------|---------|-------------|
| `ProjectileType.java` | Flyweight | Types immuables partagés |
| `Projectile.java` | Flyweight | Utilise ProjectileType, supporte AOE |
| `ProjectileTypeFactory.java` | Factory | Crée/cache les types |
| `TowerBuilder.java` | Builder | Construction fluide de tours |
| `ProjectileManager.java` | Manager | Gestion centralisée des projectiles |
| `Tower.java` | Amélioration | Utilise ProjectileType, supporte polymorphie |
| `GameScreen.java` | Intégration | Utilise Builder et Manager |

---

## Prochains patterns à considérer

1. **Object Pool** : Recycler les projectiles pour éviter GC
2. **Strategy Pattern** : Différentes IA pour ennemis/tours
3. **Observer Pattern** : Événements (tour tuée, vague terminée, etc.)
4. **State Pattern** : États du joueur (idle, attacking, dead)
5. **Command Pattern** : Queue d'actions pour resync multijoueur
