package services;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import database.ConnexionJDBC;
import database.MaConnexionJDBC;
import models.Message;
import models.Utilisateur;

/**
 * Classe responsable de la gestion des opérations liées à la base de données.
 * Elle encapsule toutes les interactions SQL (CRUD) pour les utilisateurs et les messages.
 */
public class DatabaseManager {
	
    // Objet de connexion à la base JDBC (interface abstraite)
	private ConnexionJDBC connexionJDBC;
	
    /**
     * Constructeur : instancie une connexion JDBC via MaConnexionJDBC.
     */
	public DatabaseManager() {
		this.connexionJDBC = new MaConnexionJDBC();
	}
	
    /**
     * Initialise la connexion à la base de données.
     */
	public void initialiser() throws SQLException {
		this.connexionJDBC.openConnection();
	}
	
    /**
     * Ajoute un nouvel utilisateur dans la base de données.
     */
	public Boolean ajouterUtilisateur(Utilisateur user) {

		String sql = "INSERT INTO Utilisateurs (nom_utilisateur, cle_publique, cle_privee) VALUES (?, ?, ?)";
		
		try {
            // Préparation de la requête
			PreparedStatement pstmt = this.connexionJDBC.getConnection().prepareStatement(sql);

            // Bind des paramètres
			pstmt.setString(1, user.getNomUtilisateur());
            pstmt.setString(2, user.getClePublique());
            pstmt.setString(3, user.getClePrivee());
            
            // Exécution INSERT
            int result = pstmt.executeUpdate();
            pstmt.close();

            // Retourne true si une ligne a été insérée
            return result > 0;
		}
		catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'utilisateur: " + e.getMessage());
            return false;
        }
	}
	
    /**
     * Récupère un utilisateur par son nom d'utilisateur.
     */
	public Utilisateur getUtilisateur(String nomUtilisateur) {

	    String sql = "SELECT * FROM Utilisateurs WHERE nom_utilisateur = ?";
	    
	    try (PreparedStatement pstmt = this.connexionJDBC.getConnection().prepareStatement(sql)) {

	        pstmt.setString(1, nomUtilisateur);

	        try (ResultSet rs = pstmt.executeQuery()) {

	            // Si un utilisateur est trouvé → on construit l'objet Utilisateur
	            if (rs.next()) {
	                return new Utilisateur(
	                    rs.getInt("id"),
	                    rs.getString("nom_utilisateur"),
	                    rs.getString("cle_publique"),
	                    rs.getString("cle_privee")
	                );
	            } else {
	                System.out.println("Utilisateur '" + nomUtilisateur + "' introuvable!");
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("Erreur lors de la récupération de l'utilisateur: " + e.getMessage());
	    }
	    
	    return null;
	}
	
    /**
     * Retourne la liste complète des utilisateurs enregistrés dans la base.
     */
	public ArrayList<Utilisateur> listerUtilisateurs() {

	    ArrayList<Utilisateur> utilisateurs = new ArrayList<>();
	    String sql = "SELECT * FROM Utilisateurs ORDER BY nom_utilisateur";

	    try (
            PreparedStatement stmt = this.connexionJDBC.getConnection().prepareStatement(sql);
	        ResultSet rs = stmt.executeQuery()
        ) {

	        while (rs.next()) {
	            utilisateurs.add(new Utilisateur(
	                rs.getInt("id"),
	                rs.getString("nom_utilisateur"),
	                rs.getString("cle_publique"),
	                rs.getString("cle_privee")
	            ));
	        }

	    } catch (SQLException e) {
	        System.err.println("Erreur lors de la récupération des utilisateurs: " + e.getMessage());
	    }

	    return utilisateurs;
	}
	
    /**
     * Insère un message chiffré dans la table Messages.
     */
	public Boolean envoyerMessage(Message message) {

		String sql = "INSERT INTO Messages (expediteur_id, destinataire_id, contenu_chiffre) VALUES (?, ?, ?)";
		
		try {
            PreparedStatement pstmt = connexionJDBC.getConnection().prepareStatement(sql);

            // Binding des valeurs dans la requête SQL
            pstmt.setInt(1, message.getExpediteurId());
            pstmt.setInt(2, message.getDestinataireId());
            pstmt.setString(3, message.getContenuChiffre());
            
            int result = pstmt.executeUpdate();
            pstmt.close();

            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'envoi du message: " + e.getMessage());
            return false;
        }

	}
	
    /**
     * Récupère tous les messages reçus par un utilisateur donné.
     */
	public ArrayList<Message> getMessagesRecus(int userId) {

        ArrayList<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM Messages WHERE destinataire_id = ? ORDER BY date_envoi DESC";
        
        try {
            PreparedStatement pstmt = connexionJDBC.getConnection().prepareStatement(sql);
            pstmt.setInt(1, userId);

            ResultSet rs = pstmt.executeQuery();
            
            // Construction des objets Message
            while (rs.next()) {
                Message msg = new Message(
                    rs.getInt("id"),
                    rs.getInt("expediteur_id"),
                    rs.getInt("destinataire_id"),
                    rs.getString("contenu_chiffre"),
                    rs.getTimestamp("date_envoi"),
                    rs.getBoolean("lu")
                );
                messages.add(msg);
            }
            
            rs.close();
            pstmt.close();
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des messages: " + e.getMessage());
        }
        
        return messages;
    }
    
    /**
     * Marque un message comme lu dans la base.
     */
    public void marquerCommeLu(int messageId) {

        String sql = "UPDATE Messages SET lu = TRUE WHERE id = ?";
        
        try {
            PreparedStatement pstmt = connexionJDBC.getConnection().prepareStatement(sql);
            pstmt.setInt(1, messageId);
            pstmt.executeUpdate();
            pstmt.close();
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du message: " + e.getMessage());
        }
    }
    
    /**
     * Ferme la connexion JDBC proprement.
     */
    public void fermer() throws SQLException {
        connexionJDBC.closeConnection();
    }

}
