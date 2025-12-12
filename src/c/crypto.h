#ifndef CRYPTO_H
#define CRYPTO_H

/* ===== INCLUSIONS DES BIBLIOTHEQUES ===== */
#include <stdio.h>      
#include <stdlib.h>     
#include <time.h>       // Pour la génération de nombres aléatoires
#include <stdbool.h>    // Pour le type booléen
#include <string.h>     

/* ===== STRUCTURES DE DONNEES ===== */

/**
 * Structure: CleRSA
 * Description: Représente une clé RSA (publique ou privée)
 */
typedef struct crypto {
    int n;              // MODULE: n = p * q
    int e;              // EXPOSANT: e (public) ou d (privé)
    int p;              // PREMIER FACTEUR PREMIER
    int q;              // SECOND FACTEUR PREMIER
    int phi;            // INDICATRICE D'EULER: φ(n) = (p-1)(q-1)
    bool est_publique;  // TYPE DE CLE: true si publique, false si privée
} CleRSA;

/* ===== PROTOTYPES DES FONCTIONS ===== */

/**
 * Fonction: pgcd
 * Description: Calcule le Plus Grand Commun Diviseur de deux nombres
 */
int pgcd(int a, int b);

/**
 * Fonction: inverse_modulaire
 * Description: Calcule l'inverse modulaire de e modulo phi
 */
int inverse_modulaire(int e, int phi);

/**
 * Fonction: estPremier
 * Description: Détermine si un nombre est premier
 */
bool estPremier(int n);

/**
 * Fonction: generer_premier
 * Description: Génère un nombre premier aléatoire dans un intervalle donné
 */
int generer_premier(int min, int max);

/**
 * Fonction: exp_modulaire
 * Description: Calcule (base^exp) mod mod de manière efficace
 */
long long exp_modulaire(long long base, long long exp, long long mod);

/**
 * Fonction: generer_cles
 * Description: Génère une paire de clés RSA (publique et privée) 
 */
void generer_cles(const char *fichier_publique, const char *fichier_prive);

/**
 * Fonction: lire_cle
 * Description: Lit une clé RSA depuis un fichier
 */
CleRSA *lire_cle(const char *fichier);

/**
 * Fonction: creer_cle
 * Description: Crée une structure CleRSA avec tous ses composants
 */
CleRSA *creer_cle(int n, int e, int p, int q, int phi, bool est_publique);

/**
 * Fonction: valider_cle
 * Description: Valide la cohérence mathématique d'une clé RSA
 */
bool valider_cle(CleRSA *cle);

/**
 * Fonction: liberer_cle
 * Description: Libère la mémoire allouée pour une structure CleRSA
 */
void liberer_cle(CleRSA *cle);

/**
 * Fonction: chiffrer_avec_cle
 * Description: Chiffre un message en utilisant une structure CleRSA
 */
void chiffrer_avec_cle(CleRSA *cle, const char *message, const char *fichier_sortie);

/**
 * Fonction: dechiffrer_avec_cle
 * Description: Déchiffre un message en utilisant une structure CleRSA
 */
void dechiffrer_avec_cle(CleRSA *cle, const char *fichier_entree, const char *fichier_sortie);

/**
 * Fonction: chiffrer
 * Description: Chiffre un message 
 */
void chiffrer(const char *fichier_cle, const char *message, const char *fichier_sortie);

/**
 * Fonction: dechiffrer
 * Description: Déchiffre un message 
 */
void dechiffrer(const char *fichier_cle, const char *fichier_entree, const char *fichier_sortie);

/**
 * Fonction: longeur
 * Description: Calcule la longueur d'une chaîne de caractères
 */
int longeur(const char *s);

#endif 
