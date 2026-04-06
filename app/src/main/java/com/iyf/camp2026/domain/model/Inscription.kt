package com.iyf.camp2026.domain.model

data class Inscription(
    val id: Long = 0,
    val referenceNumber: String = "",
    val dateHeure: String = "",
    val nom: String = "",
    val prenom: String = "",
    val dateNaissance: String = "",
    val sexe: String = "",
    val telephone: String = "",
    val email: String = "",
    val quartier: String = "",
    val etablissement: String = "",
    val classe: String = "",
    val filiere: String = "",
    val coursSelectionnes: List<String> = emptyList(),
    val montant: Int = 5000,
    val modePaiement: String = "",
    val numeroTransaction: String = "",
    val statut: String = "En attente",
    val synced: Boolean = false
) {
    val coursSelectionnesString: String
        get() = coursSelectionnes.joinToString(", ")

    val nomComplet: String
        get() = "$prenom $nom"
}

enum class ModePaiement(val label: String) {
    MOBILE_MONEY("Mobile Money (MTN / Wave / Orange Money)"),
    ESPECES("Espèces sur place")
}

enum class Sexe(val label: String) {
    MASCULIN("Masculin"),
    FEMININ("Féminin")
}

enum class Classe(val label: String) {
    SIXIEME("6ème"),
    CINQUIEME("5ème"),
    QUATRIEME("4ème"),
    TROISIEME("3ème"),
    SECONDE("2nde"),
    PREMIERE("1ère"),
    TERMINALE("Terminale"),
    AUTRE("Autre")
}

enum class Filiere(val label: String) {
    SCIENTIFIQUE("Scientifique"),
    LITTERAIRE("Littéraire"),
    AUTRE("Autre")
}

val COURS_DISPONIBLES = listOf(
    "Français",
    "Philosophie",
    "Mathématiques",
    "Physique-Chimie"
)
