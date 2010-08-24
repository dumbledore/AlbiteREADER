package org.albite.book.model;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
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
    final private static String BOOK_TAG                = "book";
    final private static String BOOK_TITLE_TAG          = "title";
    final private static String BOOK_AUTHOR_TAG         = "author";
    final private static String BOOK_DESCRIPTION_TAG    = "description";
    final private static String BOOK_LANGUAGE_TAG       = "language";
    final private static String BOOK_META_TAG           = "meta";

    final private static String CHAPTER_TAG             = "chapter";
    final private static String CHAPTER_SOURCE_ATTRIB   = "src";

    final private static String USERDATA_BOOK_TAG       = "book";
    final private static String USERDATA_BOOKMARK_TAG   = "bookmark";
    final private static String USERDATA_CHAPTER_ATTRIB = "chapter";
    final private static String USERDATA_POSITION_ATTRIB= "position";
    final private static String USERDATA_CRC_ATTRIB     = "crc";

    final public static String TEXT_ENCODING            = "UTF-8";

    // Meta Info
    private String  title       = "Untitled";
    private String  author      = "Unknown Author";
    private short   language    = Languages.LANG_UNKNOWN;
    private String  description = "No description";

    private Hashtable meta; //contains various book attribs, e.g. 'fiction', 'for_children', 'prose', etc.
    private Vector    bookmarks;

    //The File
    private Archive archive         = null;
    private FileConnection userfile = null;

    //Chapters
    private Chapter   firstChapter;
    private int       chaptersCount;
    
    private Chapter   currentChapter;
    private int       currentChapterPos;

    //User data; statistics
    private int           timeSpentReading = 0; //in seconds
    private long          timeFromLastCheck; //used from the last time secondsSpentReading was updated

    public Chapter getCurrentChapter() {
        return currentChapter;
    }

    public void setCurrentChapter(Chapter bc) {
        currentChapter = bc;
    }

    public int getCurrentChapterPosition() {
        return currentChapterPos;
    }

    public void setCurrentChapterPos(int pos) {
        if (pos < 0 || pos >= currentChapter.getTextBufferSize()) {
            throw new IllegalArgumentException("Position is wrong");
        }
        currentChapterPos = pos;
    }

    public void open(String filename) throws IOException, BookException {

        //read file
        archive = new Archive();
        archive.open(filename);

        try {
            //load book description (title, author, etc.)
            loadBookDescriptor();

            //load chapters info (filename + title)
            loadChaptersDescriptor();

            //load user data
            bookmarks = new Vector(10);

            //form user settings filename, i.e. ... .alb -> ... .alx
            int dotpos = filename.lastIndexOf('.');

            char[] alx_chars = new char[dotpos + 5]; //index + .alx + 1
            filename.getChars(0, dotpos +1, alx_chars, 0);
            alx_chars[dotpos+1] = 'a';
            alx_chars[dotpos+2] = 'l';
            alx_chars[dotpos+3] = 'x';
            
            String alx_filename = new String(alx_chars);

            try {
                userfile = (FileConnection)Connector.open(alx_filename);
                if (!userfile.isDirectory()) {
                    //if there is a dir by that name, the functionality will be disabled
                    if (!userfile.exists()) {
                        // create the file if it doesn't exist
                        userfile.create();
                        System.out.println("User file created");
                    } else {
                        // try to load user settings
                        loadUserData();
                    }
                }
            } catch (IOException ioe) {
                System.out.println("Couldn't load user data.");
                userfile.close();
                userfile = null;
            } catch (BookException be) {
                //Obviously, the content is wrong, so shouldn't be touched.
                System.out.println("Couldn't load user data.");
                userfile.close();
                userfile = null;
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            
        } catch (BookException be) {
            close();
            throw be;
        }
        timeFromLastCheck = System.currentTimeMillis();
    }

    public void close() {

        try {

            chaptersCount = 0;
            currentChapter = null;
            
            archive.close();
            archive = null;

            if (userfile != null) {
                userfile.close();
                userfile = null;
            }
            
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        
        if (meta != null)
            meta.clear();
        
        meta = null;

        System.gc();
    }

    public short getLanguage() {
        return language;
    }

    public void unloadChaptersBuffers() {
        //unload all chapters from memory
        Chapter chap = firstChapter;
        while(chap != null) {
            chap.unload();
            chap = chap.getNextChapter();
        }
    }

    public boolean isOpen() {
        return firstChapter != null;
    }

    private void loadBookDescriptor() throws BookException, IOException {

        ArchivedFile bookDescriptor = archive.getFile("book.xml");
        if (bookDescriptor == null)
            throw new BookException("Missing book descriptor <book.xml>");

        InputStream in = bookDescriptor.openInputStream();
        meta = new Hashtable(10); //around as much meta info in each book

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
        } catch (XmlPullParserException xppe) {
            parser = null;
            doc = null;
            throw new BookException("Book descriptor <book.xml> contains wrong data.");
        }

        root = doc.getRootElement();
        int child_count = root.getChildCount();
        for (int i = 0; i < child_count ; i++ ) {
            if (root.getType(i) != Node.ELEMENT) {
                    continue;
            }

            kid = root.getElement(i);
            if (kid.getName().equals(BOOK_TITLE_TAG))
                title = kid.getText(0);

            if (kid.getName().equals(BOOK_AUTHOR_TAG))
                author = kid.getText(0);

            if (kid.getName().equals(BOOK_DESCRIPTION_TAG))
                description = kid.getText(0);

            if (kid.getName().equals(BOOK_LANGUAGE_TAG))
                try {
                    language = Short.parseShort(kid.getText(0));
                    if (language < 1 || language > Languages.LANGS_COUNT)
                        language = Languages.LANG_UNKNOWN; //set to default
                } catch (NumberFormatException nfe) {
                        language = Languages.LANG_UNKNOWN; //set to default
                }

            if (kid.getName().equals(BOOK_META_TAG)) {
                int meta_count = kid.getChildCount();
                Element metaField;
                for (int m=0; m<meta_count; m++) {
                    if (kid.getType(m) != Node.ELEMENT)
                        continue;
                    metaField = kid.getElement(m);
                    if (metaField.getAttributeCount() > 0)
                        meta.put(metaField.getAttributeValue(0), metaField.getText(0));
                }
            }
        }
    }

    private void loadChaptersDescriptor() throws BookException, IOException {

        ArchivedFile tocDescriptor = archive.getFile("toc.xml");
        if (tocDescriptor == null)
            throw new BookException("Missing TOC descriptor <toc.xml>");

        InputStream in = tocDescriptor.openInputStream();

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
        } catch (XmlPullParserException xppe) {
            parser = null;
            doc = null;
            throw new BookException("TOC descriptor <toc.xml> contains wrong data.");
        }

        root = doc.getRootElement();
        int child_count = root.getChildCount();

        String chapterFileName = null;
        String chapterTitle = null;

        chaptersCount = 0;

        boolean isFirst = true;
        Chapter lastChapter = null;

        ArchivedFile af = null;

        for (int i = 0; i < child_count ; i++ ) {
            if (root.getType(i) != Node.ELEMENT) {
                    continue;
            }

            kid = root.getElement(i);
            if (kid.getName().equals(CHAPTER_TAG)) {
                chaptersCount++;
                
                chapterFileName = kid.getAttributeValue(KXmlParser.NO_NAMESPACE, CHAPTER_SOURCE_ATTRIB);

                if (chapterFileName == null)
                    throw new BookException("Invalid TOC descriptor: chapter does not provide src information.");

                if (kid.getChildCount() > 0) {
                    chapterTitle = kid.getText(0);
                    if (chapterTitle == null || chapterTitle.length() == 0 || chapterTitle.trim().length() == 0)
                        chapterTitle = "Chapter " + chaptersCount;
                } else {
                    chapterTitle = "Chapter " + chaptersCount;
                }
                
                af = archive.getFile(chapterFileName);
                if (af == null)
                    throw new BookException("Chapter " + chaptersCount + " declared, but its file <" + chapterFileName + "> is missing");

                if (isFirst) {
                    firstChapter = new Chapter(af, chapterTitle, chaptersCount);
                    lastChapter = firstChapter;
                    isFirst = false;
                } else {
                    lastChapter.setNextChapter(new Chapter(af, chapterTitle, chaptersCount));
                    lastChapter.getNextChapter().setPrevChapter(lastChapter);
                    lastChapter = lastChapter.getNextChapter();
                }
            }
        }
        if (chaptersCount < 1)
            throw new BookException("No chapters were found in the TOC descriptor.");

        currentChapter = firstChapter; //default value
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
        } catch (XmlPullParserException xppe) {
            parser = null;
            doc = null;
            throw new BookException("Wrong data.");
        }

        try {
            root = doc.getRootElement();
            //root element (<book>)
            int crc = Integer.parseInt(root.getAttributeValue(KXmlParser.NO_NAMESPACE, USERDATA_CRC_ATTRIB));
            int cchapter = Integer.parseInt(root.getAttributeValue(KXmlParser.NO_NAMESPACE, USERDATA_CHAPTER_ATTRIB));
            int cposition = Integer.parseInt(root.getAttributeValue(KXmlParser.NO_NAMESPACE, USERDATA_POSITION_ATTRIB));

            if (crc != this.archive.getCRC())
                throw new BookException("Wrong CRC");

            System.out.println("current pos@chap: " + cposition + "@" + cchapter);
            
            int child_count = root.getChildCount();
            for (int i = 0; i < child_count ; i++ ) {
                if (root.getType(i) != Node.ELEMENT) {
                    continue;
                }

                kid = root.getElement(i);
                if (kid.getName().equals(USERDATA_BOOKMARK_TAG)) {
                    String text = kid.getText(0);
                    if (text == null)
                        text = "";
                    int chapter = Integer.parseInt(kid.getAttributeValue(KXmlParser.NO_NAMESPACE, USERDATA_CHAPTER_ATTRIB));
                    int position = Integer.parseInt(kid.getAttributeValue(KXmlParser.NO_NAMESPACE, USERDATA_POSITION_ATTRIB));
                    if (position < 0)
                        position = 0;
                    System.out.println("Bookmark: " + chapter + " (" + position + "): [" + text + "]");
                    bookmarks.addElement(new Bookmark(getChapterByNo(chapter), position, text));

                }
            }
            currentChapter = getChapterByNo(cchapter);
            currentChapterPos = cposition;
        } catch (NullPointerException npe) {
            bookmarks.removeAllElements();
            throw new BookException("Missing info (NP Exception).");

        } catch (IllegalArgumentException iae) {
            bookmarks.removeAllElements();
            throw new BookException("Wrong data.");

        } catch (RuntimeException re) {
            //document has not root element
            bookmarks.removeAllElements();
            throw new BookException("Wrong data.");

        } finally {
            if (in != null)
                in.close();
        }
    }

    public synchronized void saveUserData() {
        //        Saving book info
        if (firstChapter != null && //i.e. if any chapters have been read
            userfile != null //i.e. the file is OK for writing
            ) {
            //lets try to save
            try {
                userfile.truncate(0);
                DataOutputStream dout = userfile.openDataOutputStream();
                try {
                    //Root element
                    //<book crc="123456789" chapter="3" position="1234">
                    dout.write("<".getBytes(TEXT_ENCODING));
                    dout.write(USERDATA_BOOK_TAG.getBytes(TEXT_ENCODING));
                    dout.write(" ".getBytes(TEXT_ENCODING));
                    dout.write(USERDATA_CRC_ATTRIB.getBytes(TEXT_ENCODING));
                    dout.write("=\"".getBytes(TEXT_ENCODING));
                    dout.write(Integer.toString(archive.getCRC()).getBytes(TEXT_ENCODING));
                    dout.write("\" ".getBytes(TEXT_ENCODING));
                    dout.write(USERDATA_CHAPTER_ATTRIB.getBytes(TEXT_ENCODING));
                    dout.write("=\"".getBytes(TEXT_ENCODING));
                    dout.write(Integer.toString(currentChapter.getChapterNo()).getBytes(TEXT_ENCODING));
                    dout.write("\" ".getBytes(TEXT_ENCODING));
                    dout.write(USERDATA_POSITION_ATTRIB.getBytes(TEXT_ENCODING));
                    dout.write("=\"".getBytes(TEXT_ENCODING));
                    dout.write(Integer.toString(currentChapterPos).getBytes(TEXT_ENCODING));
                    dout.write("\">\n".getBytes(TEXT_ENCODING));

                    //bookmarks
                    //<bookmark chapter="3" position="1234">This is some text</bookmark>
                    for (int i=0; i<bookmarks.size(); i++) {
                        Bookmark bookmark = (Bookmark)bookmarks.elementAt(i);

                        dout.write("<".getBytes(TEXT_ENCODING));
                        dout.write(USERDATA_BOOKMARK_TAG.getBytes(TEXT_ENCODING));
                        dout.write(" ".getBytes(TEXT_ENCODING));
                        dout.write(USERDATA_CHAPTER_ATTRIB.getBytes(TEXT_ENCODING));
                        dout.write("=\"".getBytes(TEXT_ENCODING));
                        dout.write(Integer.toString(bookmark.getChapter().getChapterNo()).getBytes(TEXT_ENCODING));
                        dout.write("\" ".getBytes(TEXT_ENCODING));
                        dout.write(USERDATA_POSITION_ATTRIB.getBytes(TEXT_ENCODING));
                        dout.write("=\"".getBytes(TEXT_ENCODING));
                        dout.write(Integer.toString(bookmark.getPosition()).getBytes(TEXT_ENCODING));
                        dout.write("\">".getBytes(TEXT_ENCODING));
                        dout.write(bookmark.getText().getBytes(TEXT_ENCODING));
                        dout.write("</".getBytes(TEXT_ENCODING));
                        dout.write(USERDATA_BOOKMARK_TAG.getBytes(TEXT_ENCODING));
                        dout.write(">\n".getBytes(TEXT_ENCODING));
                    }
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

    public Chapter getChapterByNo(int no) {
        Chapter bc = firstChapter;
        while (bc != null) {
            if (bc.getChapterNo() == no)
                return bc;
            bc = bc.getNextChapter();
        }
        return firstChapter;
    }

    public Archive getArchive() {
        return archive;
    }

    private void updateTimeSpentReading() {
        timeSpentReading = (int)((System.currentTimeMillis() - timeFromLastCheck)/1000);
        timeFromLastCheck = System.currentTimeMillis();
    }
}