#!/bin/bash
# Script pour télécharger les polices Poppins depuis Google Fonts
# Exécutez ce script depuis le répertoire racine du projet

FONT_DIR="app/src/main/res/font"
mkdir -p "$FONT_DIR"

echo "Téléchargement des polices Poppins..."

# Télécharger Poppins Regular
curl -L "https://fonts.gstatic.com/s/poppins/v21/pxiEyp8kv8JHgFVrJJfecg.woff2" -o /tmp/poppins_regular.woff2
# Télécharger Poppins Medium
curl -L "https://fonts.gstatic.com/s/poppins/v21/pxiByp8kv8JHgFVrLGT9Z1xlFQ.woff2" -o /tmp/poppins_medium.woff2
# Télécharger Poppins SemiBold
curl -L "https://fonts.gstatic.com/s/poppins/v21/pxiByp8kv8JHgFVrLEj6Z1xlFQ.woff2" -o /tmp/poppins_semibold.woff2
# Télécharger Poppins Bold
curl -L "https://fonts.gstatic.com/s/poppins/v21/pxiByp8kv8JHgFVrLCz7Z1xlFQ.woff2" -o /tmp/poppins_bold.woff2

# Alternative: utiliser npm pour convertir ou télécharger directement les TTF
# Les TTF peuvent être téléchargés depuis: https://fonts.google.com/specimen/Poppins

echo ""
echo "IMPORTANT: Les polices au format .woff2 ne sont pas directement utilisables sur Android."
echo "Veuillez télécharger les fichiers TTF depuis: https://fonts.google.com/specimen/Poppins"
echo "Et placer dans: $FONT_DIR/"
echo "  - poppins_regular.ttf"
echo "  - poppins_medium.ttf"
echo "  - poppins_semibold.ttf"
echo "  - poppins_bold.ttf"
echo ""
echo "Ensuite, supprimez le fichier: $FONT_DIR/poppins_regular.xml"
