# 📱 IYF Camp 2026 — Application d'Inscription

Application Android officielle pour l'inscription au **Camp d'Étude et de Formation IYF 2026**
organisé par l'**International Youth Fellowship**.

---

## 🎯 Fonctionnalités

- ✅ Formulaire d'inscription en 3 étapes (informations personnelles, scolaires, paiement)
- ✅ Enregistrement dans Google Sheets automatiquement
- ✅ Génération de reçu PDF avec QR Code
- ✅ Partage du reçu (WhatsApp, Email, etc.)
- ✅ Mode hors-ligne avec synchronisation automatique
- ✅ Notifications de rappel (J-1 et Jour J)
- ✅ Liste des inscriptions effectuées depuis l'appareil
- ✅ Validation en temps réel des champs
- ✅ Interface moderne en Jetpack Compose

---

## 🛠️ Prérequis

- **Android Studio** Hedgehog (2023.1.1) ou supérieur
- **JDK 17** (inclus dans Android Studio)
- **Connexion Internet** pour la synchronisation Google Sheets
- **Android SDK** API 24+ (Android 7.0)

---

## 🚀 Installation & Build

### Étape 1 — Cloner/Ouvrir le projet

1. Ouvrez **Android Studio**
2. Sélectionnez **File → Open** et choisissez le dossier `IYFCamp2026`
3. Attendez que Gradle synchronise les dépendances (peut prendre 3-5 min la première fois)

### Étape 2 — Ajouter les polices Poppins (OBLIGATOIRE)

Téléchargez les polices Poppins sur [Google Fonts](https://fonts.google.com/specimen/Poppins) :

1. Cliquez "Get font" → "Download all"
2. Extrayez et copiez ces fichiers dans `app/src/main/res/font/` :
   - `Poppins-Regular.ttf` → renommez en `poppins_regular.ttf`
   - `Poppins-Medium.ttf` → renommez en `poppins_medium.ttf`
   - `Poppins-SemiBold.ttf` → renommez en `poppins_semibold.ttf`
   - `Poppins-Bold.ttf` → renommez en `poppins_bold.ttf`
3. **Supprimez** le fichier `app/src/main/res/font/poppins_regular.xml`

### Étape 3 — Générer l'APK Debug

Dans Android Studio :
```
Build → Build Bundle(s) / APK(s) → Build APK(s)
```
L'APK sera généré dans : `app/build/outputs/apk/debug/app-debug.apk`

Ou via la ligne de commande :
```bash
./gradlew assembleDebug
```

### Étape 4 — Générer l'APK/AAB Release (Play Store)

#### 4.1 Créer un Keystore

```bash
mkdir -p app/keystore
keytool -genkey -v \
  -keystore app/keystore/iyf-camp-2026.jks \
  -alias iyf-camp \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -storepass iyf2026camp \
  -keypass iyf2026camp \
  -dname "CN=IYF Camp, OU=IYF, O=International Youth Fellowship, L=Abidjan, S=CI, C=CI"
```

#### 4.2 Builder l'APK Release

```bash
./gradlew assembleRelease
```
APK Release : `app/build/outputs/apk/release/app-release.apk`

#### 4.3 Builder le Bundle AAB (Play Store)

```bash
./gradlew bundleRelease
```
Bundle AAB : `app/build/outputs/bundle/release/app-release.aab`

---

## 🏗️ Architecture

```
app/src/main/
├── assets/
│   └── credentials.json          # Service Account Google Sheets
├── java/com/iyf/camp2026/
│   ├── IYFCampApplication.kt     # Application (Hilt)
│   ├── MainActivity.kt           # Point d'entrée
│   ├── data/
│   │   ├── local/                # Room Database
│   │   │   ├── AppDatabase.kt
│   │   │   ├── InscriptionDao.kt
│   │   │   └── InscriptionEntity.kt
│   │   ├── remote/
│   │   │   ├── GoogleSheetsService.kt  # Intégration Google Sheets
│   │   │   └── SheetsModels.kt
│   │   └── repository/
│   │       └── InscriptionRepository.kt
│   ├── di/                       # Injection de dépendances (Hilt)
│   │   ├── AppModule.kt
│   │   └── DatabaseModule.kt
│   ├── domain/model/
│   │   └── Inscription.kt        # Modèle principal
│   ├── ui/
│   │   ├── navigation/
│   │   │   └── AppNavigation.kt  # Navigation Compose
│   │   ├── screens/
│   │   │   ├── splash/           # Écran 1: Splash
│   │   │   ├── home/             # Écran 2: Accueil
│   │   │   ├── registration/     # Écran 3: Formulaire (3 étapes)
│   │   │   ├── confirmation/     # Écran 4: Confirmation & Reçu
│   │   │   └── myregistrations/  # Écran 5: Mes inscriptions
│   │   └── theme/
│   │       ├── Color.kt          # Couleurs IYF
│   │       ├── Theme.kt          # Thème Material3
│   │       └── Type.kt           # Typographie Poppins
│   └── utils/
│       ├── JwtHelper.kt          # JWT pour l'authentification Google
│       ├── NetworkUtils.kt       # Gestion réseau
│       ├── NotificationHelper.kt # Notifications locales
│       ├── PdfGenerator.kt       # Génération PDF natif Android
│       ├── QrCodeGenerator.kt    # QR Code ZXing
│       └── ReferenceGenerator.kt # Génération N° référence
```

---

## 🔑 Configuration Google Sheets

Le fichier `credentials.json` est déjà configuré dans `app/src/main/assets/`.

Le compte de service `iyf-camp-service@iyf-camp-2026.iam.gserviceaccount.com` doit avoir accès
en **écriture** au Google Sheets.

Pour vérifier l'accès :
1. Ouvrez https://docs.google.com/spreadsheets/d/1pm9vCmy4TAoa7EQqMRFlyYu01GahFN6OeAMOHecQnHs
2. Cliquez sur **Partager**
3. Vérifiez que `iyf-camp-service@iyf-camp-2026.iam.gserviceaccount.com` a le rôle **Éditeur**

---

## 📊 Structure du Google Sheets

| Colonne | Champ |
|---------|-------|
| A | N° Référence (IYF-CAMP-XXXXXX) |
| B | Date & Heure |
| C | Nom |
| D | Prénom |
| E | Date de naissance |
| F | Sexe |
| G | Téléphone |
| H | Email |
| I | Quartier/Commune |
| J | Établissement |
| K | Classe/Niveau |
| L | Filière |
| M | Cours sélectionnés |
| N | Montant (FCFA) |
| O | Mode de paiement |
| P | N° Transaction Mobile Money |
| Q | Statut |

---

## 📱 Publication sur le Play Store

### Informations pour la fiche Play Store

- **Nom de l'app** : IYF Camp 2026 - Inscription
- **Catégorie** : Éducation
- **Description courte** : Inscrivez-vous au Camp d'Étude et de Formation IYF 2026
- **Description longue** :
  Inscrivez-vous facilement au Camp d'Étude et de Formation organisé par l'International Youth
  Fellowship (IYF) du 08 au 10 Avril 2026 au Cours Secondaire Méthodiste (CSM) Niangon.
  Cours disponibles : Français, Philosophie, Mathématiques, Physique-Chimie.
  Prix : 5 000 FCFA. Paiement Mobile Money ou espèces.

### Screenshots recommandés

Prenez des captures d'écran de :
1. L'écran d'accueil (bannière du camp)
2. Le formulaire d'inscription (étape 1)
3. La confirmation d'inscription avec le numéro de référence
4. Un reçu PDF généré
5. La liste des inscriptions

---

## 🐛 Dépannage

### Erreur de compilation "Cannot find font"
→ Assurez-vous d'avoir ajouté les fichiers `.ttf` Poppins dans `res/font/`

### Erreur Google Sheets "403 Forbidden"
→ Vérifiez que le compte de service a accès en écriture au spreadsheet

### Erreur "Cannot generate PDF"
→ Vérifiez les permissions `WRITE_EXTERNAL_STORAGE` sur Android < 9

### Gradle sync échoue
→ `File → Invalidate Caches / Restart` dans Android Studio

---

## 📞 Support

- **Téléphone** : +225 07 59 87 21 26 / +225 07 49 54 56 06 / +225 05 65 65 44 70
- **Organisation** : International Youth Fellowship (IYF)
- **Événement** : Camp d'Étude et de Formation 2026

---

## 📄 Licence

Application développée exclusivement pour l'IYF Camp 2026.
© 2026 International Youth Fellowship. Tous droits réservés.
