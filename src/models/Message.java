package models;

import java.sql.Timestamp;

/**
 * Classe modèle représentant un message chiffré dans le système
 * Contient toutes les informations nécessaires pour identifier et gérer un message
 */
public class Message {
	private int id;                      // Identifiant unique du message
	private int expediteurId;            // ID de l'utilisateur qui envoie
	private int destinataireId;          // ID de l'utilisateur qui reçoit
	private String contenuChiffre;       // Message chiffré avec RSA
	private Timestamp dateEnvoi;         // Date et heure d'envoi
	private Boolean lu;                  // Statut de lecture du message

	/**
	 * Constructeur complet avec tous les champs
	 * Utilisé lors de la récupération depuis la base de données
	 */
	public Message(int id, int expediteurId, int destinataireId, String contenuChiffre, Timestamp dateEnvoi, Boolean lu) {
		this.setId(id);
		this.setExpediteurId(expediteurId);
		this.setDestinataireId(destinataireId);
		this.setContenuChiffre(contenuChiffre);
		this.dateEnvoi = dateEnvoi;
		this.setLu(lu);
	}

	/**
	 * Constructeur sans ID pour les nouveaux messages
	 * L'ID sera généré automatiquement par la base de données
	 */
	public Message(int expediteurId, int destinataireId, String contenuChiffre, Timestamp dateEnvoi, Boolean lu) {
		this.setExpediteurId(expediteurId);
		this.setDestinataireId(destinataireId);
		this.setContenuChiffre(contenuChiffre);
		this.dateEnvoi = dateEnvoi;
		this.setLu(lu);
	}

	/**
	 * Constructeur minimal pour créer un message rapidement
	 * Les valeurs par défaut seront ajoutées lors de l'insertion
	 */
	public Message(int expediteurId, int destinataireId, String contenuChiffre) {
		this.setExpediteurId(expediteurId);
		this.setDestinataireId(destinataireId);
		this.setContenuChiffre(contenuChiffre);
	}

	// GETTERS ET SETTERS avec encapsulation des données
	
	public Boolean getLu() {
		return this.lu;
	}

	public void setLu(Boolean lu) {
		this.lu = lu;
	}

	public String getContenuChiffre() {
		return this.contenuChiffre;
	}

	public void setContenuChiffre(String contenuChiffre) {
		this.contenuChiffre = contenuChiffre;
	}

	public int getDestinataireId() {
		return this.destinataireId;
	}

	public void setDestinataireId(int destinataireId) {
		this.destinataireId = destinataireId;
	}

	public int getExpediteurId() {
		return this.expediteurId;
	}

	public void setExpediteurId(int expediteurId) {
		this.expediteurId = expediteurId;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Timestamp getDateEnvoi() {
		return this.dateEnvoi;
	}

	public void setDateEnvoi(Timestamp dateEnvoi) {
		this.dateEnvoi = dateEnvoi;
	}

	/**
	 * Représentation textuelle du message SANS le contenu sensible
	 * Le contenu chiffré n'est pas affiché pour des raisons de sécurité
	 */
	@Override
    public String toString() {
        return "Message { " +
                "id = " + id +
                ", expediteur = " + expediteurId +
                ", destinataire = " + destinataireId +
                ", dateEnvoi = " + dateEnvoi +
                ", lu = " + lu +
                '}';
    }

	/**
	 * Méthode utilitaire pour vérifier si le message a été lu
	 */
	public boolean isLu() {
		return this.lu == true;
	}
}
