/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.io.decoders;

/**
 *
 * @author albus
 */

public interface Encodings {
    /*
     * See http://msdn.microsoft.com/en-us/library/aa752010(v=VS.85).aspx
     * --------------------------------------------------------------------
     */

    /*
     * UTF-8
     * --------------------------------------------------------------------
     */
    public static final String[] UTF_8_ALIASES = new String[] {
        "utf-8"
    };

    public static final String UTF_8 = UTF_8_ALIASES[0];

    /*
     * US ASCII
     * --------------------------------------------------------------------
     */
    public static final String[] ASCII_ALIASES = new String[] {
        "us-ascii", "ascii",
        "ISO646-US", "ISO_646.IRV:1991",
        "ISO-IR-6", "iso-ir-6us", "ANSI_X3.4-1968", "ANSI_X3.4-1986",
        "IBM367", "US", "CSASCII"
    };

    public static final String ASCII = ASCII_ALIASES[0];

    /*
     * ISO-8859
     * --------------------------------------------------------------------
     */

    /*
     * Western Europe
     */
    public static final String[] ISO_8859_1_ALIASES = new String[] {
        "iso-8859-1",
        "cp819", "csISOLatin1", "ibm819",
        "iso_8859-1", "iso_8859-1:1987", "iso8859-1",
        "iso-ir-100", "l1", "latin1"
    };

    /*
     * Western and Central Europe
     */
    public static final String[] ISO_8859_2_ALIASES = new String[] {
        "iso-8859-2", "csISOLatin2", "iso_8859-2",
        "iso_8859-2:1987", "iso8859-2", "iso-ir-101", "l2", "latin2"
    };

    /*
     * Latin 3
     */
    public static final String[] ISO_8859_3_ALIASES = new String[] {
        "iso-8859-3",
        "csISOLatin3", "ISO_8859-3", "ISO_8859-3:1988",
        "iso-ir-109", "l3", "latin3"
    };

    public static final String[] ISO_8859_4_ALIASES = new String[] {
        "iso-8859-4",
        "csISOLatin4", "ISO_8859-4",
        "ISO_8859-4:1988", "iso-ir-110", "l4", "latin4"
    };

    /*
     * Cyrillic
     */
    public static final String[] ISO_8859_5_ALIASES = new String[] {
        "iso-8859-5", "csISOLatin5", "csISOLatinCyrillic",
        "cyrillic", "ISO_8859-5", "ISO_8859-5:1988", "iso-ir-144", "l5"
    };

    /*
     * Arabic is not included
     */

    /*
     * Greek
     */
    public static final String[] ISO_8859_7_ALIASES = new String[] {
        "iso-8859-7", "csISOLatinGreek", "ECMA-118", "ELOT_928",
        "greek", "greek8", "ISO_8859-7", "ISO_8859-7:1987", "iso-ir-126"
    };

    /*
     * Hebrew is not included
     */

    /*
     * Turkish
     */
    public static final String[] ISO_8859_9_ALIASES = new String[] {
        "iso-8859-9",
        "csISOLatin5", "ISO_8859-9", "ISO_8859-9:1989",
        "iso-ir-148", "l5", "latin5"
    };

    /*
     * Nordic / Icelandic
     */
    public static final String[] ISO_8859_10_ALIASES = new String[] {
        "iso-8859-10",
        "iso_8859-10",
        "ISO_8859-10:1992", "ISO-IR-157",
        "LATIN6", "L6", "CSISOLATIN6",
        "ISO8859-10"
    };

    /*
     * Thai is not included
     */

    /*
     * Don't know much about ISO-8859-12
     */

    /*
     * Baltic + Polish
     */
    public static final String[] ISO_8859_13_ALIASES = new String[] {
        "ISO-8859-13",
        "ISO_8859-13",
        "ISO-IR-179", "LATIN7", "L7", "ISO8859"
    };

    /*
     * Celtic (Irish Gaelic, Scottish, Welsh)
     */
    public static final String[] ISO_8859_14_ALIASES = new String[] {
        "ISO-8859-14", "ISO_8859-14", "ISO_8859-14:1998",
        "ISO-IR-199", "LATIN8", "L8",
        "ISO-CELTIC", "ISO8859-14"
    };
    
    /*
     * ISO-8859-1 + Euro Sign a.k.a Latin 9
     */
    public static final String[] ISO_8859_15_ALIASES = new String[] {
        "iso-8859-15",
        "csISOLatin9", "ISO_8859-15", "l9", "latin9"
    };

    /*
     * Central, Eastern and Southern European languages
     * (Polish, Czech, Slovak, Serbian, Croatian, Slovene,
     * Hungarian, Albanian, Romanian, German, Italian)
     */
    public static final String[] ISO_8859_16_ALIASES = new String[] {
        "ISO-8859-16", "ISO_8859-16", "ISO_8859-16:2001",
        "ISO-IR-226",
        "LATIN10", "L10", "ISO8859-16"
    };

    public static final String ISO_8859_1 = ISO_8859_1_ALIASES[0];
    public static final String ISO_8859_2 = ISO_8859_2_ALIASES[0];
    public static final String ISO_8859_3 = ISO_8859_3_ALIASES[0];
    public static final String ISO_8859_4 = ISO_8859_4_ALIASES[0];
    public static final String ISO_8859_5 = ISO_8859_5_ALIASES[0];
    public static final String ISO_8859_7 = ISO_8859_7_ALIASES[0];
    public static final String ISO_8859_9 = ISO_8859_9_ALIASES[0];
    public static final String ISO_8859_10 = ISO_8859_10_ALIASES[0];
    public static final String ISO_8859_13 = ISO_8859_13_ALIASES[0];
    public static final String ISO_8859_14 = ISO_8859_14_ALIASES[0];
    public static final String ISO_8859_15 = ISO_8859_15_ALIASES[0];
    public static final String ISO_8859_16 = ISO_8859_16_ALIASES[0];

    /*
     * Windows
     * --------------------------------------------------------------------
     */

    /*
     * Central European languages that use Latin script,
     * (Polish, Czech, Slovak, Hungarian, Slovene, Serbian,
     * Croatian, Romanian and Albanian)
     */
    public static final String[] WINDOWS_1250_ALIASES =
            new String[] {
        "windows-1250", "x-cp1250", "cp1250"
    };

    /*
     * Cyrillic
     */
    public static final String[] WINDOWS_1251_ALIASES = new String[] {
        "windows-1251", "x-cp1251", "cp1251"
    };

    /*
     * Western languages
     */
    public static final String[] WINDOWS_1252_ALIASES = new String[] {
        "windows-1252", "x-cp1252", "cp1252"
    };


    /*
     * Greek
     */
    public static final String[] WINDOWS_1253_ALIASES = new String[] {
        "windows-1253", "x-cp1253", "cp1253"
    };

    /*
     * Turkish
     */
    public static final String[] WINDOWS_1254_ALIASES = new String[] {
        "windows-1254"
    };

    /*
     * Hebrew is not present
     */

    /*
     * Arabic is not present
     */

    /*
     * Baltic
     */
    public static final String[] WINDOWS_1257_ALIASES = new String[] {
        "windows-1257", "x-cp1257", "cp1257"
    };

    /*
     * Vietnamese is not present
     */

    /*
     * Cyrrilic KOI
     * --------------------------------------------------------------------
     */
    public static final String[] KOI8_R_ALIASES = new String[] {
        "koi8-r", "csKOI8R", "koi", "koi8", "koi8r"
    };

    public static final String[] KOI8_RU_ALIASES = new String[] {
        "koi8-ru"
    };

    public static final String[] KOI8_U_ALIASES = new String[] {
        "koi8-u"
    };

    public static final String KOI8_R  = KOI8_R_ALIASES[0];
    public static final String KOI8_RU = KOI8_RU_ALIASES[0];
    public static final String KOI8_U  = KOI8_U_ALIASES[0];

    /**
     * Default encoding
     */
    public static final String DEFAULT = UTF_8;
}