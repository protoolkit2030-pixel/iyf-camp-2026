# 📲 Comment obtenir l'APK — Guide Rapide

Il y a **3 façons** d'obtenir l'APK compilé. Choisissez la plus simple pour vous.

---

## 🥇 Option 1 — GitHub Actions (RECOMMANDÉE — GRATUITE, 10 minutes)

> Aucun logiciel à installer. GitHub compile l'APK dans le cloud et vous le donnez à télécharger.

### Étapes :

**1. Créer un compte GitHub** (si vous n'en avez pas) :
   → https://github.com/join (gratuit)

**2. Créer un nouveau dépôt** :
   → https://github.com/new
   - Nom : `iyf-camp-2026`
   - Visibilité : **Privé** (pour protéger le credentials.json)
   - ✅ Cliquez "Create repository"

**3. Uploader le code source** :
   - Cliquez "uploading an existing file"
   - Extrayez le ZIP `IYFCamp2026_SourceCode.zip`
   - Glissez-déposez TOUT le contenu du dossier `IYFCamp2026/`
   - Cliquez "Commit changes"

**4. L'APK se compile automatiquement !** 🎉
   - Allez dans l'onglet **Actions** de votre dépôt
   - Attendez que le workflow "Build IYF Camp 2026 APK" termine (~10 min)
   - Cliquez sur le workflow terminé → section **Artifacts**
   - Téléchargez `IYFCamp2026-debug-apk`

---

## 🥈 Option 2 — Android Studio (Si vous avez un PC)

### Prérequis :
- PC Windows/Mac/Linux avec 8 Go RAM minimum
- [Android Studio](https://developer.android.com/studio) (gratuit, ~1 Go)
- Connexion internet pour les dépendances Gradle

### Étapes :

**1. Installer Android Studio** → https://developer.android.com/studio

**2. Ouvrir le projet** :
   - Extrayez `IYFCamp2026_SourceCode.zip`
   - Android Studio → File → Open → choisir le dossier `IYFCamp2026`
   - Attendez la synchronisation Gradle (5-10 min la première fois)

**3. Ajouter les polices Poppins** :
   - Télécharger : https://fonts.google.com/specimen/Poppins → "Get font" → "Download all"
   - Copier dans `app/src/main/res/font/` :
     - `Poppins-Regular.ttf` → renommer en `poppins_regular.ttf`
     - `Poppins-Medium.ttf` → renommer en `poppins_medium.ttf`
     - `Poppins-SemiBold.ttf` → renommer en `poppins_semibold.ttf`
     - `Poppins-Bold.ttf` → renommer en `poppins_bold.ttf`
   - Supprimer les fichiers `.xml` dans le dossier `font/`

**4. Compiler l'APK** :
   - Menu : Build → Build Bundle(s)/APK(s) → Build APK(s)
   - Ou en ligne de commande :
     ```
     .\gradlew.bat assembleDebug   (Windows)
     ./gradlew assembleDebug       (Mac/Linux)
     ```

**5. Récupérer l'APK** :
   - Chemin : `app/build/outputs/apk/debug/app-debug.apk`
   - Android Studio affiche une notification avec un lien direct

---

## 🥉 Option 3 — Ligne de commande (Développeurs)

```bash
# Cloner / extraire le projet
cd IYFCamp2026

# Ajouter les polices Poppins TTF dans app/src/main/res/font/

# Rendre gradlew exécutable (Mac/Linux)
chmod +x gradlew

# Builder l'APK Debug
./gradlew assembleDebug

# Builder l'APK Release + AAB
./gradlew assembleRelease bundleRelease
```

---

## ⚡ Installer l'APK sur un téléphone Android

1. Activez les **Sources inconnues** sur votre téléphone :
   - Paramètres → Sécurité → Autoriser les sources inconnues ✅

2. Copiez `app-debug.apk` sur votre téléphone (USB, WhatsApp, Drive...)

3. Ouvrez le fichier APK sur votre téléphone → Installer

---

## ❓ Problèmes fréquents

| Problème | Solution |
|----------|----------|
| "Cannot find font poppins_regular" | Ajoutez les fichiers TTF comme indiqué ci-dessus |
| Gradle sync échoue | File → Invalidate Caches / Restart |
| "JAVA_HOME not set" | Installez JDK 17 et configurez JAVA_HOME |
| GitHub Actions échoue | Vérifiez l'onglet Actions pour les logs détaillés |

---

📞 **Support** : +225 07 59 87 21 26 / +225 07 49 54 56 06
