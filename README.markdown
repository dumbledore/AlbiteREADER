Albite Reader is an open-source e-book reader for *Java Mobile* released under the [Apache 2.0 License][1] and developed by Svetlin Ankov. Works with *EPUB*, *txt* and *(x)html* files. 

# Implemented features

*   anti-aliased bitmap font in four sizes with over 500 glyphs, including latin, cyrillic and greek
*   hyphenation for 29 languages
*   23 character encodings, including UTF-8 and most of ISO-8859 and Windows-125x
*   animated touch-guided scrolling
*   horizontal and vertical scrolling
*   on-touch dictionary look up
*   units conversion: temperature, length, area, ...
*   portrait and landscape view
*   images and basic styling
*   color profiles
*   portable book settings
*   bookmarks
*   table of contents

# System requirements

<table>
  <tr>
    <td>
      Profile
    </td>
    <td>
      MIDP 2.0
    </td>
  </tr>
  <tr>
    <td>
      Configuration
    </td>
    <td>
      CLDC 1.1
    </td>
  </tr>
  <tr>
    <td>
      Optional packages
    </td>
    <td>
      File API (JSR-75)
    </td>
  </tr>
  <tr>
    <td>
      Used heap memory
    </td>
    <td>
      depends on the version
    </td>
  </tr>
</table>

# Features in detail

Here goes a detailed review of all currently implemented features. 

## Bitmap Font

Bitmap font rendering is achieved through my own implementation. The font used, *Droid Serif*, has been pre-rendered in several different sizes: 12px, 14px, 16px, 18px, 24px and 28px. All have been anti-aliased except for the 12px version, which looks much better without it. Supported (or sometimes partially) character ranges are: 

1.  Latin + Latin Supplement (191 chars)
2.  Latin Extended A (128 chars)
3.  Latin Extended B (10 chars)
4.  Spacing Modifier Letters (11 chars)
5.  Greek and Coptic (75 chars)
6.  Cyrillic (94 chars)
7.  Latin Extended Additional (12 chars)
8.  Greek Extended (1 char)
9.  General Punctuation (34 chars)
10. Subscripts and Superscripts (1 char)
11. Currency Symbols (4 chars)
12. Letter-like Symbols (6 chars)
13. Number Forms (4 chars)
14. Mathematical Operators (12 chars)
15. Geometric Shapes (1 chars)

## Hyphenation

Hyphenation for 29 languages: 

1.  Basque
2.  Bulgarian
3.  Catalan
4.  Croatian
5.  Czech
6.  Danish
7.  Dutch
8.  German
9.  Greek
10. English
11. Esperanto
12. Estonian
13. Finnish
14. French
15. Icelandic
16. Indonesian
17. Italian
18. Latin
19. Latvian
20. Polish
21. Portuguese
22. Romanian
23. Russian
24. Slovene
25. Spanish
26. Swedish
27. Turkish
28. Ukrainian
29. Welsh

## Character Encodings

Support for 23 character encodings: 

1.  UTF-8
2.  ASCII
3.  ISO-8859-1
4.  ISO-8859-2
5.  ISO-8859-3
6.  ISO-8859-4
7.  ISO-8859-5
8.  ISO-8859-7
9.  ISO-8859-9
10. ISO-8859-10
11. ISO-8859-13
12. ISO-8859-14
13. ISO-8859-15
14. ISO-8859-16
15. WINDOWS-1250
16. WINDOWS-1251
17. WINDOWS-1252
18. WINDOWS-1253
19. WINDOWS-1254
20. WINDOWS-1257
21. KOI8-R
22. KOI8-RU
23. KOI8-U

## Unit conversion

Conversion between 80 units in 11 unit groups.

1.  Temperature 
    1.  C, K
    2.  F, Degree of Frost
2.  Length 
    1.  mm, cm, dm, m, km
    2.  mil, in, ft, yd, fm, ch, fur, mi, land
    3.  cable, nautical mile
3.  Area 
    1.  mm2, cm2, dc2, m2, a, daa, ha, km2
    2.  sq ft, sq yd, ac, sq mi
4.  Volume 
    1.  mL, L, mm3, cm3, dm3, m3
    2.  fl oz, gi, pt, qt, gal
5.  Mass 
    1.  mg, g, kg, t
    2.  gr, oz, lb, st, cwt, ton
6.  Velocity 
    1.  m/s, km/h
    2.  fps, mph
    3.  kn
7.  Pressure 
    1.  Pa, hPa, kPa, mbar, bar
    2.  mmHg, inHg
    3.  Atm
    4.  psi
8.  Power 
    1.  mW, W, kW, MW
    2.  HPS
9.  Energy 
    1.  Wh, kWh
    2.  J
    3.  cal
10. Linear density 
    1.  DPI
    2.  DPCM
11. Angle 
    1.  rad
    2.  deg

## Images and styling

*   Supports italic, bold and headings
*   Supports text centering
*   Supports images and alt text
*   Supports horizontal rulers
*   Partially supports `pre` text
*   Doesn't support CSS
*   Doesn't support tables
*   Doesn't support links and anchors

## Portable book settings: the <i>.alx</i> and <i>.alb</i> files

After opening a book for the first time, *Albite READER* will create a file with the same name, but with an *.alx* extension. This is a binary file containing your book reading progress. This includes: 

*   Current chapter and position
*   Current hyphenation language
*   Last visited position in every chapter
*   Current encoding of every chapter

If you make any bookmarks in a book, *Albite READER* will also create a *.alb* file. This is a *XML* file which contains your bookmarked text and positions. 

There are several advantages of this technique compared to the usual approach of using the RMS: 

*   You can backup your reading progress
*   All your book settings are preserved upon application update
*   You can continue reading on another phone
*   Your bookmarks are completely accessible so you can open them in a text processor.

# Used resources

## APIs

### kXML2

It's a *BSD*-licensed *XML* pull parser, especially adequate for *Java Mobile*. It's small and unobtrusive. You can read more at [their homepage][2]. 

### AlbiteZIP

It's a *Java Mobile* port of [GNU Classpath's][3] `java.util.zip` package using the [AlbiteRandomReadingFile API][4], which implements a `RandomAccessFile` with writing capabilities stripped away. Reading *EPUB* files relies on *AlbiteZIP* to work with their archives. AlbiteZIP's sources and some sample code are available at [GitHub][5]. 

### AlbiteCharacterDecoder

Provides the ability read characters not natively supported by *Java*'s `InputStreamReader`. The API is based on some code from [libiconv][6]. Sources, examples, tests and binaries are available at [GitHub][7]. 

### AlbiteUnits

A simple java API for converting various physical units. Sources and binaries at [GitHub][8]. 

### ZLTextTeXHyphenator

*Zlibrary*'s hyphenator used in [FBReaderJ. Licensed under GPL 2][9]. 

## Fonts

### Droid Serif

That's the font used on *Android* devices. It's quite nice and is built with the idea of rendering well on small screens and at small sizes. It supports some good amount of glyphs (around 500) and character ranges. It's licensed under the Apache 2.0 license. You can download the Droid fonts in TTF from [here][10]. 

## Hyphenation patterns

The used hyphenation patterns are the ones from [TeX][11]'s site or some modified versions from [FBReaderJ][9]. 

# Current limitations

There are too many limited factors: your phone is limited, Java Mobile is limited and my personal time is limited. So, it points to the conclusion that *Albite READER* is limited, too. I'd like you to know of the current limitations, so that you can decide for yourself if they may stand in your way. 

*   No support for links and anchors (i.e. objects findable by their id)
*   No support for *toc.ncx*. Chapters are made according to the files from the *spine* element in the *opf* file and are titled using a successive number. 
*   No support for accelerometers. I know it's cool, but there are some technical limitations to consider, including battery life when reading for a long time. No to mention that I often tilt the phone to a variety of angles if I am reading in my bed. 
*   No book library. Books are opened through a file browser.
*   Images are shown in separate pages. 
*   Font is available in four sizes only. That's because bitmap fonts are being used. Using SVG/TTF fonts is not only slow (one can cache the glyphs after all), but ugly. Or one may need super-sampling which would make it *terribly* slow. Java Mobile on the other hand, has limited supported for fonts. It supports a maximum of three font sizes, and often limited amount of glyphs. Not to mention the fact that font rendering in Java Mobile is dreadfully on some devices (like my Samsung GT-5230). 
*   No support for subscript and superscript. That's because of the previous limitation.
*   No support for tables.
*   No support for CSS.
*   Books with too many chapters might make the current TOC unusable. For instance, the complete *War and Peace* has more than 300 chapters, so the list of chapters becomes incredibly long and painful to browse. 
*   Languages with right-to-left text direction or hieroglyphic ones are not supported, including Chinese, Japanese, Hebrew, Arabic, etc. 
*   No language localization. In other words, the app is available only in English. 
*   No bold fonts. Instead of using bold fonts, the text is rendered in another color. Using bitmap fonts would significantly (i.e. at least twice) increase the size of both JAR and needed heap memory. 
*   There is a theoretical possibility of corruption of the *.alx* files. More precisely, it can happen if the battery dies exactly when saving the data to the phone or if the app is closed using the red key and it is unable to close fast enough. Thankfully enough, this possibility must be *very* slight in most situations.

 [1]: http://www.apache.org/licenses/LICENSE-2.0.html
 [2]: http://kxml.sourceforge.net/kxml2/
 [3]: http://www.gnu.org/software/classpath/
 [4]: http://github.com/dumbledore/AlbiteRandomReadingFile
 [5]: http://github.com/dumbledore/AlbiteZIP
 [6]: http://www.gnu.org/software/libiconv/
 [7]: http://github.com/dumbledore/AlbiteCharacterDecoder
 [8]: http://github.com/dumbledore/AlbiteUnits
 [9]: http://www.fbreader.org/FBReaderJ/
 [10]: http://github.com/downloads/dumbledore/AlbiteResources/Droid-TTF.zip
 [11]: http://www.tug.org/tex-hyphen/
