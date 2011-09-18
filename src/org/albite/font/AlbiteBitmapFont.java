/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.font;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.lcdui.Graphics;
import org.albite.image.AlbiteImageException;
import org.albite.image.AlbiteImageMono;

/**
 *
 * @author Albus Dumbledore
 */
public class AlbiteBitmapFont extends AlbiteFont {
    protected final String          fontname;

    public    final int             lineHeight;
    public    final int             lineSpacing;
    public    final int             maximumWidth;

    private   final Glyph[]         glyphs;

    private   final AlbiteImageMono glyphsCanvas;
    private   final byte[]          glyphsCanvasData;
    private   final int             glyphsCanvasWidth;
    private   final int             glyphsCanvasHeight;

    /*
     * shared by all requests
     */
    protected final int[]           imageBuffer;

    public final int                spaceWidth;
    public final int                dashWidth;
    public final int                questionWidth;

    public AlbiteBitmapFont(final String fontname)
            throws IOException, AlbiteFontException {

        this.fontname = fontname;

        final String fontFileName = "/res/font/" + fontname;

        /* reading character descriptions */
        InputStream in;
        DataInputStream din;

        in = this.getClass().getResourceAsStream(fontFileName + FILE_EXTENSION);
        if (in == null) {
            throw new IOException("Font " + fontname + "was not found!");
        }

        din = new DataInputStream(in);

        if (din.readInt() != MAGIC_NUMBER) {
            throw new AlbiteFontException(INVALID_FILE_ERROR);
        }

        /*
         * 1 byte for linespacing
         */
        lineSpacing = din.readByte();

        /*
         * 1 byte for lineheight: Characters wouldn't be likely to be
         * more than 127 pixels high, would they?
         */
        lineHeight = din.readByte();

        /*
         * 4 bytes for character range
         */
        glyphs = new Glyph[din.readInt() + 1];

        /*
         * 2 bytes for maximum width
         */
        maximumWidth = din.readShort();

        /*
         * 4 bytes for character count
         */
        int charsCount = din.readInt();
        int currentChar = 0;
        short x, y, w, h, xo, yo, xa;

        for (int i = 0; i < charsCount; i++) {
            currentChar = din.readInt();
            x  = din.readShort();
            y  = din.readShort();
            w  = din.readShort();
            h  = din.readShort();
            xo = din.readShort();
            yo = din.readShort();
            xa = din.readShort();
            glyphs[currentChar] = new Glyph(x, y, w, h, xo, yo, xa);
        }
        din.close();

        /* read glyphs image */
        in = this.getClass().getResourceAsStream(
                fontFileName + AlbiteImageMono.FILE_EXTENSION);

        if (in == null) {
            throw new AlbiteFontException("Missing graphics for font "
                    + fontname);
        }

        try {
            glyphsCanvas = new AlbiteImageMono(in);
        } catch (AlbiteImageException aie) {
            throw new AlbiteFontException("Could not load graphics for font "
                    + fontname);
        }

        glyphsCanvasData = glyphsCanvas.getData();
        glyphsCanvasWidth = glyphsCanvas.getWidth();
        glyphsCanvasHeight = glyphsCanvas.getHeight();

        imageBuffer = new int[lineHeight * maximumWidth];

        if (' ' < glyphs.length && glyphs[' '] != null) {
            spaceWidth = glyphs[' '].xadvance;
        }  else {
            spaceWidth = 0;
        }

        if ('-' < glyphs.length && glyphs['-'] != null) {
            dashWidth  = glyphs['-'].xadvance;
        } else {
            dashWidth = 0;
        }

        if ('?' < glyphs.length && glyphs['?'] != null) {
            questionWidth = glyphs['?'].xadvance;
        } else {
            questionWidth = 0;
        }

        /*
         * Null char must be regarded as a space
         * Generally, there must not be any null chars for rendering
         * as the parsers should have ommitted them, but this
         * is a safe measure.
         */
        glyphs[0] = glyphs[' '];
    }

    public final int charWidth(char c) {
        if (c < glyphs.length) {
            final Glyph g = glyphs[c];

            if (g == null) {
                //non-supported chars are replaced by `?`
                return questionWidth;
            } else {
                return g.xadvance;
            }
        } else {
            //non-supported chars are replaced by `?`
            return questionWidth;
        }
    }

    public final void drawChars(
            final Graphics g,
            final int color,
            final char[] buffer,
                  int x, final int y,
            final int offset,
            final int length) {
        int end = offset+length;
        int c;
        int glyphLen = glyphs.length;

        Glyph glyph;
        for (int i = offset; i < end; i++) {
            c = buffer[i];

            if (c < glyphLen) {
                glyph = glyphs[c];
            } else {
                //non-supported chars are replaced by `?`
                glyph = glyphs['?'];
            }

            if (glyph == null) {
                glyph = glyphs['?'];
            }

            drawCharFromGlyph(g, color, glyph, x, y);
            x += glyph.xadvance;
        }
    }

    private void drawCharFromGlyph(
            final Graphics g,
            final int color,
            final Glyph glyph,
            final int x, final int y) {

        /* copy from source to buffer and meanwhile transform color */
        final int widthIn       = glyphsCanvasWidth;
        final int glyphWidth    = glyph.width;
        final int glyphHeight   = glyph.height;
        final int glyphX        = glyph.x;
        final int glyphY        = glyph.y;

        int yy = 0;
        int xxend = 0;
        int skip = widthIn - glyphWidth;
        int xx = (glyphY * widthIn) + glyphX;
        int i = 0;
        while (yy < glyphHeight) {
            xxend = xx + glyphWidth;
            while(xx < xxend) {
                /* mask + add color */
                imageBuffer[i] = (glyphsCanvasData[xx] << 24) + color;
                i++;
                xx++;
            }
            xx += skip;
            yy++;
        }
        g.drawRGB(imageBuffer, 0, glyphWidth,
                x + glyph.xoffset, y + glyph.yoffset,
                glyphWidth, glyphHeight, true);
    }

    public final void drawChar(
            final Graphics g,
            final int color,
            final char c,
            final int x, final int y) {

        /* non-supported chars are not rendered */
        if (c < glyphs.length) {
            Glyph glyph = glyphs[c];
                if (glyph != null) {
                    drawCharFromGlyph(g, color, glyph, x, y);
            }
        }
    }

    public int getLineHeight() {
        return lineHeight;
    }
}