# 🔐 Système de Messagerie Sécurisée RSA

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![C](https://img.shields.io/badge/C-11+-blue.svg)](https://en.cppreference.com/w/c)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-4479A1.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

Un système de messagerie sécurisée combinant la puissance du C pour le chiffrement RSA et la flexibilité de Java pour l'interface utilisateur et la gestion des données.

## 📋 Table des matières

- [Présentation](#-présentation)
- [Fonctionnalités](#-fonctionnalités)
- [Architecture](#-architecture)
- [Technologies utilisées](#-technologies-utilisées)
- [Prérequis](#-prérequis)
- [Installation](#-installation)
- [Utilisation](#-utilisation)
- [Structure du projet](#-structure-du-projet)
- [Sécurité](#-sécurité)
- [Améliorations futures](#-améliorations-futures)
- [Contribution](#-contribution)
- [Auteur](#-auteur)
- [License](#-license)

## 🎯 Présentation

Ce projet a été développé dans le cadre des modules **Programmation Objet Avancée** et **Algorithmique Avancée** en L3 ISEI. Il permet à des utilisateurs d'échanger des messages entièrement chiffrés grâce à l'algorithme RSA, en combinant :

- **C** pour les opérations cryptographiques (génération de clés, chiffrement/déchiffrement)
- **Java** pour l'interface utilisateur en ligne de commande et la logique applicative
- **MySQL** pour la persistance des données (utilisateurs et messages chiffrés)

## ✨ Fonctionnalités

### Gestion des utilisateurs

- ✅ Création de compte avec génération automatique de paires de clés RSA
- ✅ Connexion/Déconnexion
- ✅ Liste des utilisateurs enregistrés

### Messagerie sécurisée

- 🔒 Chiffrement RSA de bout en bout
- 📨 Envoi de messages chiffrés entre utilisateurs
- 📬 Lecture des messages reçus avec déchiffrement automatique
- 👁️ Statut de lecture (lu/non lu)
- 📊 Statistiques personnelles (messages reçus, taux de lecture)

### Sécurité

- 🔑 Génération de clés RSA publique/privée pour chaque utilisateur
- 🛡️ Chiffrement caractère par caractère pour éviter les limitations RSA
- 💾 Stockage sécurisé des messages chiffrés en base de données
- 🚫 Protection contre les injections SQL (PreparedStatements)

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    ARCHITECTURE DU SYSTÈME                   │
└─────────────────────────────────────────────────────────────┘

┌──────────────────┐         ┌──────────────────┐
│                  │         │                  │
│  Interface Java  │◄───────►│   Module C RSA   │
│   (Terminal)     │  P.B    │   (Crypto)       │
│                  │         │                  │
└────────┬─────────┘         └──────────────────┘
         │                            │
         │ JDBC                       │ Fichiers
         │                            │ clés RSA
         ▼                            ▼
┌──────────────────┐         ┌──────────────────┐
│                  │         │                  │
│  Base MySQL      │         │  Répertoire      │
│  - Utilisateurs  │         │  keys/           │
│  - Messages      │         │  - user_public   │
│                  │         │  - user_private  │
└──────────────────┘         └──────────────────┘

P.B = ProcessBuilder (Communication inter-processus)
```

### Flux de chiffrement d'un message

```
[Utilisateur A] ──► [Java] ──► [Module C] ──► [Chiffrement RSA] 
                                                      │
                                                      ▼
                                              Clé publique B
                                                      │
                                                      ▼
                              [MySQL] ◄──── Message chiffré
```

### Flux de déchiffrement d'un message

```
[MySQL] ──► Message chiffré ──► [Java] ──► [Module C] ──► [Déchiffrement RSA]
                                                                    │
                                                                    ▼
                                                            Clé privée B
                                                                    │
                                                                    ▼
                                                            [Utilisateur B]
```

## 🛠️ Technologies utilisées

| Technologie | Version | Utilisation |
|------------|---------|-------------|
| **Java** | 17+ | Interface utilisateur, logique applicative |
| **C** | C11+ | Module cryptographique RSA |
| **MySQL** | 8.0+ | Base de données |
| **JDBC** | 8.4.0 | Connecteur Java-MySQL |
| **GCC** | - | Compilation du code C |

### Bibliothèques Java

- java.sql.* - Connexion JDBC
- java.io.* - Gestion des fichiers et processus
- java.util.* - Collections et utilitaires

### Bibliothèques C

- stdio.h, stdlib.h - Entrées/sorties et allocation mémoire
- stdbool.h - Support des booléens
- time.h - Génération de nombres aléatoires
- string.h - Manipulation de chaînes

## 📦 Prérequis

Avant de commencer, assurez-vous d'avoir installé :

- [Java JDK 17+](https://www.oracle.com/java/technologies/downloads/)
- [GCC Compiler](https://gcc.gnu.org/)
- [MySQL Server 8.0+](https://dev.mysql.com/downloads/mysql/)
- [MySQL Connector/J](https://dev.mysql.com/downloads/connector/j/) (JDBC Driver)

## 🚀 Installation

### 1. Cloner le dépôt

```bash
git clone https://github.com/votre-username/messagerie-rsa.git
cd messagerie-rsa
```

### 2. Configurer la base de données

```sql
-- Créer la base de données
CREATE DATABASE MessageSecurise;
USE MessageSecurise;

-- Table des utilisateurs
CREATE TABLE Utilisateurs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom_utilisateur VARCHAR(50) UNIQUE NOT NULL,
    cle_publique VARCHAR(255) NOT NULL,
    cle_privee VARCHAR(255) NOT NULL,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des messages
CREATE TABLE Messages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    expediteur_id INT NOT NULL,
    destinataire_id INT NOT NULL,
    contenu_chiffre TEXT NOT NULL,
    date_envoi TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    lu BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (expediteur_id) REFERENCES Utilisateurs(id),
    FOREIGN KEY (destinataire_id) REFERENCES Utilisateurs(id)
);
```

### 3. Configurer les identifiants MySQL

Modifiez le fichier database/MaConnexionJDBC.java :

```java
private static final String USER = "votre_utilisateur";
private static final String PASSWORD = "votre_mot_de_passe";
private static final String URL = "jdbc:mysql://localhost:3306/MessageSecurise?serverTimezone=UTC&useSSL=false";
```

### 4. Compiler le module C

```bash
gcc -o crypto crypto.c -lm
```

### 5. Compiler le projet Java

```bash
# Créer les répertoires nécessaires
mkdir -p bin lib keys temp

# Télécharger le connecteur MySQL (si pas déjà fait)
# Placer mysql-connector-j-8.4.0.jar dans le dossier lib/

# Compiler
javac -d bin -cp "lib/mysql-connector-j-8.4.0.jar" \
    database/*.java \
    models/*.java \
    services/*.java \
    exceptions/*.java \
    app/*.java
```

### 6. Rendre le script exécutable (Linux/Mac)

```bash
chmod +x run.sh
```

## 💻 Utilisation

### Lancer l'application

**Linux/Mac :**

```bash
./run.sh
```

**Windows :**

```bash
java -cp "bin;lib/mysql-connector-j-8.4.0.jar" app.Main
```

### Menu principal

```
╔════════════════════════════════════════╗
║  SYSTÈME DE MESSAGERIE SÉCURISÉE RSA   ║
╚════════════════════════════════════════╝

1. Créer un compte
2. Se connecter
3. Menu utilisateur
4. Quitter

➤ Votre choix:
```

### Créer un compte

1. Choisir l'option **1**
2. Entrer un nom d'utilisateur unique
3. Les clés RSA sont générées automatiquement

### Envoyer un message chiffré

1. Se connecter (option **2**)
2. Accéder au menu utilisateur (option **3**)
3. Choisir **Envoyer un message chiffré**
4. Sélectionner un destinataire
5. Taper le message → Il sera chiffré automatiquement

### Lire ses messages

1. Dans le menu utilisateur, choisir **Lire mes messages**
2. Sélectionner le numéro du message
3. Le message est déchiffré automatiquement avec votre clé privée

## 📁 Structure du projet

```
messagerie-rsa/
│
├── app/
│   └── Main.java                    # Point d'entrée de l'application
│
├── database/
│   ├── ConnexionJDBC.java          # Interface de connexion
│   └── MaConnexionJDBC.java        # Implémentation MySQL
│
├── models/
│   ├── Utilisateur.java            # Modèle utilisateur
│   └── Message.java                # Modèle message
│
├── services/
│   ├── DatabaseManager.java        # Gestionnaire de BDD (CRUD)
│   └── CryptoManager.java          # Gestionnaire cryptographie
│
├── exceptions/
│   ├── CryptoException.java        # Exception crypto
│   └── UtilisateurNonTrouveException.java
│
├── crypto.c                        # Module cryptographique RSA
├── crypto.h                        # En-têtes du module C
│
├── lib/
│   └── mysql-connector-j-8.4.0.jar # Driver JDBC
│
├── keys/                           # Clés RSA générées
│   ├── alice_public.key
│   ├── alice_private.key
│   └── ...
│
├── temp/                           # Fichiers temporaires
│
├── run.sh                          # Script de lancement
└── README.md                       # Documentation
```

## 🔒 Sécurité

### Implémentation RSA

- **Génération de clés** : Nombres premiers aléatoires entre 50-150 et 150-250
- **Exposant public** : e = 65537 (valeur standard)
- **Chiffrement** : Chaque caractère est chiffré individuellement pour éviter les dépassements
- **Formule** : C = M^e mod n (chiffrement) et M = C^d mod n (déchiffrement)

### Bonnes pratiques appliquées

✅ **Séparation des clés** : Chaque utilisateur possède sa propre paire de clés  
✅ **PreparedStatements** : Protection contre les injections SQL  
✅ **Validation des entrées** : Vérification des données utilisateur  
✅ **Gestion mémoire** : Libération correcte des ressources (C et Java)  
✅ **Fichiers temporaires** : Suppression automatique après usage

### Limitations actuelles

⚠️ Clés RSA de petite taille (démo/éducatif)  
⚠️ Pas de gestion des certificats  
⚠️ Pas de vérification d'intégrité (hash)  
⚠️ Communication non sécurisée entre Java et C (fichiers temporaires)

## 🚀 Améliorations futures

### Court terme

- [ ] Interface graphique JavaFX
- [ ] Chiffrement de fichiers (pièces jointes)
- [ ] Messagerie de groupe
- [ ] Notification en temps réel

### Moyen terme

- [ ] Augmenter la taille des clés RSA (2048 bits minimum)
- [ ] Ajouter la signature numérique des messages
- [ ] Implémenter JNI pour remplacer ProcessBuilder
- [ ] Système de récupération de mot de passe

### Long terme

- [ ] Application web (Spring Boot + React)
- [ ] Application mobile (Android/iOS)
- [ ] Chiffrement hybride (RSA + AES)
- [ ] Authentification à deux facteurs (2FA)

## 🤝 Contribution

Les contributions sont les bienvenues ! Pour contribuer :

1. Forkez le projet
2. Créez une branche pour votre fonctionnalité (`git checkout -b feature/NouvelleFonctionnalite`)
3. Committez vos changements (`git commit -m 'Ajout d'une nouvelle fonctionnalité'`)
4. Poussez vers la branche (`git push origin feature/NouvelleFonctionnalite`)
5. Ouvrez une Pull Request

### Guidelines

- Commentez votre code
- Respectez les conventions de nommage
- Testez vos modifications
- Mettez à jour la documentation si nécessaire

## 👨‍💻 Auteur

**AYMEN RAKI**  
L3 ISEI

📧 [aymen.raki.cs@gmail](mailto:aymen.raki.cs@gmail.com)  
🔗 [LinkedIn](https://linkedin.com/in/aymen-raki)  
🐙 [GitHub](https://github.com/aymenrakics)

## 📄 License

Ce projet est sous licence MIT - voir le fichier [LICENSE](LICENSE) pour plus de détails.

---

## 📚 Ressources supplémentaires

- [Documentation RSA](https://en.wikipedia.org/wiki/RSA_(cryptosystem))
- [JDBC Tutorial](https://docs.oracle.com/javase/tutorial/jdbc/)
- [MySQL Documentation](https://dev.mysql.com/doc/)

---

**Note** : Ce projet est à but éducatif. Pour une utilisation en production, des mesures de sécurité supplémentaires sont nécessaires (clés plus longues, certificats, chiffrement hybride, etc.).
