package models;

/**
 * Classe modèle représentant un utilisateur du système
 * Stocke les informations d'identification et les clés de chiffrement RSA
 */
public class Utilisateur {
	private int id;                    // Identifiant unique
	private String nomUtilisateur;     // Nom d'utilisateur unique
	private String clePublique;        // Chemin vers la clé publique RSA
	private String clePrivee;          // Chemin vers la clé privée RSA

	/**
	 * Constructeur complet avec ID
	 * Utilisé lors de la récupération depuis la base de données
	 */
	public Utilisateur(int id, String nomUtilisateur, String clePublique, String clePrivee) {
		this.setId(id);
		this.setNomUtilisateur(nomUtilisateur);
		this.setClePublique(clePublique);
		this.setClePrivee(clePrivee);
	}

	/**
	 * Constructeur sans ID pour les nouveaux utilisateurs
	 * L'ID sera auto-généré lors de l'insertion en base
	 */
	public Utilisateur(String nomUtilisateur, String clePublique, String clePrivee) {
		this.setNomUtilisateur(nomUtilisateur);
		this.setClePublique(clePublique);
		this.setClePrivee(clePrivee);
	}

	// GETTERS ET SETTERS
	
	public String getClePrivee() {
		return this.clePrivee;
	}

	public void setClePrivee(String clePrivee) {
		this.clePrivee = clePrivee;
	}

	public String getClePublique() {
		return this.clePublique;
	}

	public void setClePublique(String clePublique) {
		this.clePublique = clePublique;
	}

	public String getNomUtilisateur() {
		return this.nomUtilisateur;
	}

	public void setNomUtilisateur(String nomUtilisateur) {
		this.nomUtilisateur = nomUtilisateur;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Représentation textuelle SANS la clé privée
	 * La clé privée est sensible et ne doit jamais être affichée
	 */
	@Override
	public String toString() {
		return "Utilisateur {" +
	"id = " + id +
	", nomUtilisateur = '" + nomUtilisateur + '\'' +
	", clePublique = '" + clePublique + '\'' +
	'}';
	}
}
