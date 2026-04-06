package com.iyf.camp2026.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.iyf.camp2026.domain.model.Inscription

@Entity(tableName = "inscriptions")
data class InscriptionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val referenceNumber: String,
    val dateHeure: String,
    val nom: String,
    val prenom: String,
    val dateNaissance: String,
    val sexe: String,
    val telephone: String,
    val email: String,
    val quartier: String,
    val etablissement: String,
    val classe: String,
    val filiere: String,
    val coursSelectionnes: String, // JSON array stored as string
    val montant: Int = 5000,
    val modePaiement: String,
    val numeroTransaction: String,
    val statut: String = "En attente",
    val synced: Boolean = false
) {
    fun toDomain(): Inscription {
        val cours = if (coursSelectionnes.isNotEmpty()) {
            coursSelectionnes.split("|").filter { it.isNotEmpty() }
        } else emptyList()
        return Inscription(
            id = id,
            referenceNumber = referenceNumber,
            dateHeure = dateHeure,
            nom = nom,
            prenom = prenom,
            dateNaissance = dateNaissance,
            sexe = sexe,
            telephone = telephone,
            email = email,
            quartier = quartier,
            etablissement = etablissement,
            classe = classe,
            filiere = filiere,
            coursSelectionnes = cours,
            montant = montant,
            modePaiement = modePaiement,
            numeroTransaction = numeroTransaction,
            statut = statut,
            synced = synced
        )
    }

    companion object {
        fun fromDomain(inscription: Inscription): InscriptionEntity {
            return InscriptionEntity(
                id = inscription.id,
                referenceNumber = inscription.referenceNumber,
                dateHeure = inscription.dateHeure,
                nom = inscription.nom,
                prenom = inscription.prenom,
                dateNaissance = inscription.dateNaissance,
                sexe = inscription.sexe,
                telephone = inscription.telephone,
                email = inscription.email,
                quartier = inscription.quartier,
                etablissement = inscription.etablissement,
                classe = inscription.classe,
                filiere = inscription.filiere,
                coursSelectionnes = inscription.coursSelectionnes.joinToString("|"),
                montant = inscription.montant,
                modePaiement = inscription.modePaiement,
                numeroTransaction = inscription.numeroTransaction,
                statut = inscription.statut,
                synced = inscription.synced
            )
        }
    }
}
