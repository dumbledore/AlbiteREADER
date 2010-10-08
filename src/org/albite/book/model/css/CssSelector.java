/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.model.css;

/**
 *
 * @author Svetlin Ankov <galileostudios@gmail.com>
 */
public class CssSelector {
    public final String         elementName;
    public final String         elementID;
    public final String         elementClass;

    private final int           hash;

    public CssSelector(
            final String elementName,
            final String elementID,
            final String elementClass) {

        this.elementName = elementName;
        this.elementID = elementID;
        this.elementClass = elementClass;

        this.hash = calculateHashCode(elementName, elementID, elementClass);
    }

    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof CssSelector)) {
            return false;
        }

        final CssSelector other = (CssSelector) o;

        if (
                ((this.elementName != null
                && this.elementName.equalsIgnoreCase(other.elementName))
                || this.elementName == null && other.elementName == null)

                &&

                ((this.elementID != null
                && this.elementID.equalsIgnoreCase(other.elementID))
                || this.elementID == null && other.elementID == null)

                &&

                ((this.elementClass != null
                && this.elementClass.equalsIgnoreCase(other.elementClass))
                || this.elementClass == null && other.elementClass == null)
                ) {
            return true;
        }

        return false;
    }

    private int calculateHashCode(
            final String elementName,
            final String elementID,
            final String elementClass) {

        int h = 0;

        if (elementName != null) {
            h = 37 * h + elementName.hashCode();
        }

        if (elementID != null) {
            h = 37 * h + elementID.hashCode();
        }

        if (elementClass != null) {
            h = 37 * h + elementClass.hashCode();
        }

        return h;
    }

    public int hashCode() {
        return hash;
    }

//    public static CSS[] parseCSS() {
//
//    }
//
//    public static StyleElement parseStyle(char[] input) {
//
//    }
}