/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.processor;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.io.InputConnection;
import org.albite.book.StyleConstants;
import org.albite.book.model.element.*;
import org.albite.book.model.css.CssSelector;
import org.albite.io.AlbiteStreamReader;
import org.albite.util.archive.zip.ArchiveZip;
import org.albite.util.archive.zip.ArchiveZipEntry;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 *
 * @author Svetlin Ankov <galileostudios@gmail.com>
 */
public class HtmlProcessor implements MarkupProcessor, StyleConstants {

    public final Element[] getElements(
            final ArchiveZip archive,
            final InputConnection file,
            final int fileSize,
            final String encoding) {

        try {
            AlbiteStreamReader r =
                    new AlbiteStreamReader(
                    file.openInputStream(), encoding);

            try {
                XmlPullParserFactory factory =
                        XmlPullParserFactory.newInstance(
                        System.getProperty(
                        XmlPullParserFactory.PROPERTY_NAME), null);
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();

                xpp.setFeature(xpp.FEATURE_PROCESS_DOCDECL, true);
                xpp.setFeature(xpp.FEATURE_PROCESS_NAMESPACES, false);
                xpp.setFeature(
                        xpp.FEATURE_REPORT_NAMESPACE_ATTRIBUTES, false);
                xpp.setFeature(xpp.FEATURE_VALIDATION, false);

                xpp.setInput(r);

                return processDocument(archive, xpp, r);

            } catch (IOException e) {
                return null;
            } finally {
                r.close();
            }
        } catch (Exception e) {
            /*
             * Couldn't load the chapter
             */
            return null;
        }
    }

    private Element[] processDocument(
            final ArchiveZip archive,
            final XmlPullParser xpp,
            final AlbiteStreamReader r)
            throws XmlPullParserException, IOException {

        /*
         * CSS Elements
         */
        Hashtable cssElements = null;

        /*
         * Here we'll put the elements
         */
        final Vector elements = new Vector(2000); //~8K

        final int[] textBufPos = new int[2];

        final Vector stylesStack = new Vector(20); //~20 elements deep

        /*
         * Adding default styling twice, for we need to look backwards
         * for the first element, too.
         */
        stylesStack.addElement(StyleElement.DEFAULT_STYLE);
        stylesStack.addElement(StyleElement.DEFAULT_STYLE);

        int eventType = xpp.getEventType();

        do {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    System.out.println("Start document");
                    /*
                     * Read next event and try to see if the encoding
                     * has been set
                     */
                    eventType = xpp.next();

                    try {
                        r.setEncoding(xpp.getInputEncoding());
                    } catch (UnsupportedEncodingException e) {
                        /*
                         * Nothing to do, as the AlbiteStreamReader
                         * will still be in a valid state
                         */
                    }

                    continue;

                case XmlPullParser.END_DOCUMENT:
                    System.out.println("End document");
                    break;

                case XmlPullParser.START_TAG:
                {

                    final String elementName = xpp.getName();

                    final StyleElement lastStyle =
                            (StyleElement) stylesStack.lastElement();

                    StyleElement style = lastStyle;

                    if (cssElements != null) {
                        final String elementID =
                                xpp.getAttributeValue(null, "id");

                        final String elementClass =
                                xpp.getAttributeValue(null, "class");

                        final CssSelector css =
                                new CssSelector(
                                elementName, elementID, elementClass);

                        final StyleElement foundStyle =
                                (StyleElement) cssElements.get(css);

                        if (foundStyle != null) {
                            /*
                             * Merge the curernt style with the one we found
                             */
                            style.mergeWith(foundStyle);
                        }
                    }

                    final String styleAttrib =
                            xpp.getAttributeValue(null, "style");

                    if (styleAttrib != null) {
                        //TODO: parse css instructions
                    }

                    /*
                     * Process current element
                     */
                    Element element = null;

                    /*
                     * These elements hold style information
                     */
                    if (elementName.equalsIgnoreCase("b")
                            || elementName.equalsIgnoreCase("strong")) {
                        style.bold = ENABLE;

                    } else if (elementName.equalsIgnoreCase("i")
                            || elementName.equalsIgnoreCase("em")) {
                        style.italic = ENABLE;

                    } else if (elementName.equalsIgnoreCase("h1")
                            || elementName.equalsIgnoreCase("h2")
                            || elementName.equalsIgnoreCase("h3")
                            || elementName.equalsIgnoreCase("h4")
                            || elementName.equalsIgnoreCase("h5")
                            || elementName.equalsIgnoreCase("h6")) {

                        style.heading = ENABLE;

                    } else if (elementName.equalsIgnoreCase("img")) {

                        /*
                         * Image element
                         */
                        final String src =
                                xpp.getAttributeValue(null, "src");


                        if (src != null && archive != null) {
                            final ArchiveZipEntry entry = archive.getEntry(src);
                            final String altText =
                                    xpp.getAttributeValue(null, "alt");

                            element =
                                    new ImageElement(entry,
                                    (altText == null
                                    ? null : altText.toCharArray()));
                        }
                    } else if (elementName.equalsIgnoreCase("hr")) {
                        /*
                         * Ruler element
                         */
                        element = new RulerElement();
                    } else if (elementName.equalsIgnoreCase("link")) {
                        if (
                                "stylesheet".equalsIgnoreCase(
                                    xpp.getAttributeValue(null, "rel"))
                                && "text/css".equalsIgnoreCase(
                                    xpp.getAttributeValue(null, "type"))
                                ) {
                            final String cssfile =
                                    xpp.getAttributeValue(null, "href");

                            if (archive != null
                                    && cssfile != null
                                    && cssfile.endsWith(".css")) {
                                ArchiveZipEntry entry =
                                        archive.getEntry(cssfile);

                                cssElements = processCSS(cssElements, entry);
                            }
                        }
                    }

                    /*
                     * add the style to the stack
                     */
                    stylesStack.addElement(style);

                    if (!style.equals(lastStyle)) {
                        /*
                         * Add the style to the elements,
                         * if it's different from the last style used
                         */
                        elements.addElement(style);
                    }

                    /*
                     * Add the element, if there is such available
                     */
                    if (element != null) {
                        elements.addElement(element);
                    }

                    break;
                }

                case XmlPullParser.END_TAG:
                {
                    /*
                     * Pull last element from the stack
                     */
                    StyleElement restored =
                            (StyleElement) stylesStack.elementAt(
                                stylesStack.size() -2);

                    StyleElement removed =
                            (StyleElement) stylesStack.lastElement();

                    stylesStack.removeElementAt(stylesStack.size() - 1);

                    if (!restored.equals(removed)) {
                        /*
                         * Add the resored style to the pile of elements,
                         * if it's different from the previously used style
                         */
                        elements.addElement(restored);
                    }

                    /*
                     * Check for new line elements
                     */
                    final String elementName = xpp.getName();

                    if (elementName.equalsIgnoreCase("p")
                            || elementName.equalsIgnoreCase("br")) {
                        elements.addElement(new ParagraphElement());
                    }
                    break;
                }

                case XmlPullParser.TEXT:
                {

                    final char[] textBuf =
                            xpp.getTextCharacters(textBufPos);

                    final char[] text = new char[textBufPos[1]];

                    System.arraycopy(
                            textBuf, textBufPos[0], text, 0, text.length);

                    elements.addElement(new TextElement(text));
                    break;
                }
            }

            eventType = xpp.next();

        } while (eventType != xpp.END_DOCUMENT);

        /*
         * Return the resulting Elements
         */
        final Element[] result = new Element[elements.size()];
        elements.copyInto(result);
        return result;
    }

    private Hashtable processCSS(
            Hashtable cssElements, final ArchiveZipEntry file) {
        /*
         * TODO: Load and parse the css file
         */
        return cssElements;
    }
}
