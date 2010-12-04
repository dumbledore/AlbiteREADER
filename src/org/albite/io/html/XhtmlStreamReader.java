/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.io.html;

import org.albite.io.decoders.AlbiteStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Hashtable;

/**
 *
 * @author albus
 */
public class XhtmlStreamReader extends Reader implements HTMLSubstitues {

    private static final int SEARCH_BUFFER = 2048;
    private static final Hashtable ENTITIES = new Hashtable(200);

    private final AlbiteStreamReader in;
    private final char[] buffer = new char[10];
    private Hashtable customEntities;

    public XhtmlStreamReader(
            final AlbiteStreamReader in,
            final boolean readXmlDecl,
            final boolean readDoctypeDecl)
            throws IOException {

        this.in = in;

        if (readXmlDecl) {
            /*
             * Read xml decl
             */
            xmldecl();
        }

        if (readDoctypeDecl) {
            /*
             * Read doc decl
             */
            doctypedecl();
        }
    }

    private void xmldecl() throws IOException {
        /*
         * Try to read the xmldecl
         */

        /*
         * Mark four times as much, for the underlying AlbiteStreamReader,
         * might read multibyte (4-byte) UTF-8 chars
         */
        in.mark(SEARCH_BUFFER * 4);
        try {
            char[] buf = new char[SEARCH_BUFFER];
            int read = in.read(buf);

            if (read > 0) {
                String xmldecl = new String(buf, 0, read);
                if (!xmldecl.startsWith("<?xml")) {
                    in.reset();
                    return;
                }

                int xend = xmldecl.indexOf("?>");

                if (xend == -1) {
                    in.reset();
                    return;
                }

                xmldecl = xmldecl.substring(0, xend + 2);

                final int[] encodingPosition =
                        readAttribute(xmldecl, "encoding");

                if (encodingPosition == null) {
                    in.reset();
                    return;
                }

                final String encoding = xmldecl.substring(
                        encodingPosition[0],
                        encodingPosition[0] + encodingPosition[1]);

                try {
                    in.setEncoding(encoding);
                } catch (Exception e) {
                    /*
                     * Do nothing: the reader will continue with
                     * its current settings
                     */
                }

                in.reset();
                skip(xmldecl.length());
            } else {
                in.reset();
            }
        } catch (StringIndexOutOfBoundsException e) {
            in.reset();
        } catch (IllegalArgumentException e) {
            in.reset();
        }
    }

    private void doctypedecl() throws IOException {
        /*
         * Try to read the doctypedecl
         */

        /*
         * Mark four times as much, for the underlying AlbiteStreamReader,
         * might read multibyte (4-byte) UTF-8 chars
         */
        in.mark(SEARCH_BUFFER * 4);

        try {
            char[] buf = new char[SEARCH_BUFFER];
            int read = in.read(buf);

            if (read > 0) {
                String ddecl = new String(buf, 0, read);

                final String dstring = "<!DOCTYPE";
                int dstart = ddecl.indexOf(dstring);

                if (dstart == -1) {
                    in.reset();
                    return;
                }

                int dend = ddecl.indexOf('>', dstring.length() + dstart);
                int doptstart = ddecl.indexOf('[', dstring.length() + dstart);

                if (dend == -1) {
                    in.reset();
                    return;
                }

                if (dend < doptstart) {
                    /*
                     * No internal decl.
                     * Just skip the doctype
                     */
                    in.reset();
                    skip(dend + 1);
                }

                int doptend = ddecl.indexOf(']', doptstart + 1);
                dend = ddecl.indexOf('>', doptend + 1);

                if (dend == -1) {
                    in.reset();
                    return;
                }

                final String intdecl = ddecl.substring(doptstart, doptend + 1);

                ddecl = ddecl.substring(dstart, dend + 1);

                final String entstring = "<!ENTITY";
                int entstart = 0;
                int entend = 0;

                while (true) {
                    entstart = intdecl.indexOf(entstring, entstart);

                    if (entstart == -1) {
                        break;
                    }

                    entstart += entstring.length();

                    entend = intdecl.indexOf('>', entstart);

                    if (entend == -1) {
                        break;
                    }

                    try {
                        int replstart = intdecl.indexOf('"', entstart);

                        if (replstart == -1) {
                            continue;
                        }

                        int replend = intdecl.indexOf('"', replstart + 1);

                        if (replend == -1) {
                            continue;
                        }

                        final String entityName = intdecl.substring(entstart, replstart).trim();

                        if (entityName.indexOf('%') != -1) {
                            /*
                             * PEReference entities are not supported
                             */
                            continue;
                        }

                        final String entityValue = intdecl.substring(replstart + 2, replend - 1);

                        int entityIntVal = processEntity(entityValue);
                        if (entityIntVal != 0) {
                            /*
                             * add the entity
                             */
                            if (customEntities == null) {
                                customEntities = new Hashtable(20);
                            }
                            customEntities.put(entityName, new Integer(entityIntVal));
                        }
                    } catch (StringIndexOutOfBoundsException e) {
                        /*
                         * Just skip this entity
                         */
                        continue;
                    }
                }

                in.reset();
                skip(dend + 1);
            } else {
                in.reset();
            }
        } catch (StringIndexOutOfBoundsException e) {
            in.reset();
        } catch (IllegalArgumentException e) {
            in.reset();
        }
    }

    private void skip(int left) throws IOException {
        while (left > 0) {
            left -= in.skip(left);
        }
    }

    public static int[] readAttribute(
            final String tagString, String attribute) {

        attribute += "=";

        try {
            int start = tagString.indexOf(attribute);
            if (start != -1) {
                start += attribute.length();
                final char ch = tagString.charAt(start);
                if (ch == '"' || ch == '\'') {
                    start++;
                    final int end = tagString.indexOf(ch, start);
                    if (end != -1) {
                        return new int[] {start, end - start};
                    }
                }
            }
        } catch (StringIndexOutOfBoundsException e) {}
        return null;
    }

    public int read() throws IOException {
        int read = in.read();

        if (read == -1) {
            return read;
        }

        if (read == 0x003C) {
            // <
            return START_TAG_INT;
        }

        if (read == 0x003E) {
            // >
            return END_TAG_INT;
        }

        if (read == 38) { //'&'
            in.mark(10);

            for (int len = 0; len < 10; len++) {

                read = in.read();

                if (read == 59) { //;
                    /*
                     * Found entity
                     */
                    return processEntity(new String(buffer, 0, len));
                }

                if (read == -1
                        || read == 0x20
                        || read == 0x9 || read == 0xD || read == 0xA) {
                    /*
                     * Couldn't find entity's end before EOF
                     * or this is not a valid entry.
                     */
                    break;
                }

                buffer[len] = (char) read;
            }

            /*
             * Couldn't find entity, so reset stream from position after entity
             */
            read = 38;
            in.reset();
        }

        return read;
    }

    private int processEntity(final String entityName) {

        Object entityValue;

        entityValue = ENTITIES.get(entityName);

        if (entityValue != null) {
            /*
             * Entity found in the main table
             */
            return ((Integer) entityValue).intValue();
        }

        if (customEntities != null) {
            entityValue = customEntities.get(entityName);

            if (entityValue != null) {
                /*
                 * found in document's table
                 */
                return ((Integer) entityValue).intValue();
            }
        }

        /*
         * Is it a number?
         * &#1234;
         * &#xABCD;
         */

        if (entityName.length() > 0 && entityName.charAt(0) == '#') {
            if (entityName.length() > 3 && (entityName.charAt(1) == 'x' || entityName.charAt(1) == 'X')) {
                //at least x or X and one digit
                /*
                 * &#xABCD;
                 */
                try {
                    return Integer.parseInt(entityName.substring(2), 16);
                } catch (NumberFormatException e) {}
            } else if (entityName.length() > 2) { //at least one digit
                /*
                 * &1234;
                 */
                try {
                    return Integer.parseInt(entityName.substring(1));
                } catch (NumberFormatException e) {}
            }
        }

        /*
         * The entity couldn't be read, so a default value
         * (the null char for now) will be returned.
         */
        return 0;
    }

    public int read(char[] cbuf, int off, int len) throws IOException {

        int read = 0;

        for (int i = 0; i < len; i++) {
            read = read();

            if (read == -1) {
                /*
                 * EOF
                 */
                return i;
            }

            cbuf[i + off] = (char) read;
        }

        return len;
    }

    public void close() throws IOException {
        in.close();
    }

    static {
        /*
         * HTML Entities. See the link
         * http://en.wikipedia.org/wiki/List_of_XML_and_HTML_character_entity_references
         */

        /*
         * HTMLspecial
         */
        ENTITIES.put("quot",    new Integer('"'));
        ENTITIES.put("amp",     new Integer('&'));
        ENTITIES.put("apos",    new Integer('\''));
        ENTITIES.put("lt",      new Integer('<'));
        ENTITIES.put("gt",      new Integer('>'));

        /*
         * HTMLlat1
         */
        ENTITIES.put("nbsp",    new Integer('\u00A0'));
        ENTITIES.put("iexcl",   new Integer('\u00A1'));
        ENTITIES.put("cent",    new Integer('\u00A2'));
        ENTITIES.put("pound",   new Integer('\u00A3'));
        ENTITIES.put("curren",  new Integer('\u00A4'));
        ENTITIES.put("yen",     new Integer('\u00A5'));
        ENTITIES.put("brvbar",  new Integer('\u00A6'));
        ENTITIES.put("sect",    new Integer('\u00A7'));
        ENTITIES.put("uml",     new Integer('\u00A8'));
        ENTITIES.put("copy",    new Integer('\u00A9'));
        ENTITIES.put("ordf",    new Integer('\u00AA'));
        ENTITIES.put("laquo",   new Integer('\u00AB'));
        ENTITIES.put("not",     new Integer('\u00AC'));
        ENTITIES.put("shy",     new Integer('\u00AD'));
        ENTITIES.put("reg",     new Integer('\u00AE'));
        ENTITIES.put("macr",    new Integer('\u00AF'));

        ENTITIES.put("deg",     new Integer('\u00B0'));
        ENTITIES.put("plusmn",  new Integer('\u00B1'));
        ENTITIES.put("sup2",    new Integer('\u00B2'));
        ENTITIES.put("sup3",    new Integer('\u00B3'));
        ENTITIES.put("acute",   new Integer('\u00B4'));
        ENTITIES.put("micro",   new Integer('\u00B5'));
        ENTITIES.put("para",    new Integer('\u00B6'));
        ENTITIES.put("middot",  new Integer('\u00B7'));
        ENTITIES.put("cedil",   new Integer('\u00B8'));
        ENTITIES.put("sup1",    new Integer('\u00B9'));
        ENTITIES.put("ordm",    new Integer('\u00BA'));
        ENTITIES.put("raquo",   new Integer('\u00BB'));
        ENTITIES.put("frac14",  new Integer('\u00BC'));
        ENTITIES.put("frac12",  new Integer('\u00BD'));
        ENTITIES.put("frac34",  new Integer('\u00BE'));
        ENTITIES.put("iquest",  new Integer('\u00BF'));

        ENTITIES.put("Agrave",  new Integer('\u00C0'));
        ENTITIES.put("Aacute",  new Integer('\u00C1'));
        ENTITIES.put("Acirc",   new Integer('\u00C2'));
        ENTITIES.put("Atilde",  new Integer('\u00C3'));
        ENTITIES.put("Auml",    new Integer('\u00C4'));
        ENTITIES.put("Aring",   new Integer('\u00C5'));
        ENTITIES.put("AElig",   new Integer('\u00C6'));
        ENTITIES.put("Ccedil",  new Integer('\u00C7'));
        ENTITIES.put("Egrave",  new Integer('\u00C8'));
        ENTITIES.put("Eacute",  new Integer('\u00C9'));
        ENTITIES.put("Ecirc",   new Integer('\u00CA'));
        ENTITIES.put("Euml",    new Integer('\u00CB'));
        ENTITIES.put("Igrave",  new Integer('\u00CC'));
        ENTITIES.put("Iacute",  new Integer('\u00CD'));
        ENTITIES.put("Icirc",   new Integer('\u00CE'));
        ENTITIES.put("Iuml",    new Integer('\u00CF'));

        ENTITIES.put("ETH",     new Integer('\u00D0'));
        ENTITIES.put("Ntilde",  new Integer('\u00D1'));
        ENTITIES.put("Ograve",  new Integer('\u00D2'));
        ENTITIES.put("Oacute",  new Integer('\u00D3'));
        ENTITIES.put("Ocirc",   new Integer('\u00D4'));
        ENTITIES.put("Otilde",  new Integer('\u00D5'));
        ENTITIES.put("Ouml",    new Integer('\u00D6'));
        ENTITIES.put("times",   new Integer('\u00D7'));
        ENTITIES.put("Oslash",  new Integer('\u00D8'));
        ENTITIES.put("Ugrave",  new Integer('\u00D9'));
        ENTITIES.put("Uacute",  new Integer('\u00DA'));
        ENTITIES.put("Ucirc",   new Integer('\u00DB'));
        ENTITIES.put("Uuml",    new Integer('\u00DC'));
        ENTITIES.put("Yacute",  new Integer('\u00DD'));
        ENTITIES.put("THORN",   new Integer('\u00DE'));
        ENTITIES.put("szlig",   new Integer('\u00DF'));

        ENTITIES.put("agrave",  new Integer('\u00E0'));
        ENTITIES.put("aacute",  new Integer('\u00E1'));
        ENTITIES.put("acirc",   new Integer('\u00E2'));
        ENTITIES.put("atilde",  new Integer('\u00E3'));
        ENTITIES.put("auml",    new Integer('\u00E4'));
        ENTITIES.put("aring",   new Integer('\u00E5'));
        ENTITIES.put("aelig",   new Integer('\u00E6'));
        ENTITIES.put("ccedil",  new Integer('\u00E7'));
        ENTITIES.put("egrave",  new Integer('\u00E8'));
        ENTITIES.put("eacute",  new Integer('\u00E9'));
        ENTITIES.put("ecirc",   new Integer('\u00EA'));
        ENTITIES.put("euml",    new Integer('\u00EB'));
        ENTITIES.put("igrave",  new Integer('\u00EC'));
        ENTITIES.put("iacute",  new Integer('\u00ED'));
        ENTITIES.put("icirc",   new Integer('\u00EE'));
        ENTITIES.put("iuml",    new Integer('\u00EF'));

        ENTITIES.put("eth",     new Integer('\u00F0'));
        ENTITIES.put("ntilde",  new Integer('\u00F1'));
        ENTITIES.put("ograve",  new Integer('\u00F2'));
        ENTITIES.put("oacute",  new Integer('\u00F3'));
        ENTITIES.put("ocirc",   new Integer('\u00F4'));
        ENTITIES.put("otilde",  new Integer('\u00F5'));
        ENTITIES.put("ouml",    new Integer('\u00F6'));
        ENTITIES.put("divide",  new Integer('\u00F7'));
        ENTITIES.put("oslash",  new Integer('\u00F8'));
        ENTITIES.put("ugrave",  new Integer('\u00F9'));
        ENTITIES.put("uacute",  new Integer('\u00FA'));
        ENTITIES.put("ucirc",   new Integer('\u00FB'));
        ENTITIES.put("uuml",    new Integer('\u00FC'));
        ENTITIES.put("yacute",  new Integer('\u00FD'));
        ENTITIES.put("thorn",   new Integer('\u00FE'));
        ENTITIES.put("yuml",    new Integer('\u00FF'));

        /*
         * HTMLspecial
         */
        ENTITIES.put("OElig",   new Integer('\u0152'));
        ENTITIES.put("oelig",   new Integer('\u0153'));
        ENTITIES.put("Scaron",  new Integer('\u0160'));
        ENTITIES.put("scaron",  new Integer('\u0161'));
        ENTITIES.put("Yuml",    new Integer('\u0178'));
        ENTITIES.put("fnof",    new Integer('\u0192'));
        ENTITIES.put("circ",    new Integer('\u02C6'));
        ENTITIES.put("tilde",   new Integer('\u02DC'));

        /*
         * HTMLsymbols
         */
        ENTITIES.put("Alpha",   new Integer('\u0391'));
        ENTITIES.put("Beta",    new Integer('\u0392'));
        ENTITIES.put("Gamma",   new Integer('\u0393'));
        ENTITIES.put("Delta",   new Integer('\u0394'));
        ENTITIES.put("Epsilon", new Integer('\u0395'));
        ENTITIES.put("Zeta",    new Integer('\u0396'));
        ENTITIES.put("Eta",     new Integer('\u0397'));
        ENTITIES.put("Theta",   new Integer('\u0398'));
        ENTITIES.put("Iota",    new Integer('\u0399'));
        ENTITIES.put("Kappa",   new Integer('\u039A'));
        ENTITIES.put("Lambda",  new Integer('\u039B'));
        ENTITIES.put("Mu",      new Integer('\u039C'));
        ENTITIES.put("Nu",      new Integer('\u039D'));
        ENTITIES.put("Xi",      new Integer('\u039E'));
        ENTITIES.put("Omicron", new Integer('\u039F'));
        ENTITIES.put("Pi",      new Integer('\u03A0'));
        ENTITIES.put("Rho",     new Integer('\u03A1'));
        ENTITIES.put("Sigma",   new Integer('\u03A3'));
        ENTITIES.put("Tau",     new Integer('\u03A4'));
        ENTITIES.put("Upsilon", new Integer('\u03A5'));
        ENTITIES.put("Phi",     new Integer('\u03A6'));
        ENTITIES.put("Chi",     new Integer('\u03A7'));
        ENTITIES.put("Psi",     new Integer('\u03A8'));
        ENTITIES.put("Omega",   new Integer('\u03A9'));

        ENTITIES.put("alpha",   new Integer('\u03B1'));
        ENTITIES.put("beta",    new Integer('\u03B2'));
        ENTITIES.put("gamma",   new Integer('\u03B3'));
        ENTITIES.put("delta",   new Integer('\u03B4'));
        ENTITIES.put("epsilon", new Integer('\u03B5'));
        ENTITIES.put("zeta",    new Integer('\u03B6'));
        ENTITIES.put("eta",     new Integer('\u03B7'));
        ENTITIES.put("theta",   new Integer('\u03B8'));
        ENTITIES.put("iota",    new Integer('\u03B9'));
        ENTITIES.put("kappa",   new Integer('\u03BA'));
        ENTITIES.put("lambda",  new Integer('\u03BB'));
        ENTITIES.put("mu",      new Integer('\u03BC'));
        ENTITIES.put("nu",      new Integer('\u03BD'));
        ENTITIES.put("xi",      new Integer('\u03BE'));
        ENTITIES.put("omicron", new Integer('\u03BF'));
        ENTITIES.put("pi",      new Integer('\u03C0'));
        ENTITIES.put("rho",     new Integer('\u03C1'));
        ENTITIES.put("sigmaf",  new Integer('\u03C2'));
        ENTITIES.put("sigma",   new Integer('\u03C3'));
        ENTITIES.put("tau",     new Integer('\u03C4'));
        ENTITIES.put("upsilon", new Integer('\u03C5'));
        ENTITIES.put("phi",     new Integer('\u03C6'));
        ENTITIES.put("chi",     new Integer('\u03C7'));
        ENTITIES.put("psi",     new Integer('\u03C8'));
        ENTITIES.put("omega",   new Integer('\u03C9'));
        ENTITIES.put("thetasym",new Integer('\u03D1'));
        ENTITIES.put("upsih",   new Integer('\u03D2'));
        ENTITIES.put("piv",     new Integer('\u03D6'));

        /*
         * HTMLspecial
         */
        ENTITIES.put("ensp",    new Integer('\u2002'));
        ENTITIES.put("emsp",    new Integer('\u2003'));
        ENTITIES.put("thinsp",  new Integer('\u2009'));
        ENTITIES.put("ndash",   new Integer('\u2013'));
        ENTITIES.put("mdash",   new Integer('\u2014'));
        ENTITIES.put("lsquo",   new Integer('\u2018'));
        ENTITIES.put("rsquo",   new Integer('\u2019'));
        ENTITIES.put("sbquo",   new Integer('\u201A'));
        ENTITIES.put("ldquo",   new Integer('\u201C'));
        ENTITIES.put("rdquo",   new Integer('\u201D'));
        ENTITIES.put("bdquo",   new Integer('\u201E'));
        ENTITIES.put("dagger",  new Integer('\u2020'));
        ENTITIES.put("Dagger",  new Integer('\u2021'));
        ENTITIES.put("bull",    new Integer('\u2022'));
        ENTITIES.put("hellip",  new Integer('\u2026'));
        ENTITIES.put("permil",  new Integer('\u2030'));
        ENTITIES.put("prime",   new Integer('\u2032'));
        ENTITIES.put("Prime",   new Integer('\u2033'));
        ENTITIES.put("lsaquo",  new Integer('\u2039'));
        ENTITIES.put("rsaquo",  new Integer('\u203A'));

        /*
         *HTMLsymbol
         */
        ENTITIES.put("oline",   new Integer('\u203E'));
        ENTITIES.put("frasl",   new Integer('\u2044'));

        /*
         * HTMLspecial
         */
        ENTITIES.put("euro",    new Integer('\u20AC'));

        /*
         * HTMLsymbol
         */
        ENTITIES.put("image",   new Integer('\u2111'));
        ENTITIES.put("weierp",  new Integer('\u2118'));
        ENTITIES.put("real",    new Integer('\u211C'));
        ENTITIES.put("trade",   new Integer('\u2122'));
        ENTITIES.put("alefsym", new Integer('\u2135'));
        ENTITIES.put("larr",    new Integer('\u2190'));
        ENTITIES.put("uarr",    new Integer('\u2191'));
        ENTITIES.put("rarr",    new Integer('\u2192'));
        ENTITIES.put("darr",    new Integer('\u2193'));
        ENTITIES.put("harr",    new Integer('\u2194'));
        ENTITIES.put("crarr",   new Integer('\u21B5'));
        ENTITIES.put("lArr",    new Integer('\u21D0'));
        ENTITIES.put("uArr",    new Integer('\u21D1'));
        ENTITIES.put("rArr",    new Integer('\u21D2'));
        ENTITIES.put("dArr",    new Integer('\u21D3'));
        ENTITIES.put("hArr",    new Integer('\u21D4'));
        ENTITIES.put("forall",  new Integer('\u2200'));
        ENTITIES.put("part",    new Integer('\u2202'));
        ENTITIES.put("exist",   new Integer('\u2203'));
        ENTITIES.put("empty",   new Integer('\u2205'));
        ENTITIES.put("nabla",   new Integer('\u2207'));
        ENTITIES.put("isin",    new Integer('\u2208'));
        ENTITIES.put("notin",   new Integer('\u2209'));
        ENTITIES.put("ni",      new Integer('\u220B'));
        ENTITIES.put("prod",    new Integer('\u220F'));
        ENTITIES.put("sum",     new Integer('\u2211'));
        ENTITIES.put("minus",   new Integer('\u2212'));
        ENTITIES.put("lowast",  new Integer('\u2217'));
        ENTITIES.put("radic",   new Integer('\u221A'));
        ENTITIES.put("prop",    new Integer('\u221D'));
        ENTITIES.put("infin",   new Integer('\u221E'));
        ENTITIES.put("ang",     new Integer('\u2220'));
        ENTITIES.put("and",     new Integer('\u2227'));
        ENTITIES.put("or",      new Integer('\u2228'));
        ENTITIES.put("cap",     new Integer('\u2229'));
        ENTITIES.put("cup",     new Integer('\u222A'));
        ENTITIES.put("int",     new Integer('\u222B'));
        ENTITIES.put("there4",  new Integer('\u2234'));
        ENTITIES.put("sim",     new Integer('\u223C'));
        ENTITIES.put("cong",    new Integer('\u2245'));
        ENTITIES.put("asymp",   new Integer('\u2248'));
        ENTITIES.put("ne",      new Integer('\u2260'));
        ENTITIES.put("equiv",   new Integer('\u2261'));
        ENTITIES.put("le",      new Integer('\u2264'));
        ENTITIES.put("ge",      new Integer('\u2265'));
        ENTITIES.put("sub",     new Integer('\u2282'));
        ENTITIES.put("sup",     new Integer('\u2283'));
        ENTITIES.put("nsub",    new Integer('\u2284'));
        ENTITIES.put("sube",    new Integer('\u2286'));
        ENTITIES.put("supe",    new Integer('\u2287'));
        ENTITIES.put("oplus",   new Integer('\u2295'));
        ENTITIES.put("otimes",  new Integer('\u2297'));
        ENTITIES.put("perp",    new Integer('\u22A5'));
        ENTITIES.put("sdot",    new Integer('\u22C5'));
        ENTITIES.put("lceil",   new Integer('\u2308'));
        ENTITIES.put("rceil",   new Integer('\u2309'));
        ENTITIES.put("lfloor",  new Integer('\u230A'));
        ENTITIES.put("rfloor",  new Integer('\u230B'));
        ENTITIES.put("lang",    new Integer('\u2329'));
        ENTITIES.put("rang",    new Integer('\u232A'));
        ENTITIES.put("loz",     new Integer('\u25CA'));
        ENTITIES.put("spades",  new Integer('\u2660'));
        ENTITIES.put("clubs",   new Integer('\u2663'));
        ENTITIES.put("hearts",  new Integer('\u2665'));
        ENTITIES.put("diams",   new Integer('\u2666'));
    }
}