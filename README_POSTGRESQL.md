# Système de Gestion d'École - Intégration PostgreSQL et DBeaver

## 🎯 Nouvelles Fonctionnalités (Branche `ajoute-dbeaver`)

Cette branche apporte une intégration complète avec PostgreSQL et DBeaver, permettant de migrer du stockage local vers une base de données relationnelle moderne.

## 📋 Fonctionnalités Ajoutées

### 🗄️ Support PostgreSQL
- **RepositoryJdbc** : Nouvelle implémentation pour PostgreSQL
- **Basculement automatique** : L'application utilise PostgreSQL si disponible, sinon retombe sur le stockage local
- **Configuration flexible** : Fichier `db.properties` pour la configuration de base de données
- **Script SQL** : `schema.sql` avec création de tables et données de test

### 🔧 Architecture Améliorée
- **RepositoryInterface** : Abstraction commune pour tous les types de stockage
- **RepositoryFactory** : Factory pattern pour choisir automatiquement le bon repository
- **Support des types PostgreSQL** : Gestion correcte des arrays et types numériques

### 📊 Compatibilité DBeaver
- Configuration prête pour DBeaver
- Tables optimisées avec index et contraintes
- Données de test pour commencer rapidement

## 🚀 Installation et Configuration

### Prérequis
- PostgreSQL 12+ installé
- DBeaver (optionnel, pour interface graphique)
- Java 17+
- Gradle

### Configuration PostgreSQL

1. **Installer PostgreSQL** (si pas déjà fait)
2. **Créer la base de données** :
```sql
CREATE DATABASE school;
CREATE USER school_user WITH PASSWORD 'VotreMotDePasse';
GRANT ALL PRIVILEGES ON DATABASE school TO school_user;
```

3. **Exécuter le script de création** :
```bash
psql -h localhost -U school_user -d school -f schema.sql
```

4. **Configurer l'application** :
Modifier `src/main/resources/db.properties` :
```properties
db.url=jdbc:postgresql://localhost:5432/school
db.user=school_user
db.password=VotreMotDePasse
```

### Configuration DBeaver

1. **Créer une nouvelle connexion PostgreSQL**
2. **Paramètres de connexion** :
   - Host : `localhost`
   - Port : `5432`
   - Database : `school`
   - Username : `school_user`
   - Password : votre mot de passe

## 🏃‍♂️ Lancement

```bash
# Compilation
./gradlew build

# Lancement
./gradlew run
```

L'application détecte automatiquement PostgreSQL :
- ✅ **PostgreSQL disponible** → Titre : "Système gestion école - PostgreSQL"
- ⚠️ **PostgreSQL indisponible** → Titre : "Système gestion école - Local" (fallback automatique)

## 📈 Structure de la Base de Données

### Tables Principales

```sql
-- Étudiants
CREATE TABLE students (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE
);

-- Professeurs
CREATE TABLE professors (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE
);

-- Cours
CREATE TABLE courses (
    id SERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    professor_id INTEGER REFERENCES professors(id)
);

-- Inscriptions avec notes
CREATE TABLE enrollments (
    id SERIAL PRIMARY KEY,
    student_id INTEGER REFERENCES students(id),
    course_id INTEGER REFERENCES courses(id),
    grades numeric[] DEFAULT ARRAY[]::numeric[],
    UNIQUE(student_id, course_id)
);
```

## 🔍 Fonctionnalités Techniques

### Gestion des Notes
- **Arrays PostgreSQL** : Les notes sont stockées dans des tableaux `numeric[]`
- **Conversion automatique** : Gestion du casting `BigDecimal` ↔ `Double`
- **Interface unifiée** : Même API que le stockage local

### Gestion des Erreurs
- **Contraintes d'unicité** : Emails uniques, codes de cours uniques
- **Clés étrangères** : Intégrité référentielle garantie
- **Transactions** : Opérations atomiques pour la suppression

### Performance
- **Index optimisés** : Sur les colonnes fréquemment recherchées
- **Connection pooling** : Réutilisation des connexions
- **Requêtes préparées** : Protection contre l'injection SQL

## 🛠️ Développement

### Architecture du Code

```
src/main/java/com/example/app/
├── RepositoryInterface.java      # Interface commune
├── Repository.java               # Stockage local (sérialisé)
├── RepositoryJdbc.java          # Stockage PostgreSQL
├── RepositoryFactory.java       # Factory pour choisir le repository
└── MainFX.java                  # Interface utilisateur
```

### Ajout de Nouvelles Fonctionnalités

Pour ajouter une nouvelle méthode au repository :

1. **Ajouter à l'interface** `RepositoryInterface`
2. **Implémenter dans** `Repository` (stockage local)
3. **Implémenter dans** `RepositoryJdbc` (PostgreSQL)
4. **Utiliser dans** `MainFX`

## 🐛 Dépannage

### Problèmes Courants

**Erreur de connexion PostgreSQL** :
```
⚠ PostgreSQL non disponible, utilisation du stockage local
```
→ Vérifier que PostgreSQL est démarré et que les paramètres de connexion sont corrects

**ClassCastException avec les notes** :
→ Déjà corrigé dans cette version (gestion BigDecimal/Double)

**Fichiers volumineux Git** :
→ Les dossiers `build/` et `.gradle/` sont maintenant exclus via `.gitignore`

## 📚 Ressources

- [Documentation PostgreSQL](https://www.postgresql.org/docs/)
- [Guide DBeaver](https://dbeaver.io/docs/)
- [JavaFX Documentation](https://openjfx.io/)

## 🤝 Contribution

Cette branche est prête pour :
- Tests avec différentes configurations PostgreSQL
- Ajout de nouvelles fonctionnalités métier
- Optimisations de performance
- Migration vers d'autres bases de données

---

**Branche créée le** : 12 janvier 2026  
**Auteur** : Migration PostgreSQL/DBeaver  
**Status** : ✅ Fonctionnel et testé
