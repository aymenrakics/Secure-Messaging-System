package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Implémentation concrète de l'interface ConnexionJDBC pour MySQL
 * Gère la connexion à la base de données MessageSecurise
 */
public class MaConnexionJDBC implements ConnexionJDBC {
	// Objet Connection pour maintenir la connexion active
	private Connection connexion;
	
	// Paramètres de connexion à la base de données
	private static final String USER = "root";
	private static final String PASSWORD = "ton_mot_de_passe";
	private static final String URL = "jdbc:mysql://localhost:3306/MessageSecurise?serverTimezone=UTC&useSSL=false";
    
	/**
	 * Constructeur initialisant la connexion à null
	 */
    public MaConnexionJDBC() {
    	this.connexion = null;
    }
    
	/**
	 * Établit la connexion à la base de données MySQL
	 * Charge d'abord le driver JDBC puis crée la connexion
	 */
	@Override
	public void openConnection() throws SQLException {
		try {
			// Chargement du driver MySQL JDBC
			Class.forName("com.mysql.cj.jdbc.Driver");
            
			// Création de la connexion avec les paramètres définis
            this.connexion = DriverManager.getConnection(URL, USER, PASSWORD); 
            System.out.println("Connexion à la base de données établie avec succès.");
		}
		catch (ClassNotFoundException e) {
			// Gestion de l'erreur si le driver n'est pas trouvé
            System.err.println("Driver MySQL introuvable: " + e.getMessage());
            throw new SQLException("Driver introuvable", e);
		}
	}
	
	/**
	 * Retourne l'objet Connection pour permettre l'exécution de requêtes
	 */
	@Override
	public Connection getConnection() {
		return this.connexion;
	}
	
	/**
	 * Ferme la connexion si elle est ouverte
	 * Vérifie d'abord que la connexion existe et n'est pas déjà fermée
	 */
	@Override
	public void closeConnection() throws SQLException {
		if (this.connexion != null && !this.connexion.isClosed()) {
		    this.connexion.close();
		}
	}
}
