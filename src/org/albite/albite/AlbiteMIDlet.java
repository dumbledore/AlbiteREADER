/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.albite;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import org.albite.book.model.book.Book;
import org.albite.book.model.book.BookException;
import org.albite.book.model.book.Bookmark;
import org.albite.book.model.book.BookmarkManager;
import org.albite.dictionary.DictionaryManager;
import org.albite.dictionary.Dictionary;
import org.albite.io.decoders.Encodings;
import org.albite.util.units.Unit;
import org.albite.util.units.UnitGroup;
import org.geometerplus.zlibrary.text.hyphenation.Languages;
import org.netbeans.microedition.lcdui.SplashScreen;
import org.netbeans.microedition.lcdui.WaitScreen;
import org.netbeans.microedition.lcdui.pda.FileBrowser;
import org.netbeans.microedition.lcdui.pda.FolderBrowser;
import org.netbeans.microedition.util.SimpleCancellableTask;

/**
 * @author Albus Dumbledore
 */
public class AlbiteMIDlet extends MIDlet
        implements CommandListener {

    /*
     * App
     */
    private boolean                 midletPaused            = false;
    private boolean                 firstTime               = false;
    private final String            version;
    private RecordStore             rs;

    public final boolean            lightMode;
    public final boolean            debugMode;

    /*
     * Folders
     */
    private String                  dictsFolder             = "";

    /*
     * Book
     */
    private String                  bookURL;

    /*
     * Section: Dictionary / Converter
     */
    private String                  entryForLookup;

    /*
     * Dictionary
     */
    public final DictionaryManager  dictman = new DictionaryManager();

    private Dictionary              selectedDictionary      = null;
    private String[]                searchResult            = null;
    private String                  searchWord              = null;

    /*
     * Number
     */
    private boolean                 numberOK                = true;

    /*
     * Bookmarks
     */
    private int                     bookmarkPosition        = 0;
    private String                  bookmarkString          = "";
    private boolean                 bookmarkAdding          = true;

    /*
     * Menu
     */
    private boolean                 calledOutside           = false;
//    private boolean                 calledContext           = false;
    private boolean                 showColors              = false;

    public AlbiteMIDlet() {
        final String v = getAppProperty("MIDlet-Version");

        if (v == null) {
            version = "unknown version";
        } else {
            version = v;
        }

        String s;
        s = getAppProperty("Light-Mode");
        lightMode = "true".equalsIgnoreCase(s);

        s = getAppProperty("Debug-Mode");
        debugMode = "true".equalsIgnoreCase(s);
    }
    
    //<editor-fold defaultstate="collapsed" desc=" Generated Fields ">//GEN-BEGIN:|fields|0|
    private Command DISMISS_COMMAND;
    private Command CANCEL_COMMAND;
    private Command BACK_COMMAND;
    private Command NEXT_COMMAND;
    private Command CLOSE_COMMAND;
    private Command GO_COMMAND;
    private Command NO_COMMAND;
    private Command YES_COMMAND;
    private Command APPLY_COMMAND;
    private Command RESTART_COMMAND;
    private Command DELETE_COMMAND;
    private Command EDIT_COMMAND;
    private Command ADD_COMMAND;
    private FileBrowser bookBrowser;
    private List encodings;
    private Alert bookError;
    private WaitScreen reflowChapterScreen;
    private WaitScreen loadBook;
    private BookCanvas bookCanvas;
    private List suggestions;
    private Form wordDefinition;
    private StringItem dictrionaryStringItem;
    private StringItem wordStringItem;
    private StringItem definitionStringItem;
    private TextBox wordBox;
    private List unitGroups;
    private TextBox numberBox;
    private List unitFrom;
    private List unitTo;
    private Alert numberError;
    private Form conversionResult;
    private StringItem resultFromQuantity;
    private StringItem resultFromUnit;
    private StringItem resultToUnit;
    private StringItem resultToQuantity;
    private List toc;
    private Form acceptLicense;
    private StringItem license1;
    private StringItem license5;
    private StringItem license13;
    private List chapterPositions;
    private SplashScreen splashScreen;
    private List menu;
    private Form showLicense;
    private ImageItem imageItem;
    private StringItem stringItem3;
    private StringItem stringItem2;
    private StringItem stringItem1;
    private StringItem stringItem;
    private Alert exitBox;
    private List fontSizes;
    private Form scrollingOptions;
    private ChoiceGroup smoothScrolling;
    private ChoiceGroup scrollingDirectionGroup;
    private Gauge scrollingSpeed;
    private Gauge holdingTimeMultiplier;
    private List schemes;
    private List colors;
    private Form selectPercent;
    private Gauge chapterPercent;
    private List screenModes;
    private Alert noDictionaries;
    private List dictionaries;
    private WaitScreen lookup;
    private Form bookInfo;
    private Alert dictionaryError;
    private List bookmarks;
    private Alert deleteBookmarkAlert;
    private TextBox bookmarkText;
    private WaitScreen scanningDictionaries;
    private Form pageSettings;
    private ChoiceGroup reloadImages;
    private Gauge lineSpacing;
    private Gauge pageMargins;
    private FolderBrowser folderBrowser;
    private List languages;
    private Alert noBookmarksFound;
    private SimpleCancellableTask applyScreenModeTask;
    private SimpleCancellableTask goToBookmarkTask;
    private SimpleCancellableTask applyPageOptionsTask;
    private SimpleCancellableTask goToChapterTask;
    private SimpleCancellableTask applyFontSizeTask;
    private SimpleCancellableTask applyLanguageTask;
    private SimpleCancellableTask applyEncodingTask;
    private SimpleCancellableTask loadBookTask;
    private Image albiteLogo;
    private Font loadingFont;
    private Font smallPlainFont;
    private Font underlinedFont;
    private Font normalFont;
    private SimpleCancellableTask lookupTask;
    private SimpleCancellableTask scanningDictionariesTask;
    //</editor-fold>//GEN-END:|fields|0|

    //<editor-fold defaultstate="collapsed" desc=" Generated Methods ">//GEN-BEGIN:|methods|0|
    //</editor-fold>//GEN-END:|methods|0|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: initialize ">//GEN-BEGIN:|0-initialize|0|0-preInitialize
    /**
     * Initilizes the application.
     * It is called only once when the MIDlet is started. The method is called before the <code>startMIDlet</code> method.
     */
    private void initialize() {//GEN-END:|0-initialize|0|0-preInitialize
        // write pre-initialize user code here
        bookCanvas = new BookCanvas(this);//GEN-BEGIN:|0-initialize|1|0-postInitialize
        bookCanvas.setTitle(null);
        bookCanvas.setFullScreenMode(true);//GEN-END:|0-initialize|1|0-postInitialize
        // write post-initialize user code here

        /* RMS */
        openRMSAndLoadData();

        /* Initialize Dictionary Manager */
        dictman.reloadDictionaries(dictsFolder);
        fillDicts();

        /*
         * The BookCanvas must be initialized before usage. This is because
         * of the fact, that it wouldn't have correct metrics, i.e.
         * wouldn't be in fullscreenmode when looked at from the constructor
         */
        bookCanvas.initialize();
    }//GEN-BEGIN:|0-initialize|2|
    //</editor-fold>//GEN-END:|0-initialize|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: startMIDlet ">//GEN-BEGIN:|3-startMIDlet|0|3-preAction
    /**
     * Performs an action assigned to the Mobile Device - MIDlet Started point.
     */
    public void startMIDlet() {//GEN-END:|3-startMIDlet|0|3-preAction
        // write pre-action user code here
        switchDisplayable(null, getSplashScreen());//GEN-LINE:|3-startMIDlet|1|3-postAction
        // write post-action user code here
    }//GEN-BEGIN:|3-startMIDlet|2|
    //</editor-fold>//GEN-END:|3-startMIDlet|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: resumeMIDlet ">//GEN-BEGIN:|4-resumeMIDlet|0|4-preAction
    /**
     * Performs an action assigned to the Mobile Device - MIDlet Resumed point.
     */
    public void resumeMIDlet() {//GEN-END:|4-resumeMIDlet|0|4-preAction
        // write pre-action user code here
//GEN-LINE:|4-resumeMIDlet|1|4-postAction
        // write post-action user code here
    }//GEN-BEGIN:|4-resumeMIDlet|2|
    //</editor-fold>//GEN-END:|4-resumeMIDlet|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: switchDisplayable ">//GEN-BEGIN:|5-switchDisplayable|0|5-preSwitch
    /**
     * Switches a current displayable in a display. The <code>display</code> instance is taken from <code>getDisplay</code> method. This method is used by all actions in the design for switching displayable.
     * @param alert the Alert which is temporarily set to the display; if <code>null</code>, then <code>nextDisplayable</code> is set immediately
     * @param nextDisplayable the Displayable to be set
     */
    public void switchDisplayable(Alert alert, Displayable nextDisplayable) {//GEN-END:|5-switchDisplayable|0|5-preSwitch
        // write pre-switch user code here
        Display display = getDisplay();//GEN-BEGIN:|5-switchDisplayable|1|5-postSwitch
        if (alert == null) {
            display.setCurrent(nextDisplayable);
        } else {
            display.setCurrent(alert, nextDisplayable);
        }//GEN-END:|5-switchDisplayable|1|5-postSwitch
        // write post-switch user code here
    }//GEN-BEGIN:|5-switchDisplayable|2|
    //</editor-fold>//GEN-END:|5-switchDisplayable|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: commandAction for Displayables ">//GEN-BEGIN:|7-commandAction|0|7-preCommandAction
    /**
     * Called by a system to indicated that a command has been invoked on a particular displayable.
     * @param command the Command that was invoked
     * @param displayable the Displayable where the command was invoked
     */
    public void commandAction(Command command, Displayable displayable) {//GEN-END:|7-commandAction|0|7-preCommandAction
        // write pre-action user code here
        if (displayable == acceptLicense) {//GEN-BEGIN:|7-commandAction|1|709-preAction
            if (command == NO_COMMAND) {//GEN-END:|7-commandAction|1|709-preAction
                // write pre-action user code here
                exitMIDlet();//GEN-LINE:|7-commandAction|2|709-postAction
                // write post-action user code here
            } else if (command == YES_COMMAND) {//GEN-LINE:|7-commandAction|3|715-preAction
                // write pre-action user code here
                /*
                 * Won't be needed anymore.
                 */
                acceptLicense = null;
                lastBookAvailable();//GEN-LINE:|7-commandAction|4|715-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|5|119-preAction
        } else if (displayable == bookBrowser) {
            if (command == CANCEL_COMMAND) {//GEN-END:|7-commandAction|5|119-preAction
                // write pre-action user code here
                displayBookCanvas();//GEN-LINE:|7-commandAction|6|119-postAction
                // write post-action user code here
            } else if (command == FileBrowser.SELECT_FILE_COMMAND) {//GEN-LINE:|7-commandAction|7|34-preAction
                // write pre-action user code here
                bookURL = getBookBrowser().getSelectedFileURL();
                switchDisplayable(null, getLoadBook());//GEN-LINE:|7-commandAction|8|34-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|9|103-preAction
        } else if (displayable == bookError) {
            if (command == DISMISS_COMMAND) {//GEN-END:|7-commandAction|9|103-preAction
                // write pre-action user code here
                switchDisplayable(null, getBookBrowser());//GEN-LINE:|7-commandAction|10|103-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|11|730-preAction
        } else if (displayable == bookInfo) {
            if (command == DISMISS_COMMAND) {//GEN-END:|7-commandAction|11|730-preAction
                // write pre-action user code here
                unloadInfo();//GEN-LINE:|7-commandAction|12|730-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|13|962-preAction
        } else if (displayable == bookmarkText) {
            if (command == BACK_COMMAND) {//GEN-END:|7-commandAction|13|962-preAction
                // write pre-action user code here
                returnToBookmarks();//GEN-LINE:|7-commandAction|14|962-postAction
                // write post-action user code here
            } else if (command == NEXT_COMMAND) {//GEN-LINE:|7-commandAction|15|963-preAction
                // write pre-action user code here
                processBookmark();//GEN-LINE:|7-commandAction|16|963-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|17|918-preAction
        } else if (displayable == bookmarks) {
            if (command == ADD_COMMAND) {//GEN-END:|7-commandAction|17|918-preAction
                // write pre-action user code here
                bookmarkAdding = true;
                getBookmarkText().setString(bookmarkString);
                switchDisplayable(null, getBookmarkText());//GEN-LINE:|7-commandAction|18|918-postAction
                // write post-action user code here
            } else if (command == BACK_COMMAND) {//GEN-LINE:|7-commandAction|19|915-preAction
                // write pre-action user code here
                returnToMenu();//GEN-LINE:|7-commandAction|20|915-postAction
                // write post-action user code here
            } else if (command == DELETE_COMMAND) {//GEN-LINE:|7-commandAction|21|922-preAction
                // write pre-action user code here
                switchDisplayable(getDeleteBookmarkAlert(), getBookmarks());//GEN-LINE:|7-commandAction|22|922-postAction
                // write post-action user code here
            } else if (command == EDIT_COMMAND) {//GEN-LINE:|7-commandAction|23|920-preAction
                // write pre-action user code here
                bookmarkAdding = false;

                final Book book = bookCanvas.getCurrentBook();
                final int pos = getBookmarks().getSelectedIndex();
                final Bookmark bookmark =
                        book.getBookmarkManager().bookmarkAt(pos);

                if (bookmark != null) {
                    getBookmarkText().setString(bookmark.getText());
                }

                canEditBookmark();//GEN-LINE:|7-commandAction|24|920-postAction
                // write post-action user code here
            } else if (command == GO_COMMAND) {//GEN-LINE:|7-commandAction|25|916-preAction
                // write pre-action user code here
                goToBookmark();//GEN-LINE:|7-commandAction|26|916-postAction
                // write post-action user code here
            } else if (command == List.SELECT_COMMAND) {//GEN-LINE:|7-commandAction|27|891-preAction
                // write pre-action user code here
                bookmarksAction();//GEN-LINE:|7-commandAction|28|891-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|29|705-preAction
        } else if (displayable == chapterPositions) {
            if (command == BACK_COMMAND) {//GEN-END:|7-commandAction|29|705-preAction
                // write pre-action user code here
                switchDisplayable(null, getToc());//GEN-LINE:|7-commandAction|30|705-postAction
                // write post-action user code here
            } else if (command == GO_COMMAND) {//GEN-LINE:|7-commandAction|31|360-preAction
                // write pre-action user code here
                caseSelectPercent();//GEN-LINE:|7-commandAction|32|360-postAction
                // write post-action user code here
            } else if (command == List.SELECT_COMMAND) {//GEN-LINE:|7-commandAction|33|355-preAction
                // write pre-action user code here
                chapterPositionsAction();//GEN-LINE:|7-commandAction|34|355-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|35|534-preAction
        } else if (displayable == colors) {
            if (command == APPLY_COMMAND) {//GEN-END:|7-commandAction|35|534-preAction
                // write pre-action user code here
                applyColorScheme();//GEN-LINE:|7-commandAction|36|534-postAction
                // write post-action user code here
            } else if (command == BACK_COMMAND) {//GEN-LINE:|7-commandAction|37|627-preAction
                // write pre-action user code here
                switchDisplayable(null, getSchemes());//GEN-LINE:|7-commandAction|38|627-postAction
                // write post-action user code here
            } else if (command == List.SELECT_COMMAND) {//GEN-LINE:|7-commandAction|39|531-preAction
                // write pre-action user code here
                colorsAction();//GEN-LINE:|7-commandAction|40|531-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|41|693-preAction
        } else if (displayable == conversionResult) {
            if (command == BACK_COMMAND) {//GEN-END:|7-commandAction|41|693-preAction
                // write pre-action user code here
                switchDisplayable(null, getUnitTo());//GEN-LINE:|7-commandAction|42|693-postAction
                // write post-action user code here
            } else if (command == CLOSE_COMMAND) {//GEN-LINE:|7-commandAction|43|293-preAction
                // write pre-action user code here
                switchDisplayable(null, bookCanvas);//GEN-LINE:|7-commandAction|44|293-postAction
                // write post-action user code here
            } else if (command == RESTART_COMMAND) {//GEN-LINE:|7-commandAction|45|696-preAction
                // write pre-action user code here
                switchDisplayable(null, getNumberBox());//GEN-LINE:|7-commandAction|46|696-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|47|904-preAction
        } else if (displayable == deleteBookmarkAlert) {
            if (command == NO_COMMAND) {//GEN-END:|7-commandAction|47|904-preAction
                // write pre-action user code here
                switchDisplayable(null, getBookmarks());//GEN-LINE:|7-commandAction|48|904-postAction
                // write post-action user code here
            } else if (command == YES_COMMAND) {//GEN-LINE:|7-commandAction|49|903-preAction
                // write pre-action user code here
                deleteBookmark();//GEN-LINE:|7-commandAction|50|903-postAction
                // write post-action user code here
                bookCanvas.saveBookmarks();
            }//GEN-BEGIN:|7-commandAction|51|768-preAction
        } else if (displayable == dictionaries) {
            if (command == BACK_COMMAND) {//GEN-END:|7-commandAction|51|768-preAction
                // write pre-action user code here
                switchDisplayable(null, getWordBox());//GEN-LINE:|7-commandAction|52|768-postAction
                // write post-action user code here
            } else if (command == List.SELECT_COMMAND) {//GEN-LINE:|7-commandAction|53|761-preAction
                // write pre-action user code here
                dictionariesAction();//GEN-LINE:|7-commandAction|54|761-postAction
                // write post-action user code here
            } else if (command == NEXT_COMMAND) {//GEN-LINE:|7-commandAction|55|769-preAction
                // write pre-action user code here
                clearSuggestions();//GEN-LINE:|7-commandAction|56|769-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|57|772-preAction
        } else if (displayable == dictionaryError) {
            if (command == DISMISS_COMMAND) {//GEN-END:|7-commandAction|57|772-preAction
                // write pre-action user code here
                switchDisplayable(null, getDictionaries());//GEN-LINE:|7-commandAction|58|772-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|59|1038-preAction
        } else if (displayable == encodings) {
            if (command == APPLY_COMMAND) {//GEN-END:|7-commandAction|59|1038-preAction
                // write pre-action user code here
                applyEncoding();//GEN-LINE:|7-commandAction|60|1038-postAction
                // write post-action user code here
            } else if (command == BACK_COMMAND) {//GEN-LINE:|7-commandAction|61|1039-preAction
                // write pre-action user code here
                returnToMenu();//GEN-LINE:|7-commandAction|62|1039-postAction
                // write post-action user code here
            } else if (command == List.SELECT_COMMAND) {//GEN-LINE:|7-commandAction|63|1027-preAction
                // write pre-action user code here
                encodingsAction();//GEN-LINE:|7-commandAction|64|1027-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|65|551-preAction
        } else if (displayable == exitBox) {
            if (command == NO_COMMAND) {//GEN-END:|7-commandAction|65|551-preAction
                // write pre-action user code here
                returnToMenu();//GEN-LINE:|7-commandAction|66|551-postAction
                // write post-action user code here
            } else if (command == YES_COMMAND) {//GEN-LINE:|7-commandAction|67|549-preAction
                // write pre-action user code here
                exitMIDlet();//GEN-LINE:|7-commandAction|68|549-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|69|856-preAction
        } else if (displayable == folderBrowser) {
            if (command == CANCEL_COMMAND) {//GEN-END:|7-commandAction|69|856-preAction
                // write pre-action user code here
                returnToMenu();//GEN-LINE:|7-commandAction|70|856-postAction
                // write post-action user code here
            } else if (command == List.SELECT_COMMAND) {//GEN-LINE:|7-commandAction|71|853-preAction
                // write pre-action user code here
                folderBrowserAction();//GEN-LINE:|7-commandAction|72|853-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|73|641-preAction
        } else if (displayable == fontSizes) {
            if (command == APPLY_COMMAND) {//GEN-END:|7-commandAction|73|641-preAction
                // write pre-action user code here
                applyFontSize();//GEN-LINE:|7-commandAction|74|641-postAction
                // write post-action user code here
            } else if (command == BACK_COMMAND) {//GEN-LINE:|7-commandAction|75|642-preAction
                // write pre-action user code here
                returnToMenu();//GEN-LINE:|7-commandAction|76|642-postAction
                // write post-action user code here
            } else if (command == List.SELECT_COMMAND) {//GEN-LINE:|7-commandAction|77|559-preAction
                // write pre-action user code here
                fontSizesAction();//GEN-LINE:|7-commandAction|78|559-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|79|1029-preAction
        } else if (displayable == languages) {
            if (command == APPLY_COMMAND) {//GEN-END:|7-commandAction|79|1029-preAction
                // write pre-action user code here
                applyLanguage();//GEN-LINE:|7-commandAction|80|1029-postAction
                // write post-action user code here
            } else if (command == BACK_COMMAND) {//GEN-LINE:|7-commandAction|81|1032-preAction
                // write pre-action user code here
                returnToMenu();//GEN-LINE:|7-commandAction|82|1032-postAction
                // write post-action user code here
            } else if (command == List.SELECT_COMMAND) {//GEN-LINE:|7-commandAction|83|1024-preAction
                // write pre-action user code here
                languagesAction();//GEN-LINE:|7-commandAction|84|1024-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|85|159-preAction
        } else if (displayable == loadBook) {
            if (command == WaitScreen.FAILURE_COMMAND) {//GEN-END:|7-commandAction|85|159-preAction
                // write pre-action user code here
                switchDisplayable(null, getBookError());//GEN-LINE:|7-commandAction|86|159-postAction
                // write post-action user code here
            } else if (command == WaitScreen.SUCCESS_COMMAND) {//GEN-LINE:|7-commandAction|87|158-preAction
                // write pre-action user code here
                method();//GEN-LINE:|7-commandAction|88|158-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|89|766-preAction
        } else if (displayable == lookup) {
            if (command == WaitScreen.FAILURE_COMMAND) {//GEN-END:|7-commandAction|89|766-preAction
                // write pre-action user code here
                switchDisplayable(null, getDictionaryError());//GEN-LINE:|7-commandAction|90|766-postAction
                // write post-action user code here
            } else if (command == WaitScreen.SUCCESS_COMMAND) {//GEN-LINE:|7-commandAction|91|765-preAction
                // write pre-action user code here
                wordFound();//GEN-LINE:|7-commandAction|92|765-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|93|630-preAction
        } else if (displayable == menu) {
            if (command == BACK_COMMAND) {//GEN-END:|7-commandAction|93|630-preAction
                // write pre-action user code here
                switchDisplayable(null, bookCanvas);//GEN-LINE:|7-commandAction|94|630-postAction
                // write post-action user code here
            } else if (command == List.SELECT_COMMAND) {//GEN-LINE:|7-commandAction|95|430-preAction
                // write pre-action user code here
                menuAction();//GEN-LINE:|7-commandAction|96|430-postAction
                // write post-action user code here
            } else if (command == NEXT_COMMAND) {//GEN-LINE:|7-commandAction|97|629-preAction
                // write pre-action user code here
                menuAction();//GEN-LINE:|7-commandAction|98|629-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|99|977-preAction
        } else if (displayable == noBookmarksFound) {
            if (command == DISMISS_COMMAND) {//GEN-END:|7-commandAction|99|977-preAction
                // write pre-action user code here
                switchDisplayable(null, getBookmarks());//GEN-LINE:|7-commandAction|100|977-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|101|754-preAction
        } else if (displayable == noDictionaries) {
            if (command == DISMISS_COMMAND) {//GEN-END:|7-commandAction|101|754-preAction
                // write pre-action user code here
                returnToMenu();//GEN-LINE:|7-commandAction|102|754-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|103|667-preAction
        } else if (displayable == numberBox) {
            if (command == BACK_COMMAND) {//GEN-END:|7-commandAction|103|667-preAction
                // write pre-action user code here
                returnToMenu();//GEN-LINE:|7-commandAction|104|667-postAction
                // write post-action user code here
            } else if (command == NEXT_COMMAND) {//GEN-LINE:|7-commandAction|105|668-preAction
                // write pre-action user code here
                isNumberOKCheck();//GEN-LINE:|7-commandAction|106|668-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|107|717-preAction
        } else if (displayable == numberError) {
            if (command == DISMISS_COMMAND) {//GEN-END:|7-commandAction|107|717-preAction
                // write pre-action user code here
                switchDisplayable(null, getNumberBox());//GEN-LINE:|7-commandAction|108|717-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|109|882-preAction
        } else if (displayable == pageSettings) {
            if (command == APPLY_COMMAND) {//GEN-END:|7-commandAction|109|882-preAction
                // write pre-action user code here
                applyPageOptions();//GEN-LINE:|7-commandAction|110|882-postAction
                // write post-action user code here
            } else if (command == BACK_COMMAND) {//GEN-LINE:|7-commandAction|111|883-preAction
                // write pre-action user code here
                returnToMenu();//GEN-LINE:|7-commandAction|112|883-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|113|1058-preAction
        } else if (displayable == reflowChapterScreen) {
            if (command == WaitScreen.FAILURE_COMMAND) {//GEN-END:|7-commandAction|113|1058-preAction
                // write pre-action user code here
//GEN-LINE:|7-commandAction|114|1058-postAction
                // write post-action user code here
            } else if (command == WaitScreen.SUCCESS_COMMAND) {//GEN-LINE:|7-commandAction|115|1057-preAction
                // write pre-action user code here
                switchDisplayable(null, bookCanvas);//GEN-LINE:|7-commandAction|116|1057-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|117|871-preAction
        } else if (displayable == scanningDictionaries) {
            if (command == WaitScreen.FAILURE_COMMAND) {//GEN-END:|7-commandAction|117|871-preAction
                // write pre-action user code here
                switchDisplayable(null, bookCanvas);//GEN-LINE:|7-commandAction|118|871-postAction
                // write post-action user code here
            } else if (command == WaitScreen.SUCCESS_COMMAND) {//GEN-LINE:|7-commandAction|119|870-preAction
                // write pre-action user code here
                switchDisplayable(null, bookCanvas);//GEN-LINE:|7-commandAction|120|870-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|121|623-preAction
        } else if (displayable == schemes) {
            if (command == BACK_COMMAND) {//GEN-END:|7-commandAction|121|623-preAction
                // write pre-action user code here
                returnToMenu();//GEN-LINE:|7-commandAction|122|623-postAction
                // write post-action user code here
            } else if (command == List.SELECT_COMMAND) {//GEN-LINE:|7-commandAction|123|513-preAction
                // write pre-action user code here
                schemesAction();//GEN-LINE:|7-commandAction|124|513-postAction
                // write post-action user code here
            } else if (command == NEXT_COMMAND) {//GEN-LINE:|7-commandAction|125|624-preAction
                // write pre-action user code here
                showColors = (schemes.getSelectedIndex() != 0);
                showColorPicker();//GEN-LINE:|7-commandAction|126|624-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|127|702-preAction
        } else if (displayable == screenModes) {
            if (command == APPLY_COMMAND) {//GEN-END:|7-commandAction|127|702-preAction
                // write pre-action user code here
                applyScreenMode();//GEN-LINE:|7-commandAction|128|702-postAction
                // write post-action user code here
            } else if (command == BACK_COMMAND) {//GEN-LINE:|7-commandAction|129|701-preAction
                // write pre-action user code here
                returnToMenu();//GEN-LINE:|7-commandAction|130|701-postAction
                // write post-action user code here
            } else if (command == List.SELECT_COMMAND) {//GEN-LINE:|7-commandAction|131|591-preAction
                // write pre-action user code here
                screenModesAction();//GEN-LINE:|7-commandAction|132|591-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|133|637-preAction
        } else if (displayable == scrollingOptions) {
            if (command == APPLY_COMMAND) {//GEN-END:|7-commandAction|133|637-preAction
                // write pre-action user code here
                applyScrollingOptions();//GEN-LINE:|7-commandAction|134|637-postAction
                // write post-action user code here
            } else if (command == BACK_COMMAND) {//GEN-LINE:|7-commandAction|135|638-preAction
                // write pre-action user code here
                returnToMenu();//GEN-LINE:|7-commandAction|136|638-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|137|663-preAction
        } else if (displayable == selectPercent) {
            if (command == APPLY_COMMAND) {//GEN-END:|7-commandAction|137|663-preAction
                // write pre-action user code here
                goToChapter();//GEN-LINE:|7-commandAction|138|663-postAction
                // write post-action user code here
            } else if (command == BACK_COMMAND) {//GEN-LINE:|7-commandAction|139|662-preAction
                // write pre-action user code here
                switchDisplayable(null, getChapterPositions());//GEN-LINE:|7-commandAction|140|662-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|141|732-preAction
        } else if (displayable == showLicense) {
            if (command == DISMISS_COMMAND) {//GEN-END:|7-commandAction|141|732-preAction
                // write pre-action user code here
                returnToMenu();//GEN-LINE:|7-commandAction|142|732-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|143|368-preAction
        } else if (displayable == splashScreen) {
            if (command == SplashScreen.DISMISS_COMMAND) {//GEN-END:|7-commandAction|143|368-preAction
                // write pre-action user code here
                runsForTheFirstTime();//GEN-LINE:|7-commandAction|144|368-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|145|206-preAction
        } else if (displayable == suggestions) {
            if (command == BACK_COMMAND) {//GEN-END:|7-commandAction|145|206-preAction
                // write pre-action user code here
                switchDisplayable(null, getDictionaries());//GEN-LINE:|7-commandAction|146|206-postAction
                // write post-action user code here
            } else if (command == List.SELECT_COMMAND) {//GEN-LINE:|7-commandAction|147|189-preAction
                // write pre-action user code here
                suggestionsAction();//GEN-LINE:|7-commandAction|148|189-postAction
                // write post-action user code here
            } else if (command == NEXT_COMMAND) {//GEN-LINE:|7-commandAction|149|210-preAction
                // write pre-action user code here
                setWord();//GEN-LINE:|7-commandAction|150|210-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|151|633-preAction
        } else if (displayable == toc) {
            if (command == BACK_COMMAND) {//GEN-END:|7-commandAction|151|633-preAction
                // write pre-action user code here
                returnToMenu();//GEN-LINE:|7-commandAction|152|633-postAction
                // write post-action user code here
            } else if (command == List.SELECT_COMMAND) {//GEN-LINE:|7-commandAction|153|326-preAction
                // write pre-action user code here
                tocAction();//GEN-LINE:|7-commandAction|154|326-postAction
                // write post-action user code here
            } else if (command == NEXT_COMMAND) {//GEN-LINE:|7-commandAction|155|634-preAction
                // write pre-action user code here
                switchDisplayable(null, getChapterPositions());//GEN-LINE:|7-commandAction|156|634-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|157|686-preAction
        } else if (displayable == unitFrom) {
            if (command == BACK_COMMAND) {//GEN-END:|7-commandAction|157|686-preAction
                // write pre-action user code here
                switchDisplayable(null, getUnitGroups());//GEN-LINE:|7-commandAction|158|686-postAction
                // write post-action user code here
            } else if (command == List.SELECT_COMMAND) {//GEN-LINE:|7-commandAction|159|267-preAction
                // write pre-action user code here
                unitFromAction();//GEN-LINE:|7-commandAction|160|267-postAction
                // write post-action user code here
            } else if (command == NEXT_COMMAND) {//GEN-LINE:|7-commandAction|161|687-preAction
                // write pre-action user code here
                switchDisplayable(null, getUnitTo());//GEN-LINE:|7-commandAction|162|687-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|163|679-preAction
        } else if (displayable == unitGroups) {
            if (command == BACK_COMMAND) {//GEN-END:|7-commandAction|163|679-preAction
                // write pre-action user code here
                switchDisplayable(null, getNumberBox());//GEN-LINE:|7-commandAction|164|679-postAction
                // write post-action user code here
            } else if (command == List.SELECT_COMMAND) {//GEN-LINE:|7-commandAction|165|256-preAction
                // write pre-action user code here
                unitGroupsAction();//GEN-LINE:|7-commandAction|166|256-postAction
                // write post-action user code here
            } else if (command == NEXT_COMMAND) {//GEN-LINE:|7-commandAction|167|672-preAction
                // write pre-action user code here
                loadUnitsToLists();//GEN-LINE:|7-commandAction|168|672-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|169|646-preAction
        } else if (displayable == unitTo) {
            if (command == BACK_COMMAND) {//GEN-END:|7-commandAction|169|646-preAction
                // write pre-action user code here
                switchDisplayable(null, getUnitFrom());//GEN-LINE:|7-commandAction|170|646-postAction
                // write post-action user code here
            } else if (command == List.SELECT_COMMAND) {//GEN-LINE:|7-commandAction|171|270-preAction
                // write pre-action user code here
                unitToAction();//GEN-LINE:|7-commandAction|172|270-postAction
                // write post-action user code here
            } else if (command == NEXT_COMMAND) {//GEN-LINE:|7-commandAction|173|691-preAction
                // write pre-action user code here
                convertUnits();//GEN-LINE:|7-commandAction|174|691-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|175|741-preAction
        } else if (displayable == wordBox) {
            if (command == BACK_COMMAND) {//GEN-END:|7-commandAction|175|741-preAction
                // write pre-action user code here
                returnToMenu();//GEN-LINE:|7-commandAction|176|741-postAction
                // write post-action user code here
            } else if (command == NEXT_COMMAND) {//GEN-LINE:|7-commandAction|177|711-preAction
                // write pre-action user code here
                switchDisplayable(null, getDictionaries());//GEN-LINE:|7-commandAction|178|711-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|179|785-preAction
        } else if (displayable == wordDefinition) {
            if (command == BACK_COMMAND) {//GEN-END:|7-commandAction|179|785-preAction
                // write pre-action user code here
                backToSuggestions();//GEN-LINE:|7-commandAction|180|785-postAction
                // write post-action user code here
            } else if (command == CLOSE_COMMAND) {//GEN-LINE:|7-commandAction|181|784-preAction
                // write pre-action user code here
                returnToMenu();//GEN-LINE:|7-commandAction|182|784-postAction
                // write post-action user code here
            } else if (command == RESTART_COMMAND) {//GEN-LINE:|7-commandAction|183|786-preAction
                // write pre-action user code here
                switchDisplayable(null, getWordBox());//GEN-LINE:|7-commandAction|184|786-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|185|7-postCommandAction
        }//GEN-END:|7-commandAction|185|7-postCommandAction
        // write post-action user code here

        /*
         * Here is the place to process non-standard displayables
         */
        if (displayable == folderBrowser) {
            if (command == FolderBrowser.SELECT_FOLDER_COMMAND) {
                scanDictionaries();
            }
        }
    }//GEN-BEGIN:|7-commandAction|186|
    //</editor-fold>//GEN-END:|7-commandAction|186|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: bookBrowser ">//GEN-BEGIN:|32-getter|0|32-preInit
    /**
     * Returns an initiliazed instance of bookBrowser component.
     * @return the initialized component instance
     */
    public FileBrowser getBookBrowser() {
        if (bookBrowser == null) {//GEN-END:|32-getter|0|32-preInit
            // write pre-init user code here
            bookBrowser = new FileBrowser(getDisplay());//GEN-BEGIN:|32-getter|1|32-postInit
            bookBrowser.setTitle("Open book");
            bookBrowser.setCommandListener(this);
            bookBrowser.setFilter("");
            bookBrowser.addCommand(FileBrowser.SELECT_FILE_COMMAND);
            bookBrowser.addCommand(getCANCEL_COMMAND());//GEN-END:|32-getter|1|32-postInit
            // write post-init user code here
            bookBrowser.setDir(getCurrentBookFolder());
        }//GEN-BEGIN:|32-getter|2|
        return bookBrowser;
    }
    //</editor-fold>//GEN-END:|32-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: DISMISS_COMMAND ">//GEN-BEGIN:|102-getter|0|102-preInit
    /**
     * Returns an initiliazed instance of DISMISS_COMMAND component.
     * @return the initialized component instance
     */
    public Command getDISMISS_COMMAND() {
        if (DISMISS_COMMAND == null) {//GEN-END:|102-getter|0|102-preInit
            // write pre-init user code here
            DISMISS_COMMAND = new Command("Dismiss", Command.OK, 0);//GEN-LINE:|102-getter|1|102-postInit
            // write post-init user code here
        }//GEN-BEGIN:|102-getter|2|
        return DISMISS_COMMAND;
    }
    //</editor-fold>//GEN-END:|102-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: CANCEL_COMMAND ">//GEN-BEGIN:|118-getter|0|118-preInit
    /**
     * Returns an initiliazed instance of CANCEL_COMMAND component.
     * @return the initialized component instance
     */
    public Command getCANCEL_COMMAND() {
        if (CANCEL_COMMAND == null) {//GEN-END:|118-getter|0|118-preInit
            // write pre-init user code here
            CANCEL_COMMAND = new Command("Cancel", Command.CANCEL, 0);//GEN-LINE:|118-getter|1|118-postInit
            // write post-init user code here
        }//GEN-BEGIN:|118-getter|2|
        return CANCEL_COMMAND;
    }
    //</editor-fold>//GEN-END:|118-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: bookError ">//GEN-BEGIN:|101-getter|0|101-preInit
    /**
     * Returns an initiliazed instance of bookError component.
     * @return the initialized component instance
     */
    public Alert getBookError() {
        if (bookError == null) {//GEN-END:|101-getter|0|101-preInit
            // write pre-init user code here
            bookError = new Alert("Sorry!", "Sorry, cannot open this book.", null, AlertType.WARNING);//GEN-BEGIN:|101-getter|1|101-postInit
            bookError.addCommand(getDISMISS_COMMAND());
            bookError.setCommandListener(this);
            bookError.setTimeout(Alert.FOREVER);//GEN-END:|101-getter|1|101-postInit
            // write post-init user code here
        }//GEN-BEGIN:|101-getter|2|
        return bookError;
    }
    //</editor-fold>//GEN-END:|101-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: lastBookAvailable ">//GEN-BEGIN:|134-if|0|134-preIf
    /**
     * Performs an action assigned to the lastBookAvailable if-point.
     */
    public void lastBookAvailable() {//GEN-END:|134-if|0|134-preIf
        // enter pre-if user code here
        if (bookURL != null) {//GEN-LINE:|134-if|1|135-preAction
            // write pre-action user code here
            switchDisplayable(null, getLoadBook());//GEN-LINE:|134-if|2|135-postAction
            // write post-action user code here
        } else {//GEN-LINE:|134-if|3|136-preAction
            // write pre-action user code here
            switchDisplayable(null, getBookBrowser());//GEN-LINE:|134-if|4|136-postAction
            // write post-action user code here
        }//GEN-LINE:|134-if|5|134-postIf
        // enter post-if user code here
    }//GEN-BEGIN:|134-if|6|
    //</editor-fold>//GEN-END:|134-if|6|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: displayBookCanvas ">//GEN-BEGIN:|151-if|0|151-preIf
    /**
     * Performs an action assigned to the displayBookCanvas if-point.
     */
    public void displayBookCanvas() {//GEN-END:|151-if|0|151-preIf
        // enter pre-if user code here
        if (bookCanvas.isBookOpen()) {//GEN-LINE:|151-if|1|152-preAction
            // write pre-action user code here
            returnToMenu();//GEN-LINE:|151-if|2|152-postAction
            // write post-action user code here
        } else {//GEN-LINE:|151-if|3|153-preAction
            // write pre-action user code here
            exitMIDlet();//GEN-LINE:|151-if|4|153-postAction
            // write post-action user code here
        }//GEN-LINE:|151-if|5|151-postIf
        // enter post-if user code here
    }//GEN-BEGIN:|151-if|6|
    //</editor-fold>//GEN-END:|151-if|6|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: loadBook ">//GEN-BEGIN:|157-getter|0|157-preInit
    /**
     * Returns an initiliazed instance of loadBook component.
     * @return the initialized component instance
     */
    public WaitScreen getLoadBook() {
        if (loadBook == null) {//GEN-END:|157-getter|0|157-preInit
            // write pre-init user code here
            loadBook = new WaitScreen(getDisplay());//GEN-BEGIN:|157-getter|1|157-postInit
            loadBook.setTitle(null);
            loadBook.setCommandListener(this);
            loadBook.setFullScreenMode(true);
            loadBook.setImage(getAlbiteLogo());
            loadBook.setText("Opening book...");
            loadBook.setTextFont(getLoadingFont());
            loadBook.setTask(getLoadBookTask());//GEN-END:|157-getter|1|157-postInit
            // write post-init user code here
        }//GEN-BEGIN:|157-getter|2|
        return loadBook;
    }
    //</editor-fold>//GEN-END:|157-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: loadBookTask ">//GEN-BEGIN:|160-getter|0|160-preInit
    /**
     * Returns an initiliazed instance of loadBookTask component.
     * @return the initialized component instance
     */
    public SimpleCancellableTask getLoadBookTask() {
        if (loadBookTask == null) {//GEN-END:|160-getter|0|160-preInit
            // write pre-init user code here
            loadBookTask = new SimpleCancellableTask();//GEN-BEGIN:|160-getter|1|160-execute
            loadBookTask.setExecutable(new org.netbeans.microedition.util.Executable() {
                public void execute() throws Exception {//GEN-END:|160-getter|1|160-execute
//                    /*
//                     * bookURL already loaded before calling this task
//                     */
//                    bookCanvas.openBook(bookURL);
//                    fillBookmarks();
                }//GEN-BEGIN:|160-getter|2|160-postInit
            });//GEN-END:|160-getter|2|160-postInit
            // write post-init user code here
        }//GEN-BEGIN:|160-getter|3|
        return loadBookTask;
    }
    //</editor-fold>//GEN-END:|160-getter|3|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: albiteLogo ">//GEN-BEGIN:|165-getter|0|165-preInit
    /**
     * Returns an initiliazed instance of albiteLogo component.
     * @return the initialized component instance
     */
    public Image getAlbiteLogo() {
        if (albiteLogo == null) {//GEN-END:|165-getter|0|165-preInit
            // write pre-init user code here
            try {//GEN-BEGIN:|165-getter|1|165-@java.io.IOException
                albiteLogo = Image.createImage("/res/reader.png");
            } catch (java.io.IOException e) {//GEN-END:|165-getter|1|165-@java.io.IOException
            }//GEN-LINE:|165-getter|2|165-postInit
            // write post-init user code here
        }//GEN-BEGIN:|165-getter|3|
        return albiteLogo;
    }
    //</editor-fold>//GEN-END:|165-getter|3|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: loadingFont ">//GEN-BEGIN:|180-getter|0|180-preInit
    /**
     * Returns an initiliazed instance of loadingFont component.
     * @return the initialized component instance
     */
    public Font getLoadingFont() {
        if (loadingFont == null) {//GEN-END:|180-getter|0|180-preInit
            // write pre-init user code here
            loadingFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_ITALIC, Font.SIZE_SMALL);//GEN-LINE:|180-getter|1|180-postInit
            // write post-init user code here
        }//GEN-BEGIN:|180-getter|2|
        return loadingFont;
    }
    //</editor-fold>//GEN-END:|180-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: wordDefinition ">//GEN-BEGIN:|181-getter|0|181-preInit
    /**
     * Returns an initiliazed instance of wordDefinition component.
     * @return the initialized component instance
     */
    public Form getWordDefinition() {
        if (wordDefinition == null) {//GEN-END:|181-getter|0|181-preInit
            // write pre-init user code here
            wordDefinition = new Form("Word definition", new Item[] { getWordStringItem(), getDefinitionStringItem(), getDictrionaryStringItem() });//GEN-BEGIN:|181-getter|1|181-postInit
            wordDefinition.addCommand(getCLOSE_COMMAND());
            wordDefinition.addCommand(getBACK_COMMAND());
            wordDefinition.addCommand(getRESTART_COMMAND());
            wordDefinition.setCommandListener(this);//GEN-END:|181-getter|1|181-postInit
            // write post-init user code here
        }//GEN-BEGIN:|181-getter|2|
        return wordDefinition;
    }
    //</editor-fold>//GEN-END:|181-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: wordStringItem ">//GEN-BEGIN:|182-getter|0|182-preInit
    /**
     * Returns an initiliazed instance of wordStringItem component.
     * @return the initialized component instance
     */
    public StringItem getWordStringItem() {
        if (wordStringItem == null) {//GEN-END:|182-getter|0|182-preInit
            // write pre-init user code here
            wordStringItem = new StringItem("Word", "");//GEN-LINE:|182-getter|1|182-postInit
            // write post-init user code here
        }//GEN-BEGIN:|182-getter|2|
        return wordStringItem;
    }
    //</editor-fold>//GEN-END:|182-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: definitionStringItem ">//GEN-BEGIN:|183-getter|0|183-preInit
    /**
     * Returns an initiliazed instance of definitionStringItem component.
     * @return the initialized component instance
     */
    public StringItem getDefinitionStringItem() {
        if (definitionStringItem == null) {//GEN-END:|183-getter|0|183-preInit
            // write pre-init user code here
            definitionStringItem = new StringItem("Definition", "");//GEN-LINE:|183-getter|1|183-postInit
            // write post-init user code here
        }//GEN-BEGIN:|183-getter|2|
        return definitionStringItem;
    }
    //</editor-fold>//GEN-END:|183-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: dictrionaryStringItem ">//GEN-BEGIN:|184-getter|0|184-preInit
    /**
     * Returns an initiliazed instance of dictrionaryStringItem component.
     * @return the initialized component instance
     */
    public StringItem getDictrionaryStringItem() {
        if (dictrionaryStringItem == null) {//GEN-END:|184-getter|0|184-preInit
            // write pre-init user code here
            dictrionaryStringItem = new StringItem("Dictionary", "");//GEN-LINE:|184-getter|1|184-postInit
            // write post-init user code here
        }//GEN-BEGIN:|184-getter|2|
        return dictrionaryStringItem;
    }
    //</editor-fold>//GEN-END:|184-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: suggestions ">//GEN-BEGIN:|188-getter|0|188-preInit
    /**
     * Returns an initiliazed instance of suggestions component.
     * @return the initialized component instance
     */
    public List getSuggestions() {
        if (suggestions == null) {//GEN-END:|188-getter|0|188-preInit
            // write pre-init user code here
            suggestions = new List("Did you mean?", Choice.IMPLICIT);//GEN-BEGIN:|188-getter|1|188-postInit
            suggestions.addCommand(getBACK_COMMAND());
            suggestions.addCommand(getNEXT_COMMAND());
            suggestions.setCommandListener(this);
            suggestions.setSelectCommand(getNEXT_COMMAND());//GEN-END:|188-getter|1|188-postInit
            // write post-init user code here
        }//GEN-BEGIN:|188-getter|2|
        return suggestions;
    }
    //</editor-fold>//GEN-END:|188-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: suggestionsAction ">//GEN-BEGIN:|188-action|0|188-preAction
    /**
     * Performs an action assigned to the selected list element in the suggestions component.
     */
    public void suggestionsAction() {//GEN-END:|188-action|0|188-preAction
        // enter pre-action user code here
        String __selectedString = getSuggestions().getString(getSuggestions().getSelectedIndex());//GEN-LINE:|188-action|1|188-postAction
        // enter post-action user code here
    }//GEN-BEGIN:|188-action|2|
    //</editor-fold>//GEN-END:|188-action|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: enterWord ">//GEN-BEGIN:|223-entry|0|224-preAction
    /**
     * Performs an action assigned to the enterWord entry-point.
     */
    public void enterWord() {//GEN-END:|223-entry|0|224-preAction
        // write pre-action user code here
        if (entryForLookup == null) {
            entryForLookup = "";
        }

        getWordBox().setString(entryForLookup);

        dictionariesFound();//GEN-LINE:|223-entry|1|224-postAction
        // write post-action user code here
    }//GEN-BEGIN:|223-entry|2|
    //</editor-fold>//GEN-END:|223-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: BACK_COMMAND ">//GEN-BEGIN:|205-getter|0|205-preInit
    /**
     * Returns an initiliazed instance of BACK_COMMAND component.
     * @return the initialized component instance
     */
    public Command getBACK_COMMAND() {
        if (BACK_COMMAND == null) {//GEN-END:|205-getter|0|205-preInit
            // write pre-init user code here
            BACK_COMMAND = new Command("Back", Command.BACK, 0);//GEN-LINE:|205-getter|1|205-postInit
            // write post-init user code here
        }//GEN-BEGIN:|205-getter|2|
        return BACK_COMMAND;
    }
    //</editor-fold>//GEN-END:|205-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: NEXT_COMMAND ">//GEN-BEGIN:|209-getter|0|209-preInit
    /**
     * Returns an initiliazed instance of NEXT_COMMAND component.
     * @return the initialized component instance
     */
    public Command getNEXT_COMMAND() {
        if (NEXT_COMMAND == null) {//GEN-END:|209-getter|0|209-preInit
            // write pre-init user code here
            NEXT_COMMAND = new Command("Next", Command.OK, 0);//GEN-LINE:|209-getter|1|209-postInit
            // write post-init user code here
        }//GEN-BEGIN:|209-getter|2|
        return NEXT_COMMAND;
    }
    //</editor-fold>//GEN-END:|209-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: wordBox ">//GEN-BEGIN:|216-getter|0|216-preInit
    /**
     * Returns an initiliazed instance of wordBox component.
     * @return the initialized component instance
     */
    public TextBox getWordBox() {
        if (wordBox == null) {//GEN-END:|216-getter|0|216-preInit
            // write pre-init user code here
            wordBox = new TextBox("Enter word", "", 100, TextField.ANY);//GEN-BEGIN:|216-getter|1|216-postInit
            wordBox.addCommand(getNEXT_COMMAND());
            wordBox.addCommand(getBACK_COMMAND());
            wordBox.setCommandListener(this);
            wordBox.setInitialInputMode("UCB_BASIC_LATIN");//GEN-END:|216-getter|1|216-postInit
            // write post-init user code here
        }//GEN-BEGIN:|216-getter|2|
        return wordBox;
    }
    //</editor-fold>//GEN-END:|216-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: openLibrary ">//GEN-BEGIN:|242-entry|0|243-preAction
    /**
     * Performs an action assigned to the openLibrary entry-point.
     */
    public void openLibrary() {//GEN-END:|242-entry|0|243-preAction
        // write pre-action user code here
        switchDisplayable(null, getBookBrowser());//GEN-LINE:|242-entry|1|243-postAction
        // write post-action user code here
    }//GEN-BEGIN:|242-entry|2|
    //</editor-fold>//GEN-END:|242-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: enterNumber ">//GEN-BEGIN:|252-entry|0|253-preAction
    /**
     * Performs an action assigned to the enterNumber entry-point.
     */
    public void enterNumber() {//GEN-END:|252-entry|0|253-preAction
        // write pre-action user code here
        if (entryForLookup == null) {
            entryForLookup = "";
        }

        getNumberBox().setString(entryForLookup);
        switchDisplayable(null, getNumberBox());//GEN-LINE:|252-entry|1|253-postAction
        // write post-action user code here
    }//GEN-BEGIN:|252-entry|2|
    //</editor-fold>//GEN-END:|252-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: unitGroupsAction ">//GEN-BEGIN:|255-action|0|255-preAction
    /**
     * Performs an action assigned to the selected list element in the unitGroups component.
     */
    public void unitGroupsAction() {//GEN-END:|255-action|0|255-preAction
        // enter pre-action user code here
        String __selectedString = getUnitGroups().getString(getUnitGroups().getSelectedIndex());//GEN-LINE:|255-action|1|255-postAction
        // enter post-action user code here
    }//GEN-BEGIN:|255-action|2|
    //</editor-fold>//GEN-END:|255-action|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: CLOSE_COMMAND ">//GEN-BEGIN:|292-getter|0|292-preInit
    /**
     * Returns an initiliazed instance of CLOSE_COMMAND component.
     * @return the initialized component instance
     */
    public Command getCLOSE_COMMAND() {
        if (CLOSE_COMMAND == null) {//GEN-END:|292-getter|0|292-preInit
            // write pre-init user code here
            CLOSE_COMMAND = new Command("Close", Command.CANCEL, 0);//GEN-LINE:|292-getter|1|292-postInit
            // write post-init user code here
        }//GEN-BEGIN:|292-getter|2|
        return CLOSE_COMMAND;
    }
    //</editor-fold>//GEN-END:|292-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: unitFromAction ">//GEN-BEGIN:|266-action|0|266-preAction
    /**
     * Performs an action assigned to the selected list element in the unitFrom component.
     */
    public void unitFromAction() {//GEN-END:|266-action|0|266-preAction
        // enter pre-action user code here
        String __selectedString = getUnitFrom().getString(getUnitFrom().getSelectedIndex());//GEN-LINE:|266-action|1|266-postAction
        // enter post-action user code here
    }//GEN-BEGIN:|266-action|2|
    //</editor-fold>//GEN-END:|266-action|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: unitToAction ">//GEN-BEGIN:|269-action|0|269-preAction
    /**
     * Performs an action assigned to the selected list element in the unitTo component.
     */
    public void unitToAction() {//GEN-END:|269-action|0|269-preAction
        // enter pre-action user code here
        String __selectedString = getUnitTo().getString(getUnitTo().getSelectedIndex());//GEN-LINE:|269-action|1|269-postAction
        // enter post-action user code here
    }//GEN-BEGIN:|269-action|2|
    //</editor-fold>//GEN-END:|269-action|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: isNumberOKCheck ">//GEN-BEGIN:|312-if|0|312-preIf
    /**
     * Performs an action assigned to the isNumberOKCheck if-point.
     */
    public void isNumberOKCheck() {//GEN-END:|312-if|0|312-preIf
        // enter pre-if user code here
        numberOK = true;
        try {
            Double.parseDouble(numberBox.getString());
        } catch (NumberFormatException e) {
            numberOK = false;
        }
        if (numberOK) {//GEN-LINE:|312-if|1|313-preAction
            // write pre-action user code here
            switchDisplayable(null, getUnitGroups());//GEN-LINE:|312-if|2|313-postAction
            // write post-action user code here
        } else {//GEN-LINE:|312-if|3|314-preAction
            // write pre-action user code here
            switchDisplayable(null, getNumberError());//GEN-LINE:|312-if|4|314-postAction
            // write post-action user code here
        }//GEN-LINE:|312-if|5|312-postIf
        // enter post-if user code here
    }//GEN-BEGIN:|312-if|6|
    //</editor-fold>//GEN-END:|312-if|6|
    
    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: numberError ">//GEN-BEGIN:|316-getter|0|316-preInit
    /**
     * Returns an initiliazed instance of numberError component.
     * @return the initialized component instance
     */
    public Alert getNumberError() {
        if (numberError == null) {//GEN-END:|316-getter|0|316-preInit
            // write pre-init user code here
            numberError = new Alert("Sorry!", "You need to enter a number.", null, AlertType.ERROR);//GEN-BEGIN:|316-getter|1|316-postInit
            numberError.addCommand(getDISMISS_COMMAND());
            numberError.setCommandListener(this);
            numberError.setTimeout(Alert.FOREVER);//GEN-END:|316-getter|1|316-postInit
            // write post-init user code here
        }//GEN-BEGIN:|316-getter|2|
        return numberError;
    }
    //</editor-fold>//GEN-END:|316-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: tocAction ">//GEN-BEGIN:|325-action|0|325-preAction
    /**
     * Performs an action assigned to the selected list element in the toc component.
     */
    public void tocAction() {//GEN-END:|325-action|0|325-preAction
        // enter pre-action user code here
        String __selectedString = getToc().getString(getToc().getSelectedIndex());//GEN-LINE:|325-action|1|325-postAction
        // enter post-action user code here
    }//GEN-BEGIN:|325-action|2|
    //</editor-fold>//GEN-END:|325-action|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: showToc ">//GEN-BEGIN:|332-entry|0|333-preAction
    /**
     * Performs an action assigned to the showToc entry-point.
     */
    public void showToc() {//GEN-END:|332-entry|0|333-preAction
        // write pre-action user code here
        switchDisplayable(null, getToc());//GEN-LINE:|332-entry|1|333-postAction
        // write post-action user code here
    }//GEN-BEGIN:|332-entry|2|
    //</editor-fold>//GEN-END:|332-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: goToChapter ">//GEN-BEGIN:|344-entry|0|345-preAction
    /**
     * Performs an action assigned to the goToChapter entry-point.
     */
    public void goToChapter() {//GEN-END:|344-entry|0|345-preAction
        // write pre-action user code here
        getReflowChapterScreen().setTask(getGoToChapterTask());
        switchDisplayable(null, getReflowChapterScreen());//GEN-LINE:|344-entry|1|345-postAction
        // write post-action user code here
    }//GEN-BEGIN:|344-entry|2|
    //</editor-fold>//GEN-END:|344-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: showLoadingScreen ">//GEN-BEGIN:|348-if|0|348-preIf
    /**
     * Performs an action assigned to the showLoadingScreen if-point.
     */
    public void showLoadingScreen() {//GEN-END:|348-if|0|348-preIf
        // enter pre-if user code here
        if (!bookCanvas.isBookOpen(bookURL)) {//GEN-LINE:|348-if|1|349-preAction
            // write pre-action user code here
            switchDisplayable(null, getLoadBook());//GEN-LINE:|348-if|2|349-postAction
            // write post-action user code here
        } else {//GEN-LINE:|348-if|3|350-preAction
            // write pre-action user code here
            switchDisplayable(null, bookCanvas);//GEN-LINE:|348-if|4|350-postAction
            // write post-action user code here
        }//GEN-LINE:|348-if|5|348-postIf
        // enter post-if user code here
    }//GEN-BEGIN:|348-if|6|
    //</editor-fold>//GEN-END:|348-if|6|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: toc ">//GEN-BEGIN:|325-getter|0|325-preInit
    /**
     * Returns an initiliazed instance of toc component.
     * @return the initialized component instance
     */
    public List getToc() {
        if (toc == null) {//GEN-END:|325-getter|0|325-preInit
            // write pre-init user code here
            toc = new List("Table of contents", Choice.IMPLICIT);//GEN-BEGIN:|325-getter|1|325-postInit
            toc.addCommand(getBACK_COMMAND());
            toc.addCommand(getNEXT_COMMAND());
            toc.setCommandListener(this);
            toc.setFitPolicy(Choice.TEXT_WRAP_DEFAULT);
            toc.setSelectCommand(getNEXT_COMMAND());//GEN-END:|325-getter|1|325-postInit
            // write post-init user code here
        }//GEN-BEGIN:|325-getter|2|
        return toc;
    }
    //</editor-fold>//GEN-END:|325-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: GO_COMMAND ">//GEN-BEGIN:|359-getter|0|359-preInit
    /**
     * Returns an initiliazed instance of GO_COMMAND component.
     * @return the initialized component instance
     */
    public Command getGO_COMMAND() {
        if (GO_COMMAND == null) {//GEN-END:|359-getter|0|359-preInit
            // write pre-init user code here
            GO_COMMAND = new Command("Go!", Command.OK, 0);//GEN-LINE:|359-getter|1|359-postInit
            // write post-init user code here
        }//GEN-BEGIN:|359-getter|2|
        return GO_COMMAND;
    }
    //</editor-fold>//GEN-END:|359-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: chapterPositions ">//GEN-BEGIN:|354-getter|0|354-preInit
    /**
     * Returns an initiliazed instance of chapterPositions component.
     * @return the initialized component instance
     */
    public List getChapterPositions() {
        if (chapterPositions == null) {//GEN-END:|354-getter|0|354-preInit
            // write pre-init user code here
            chapterPositions = new List("Where to go?", Choice.IMPLICIT);//GEN-BEGIN:|354-getter|1|354-postInit
            chapterPositions.append("Where I was last time", null);
            chapterPositions.append("Start of chapter", null);
            chapterPositions.append("End of chapter", null);
            chapterPositions.append("Go to percent", null);
            chapterPositions.addCommand(getGO_COMMAND());
            chapterPositions.addCommand(getBACK_COMMAND());
            chapterPositions.setCommandListener(this);
            chapterPositions.setSelectCommand(getGO_COMMAND());
            chapterPositions.setSelectedFlags(new boolean[] { true, false, false, false });//GEN-END:|354-getter|1|354-postInit
            // write post-init user code here
        }//GEN-BEGIN:|354-getter|2|
        return chapterPositions;
    }
    //</editor-fold>//GEN-END:|354-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: chapterPositionsAction ">//GEN-BEGIN:|354-action|0|354-preAction
    /**
     * Performs an action assigned to the selected list element in the chapterPositions component.
     */
    public void chapterPositionsAction() {//GEN-END:|354-action|0|354-preAction
        // enter pre-action user code here
        String __selectedString = getChapterPositions().getString(getChapterPositions().getSelectedIndex());//GEN-BEGIN:|354-action|1|364-preAction
        if (__selectedString != null) {
            if (__selectedString.equals("Where I was last time")) {//GEN-END:|354-action|1|364-preAction
                // write pre-action user code here
//GEN-LINE:|354-action|2|364-postAction
                // write post-action user code here
            } else if (__selectedString.equals("Start of chapter")) {//GEN-LINE:|354-action|3|365-preAction
                // write pre-action user code here
//GEN-LINE:|354-action|4|365-postAction
                // write post-action user code here
            } else if (__selectedString.equals("End of chapter")) {//GEN-LINE:|354-action|5|366-preAction
                // write pre-action user code here
//GEN-LINE:|354-action|6|366-postAction
                // write post-action user code here
            } else if (__selectedString.equals("Go to percent")) {//GEN-LINE:|354-action|7|608-preAction
                // write pre-action user code here
                switchDisplayable(null, getChapterPositions());//GEN-LINE:|354-action|8|608-postAction
                // write post-action user code here
            }//GEN-BEGIN:|354-action|9|354-postAction
        }//GEN-END:|354-action|9|354-postAction
        // enter post-action user code here
    }//GEN-BEGIN:|354-action|10|
    //</editor-fold>//GEN-END:|354-action|10|
    
    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: splashScreen ">//GEN-BEGIN:|367-getter|0|367-preInit
    /**
     * Returns an initiliazed instance of splashScreen component.
     * @return the initialized component instance
     */
    public SplashScreen getSplashScreen() {
        if (splashScreen == null) {//GEN-END:|367-getter|0|367-preInit
            // write pre-init user code here
            splashScreen = new SplashScreen(getDisplay());//GEN-BEGIN:|367-getter|1|367-postInit
            splashScreen.setTitle(null);
            splashScreen.setCommandListener(this);
            splashScreen.setFullScreenMode(true);
            splashScreen.setImage(getAlbiteLogo());
            splashScreen.setText(version);
            splashScreen.setTextFont(getSmallPlainFont());
            splashScreen.setTimeout(1500);//GEN-END:|367-getter|1|367-postInit
            // write post-init user code here
        }//GEN-BEGIN:|367-getter|2|
        return splashScreen;
    }
    //</editor-fold>//GEN-END:|367-getter|2|
    
    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: smallPlainFont ">//GEN-BEGIN:|371-getter|0|371-preInit
    /**
     * Returns an initiliazed instance of smallPlainFont component.
     * @return the initialized component instance
     */
    public Font getSmallPlainFont() {
        if (smallPlainFont == null) {//GEN-END:|371-getter|0|371-preInit
            // write pre-init user code here
            smallPlainFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL);//GEN-LINE:|371-getter|1|371-postInit
            // write post-init user code here
        }//GEN-BEGIN:|371-getter|2|
        return smallPlainFont;
    }
    //</editor-fold>//GEN-END:|371-getter|2|
    
    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: acceptLicense ">//GEN-BEGIN:|372-getter|0|372-preInit
    /**
     * Returns an initiliazed instance of acceptLicense component.
     * @return the initialized component instance
     */
    public Form getAcceptLicense() {
        if (acceptLicense == null) {//GEN-END:|372-getter|0|372-preInit
            // write pre-init user code here
            acceptLicense = new Form("License agreement", new Item[] { getLicense1(), getLicense5(), getLicense13() });//GEN-BEGIN:|372-getter|1|372-postInit
            acceptLicense.addCommand(getNO_COMMAND());
            acceptLicense.addCommand(getYES_COMMAND());
            acceptLicense.setCommandListener(this);//GEN-END:|372-getter|1|372-postInit
            // write post-init user code here
        }//GEN-BEGIN:|372-getter|2|
        return acceptLicense;
    }
    //</editor-fold>//GEN-END:|372-getter|2|
    
    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: license1 ">//GEN-BEGIN:|373-getter|0|373-preInit
    /**
     * Returns an initiliazed instance of license1 component.
     * @return the initialized component instance
     */
    public StringItem getLicense1() {
        if (license1 == null) {//GEN-END:|373-getter|0|373-preInit
            // write pre-init user code here
            license1 = new StringItem("", "Albite READER is a free ebook reader developed by Svetlin Ankov and licensed under the Apache 2.0 License.");//GEN-BEGIN:|373-getter|1|373-postInit
            license1.setFont(getNormalFont());//GEN-END:|373-getter|1|373-postInit
            // write post-init user code here
        }//GEN-BEGIN:|373-getter|2|
        return license1;
    }
    //</editor-fold>//GEN-END:|373-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: runsForTheFirstTime ">//GEN-BEGIN:|378-if|0|378-preIf
    /**
     * Performs an action assigned to the runsForTheFirstTime if-point.
     */
    public void runsForTheFirstTime() {//GEN-END:|378-if|0|378-preIf
        // enter pre-if user code here
        if (firstTime) {//GEN-LINE:|378-if|1|379-preAction
            // write pre-action user code here
            switchDisplayable(null, getAcceptLicense());//GEN-LINE:|378-if|2|379-postAction
            // write post-action user code here
        } else {//GEN-LINE:|378-if|3|380-preAction
            // write pre-action user code here
            lastBookAvailable();//GEN-LINE:|378-if|4|380-postAction
            // write post-action user code here
        }//GEN-LINE:|378-if|5|378-postIf
        // enter post-if user code here
    }//GEN-BEGIN:|378-if|6|
    //</editor-fold>//GEN-END:|378-if|6|
    
    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: underlinedFont ">//GEN-BEGIN:|413-getter|0|413-preInit
    /**
     * Returns an initiliazed instance of underlinedFont component.
     * @return the initialized component instance
     */
    public Font getUnderlinedFont() {
        if (underlinedFont == null) {//GEN-END:|413-getter|0|413-preInit
            // write pre-init user code here
            underlinedFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_UNDERLINED, Font.SIZE_MEDIUM);//GEN-LINE:|413-getter|1|413-postInit
            // write post-init user code here
        }//GEN-BEGIN:|413-getter|2|
        return underlinedFont;
    }
    //</editor-fold>//GEN-END:|413-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: license5 ">//GEN-BEGIN:|415-getter|0|415-preInit
    /**
     * Returns an initiliazed instance of license5 component.
     * @return the initialized component instance
     */
    public StringItem getLicense5() {
        if (license5 == null) {//GEN-END:|415-getter|0|415-preInit
            // write pre-init user code here
            license5 = new StringItem("", "http://www.apache.org/licenses/LICENSE-2.0.txt", Item.HYPERLINK);//GEN-BEGIN:|415-getter|1|415-postInit
            license5.setFont(getUnderlinedFont());//GEN-END:|415-getter|1|415-postInit
            // write post-init user code here
        }//GEN-BEGIN:|415-getter|2|
        return license5;
    }
    //</editor-fold>//GEN-END:|415-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: normalFont ">//GEN-BEGIN:|416-getter|0|416-preInit
    /**
     * Returns an initiliazed instance of normalFont component.
     * @return the initialized component instance
     */
    public Font getNormalFont() {
        if (normalFont == null) {//GEN-END:|416-getter|0|416-preInit
            // write pre-init user code here
            normalFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);//GEN-LINE:|416-getter|1|416-postInit
            // write post-init user code here
        }//GEN-BEGIN:|416-getter|2|
        return normalFont;
    }
    //</editor-fold>//GEN-END:|416-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: license13 ">//GEN-BEGIN:|427-getter|0|427-preInit
    /**
     * Returns an initiliazed instance of license13 component.
     * @return the initialized component instance
     */
    public StringItem getLicense13() {
        if (license13 == null) {//GEN-END:|427-getter|0|427-preInit
            // write pre-init user code here
            license13 = new StringItem("", "Do you accept the license?");//GEN-BEGIN:|427-getter|1|427-postInit
            license13.setFont(getNormalFont());//GEN-END:|427-getter|1|427-postInit
            // write post-init user code here
        }//GEN-BEGIN:|427-getter|2|
        return license13;
    }
    //</editor-fold>//GEN-END:|427-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: showMenu ">//GEN-BEGIN:|436-entry|0|437-preAction
    /**
     * Performs an action assigned to the showMenu entry-point.
     */
    public void showMenu() {//GEN-END:|436-entry|0|437-preAction
        // write pre-action user code here
        calledOutside = false;
//        calledContext = false;
        switchDisplayable(null, getMenu());//GEN-LINE:|436-entry|1|437-postAction
        // write post-action user code here
    }//GEN-BEGIN:|436-entry|2|
    //</editor-fold>//GEN-END:|436-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: showLicense ">//GEN-BEGIN:|428-getter|0|428-preInit
    /**
     * Returns an initiliazed instance of showLicense component.
     * @return the initialized component instance
     */
    public Form getShowLicense() {
        if (showLicense == null) {//GEN-END:|428-getter|0|428-preInit
            // write pre-init user code here
            showLicense = new Form("About", new Item[] { getImageItem(), getStringItem(), getStringItem1(), getStringItem2(), getStringItem3() });//GEN-BEGIN:|428-getter|1|428-postInit
            showLicense.addCommand(getDISMISS_COMMAND());
            showLicense.setCommandListener(this);//GEN-END:|428-getter|1|428-postInit
            // write post-init user code here
        }//GEN-BEGIN:|428-getter|2|
        return showLicense;
    }
    //</editor-fold>//GEN-END:|428-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: menu ">//GEN-BEGIN:|429-getter|0|429-preInit
    /**
     * Returns an initiliazed instance of menu component.
     * @return the initialized component instance
     */
    public List getMenu() {
        if (menu == null) {//GEN-END:|429-getter|0|429-preInit
            // write pre-init user code here
            menu = new List("Albite READER " + version, Choice.IMPLICIT);//GEN-BEGIN:|429-getter|1|429-postInit
            menu.append("Open book", null);
            menu.append("Table of contents", null);
            menu.append("Bookmarks", null);
            menu.append("Book details", null);
            menu.append("Setup hyphenation", null);
            menu.append("Set chapter encoding", null);
            menu.append("Lookup word", null);
            menu.append("Convert number", null);
            menu.append("Font size", null);
            menu.append("Switch day / night", null);
            menu.append("Choose colors", null);
            menu.append("Screen mode", null);
            menu.append("Page interaction", null);
            menu.append("Page layout", null);
            menu.append("Set dictionary folder", null);
            menu.append("About", null);
            menu.append("Quit", null);
            menu.addCommand(getNEXT_COMMAND());
            menu.addCommand(getBACK_COMMAND());
            menu.setCommandListener(this);
            menu.setFitPolicy(Choice.TEXT_WRAP_OFF);
            menu.setSelectedFlags(new boolean[] { false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false });//GEN-END:|429-getter|1|429-postInit
            // write post-init user code here

            /*
             * Remove some actions not used in light mode
             */
            if (lightMode) {
                menu.delete(14);
                menu.delete(11);
                menu.delete(8);
                menu.delete(6);
                menu.delete(4);
            }
        }//GEN-BEGIN:|429-getter|2|
        return menu;
    }
    //</editor-fold>//GEN-END:|429-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: menuAction ">//GEN-BEGIN:|429-action|0|429-preAction
    /**
     * Performs an action assigned to the selected list element in the menu component.
     */
    public void menuAction() {//GEN-END:|429-action|0|429-preAction
        // enter pre-action user code here
        String __selectedString = getMenu().getString(getMenu().getSelectedIndex());//GEN-BEGIN:|429-action|1|470-preAction
        if (__selectedString != null) {
            if (__selectedString.equals("Open book")) {//GEN-END:|429-action|1|470-preAction
                // write pre-action user code here
                openLibrary();//GEN-LINE:|429-action|2|470-postAction
                // write post-action user code here
            } else if (__selectedString.equals("Table of contents")) {//GEN-LINE:|429-action|3|469-preAction
                // write pre-action user code here
                showToc();//GEN-LINE:|429-action|4|469-postAction
                // write post-action user code here
            } else if (__selectedString.equals("Bookmarks")) {//GEN-LINE:|429-action|5|936-preAction
                // write pre-action user code here
                bookCanvas.setupNewBookmark();
                switchDisplayable(null, getBookmarks());//GEN-LINE:|429-action|6|936-postAction
                // write post-action user code here
            } else if (__selectedString.equals("Book details")) {//GEN-LINE:|429-action|7|734-preAction
                // write pre-action user code here
                showBookInfo();//GEN-LINE:|429-action|8|734-postAction
                // write post-action user code here
            } else if (__selectedString.equals("Setup hyphenation")) {//GEN-LINE:|429-action|9|1045-preAction
                // write pre-action user code here
                switchDisplayable(null, getLanguages());//GEN-LINE:|429-action|10|1045-postAction
                // write post-action user code here
            } else if (__selectedString.equals("Set chapter encoding")) {//GEN-LINE:|429-action|11|1046-preAction
                // write pre-action user code here
                switchDisplayable(null, getEncodings());//GEN-LINE:|429-action|12|1046-postAction
                // write post-action user code here
            } else if (__selectedString.equals("Lookup word")) {//GEN-LINE:|429-action|13|471-preAction
                // write pre-action user code here
                setEntryForLookup("");
                enterWord();//GEN-LINE:|429-action|14|471-postAction
                // write post-action user code here
            } else if (__selectedString.equals("Convert number")) {//GEN-LINE:|429-action|15|472-preAction
                // write pre-action user code here
                setEntryForLookup("");
                enterNumber();//GEN-LINE:|429-action|16|472-postAction
                // write post-action user code here
            } else if (__selectedString.equals("Font size")) {//GEN-LINE:|429-action|17|473-preAction
                // write pre-action user code here
                setFontSize();//GEN-LINE:|429-action|18|473-postAction
                // write post-action user code here
            } else if (__selectedString.equals("Switch day / night")) {//GEN-LINE:|429-action|19|474-preAction
                // write pre-action user code here
                bookCanvas.cycleColorSchemes();
                switchDisplayable(null, bookCanvas);//GEN-LINE:|429-action|20|474-postAction
                // write post-action user code here
            } else if (__selectedString.equals("Choose colors")) {//GEN-LINE:|429-action|21|475-preAction
                // write pre-action user code here
                setColorScheme();//GEN-LINE:|429-action|22|475-postAction
                // write post-action user code here
            } else if (__selectedString.equals("Screen mode")) {//GEN-LINE:|429-action|23|476-preAction
                // write pre-action user code here
                setSreenMode();//GEN-LINE:|429-action|24|476-postAction
                // write post-action user code here
            } else if (__selectedString.equals("Page interaction")) {//GEN-LINE:|429-action|25|588-preAction
                // write pre-action user code here
                setScrollingOptions();//GEN-LINE:|429-action|26|588-postAction
                // write post-action user code here
            } else if (__selectedString.equals("Page layout")) {//GEN-LINE:|429-action|27|935-preAction
                // write pre-action user code here
                switchDisplayable(null, getPageSettings());//GEN-LINE:|429-action|28|935-postAction
                // write post-action user code here
            } else if (__selectedString.equals("Set dictionary folder")) {//GEN-LINE:|429-action|29|477-preAction
                // write pre-action user code here
                switchDisplayable(null, getFolderBrowser());//GEN-LINE:|429-action|30|477-postAction
                // write post-action user code here
            } else if (__selectedString.equals("About")) {//GEN-LINE:|429-action|31|478-preAction
                // write pre-action user code here
                switchDisplayable(null, getShowLicense());//GEN-LINE:|429-action|32|478-postAction
                // write post-action user code here
            } else if (__selectedString.equals("Quit")) {//GEN-LINE:|429-action|33|479-preAction
                // write pre-action user code here
                quit();//GEN-LINE:|429-action|34|479-postAction
                // write post-action user code here
            }//GEN-BEGIN:|429-action|35|429-postAction
        }//GEN-END:|429-action|35|429-postAction
        // enter post-action user code here
    }//GEN-BEGIN:|429-action|36|
    //</editor-fold>//GEN-END:|429-action|36|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: imageItem ">//GEN-BEGIN:|447-getter|0|447-preInit
    /**
     * Returns an initiliazed instance of imageItem component.
     * @return the initialized component instance
     */
    public ImageItem getImageItem() {
        if (imageItem == null) {//GEN-END:|447-getter|0|447-preInit
            // write pre-init user code here
            imageItem = new ImageItem("", getAlbiteLogo(), ImageItem.LAYOUT_CENTER, "");//GEN-LINE:|447-getter|1|447-postInit
            // write post-init user code here
        }//GEN-BEGIN:|447-getter|2|
        return imageItem;
    }
    //</editor-fold>//GEN-END:|447-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: stringItem ">//GEN-BEGIN:|448-getter|0|448-preInit
    /**
     * Returns an initiliazed instance of stringItem component.
     * @return the initialized component instance
     */
    public StringItem getStringItem() {
        if (stringItem == null) {//GEN-END:|448-getter|0|448-preInit
            // write pre-init user code here
            stringItem = new StringItem("Version:", version);//GEN-BEGIN:|448-getter|1|448-postInit
            stringItem.setLayout(ImageItem.LAYOUT_LEFT);
            stringItem.setFont(getNormalFont());//GEN-END:|448-getter|1|448-postInit
            // write post-init user code here
        }//GEN-BEGIN:|448-getter|2|
        return stringItem;
    }
    //</editor-fold>//GEN-END:|448-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: stringItem1 ">//GEN-BEGIN:|449-getter|0|449-preInit
    /**
     * Returns an initiliazed instance of stringItem1 component.
     * @return the initialized component instance
     */
    public StringItem getStringItem1() {
        if (stringItem1 == null) {//GEN-END:|449-getter|0|449-preInit
            // write pre-init user code here
            stringItem1 = new StringItem("", "Albite READER is a free ebook reader for the Java ME Platform, developed by Svetlin Ankov.");//GEN-BEGIN:|449-getter|1|449-postInit
            stringItem1.setFont(getNormalFont());//GEN-END:|449-getter|1|449-postInit
            // write post-init user code here
        }//GEN-BEGIN:|449-getter|2|
        return stringItem1;
    }
    //</editor-fold>//GEN-END:|449-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: stringItem2 ">//GEN-BEGIN:|450-getter|0|450-preInit
    /**
     * Returns an initiliazed instance of stringItem2 component.
     * @return the initialized component instance
     */
    public StringItem getStringItem2() {
        if (stringItem2 == null) {//GEN-END:|450-getter|0|450-preInit
            // write pre-init user code here
            stringItem2 = new StringItem("", "You can get this application and free books from the following link:");//GEN-BEGIN:|450-getter|1|450-postInit
            stringItem2.setFont(getNormalFont());//GEN-END:|450-getter|1|450-postInit
            // write post-init user code here
        }//GEN-BEGIN:|450-getter|2|
        return stringItem2;
    }
    //</editor-fold>//GEN-END:|450-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: stringItem3 ">//GEN-BEGIN:|451-getter|0|451-preInit
    /**
     * Returns an initiliazed instance of stringItem3 component.
     * @return the initialized component instance
     */
    public StringItem getStringItem3() {
        if (stringItem3 == null) {//GEN-END:|451-getter|0|451-preInit
            // write pre-init user code here
            stringItem3 = new StringItem("", "http://albite.vlexofree.com", Item.HYPERLINK);//GEN-BEGIN:|451-getter|1|451-postInit
            stringItem3.setFont(getUnderlinedFont());//GEN-END:|451-getter|1|451-postInit
            // write post-init user code here
        }//GEN-BEGIN:|451-getter|2|
        return stringItem3;
    }
    //</editor-fold>//GEN-END:|451-getter|2|



















    //<editor-fold defaultstate="collapsed" desc=" Generated Method: returnToMenu ">//GEN-BEGIN:|462-if|0|462-preIf
    /**
     * Performs an action assigned to the returnToMenu if-point.
     */
    public void returnToMenu() {//GEN-END:|462-if|0|462-preIf
        // enter pre-if user code here
        if (!calledOutside) {//GEN-LINE:|462-if|1|463-preAction
            // write pre-action user code here
            switchDisplayable(null, getMenu());//GEN-LINE:|462-if|2|463-postAction
            // write post-action user code here
        } else {//GEN-LINE:|462-if|3|464-preAction
            // write pre-action user code here
            switchDisplayable(null, bookCanvas);//GEN-LINE:|462-if|4|464-postAction
            // write post-action user code here
        }//GEN-LINE:|462-if|5|462-postIf
        // enter post-if user code here
    }//GEN-BEGIN:|462-if|6|
    //</editor-fold>//GEN-END:|462-if|6|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: showColorPicker ">//GEN-BEGIN:|501-if|0|501-preIf
    /**
     * Performs an action assigned to the showColorPicker if-point.
     */
    public void showColorPicker() {//GEN-END:|501-if|0|501-preIf
        // enter pre-if user code here
        if (showColors) {//GEN-LINE:|501-if|1|502-preAction
            // write pre-action user code here
            switchDisplayable(null, getColors());//GEN-LINE:|501-if|2|502-postAction
            // write post-action user code here
        } else {//GEN-LINE:|501-if|3|503-preAction
            // write pre-action user code here
            applyColorScheme();//GEN-LINE:|501-if|4|503-postAction
            // write post-action user code here
        }//GEN-LINE:|501-if|5|501-postIf
        // enter post-if user code here
    }//GEN-BEGIN:|501-if|6|
    //</editor-fold>//GEN-END:|501-if|6|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: schemesAction ">//GEN-BEGIN:|512-action|0|512-preAction
    /**
     * Performs an action assigned to the selected list element in the schemes component.
     */
    public void schemesAction() {//GEN-END:|512-action|0|512-preAction
        // enter pre-action user code here
        String __selectedString = getSchemes().getString(getSchemes().getSelectedIndex());//GEN-LINE:|512-action|1|512-postAction
        // enter post-action user code here
    }//GEN-BEGIN:|512-action|2|
    //</editor-fold>//GEN-END:|512-action|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: APPLY_COMMAND ">//GEN-BEGIN:|533-getter|0|533-preInit
    /**
     * Returns an initiliazed instance of APPLY_COMMAND component.
     * @return the initialized component instance
     */
    public Command getAPPLY_COMMAND() {
        if (APPLY_COMMAND == null) {//GEN-END:|533-getter|0|533-preInit
            // write pre-init user code here
            APPLY_COMMAND = new Command("Apply", Command.OK, 0);//GEN-LINE:|533-getter|1|533-postInit
            // write post-init user code here
        }//GEN-BEGIN:|533-getter|2|
        return APPLY_COMMAND;
    }
    //</editor-fold>//GEN-END:|533-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: colorsAction ">//GEN-BEGIN:|530-action|0|530-preAction
    /**
     * Performs an action assigned to the selected list element in the colors component.
     */
    public void colorsAction() {//GEN-END:|530-action|0|530-preAction
        // enter pre-action user code here
        String __selectedString = getColors().getString(getColors().getSelectedIndex());//GEN-LINE:|530-action|1|530-postAction
        // enter post-action user code here
    }//GEN-BEGIN:|530-action|2|
    //</editor-fold>//GEN-END:|530-action|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: setColorScheme ">//GEN-BEGIN:|541-entry|0|542-preAction
    /**
     * Performs an action assigned to the setColorScheme entry-point.
     */
    public void setColorScheme() {//GEN-END:|541-entry|0|542-preAction
        // write pre-action user code here
        switchDisplayable(null, getSchemes());//GEN-LINE:|541-entry|1|542-postAction
        // write post-action user code here
    }//GEN-BEGIN:|541-entry|2|
    //</editor-fold>//GEN-END:|541-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: quit ">//GEN-BEGIN:|554-entry|0|555-preAction
    /**
     * Performs an action assigned to the quit entry-point.
     */
    public void quit() {//GEN-END:|554-entry|0|555-preAction
        // write pre-action user code here
        switchDisplayable(null, getExitBox());//GEN-LINE:|554-entry|1|555-postAction
        // write post-action user code here
    }//GEN-BEGIN:|554-entry|2|
    //</editor-fold>//GEN-END:|554-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: YES_COMMAND ">//GEN-BEGIN:|548-getter|0|548-preInit
    /**
     * Returns an initiliazed instance of YES_COMMAND component.
     * @return the initialized component instance
     */
    public Command getYES_COMMAND() {
        if (YES_COMMAND == null) {//GEN-END:|548-getter|0|548-preInit
            // write pre-init user code here
            YES_COMMAND = new Command("Yes", Command.OK, 0);//GEN-LINE:|548-getter|1|548-postInit
            // write post-init user code here
        }//GEN-BEGIN:|548-getter|2|
        return YES_COMMAND;
    }
    //</editor-fold>//GEN-END:|548-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: NO_COMMAND ">//GEN-BEGIN:|550-getter|0|550-preInit
    /**
     * Returns an initiliazed instance of NO_COMMAND component.
     * @return the initialized component instance
     */
    public Command getNO_COMMAND() {
        if (NO_COMMAND == null) {//GEN-END:|550-getter|0|550-preInit
            // write pre-init user code here
            NO_COMMAND = new Command("No", Command.BACK, 0);//GEN-LINE:|550-getter|1|550-postInit
            // write post-init user code here
        }//GEN-BEGIN:|550-getter|2|
        return NO_COMMAND;
    }
    //</editor-fold>//GEN-END:|550-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: schemes ">//GEN-BEGIN:|512-getter|0|512-preInit
    /**
     * Returns an initiliazed instance of schemes component.
     * @return the initialized component instance
     */
    public List getSchemes() {
        if (schemes == null) {//GEN-END:|512-getter|0|512-preInit
            // write pre-init user code here
            schemes = new List("Select scheme", Choice.IMPLICIT);//GEN-BEGIN:|512-getter|1|512-postInit
            schemes.addCommand(getBACK_COMMAND());
            schemes.addCommand(getNEXT_COMMAND());
            schemes.setCommandListener(this);
            schemes.setSelectCommand(getNEXT_COMMAND());//GEN-END:|512-getter|1|512-postInit
            // write post-init user code here

            /*
             * Load Schemes
             */
            for (int i = 0; i < ColorScheme.SCHEMES.length; i++) {
                schemes.append(ColorScheme.SCHEMES[i], null);
            }
        }//GEN-BEGIN:|512-getter|2|
        return schemes;
    }
    //</editor-fold>//GEN-END:|512-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: colors ">//GEN-BEGIN:|530-getter|0|530-preInit
    /**
     * Returns an initiliazed instance of colors component.
     * @return the initialized component instance
     */
    public List getColors() {
        if (colors == null) {//GEN-END:|530-getter|0|530-preInit
            // write pre-init user code here
            colors = new List("Select color", Choice.IMPLICIT);//GEN-BEGIN:|530-getter|1|530-postInit
            colors.addCommand(getAPPLY_COMMAND());
            colors.addCommand(getBACK_COMMAND());
            colors.setCommandListener(this);
            colors.setSelectCommand(getAPPLY_COMMAND());//GEN-END:|530-getter|1|530-postInit
            // write post-init user code here

            /*
             * Load Colors
             */
            for (int i = 0; i < ColorScheme.HUE_NAMES.length; i++) {
                colors.append(ColorScheme.HUE_NAMES[i], null);
            }
        }//GEN-BEGIN:|530-getter|2|
        return colors;
    }
    //</editor-fold>//GEN-END:|530-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: unitGroups ">//GEN-BEGIN:|255-getter|0|255-preInit
    /**
     * Returns an initiliazed instance of unitGroups component.
     * @return the initialized component instance
     */
    public List getUnitGroups() {
        if (unitGroups == null) {//GEN-END:|255-getter|0|255-preInit
            // write pre-init user code here
            unitGroups = new List("Select units type", Choice.IMPLICIT);//GEN-BEGIN:|255-getter|1|255-postInit
            unitGroups.addCommand(getNEXT_COMMAND());
            unitGroups.addCommand(getBACK_COMMAND());
            unitGroups.setCommandListener(this);
            unitGroups.setFitPolicy(Choice.TEXT_WRAP_DEFAULT);
            unitGroups.setSelectCommand(getNEXT_COMMAND());//GEN-END:|255-getter|1|255-postInit
            // write post-init user code here

            /*
             * Load metrics list
             */
            final UnitGroup[] groups = UnitGroup.GROUPS;
            for (int i = 0; i < groups.length; i++) {
                unitGroups.append(groups[i].name, null);
            }
        }//GEN-BEGIN:|255-getter|2|
        return unitGroups;
    }
    //</editor-fold>//GEN-END:|255-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: setFontSize ">//GEN-BEGIN:|565-entry|0|566-preAction
    /**
     * Performs an action assigned to the setFontSize entry-point.
     */
    public void setFontSize() {//GEN-END:|565-entry|0|566-preAction
        // write pre-action user code here
        switchDisplayable(null, getFontSizes());//GEN-LINE:|565-entry|1|566-postAction
        // write post-action user code here
    }//GEN-BEGIN:|565-entry|2|
    //</editor-fold>//GEN-END:|565-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: fontSizes ">//GEN-BEGIN:|558-getter|0|558-preInit
    /**
     * Returns an initiliazed instance of fontSizes component.
     * @return the initialized component instance
     */
    public List getFontSizes() {
        if (fontSizes == null) {//GEN-END:|558-getter|0|558-preInit
            // write pre-init user code here
            fontSizes = new List("Select font size", Choice.IMPLICIT);//GEN-BEGIN:|558-getter|1|558-postInit
            fontSizes.addCommand(getAPPLY_COMMAND());
            fontSizes.addCommand(getBACK_COMMAND());
            fontSizes.setCommandListener(this);
            fontSizes.setSelectCommand(getAPPLY_COMMAND());//GEN-END:|558-getter|1|558-postInit
            // write post-init user code here
            final byte[] fonts = bookCanvas.fontSizes;

            for (int i = 0; i < fonts.length; i++) {
                fontSizes.append(Integer.toString(fonts[i]), null);
            }

            fontSizes.setSelectedIndex(bookCanvas.getFontSizeIndex(), true);
        }//GEN-BEGIN:|558-getter|2|
        return fontSizes;
    }
    //</editor-fold>//GEN-END:|558-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: fontSizesAction ">//GEN-BEGIN:|558-action|0|558-preAction
    /**
     * Performs an action assigned to the selected list element in the fontSizes component.
     */
    public void fontSizesAction() {//GEN-END:|558-action|0|558-preAction
        // enter pre-action user code here
        String __selectedString = getFontSizes().getString(getFontSizes().getSelectedIndex());//GEN-LINE:|558-action|1|558-postAction
        // enter post-action user code here
    }//GEN-BEGIN:|558-action|2|
    //</editor-fold>//GEN-END:|558-action|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: setScrollingOptions ">//GEN-BEGIN:|573-entry|0|574-preAction
    /**
     * Performs an action assigned to the setScrollingOptions entry-point.
     */
    public void setScrollingOptions() {//GEN-END:|573-entry|0|574-preAction
        // write pre-action user code here
        switchDisplayable(null, getScrollingOptions());//GEN-LINE:|573-entry|1|574-postAction
        // write post-action user code here
    }//GEN-BEGIN:|573-entry|2|
    //</editor-fold>//GEN-END:|573-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: scrollingOptions ">//GEN-BEGIN:|572-getter|0|572-preInit
    /**
     * Returns an initiliazed instance of scrollingOptions component.
     * @return the initialized component instance
     */
    public Form getScrollingOptions() {
        if (scrollingOptions == null) {//GEN-END:|572-getter|0|572-preInit
            // write pre-init user code here
            scrollingOptions = new Form("Page interaction", new Item[] { getScrollingSpeed(), getSmoothScrolling(), getScrollingDirectionGroup(), getHoldingTimeMultiplier() });//GEN-BEGIN:|572-getter|1|572-postInit
            scrollingOptions.addCommand(getAPPLY_COMMAND());
            scrollingOptions.addCommand(getBACK_COMMAND());
            scrollingOptions.setCommandListener(this);//GEN-END:|572-getter|1|572-postInit
            // write post-init user code here
        }//GEN-BEGIN:|572-getter|2|
        return scrollingOptions;
    }
    //</editor-fold>//GEN-END:|572-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: scrollingSpeed ">//GEN-BEGIN:|582-getter|0|582-preInit
    /**
     * Returns an initiliazed instance of scrollingSpeed component.
     * @return the initialized component instance
     */
    public Gauge getScrollingSpeed() {
        if (scrollingSpeed == null) {//GEN-END:|582-getter|0|582-preInit
            // write pre-init user code here
            scrollingSpeed = new Gauge("Scrolling speed", true, 100, bookCanvas.getScrollingSpeed());//GEN-LINE:|582-getter|1|582-postInit
            // write post-init user code here
        }//GEN-BEGIN:|582-getter|2|
        return scrollingSpeed;
    }
    //</editor-fold>//GEN-END:|582-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: setSreenMode ">//GEN-BEGIN:|593-entry|0|594-preAction
    /**
     * Performs an action assigned to the setSreenMode entry-point.
     */
    public void setSreenMode() {//GEN-END:|593-entry|0|594-preAction
        switchDisplayable(null, getScreenModes());//GEN-LINE:|593-entry|1|594-postAction
        // write post-action user code here
    }//GEN-BEGIN:|593-entry|2|
    //</editor-fold>//GEN-END:|593-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: screenModes ">//GEN-BEGIN:|590-getter|0|590-preInit
    /**
     * Returns an initiliazed instance of screenModes component.
     * @return the initialized component instance
     */
    public List getScreenModes() {
        if (screenModes == null) {//GEN-END:|590-getter|0|590-preInit
            // write pre-init user code here
            screenModes = new List("Screen mode", Choice.IMPLICIT);//GEN-BEGIN:|590-getter|1|590-postInit
            screenModes.append("Normal", null);
            screenModes.append("Normal, Fullscreen", null);
            screenModes.append("Clockwise", null);
            screenModes.append("Upside-down", null);
            screenModes.append("Counterclockwise", null);
            screenModes.addCommand(getBACK_COMMAND());
            screenModes.addCommand(getAPPLY_COMMAND());
            screenModes.setCommandListener(this);
            screenModes.setSelectCommand(getAPPLY_COMMAND());
            screenModes.setSelectedFlags(new boolean[] { false, false, false, false, false });//GEN-END:|590-getter|1|590-postInit
            // write post-init user code here
            screenModes.setSelectedIndex(bookCanvas.getScreenMode(), true);
        }//GEN-BEGIN:|590-getter|2|
        return screenModes;
    }
    //</editor-fold>//GEN-END:|590-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: screenModesAction ">//GEN-BEGIN:|590-action|0|590-preAction
    /**
     * Performs an action assigned to the selected list element in the screenModes component.
     */
    public void screenModesAction() {//GEN-END:|590-action|0|590-preAction
        // enter pre-action user code here
        switch (getScreenModes().getSelectedIndex()) {//GEN-BEGIN:|590-action|1|602-preAction
            case 0://GEN-END:|590-action|1|602-preAction
                // write pre-action user code here
//GEN-LINE:|590-action|2|602-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|590-action|3|603-preAction
            case 1://GEN-END:|590-action|3|603-preAction
                // write pre-action user code here
//GEN-LINE:|590-action|4|603-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|590-action|5|604-preAction
            case 2://GEN-END:|590-action|5|604-preAction
                // write pre-action user code here
//GEN-LINE:|590-action|6|604-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|590-action|7|606-preAction
            case 3://GEN-END:|590-action|7|606-preAction
                // write pre-action user code here
//GEN-LINE:|590-action|8|606-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|590-action|9|605-preAction
            case 4://GEN-END:|590-action|9|605-preAction
                // write pre-action user code here
//GEN-LINE:|590-action|10|605-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|590-action|11|590-postAction
        }//GEN-END:|590-action|11|590-postAction
        // enter post-action user code here
    }//GEN-BEGIN:|590-action|12|
    //</editor-fold>//GEN-END:|590-action|12|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: selectPercent ">//GEN-BEGIN:|609-getter|0|609-preInit
    /**
     * Returns an initiliazed instance of selectPercent component.
     * @return the initialized component instance
     */
    public Form getSelectPercent() {
        if (selectPercent == null) {//GEN-END:|609-getter|0|609-preInit
            // write pre-init user code here
            selectPercent = new Form("Select position", new Item[] { getChapterPercent() });//GEN-BEGIN:|609-getter|1|609-postInit
            selectPercent.addCommand(getBACK_COMMAND());
            selectPercent.addCommand(getAPPLY_COMMAND());
            selectPercent.setCommandListener(this);//GEN-END:|609-getter|1|609-postInit
            // write post-init user code here
        }//GEN-BEGIN:|609-getter|2|
        return selectPercent;
    }
    //</editor-fold>//GEN-END:|609-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: caseSelectPercent ">//GEN-BEGIN:|618-if|0|618-preIf
    /**
     * Performs an action assigned to the caseSelectPercent if-point.
     */
    public void caseSelectPercent() {//GEN-END:|618-if|0|618-preIf
        // enter pre-if user code here
        if (getChapterPositions().getSelectedIndex() == 3) {//GEN-LINE:|618-if|1|619-preAction
            // write pre-action user code here
            switchDisplayable(null, getSelectPercent());//GEN-LINE:|618-if|2|619-postAction
            // write post-action user code here
        } else {//GEN-LINE:|618-if|3|620-preAction
            // write pre-action user code here
            goToChapter();//GEN-LINE:|618-if|4|620-postAction
            // write post-action user code here
        }//GEN-LINE:|618-if|5|618-postIf
        // enter post-if user code here
    }//GEN-BEGIN:|618-if|6|
    //</editor-fold>//GEN-END:|618-if|6|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: applyScrollingOptions ">//GEN-BEGIN:|648-entry|0|649-preAction
    /**
     * Performs an action assigned to the applyScrollingOptions entry-point.
     */
    public void applyScrollingOptions() {//GEN-END:|648-entry|0|649-preAction
        // write pre-action user code here
        bookCanvas.setScrollingOptions(
                getScrollingSpeed().getValue() / 100F,
                getSmoothScrolling().isSelected(0),
                getScrollingDirectionGroup().isSelected(0)
                );
        bookCanvas.setHoldingTimeByMultiplier(
                getHoldingTimeMultiplier().getValue());
        switchDisplayable(null, bookCanvas);//GEN-LINE:|648-entry|1|649-postAction
        // write post-action user code here
    }//GEN-BEGIN:|648-entry|2|
    //</editor-fold>//GEN-END:|648-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: applyFontSize ">//GEN-BEGIN:|652-entry|0|653-preAction
    /**
     * Performs an action assigned to the applyFontSize entry-point.
     */
    public void applyFontSize() {//GEN-END:|652-entry|0|653-preAction
        // write pre-action user code here
        getReflowChapterScreen().setTask(getApplyFontSizeTask());
        switchDisplayable(null, getReflowChapterScreen());//GEN-LINE:|652-entry|1|653-postAction
        // write post-action user code here
    }//GEN-BEGIN:|652-entry|2|
    //</editor-fold>//GEN-END:|652-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: applyColorScheme ">//GEN-BEGIN:|656-entry|0|657-preAction
    /**
     * Performs an action assigned to the applyColorScheme entry-point.
     */
    public void applyColorScheme() {//GEN-END:|656-entry|0|657-preAction
        // write pre-action user code here
        final int index = schemes.getSelectedIndex();

        bookCanvas.setScheme(
                (byte) index,
                (index == 0
                ? 0
                : ColorScheme.HUE_VALUES[colors.getSelectedIndex()]
                ));
        switchDisplayable(null, bookCanvas);//GEN-LINE:|656-entry|1|657-postAction
        // write post-action user code here
    }//GEN-BEGIN:|656-entry|2|
    //</editor-fold>//GEN-END:|656-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: chapterPercent ">//GEN-BEGIN:|666-getter|0|666-preInit
    /**
     * Returns an initiliazed instance of chapterPercent component.
     * @return the initialized component instance
     */
    public Gauge getChapterPercent() {
        if (chapterPercent == null) {//GEN-END:|666-getter|0|666-preInit
            // write pre-init user code here
            chapterPercent = new Gauge("Position in chapter", true, 100, 0);//GEN-LINE:|666-getter|1|666-postInit
            // write post-init user code here
        }//GEN-BEGIN:|666-getter|2|
        return chapterPercent;
    }
    //</editor-fold>//GEN-END:|666-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: loadUnitsToLists ">//GEN-BEGIN:|673-entry|0|674-preAction
    /**
     * Performs an action assigned to the loadUnitsToLists entry-point.
     */
    public void loadUnitsToLists() {//GEN-END:|673-entry|0|674-preAction
        // write pre-action user code here

        /* Find Selected Group */
        UnitGroup group = UnitGroup.GROUPS[unitGroups.getSelectedIndex()];

        /* Load items in lists */
        unitFrom = null;
        unitTo = null;

        List from = getUnitFrom();
        List to = getUnitTo();

        Unit[] units = group.units;
        for (int i = 0; i < units.length; i++) {
            from.append(units[i].name, null);
            to.append(units[i].name, null);
        }
        switchDisplayable(null, getUnitFrom());//GEN-LINE:|673-entry|1|674-postAction
        // write post-action user code here
    }//GEN-BEGIN:|673-entry|2|
    //</editor-fold>//GEN-END:|673-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: convertUnits ">//GEN-BEGIN:|683-entry|0|684-preAction
    /**
     * Performs an action assigned to the convertUnits entry-point.
     */
    public void convertUnits() {//GEN-END:|683-entry|0|684-preAction
        // write pre-action user code here

        /*
         * Converting units
         */
        UnitGroup group = UnitGroup.GROUPS[unitGroups.getSelectedIndex()];
        Unit[] units = group.units;
        Unit unitFrom = group.units[this.unitFrom.getSelectedIndex()];
        Unit unitTo = group.units[this.unitTo.getSelectedIndex()];

        double quantityFrom =
                Double.parseDouble(numberBox.getString());
        double quantityTo = round(Unit.convert(quantityFrom, unitFrom, unitTo));

        getResultFromQuantity().setText(Double.toString(quantityFrom));
        getResultFromUnit().setText(unitFrom.name);
        getResultToQuantity().setText(Double.toString(quantityTo));
        getResultToUnit().setText(unitTo.name);
        switchDisplayable(null, getConversionResult());//GEN-LINE:|683-entry|1|684-postAction
        // write post-action user code here
    }//GEN-BEGIN:|683-entry|2|
    //</editor-fold>//GEN-END:|683-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: RESTART_COMMAND ">//GEN-BEGIN:|695-getter|0|695-preInit
    /**
     * Returns an initiliazed instance of RESTART_COMMAND component.
     * @return the initialized component instance
     */
    public Command getRESTART_COMMAND() {
        if (RESTART_COMMAND == null) {//GEN-END:|695-getter|0|695-preInit
            // write pre-init user code here
            RESTART_COMMAND = new Command("Restart", Command.SCREEN, 0);//GEN-LINE:|695-getter|1|695-postInit
            // write post-init user code here
        }//GEN-BEGIN:|695-getter|2|
        return RESTART_COMMAND;
    }
    //</editor-fold>//GEN-END:|695-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: applyScreenMode ">//GEN-BEGIN:|698-entry|0|699-preAction
    /**
     * Performs an action assigned to the applyScreenMode entry-point.
     */
    public void applyScreenMode() {//GEN-END:|698-entry|0|699-preAction
        // write pre-action user code here
        getReflowChapterScreen().setTask(getApplyScreenModeTask());
        switchDisplayable(null, getReflowChapterScreen());//GEN-LINE:|698-entry|1|699-postAction
        // write post-action user code here
    }//GEN-BEGIN:|698-entry|2|
    //</editor-fold>//GEN-END:|698-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: showBookInfo ">//GEN-BEGIN:|720-entry|0|721-preAction
    /**
     * Performs an action assigned to the showBookInfo entry-point.
     */
    public void showBookInfo() {//GEN-END:|720-entry|0|721-preAction
        // write pre-action user code here
        switchDisplayable(null, getBookInfo());//GEN-LINE:|720-entry|1|721-postAction
        // write post-action user code here
    }//GEN-BEGIN:|720-entry|2|
    //</editor-fold>//GEN-END:|720-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: unloadInfo ">//GEN-BEGIN:|727-entry|0|728-preAction
    /**
     * Performs an action assigned to the unloadInfo entry-point.
     */
    public void unloadInfo() {//GEN-END:|727-entry|0|728-preAction
        // write pre-action user code here
        bookInfo = null;
        returnToMenu();//GEN-LINE:|727-entry|1|728-postAction
        // write post-action user code here
    }//GEN-BEGIN:|727-entry|2|
    //</editor-fold>//GEN-END:|727-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: bookInfo ">//GEN-BEGIN:|724-getter|0|724-preInit
    /**
     * Returns an initiliazed instance of bookInfo component.
     * @return the initialized component instance
     */
    public Form getBookInfo() {
        if (bookInfo == null) {//GEN-END:|724-getter|0|724-preInit
            // write pre-init user code here
            bookInfo = new Form("Book details");//GEN-BEGIN:|724-getter|1|724-postInit
            bookInfo.addCommand(getDISMISS_COMMAND());
            bookInfo.setCommandListener(this);//GEN-END:|724-getter|1|724-postInit
            // write post-init user code here
            bookCanvas.fillBookInfo(bookInfo);
        }//GEN-BEGIN:|724-getter|2|
        return bookInfo;
    }
    //</editor-fold>//GEN-END:|724-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: dictionariesFound ">//GEN-BEGIN:|747-if|0|747-preIf
    /**
     * Performs an action assigned to the dictionariesFound if-point.
     */
    public void dictionariesFound() {//GEN-END:|747-if|0|747-preIf
        // enter pre-if user code here
        if (getDictionaries().size() > 0) {//GEN-LINE:|747-if|1|748-preAction
            // write pre-action user code here
            switchDisplayable(null, getWordBox());//GEN-LINE:|747-if|2|748-postAction
            // write post-action user code here
        } else {//GEN-LINE:|747-if|3|749-preAction
            // write pre-action user code here
            switchDisplayable(null, getNoDictionaries());//GEN-LINE:|747-if|4|749-postAction
            // write post-action user code here
        }//GEN-LINE:|747-if|5|747-postIf
        // enter post-if user code here
    }//GEN-BEGIN:|747-if|6|
    //</editor-fold>//GEN-END:|747-if|6|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: noDictionaries ">//GEN-BEGIN:|750-getter|0|750-preInit
    /**
     * Returns an initiliazed instance of noDictionaries component.
     * @return the initialized component instance
     */
    public Alert getNoDictionaries() {
        if (noDictionaries == null) {//GEN-END:|750-getter|0|750-preInit
            // write pre-init user code here
            noDictionaries = new Alert("Sorry!", "No dictionaries found or folder not set.", null, AlertType.WARNING);//GEN-BEGIN:|750-getter|1|750-postInit
            noDictionaries.addCommand(getDISMISS_COMMAND());
            noDictionaries.setCommandListener(this);
            noDictionaries.setTimeout(Alert.FOREVER);//GEN-END:|750-getter|1|750-postInit
            // write post-init user code here
        }//GEN-BEGIN:|750-getter|2|
        return noDictionaries;
    }
    //</editor-fold>//GEN-END:|750-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: clearSuggestions ">//GEN-BEGIN:|757-entry|0|758-preAction
    /**
     * Performs an action assigned to the clearSuggestions entry-point.
     */
    public void clearSuggestions() {//GEN-END:|757-entry|0|758-preAction
        /*
         * Clear suggestions
         */
        suggestions = null;
        setDictionary();//GEN-LINE:|757-entry|1|758-postAction
        // write post-action user code here
    }//GEN-BEGIN:|757-entry|2|
    //</editor-fold>//GEN-END:|757-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: dictionaries ">//GEN-BEGIN:|760-getter|0|760-preInit
    /**
     * Returns an initiliazed instance of dictionaries component.
     * @return the initialized component instance
     */
    public List getDictionaries() {
        if (dictionaries == null) {//GEN-END:|760-getter|0|760-preInit
            // write pre-init user code here
            dictionaries = new List("Dictionaries", Choice.IMPLICIT);//GEN-BEGIN:|760-getter|1|760-postInit
            dictionaries.addCommand(getNEXT_COMMAND());
            dictionaries.addCommand(getBACK_COMMAND());
            dictionaries.setCommandListener(this);
            dictionaries.setFitPolicy(Choice.TEXT_WRAP_DEFAULT);
            dictionaries.setSelectCommand(getNEXT_COMMAND());//GEN-END:|760-getter|1|760-postInit
            // write post-init user code here
        }//GEN-BEGIN:|760-getter|2|
        return dictionaries;
    }
    //</editor-fold>//GEN-END:|760-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: dictionariesAction ">//GEN-BEGIN:|760-action|0|760-preAction
    /**
     * Performs an action assigned to the selected list element in the dictionaries component.
     */
    public void dictionariesAction() {//GEN-END:|760-action|0|760-preAction
        // enter pre-action user code here
        String __selectedString = getDictionaries().getString(getDictionaries().getSelectedIndex());//GEN-LINE:|760-action|1|760-postAction
        // enter post-action user code here
    }//GEN-BEGIN:|760-action|2|
    //</editor-fold>//GEN-END:|760-action|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: lookup ">//GEN-BEGIN:|764-getter|0|764-preInit
    /**
     * Returns an initiliazed instance of lookup component.
     * @return the initialized component instance
     */
    public WaitScreen getLookup() {
        if (lookup == null) {//GEN-END:|764-getter|0|764-preInit
            // write pre-init user code here
            lookup = new WaitScreen(getDisplay());//GEN-BEGIN:|764-getter|1|764-postInit
            lookup.setTitle("Word lookup");
            lookup.setCommandListener(this);
            lookup.setFullScreenMode(true);
            lookup.setImage(getAlbiteLogo());
            lookup.setText("Searching, please wait...");
            lookup.setTextFont(getLoadingFont());
            lookup.setTask(getLookupTask());//GEN-END:|764-getter|1|764-postInit
            // write post-init user code here
        }//GEN-BEGIN:|764-getter|2|
        return lookup;
    }
    //</editor-fold>//GEN-END:|764-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: dictionaryError ">//GEN-BEGIN:|771-getter|0|771-preInit
    /**
     * Returns an initiliazed instance of dictionaryError component.
     * @return the initialized component instance
     */
    public Alert getDictionaryError() {
        if (dictionaryError == null) {//GEN-END:|771-getter|0|771-preInit
            // write pre-init user code here
            dictionaryError = new Alert("Sorry!", "Couldn\'t load the dictionary.", null, AlertType.ERROR);//GEN-BEGIN:|771-getter|1|771-postInit
            dictionaryError.addCommand(getDISMISS_COMMAND());
            dictionaryError.setCommandListener(this);
            dictionaryError.setTimeout(Alert.FOREVER);//GEN-END:|771-getter|1|771-postInit
            // write post-init user code here
        }//GEN-BEGIN:|771-getter|2|
        return dictionaryError;
    }
    //</editor-fold>//GEN-END:|771-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: lookupTask ">//GEN-BEGIN:|767-getter|0|767-preInit
    /**
     * Returns an initiliazed instance of lookupTask component.
     * @return the initialized component instance
     */
    public SimpleCancellableTask getLookupTask() {
        if (lookupTask == null) {//GEN-END:|767-getter|0|767-preInit
            // write pre-init user code here
            lookupTask = new SimpleCancellableTask();//GEN-BEGIN:|767-getter|1|767-execute
            lookupTask.setExecutable(new org.netbeans.microedition.util.Executable() {
                public void execute() throws Exception {//GEN-END:|767-getter|1|767-execute
                    // write task-execution user code here
                    try {
                        searchResult = selectedDictionary.lookUp(searchWord);
                    } catch (OutOfMemoryError e) {
                        dictman.unloadDictionaries();
                        Runtime.getRuntime().gc();
                    }
                }//GEN-BEGIN:|767-getter|2|767-postInit
            });//GEN-END:|767-getter|2|767-postInit
            // write post-init user code here
        }//GEN-BEGIN:|767-getter|3|
        return lookupTask;
    }
    //</editor-fold>//GEN-END:|767-getter|3|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: wordFound ">//GEN-BEGIN:|777-if|0|777-preIf
    /**
     * Performs an action assigned to the wordFound if-point.
     */
    public void wordFound() {//GEN-END:|777-if|0|777-preIf
        // enter pre-if user code here
        if (searchResult.length == 1) {//GEN-LINE:|777-if|1|778-preAction
            // write pre-action user code here
            setDefinition();//GEN-LINE:|777-if|2|778-postAction
            // write post-action user code here
        } else {//GEN-LINE:|777-if|3|779-preAction
            // write pre-action user code here
            fillSuggestions();//GEN-LINE:|777-if|4|779-postAction
            // write post-action user code here
        }//GEN-LINE:|777-if|5|777-postIf
        // enter post-if user code here
    }//GEN-BEGIN:|777-if|6|
    //</editor-fold>//GEN-END:|777-if|6|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: setDefinition ">//GEN-BEGIN:|782-entry|0|783-preAction
    /**
     * Performs an action assigned to the setDefinition entry-point.
     */
    public void setDefinition() {//GEN-END:|782-entry|0|783-preAction
        // write pre-action user code here

        if (searchResult != null) {
            Form f = getWordDefinition();
            f.deleteAll();

            f.append(
                    new StringItem("Word:", searchWord));
            f.append(
                    new StringItem("Definition:", searchResult[0]));
            f.append(
                    new StringItem(
                        "Dictionary:",
                        selectedDictionary.getTitle()));
        }
        switchDisplayable(null, getWordDefinition());//GEN-LINE:|782-entry|1|783-postAction
        // write post-action user code here
    }//GEN-BEGIN:|782-entry|2|
    //</editor-fold>//GEN-END:|782-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: setWord ">//GEN-BEGIN:|791-entry|0|792-preAction
    /**
     * Performs an action assigned to the setWord entry-point.
     */
    public void setWord() {//GEN-END:|791-entry|0|792-preAction
        // write pre-action user code here
        final List sg = getSuggestions();
        final String word = sg.getString(sg.getSelectedIndex());

        /*
         * Setup search word
         */
        searchWord = word;

        setupSearch();//GEN-LINE:|791-entry|1|792-postAction
        // write post-action user code here
    }//GEN-BEGIN:|791-entry|2|
    //</editor-fold>//GEN-END:|791-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: setDictionary ">//GEN-BEGIN:|805-entry|0|806-preAction
    /**
     * Performs an action assigned to the setDictionary entry-point.
     */
    public void setDictionary() {//GEN-END:|805-entry|0|806-preAction
        // write pre-action user code here
        Dictionary[] dicts = dictman.getDictionaries();
        selectedDictionary =
                dicts[getDictionaries().getSelectedIndex()];

        /*
         * Reset search word
         */
        searchWord = getWordBox().getString();

        /*
         * Reset suggestions
         */
        suggestions = null;
        setupSearch();//GEN-LINE:|805-entry|1|806-postAction
        // write post-action user code here
    }//GEN-BEGIN:|805-entry|2|
    //</editor-fold>//GEN-END:|805-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: setupSearch ">//GEN-BEGIN:|810-entry|0|811-preAction
    /**
     * Performs an action assigned to the setupSearch entry-point.
     */
    public void setupSearch() {//GEN-END:|810-entry|0|811-preAction
        // write pre-action user code here
        /*
         * Setup wait string
         */
        WaitScreen w = getLookup();

        switchDisplayable(null, getLookup());//GEN-LINE:|810-entry|1|811-postAction
        // write post-action user code here
    }//GEN-BEGIN:|810-entry|2|
    //</editor-fold>//GEN-END:|810-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: numberBox ">//GEN-BEGIN:|245-getter|0|245-preInit
    /**
     * Returns an initiliazed instance of numberBox component.
     * @return the initialized component instance
     */
    public TextBox getNumberBox() {
        if (numberBox == null) {//GEN-END:|245-getter|0|245-preInit
            // write pre-init user code here
            numberBox = new TextBox("Enter number", "", 64, TextField.DECIMAL);//GEN-BEGIN:|245-getter|1|245-postInit
            numberBox.addCommand(getBACK_COMMAND());
            numberBox.addCommand(getNEXT_COMMAND());
            numberBox.setCommandListener(this);
            numberBox.setInitialInputMode("UCB_BASIC_LATIN");//GEN-END:|245-getter|1|245-postInit
            // write post-init user code here
        }//GEN-BEGIN:|245-getter|2|
        return numberBox;
    }
    //</editor-fold>//GEN-END:|245-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: fillSuggestions ">//GEN-BEGIN:|819-entry|0|820-preAction
    /**
     * Performs an action assigned to the fillSuggestions entry-point.
     */
    public void fillSuggestions() {//GEN-END:|819-entry|0|820-preAction
        // write pre-action user code here
        suggestions = null;
        final List l = getSuggestions();

        for (int i = 0; i < searchResult.length; i++) {
            l.append(searchResult[i], null);
        }
        switchDisplayable(null, getSuggestions());//GEN-LINE:|819-entry|1|820-postAction
        // write post-action user code her
    }//GEN-BEGIN:|819-entry|2|
    //</editor-fold>//GEN-END:|819-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: backToSuggestions ">//GEN-BEGIN:|824-if|0|824-preIf
    /**
     * Performs an action assigned to the backToSuggestions if-point.
     */
    public void backToSuggestions() {//GEN-END:|824-if|0|824-preIf
        // enter pre-if user code here
        if (getSuggestions().size() > 0) {//GEN-LINE:|824-if|1|825-preAction
            // write pre-action user code here
            switchDisplayable(null, getSuggestions());//GEN-LINE:|824-if|2|825-postAction
            // write post-action user code here
        } else {//GEN-LINE:|824-if|3|826-preAction
            // write pre-action user code here
            switchDisplayable(null, getDictionaries());//GEN-LINE:|824-if|4|826-postAction
            // write post-action user code here
        }//GEN-LINE:|824-if|5|824-postIf
        // enter post-if user code here
    }//GEN-BEGIN:|824-if|6|
    //</editor-fold>//GEN-END:|824-if|6|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: folderBrowser ">//GEN-BEGIN:|852-getter|0|852-preInit
    /**
     * Returns an initiliazed instance of folderBrowser component.
     * @return the initialized component instance
     */
    public FolderBrowser getFolderBrowser() {
        if (folderBrowser == null) {//GEN-END:|852-getter|0|852-preInit
            // write pre-init user code here
            folderBrowser = new FolderBrowser(getDisplay());//GEN-BEGIN:|852-getter|1|852-postInit
            folderBrowser.setTitle(null);
            folderBrowser.addCommand(getCANCEL_COMMAND());
            folderBrowser.setCommandListener(this);
            folderBrowser.setCommandListener(this);//GEN-END:|852-getter|1|852-postInit
            // write post-init user code here
            folderBrowser.addCommand(FolderBrowser.SELECT_FOLDER_COMMAND);
        }//GEN-BEGIN:|852-getter|2|
        return folderBrowser;
    }
    //</editor-fold>//GEN-END:|852-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: folderBrowserAction ">//GEN-BEGIN:|852-action|0|852-preAction
    /**
     * Performs an action assigned to the selected list element in the folderBrowser component.
     */
    public void folderBrowserAction() {//GEN-END:|852-action|0|852-preAction
        // enter pre-action user code here
        String __selectedString = getFolderBrowser().getString(getFolderBrowser().getSelectedIndex());//GEN-LINE:|852-action|1|852-postAction
        // enter post-action user code here
    }//GEN-BEGIN:|852-action|2|
    //</editor-fold>//GEN-END:|852-action|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: scanDictionaries ">//GEN-BEGIN:|874-entry|0|875-preAction
    /**
     * Performs an action assigned to the scanDictionaries entry-point.
     */
    public void scanDictionaries() {//GEN-END:|874-entry|0|875-preAction
        // write pre-action user code here
        switchDisplayable(null, getScanningDictionaries());//GEN-LINE:|874-entry|1|875-postAction
        // write post-action user code here
    }//GEN-BEGIN:|874-entry|2|
    //</editor-fold>//GEN-END:|874-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: scanningDictionaries ">//GEN-BEGIN:|869-getter|0|869-preInit
    /**
     * Returns an initiliazed instance of scanningDictionaries component.
     * @return the initialized component instance
     */
    public WaitScreen getScanningDictionaries() {
        if (scanningDictionaries == null) {//GEN-END:|869-getter|0|869-preInit
            // write pre-init user code here
            scanningDictionaries = new WaitScreen(getDisplay());//GEN-BEGIN:|869-getter|1|869-postInit
            scanningDictionaries.setTitle(null);
            scanningDictionaries.setCommandListener(this);
            scanningDictionaries.setFullScreenMode(true);
            scanningDictionaries.setImage(getAlbiteLogo());
            scanningDictionaries.setText("Scanning dictionaries...");
            scanningDictionaries.setTextFont(getLoadingFont());
            scanningDictionaries.setTask(getScanningDictionariesTask());//GEN-END:|869-getter|1|869-postInit
            // write post-init user code here
        }//GEN-BEGIN:|869-getter|2|
        return scanningDictionaries;
    }
    //</editor-fold>//GEN-END:|869-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: scanningDictionariesTask ">//GEN-BEGIN:|872-getter|0|872-preInit
    /**
     * Returns an initiliazed instance of scanningDictionariesTask component.
     * @return the initialized component instance
     */
    public SimpleCancellableTask getScanningDictionariesTask() {
        if (scanningDictionariesTask == null) {//GEN-END:|872-getter|0|872-preInit
            // write pre-init user code here
            scanningDictionariesTask = new SimpleCancellableTask();//GEN-BEGIN:|872-getter|1|872-execute
            scanningDictionariesTask.setExecutable(new org.netbeans.microedition.util.Executable() {
                public void execute() throws Exception {//GEN-END:|872-getter|1|872-execute
                    // write task-execution user code here
                    dictsFolder = folderBrowser.getSelectedFolderURL();
                    dictman.reloadDictionaries(dictsFolder);
                    fillDicts();
                }//GEN-BEGIN:|872-getter|2|872-postInit
            });//GEN-END:|872-getter|2|872-postInit
            // write post-init user code here
        }//GEN-BEGIN:|872-getter|3|
        return scanningDictionariesTask;
    }
    //</editor-fold>//GEN-END:|872-getter|3|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: applyPageOptions ">//GEN-BEGIN:|885-entry|0|886-preAction
    /**
     * Performs an action assigned to the applyPageOptions entry-point.
     */
    public void applyPageOptions() {//GEN-END:|885-entry|0|886-preAction
        // write pre-action user code here
        getReflowChapterScreen().setTask(getApplyPageOptionsTask());
        switchDisplayable(null, getReflowChapterScreen());//GEN-LINE:|885-entry|1|886-postAction
        // write post-action user code here
    }//GEN-BEGIN:|885-entry|2|
    //</editor-fold>//GEN-END:|885-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: pageSettings ">//GEN-BEGIN:|878-getter|0|878-preInit
    /**
     * Returns an initiliazed instance of pageSettings component.
     * @return the initialized component instance
     */
    public Form getPageSettings() {
        if (pageSettings == null) {//GEN-END:|878-getter|0|878-preInit
            // write pre-init user code here
            pageSettings = new Form("Page layout", new Item[] { getPageMargins(), getLineSpacing(), getReloadImages() });//GEN-BEGIN:|878-getter|1|878-postInit
            pageSettings.addCommand(getAPPLY_COMMAND());
            pageSettings.addCommand(getBACK_COMMAND());
            pageSettings.setCommandListener(this);//GEN-END:|878-getter|1|878-postInit
            // write post-init user code here
        }//GEN-BEGIN:|878-getter|2|
        return pageSettings;
    }
    //</editor-fold>//GEN-END:|878-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: pageMargins ">//GEN-BEGIN:|879-getter|0|879-preInit
    /**
     * Returns an initiliazed instance of pageMargins component.
     * @return the initialized component instance
     */
    public Gauge getPageMargins() {
        if (pageMargins == null) {//GEN-END:|879-getter|0|879-preInit
            // write pre-init user code here
            pageMargins = new Gauge("Page margins:", true, 20, bookCanvas.getCurrentMargin());//GEN-LINE:|879-getter|1|879-postInit
            // write post-init user code here
        }//GEN-BEGIN:|879-getter|2|
        return pageMargins;
    }
    //</editor-fold>//GEN-END:|879-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: lineSpacing ">//GEN-BEGIN:|880-getter|0|880-preInit
    /**
     * Returns an initiliazed instance of lineSpacing component.
     * @return the initialized component instance
     */
    public Gauge getLineSpacing() {
        if (lineSpacing == null) {//GEN-END:|880-getter|0|880-preInit
            // write pre-init user code here
            lineSpacing = new Gauge("Line spacing:", true, 10, bookCanvas.getCurrentLineSpacing());//GEN-LINE:|880-getter|1|880-postInit
            // write post-init user code here
        }//GEN-BEGIN:|880-getter|2|
        return lineSpacing;
    }
    //</editor-fold>//GEN-END:|880-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: reloadImages ">//GEN-BEGIN:|881-getter|0|881-preInit
    /**
     * Returns an initiliazed instance of reloadImages component.
     * @return the initialized component instance
     */
    public ChoiceGroup getReloadImages() {
        if (reloadImages == null) {//GEN-END:|881-getter|0|881-preInit
            // write pre-init user code here
            reloadImages = new ChoiceGroup("", Choice.MULTIPLE);//GEN-BEGIN:|881-getter|1|881-postInit
            reloadImages.append("Render images in books", null);
            reloadImages.setSelectedFlags(new boolean[] { bookCanvas.rendersImages() });//GEN-END:|881-getter|1|881-postInit
            // write post-init user code here
        }//GEN-BEGIN:|881-getter|2|
        return reloadImages;
    }
    //</editor-fold>//GEN-END:|881-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: bookmarks ">//GEN-BEGIN:|890-getter|0|890-preInit
    /**
     * Returns an initiliazed instance of bookmarks component.
     * @return the initialized component instance
     */
    public List getBookmarks() {
        if (bookmarks == null) {//GEN-END:|890-getter|0|890-preInit
            // write pre-init user code here
            bookmarks = new List("Bookmarks", Choice.IMPLICIT);//GEN-BEGIN:|890-getter|1|890-postInit
            bookmarks.addCommand(getBACK_COMMAND());
            bookmarks.addCommand(getGO_COMMAND());
            bookmarks.addCommand(getADD_COMMAND());
            bookmarks.addCommand(getEDIT_COMMAND());
            bookmarks.addCommand(getDELETE_COMMAND());
            bookmarks.setCommandListener(this);
            bookmarks.setFitPolicy(Choice.TEXT_WRAP_OFF);
            bookmarks.setSelectCommand(getGO_COMMAND());//GEN-END:|890-getter|1|890-postInit
            // write post-init user code here
        }//GEN-BEGIN:|890-getter|2|
        return bookmarks;
    }
    //</editor-fold>//GEN-END:|890-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: bookmarksAction ">//GEN-BEGIN:|890-action|0|890-preAction
    /**
     * Performs an action assigned to the selected list element in the bookmarks component.
     */
    public void bookmarksAction() {//GEN-END:|890-action|0|890-preAction
        // enter pre-action user code here
//GEN-LINE:|890-action|1|890-postAction
        // enter post-action user code here
    }//GEN-BEGIN:|890-action|2|
    //</editor-fold>//GEN-END:|890-action|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: deleteBookmarkAlert ">//GEN-BEGIN:|893-getter|0|893-preInit
    /**
     * Returns an initiliazed instance of deleteBookmarkAlert component.
     * @return the initialized component instance
     */
    public Alert getDeleteBookmarkAlert() {
        if (deleteBookmarkAlert == null) {//GEN-END:|893-getter|0|893-preInit
            // write pre-init user code here
            deleteBookmarkAlert = new Alert("Are you sure?", "Do you want to delete this bookmark?", null, AlertType.CONFIRMATION);//GEN-BEGIN:|893-getter|1|893-postInit
            deleteBookmarkAlert.addCommand(getNO_COMMAND());
            deleteBookmarkAlert.addCommand(getYES_COMMAND());
            deleteBookmarkAlert.setCommandListener(this);
            deleteBookmarkAlert.setTimeout(Alert.FOREVER);//GEN-END:|893-getter|1|893-postInit
            // write post-init user code here
        }//GEN-BEGIN:|893-getter|2|
        return deleteBookmarkAlert;
    }
    //</editor-fold>//GEN-END:|893-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: bookmarkText ">//GEN-BEGIN:|894-getter|0|894-preInit
    /**
     * Returns an initiliazed instance of bookmarkText component.
     * @return the initialized component instance
     */
    public TextBox getBookmarkText() {
        if (bookmarkText == null) {//GEN-END:|894-getter|0|894-preInit
            // write pre-init user code here
            bookmarkText = new TextBox("Bookmark text", "", 256, TextField.ANY);//GEN-BEGIN:|894-getter|1|894-postInit
            bookmarkText.addCommand(getBACK_COMMAND());
            bookmarkText.addCommand(getNEXT_COMMAND());
            bookmarkText.setCommandListener(this);
            bookmarkText.setInitialInputMode("UCB_BASIC_LATIN");//GEN-END:|894-getter|1|894-postInit
            // write post-init user code here
        }//GEN-BEGIN:|894-getter|2|
        return bookmarkText;
    }
    //</editor-fold>//GEN-END:|894-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: addBookmarkAutomatically ">//GEN-BEGIN:|895-entry|0|896-preAction
    /**
     * Performs an action assigned to the addBookmarkAutomatically entry-point.
     */
    public void addBookmarkAutomatically() {//GEN-END:|895-entry|0|896-preAction
        // write pre-action user code here
        getBookmarkText().setString(bookmarkString);
        switchDisplayable(null, getBookmarkText());//GEN-LINE:|895-entry|1|896-postAction
        // write post-action user code here
    }//GEN-BEGIN:|895-entry|2|
    //</editor-fold>//GEN-END:|895-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: processBookmark ">//GEN-BEGIN:|898-entry|0|899-preAction
    /**
     * Performs an action assigned to the processBookmark entry-point.
     */
    public void processBookmark() {//GEN-END:|898-entry|0|899-preAction
        // write pre-action user code here
        final Book book = bookCanvas.getCurrentBook();
        final String s = getBookmarkText().getString();

        if (bookmarkAdding) {
            /*
             * Adding a new bookmark
             */
            final Bookmark bookmark =
                    new Bookmark(book.getCurrentChapter(), bookmarkPosition, s);

            final int pos = book.getBookmarkManager().addBookmark(bookmark);

            /*
             * Insert the new bookmark into the list
             */
            getBookmarks().insert(pos, bookmark.getTextForList(), null);
        } else {
            /*
             * Eding the selected bookmark
             */
            final int pos = getBookmarks().getSelectedIndex();

            final Bookmark bookmark =
                    book.getBookmarkManager().bookmarkAt(pos);

             if (bookmark != null) {
                 /*
                  * Update the bookmark
                  */
                 bookmark.setText(s);

                 /*
                  * Update the list
                  */
                 bookmarks.set(pos, bookmark.getTextForList(), null);
             }
        }
        bookCanvas.saveBookmarks();
        proceedToBookmarks();//GEN-LINE:|898-entry|1|899-postAction
        // write post-action user code here
    }//GEN-BEGIN:|898-entry|2|
    //</editor-fold>//GEN-END:|898-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: deleteBookmark ">//GEN-BEGIN:|900-entry|0|901-preAction
    /**
     * Performs an action assigned to the deleteBookmark entry-point.
     */
    public void deleteBookmark() {//GEN-END:|900-entry|0|901-preAction
        // write pre-action user code here

        final int pos = getBookmarks().getSelectedIndex();
        final Book book = bookCanvas.getCurrentBook();

        if (pos != -1) {
            /*
             * Deleting the bookmark
             */
            book.getBookmarkManager().deleteBookmarkAt(pos);

            /*
             * Delete from the list
             */
            bookmarks.delete(pos);
        }

        switchDisplayable(null, getBookmarks());//GEN-LINE:|900-entry|1|901-postAction
        // write post-action user code here
    }//GEN-BEGIN:|900-entry|2|
    //</editor-fold>//GEN-END:|900-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: ADD_COMMAND ">//GEN-BEGIN:|917-getter|0|917-preInit
    /**
     * Returns an initiliazed instance of ADD_COMMAND component.
     * @return the initialized component instance
     */
    public Command getADD_COMMAND() {
        if (ADD_COMMAND == null) {//GEN-END:|917-getter|0|917-preInit
            // write pre-init user code here
            ADD_COMMAND = new Command("Add", Command.SCREEN, 0);//GEN-LINE:|917-getter|1|917-postInit
            // write post-init user code here
        }//GEN-BEGIN:|917-getter|2|
        return ADD_COMMAND;
    }
    //</editor-fold>//GEN-END:|917-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: EDIT_COMMAND ">//GEN-BEGIN:|919-getter|0|919-preInit
    /**
     * Returns an initiliazed instance of EDIT_COMMAND component.
     * @return the initialized component instance
     */
    public Command getEDIT_COMMAND() {
        if (EDIT_COMMAND == null) {//GEN-END:|919-getter|0|919-preInit
            // write pre-init user code here
            EDIT_COMMAND = new Command("Edit", Command.SCREEN, 0);//GEN-LINE:|919-getter|1|919-postInit
            // write post-init user code here
        }//GEN-BEGIN:|919-getter|2|
        return EDIT_COMMAND;
    }
    //</editor-fold>//GEN-END:|919-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: DELETE_COMMAND ">//GEN-BEGIN:|921-getter|0|921-preInit
    /**
     * Returns an initiliazed instance of DELETE_COMMAND component.
     * @return the initialized component instance
     */
    public Command getDELETE_COMMAND() {
        if (DELETE_COMMAND == null) {//GEN-END:|921-getter|0|921-preInit
            // write pre-init user code here
            DELETE_COMMAND = new Command("Delete", Command.SCREEN, 0);//GEN-LINE:|921-getter|1|921-postInit
            // write post-init user code here
        }//GEN-BEGIN:|921-getter|2|
        return DELETE_COMMAND;
    }
    //</editor-fold>//GEN-END:|921-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: goToBookmark ">//GEN-BEGIN:|924-entry|0|925-preAction
    /**
     * Performs an action assigned to the goToBookmark entry-point.
     */
    public void goToBookmark() {//GEN-END:|924-entry|0|925-preAction
        // write pre-action user code here
        getReflowChapterScreen().setTask(getGoToBookmarkTask());
        switchDisplayable(null, getReflowChapterScreen());//GEN-LINE:|924-entry|1|925-postAction
        // write post-action user code here
    }//GEN-BEGIN:|924-entry|2|
    //</editor-fold>//GEN-END:|924-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: returnToBookmarks ">//GEN-BEGIN:|954-if|0|954-preIf
    /**
     * Performs an action assigned to the returnToBookmarks if-point.
     */
    public void returnToBookmarks() {//GEN-END:|954-if|0|954-preIf
        // enter pre-if user code here
        if (!calledOutside) {//GEN-LINE:|954-if|1|955-preAction
            // write pre-action user code here
            switchDisplayable(null, getBookmarks());//GEN-LINE:|954-if|2|955-postAction
            // write post-action user code here
        } else {//GEN-LINE:|954-if|3|956-preAction
            // write pre-action user code here
            returnToMenu();//GEN-LINE:|954-if|4|956-postAction
            // write post-action user code here
        }//GEN-LINE:|954-if|5|954-postIf
        // enter post-if user code here
    }//GEN-BEGIN:|954-if|6|
    //</editor-fold>//GEN-END:|954-if|6|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: proceedToBookmarks ">//GEN-BEGIN:|968-if|0|968-preIf
    /**
     * Performs an action assigned to the proceedToBookmarks if-point.
     */
    public void proceedToBookmarks() {//GEN-END:|968-if|0|968-preIf
        // enter pre-if user code here
        if (!bookmarkAdding) {//GEN-LINE:|968-if|1|969-preAction
            // write pre-action user code here
            switchDisplayable(null, getBookmarks());//GEN-LINE:|968-if|2|969-postAction
            // write post-action user code here
        } else {//GEN-LINE:|968-if|3|970-preAction
            // write pre-action user code here
            switchDisplayable(null, bookCanvas);//GEN-LINE:|968-if|4|970-postAction
            // write post-action user code here
        }//GEN-LINE:|968-if|5|968-postIf
        // enter post-if user code here
    }//GEN-BEGIN:|968-if|6|
    //</editor-fold>//GEN-END:|968-if|6|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: canEditBookmark ">//GEN-BEGIN:|974-if|0|974-preIf
    /**
     * Performs an action assigned to the canEditBookmark if-point.
     */
    public void canEditBookmark() {//GEN-END:|974-if|0|974-preIf
        // enter pre-if user code here
        if (bookmarkSelected()) {//GEN-LINE:|974-if|1|975-preAction
            // write pre-action user code here
            switchDisplayable(null, getBookmarkText());//GEN-LINE:|974-if|2|975-postAction
            // write post-action user code here
        } else {//GEN-LINE:|974-if|3|976-preAction
            // write pre-action user code here
            switchDisplayable(null, getNoBookmarksFound());//GEN-LINE:|974-if|4|976-postAction
            // write post-action user code here
        }//GEN-LINE:|974-if|5|974-postIf
        // enter post-if user code here
    }//GEN-BEGIN:|974-if|6|
    //</editor-fold>//GEN-END:|974-if|6|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: canGoToBookmark ">//GEN-BEGIN:|981-if|0|981-preIf
    /**
     * Performs an action assigned to the canGoToBookmark if-point.
     */
    public void canGoToBookmark() {//GEN-END:|981-if|0|981-preIf
        // enter pre-if user code here
        if (bookmarkSelected()) {//GEN-LINE:|981-if|1|982-preAction
            // write pre-action user code here
            goToBookmark();//GEN-LINE:|981-if|2|982-postAction
            // write post-action user code here
        } else {//GEN-LINE:|981-if|3|983-preAction
            // write pre-action user code here
            switchDisplayable(null, getNoBookmarksFound());//GEN-LINE:|981-if|4|983-postAction
            // write post-action user code here
        }//GEN-LINE:|981-if|5|981-postIf
        // enter post-if user code here
    }//GEN-BEGIN:|981-if|6|
    //</editor-fold>//GEN-END:|981-if|6|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: canDeleteBookmark ">//GEN-BEGIN:|984-if|0|984-preIf
    /**
     * Performs an action assigned to the canDeleteBookmark if-point.
     */
    public void canDeleteBookmark() {//GEN-END:|984-if|0|984-preIf
        // enter pre-if user code here
        if (bookmarkSelected()) {//GEN-LINE:|984-if|1|985-preAction
            // write pre-action user code here
            switchDisplayable(null, getDeleteBookmarkAlert());//GEN-LINE:|984-if|2|985-postAction
            // write post-action user code here
        } else {//GEN-LINE:|984-if|3|986-preAction
            // write pre-action user code here
            switchDisplayable(null, getNoBookmarksFound());//GEN-LINE:|984-if|4|986-postAction
            // write post-action user code here
        }//GEN-LINE:|984-if|5|984-postIf
        // enter post-if user code here
    }//GEN-BEGIN:|984-if|6|
    //</editor-fold>//GEN-END:|984-if|6|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: noBookmarksFound ">//GEN-BEGIN:|973-getter|0|973-preInit
    /**
     * Returns an initiliazed instance of noBookmarksFound component.
     * @return the initialized component instance
     */
    public Alert getNoBookmarksFound() {
        if (noBookmarksFound == null) {//GEN-END:|973-getter|0|973-preInit
            // write pre-init user code here
            noBookmarksFound = new Alert("Sorry!", "Select a bookmark first.", null, AlertType.WARNING);//GEN-BEGIN:|973-getter|1|973-postInit
            noBookmarksFound.addCommand(getDISMISS_COMMAND());
            noBookmarksFound.setCommandListener(this);
            noBookmarksFound.setTimeout(Alert.FOREVER);//GEN-END:|973-getter|1|973-postInit
            // write post-init user code here
        }//GEN-BEGIN:|973-getter|2|
        return noBookmarksFound;
    }
    //</editor-fold>//GEN-END:|973-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: lookupWordOrNumber ">//GEN-BEGIN:|1002-entry|0|1003-preAction
    /**
     * Performs an action assigned to the lookupWordOrNumber entry-point.
     */
    public void lookupWordOrNumber() {//GEN-END:|1002-entry|0|1003-preAction
        // write pre-action user code here
        /*
         * Check if it's a word or a number
         */
        boolean isNumber = true;
        try {
            Double.parseDouble(entryForLookup);
        } catch (NumberFormatException e) {
            isNumber = false;
        }

        if (isNumber) {
            /*
             * Show units converter,
             * with the number preentered
             */
            enterNumber();
        } else {
            /*
             * Show dictionary,
             * with the word pre entered
             */
            enterWord();
        }
//GEN-LINE:|1002-entry|1|1003-postAction
        // write post-action user code here
    }//GEN-BEGIN:|1002-entry|2|
    //</editor-fold>//GEN-END:|1002-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: holdingTimeMultiplier ">//GEN-BEGIN:|1012-getter|0|1012-preInit
    /**
     * Returns an initiliazed instance of holdingTimeMultiplier component.
     * @return the initialized component instance
     */
    public Gauge getHoldingTimeMultiplier() {
        if (holdingTimeMultiplier == null) {//GEN-END:|1012-getter|0|1012-preInit
            // write pre-init user code here
            holdingTimeMultiplier = new Gauge("Long press after", true, 4, bookCanvas.getHoldingTimeMultiplier() - 1);//GEN-LINE:|1012-getter|1|1012-postInit
            // write post-init user code here
        }//GEN-BEGIN:|1012-getter|2|
        return holdingTimeMultiplier;
    }
    //</editor-fold>//GEN-END:|1012-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: unitFrom ">//GEN-BEGIN:|266-getter|0|266-preInit
    /**
     * Returns an initiliazed instance of unitFrom component.
     * @return the initialized component instance
     */
    public List getUnitFrom() {
        if (unitFrom == null) {//GEN-END:|266-getter|0|266-preInit
            // write pre-init user code here
            unitFrom = new List("Convert from", Choice.IMPLICIT);//GEN-BEGIN:|266-getter|1|266-postInit
            unitFrom.addCommand(getBACK_COMMAND());
            unitFrom.addCommand(getNEXT_COMMAND());
            unitFrom.setCommandListener(this);
            unitFrom.setSelectCommand(getNEXT_COMMAND());//GEN-END:|266-getter|1|266-postInit
            // write post-init user code here
        }//GEN-BEGIN:|266-getter|2|
        return unitFrom;
    }
    //</editor-fold>//GEN-END:|266-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: unitTo ">//GEN-BEGIN:|269-getter|0|269-preInit
    /**
     * Returns an initiliazed instance of unitTo component.
     * @return the initialized component instance
     */
    public List getUnitTo() {
        if (unitTo == null) {//GEN-END:|269-getter|0|269-preInit
            // write pre-init user code here
            unitTo = new List("Convert to", Choice.IMPLICIT);//GEN-BEGIN:|269-getter|1|269-postInit
            unitTo.addCommand(getBACK_COMMAND());
            unitTo.addCommand(getNEXT_COMMAND());
            unitTo.setCommandListener(this);
            unitTo.setSelectCommand(getNEXT_COMMAND());//GEN-END:|269-getter|1|269-postInit
            // write post-init user code here
        }//GEN-BEGIN:|269-getter|2|
        return unitTo;
    }
    //</editor-fold>//GEN-END:|269-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: conversionResult ">//GEN-BEGIN:|291-getter|0|291-preInit
    /**
     * Returns an initiliazed instance of conversionResult component.
     * @return the initialized component instance
     */
    public Form getConversionResult() {
        if (conversionResult == null) {//GEN-END:|291-getter|0|291-preInit
            // write pre-init user code here
            conversionResult = new Form("Conversion result", new Item[] { getResultFromQuantity(), getResultFromUnit(), getResultToQuantity(), getResultToUnit() });//GEN-BEGIN:|291-getter|1|291-postInit
            conversionResult.addCommand(getCLOSE_COMMAND());
            conversionResult.addCommand(getBACK_COMMAND());
            conversionResult.addCommand(getRESTART_COMMAND());
            conversionResult.setCommandListener(this);//GEN-END:|291-getter|1|291-postInit
            // write post-init user code here
        }//GEN-BEGIN:|291-getter|2|
        return conversionResult;
    }
    //</editor-fold>//GEN-END:|291-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: exitBox ">//GEN-BEGIN:|547-getter|0|547-preInit
    /**
     * Returns an initiliazed instance of exitBox component.
     * @return the initialized component instance
     */
    public Alert getExitBox() {
        if (exitBox == null) {//GEN-END:|547-getter|0|547-preInit
            // write pre-init user code here
            exitBox = new Alert("Quit", "Do you want to quit?", getAlbiteLogo(), AlertType.CONFIRMATION);//GEN-BEGIN:|547-getter|1|547-postInit
            exitBox.addCommand(getYES_COMMAND());
            exitBox.addCommand(getNO_COMMAND());
            exitBox.setCommandListener(this);
            exitBox.setTimeout(Alert.FOREVER);//GEN-END:|547-getter|1|547-postInit
            // write post-init user code here
        }//GEN-BEGIN:|547-getter|2|
        return exitBox;
    }
    //</editor-fold>//GEN-END:|547-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: languages ">//GEN-BEGIN:|1023-getter|0|1023-preInit
    /**
     * Returns an initiliazed instance of languages component.
     * @return the initialized component instance
     */
    public List getLanguages() {
        if (languages == null) {//GEN-END:|1023-getter|0|1023-preInit
            // write pre-init user code here
            languages = new List("Setup hyphenation", Choice.IMPLICIT);//GEN-BEGIN:|1023-getter|1|1023-postInit
            languages.addCommand(getAPPLY_COMMAND());
            languages.addCommand(getBACK_COMMAND());
            languages.setCommandListener(this);
            languages.setSelectCommand(getAPPLY_COMMAND());//GEN-END:|1023-getter|1|1023-postInit
            // write post-init user code here
            languages.append("Auto", null);
            languages.append("No hyphenation", null);

            if (!lightMode) {
                final String[][] langs = Languages.LANGUAGES;
                for (int i = 0; i < langs.length; i++) {
                    languages.append(langs[i][1], null);
                }
            }
        }//GEN-BEGIN:|1023-getter|2|
        return languages;
    }
    //</editor-fold>//GEN-END:|1023-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: languagesAction ">//GEN-BEGIN:|1023-action|0|1023-preAction
    /**
     * Performs an action assigned to the selected list element in the languages component.
     */
    public void languagesAction() {//GEN-END:|1023-action|0|1023-preAction
        // enter pre-action user code here
        String __selectedString = getLanguages().getString(getLanguages().getSelectedIndex());//GEN-LINE:|1023-action|1|1023-postAction
        // enter post-action user code here
    }//GEN-BEGIN:|1023-action|2|
    //</editor-fold>//GEN-END:|1023-action|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: encodings ">//GEN-BEGIN:|1026-getter|0|1026-preInit
    /**
     * Returns an initiliazed instance of encodings component.
     * @return the initialized component instance
     */
    public List getEncodings() {
        if (encodings == null) {//GEN-END:|1026-getter|0|1026-preInit
            // write pre-init user code here
            encodings = new List("Set chapter encoding", Choice.IMPLICIT);//GEN-BEGIN:|1026-getter|1|1026-postInit
            encodings.addCommand(getAPPLY_COMMAND());
            encodings.addCommand(getBACK_COMMAND());
            encodings.setCommandListener(this);
            encodings.setSelectCommand(getAPPLY_COMMAND());//GEN-END:|1026-getter|1|1026-postInit
            // write post-init user code here
            encodings.append("Auto", null);

            final String[] encs = Encodings.ENCODINGS;
            for (int i = 0; i < encs.length; i++) {
                encodings.append(encs[i], null);
            }
        }//GEN-BEGIN:|1026-getter|2|
        return encodings;
    }
    //</editor-fold>//GEN-END:|1026-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: encodingsAction ">//GEN-BEGIN:|1026-action|0|1026-preAction
    /**
     * Performs an action assigned to the selected list element in the encodings component.
     */
    public void encodingsAction() {//GEN-END:|1026-action|0|1026-preAction
        // enter pre-action user code here
        String __selectedString = getEncodings().getString(getEncodings().getSelectedIndex());//GEN-LINE:|1026-action|1|1026-postAction
        // enter post-action user code here
    }//GEN-BEGIN:|1026-action|2|
    //</editor-fold>//GEN-END:|1026-action|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: applyLanguage ">//GEN-BEGIN:|1034-entry|0|1035-preAction
    /**
     * Performs an action assigned to the applyLanguage entry-point.
     */
    public void applyLanguage() {//GEN-END:|1034-entry|0|1035-preAction
        // write pre-action user code here
        getReflowChapterScreen().setTask(getApplyLanguageTask());
        switchDisplayable(null, getReflowChapterScreen());//GEN-LINE:|1034-entry|1|1035-postAction
        // write post-action user code here
    }//GEN-BEGIN:|1034-entry|2|
    //</editor-fold>//GEN-END:|1034-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: applyEncoding ">//GEN-BEGIN:|1041-entry|0|1042-preAction
    /**
     * Performs an action assigned to the applyEncoding entry-point.
     */
    public void applyEncoding() {//GEN-END:|1041-entry|0|1042-preAction
        // write pre-action user code here
        getReflowChapterScreen().setTask(getApplyEncodingTask());
        switchDisplayable(null, getReflowChapterScreen());//GEN-LINE:|1041-entry|1|1042-postAction
        // write post-action user code here
    }//GEN-BEGIN:|1041-entry|2|
    //</editor-fold>//GEN-END:|1041-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: scrollingDirectionGroup ">//GEN-BEGIN:|1047-getter|0|1047-preInit
    /**
     * Returns an initiliazed instance of scrollingDirectionGroup component.
     * @return the initialized component instance
     */
    public ChoiceGroup getScrollingDirectionGroup() {
        if (scrollingDirectionGroup == null) {//GEN-END:|1047-getter|0|1047-preInit
            // write pre-init user code here
            scrollingDirectionGroup = new ChoiceGroup("Scrolling direction", Choice.EXCLUSIVE);//GEN-BEGIN:|1047-getter|1|1047-postInit
            scrollingDirectionGroup.append("Horizontal", null);
            scrollingDirectionGroup.append("Vertical", null);
            scrollingDirectionGroup.setSelectedFlags(new boolean[] { bookCanvas.getHorizontalScalling(), !bookCanvas.getHorizontalScalling() });//GEN-END:|1047-getter|1|1047-postInit
            // write post-init user code here
        }//GEN-BEGIN:|1047-getter|2|
        return scrollingDirectionGroup;
    }
    //</editor-fold>//GEN-END:|1047-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: smoothScrolling ">//GEN-BEGIN:|1050-getter|0|1050-preInit
    /**
     * Returns an initiliazed instance of smoothScrolling component.
     * @return the initialized component instance
     */
    public ChoiceGroup getSmoothScrolling() {
        if (smoothScrolling == null) {//GEN-END:|1050-getter|0|1050-preInit
            // write pre-init user code here
            smoothScrolling = new ChoiceGroup("Smooth scrolling", Choice.MULTIPLE);//GEN-BEGIN:|1050-getter|1|1050-postInit
            smoothScrolling.append("Enable", null);
            smoothScrolling.setSelectedFlags(new boolean[] { bookCanvas.getSmoothScrolling() });//GEN-END:|1050-getter|1|1050-postInit
            // write post-init user code here
        }//GEN-BEGIN:|1050-getter|2|
        return smoothScrolling;
    }
    //</editor-fold>//GEN-END:|1050-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: resultFromQuantity ">//GEN-BEGIN:|306-getter|0|306-preInit
    /**
     * Returns an initiliazed instance of resultFromQuantity component.
     * @return the initialized component instance
     */
    public StringItem getResultFromQuantity() {
        if (resultFromQuantity == null) {//GEN-END:|306-getter|0|306-preInit
            // write pre-init user code here
            resultFromQuantity = new StringItem("Initial quantity:", "");//GEN-LINE:|306-getter|1|306-postInit
            // write post-init user code here
        }//GEN-BEGIN:|306-getter|2|
        return resultFromQuantity;
    }
    //</editor-fold>//GEN-END:|306-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: resultFromUnit ">//GEN-BEGIN:|308-getter|0|308-preInit
    /**
     * Returns an initiliazed instance of resultFromUnit component.
     * @return the initialized component instance
     */
    public StringItem getResultFromUnit() {
        if (resultFromUnit == null) {//GEN-END:|308-getter|0|308-preInit
            // write pre-init user code here
            resultFromUnit = new StringItem("Initial units:", "");//GEN-LINE:|308-getter|1|308-postInit
            // write post-init user code here
        }//GEN-BEGIN:|308-getter|2|
        return resultFromUnit;
    }
    //</editor-fold>//GEN-END:|308-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: resultToUnit ">//GEN-BEGIN:|311-getter|0|311-preInit
    /**
     * Returns an initiliazed instance of resultToUnit component.
     * @return the initialized component instance
     */
    public StringItem getResultToUnit() {
        if (resultToUnit == null) {//GEN-END:|311-getter|0|311-preInit
            // write pre-init user code here
            resultToUnit = new StringItem("Resulting units:", "");//GEN-LINE:|311-getter|1|311-postInit
            // write post-init user code here
        }//GEN-BEGIN:|311-getter|2|
        return resultToUnit;
    }
    //</editor-fold>//GEN-END:|311-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: resultToQuantity ">//GEN-BEGIN:|310-getter|0|310-preInit
    /**
     * Returns an initiliazed instance of resultToQuantity component.
     * @return the initialized component instance
     */
    public StringItem getResultToQuantity() {
        if (resultToQuantity == null) {//GEN-END:|310-getter|0|310-preInit
            // write pre-init user code here
            resultToQuantity = new StringItem("Resulting quantity:", "");//GEN-LINE:|310-getter|1|310-postInit
            // write post-init user code here
        }//GEN-BEGIN:|310-getter|2|
        return resultToQuantity;
    }
    //</editor-fold>//GEN-END:|310-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: reflowChapterScreen ">//GEN-BEGIN:|1056-getter|0|1056-preInit
    /**
     * Returns an initiliazed instance of reflowChapterScreen component.
     * @return the initialized component instance
     */
    public WaitScreen getReflowChapterScreen() {
        if (reflowChapterScreen == null) {//GEN-END:|1056-getter|0|1056-preInit
            // write pre-init user code here
            reflowChapterScreen = new WaitScreen(getDisplay());//GEN-BEGIN:|1056-getter|1|1056-postInit
            reflowChapterScreen.setTitle(null);
            reflowChapterScreen.setCommandListener(this);
            reflowChapterScreen.setFullScreenMode(true);
            reflowChapterScreen.setImage(getAlbiteLogo());
            reflowChapterScreen.setText("Laying out chapter...");
            reflowChapterScreen.setTextFont(getLoadingFont());//GEN-END:|1056-getter|1|1056-postInit
            // write post-init user code here
        }//GEN-BEGIN:|1056-getter|2|
        return reflowChapterScreen;
    }
    //</editor-fold>//GEN-END:|1056-getter|2|
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: applyEncodingTask ">//GEN-BEGIN:|1061-getter|0|1061-preInit
    /**
     * Returns an initiliazed instance of applyEncodingTask component.
     * @return the initialized component instance
     */
    public SimpleCancellableTask getApplyEncodingTask() {
        if (applyEncodingTask == null) {//GEN-END:|1061-getter|0|1061-preInit
            // write pre-init user code here
            applyEncodingTask = new SimpleCancellableTask();//GEN-BEGIN:|1061-getter|1|1061-execute
            applyEncodingTask.setExecutable(new org.netbeans.microedition.util.Executable() {
                public void execute() throws Exception {//GEN-END:|1061-getter|1|1061-execute
                    // write task-execution user code here
                    final int index = getEncodings().getSelectedIndex();

                    if (index != -1) {
                        if (index == 0) {
                            bookCanvas.setAutoChapterEncoding();
                        } else {
                            bookCanvas.setChapterEncoding(
                                    Encodings.ENCODINGS[index - 1]);
                        }
                    }
                }//GEN-BEGIN:|1061-getter|2|1061-postInit
            });//GEN-END:|1061-getter|2|1061-postInit
            // write post-init user code here
        }//GEN-BEGIN:|1061-getter|3|
        return applyEncodingTask;
    }
    //</editor-fold>//GEN-END:|1061-getter|3|
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: applyFontSizeTask ">//GEN-BEGIN:|1062-getter|0|1062-preInit
    /**
     * Returns an initiliazed instance of applyFontSizeTask component.
     * @return the initialized component instance
     */
    public SimpleCancellableTask getApplyFontSizeTask() {
        if (applyFontSizeTask == null) {//GEN-END:|1062-getter|0|1062-preInit
            // write pre-init user code here
            applyFontSizeTask = new SimpleCancellableTask();//GEN-BEGIN:|1062-getter|1|1062-execute
            applyFontSizeTask.setExecutable(new org.netbeans.microedition.util.Executable() {
                public void execute() throws Exception {//GEN-END:|1062-getter|1|1062-execute
                    // write task-execution user code here
                    bookCanvas.setFontSize((byte) fontSizes.getSelectedIndex());
                }//GEN-BEGIN:|1062-getter|2|1062-postInit
            });//GEN-END:|1062-getter|2|1062-postInit
            // write post-init user code here
        }//GEN-BEGIN:|1062-getter|3|
        return applyFontSizeTask;
    }
    //</editor-fold>//GEN-END:|1062-getter|3|
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: applyLanguageTask ">//GEN-BEGIN:|1063-getter|0|1063-preInit
    /**
     * Returns an initiliazed instance of applyLanguageTask component.
     * @return the initialized component instance
     */
    public SimpleCancellableTask getApplyLanguageTask() {
        if (applyLanguageTask == null) {//GEN-END:|1063-getter|0|1063-preInit
            // write pre-init user code here
            applyLanguageTask = new SimpleCancellableTask();//GEN-BEGIN:|1063-getter|1|1063-execute
            applyLanguageTask.setExecutable(new org.netbeans.microedition.util.Executable() {
                public void execute() throws Exception {//GEN-END:|1063-getter|1|1063-execute
                    // write task-execution user code here
                    final int index = getLanguages().getSelectedIndex();

                    if (index != -1) {
                        if (index == 0) {
                            /*
                             * Try to guess language automatically
                             */
                            bookCanvas.setAutoBookLanguage();
                        } else if (index == 1) {
                            /*
                             * Don't use hyphenation
                             */
                            bookCanvas.setBookLanguage(Languages.NO_LANGUAGE);
                        } else {
                            bookCanvas.setBookLanguage(Languages.LANGUAGES[index - 1][0]);
                        }
                    }
                }//GEN-BEGIN:|1063-getter|2|1063-postInit
            });//GEN-END:|1063-getter|2|1063-postInit
            // write post-init user code here
        }//GEN-BEGIN:|1063-getter|3|
        return applyLanguageTask;
    }
    //</editor-fold>//GEN-END:|1063-getter|3|
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: applyPageOptionsTask ">//GEN-BEGIN:|1064-getter|0|1064-preInit
    /**
     * Returns an initiliazed instance of applyPageOptionsTask component.
     * @return the initialized component instance
     */
    public SimpleCancellableTask getApplyPageOptionsTask() {
        if (applyPageOptionsTask == null) {//GEN-END:|1064-getter|0|1064-preInit
            // write pre-init user code here
            applyPageOptionsTask = new SimpleCancellableTask();//GEN-BEGIN:|1064-getter|1|1064-execute
            applyPageOptionsTask.setExecutable(new org.netbeans.microedition.util.Executable() {
                public void execute() throws Exception {//GEN-END:|1064-getter|1|1064-execute
                    // write task-execution user code here
                    bookCanvas.updatePageSettings(
                            getPageMargins().getValue(),
                            getLineSpacing().getValue(),
                            getReloadImages().isSelected(0)
                    );
                }//GEN-BEGIN:|1064-getter|2|1064-postInit
            });//GEN-END:|1064-getter|2|1064-postInit
            // write post-init user code here
        }//GEN-BEGIN:|1064-getter|3|
        return applyPageOptionsTask;
    }
    //</editor-fold>//GEN-END:|1064-getter|3|
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: goToChapterTask ">//GEN-BEGIN:|1065-getter|0|1065-preInit
    /**
     * Returns an initiliazed instance of goToChapterTask component.
     * @return the initialized component instance
     */
    public SimpleCancellableTask getGoToChapterTask() {
        if (goToChapterTask == null) {//GEN-END:|1065-getter|0|1065-preInit
            // write pre-init user code here
            goToChapterTask = new SimpleCancellableTask();//GEN-BEGIN:|1065-getter|1|1065-execute
            goToChapterTask.setExecutable(new org.netbeans.microedition.util.Executable() {
                public void execute() throws Exception {//GEN-END:|1065-getter|1|1065-execute
                    // write task-execution user code here
                    final int chapterIndex = getToc().getSelectedIndex();
                    final int menuIndex =
                            getChapterPositions().getSelectedIndex();

                    switch (menuIndex) {
                        case 0:
                            bookCanvas.goToSavedPosition(chapterIndex);
                            break;

                        case 1:
                            bookCanvas.goToFirstPage(chapterIndex);
                            break;

                        case 2:
                            bookCanvas.goToLastPage(chapterIndex);
                            break;

                        case 3:
                            bookCanvas.goToPosition(
                                    chapterIndex,
                                    getChapterPercent().getValue() / 100F);
                            break;

                        default:
                            bookCanvas.goToFirstPage(
                                    getToc().getSelectedIndex());
                            break;

                    }
                }//GEN-BEGIN:|1065-getter|2|1065-postInit
            });//GEN-END:|1065-getter|2|1065-postInit
            // write post-init user code here
        }//GEN-BEGIN:|1065-getter|3|
        return goToChapterTask;
    }
    //</editor-fold>//GEN-END:|1065-getter|3|
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: applyScreenModeTask ">//GEN-BEGIN:|1066-getter|0|1066-preInit
    /**
     * Returns an initiliazed instance of applyScreenModeTask component.
     * @return the initialized component instance
     */
    public SimpleCancellableTask getApplyScreenModeTask() {
        if (applyScreenModeTask == null) {//GEN-END:|1066-getter|0|1066-preInit
            // write pre-init user code here
            applyScreenModeTask = new SimpleCancellableTask();//GEN-BEGIN:|1066-getter|1|1066-execute
            applyScreenModeTask.setExecutable(new org.netbeans.microedition.util.Executable() {
                public void execute() throws Exception {//GEN-END:|1066-getter|1|1066-execute
                    // write task-execution user code here
                    int orientation = 0;
                    boolean fullscreen = true;

                    final int index = getScreenModes().getSelectedIndex();
                    switch (index) {
                        case 0:
                            fullscreen = false;
                            /*
                             * Pass trough
                             */
                        case 1:
                            orientation = BookCanvas.ORIENTATION_0;
                            break;

                        case 2:
                            orientation = BookCanvas.ORIENTATION_90;
                            break;

                        case 3:
                            orientation = BookCanvas.ORIENTATION_180;
                            break;

                        case 4:
                            orientation = BookCanvas.ORIENTATION_270;
                            break;

                        default:
                            orientation = BookCanvas.ORIENTATION_0;
                    }

                    bookCanvas.setOrientation(orientation, fullscreen);
                }//GEN-BEGIN:|1066-getter|2|1066-postInit
            });//GEN-END:|1066-getter|2|1066-postInit
            // write post-init user code here
        }//GEN-BEGIN:|1066-getter|3|
        return applyScreenModeTask;
    }
    //</editor-fold>//GEN-END:|1066-getter|3|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: goToBookmarkTask ">//GEN-BEGIN:|1067-getter|0|1067-preInit
    /**
     * Returns an initiliazed instance of goToBookmarkTask component.
     * @return the initialized component instance
     */
    public SimpleCancellableTask getGoToBookmarkTask() {
        if (goToBookmarkTask == null) {//GEN-END:|1067-getter|0|1067-preInit
            // write pre-init user code here
            goToBookmarkTask = new SimpleCancellableTask();//GEN-BEGIN:|1067-getter|1|1067-execute
            goToBookmarkTask.setExecutable(new org.netbeans.microedition.util.Executable() {
                public void execute() throws Exception {//GEN-END:|1067-getter|1|1067-execute
                    // write task-execution user code here
                    final Book book = bookCanvas.getCurrentBook();
                    final int pos = getBookmarks().getSelectedIndex();
                    final Bookmark bookmark =
                            book.getBookmarkManager().bookmarkAt(pos);

                    bookCanvas.goToPosition(bookmark);
                }//GEN-BEGIN:|1067-getter|2|1067-postInit
            });//GEN-END:|1067-getter|2|1067-postInit
            // write post-init user code here
        }//GEN-BEGIN:|1067-getter|3|
        return goToBookmarkTask;
    }
    //</editor-fold>//GEN-END:|1067-getter|3|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: method ">//GEN-BEGIN:|1079-entry|0|1080-preAction
    /**
     * Performs an action assigned to the method entry-point.
     */
    public void method() {//GEN-END:|1079-entry|0|1080-preAction
        // write pre-action user code here
        /*
         * bookURL already loaded before calling this task
         */
        try {
            bookCanvas.openBook(bookURL);
        } catch (IOException t) {
            System.out.println(t.toString());
            throw new RuntimeException(t.toString());
        } catch (BookException b) {}
        fillBookmarks();
        switchDisplayable(null, bookCanvas);//GEN-LINE:|1079-entry|1|1080-postAction
        // write post-action user code here
    }//GEN-BEGIN:|1079-entry|2|
    //</editor-fold>//GEN-END:|1079-entry|2|

    /**
     * Returns a display instance.
     * @return the display instance.
     */
    public final Display getDisplay () {
        return Display.getDisplay(this);
    }

    /**
     * Called when MIDlet is started.
     * Checks whether the MIDlet have been already started and initialize/starts or resumes the MIDlet.
     */
    public final void startApp() {
        if (midletPaused) {
            resumeMIDlet();
        } else {
            initialize();
            startMIDlet();
        }
        midletPaused = false;
    }

    /**
     * Called when MIDlet is paused.
     */
    public final void pauseApp() {
        midletPaused = true;
    }

    /**
     * Called to signal the MIDlet to terminate.
     * @param unconditional if true, then the MIDlet has to be unconditionally terminated and all resources has to be released.
     */
    public final void destroyApp(final boolean unconditional)
            throws MIDletStateChangeException{
        //MIDlet destroyed by the AMS
        //call clean-up
        exitMIDlet();
    }

    /**
     * Exits MIDlet.
     */
    public final void exitMIDlet() {
        //Clean-up code. The MIDlet destroys by its own accord
        saveOptionsToRMS();
        closeRMS();
        dictman.closeDictionaries();
        bookCanvas.close();
        switchDisplayable(null, null);
        notifyDestroyed();
    }

    private void openRMSAndLoadData() {
        try {
            rs = RecordStore.openRecordStore("application", true);

            if (rs.getNumRecords() > 0) {
                //deserialize first record
                byte[] data = rs.getRecord(1);
                DataInputStream din =
                        new DataInputStream(new ByteArrayInputStream(data));
                try {
                    //load last book open
                    bookURL     = din.readUTF();
                    dictsFolder = din.readUTF();
                } catch (IOException ioe) {}

            } else {
                /*
                 * No records found, so it must be the first time
                 * the app starts on this device.
                 */
                firstTime = true;
            }

        } catch (RecordStoreException rse) {}
    }

    public final void saveOptionsToRMS() {
        if (bookURL != null && !bookURL.equalsIgnoreCase("")) {
            /*
             * A book has been opened successfully
             */
            try {
                ByteArrayOutputStream boas = new ByteArrayOutputStream();
                DataOutputStream dout = new DataOutputStream(boas);
                try {
                    //save last book open
                    dout.writeUTF(bookURL);
                    dout.writeUTF(dictsFolder);

                    byte[] data = boas.toByteArray();

                    //serialize first record
                    if (rs.getNumRecords() > 0) {
                        rs.setRecord(1, data, 0, data.length);
                    } else {
                        rs.addRecord(data, 0, data.length);
                    }
                } catch (IOException ioe) {}
            } catch (RecordStoreException rse) {}
        }
    }

    private void closeRMS() {
        try {
            rs.closeRecordStore();
        } catch (RecordStoreException rse) {}
    }

    public final void setEntryForLookup(final String s) {
        entryForLookup = s;
    }

    private double round(final double d) {
        double d2 = d * 10;
        long l = (long) d2;
        return ((double) l) / 10;
    }

    public final void calledOutside() {
        calledOutside = true;
//        calledContext = false;
    }

    private void fillDicts() {
        /*
         * Fill the dicts
         */

        dictionaries = null;
        List dicts = getDictionaries();

        Dictionary[] d = dictman.getDictionaries();

        if (d != null) {
            for (int i = 0; i < d.length; i++) {
                dicts.append(d[i].getTitle(), null);
            }
        }
    }

    private void fillBookmarks() {
        bookmarks = null;
        final List l = getBookmarks();

        final BookmarkManager bm =
                bookCanvas.getCurrentBook().getBookmarkManager();

        Bookmark b = bm.getFirst();

        while (b != null) {
            l.append(b.getTextForList(), null);
            b = b.getNext();
        }
    }

    public final void setCurrentBookmarkOptions(
            final int pos, final String s) {
        
        bookmarkPosition = pos;
        bookmarkString = s;
    }

    private boolean bookmarkSelected() {
        return getBookmarks().getSelectedIndex() >= 0
            && getBookmarks().getSelectedIndex() < getBookmarks().size();
    }

    public final void resetToc() {
        toc = null;
    }

    private String getCurrentBookFolder() {
        if (bookURL == null) {
            return null;
        }

        try {
            final String sub = bookURL.substring(8); // i.e. file:///
            final int pos = sub.lastIndexOf('/');
            if (pos >= 0) {
                return sub.substring(0, pos + 1);
            }
        } catch (Exception e) {
        }

        return null;
    }
}