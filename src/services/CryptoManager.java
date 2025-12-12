package services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Gestionnaire des opérations cryptographiques RSA
 */
public class CryptoManager {
	// Chemin vers l'exécutable de chiffrement
	private static final String CRYPTO_EXECUTABLE = "./crypto";
	// Répertoire de stockage des clés RSA
    private static final String KEYS_DIR = "keys/";
    // Répertoire temporaire pour les fichiers de chiffrement
    private static final String TEMP_DIR = "temp/";
    
    /**
     * Constructeur qui initialise les répertoires nécessaires
     */
    public CryptoManager() {
      	creerRepertoires();
    }
    
    /**
     * Crée les répertoires keys/ et temp/ s'ils n'existent pas
     */
    public void creerRepertoires() {
    	new File(KEYS_DIR).mkdir();
    	new File(TEMP_DIR).mkdir();
    }
    
    /**
     * Génère une paire de clés RSA (publique et privée) pour un utilisateur
     */
    public Boolean genererCles(String nomUtilisateur) {
    	// Construction des chemins des fichiers de clés
    	String clePublique = KEYS_DIR + nomUtilisateur + "_public.key";
        String clePrivee = KEYS_DIR + nomUtilisateur + "_private.key";
        
        try {
        	// Création du processus pour exécuter la commande de génération
        	ProcessBuilder pb = new ProcessBuilder(
        			CRYPTO_EXECUTABLE,
        			"generer",
        			clePublique,
        			clePrivee
        			);
        	// Redirige les erreurs vers la sortie standard
        	pb.redirectErrorStream(true);
        	Process process = pb.start();
        	
        	// Lecture de la sortie du processus
        	BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
                );
        	
        	String ligne;
        	// Affiche chaque ligne de sortie du processus
            while ((ligne = reader.readLine()) != null) {
                System.out.println(ligne);
            }
            
            // Attend la fin du processus et récupère le code de retour
            int exitCode = process.waitFor();
            reader.close();
            // Code 0 = succès
            return exitCode == 0;
        }
        catch (IOException e) {
            System.err.println("Erreur I/O lors de la génération des clés: " + e.getMessage());
            return false;
        } 
        catch (InterruptedException e) {
            System.err.println("Processus interrompu: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Chiffre un message avec la clé publique du destinataire
     */
    public String chiffrerMessage(String nomDestinataire, String message) {
        String clePublique = KEYS_DIR + nomDestinataire + "_public.key";
        // Nom unique basé sur le timestamp pour éviter les collisions
        String fichierChiffre = TEMP_DIR + "msg_" + System.currentTimeMillis() + ".enc";
        
        try {
            // Commande de chiffrement avec clé publique, message et fichier de sortie
            ProcessBuilder pb = new ProcessBuilder(
                CRYPTO_EXECUTABLE,
                "chiffrer",
                clePublique,
                message,
                fichierChiffre
            );
            
            pb.redirectErrorStream(true);
            Process process = pb.start();
                        
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
            );
            
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                System.out.println(ligne);
            }
            
            int exitCode = process.waitFor();
            reader.close();
            
            // Si le chiffrement a réussi, lire le fichier chiffré
            if (exitCode == 0) {                
                BufferedReader fileReader = new BufferedReader(
                    new FileReader(fichierChiffre)
                );
                
                // Construction du contenu chiffré
                StringBuilder contenu = new StringBuilder();
                String line;
                while ((line = fileReader.readLine()) != null) {
                    contenu.append(line).append("\n");
                }
                fileReader.close();
                
                // Suppression du fichier temporaire
                new File(fichierChiffre).delete();
                
                return contenu.toString().trim();
            }
            
        } catch (IOException e) {
            System.err.println("Erreur lors du chiffrement: " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Processus interrompu: " + e.getMessage());
        }
        
        return null;
    }
        
    /**
     * Déchiffre un message avec la clé privée de l'utilisateur
     */
    public String dechiffrerMessage(String nomUtilisateur, String contenuChiffre) {
        String clePrivee = KEYS_DIR + nomUtilisateur + "_private.key";
        // Fichiers temporaires pour le chiffré et le déchiffré
        String fichierChiffre = TEMP_DIR + "msg_" + System.currentTimeMillis() + ".enc";
        String fichierDechiffre = TEMP_DIR + "msg_" + System.currentTimeMillis() + ".dec";
        
        try {            
            // Écriture du contenu chiffré dans un fichier temporaire
            BufferedWriter writer = new BufferedWriter(new FileWriter(fichierChiffre));
            writer.write(contenuChiffre);
            writer.close();
            
            // Commande de déchiffrement
            ProcessBuilder pb = new ProcessBuilder(
                CRYPTO_EXECUTABLE,
                "dechiffrer",
                clePrivee,
                fichierChiffre,
                fichierDechiffre
            );
            
            pb.redirectErrorStream(true);
            Process process = pb.start();
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
            );
            
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                System.out.println(ligne);
            }
            
            int exitCode = process.waitFor();
            reader.close();
            
            // Si le déchiffrement a réussi, lire le fichier déchiffré
            if (exitCode == 0) {
                BufferedReader fileReader = new BufferedReader(
                    new FileReader(fichierDechiffre)
                );
                
                StringBuilder contenu = new StringBuilder();
                String line;
                while ((line = fileReader.readLine()) != null) {
                    contenu.append(line);
                }
                fileReader.close();
                
                // Nettoyage des fichiers temporaires
                new File(fichierChiffre).delete();
                new File(fichierDechiffre).delete();
                
                return contenu.toString();
            }
            
        } catch (IOException e) {
            System.err.println("Erreur lors du déchiffrement: " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Processus interrompu: " + e.getMessage());
        }
        
        return null;
    }
        
    /**
     * Retourne le chemin complet vers la clé publique d'un utilisateur
     */
    public String getCheminClePublique(String nomUtilisateur) {
        return KEYS_DIR + nomUtilisateur + "_public.key";
    }
    
    /**
     * Retourne le chemin complet vers la clé privée d'un utilisateur
     */
    public String getCheminClePrivee(String nomUtilisateur) {
        return KEYS_DIR + nomUtilisateur + "_private.key";
    }
        
    /**
     * Vérifie si les deux clés (publique et privée) existent pour un utilisateur
     */
    public boolean cleesExistent(String nomUtilisateur) {
        File clePublique = new File(getCheminClePublique(nomUtilisateur));
        File clePrivee = new File(getCheminClePrivee(nomUtilisateur));
        return clePublique.exists() && clePrivee.exists();
    }
}
