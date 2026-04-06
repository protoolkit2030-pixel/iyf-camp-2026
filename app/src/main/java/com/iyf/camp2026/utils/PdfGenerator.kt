package com.iyf.camp2026.utils

import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.util.Log
import com.iyf.camp2026.domain.model.Inscription
import java.io.File
import java.io.FileOutputStream

object PdfGenerator {
    private const val TAG = "PdfGenerator"

    // Colors
    private val COLOR_ORANGE = Color.rgb(255, 102, 0)       // #FF6600
    private val COLOR_DARK_GREEN = Color.rgb(27, 67, 50)    // #1B4332
    private val COLOR_WHITE = Color.WHITE
    private val COLOR_LIGHT_GRAY = Color.rgb(245, 245, 245)
    private val COLOR_DARK_GRAY = Color.rgb(80, 80, 80)
    private val COLOR_BLACK = Color.BLACK

    // Page dimensions (A4 at 72 dpi)
    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN = 40f

    /**
     * Génère le reçu PDF pour une inscription et le sauvegarde dans le stockage
     * @return Le chemin du fichier PDF généré, ou null en cas d'erreur
     */
    fun generateReceipt(context: Context, inscription: Inscription): File? {
        return try {
            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas

            var yPos = drawDocument(canvas, inscription)

            document.finishPage(page)

            // Save to file
            val pdfFile = getPdfFile(context, inscription.referenceNumber)
            pdfFile.parentFile?.mkdirs()
            FileOutputStream(pdfFile).use { fos ->
                document.writeTo(fos)
            }
            document.close()

            Log.d(TAG, "PDF généré: ${pdfFile.absolutePath}")
            pdfFile
        } catch (e: Exception) {
            Log.e(TAG, "Erreur génération PDF", e)
            null
        }
    }

    private fun drawDocument(canvas: Canvas, inscription: Inscription): Float {
        var yPos = MARGIN

        // ─── HEADER BACKGROUND ───────────────────────────────────────
        val headerPaint = Paint().apply {
            color = COLOR_ORANGE
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), 130f, headerPaint)

        // ─── IYF LOGO (circle) ───────────────────────────────────────
        val logoPaint = Paint().apply {
            color = Color.rgb(0, 70, 160) // IYF blue
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawCircle(70f, 65f, 40f, logoPaint)

        // IYF text in logo
        val logoTextPaint = Paint().apply {
            color = COLOR_WHITE
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("IYF", 70f, 72f, logoTextPaint)

        // ─── RECEIPT TITLE ───────────────────────────────────────────
        val titlePaint = Paint().apply {
            color = COLOR_WHITE
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("REÇU D'INSCRIPTION", 130f, 45f, titlePaint)

        val subtitlePaint = Paint().apply {
            color = COLOR_WHITE
            textSize = 13f
            isAntiAlias = true
        }
        canvas.drawText("Camp d'Étude et de Formation - IYF 2026", 130f, 68f, subtitlePaint)
        canvas.drawText("International Youth Fellowship", 130f, 88f, subtitlePaint)

        yPos = 140f

        // ─── CAMP INFO BOX ───────────────────────────────────────────
        val boxPaint = Paint().apply {
            color = COLOR_LIGHT_GRAY
            style = Paint.Style.FILL
        }
        val borderPaint = Paint().apply {
            color = COLOR_ORANGE
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }
        canvas.drawRect(MARGIN, yPos, PAGE_WIDTH - MARGIN, yPos + 60f, boxPaint)
        canvas.drawRect(MARGIN, yPos, PAGE_WIDTH - MARGIN, yPos + 60f, borderPaint)

        // Left orange accent bar
        val accentPaint = Paint().apply {
            color = COLOR_ORANGE
            style = Paint.Style.FILL
        }
        canvas.drawRect(MARGIN, yPos, MARGIN + 8f, yPos + 60f, accentPaint)

        val campInfoTitle = Paint().apply {
            color = COLOR_DARK_GREEN
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("🗓  08 au 10 Avril 2026", MARGIN + 20f, yPos + 22f, campInfoTitle)
        canvas.drawText("📍  Cours Secondaire Méthodiste (CSM) Niangon", MARGIN + 20f, yPos + 44f, campInfoTitle)

        yPos += 80f

        // ─── REFERENCE NUMBER ────────────────────────────────────────
        val refBgPaint = Paint().apply {
            color = COLOR_DARK_GREEN
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(
            RectF(MARGIN, yPos, PAGE_WIDTH - MARGIN, yPos + 50f),
            8f, 8f, refBgPaint
        )

        val refLabelPaint = Paint().apply {
            color = Color.rgb(255, 200, 150)
            textSize = 11f
            isAntiAlias = true
        }
        canvas.drawText("N° DE RÉFÉRENCE", MARGIN + 15f, yPos + 18f, refLabelPaint)

        val refValuePaint = Paint().apply {
            color = COLOR_WHITE
            textSize = 20f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText(inscription.referenceNumber, MARGIN + 15f, yPos + 40f, refValuePaint)

        // Date on the right
        val datePaint = Paint().apply {
            color = COLOR_WHITE
            textSize = 11f
            textAlign = Paint.Align.RIGHT
            isAntiAlias = true
        }
        canvas.drawText("Date: ${inscription.dateHeure}", PAGE_WIDTH - MARGIN - 15f, yPos + 30f, datePaint)

        yPos += 70f

        // ─── SECTION TITLE: INFORMATIONS DU PARTICIPANT ──────────────
        yPos = drawSectionTitle(canvas, "INFORMATIONS DU PARTICIPANT", yPos)

        // ─── PARTICIPANT INFO ROWS ────────────────────────────────────
        yPos = drawInfoRow(canvas, "Nom & Prénom", "${inscription.prenom} ${inscription.nom}", yPos)
        yPos = drawInfoRow(canvas, "Date de naissance", inscription.dateNaissance, yPos)
        yPos = drawInfoRow(canvas, "Sexe", inscription.sexe, yPos)
        yPos = drawInfoRow(canvas, "Téléphone", inscription.telephone, yPos)
        if (inscription.email.isNotEmpty()) {
            yPos = drawInfoRow(canvas, "Email", inscription.email, yPos)
        }
        yPos = drawInfoRow(canvas, "Quartier / Commune", inscription.quartier, yPos)

        yPos += 10f

        // ─── SECTION TITLE: INFORMATIONS SCOLAIRES ───────────────────
        yPos = drawSectionTitle(canvas, "INFORMATIONS SCOLAIRES", yPos)

        yPos = drawInfoRow(canvas, "Établissement", inscription.etablissement, yPos)
        yPos = drawInfoRow(canvas, "Classe / Niveau", inscription.classe, yPos)
        if (inscription.filiere.isNotEmpty() && inscription.filiere != "Autre") {
            yPos = drawInfoRow(canvas, "Filière", inscription.filiere, yPos)
        }
        yPos = drawInfoRow(canvas, "Cours sélectionnés", inscription.coursSelectionnesString, yPos)

        yPos += 10f

        // ─── SECTION TITLE: PAIEMENT ─────────────────────────────────
        yPos = drawSectionTitle(canvas, "INFORMATIONS DE PAIEMENT", yPos)

        val amountBgPaint = Paint().apply {
            color = Color.rgb(255, 240, 220)
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(RectF(MARGIN, yPos, PAGE_WIDTH - MARGIN, yPos + 45f), 6f, 6f, amountBgPaint)

        val amountLabelPaint = Paint().apply {
            color = COLOR_DARK_GRAY
            textSize = 12f
            isAntiAlias = true
        }
        canvas.drawText("Montant réglé :", MARGIN + 12f, yPos + 18f, amountLabelPaint)

        val amountValuePaint = Paint().apply {
            color = COLOR_ORANGE
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("5 000 FCFA", MARGIN + 150f, yPos + 35f, amountValuePaint)
        yPos += 55f

        yPos = drawInfoRow(canvas, "Mode de paiement", inscription.modePaiement, yPos)
        if (inscription.numeroTransaction.isNotEmpty()) {
            yPos = drawInfoRow(canvas, "N° Transaction", inscription.numeroTransaction, yPos)
        }
        yPos = drawInfoRow(canvas, "Statut", inscription.statut, yPos, highlightValue = true)

        yPos += 15f

        // ─── QR CODE ─────────────────────────────────────────────────
        val qrBitmap = QrCodeGenerator.generateQrCode(inscription.referenceNumber, 120)
        val qrLeft = PAGE_WIDTH - MARGIN - 130f
        canvas.drawBitmap(qrBitmap, qrLeft, yPos, null)

        val qrLabelPaint = Paint().apply {
            color = COLOR_DARK_GRAY
            textSize = 9f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("Scanner pour vérifier", qrLeft + 60f, yPos + 135f, qrLabelPaint)

        // ─── IMPORTANT MESSAGE BOX ────────────────────────────────────
        val msgBgPaint = Paint().apply {
            color = Color.rgb(232, 245, 233) // Light green
            style = Paint.Style.FILL
        }
        val msgBorderPaint = Paint().apply {
            color = COLOR_DARK_GREEN
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        val msgBoxTop = yPos
        val msgBoxRight = qrLeft - 15f

        canvas.drawRoundRect(RectF(MARGIN, msgBoxTop, msgBoxRight, msgBoxTop + 130f), 6f, 6f, msgBgPaint)
        canvas.drawRoundRect(RectF(MARGIN, msgBoxTop, msgBoxRight, msgBoxTop + 130f), 6f, 6f, msgBorderPaint)

        val msgTitlePaint = Paint().apply {
            color = COLOR_DARK_GREEN
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("ℹ  IMPORTANT", MARGIN + 10f, msgBoxTop + 20f, msgTitlePaint)

        val msgTextPaint = Paint().apply {
            color = COLOR_DARK_GRAY
            textSize = 9.5f
            isAntiAlias = true
        }
        val msgLines = listOf(
            "Ce reçu confirme votre inscription au",
            "Camp d'Étude et de Formation du",
            "08 au 10 Avril 2026 au CSM Niangon.",
            "",
            "Veuillez présenter ce reçu à l'entrée",
            "du camp. Toute inscription non payée",
            "doit être réglée sur place."
        )
        var msgY = msgBoxTop + 38f
        for (line in msgLines) {
            canvas.drawText(line, MARGIN + 10f, msgY, msgTextPaint)
            msgY += 14f
        }

        yPos = maxOf(yPos + 145f, msgBoxTop + 160f)

        // ─── FOOTER ───────────────────────────────────────────────────
        val footerBgPaint = Paint().apply {
            color = COLOR_DARK_GREEN
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, PAGE_HEIGHT - 70f, PAGE_WIDTH.toFloat(), PAGE_HEIGHT.toFloat(), footerBgPaint)

        val footerOrangeAccent = Paint().apply {
            color = COLOR_ORANGE
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, PAGE_HEIGHT - 70f, PAGE_WIDTH.toFloat(), PAGE_HEIGHT - 67f, footerOrangeAccent)

        val footerTextPaint = Paint().apply {
            color = COLOR_WHITE
            textSize = 10f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("International Youth Fellowship (IYF) — Camp d'Étude et de Formation 2026", PAGE_WIDTH / 2f, PAGE_HEIGHT - 48f, footerTextPaint)
        canvas.drawText("+225 07 59 87 21 26  |  +225 07 49 54 56 06  |  +225 05 65 65 44 70", PAGE_WIDTH / 2f, PAGE_HEIGHT - 30f, footerTextPaint)

        val footerSmallPaint = Paint().apply {
            color = Color.rgb(180, 210, 190)
            textSize = 8f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("Document généré le ${ReferenceGenerator.getCurrentDateTimeFormatted()}", PAGE_WIDTH / 2f, PAGE_HEIGHT - 12f, footerSmallPaint)

        return yPos
    }

    private fun drawSectionTitle(canvas: Canvas, title: String, yPos: Float): Float {
        val bgPaint = Paint().apply {
            color = COLOR_ORANGE
            style = Paint.Style.FILL
        }
        canvas.drawRect(MARGIN, yPos, PAGE_WIDTH - MARGIN, yPos + 28f, bgPaint)

        val textPaint = Paint().apply {
            color = COLOR_WHITE
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText(title, MARGIN + 10f, yPos + 19f, textPaint)
        return yPos + 35f
    }

    private fun drawInfoRow(
        canvas: Canvas,
        label: String,
        value: String,
        yPos: Float,
        highlightValue: Boolean = false
    ): Float {
        val rowHeight = 26f
        val separatorPaint = Paint().apply {
            color = Color.rgb(220, 220, 220)
            strokeWidth = 0.5f
        }
        canvas.drawLine(MARGIN, yPos + rowHeight, PAGE_WIDTH - MARGIN, yPos + rowHeight, separatorPaint)

        val labelPaint = Paint().apply {
            color = COLOR_DARK_GRAY
            textSize = 10f
            isAntiAlias = true
        }
        canvas.drawText(label, MARGIN + 10f, yPos + 18f, labelPaint)

        val valuePaint = Paint().apply {
            color = if (highlightValue && value == "Confirmé") Color.rgb(27, 150, 50)
            else if (highlightValue) COLOR_ORANGE
            else COLOR_BLACK
            textSize = 10f
            typeface = if (highlightValue) Typeface.create(Typeface.DEFAULT, Typeface.BOLD) else Typeface.DEFAULT
            textAlign = Paint.Align.RIGHT
            isAntiAlias = true
        }
        canvas.drawText(value.ifEmpty { "—" }, PAGE_WIDTH - MARGIN - 10f, yPos + 18f, valuePaint)

        return yPos + rowHeight
    }

    fun getPdfFile(context: Context, referenceNumber: String): File {
        val pdfDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "IYF_Receipts")
        pdfDir.mkdirs()
        return File(pdfDir, "Recu_$referenceNumber.pdf")
    }
}
