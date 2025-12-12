#include "crypto.h"

int pgcd(int a, int b) {    
    while (b != 0) {
        int temp = b;        
        b = a % b;           
        a = temp;            
    }
    return a;                
}

int inverse_modulaire(int e, int phi) {
    // Initialisation des variables pour l'algorithme d'Euclide étendu
    int t = 0, nouveau_t = 1;           
    int r = phi, nouveau_r = e;         
        
    while (nouveau_r != 0) {
        int quotient = r / nouveau_r;    
        
        // Mise à jour de t
        int temp_t = t;
        t = nouveau_t;
        nouveau_t = temp_t - quotient * nouveau_t;
        
        // Mise à jour de r
        int temp_r = r;
        r = nouveau_r;
        nouveau_r = temp_r - quotient * nouveau_r;
    }
    
    // Si r > 1, e et phi ne sont pas premiers entre eux
    if (r > 1) return -1;
    
    // Si t est négatif, on le ramène dans l'intervalle [0, phi[
    if (t < 0) t += phi;
    
    return t;  // t est l'inverse modulaire de e modulo phi
}

bool estPremier(int n) {
    // Cas particuliers
    if (n < 2) return false;              // 0, 1 et négatifs ne sont pas premiers
    if (n <= 3) return true;              // 2 et 3 sont premiers
    
    // Tout nombre divisible par 2 ou 3 n'est pas premier
    if (n % 2 == 0 || n % 3 == 0) return false;
    
    // Test pour tous les nombres de la forme 6k±1 jusqu'à √n
    // Car tout premier > 3 est de la forme 6k±1
    for (int i = 5; i * i < n; i += 6) {
        if (n % i == 0 || n % (i + 2) == 0) return false;
    }
    
    return true;  // Si aucun diviseur trouvé, n est premier
}

int generer_premier(int min, int max) {
    int p;
    // Génère des nombres aléatoires jusqu'à trouver un nombre premier
    do {
        p = min + rand() % (max - min);  // Nombre aléatoire dans [min, max[
    } while (!estPremier(p));             // Continue tant que p n'est pas premier
    
    return p;  // Retourne le nombre premier trouvé
}

long long exp_modulaire(long long base, long long exp, long long mod) {
    long long resultat = 1;
    base = base % mod;
    
    while (exp > 0) {
        if (exp % 2 == 1)
            resultat = (resultat * base) % mod;
        exp = exp >> 1;
        base = (base * base) % mod;
    }
    return resultat;
}

// FONCTION POUR CREER UNE STRUCTURE CLE COMPLETE
CleRSA *creer_cle(int n, int e, int p, int q, int phi, bool est_publique) {
    CleRSA *cle = (CleRSA*) malloc(sizeof(CleRSA));
    if (!cle) {
        perror("Erreur d'allocation memoire");
        return NULL;
    }
    cle->n = n;
    cle->e = e;
    cle->p = p;
    cle->q = q;
    cle->phi = phi;
    cle->est_publique = est_publique;
    return cle;
}

// FONCTION POUR VALIDER UNE CLE RSA
bool valider_cle(CleRSA *cle) {
    if (!cle) return false;
    
    // Vérifier que n = p * q si p et q sont disponibles
    if (cle->p > 0 && cle->q > 0) {
        if (cle->n != cle->p * cle->q) return false;
    }
    
    // Vérifier que phi = (p-1)(q-1) si disponible
    if (cle->p > 0 && cle->q > 0 && cle->phi > 0) {
        if (cle->phi != (cle->p - 1) * (cle->q - 1)) return false;
    }
    
    // Vérifier que e et phi sont premiers entre eux si phi est disponible
    if (cle->phi > 0 && cle->e > 0) {
        if (pgcd(cle->e, cle->phi) != 1) return false;
    }
    
    return true;
}

void liberer_cle(CleRSA *cle) {    
    if (cle) {
        free(cle);  
    }
}

void generer_cles(const char *fichier_publique, const char *fichier_prive) {
    srand((unsigned)time(NULL));

    int p = generer_premier(50, 150);
    int q = generer_premier(150, 250);
    int n = p * q;
    int phi = (p - 1) * (q - 1);

    int e = 65537;
    while (pgcd(e, phi) != 1) {
        e = 3 + rand() % (phi - 3);
    }

    int d = inverse_modulaire(e, phi);

    if (d == -1) {
        fprintf(stderr, "Erreur, impossible de calculer d\n");
        return;
    }

    // CREER LA STRUCTURE DE CLE PUBLIQUE
    CleRSA *cle_pub = creer_cle(n, e, p, q, phi, true);
    if (!cle_pub || !valider_cle(cle_pub)) {
        fprintf(stderr, "Erreur validation cle publique\n");
        liberer_cle(cle_pub);
        return;
    }

    // CREER LA STRUCTURE DE CLE PRIVEE
    CleRSA *cle_priv = creer_cle(n, d, p, q, phi, false);
    if (!cle_priv || !valider_cle(cle_priv)) {
        fprintf(stderr, "Erreur validation cle privee\n");
        liberer_cle(cle_pub);
        liberer_cle(cle_priv);
        return;
    }

    // ENREGISTER LA CLE PUBLIQUE
    FILE *fp = fopen(fichier_publique, "w");
    if (!fp) {
        perror("Erreur d'ouverture du fichier cle publique");
        liberer_cle(cle_pub);
        liberer_cle(cle_priv);
        return;
    }
    fprintf(fp, "%d\n%d\n", cle_pub->n, cle_pub->e);
    fclose(fp);

    // ENREGISTER LA CLE PRIVEE
    fp = fopen(fichier_prive, "w");
    if (!fp) {
        perror("Erreur d'ouverture du fichier cle privee");
        liberer_cle(cle_pub);
        liberer_cle(cle_priv);
        return;
    }
    fprintf(fp, "%d\n%d\n", cle_priv->n, cle_priv->e);
    fclose(fp);

    printf("Cles generees avec succes\n");
    printf("n = %d, e = %d, d = %d\n", cle_pub->n, cle_pub->e, cle_priv->e);

    liberer_cle(cle_pub);
    liberer_cle(cle_priv);
}

CleRSA *lire_cle(const char *fichier) {
    FILE *fp = fopen(fichier, "r");
    if (!fp) {
        perror("Erreur d'ouverture du fichier");
        return NULL;
    }

    int n, e;
    if (fscanf(fp, "%d\n%d\n", &n, &e) != 2) {
        fprintf(stderr, "Erreur lecture cle\n");
        fclose(fp);
        return NULL;
    }
    fclose(fp);

    // CREER UNE STRUCTURE CLE (sans p, q, phi car non stockés dans le fichier)
    CleRSA *cle = creer_cle(n, e, 0, 0, 0, true);
    return cle;
}

void chiffrer_avec_cle(CleRSA *cle, const char *message, const char *fichier_sortie) {
    if (!cle) {
        fprintf(stderr, "Erreur: cle nulle\n");
        return;
    }

    FILE *fout = fopen(fichier_sortie, "w");
    if (!fout) {
        perror("Erreur d'ouverture du fichier de sortie");
        return;
    }

    int len = strlen(message);
    for (int i = 0; i < len; i++) {
        int c = (int) message[i];
        long long chiffre = exp_modulaire(c, cle->e, cle->n);
        fprintf(fout, "%lld ", chiffre);
    }

    fclose(fout);
    printf("Message chiffre avec succes\n");
}

void chiffrer(const char *fichier_cle, const char *message, const char *fichier_sortie) {
    CleRSA *cle = lire_cle(fichier_cle);
    if (!cle) {
        fprintf(stderr, "Erreur lecture cle\n");
        return;
    }
    
    chiffrer_avec_cle(cle, message, fichier_sortie);
    liberer_cle(cle);
}

void dechiffrer_avec_cle(CleRSA *cle, const char *fichier_entree, const char *fichier_sortie) {
    if (!cle) {
        fprintf(stderr, "Erreur: cle nulle\n");
        return;
    }

    FILE *fin = fopen(fichier_entree, "r");
    if (!fin) {
        perror("Erreur d'ouverture du fichier d'entree");
        return;
    }

    FILE *fout = fopen(fichier_sortie, "w");
    if (!fout) {
        perror("Erreur d'ouverture du fichier de sortie");
        fclose(fin);
        return;
    }

    long long chiffre;
    while (fscanf(fin, "%lld", &chiffre) == 1) {
        long long dechiffre = exp_modulaire(chiffre, cle->e, cle->n);
        fprintf(fout, "%c", (char) dechiffre);
    }

    fclose(fin);
    fclose(fout);
    printf("Message dechiffre avec succes\n");
}

void dechiffrer(const char *fichier_cle, const char *fichier_entree, const char *fichier_sortie) {
    // Lecture de la clé depuis le fichier
    CleRSA *cle = lire_cle(fichier_cle);
    if (!cle) {
        fprintf(stderr, "Erreur lecture cle\n");
        return;
    }
    
    // Déchiffrement avec la structure CleRSA
    dechiffrer_avec_cle(cle, fichier_entree, fichier_sortie);
    
    // Libération de la mémoire allouée
    liberer_cle(cle);
}

int longeur(const char *s) {
    return (int) strlen(s);
}

/**
 * Fonction: main
 * Description: Point d'entrée du programme - gère les arguments en ligne de commande
 */
int main(int argc, char const *argv[]) {
    // Vérification du nombre minimal d'arguments
    if (argc < 2) {
        // Affichage du message d'aide
        printf("Usage:\n");
        printf("  %s generer <cle_publique> <cle_privee>\n", argv[0]);
        printf("  %s chiffrer <cle_publique> <message> <fichier_sortie>\n", argv[0]);
        printf("  %s dechiffrer <cle_privee> <fichier_chiffre> <fichier_sortie>\n", argv[0]);
        return 1;
    }
    
    // COMMANDE: generer
    // Génère une paire de clés RSA (publique et privée)
    if (strcmp(argv[1], "generer") == 0) {
        if (argc != 4) {
            fprintf(stderr, "Usage: %s generer <cle_publique> <cle_privee>\n", argv[0]);
            return 1;
        }
        generer_cles(argv[2], argv[3]);
    }
    
    // COMMANDE: chiffrer
    // Chiffre un message avec la clé publique
    else if (strcmp(argv[1], "chiffrer") == 0) {
        if (argc != 5) {
            fprintf(stderr, "Usage: %s chiffrer <cle_publique> <message> <fichier_sortie>\n", argv[0]);
            return 1;
        }
        chiffrer(argv[2], argv[3], argv[4]);
    }
    
    // COMMANDE: dechiffrer
    // Déchiffre un message avec la clé privée
    else if (strcmp(argv[1], "dechiffrer") == 0) {
        if (argc != 5) {
            fprintf(stderr, "Usage: %s dechiffrer <cle_privee> <fichier_chiffre> <fichier_sortie>\n", argv[0]);
            return 1;
        }
        dechiffrer(argv[2], argv[3], argv[4]);
    }
    
    // COMMANDE INCONNUE
    else {
        fprintf(stderr, "Commande inconnue: %s\n", argv[1]);
        return 1;
    }

    return 0;  
}
