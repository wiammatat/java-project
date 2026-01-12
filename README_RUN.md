  Lancer l'application (Windows)

Pré-requis
- JDK 17 (recommandé)
- Gradle wrapper fourni (utiliser ./gradlew.bat)

Installer Temurin 17 via winget (si disponible):
  winget install EclipseAdoptium.Temurin.17.JDK

Si vous installez manuellement, définissez la variable d'environnement JAVA_HOME vers le dossier du JDK.

Commandes:
- PowerShell: .\run.ps1
- CMD: run.bat
- Directement: .\gradlew.bat run

Remarques:
- Gradle dans ce projet est configuré pour utiliser Java 17 via toolchain. Si Gradle se plaint de JAVA_HOME, assurez-vous que votre JDK est installé et que la variable est correctement définie.
- Si vous préférez utiliser IntelliJ, configurez "Gradle JVM" dans les paramètres Gradle (Build Tools > Gradle) vers un JDK compatible (17).

