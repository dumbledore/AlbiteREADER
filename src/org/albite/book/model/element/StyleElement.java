/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.model.element;

import org.albite.book.StyleConstants;

/**
 *
 * @author Svetlin Ankov <galileostudios@gmail.com>
 */
public class StyleElement
        implements StyleConstants, Element {

    public static final StyleElement DEFAULT_STYLE =
            new StyleElement(DISABLE, DISABLE, DISABLE, JUSTIFY);

    public byte bold;
    public byte italic;
    public byte heading;
    public byte align;

    public StyleElement(
            final byte italicState,
            final byte boldState,
            final byte headingState,
            final byte textAlign) {

        this.italic     = italicState;
        this.bold       = boldState;
        this.heading    = headingState;
        this.align      = textAlign;
    }

    public void mergeWith(final StyleElement other) {

        if (other.italic != INHERIT) {
            this.italic = other.italic;
        }

        if (other.bold != INHERIT) {
            this.bold = other.bold;
        }

        if (other.heading != INHERIT) {
            this.heading = other.heading;
        }

        if (other.align != INHERIT) {
            this.align = other.align;
        }
    }

    public final boolean equals(final Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || !(o instanceof StyleElement)) {
            return false;
        }

        final StyleElement other = (StyleElement) o;

        return (this.italic == other.italic
                && this.bold == other.bold
                && this.heading == other.heading
                && this.align == other.align
                );
    }

    public final int hashCode() {
        return (
                  ((italic   & 0xFF) << 24)
                | ((bold     & 0xFF) << 16)
                | ((heading  & 0xFF) <<  8)
                | ( align    & 0xFF)
                );
    }

    public final byte getType() {
        return STYLE;
    }
}