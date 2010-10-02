package org.albite.book.model;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.StringItem;
import org.albite.dictionary.DictionaryException;
import org.albite.dictionary.LocalDictionary;
import org.albite.util.archive.Archive;
import org.albite.util.archive.ArchivedFile;
import org.kxml2.io.KXmlParser;
import org.kxml2.kdom.Document;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;
import org.xmlpull.v1.XmlPullParserException;

//a singleton for performance reasons, mainly memory fragmentation and garbage collection
//syncs not neccessary for this application; may be implemented in future
public class Book {
    private static final String BOOK_TAG                = "book";
    private static final String BOOK_TITLE_TAG          = "title";
    private static final String BOOK_AUTHOR_TAG         = "author";
    private static final String BOOK_DESCRIPTION_TAG    = "description";
    private static final String BOOK_LANGUAGE_TAG       = "language";
    private static final String BOOK_META_TAG           = "meta";
    private static final String BOOK_THUMBNAIL_ATTRIB   = "thumbnail";
    private static final String BOOK_DICTIONARY_ATTRIB  = "dictionary";

    final private static String INFO_TAG                = "info";
    final private static String INFO_NAME_ATTRIB        = "name";

    final private static String CHAPTER_TAG             = "chapter";
    final private static String CHAPTER_SOURCE_ATTRIB   = "src";

    final private static String USERDATA_BOOK_TAG       = "book";
    final private static String USERDATA_BOOKMARK_TAG   = "bookmark";
    final private static String USERDATA_CHAPTER_ATTRIB = "chapter";
    final private static String USERDATA_CHAPTER_TAG    = "chapter";
    final private static String USERDATA_POSITION_ATTRIB= "position";
    final private static String USERDATA_CRC_ATTRIB     = "crc";

    final public static String TEXT_ENCODING            = "UTF-8";

    // Meta Info
    private String  title       = "Untitled";
    private String  author      = "Unknown Author";
    private short   language    = Languages.LANG_UNKNOWN;
    private String  description = null;

    private LocalDictionary dict = null;

    private Hashtable meta; //contains various book attribs, e.g. 'fiction', 'for_children', 'prose', etc.
    private BookmarkManager bookmarks;

    private ArchivedFile thumbImageFile = null;

    //The File
    private Archive archive         = null;
    private FileConnection userfile = null;

    //Chapters
    private Chapter[]     chapters;
    private Chapter       currentChapter;

    public Book(String filename) throws IOException, BookException {

        //read file
        archive = new Archive(filename);

        try {
            //load book description (title, author, etc.)
            loadBookDescriptor();

            //load chapters info (filename + title)
            loadChaptersDescriptor();

            //load user data
            bookmarks = new BookmarkManager();

            //form user settings filename, i.e. ... .alb -> ... .alx
            int dotpos = filename.lastIndexOf('.');

            char[] alx_chars = new char[dotpos + 5]; //index + .alx + 1
            filename.getChars(0, dotpos +1, alx_chars, 0);
            alx_chars[dotpos+1] = 'a';
            alx_chars[dotpos+2] = 'l';
            alx_chars[dotpos+3] = 'x';

            String alx_filename = new String(alx_chars);

            try {
                userfile = (FileConnection) Connector.open(
                        alx_filename, Connector.READ_WRITE);

                if (!userfile.isDirectory()) {
                    /*
                     * if there is a dir by that name,
                     * the functionality will be disabled
                     *
                     */
                    if (!userfile.exists()) {
                        // create the file if it doesn't exist
                        userfile.create();
//                        System.out.println("User file created");
                    } else {
                        // try to load user settings
                        loadUserData();
                    }
                }
            } catch (SecurityException e) {
                if (userfile != null) {
                    userfile.close();
                    userfile = null;
                }
            } catch (IOException e) {
                if (userfile != null) {
                    userfile.close();
                    userfile = null;
                }
            } catch (BookException e) {

                /*
                 * Obviously, the content is wrong. The file's content will be
                 * overwritten as to prevent malformed files from
                 * making it permanently impossible for the user to save date
                 * for a particular book.
                 */
                e.printStackTrace();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();

        } catch (BookException be) {
            close();
            throw be;
        }
    }

    public void close() {

        try {

            archive.close();

            if (userfile != null) {
                userfile.close();
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

//        System.gc();
    }

    public short getLanguage() {
        return language;
    }

    public void unloadChaptersBuffers() {
        //unload all chapters from memory
        Chapter chap = chapters[0];
        while(chap != null) {
            chap.unload();
            chap = chap.getNextChapter();
        }
    }

    private void loadBookDescriptor() throws BookException, IOException {

        ArchivedFile bookDescriptor = archive.getFile("book.xml");

        if (bookDescriptor == null) {
            throw new BookException("Missing book descriptor <book.xml>");
        }

        final byte[] contents = bookDescriptor.getAsBytes();

        ByteArrayInputStream in =
                new ByteArrayInputStream(contents);

        meta = new Hashtable(10); //around as much meta info in each book

        KXmlParser parser = null;
        Document doc = null;
        Element root;
        Element kid;

        try {
            parser = new KXmlParser();
            parser.setInput(new InputStreamReader(in));

//            replaceEntities(parser);

            doc = new Document();
            doc.parse(parser);
            parser = null;

        } catch (XmlPullParserException xppe) {
            parser = null;
            doc = null;
            throw new BookException(
                "Book descriptor <book.xml> contains wrong data.");
        }

        root = doc.getRootElement();

        final String thumbString =
                root.getAttributeValue(
                    KXmlParser.NO_NAMESPACE,
                    BOOK_THUMBNAIL_ATTRIB);

        if (thumbString != null) {
            thumbImageFile = archive.getFile(thumbString);
        }

        final String dictString =
                root.getAttributeValue(
                    KXmlParser.NO_NAMESPACE,
                    BOOK_DICTIONARY_ATTRIB);

        if (dictString != null) {
            try {
                dict = new LocalDictionary(archive.getFile(dictString));
            } catch (DictionaryException e) {
                /*
                 * Couldn't load dict.
                 */
                dict = null;
            }
        }

        int child_count = root.getChildCount();
        for (int i = 0; i < child_count ; i++ ) {
            if (root.getType(i) != Node.ELEMENT) {
                continue;
            }

            kid = root.getElement(i);
            if (kid.getName().equals(BOOK_TITLE_TAG)) {
                title = kid.getText(0);
            }

            if (kid.getName().equals(BOOK_AUTHOR_TAG)) {
                author = kid.getText(0);
            }

            if (kid.getName().equals(BOOK_DESCRIPTION_TAG)) {
                description = kid.getText(0);
            }

            if (kid.getName().equals(BOOK_LANGUAGE_TAG))
                try {
                    language = Short.parseShort(kid.getText(0));
                    if (language < 1 || language > Languages.LANGS_COUNT) {
                        language = Languages.LANG_UNKNOWN; //set to default
                    }
                } catch (NumberFormatException nfe) {
                    language = Languages.LANG_UNKNOWN; //set to default
                }

            if (kid.getName().equals(BOOK_META_TAG)) {

                final int metaCount = kid.getChildCount();

                for (int m = 0; m < metaCount; m++) {

                    final Element metaField = kid.getElement(m);

                    if (metaField != null) {

                        if (metaField.getName().equals(INFO_TAG)) {

                            final String infoName =
                                    metaField.getAttributeValue(
                                    KXmlParser.NO_NAMESPACE, INFO_NAME_ATTRIB);

                            final String infoValue =
                                    metaField.getText(0);

                            if (infoName != null && infoValue != null) {
                                meta.put(infoName, infoValue);
                            }
                        }
                    }
                }
            }
        }
    }

    private void loadChaptersDescriptor() throws BookException, IOException {

        ArchivedFile tocDescriptor = archive.getFile("toc.xml");
        if (tocDescriptor == null)
            throw new BookException("Missing TOC descriptor <toc.xml>");

        final byte[] contents = tocDescriptor.getAsBytes();

        ByteArrayInputStream in =
                new ByteArrayInputStream(contents);

        KXmlParser parser = null;
        Document doc = null;
        Element root;
        Element kid;

        try {
            parser = new KXmlParser();
            parser.setInput(new InputStreamReader(in));

//            replaceEntities(parser);

            doc = new Document();
            doc.parse(parser);
            parser = null;
        } catch (XmlPullParserException xppe) {
            parser = null;
            doc = null;
            throw new BookException(
                    "TOC descriptor <toc.xml> contains wrong data.");
        }

        root = doc.getRootElement();
        int child_count = root.getChildCount();

        String chapterFileName = null;
        String chapterTitle = null;

        Vector chaptersVector = new Vector();
        int currentChapterNumber = 0;
        Chapter prev = null;

        ArchivedFile af = null;
 
        for (int i = 0; i < child_count ; i++ ) {
            if (root.getType(i) != Node.ELEMENT) {
                    continue;
            }

            kid = root.getElement(i);
            if (kid.getName().equals(CHAPTER_TAG)) {

                currentChapterNumber++;

                chapterFileName = kid.getAttributeValue(
                        KXmlParser.NO_NAMESPACE, CHAPTER_SOURCE_ATTRIB);

                if (chapterFileName == null)
                    throw new BookException(
                            "Invalid TOC descriptor: chapter does not provide"
                            + " src information.");

                if (kid.getChildCount() > 0) {
                    chapterTitle = kid.getText(0);
                    if (chapterTitle == null
                            || chapterTitle.length() == 0
                            || chapterTitle.trim().length() == 0)
                    {
                        chapterTitle = "Chapter #" + (currentChapterNumber);
                    }
                } else {
                    chapterTitle = "Chapter #" + (currentChapterNumber);
                }

                af = archive.getFile(chapterFileName);
                if (af == null)
                    throw new BookException("Chapter #" + currentChapterNumber
                            + " declared, but its file <" + chapterFileName
                            + "> is missing");

                final Chapter cur =
                        new Chapter(af, chapterTitle, currentChapterNumber - 1);
                chaptersVector.addElement(cur);

                if (prev != null) {
                    prev.setNextChapter(cur);
                    cur.setPrevChapter(prev);
                }

                prev = cur;
            }
        }

        if (currentChapterNumber < 1) {
            throw new BookException(
                    "No chapters were found in the TOC descriptor.");
        }

        chapters = new Chapter[chaptersVector.size()];

        for (int i = 0; i < chaptersVector.size(); i ++) {
            chapters[i] = (Chapter) chaptersVector.elementAt(i);
        }

        currentChapter = chapters[0]; //default value
    }

    private void loadUserData() throws BookException, IOException {
        InputStream in = userfile.openInputStream();

        KXmlParser parser = null;
        Document doc = null;
        Element root;
        Element kid;

        try {
            parser = new KXmlParser();
            parser.setInput(new InputStreamReader(in));

            doc = new Document();
            doc.parse(parser);
            parser = null;
        } catch (XmlPullParserException e) {
            parser = null;
            doc = null;
            throw new BookException("Wrong XML data.");
        }

        try {
            /*
             * root element (<book>)
             */
            root = doc.getRootElement();

            try {
                int crc = Integer.parseInt(
                        root.getAttributeValue(
                        KXmlParser.NO_NAMESPACE, USERDATA_CRC_ATTRIB));

                if (crc != this.archive.getCRC()) {
                    throw new BookException("Wrong CRC");
                }
            } catch (NumberFormatException nfe) {
                throw new BookException("Wrong CRC");
            }

            final int cchapter = readIntFromXML(root, USERDATA_CHAPTER_ATTRIB);

            int childCount = root.getChildCount();

            for (int i = 0; i < childCount ; i++ ) {
                if (root.getType(i) != Node.ELEMENT) {
                    continue;
                }

                kid = root.getElement(i);

                if (kid.getName().equals(USERDATA_BOOKMARK_TAG)
                        || kid.getName().equals(USERDATA_CHAPTER_TAG)) {

                    final int chapter =
                            readIntFromXML(kid, USERDATA_CHAPTER_ATTRIB);

                    int position =
                            readIntFromXML(kid, USERDATA_POSITION_ATTRIB);

                    if (position < 0) {
                        position = 0;
                    }

                    if (kid.getName().equals(USERDATA_BOOKMARK_TAG)) {

                        String text = kid.getText(0);

                        if (text == null) {
                            text = "Untitled";
                        }

                        bookmarks.addBookmark(
                                new Bookmark(getChapter(chapter),
                                position, text));

                    } else {
                        Chapter c = getChapter(chapter);
                        c.setCurrentPosition(position);
                    }
                }
            }

            currentChapter = getChapter(cchapter);

        } catch (NullPointerException e) {
            bookmarks.deleteAll();
            throw new BookException("Missing info (NP Exception)");

        } catch (IllegalArgumentException e) {
            bookmarks.deleteAll();
            throw new BookException("Malformed int data");

        } catch (RuntimeException e) {
            //document has not got a root element
            bookmarks.deleteAll();
            throw new BookException("Wrong data");

        } finally {
            if (in != null)
                in.close();
        }
    }

    private int readIntFromXML(Element kid, String elementName) {
        int number = 0;

        try {
            number = Integer.parseInt(
                kid.getAttributeValue(
                KXmlParser.NO_NAMESPACE, elementName));
        } catch (NumberFormatException nfe) {}

        return number;
    }

    public final void saveUserData() {
        //        Saving book info
        if (chapters != null && //i.e. if any chapters have been read
            userfile != null //i.e. the file is OK for writing
            ) {
            //lets try to save
            try {
                userfile.truncate(0);
                DataOutputStream dout = userfile.openDataOutputStream();
                try {
                    /*
                     * Root element
                     * <book crc="123456789" chapter="3" position="1234">
                     */
                    dout.write("<".getBytes(TEXT_ENCODING));
                    dout.write(USERDATA_BOOK_TAG.getBytes(TEXT_ENCODING));
                    dout.write(" ".getBytes(TEXT_ENCODING));
                    dout.write(USERDATA_CRC_ATTRIB.getBytes(TEXT_ENCODING));
                    dout.write("=\"".getBytes(TEXT_ENCODING));
                    dout.write(Integer.toString(archive.getCRC())
                            .getBytes(TEXT_ENCODING));
                    dout.write("\" ".getBytes(TEXT_ENCODING));
                    dout.write(USERDATA_CHAPTER_ATTRIB.getBytes(TEXT_ENCODING));
                    dout.write("=\"".getBytes(TEXT_ENCODING));
                    dout.write(
                            Integer.toString(currentChapter.getNumber())
                            .getBytes(TEXT_ENCODING));
                    dout.write("\">\n".getBytes(TEXT_ENCODING));

                    /*
                     * current chapter positions
                     * <chapter chapter="3" position="1234" />
                     */
                    for (int i = 0; i < chapters.length; i++) {
                        Chapter c = chapters[i];
                        int n = c.getNumber();
                        int pos = c.getCurrentPosition();

                        dout.write("\t<".getBytes(TEXT_ENCODING));
                        dout.write(USERDATA_CHAPTER_TAG
                                .getBytes(TEXT_ENCODING));
                        dout.write(" ".getBytes(TEXT_ENCODING));
                        dout.write(USERDATA_CHAPTER_ATTRIB
                                .getBytes(TEXT_ENCODING));
                        dout.write("=\"".getBytes(TEXT_ENCODING));
                        dout.write(Integer.toString(n).getBytes(TEXT_ENCODING));
                        dout.write("\" ".getBytes(TEXT_ENCODING));
                        dout.write(USERDATA_POSITION_ATTRIB
                                .getBytes(TEXT_ENCODING));
                        dout.write("=\"".getBytes(TEXT_ENCODING));
                        dout.write(Integer.toString(pos)
                                .getBytes(TEXT_ENCODING));
                        dout.write("\" />\n".getBytes(TEXT_ENCODING));
                    }

                    /*
                     * bookmarks
                     * <bookmark chapter="3" position="1234">Text</bookmark>
                     */
                    Bookmark bookmark = bookmarks.getFirst();
                    while (bookmark != null) {
                        System.out.println("writing bookmark " + bookmark.getText());

                        dout.write("\t<".getBytes(TEXT_ENCODING));
                        dout.write(USERDATA_BOOKMARK_TAG
                                .getBytes(TEXT_ENCODING));
                        dout.write(" ".getBytes(TEXT_ENCODING));
                        dout.write(USERDATA_CHAPTER_ATTRIB
                                .getBytes(TEXT_ENCODING));
                        dout.write("=\"".getBytes(TEXT_ENCODING));
                        dout.write(Integer.toString(
                                bookmark.getChapter().getNumber()
                                ).getBytes(TEXT_ENCODING));
                        dout.write("\" ".getBytes(TEXT_ENCODING));
                        dout.write(USERDATA_POSITION_ATTRIB
                                .getBytes(TEXT_ENCODING));
                        dout.write("=\"".getBytes(TEXT_ENCODING));
                        dout.write(Integer.toString(bookmark.getPosition())
                                .getBytes(TEXT_ENCODING));
                        dout.write("\">".getBytes(TEXT_ENCODING));
                        dout.write(bookmark.getTextForHTML().getBytes(TEXT_ENCODING));
                        dout.write("</".getBytes(TEXT_ENCODING));
                        dout.write(USERDATA_BOOKMARK_TAG
                                .getBytes(TEXT_ENCODING));
                        dout.write(">\n".getBytes(TEXT_ENCODING));

                        bookmark = bookmark.next;
                    }

                    /*
                     * Close book tag
                     */
                    dout.write("</".getBytes(TEXT_ENCODING));
                    dout.write(USERDATA_BOOK_TAG.getBytes(TEXT_ENCODING));
                    dout.write(">\n".getBytes(TEXT_ENCODING));

                } catch (IOException ioe) {
                    ioe.printStackTrace();
                } finally {
                    dout.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

    }

    public final int getChaptersCount() {
        return chapters.length;
    }

    public final Chapter getChapter(final int number) {

        if (number < 0) {
            return chapters[0];
        }

        if (number > chapters.length - 1) {
            return chapters[chapters.length - 1];
        }

        return chapters[number];
    }

    public Archive getArchive() {
        return archive;
    }

    public void fillBookInfo(Form f) {
        if (thumbImageFile != null) {
            Image image;

            try {
                image = thumbImageFile.getAsImage();

                ImageItem ii =
                        new ImageItem(
                        null,
                        image,
                        ImageItem.LAYOUT_CENTER,
                        null);

                f.append(ii);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        StringItem s;

        s = new StringItem("Title:", title);
        s.setLayout(StringItem.LAYOUT_LEFT);

        f.append(s);

        s = new StringItem("Author:", author);
        f.append(s);

        if (description != null) {
            f.append(description);
        }

        for (Enumeration e = meta.keys(); e.hasMoreElements();) {
            final String infoName = (String) e.nextElement();
            final String infoValue = (String) meta.get(infoName);

            if (infoName != null && infoValue != null) {
                s = new StringItem(infoName + ":", infoValue);
                f.append(s);
            }
        }

        s = new StringItem("Language ID:", Integer.toString(language));
    }

    public final LocalDictionary getDictionary() {
        return dict;
    }

    public final BookmarkManager getBookmarkManager() {
        return bookmarks;
    }

    public final Chapter getCurrentChapter() {
        return currentChapter;
    }

    public final void setCurrentChapter(final Chapter bc) {
        currentChapter = bc;
    }

    public final int getCurrentChapterPosition() {
        return currentChapter.getCurrentPosition();
    }

    public final void setCurrentChapterPos(final int pos) {
        if (pos < 0 || pos >= currentChapter.getTextBuffer().length) {
            throw new IllegalArgumentException("Position is wrong");
        }

        currentChapter.setCurrentPosition(pos);
    }
}