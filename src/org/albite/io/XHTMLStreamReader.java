/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.io;

import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

/**
 *
 * @author albus
 */
public class XhtmlStreamReader extends Reader implements HTMLSubstitues {

    private static final Hashtable entities = new Hashtable(200);

    private final AlbiteStreamReader in;
    private final char[] buffer = new char[10];
    private Hashtable customEntities;

    public XhtmlStreamReader(final AlbiteStreamReader in) throws IOException {
        this.in = in;

        /*
         * Read xml decl
         */
        xmldecl();
        
        /*
         * Read doc decl
         */
        doctypedecl();
    }

    private void xmldecl() throws IOException {
        /*
         * Try to read the xmldecl
         */
        in.mark(200);

        try {
            char[] buf = new char[199];
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
                System.out.println("xmldecl: {" + xmldecl + "}");

                final String enc = "encoding=\"";

                int start = xmldecl.indexOf(enc);

                if (start == -1) {
                    in.reset();
                    return;
                }

                int end = xmldecl.indexOf('\"', start + enc.length());

                final String encoding =
                        xmldecl.substring(start + enc.length(), end);

                System.out.println("Encoding: {" + encoding + "}");

                try {
                    in.setEncoding(encoding);
                } catch (UnsupportedEncodingException e) {
                    /*
                     * Do nothing: the reader will continue with
                     * its current settings
                     */
                }

                in.reset();
                skipz(xmldecl.length());
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
        in.mark(2048);

        try {
            char[] buf = new char[2047];
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
                    skipz(dend + 1);
                }

                int doptend = ddecl.indexOf(']', doptstart + 1);
                dend = ddecl.indexOf('>', doptend + 1);

                if (dend == -1) {
                    in.reset();
                    return;
                }

                final String intdecl = ddecl.substring(doptstart, doptend + 1);

                ddecl = ddecl.substring(dstart, dend + 1);

                System.out.println("DOC: {" + ddecl + "}");
                System.out.println("INTDECL: {" + intdecl + "}");

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
                        System.out.println("ENT {" + intdecl.substring(entstart, entend) + "}");

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

                        System.out.println("ENT name: {" + entityName + "}, value: {" + entityValue +"}");

                        int entityIntVal = processEntity(entityValue);
                        if (entityIntVal != 0) {
                            /*
                             * add the entity
                             */
                            if (customEntities == null) {
                                customEntities = new Hashtable(20);
                            }
                            customEntities.put(entityName, new Integer(entityIntVal));
                            System.out.println(entityName + " -> " + entityIntVal);
                        }
//                        customEntities.put(entityName, lock);
                    } catch (StringIndexOutOfBoundsException e) {
                        /*
                         * Just skip this entity
                         */
                        continue;
                    }
                }

                in.reset();
                skipz(dend + 1);
            } else {
                in.reset();
            }
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
            in.reset();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            in.reset();
        }
    }

    private void skipz(int left) throws IOException {
        while (left > 0) {
            left -= in.skip(left);
        }
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

        System.out.println("Processing entity: {" + entityName + "}");
        Object entityValue;

        entityValue = entities.get(entityName);

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

        if (entityName.charAt(0) == '#') {
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
        entities.put("quot",    new Integer('"'));
        entities.put("amp",     new Integer('&'));
        entities.put("apos",    new Integer('\''));
        entities.put("lt",      new Integer('<'));
        entities.put("gt",      new Integer('>'));

        /*
         * HTMLlat1
         */
        entities.put("nbsp",    new Integer('\u00A0'));
        entities.put("iexcl",   new Integer('\u00A1'));
        entities.put("cent",    new Integer('\u00A2'));
        entities.put("pound",   new Integer('\u00A3'));
        entities.put("curren",  new Integer('\u00A4'));
        entities.put("yen",     new Integer('\u00A5'));
        entities.put("brvbar",  new Integer('\u00A6'));
        entities.put("sect",    new Integer('\u00A7'));
        entities.put("uml",     new Integer('\u00A8'));
        entities.put("copy",    new Integer('\u00A9'));
        entities.put("ordf",    new Integer('\u00AA'));
        entities.put("laquo",   new Integer('\u00AB'));
        entities.put("not",     new Integer('\u00AC'));
        entities.put("shy",     new Integer('\u00AD'));
        entities.put("reg",     new Integer('\u00AE'));
        entities.put("macr",    new Integer('\u00AF'));

        entities.put("deg",     new Integer('\u00B0'));
        entities.put("plusmn",  new Integer('\u00B1'));
        entities.put("sup2",    new Integer('\u00B2'));
        entities.put("sup3",    new Integer('\u00B3'));
        entities.put("acute",   new Integer('\u00B4'));
        entities.put("micro",   new Integer('\u00B5'));
        entities.put("para",    new Integer('\u00B6'));
        entities.put("middot",  new Integer('\u00B7'));
        entities.put("cedil",   new Integer('\u00B8'));
        entities.put("sup1",    new Integer('\u00B9'));
        entities.put("ordm",    new Integer('\u00BA'));
        entities.put("raquo",   new Integer('\u00BB'));
        entities.put("frac14",  new Integer('\u00BC'));
        entities.put("frac12",  new Integer('\u00BD'));
        entities.put("frac34",  new Integer('\u00BE'));
        entities.put("iquest",  new Integer('\u00BF'));

        entities.put("Agrave",  new Integer('\u00C0'));
        entities.put("Aacute",  new Integer('\u00C1'));
        entities.put("Acirc",   new Integer('\u00C2'));
        entities.put("Atilde",  new Integer('\u00C3'));
        entities.put("Auml",    new Integer('\u00C4'));
        entities.put("Aring",   new Integer('\u00C5'));
        entities.put("AElig",   new Integer('\u00C6'));
        entities.put("Ccedil",  new Integer('\u00C7'));
        entities.put("Egrave",  new Integer('\u00C8'));
        entities.put("Eacute",  new Integer('\u00C9'));
        entities.put("Ecirc",   new Integer('\u00CA'));
        entities.put("Euml",    new Integer('\u00CB'));
        entities.put("Igrave",  new Integer('\u00CC'));
        entities.put("Iacute",  new Integer('\u00CD'));
        entities.put("Icirc",   new Integer('\u00CE'));
        entities.put("Iuml",    new Integer('\u00CF'));

        entities.put("ETH",     new Integer('\u00D0'));
        entities.put("Ntilde",  new Integer('\u00D1'));
        entities.put("Ograve",  new Integer('\u00D2'));
        entities.put("Oacute",  new Integer('\u00D3'));
        entities.put("Ocirc",   new Integer('\u00D4'));
        entities.put("Otilde",  new Integer('\u00D5'));
        entities.put("Ouml",    new Integer('\u00D6'));
        entities.put("times",   new Integer('\u00D7'));
        entities.put("Oslash",  new Integer('\u00D8'));
        entities.put("Ugrave",  new Integer('\u00D9'));
        entities.put("Uacute",  new Integer('\u00DA'));
        entities.put("Ucirc",   new Integer('\u00DB'));
        entities.put("Uuml",    new Integer('\u00DC'));
        entities.put("Yacute",  new Integer('\u00DD'));
        entities.put("THORN",   new Integer('\u00DE'));
        entities.put("szlig",   new Integer('\u00DF'));

        entities.put("agrave",  new Integer('\u00E0'));
        entities.put("aacute",  new Integer('\u00E1'));
        entities.put("acirc",   new Integer('\u00E2'));
        entities.put("atilde",  new Integer('\u00E3'));
        entities.put("auml",    new Integer('\u00E4'));
        entities.put("aring",   new Integer('\u00E5'));
        entities.put("aelig",   new Integer('\u00E6'));
        entities.put("ccedil",  new Integer('\u00E7'));
        entities.put("egrave",  new Integer('\u00E8'));
        entities.put("eacute",  new Integer('\u00E9'));
        entities.put("ecirc",   new Integer('\u00EA'));
        entities.put("euml",    new Integer('\u00EB'));
        entities.put("igrave",  new Integer('\u00EC'));
        entities.put("iacute",  new Integer('\u00ED'));
        entities.put("icirc",   new Integer('\u00EE'));
        entities.put("iuml",    new Integer('\u00EF'));

        entities.put("eth",     new Integer('\u00F0'));
        entities.put("ntilde",  new Integer('\u00F1'));
        entities.put("ograve",  new Integer('\u00F2'));
        entities.put("oacute",  new Integer('\u00F3'));
        entities.put("ocirc",   new Integer('\u00F4'));
        entities.put("otilde",  new Integer('\u00F5'));
        entities.put("ouml",    new Integer('\u00F6'));
        entities.put("divide",  new Integer('\u00F7'));
        entities.put("oslash",  new Integer('\u00F8'));
        entities.put("ugrave",  new Integer('\u00F9'));
        entities.put("uacute",  new Integer('\u00FA'));
        entities.put("ucirc",   new Integer('\u00FB'));
        entities.put("uuml",    new Integer('\u00FC'));
        entities.put("yacute",  new Integer('\u00FD'));
        entities.put("thorn",   new Integer('\u00FE'));
        entities.put("yuml",    new Integer('\u00FF'));

        /*
         * HTMLspecial
         */
        entities.put("OElig",   new Integer('\u0152'));
        entities.put("oelig",   new Integer('\u0153'));
        entities.put("Scaron",  new Integer('\u0160'));
        entities.put("scaron",  new Integer('\u0161'));
        entities.put("Yuml",    new Integer('\u0178'));
        entities.put("fnof",    new Integer('\u0192'));
        entities.put("circ",    new Integer('\u02C6'));
        entities.put("tilde",   new Integer('\u02DC'));

        /*
         * HTMLsymbols
         */
        entities.put("Alpha",   new Integer('\u0391'));
        entities.put("Beta",    new Integer('\u0392'));
        entities.put("Gamma",   new Integer('\u0393'));
        entities.put("Delta",   new Integer('\u0394'));
        entities.put("Epsilon", new Integer('\u0395'));
        entities.put("Zeta",    new Integer('\u0396'));
        entities.put("Eta",     new Integer('\u0397'));
        entities.put("Theta",   new Integer('\u0398'));
        entities.put("Iota",    new Integer('\u0399'));
        entities.put("Kappa",   new Integer('\u039A'));
        entities.put("Lambda",  new Integer('\u039B'));
        entities.put("Mu",      new Integer('\u039C'));
        entities.put("Nu",      new Integer('\u039D'));
        entities.put("Xi",      new Integer('\u039E'));
        entities.put("Omicron", new Integer('\u039F'));
        entities.put("Pi",      new Integer('\u03A0'));
        entities.put("Rho",     new Integer('\u03A1'));
        entities.put("Sigma",   new Integer('\u03A3'));
        entities.put("Tau",     new Integer('\u03A4'));
        entities.put("Upsilon", new Integer('\u03A5'));
        entities.put("Phi",     new Integer('\u03A6'));
        entities.put("Chi",     new Integer('\u03A7'));
        entities.put("Psi",     new Integer('\u03A8'));
        entities.put("Omega",   new Integer('\u03A9'));

        entities.put("alpha",   new Integer('\u03B1'));
        entities.put("beta",    new Integer('\u03B2'));
        entities.put("gamma",   new Integer('\u03B3'));
        entities.put("delta",   new Integer('\u03B4'));
        entities.put("epsilon", new Integer('\u03B5'));
        entities.put("zeta",    new Integer('\u03B6'));
        entities.put("eta",     new Integer('\u03B7'));
        entities.put("theta",   new Integer('\u03B8'));
        entities.put("iota",    new Integer('\u03B9'));
        entities.put("kappa",   new Integer('\u03BA'));
        entities.put("lambda",  new Integer('\u03BB'));
        entities.put("mu",      new Integer('\u03BC'));
        entities.put("nu",      new Integer('\u03BD'));
        entities.put("xi",      new Integer('\u03BE'));
        entities.put("omicron", new Integer('\u03BF'));
        entities.put("pi",      new Integer('\u03C0'));
        entities.put("rho",     new Integer('\u03C1'));
        entities.put("sigmaf",  new Integer('\u03C2'));
        entities.put("sigma",   new Integer('\u03C3'));
        entities.put("tau",     new Integer('\u03C4'));
        entities.put("upsilon", new Integer('\u03C5'));
        entities.put("phi",     new Integer('\u03C6'));
        entities.put("chi",     new Integer('\u03C7'));
        entities.put("psi",     new Integer('\u03C8'));
        entities.put("omega",   new Integer('\u03C9'));
        entities.put("thetasym",new Integer('\u03D1'));
        entities.put("upsih",   new Integer('\u03D2'));
        entities.put("piv",     new Integer('\u03D6'));

        /*
         * HTMLspecial
         */
        entities.put("ensp",    new Integer('\u2002'));
        entities.put("emsp",    new Integer('\u2003'));
        entities.put("thinsp",  new Integer('\u2009'));
        entities.put("ndash",   new Integer('\u2013'));
        entities.put("mdash",   new Integer('\u2014'));
        entities.put("lsquo",   new Integer('\u2018'));
        entities.put("rsquo",   new Integer('\u2019'));
        entities.put("sbquo",   new Integer('\u201A'));
        entities.put("ldquo",   new Integer('\u201C'));
        entities.put("rdquo",   new Integer('\u201D'));
        entities.put("bdquo",   new Integer('\u201E'));
        entities.put("dagger",  new Integer('\u2020'));
        entities.put("Dagger",  new Integer('\u2021'));
        entities.put("bull",    new Integer('\u2022'));
        entities.put("hellip",  new Integer('\u2026'));
        entities.put("permil",  new Integer('\u2030'));
        entities.put("prime",   new Integer('\u2032'));
        entities.put("Prime",   new Integer('\u2033'));
        entities.put("lsaquo",  new Integer('\u2039'));
        entities.put("rsaquo",  new Integer('\u203A'));

        /*
         *HTMLsymbol
         */
        entities.put("oline",   new Integer('\u203E'));
        entities.put("frasl",   new Integer('\u2044'));

        /*
         * HTMLspecial
         */
        entities.put("euro",    new Integer('\u20AC'));

        /*
         * HTMLsymbol
         */
        entities.put("image",   new Integer('\u2111'));
        entities.put("weierp",  new Integer('\u2118'));
        entities.put("real",    new Integer('\u211C'));
        entities.put("trade",   new Integer('\u2122'));
        entities.put("alefsym", new Integer('\u2135'));
        entities.put("larr",    new Integer('\u2190'));
        entities.put("uarr",    new Integer('\u2191'));
        entities.put("rarr",    new Integer('\u2192'));
        entities.put("darr",    new Integer('\u2193'));
        entities.put("harr",    new Integer('\u2194'));
        entities.put("crarr",   new Integer('\u21B5'));
        entities.put("lArr",    new Integer('\u21D0'));
        entities.put("uArr",    new Integer('\u21D1'));
        entities.put("rArr",    new Integer('\u21D2'));
        entities.put("dArr",    new Integer('\u21D3'));
        entities.put("hArr",    new Integer('\u21D4'));
        entities.put("forall",  new Integer('\u2200'));
        entities.put("part",    new Integer('\u2202'));
        entities.put("exist",   new Integer('\u2203'));
        entities.put("empty",   new Integer('\u2205'));
        entities.put("nabla",   new Integer('\u2207'));
        entities.put("isin",    new Integer('\u2208'));
        entities.put("notin",   new Integer('\u2209'));
        entities.put("ni",      new Integer('\u220B'));
        entities.put("prod",    new Integer('\u220F'));
        entities.put("sum",     new Integer('\u2211'));
        entities.put("minus",   new Integer('\u2212'));
        entities.put("lowast",  new Integer('\u2217'));
        entities.put("radic",   new Integer('\u221A'));
        entities.put("prop",    new Integer('\u221D'));
        entities.put("infin",   new Integer('\u221E'));
        entities.put("ang",     new Integer('\u2220'));
        entities.put("and",     new Integer('\u2227'));
        entities.put("or",      new Integer('\u2228'));
        entities.put("cap",     new Integer('\u2229'));
        entities.put("cup",     new Integer('\u222A'));
        entities.put("int",     new Integer('\u222B'));
        entities.put("there4",  new Integer('\u2234'));
        entities.put("sim",     new Integer('\u223C'));
        entities.put("cong",    new Integer('\u2245'));
        entities.put("asymp",   new Integer('\u2248'));
        entities.put("ne",      new Integer('\u2260'));
        entities.put("equiv",   new Integer('\u2261'));
        entities.put("le",      new Integer('\u2264'));
        entities.put("ge",      new Integer('\u2265'));
        entities.put("sub",     new Integer('\u2282'));
        entities.put("sup",     new Integer('\u2283'));
        entities.put("nsub",    new Integer('\u2284'));
        entities.put("sube",    new Integer('\u2286'));
        entities.put("supe",    new Integer('\u2287'));
        entities.put("oplus",   new Integer('\u2295'));
        entities.put("otimes",  new Integer('\u2297'));
        entities.put("perp",    new Integer('\u22A5'));
        entities.put("sdot",    new Integer('\u22C5'));
        entities.put("lceil",   new Integer('\u2308'));
        entities.put("rceil",   new Integer('\u2309'));
        entities.put("lfloor",  new Integer('\u230A'));
        entities.put("rfloor",  new Integer('\u230B'));
        entities.put("lang",    new Integer('\u2329'));
        entities.put("rang",    new Integer('\u232A'));
        entities.put("loz",     new Integer('\u25CA'));
        entities.put("spades",  new Integer('\u2660'));
        entities.put("clubs",   new Integer('\u2663'));
        entities.put("hearts",  new Integer('\u2665'));
        entities.put("diams",   new Integer('\u2666'));
    }
}