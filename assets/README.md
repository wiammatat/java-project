# Assets - Captures d'écran du Système de Gestion d'École

Ce dossier contient les captures d'écran de l'interface JavaFX du système de gestion d'école et de la base de données PostgreSQL.

## 📸 Captures d'écran disponibles

### Interface JavaFX - Onglets principaux
- `Etudiant.png` - Onglet de gestion des étudiants avec tableau et formulaires
- `Professeur.png` - Onglet de gestion des professeurs avec liste complète
- `cours.png` - Onglet de gestion des cours et matières
- `inscription.png` - Onglet de gestion des inscriptions étudiants/cours

### Base de données PostgreSQL (DBeaver)
- `studentdbeaver.png` - Table des étudiants dans DBeaver
- `professeurdbeaver.png` - Table des professeurs dans DBeaver  
- `coursdbeaver.png` - Table des cours dans DBeaver
- `enrolmentsdbeaver.png` - Table des inscriptions dans DBeaver

### 🎥 Vidéo de démonstration
- `Enregistrement 2026-01-15 101122.mp4` - Démonstration complète de l'application JavaFX *(fichier local - trop volumineux pour GitHub)*

## 🔧 Utilisation du système

### Interface JavaFX
L'application propose 4 onglets principaux permettant la gestion complète de l'école :
1. **Étudiants** : Ajout, modification, suppression d'étudiants
2. **Professeurs** : Gestion complète du corps professoral  
3. **Cours** : Création et gestion des cours/matières
4. **Inscriptions** : Attribution de cours aux étudiants

### Base de données PostgreSQL
Les données sont stockées de manière persistante dans PostgreSQL avec les tables :
- `students` : Informations des étudiants
- `professors` : Données des professeurs
- `courses` : Catalogue des cours
- `enrollments` : Relations étudiant-cours

## 🚀 Technologies utilisées

- **JavaFX** : Interface utilisateur moderne
- **PostgreSQL** : Base de données relationnelle
- **Gradle** : Gestion des dépendances et build
- **JDBC** : Connexion base de données
- **Java 17** : Langage de développement

## 📝 Comment reproduire

1. Clonez le projet : `git clone https://github.com/wiammatat/java-project.git`
2. Configurez PostgreSQL avec la base `school`
3. Compilez : `gradlew build`  
4. Lancez : `gradlew run`

---

**Créé le** : 15 janvier 2026  
**Usage** : Documentation et présentation du projet
