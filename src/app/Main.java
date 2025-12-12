package app;

import models.Utilisateur;
import models.Message;
import services.DatabaseManager;
import services.CryptoManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import exceptions.UtilisateurNonTrouveException;
import exceptions.CryptoException;

public class Main {
    
    // Gestionnaire de base de données
    private static DatabaseManager dbManager;
    // Gestionnaire de chiffrement/déchiffrement RSA
    private static CryptoManager cryptoManager;
    // Scanner pour lire les entrées utilisateur
    private static Scanner scanner;
    // Stocke l'utilisateur actuellement connecté
    private static Utilisateur utilisateurCourant;
    
    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        dbManager = new DatabaseManager();
        cryptoManager = new CryptoManager();
        
        try {            
            // Ouverture de la connexion JDBC
            dbManager.initialiser();
            
            boolean continuer = true;
            while (continuer) {
                // Affiche le menu principal
                afficherMenuPrincipal();
                int choix = lireChoix();
                
                switch (choix) {
                    case 1:
                        // Création d'un nouveau compte utilisateur
                        creerCompte();
                        break;
                    case 2:
                        // Connexion d'un utilisateur existant
                        seConnecter();
                        break;
                    case 3:
                        // Menu réservé aux utilisateurs connectés
                        if (utilisateurCourant != null) {
                            menuUtilisateur();
                        } else {
                            System.out.println("⚠ Vous devez être connecté!");
                        }
                        break;
                    case 4:
                        // Quitter l'application
                        continuer = false;
                        System.out.println("Au revoir!");
                        break;
                    default:
                        System.out.println("⚠ Choix invalide!");
                }
            }
            
        } catch (SQLException e) {            
            // Capture des erreurs liées à JDBC
            System.err.println("Erreur de connexion à la base de données: " + e.getMessage());
            e.printStackTrace();
        } finally {            
            try {
                // Fermeture propre de la connexion
                dbManager.fermer();
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture: " + e.getMessage());
            }
            scanner.close();
        }
    }
        
    private static void afficherMenuPrincipal() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║  SYSTÈME DE MESSAGERIE SÉCURISÉE RSA     ║");
        System.out.println("╚════════════════════════════════════════  ╝");
        
        // Affiche le nom de l'utilisateur connecté si applicable
        if (utilisateurCourant != null) {
            System.out.println("Connecté: " + utilisateurCourant.getNomUtilisateur());
        }
        
        // Choix disponibles
        System.out.println("\n1. Créer un compte");
        System.out.println("2. Se connecter");
        System.out.println("3. Menu utilisateur");
        System.out.println("4. Quitter");
        System.out.print("\n➤ Votre choix: ");
    }
        
    private static void creerCompte() {
        // Interface de création
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║       CRÉATION DE COMPTE              ║");
        System.out.println("╚════════════════════════════════════════╝");
        
        System.out.print("Nom d'utilisateur: ");
        String nomUtilisateur = scanner.nextLine();
        
        // Vérification simple
        if (nomUtilisateur.trim().isEmpty()) {
            System.out.println("Le nom ne peut pas être vide!");
            return;
        }
        
        try {            
            // Vérifie si l'utilisateur existe déjà
            if (dbManager.getUtilisateur(nomUtilisateur) != null) {
                System.out.println("Cet utilisateur existe déjà!");
                return;
            }
            
            // Génération d'une paire de clés RSA
            System.out.println("\nGénération des clés RSA...");
            if (!cryptoManager.genererCles(nomUtilisateur)) {
                System.out.println("Erreur lors de la génération des clés!");
                return;
            }
            
            // Récupère le chemin vers les fichiers clés
            String clePublique = cryptoManager.getCheminClePublique(nomUtilisateur);
            String clePrivee = cryptoManager.getCheminClePrivee(nomUtilisateur);
                        
            // Création du modèle utilisateur
            Utilisateur nouveauUser = new Utilisateur(nomUtilisateur, clePublique, clePrivee);
            
            // Insertion dans la base
            if (dbManager.ajouterUtilisateur(nouveauUser)) {
                System.out.println("Compte créé avec succès!");
                System.out.println("Clés RSA générées et stockées.");
            } else {
                System.out.println("Erreur lors de la création du compte.");
            }
            
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }
        
    private static void seConnecter() {
        // Interface
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║           CONNEXION                   ║");
        System.out.println("╚════════════════════════════════════════╝");
        
        System.out.print("Nom d'utilisateur: ");
        String nomUtilisateur = scanner.nextLine();
        
        try {
            // Récupère l'utilisateur dans la BDD
            Utilisateur user = dbManager.getUtilisateur(nomUtilisateur);
            
            if (user != null) {
                utilisateurCourant = user;
                System.out.println("Connexion réussie!");
                System.out.println("Bienvenue " + user.getNomUtilisateur() + "!");
            } else {
                throw new UtilisateurNonTrouveException("Utilisateur '" + nomUtilisateur + "' introuvable!");
            }
            
        } catch (UtilisateurNonTrouveException e) {
            System.out.println("" + e.getMessage());
        }
    }
        
    private static void menuUtilisateur() {
        // Permet de naviguer dans un sous-menu
        boolean continuer = true;
        
        while (continuer) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║         MENU UTILISATEUR              ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.println("1. Envoyer un message chiffré");
            System.out.println("2. Lire mes messages");
            System.out.println("3. Lister les utilisateurs");
            System.out.println("4. Statistiques");
            System.out.println("5. Se déconnecter");
            System.out.print("\n➤ Votre choix: ");
            
            int choix = lireChoix();
            
            switch (choix) {
                case 1:
                    envoyerMessage();
                    break;
                case 2:
                    lireMessages();
                    break;
                case 3:
                    listerUtilisateurs();
                    break;
                case 4:
                    afficherStatistiques();
                    break;
                case 5:
                    // Réinitialise la session
                    utilisateurCourant = null;
                    System.out.println("Déconnexion réussie.");
                    continuer = false;
                    break;
                default:
                    System.out.println("Choix invalide!");
            }
        }
    }
    
    private static void envoyerMessage() {
        // Interface
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║       ENVOYER UN MESSAGE              ║");
        System.out.println("╚════════════════════════════════════════╝");
        
        try {
            // Liste des utilisateurs SQL
            ArrayList<Utilisateur> utilisateurs = dbManager.listerUtilisateurs();
            
            // Affichage
            System.out.println("\nUtilisateurs disponibles:");
            int compteur = 1;
            for (Utilisateur u : utilisateurs) {
                if (u.getId() != utilisateurCourant.getId()) {
                    System.out.println("  " + compteur + ". " + u.getNomUtilisateur());
                    compteur++;
                }
            }
            
            // Nom du destinataire
            System.out.print("\n➤ Nom du destinataire: ");
            String nomDestinataire = scanner.nextLine();
            
            Utilisateur destinataire = dbManager.getUtilisateur(nomDestinataire);
            
            if (destinataire == null) {
                throw new UtilisateurNonTrouveException("Destinataire '" + nomDestinataire + "' introuvable!");
            }
            
            // Empêche l’auto-envoi
            if (destinataire.getId() == utilisateurCourant.getId()) {
                System.out.println("Vous ne pouvez pas vous envoyer de message!");
                return;
            }
            
            System.out.print("➤ Message: ");
            String messageTexte = scanner.nextLine();
            
            if (messageTexte.trim().isEmpty()) {
                System.out.println("Le message ne peut pas être vide!");
                return;
            }
            
            // Chiffrement du texte via RSA
            System.out.println("\nChiffrement du message avec RSA...");
            String messageChiffre = cryptoManager.chiffrerMessage(nomDestinataire, messageTexte);
            
            if (messageChiffre == null) {
                System.out.println("Erreur lors du chiffrement!");
                return;
            }
            
            // Création du modèle "Message"
            Message message = new Message(
                utilisateurCourant.getId(),
                destinataire.getId(),
                messageChiffre
            );
            
            // Envoi vers la base
            if (dbManager.envoyerMessage(message)) {
                System.out.println("Message envoyé et chiffré avec succès!");
                System.out.println("Destinataire: " + destinataire.getNomUtilisateur());
            } else {
                System.out.println("Erreur lors de l'envoi du message.");
            }
            
        } catch (UtilisateurNonTrouveException e) {
            System.out.println(" " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }
        
    private static void lireMessages() {
        // Interface
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║         MES MESSAGES REÇUS            ║");
        System.out.println("╚════════════════════════════════════════╝");
        
        // Récupère les messages de la BDD
        ArrayList<Message> messages = dbManager.getMessagesRecus(utilisateurCourant.getId());
        
        if (messages.isEmpty()) {
            System.out.println("Aucun message.");
            return;
        }
        
        // Compte les messages non lus
        int nonLus = 0;
        for (Message msg : messages) {
            if (!msg.isLu()) nonLus++;
        }
        
        // Affichage des messages avec indicateurs
        System.out.println("\nVous avez " + messages.size() + " message(s) (" + nonLus + " non lu(s))\n");
        
        for (int i = 0; i < messages.size(); i++) {
            Message msg = messages.get(i);
            
            // Récupération du nom de l'expéditeur
            String nomExpediteur = "Inconnu";
            for (Utilisateur u : dbManager.listerUtilisateurs()) {
                if (u.getId() == msg.getExpediteurId()) {
                    nomExpediteur = u.getNomUtilisateur();
                    break;
                }
            }
            
            // ● = non lu | ✓ = lu
            String statut = msg.isLu() ? "✓" : "●";
            System.out.printf("%d. %s De: %s - %s\n", 
                (i + 1), statut, nomExpediteur, msg.getDateEnvoi());
        }
        
        System.out.print("\n➤ Numéro du message à lire (0 pour retour): ");
        int choix = lireChoix();
        
        if (choix > 0 && choix <= messages.size()) {
            Message msg = messages.get(choix - 1);
            
            try {
                System.out.println("\nDéchiffrement du message avec votre clé privée...");
                
                // Déchiffrement RSA
                String messageDechiffre = cryptoManager.dechiffrerMessage(
                    utilisateurCourant.getNomUtilisateur(),
                    msg.getContenuChiffre()
                );
                
                if (messageDechiffre != null) {
                    // Affichage du texte déchiffré
                    System.out.println("\n┌─────────────────────────────────────┐");
                    System.out.println("│  MESSAGE DÉCHIFFRÉ                │");
                    System.out.println("└─────────────────────────────────────┘");
                    System.out.println(messageDechiffre);
                    System.out.println();
                    
                    // Passage à "lu" côté BDD
                    dbManager.marquerCommeLu(msg.getId());
                } else {
                    System.out.println("Erreur lors du déchiffrement!");
                }
                
            } catch (Exception e) {
                System.err.println("Erreur: " + e.getMessage());
            }
        }
    }
        
    private static void listerUtilisateurs() {
        // Interface
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║     LISTE DES UTILISATEURS            ║");
        System.out.println("╚════════════════════════════════════════╝");
        
        // Récupération SQL
        ArrayList<Utilisateur> utilisateurs = dbManager.listerUtilisateurs();
        
        if (utilisateurs.isEmpty()) {
            System.out.println("Aucun utilisateur.");
            return;
        }
        
        System.out.println("\nTotal: " + utilisateurs.size() + " utilisateur(s)\n");
        
        // Affichage de chaque utilisateur
        for (int i = 0; i < utilisateurs.size(); i++) {
            Utilisateur u = utilisateurs.get(i);
            
            // Marque l'utilisateur courant
            String marqueur = u.getId() == utilisateurCourant.getId() ? " (vous)" : "";
            System.out.println((i + 1) + ". " + u.getNomUtilisateur() + marqueur);
        }
    }
        
    private static void afficherStatistiques() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║         STATISTIQUES                  ║");
        System.out.println("╚════════════════════════════════════════╝");
        
        // Récupère les messages pour calcul des stats
        ArrayList<Message> messagesRecus = dbManager.getMessagesRecus(utilisateurCourant.getId());
                
        int totalRecus = messagesRecus.size();
        int lus = 0;
        int nonLus = 0;
        
        // Comptage des états
        for (Message msg : messagesRecus) {
            if (msg.isLu()) lus++;
            else nonLus++;
        }
        
        // Affichage global
        System.out.println("\nVos statistiques:");
        System.out.println("  • Messages reçus: " + totalRecus);
        System.out.println("  • Messages lus: " + lus);
        System.out.println("  • Messages non lus: " + nonLus);
        
        // Calcul du taux de lecture
        if (totalRecus > 0) {
            double tauxLecture = (lus * 100.0) / totalRecus;
            System.out.printf("  • Taux de lecture: %.1f%%\n", tauxLecture);
        }
    }
        
    private static int lireChoix() {
        try {
            // Lecture d'un entier
            String input = scanner.nextLine();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
