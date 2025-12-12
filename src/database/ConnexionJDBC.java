package database;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Interface définissant le contrat pour la gestion des connexions JDBC
 */
public interface ConnexionJDBC {
	
	/**
	 * Ouvre une connexion à la base de données
	 */
	void openConnection() throws SQLException;
	
	/**
	 * Récupère l'objet Connection actuel pour exécuter des requêtes
	 */
	Connection getConnection();
	
	/**
	 * Ferme proprement la connexion à la base de données
	 */
	void closeConnection() throws SQLException;
}
