/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.model.book;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;
import org.albite.book.model.parser.HTMLTextParser;
import org.albite.io.RandomReadingFile;
import org.albite.io.decoders.AlbiteStreamReader;
import org.albite.io.decoders.Encodings;
import org.albite.util.archive.Archive;
import org.albite.util.archive.ArchiveEntry;
import org.albite.util.archive.zip.ArchiveZip;
import org.kxml2.io.KXmlParser;
import org.kxml2.kdom.Document;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;
import org.xmlpull.v1.XmlPullParserException;

/**
 *
 * @author Svetlin Ankov <galileostudios@gmail.com>
 */
public class EPubBook extends Book {

    /*
     * Book file
     */
    private ArchiveZip      bookArchive;

    public EPubBook(final String filename)
            throws IOException, BookException {

        this.bookURL = filename;
        this.parser = new HTMLTextParser();
        bookArchive = new ArchiveZip(filename);
        language = null;

        try {
            /*
             * load chapters info (filename + title)
             */
            loadChaptersAndBookDescriptor();

            linkChapters();
            loadUserFiles(filename);
        } catch (IOException ioe) {
        } catch (BookException be) {
            close();
            throw be;
        }
    }

    private String text(final Element kid) {

        String res = null;

        try {
            res = kid.getText(0);
        } catch (Exception e) {}

        if (res == null) {
            return "";
        }

        return res;
    }

    private Element getElement(final Node node, final String name) {
        for (int i = 0; i < node.getChildCount(); i++) {
            if (node.getType(i) != Node.ELEMENT) {
                continue;
            }

            Element element = node.getElement(i);
            if (name.equalsIgnoreCase(element.getName())) {
                return element;
            }
        }

        return null;
    }

    private void loadChaptersAndBookDescriptor()
            throws BookException, IOException  {

        InputStream in;
        String opfFileName = null;
        final String opfFilePath;

        /*
         * first load META-INF/container.xml
         */
        ArchiveEntry container =
                bookArchive.getEntry("META-INF/container.xml");

        if (container == null) {
            throw new BookException("Missing manifest");
        }

        in = container.openInputStream();

        try {
            KXmlParser parser = null;
            Document doc = null;
            Element root;
            Element kid;
            try {
                parser = new KXmlParser();
                parser.setInput(new AlbiteStreamReader(
                        in, Encodings.DEFAULT));

                doc = new Document();
                doc.parse(parser);
                parser = null;

                root = doc.getRootElement();

                Element rfile = root
                        .getElement(parser.NO_NAMESPACE, "rootfiles")
                        .getElement(parser.NO_NAMESPACE, "rootfile");

                opfFileName = rfile.getAttributeValue(
                        parser.NO_NAMESPACE, "full-path");

                if (opfFileName == null) {
                    throw new BookException("Missing opf file");
                }

                opfFilePath = RandomReadingFile.getPathFromURL(opfFileName);

                //#debug
                System.out.println(opfFilePath);

            } catch (XmlPullParserException xppe) {
                parser = null;
                doc = null;
                throw new BookException(
                    "container.xml is invalid");
            }
        } finally {
            in.close();
        }

        /*
         * now the opf file
         */
        ArchiveEntry opfFile = bookArchive.getEntry(opfFileName);

        if (opfFile == null) {
            throw new BookException("Missing opf");
        }

        in = opfFile.openInputStream();

        try {
            KXmlParser parser = null;
            Document doc = null;
            Element root;
            Element kid;

            try {
                parser = new KXmlParser();

                try {
                    parser.setFeature(
                            KXmlParser.FEATURE_PROCESS_NAMESPACES, true);
                } catch (XmlPullParserException e) {}

                parser.setInput(new AlbiteStreamReader(
                        in, Encodings.DEFAULT));

                doc = new Document();
                doc.parse(parser);
                parser = null;

                root = doc.getRootElement();

                try {
                    /*
                     * try to get the metadata
                     */
//                    meta = new Hashtable(10);

                    Element metadata = getElement(root, "metadata");
                    
                    Element dcmetadata = getElement(metadata, "dc-metadata");
                    
                    if (dcmetadata != null) {
                        metadata = dcmetadata;
                    }

                    if (metadata != null) {
                        for (int i = 0; i < metadata.getChildCount(); i++) {
                            if (metadata.getType(i) != Node.ELEMENT) {
                                continue;
                            }

                            kid = metadata.getElement(i);

                            if (kid.getName().equalsIgnoreCase("title")) {
                                title = text(kid);
                                continue;
                            }

                            if (kid.getName().equalsIgnoreCase("creator")) {
                                author = text(kid);
                                continue;
                            }

                            if (kid.getName().equalsIgnoreCase("language")) {
                                language = text(kid);
                                /*
                                 * squash it to a 2-letter tag
                                 */
                                if (language.length() > 2) {
                                    language = language.substring(0, 2);
                                }

                                /*
                                 * set currentLanguage to the default value
                                 * afterward (in loadUserFile) it will
                                 * be overwritten
                                 */
                                currentLanguage = language;

                                continue;
                            }

//                            if (kid.getName().equalsIgnoreCase("meta")) {
//                                String metaname = kid.getAttributeValue(parser.NO_NAMESPACE, "name");
//                                String metavalue = kid.getAttributeValue(parser.NO_NAMESPACE, "content");
//                                if (metaname != null && metavalue != null
//                                        && !metaname.startsWith("calibre")) {
//                                    /*
//                                     * Ignore Calibre-specific tags,
//                                     * as they are not informative for the
//                                     * reader, but only for Calibre
//                                     */
//                                    meta.put(metaname, metavalue);
//                                }
//
//                                continue;
//                            }
//
//                            /*
//                             * It's a metadata then
//                             */
//                            {
//                                String metaname = kid.getName();
//                                String metavalue = text(kid);
//
//                                if (metaname != null && metavalue != null
//                                        && !metaname.startsWith("calibre")) {
//                                    meta.put(metaname, metavalue);
//                                }
//                            }
                        }
                    }
                } catch (Exception e) {
                    /*
                     * If there is a problem with the metadata,
                     * it's not worth bothering
                     */
                    //#debug
                    e.printStackTrace();
                }

                Hashtable manifest = new Hashtable(200);

                try {
                    /*
                     * Parse the manifest list
                     */
                    Element manifestEl = getElement(root, "manifest");

                    if (manifestEl == null) {
                        throw new BookException("No manifest tag in OPF");
                    }
                    
                    for (int i = 0; i < manifestEl.getChildCount(); i++) {
                        if (manifestEl.getType(i) != Node.ELEMENT) {
                            continue;
                        }

                        kid = manifestEl.getElement(i);

                        if (kid.getName().equalsIgnoreCase("item")) {
                            String id = kid.getAttributeValue(
                                    parser.NO_NAMESPACE, "id");
                            String href = kid.getAttributeValue(
                                    parser.NO_NAMESPACE, "href");

                            if (id != null && href != null) {
                                /*
                                 * Item is OK
                                 */
                                manifest.put(id, href);
                            }
                        }
                    }
                } catch (Exception e) {
                    //#debug
                    e.printStackTrace();
                    throw new BookException("couldn't parse manifest");
                }

                try {
                    /*
                     * Parse the spine and create the chapters
                     */
                    Vector chaps = new Vector(40);

                    Element spine = getElement(root, "spine");

                    if (spine == null) {
                        throw new BookException("No spine tag in OPF");
                    }

                    for (int i = 0; i < spine.getChildCount(); i++) {
                        if (spine.getType(i) != Node.ELEMENT) {
                            continue;
                        }

                        kid = spine.getElement(i);

                        if (kid.getName().equalsIgnoreCase("itemref")) {
                            String idref = kid.getAttributeValue(
                                    parser.NO_NAMESPACE, "idref");
                            if (idref != null) {
                                String href = (String) manifest.get(idref);

                                if (href != null) {
                                    ArchiveEntry entry =
                                            bookArchive.getEntry(
                                            RandomReadingFile
                                            .relativeToAbsoluteURL(
                                            opfFilePath + href));

                                    if (entry != null) {
                                        /*
                                         * chapter is OK
                                         */
//                                        final Chapter cur = loadChapter(
//                                                entry, chaps.size());
                                        splitChapterIntoPieces(
                                                entry,
                                                entry.fileSize(),
                                                entry,
                                                MAXIMUM_HTML_FILESIZE,
                                                chaps.size(),
                                                true,
                                                chaps
                                                );
                                    }
                                }
                            }
                        }
                    }

                    if (chaps.isEmpty()) {
                        throw new BookException("no chaps found in opf");
                    }

                    /*
                     * all chaps loaded
                     */
                    chapters = new Chapter[chaps.size()];
                    chaps.copyInto(chapters);
                } catch (Exception e) {
                    //#debug
                    e.printStackTrace();
                    throw new BookException("couldn't load chapters");
                }
            } catch (XmlPullParserException xppe) {
                parser = null;
                doc = null;
                //#debug
                xppe.printStackTrace();
                throw new BookException(
                    "the opf file is invalid");
            }
        } finally {
            in.close();
        }
    }

    public Archive getArchive() {
        return bookArchive;
    }

    public final void close() throws IOException {
        bookArchive.close();
        closeUserFiles();
    }
}