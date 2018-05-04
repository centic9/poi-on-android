package org.apache.poi.java.awt.font;

import java.awt.geom.Rectangle2D;

import java.text.AttributedCharacterIterator;
import java.util.Set;


/**
 * See {@link java.awt.font.TextLayout}
 */
public class TextLayout {

    private final AttributedCharacterIterator text;

    public  TextLayout(AttributedCharacterIterator text, FontRenderContext frc) {
        this.text = text;
    }

    public float  getAdvance() {
        return 1.0f;
    }

    public Rectangle2D getBounds() {
        return new Rectangle2D() {
            @Override
            public double getX() {
                return 0;
            }

            @Override
            public double getY() {
                return 0;
            }

            @Override
            public double getWidth() {
                // 1 semble être la taille d'un caractère dans Excel en police Arial (Calibri en vérité).
                // je suis convaincu que c'est faux mais ça marche tellement bien que je vais laisser
                // ça comme ça avec une marge de 1 au cas où...
                // Astuce pour ne pas redimensionner en fonction de toutes les lignes: utiliser les
                // attributs posés sur le texte: si Gras alors redimensionner, sinon osef
                // Gras == true si attribut weight == 2

                // 1 seems to be acceptable size for Calibri font +1 for margin
                // Do not autosize if text is Bold (for table header only for example)
                for (AttributedCharacterIterator.Attribute key: text.getAllAttributeKeys()) {
                    if (key.toString().contains("weight")) {
                        return text.getEndIndex() - text.getBeginIndex() + 1;
                    }
                }
                return -1;
            }

            @Override
            public double getHeight() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public void setRect(double v, double v1, double v2, double v3) {

            }

            @Override
            public int outcode(double v, double v1) {
                return 0;
            }

            @Override
            public Rectangle2D createIntersection(Rectangle2D rectangle2D) {
                return null;
            }

            @Override
            public Rectangle2D createUnion(Rectangle2D rectangle2D) {
                return null;
            }
        };
    }
}
