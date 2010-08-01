package org.albite.book.book;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import javax.microedition.rms.RecordStore;
import org.albite.dictionary.Dictionary;
import org.albite.util.archive.Archive;
import org.albite.util.archive.ArchivedFile;
import org.geometerplus.zlibrary.text.hyphenation.ZLTextTeXHyphenator;
import org.kxml2.io.KXmlParser;
import org.kxml2.kdom.Document;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;
import org.xmlpull.v1.XmlPullParserException;

//a singleton for performance reasons, mainly memory fragmentation and garbage collection
//syncs not neccessary for this application; may be implemented in future
public class Book {
    final private static String BOOK_TITLE_TAG          = "title";
    final private static String BOOK_AUTHOR_TAG         = "author";
    final private static String BOOK_DESCRIPTION_TAG    = "description";
    final private static String BOOK_LANGUAGE_TAG       = "language";
    final private static String BOOK_META_TAG           = "meta";

    final private static String CHAPTER_TAG             = "chapter";

    final private static ZLTextTeXHyphenator hyphenator = new ZLTextTeXHyphenator();;
    final private static Dictionary          dictionary = new Dictionary();

    // Meta Info
    private String  title       = "Untitled";
    private String  author      = "Unknown Author";
    private short   language    = Languages.LANG_UNKNOWN;
    private String  description = "No description";

    private Hashtable meta; //contains various book attribs, e.g. 'fiction', 'for_children', 'prose', etc.

    //The File
    private Archive archive     = null;

    //Chapters
    private BookChapter   firstChapter;
    private int           chaptersCount;
    private BookChapter   currentChapter;

    //User data; statistics
    private int           timeSpentReading = 0; //in seconds
    private long          timeFromLastCheck; //used from the last time secondsSpentReading was updated

    public BookChapter getCurrentChapter() {
        return currentChapter;
    }

    public void setCurrentChapter(BookChapter bc) {
        currentChapter = bc;
    }

    public ZLTextTeXHyphenator getHyphenator() {
        return hyphenator;
    }

    public Dictionary getDictionary() {
        return dictionary;
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

            //load hyphenator and dictionary according to book language
            hyphenator.load(language);
            dictionary.load(language);
////            hyphenator.load((short)(getBook().language + 4));
////            dictionary.load((short)(getBook().language + 4));
//
//            //
//            recordId = getIDForBook();
//            if (recordId == 0) {
//                //new book, nothing to load
//            } else {
//                try {
//                    byte[] data = rs.getRecord(recordId);
//                    DataInputStream din = new DataInputStream(new ByteArrayInputStream(data));
//                    //skip what we know and don't need
//                    din.readUTF();
//                    din.readInt();
//                    currentChapter = getChapterByNo(din.readInt());
//                    currentChapter.currentPosition = din.readInt();
//
//                } catch (RecordStoreException rse) {
//                    rse.printStackTrace();
//                }
//            }

        } catch (BookException be) {
            close();
            throw be;
        }
        timeFromLastCheck = System.currentTimeMillis();
    }

//    int getIDForBook() {
//        try {
//            int recordId_;
//            RecordEnumeration re = rs.enumerateRecords(null, null, false);
//            String fileURL;
//            int CRC;
//            try {
//                while (re.hasNextElement()) {
//                    recordId_ = re.nextRecordId();
//
//                    byte[] data = rs.getRecord(recordId_);
//                    DataInputStream din = new DataInputStream(new ByteArrayInputStream(data));
//                    fileURL = din.readUTF();
//                    CRC = din.readInt();
//
//                    if (fileURL.equals(archive.getFileURL()) && this.crc32 == CRC)
//                        return recordId_;
//                }
//            } catch (IOException ioe) {
//                ioe.printStackTrace();
//            }
//        } catch (RecordStoreException rse) {
//            rse.printStackTrace();
//        }
//        return 0; //Not found
//    }

    public void close() {

//        Saving book info
//        if (firstChapter != null) { //i.e. if any chapters have been read
//            //lets try to save
//            try {
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                DataOutputStream dout = new DataOutputStream(baos);
//                dout.writeUTF(archive.getFileURL());
//                dout.writeInt(crc32);
//                dout.writeInt(currentChapter.chapterNo);
//                dout.writeInt(currentChapter.currentPosition);
//                byte[] data = baos.toByteArray();
//                dout.close();
//                if (recordId == 0) {
//                    //make a new entry
//                    rs.addRecord(data, 0, data.length);
//                } else {
//                    //update entry
//                    rs.setRecord(recordId, data, 0, data.length);
//                }
//            } catch (RecordStoreException rse) {
//                rse.printStackTrace();
//            } catch (IOException ioe) {
//                ioe.printStackTrace();
//            }
//
//            //killing connections between chapters in order to easy GC
//            BookChapter chap = firstChapter;
//            BookChapter chap_;
//            while (chap.nextChapter != null) {
//                chap_ = chap;
//                chap = chap.nextChapter;
//                chap_.nextChapter = null;
//            }
//            while (chap.prevChapter != null) {
//                chap_ = chap;
//                chap = chap.prevChapter;
//                chap_.prevChapter = null;
//            }
//            chaptersCount = 0;
//            currentChapter = null;
//        }

        try {
            archive.close();
            archive = null;
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
        BookChapter chap = firstChapter;
        while(chap != null) {
            chap.unload();
            chap = chap.nextChapter;
        }
    }

    public boolean opened() {
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
        BookChapter lastChapter = null;

        ArchivedFile af = null;

        for (int i = 0; i < child_count ; i++ ) {
            if (root.getType(i) != Node.ELEMENT) {
                    continue;
            }

            kid = root.getElement(i);
            if (kid.getName().equals(CHAPTER_TAG)) {
                chaptersCount++;
                
                chapterFileName = kid.getAttributeValue(KXmlParser.NO_NAMESPACE, "src");

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
                    firstChapter = new BookChapter(af, chapterTitle, chaptersCount);
                    lastChapter = firstChapter;
                    isFirst = false;
                } else {
                    lastChapter.nextChapter = new BookChapter(af, chapterTitle, chaptersCount);
                    lastChapter.nextChapter.prevChapter = lastChapter;
                    lastChapter = lastChapter.nextChapter;
                }
            }
        }
        if (chaptersCount < 1)
            throw new BookException("No chapters were found in the TOC descriptor.");

        currentChapter = firstChapter; //value will be overwritten by the RMS method
    }

    public BookChapter getChapterByNo(int no) {
        BookChapter bc = firstChapter;
        while (bc != null) {
            if (bc.chapterNo == no)
                return bc;
            bc = bc.nextChapter;
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