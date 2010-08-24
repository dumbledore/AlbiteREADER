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
public class AlbiteFont {
    public static final int MAGIC_NUMBER = 1095516742;
    public static final String FILE_EXTENSION = ".alf";
    protected static final String INVALID_FILE_ERROR = "ALF file is corrupted.";
    
    protected final String  fontname;
    public    final int     lineHeight;
    public    final int     lineSpacing;
    public    final int     maximumWidth;

    private   final Glyph[] glyphs;

    private   final AlbiteImageMono glyphsCanvas;
    private   final byte[]          glyphsCanvasData;
    private   final int             glyphsCanvasWidth;
    private   final int             glyphsCanvasHeight;

    protected final int[]   imageBuffer; //shared by all requests

    public final int spaceWidth;
    public final int dashWidth;
    public final int questionWidth;

    public AlbiteFont(String fontname) 
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

        lineSpacing = din.readByte();           //1 byte for linespacing
        lineHeight = din.readByte();            //1 byte for lineheight (characters wouldn't be likely to be more than 127pixels high, right?!
        glyphs = new Glyph[din.readInt()+1];    //4 bytes for character range
        maximumWidth = din.readShort();         //2 bytes for maximum width
        int charsCount = din.readInt();         //4 bytes for character count
        int currentChar = 0;
        short x,y,w,h,xo,yo,xa;
        for (int i=0; i<charsCount; i++) {
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
    }

    public String getFontname() {
        return fontname;
    }
    
    public int charsWidth(char[] c, int offset, int length) {

        int res = 0;
        int charWidth = 0;
        char currentChar;
        Glyph g;
        int glyphsLen = glyphs.length;

        for (int i=offset; i<offset+length; i++) {
            currentChar = c[i];
            if (currentChar < glyphsLen) {
                g = glyphs[currentChar];
                if (g == null) {
                    //non-supported chars are replaced by `?`
                    charWidth = questionWidth;
                } else {
                    charWidth = g.xadvance;
                }
                res += charWidth;
            } else {
                //non-supported chars are replaced by `?`
                charWidth = questionWidth;
            }
        }
        return res;
    }

    public int charsWidth(char[] c) {
        return charsWidth(c, 0, c.length);
    }

    public void drawChars(Graphics g, int color, char[] buffer, int x, int y, int offset, int length) {
        int end = offset+length;
        int c;
        int glyphLen = glyphs.length;

        Glyph glyph;
        for (int i=offset; i<end; i++) {
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

    public void drawChars(Graphics g, int color, char[] buffer, int x, int y) {
        drawChars(g, color, buffer, x, y, 0, buffer.length);
    }

    private void drawCharFromGlyph(Graphics g, int color, Glyph glyph, int x, int y) {

        /* copy from source to buffer and meanwhile transform color */
        final int widthIn       = glyphsCanvasWidth;
        final int glyphWidth    = glyph.width;
        final int glyphHeight   = glyph.height;
        final int glyphX        = glyph.x;
        final int glyphY        = glyph.y;

        int yy = 0;
        int xxend = 0;
        int skip = widthIn - glyphWidth;
        int xx=glyphY*widthIn + glyphX;
        int i = 0;
        while (yy < glyphHeight) {
            xxend = xx+glyphWidth;
            while(xx < xxend) {
                /* mask + add color */
                imageBuffer[i] = (glyphsCanvasData[xx] << 24) + color;
                i++;
                xx++;
            }
            xx += skip;
            yy++;
        }
        g.drawRGB(imageBuffer, 0, glyphWidth, x + glyph.xoffset, y + glyph.yoffset, glyphWidth, glyphHeight, true);
    }

    public void drawChar(Graphics g, int color, char c, int x, int y) {
        
        /* non-supported chars are not rendered */
        if (c < glyphs.length) {
            Glyph glyph = glyphs[c];
                if (glyph != null) {
                    drawCharFromGlyph(g, color, glyph, x, y);
            }
        }
    }
}