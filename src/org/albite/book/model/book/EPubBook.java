/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.book.model.book;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;
import org.albite.book.model.parser.HtmlTextParser;
import org.albite.io.AlbiteStreamReader;
import org.albite.util.archive.zip.ArchiveZip;
import org.albite.util.archive.zip.ArchiveZipEntry;
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
    private ArchiveZip  bookArchive;
    private Hashtable   meta;

    public EPubBook(final String filename)
            throws IOException, BookException {

        this.parser = new HtmlTextParser();
        bookArchive = new ArchiveZip(filename);
        language = null; //TODO: overwrite

        try {
            /*
             * load chapters info (filename + title)
             */
            loadChaptersAndBookDescriptor();
            loadUserFile(filename);
        } catch (IOException ioe) {
        } catch (BookException be) {
            close();
            throw be;
        }
    }

    public String getURL() {
        return bookArchive.getURL();
    }

    private String text(final Element kid) {
        final String res = kid.getText(0);
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

    private void loadChaptersAndBookDescriptor() throws BookException, IOException  {

        InputStream in;
        String opfFileName = null;
        String opfFilePath = "";

        /*
         * first load META-INF/container.xml
         */
        ArchiveZipEntry container =
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
                        in, AlbiteStreamReader.DEFAULT_ENCODING));

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

                int i;

                i = opfFileName.lastIndexOf('/');

                if (i == -1) {
                    i = opfFileName.lastIndexOf('\\');
                }

                if (i >= 0) {
                    opfFilePath = opfFileName.substring(0, i + 1);
                }

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
        ArchiveZipEntry opfFile = bookArchive.getEntry(opfFileName);

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
                        in, AlbiteStreamReader.DEFAULT_ENCODING));

                doc = new Document();
                doc.parse(parser);
                parser = null;

                root = doc.getRootElement();

                System.out.println("OPF ROOT: " + root.getName());

                try {
                    /*
                     * try to get the metadata
                     */
                    meta = new Hashtable(10);

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
                                    currentLanguage = language;
                                }
                                System.out.println("lang: " + language);
                                //TODO: parse lang so it's in a 2-symbol form
                                continue;
                            }

                            /*
                             * It's a metadata then
                             */
                            meta.put(kid.getName(), text(kid));
                        }
                    }
                } catch (Exception e) {
                    /*
                     * If there is a problem with the metadata,
                     * it's not worth bothering
                     */
                    e.printStackTrace();
                    System.out.println("something wrong with the metadata");
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
                    e.printStackTrace();
                    throw new BookException("couldn't parse manifest");
                }

                try {
                    /*
                     * Parse the spine and create the chapters
                     */
                    Vector chaps = new Vector(40);
                    Chapter prev = null;

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
                                    ArchiveZipEntry entry =
                                            bookArchive.getEntry(opfFilePath + href);

                                    if (entry != null) {
                                        /*
                                         * chapter is OK
                                         */
                                        final Chapter cur = loadChapter(
                                                entry, chaps.size());
                                         
                                        chaps.addElement(cur);

                                        if (prev != null) {
                                            prev.setNextChapter(cur);
                                            cur.setPrevChapter(prev);
                                        }

                                        prev = cur;
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
                    e.printStackTrace();
                    throw new BookException("couldn't load chapters");
                }
            } catch (XmlPullParserException xppe) {
                parser = null;
                doc = null;
                xppe.printStackTrace();
                throw new BookException(
                    "the opf file is invalid");
            }
        } finally {
            in.close();
        }
    }

    private Chapter loadChapter(final ArchiveZipEntry entry, final int num) {
        return new Chapter(entry, entry.fileSize(),
                "Chapter #" + (num + 1), true, num);
    }
}