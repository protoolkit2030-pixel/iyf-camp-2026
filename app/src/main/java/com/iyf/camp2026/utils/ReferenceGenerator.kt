package com.iyf.camp2026.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

object ReferenceGenerator {

    /**
     * Génère un numéro de référence unique au format IYF-CAMP-XXXXXX
     */
    fun generate(): String {
        val timestamp = System.currentTimeMillis()
        val random = Random.nextInt(100, 999)
        val number = (timestamp % 999 + random * 100).toString().takeLast(6).padStart(6, '0')
        return "IYF-CAMP-$number"
    }

    /**
     * Retourne la date et l'heure actuelles formatées
     */
    fun getCurrentDateTimeFormatted(): String {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
            LocalDateTime.now().format(formatter)
        } else {
            val sdf = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss", java.util.Locale.FRANCE)
            sdf.format(java.util.Date())
        }
    }

    /**
     * Retourne la date actuelle formatée (sans heure)
     */
    fun getCurrentDateFormatted(): String {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            LocalDateTime.now().format(formatter)
        } else {
            val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.FRANCE)
            sdf.format(java.util.Date())
        }
    }
}
