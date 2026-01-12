Résolution de l'erreur : "Votre build est configuré pour utiliser Java 25.0.1 mais Gradle 8.10 supporte jusqu'à Java 23"

Problème
--------
Gradle (version 8.10) installé dans cet environnement ne supporte pas l'exécution sur JVM Java 25. Votre IDE/ordinateur utilise Java 25 comme Gradle JVM, ce qui provoque l'échec de synchronisation.

Solutions possibles
-------------------
1) Changer la JVM utilisée par Gradle dans IntelliJ IDEA
   - File > Settings > Build, Execution, Deployment > Build Tools > Gradle
   - Sous "Gradle JVM", choisissez un JDK compatible (par ex. AdoptOpenJDK/Temurin 17 ou 23).
   - Appliquer et re-synchroniser le projet.

2) Forcer Gradle localement via `gradle.properties`
   - Ouvrez `gradle.properties` (à la racine du projet) et décommentez `org.gradle.java.home`
   - Mettez le chemin vers un JDK installé, par ex.:
     org.gradle.java.home=C:\Program Files\Java\jdk-17

3) Mettre à jour Gradle (option avancée)
   - Mettre à jour Gradle wrapper vers une version qui supporte Java 25. Ceci peut être risqué
     si des plugins ne sont pas compatibles.
   - Commande (ayant soin de tester) :
     gradlew wrapper --gradle-version 8.##   (choisir version compatible)

Recommandation
--------------
Le plus simple et le plus sûr est d'utiliser Java 17 car votre `build.gradle` cible déjà Java 17 via toolchains.

Si vous voulez que je modifie `gradle.properties` pour pointer vers un JDK 17 installé sur votre machine, dites-moi le chemin complet du JDK (ex: C:\Program Files\Java\jdk-17). Sinon suivez les étapes 1 ou 2 ci-dessus.

