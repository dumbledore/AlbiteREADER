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
import org.albite.util.archive.Archive;
import org.albite.util.units.Unit;
import org.albite.util.units.UnitGroup;
import org.netbeans.microedition.lcdui.SplashScreen;
import org.netbeans.microedition.lcdui.WaitScreen;
import org.netbeans.microedition.lcdui.pda.FileBrowser;
import org.netbeans.microedition.util.SimpleCancellableTask;


/**
 * @author Albus Dumbledore
 */
public class AlbiteMIDlet extends MIDlet implements CommandListener {

    private boolean midletPaused = false;
    private static final String STRING_ERROR_BOOK = "There was a problem opening this book. Probably the file is corrupted.";
    private String bookURL;
    private RecordStore rs;

    private String entryForLookup;
    private boolean numberOK = true;

    private final String version;

    private boolean firstTime = false;

    private boolean openMenu = false;

    private boolean showColors = false;

    public AlbiteMIDlet() {
        String v = getAppProperty("MIDlet-Version");
        if (v == null) {
            version = "unknown version";
        } else {
            version = v;
        }
    }
    
    //<editor-fold defaultstate="collapsed" desc=" Generated Fields ">//GEN-BEGIN:|fields|0|
    private Command okCommand12;
    private Command backCommand8;
    private Command okCommand14;
    private Command backCommand10;
    private Command DISMISS_COMMAND;
    private Command CANCEL_COMMAND;
    private Command backCommand;
    private Command cancelCommand;
    private Command okCommand1;
    private Command okCommand2;
    private Command okCommand;
    private Command cancelCommand1;
    private Command backCommand1;
    private Command cancelCommand2;
    private Command okCommand3;
    private Command backCommand3;
    private Command okCommand5;
    private Command okCommand6;
    private Command backCommand4;
    private Command backCommand2;
    private Command okCommand4;
    private Command okCommand7;
    private Command cancelCommand6;
    private Command backCommand5;
    private Command screenCommand;
    private Command cancelCommand7;
    private Command okCommand8;
    private Command acceptLicenseCommand;
    private Command rejectLicenseCommand;
    private Command backCommand6;
    private Command okCommand9;
    private Command okCommand10;
    private Command dismissLicenseCommand;
    private Command cancelCommand3;
    private Command okCommand11;
    private Command backCommand7;
    private Command okCommand13;
    private Command backCommand9;
    private FileBrowser fileBrowser;
    private List schemes;
    private List colors;
    private Alert errorAlert;
    private WaitScreen loadBook;
    private BookCanvas bookCanvas;
    private List dictsList;
    private List dictEntriesList;
    private Form showDictEntry;
    private StringItem dictrionaryStringItem;
    private StringItem wordStringItem;
    private StringItem definitionStringItem;
    private TextBox enterDictEntryTextBox;
    private List unitGroupList;
    private TextBox enterNumberTextBox;
    private List unitFromList;
    private List unitToList;
    private Alert numberErrorAlert;
    private Form showConversionResultForm;
    private StringItem resultFromQuantity;
    private StringItem resultFromUnit;
    private StringItem resultToUnit;
    private StringItem resultToQuantity;
    private List tocList;
    private Form acceptLicense;
    private StringItem license1;
    private StringItem license4;
    private StringItem license5;
    private StringItem license13;
    private List chapterPositionList;
    private SplashScreen splashScreen;
    private List menu;
    private Form showLicense;
    private ImageItem imageItem;
    private StringItem stringItem12;
    private StringItem stringItem11;
    private StringItem stringItem10;
    private StringItem stringItem9;
    private StringItem stringItem8;
    private StringItem stringItem7;
    private StringItem stringItem6;
    private StringItem stringItem5;
    private StringItem stringItem4;
    private StringItem stringItem3;
    private StringItem stringItem2;
    private StringItem stringItem1;
    private StringItem stringItem;
    private Alert exitBox;
    private List fontSizes;
    private SimpleCancellableTask loadBookTask;
    private Image albiteLogo;
    private Font loadingFont;
    private Font smallPlainFont;
    private Font underlinedFont;
    private Font normalFont;
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
        bookCanvas.setTitle("bookCanvas");
        bookCanvas.setFullScreenMode(true);
        enterNumberTextBox = new TextBox("Enter number", "", 64, TextField.DECIMAL);
        enterNumberTextBox.addCommand(getOkCommand3());
        enterNumberTextBox.addCommand(getCancelCommand2());
        enterNumberTextBox.setCommandListener(this);
        enterNumberTextBox.setInitialInputMode("UCB_BASIC_LATIN");
        unitFromList = new List("Convert From", Choice.IMPLICIT);
        unitFromList.addCommand(getBackCommand3());
        unitFromList.addCommand(getOkCommand5());
        unitFromList.setCommandListener(this);
        unitToList = new List("Convert To", Choice.IMPLICIT);
        unitToList.addCommand(getBackCommand4());
        unitToList.addCommand(getOkCommand6());
        unitToList.setCommandListener(this);
        resultFromQuantity = new StringItem("Initial Quantity:", "");
        resultFromUnit = new StringItem("Initial Units:", "");
        resultToUnit = new StringItem("Resulting Units:", "");
        resultToQuantity = new StringItem("Resulting Quantity:", "");
        showConversionResultForm = new Form("Conversion Result", new Item[] { resultFromQuantity, resultFromUnit, resultToQuantity, resultToUnit });
        showConversionResultForm.addCommand(getCancelCommand6());
        showConversionResultForm.addCommand(getBackCommand5());
        showConversionResultForm.addCommand(getScreenCommand());
        showConversionResultForm.setCommandListener(this);
        exitBox = new Alert("Quit", "Do you want to quit?", null, null);
        exitBox.addCommand(getOkCommand11());
        exitBox.addCommand(getBackCommand7());
        exitBox.setCommandListener(this);
        exitBox.setTimeout(Alert.FOREVER);//GEN-END:|0-initialize|1|0-postInitialize
        // write post-initialize user code here

        /* RMS */
        openRMSAndLoadData();

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
        if (displayable == acceptLicense) {//GEN-BEGIN:|7-commandAction|1|375-preAction
            if (command == acceptLicenseCommand) {//GEN-END:|7-commandAction|1|375-preAction
                // write pre-action user code here
                lastBookAvailable();//GEN-LINE:|7-commandAction|2|375-postAction
                // write post-action user code here
            } else if (command == rejectLicenseCommand) {//GEN-LINE:|7-commandAction|3|377-preAction
                // write pre-action user code here
                exitMIDlet();//GEN-LINE:|7-commandAction|4|377-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|5|355-preAction
        } else if (displayable == chapterPositionList) {
            if (command == List.SELECT_COMMAND) {//GEN-END:|7-commandAction|5|355-preAction
                // write pre-action user code here
                chapterPositionListAction();//GEN-LINE:|7-commandAction|6|355-postAction
                // write post-action user code here
            } else if (command == backCommand6) {//GEN-LINE:|7-commandAction|7|358-preAction
                // write pre-action user code here
                switchDisplayable(null, getTocList());//GEN-LINE:|7-commandAction|8|358-postAction
                // write post-action user code here
            } else if (command == okCommand9) {//GEN-LINE:|7-commandAction|9|360-preAction
                // write pre-action user code here
                goToChapter();//GEN-LINE:|7-commandAction|10|360-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|11|531-preAction
        } else if (displayable == colors) {
            if (command == List.SELECT_COMMAND) {//GEN-END:|7-commandAction|11|531-preAction
                // write pre-action user code here
                colorsAction();//GEN-LINE:|7-commandAction|12|531-postAction
                // write post-action user code here
            } else if (command == backCommand10) {//GEN-LINE:|7-commandAction|13|536-preAction
                // write pre-action user code here
                switchDisplayable(null, getSchemes());//GEN-LINE:|7-commandAction|14|536-postAction
                // write post-action user code here
            } else if (command == okCommand14) {//GEN-LINE:|7-commandAction|15|534-preAction
                // write pre-action user code here
                bookCanvas.setScheme(
                        (byte) schemes.getSelectedIndex(),
                        ColorScheme.HUE_VALUES[colors.getSelectedIndex()]
                        );
                switchDisplayable(null, bookCanvas);//GEN-LINE:|7-commandAction|16|534-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|17|189-preAction
        } else if (displayable == dictEntriesList) {
            if (command == List.SELECT_COMMAND) {//GEN-END:|7-commandAction|17|189-preAction
                // write pre-action user code here
                dictEntriesListAction();//GEN-LINE:|7-commandAction|18|189-postAction
                // write post-action user code here
            } else if (command == backCommand) {//GEN-LINE:|7-commandAction|19|206-preAction
                // write pre-action user code here
//GEN-LINE:|7-commandAction|20|206-postAction
                // write post-action user code here
            } else if (command == cancelCommand) {//GEN-LINE:|7-commandAction|21|208-preAction
                // write pre-action user code here
                switchDisplayable(null, bookCanvas);//GEN-LINE:|7-commandAction|22|208-postAction
                // write post-action user code here
            } else if (command == okCommand) {//GEN-LINE:|7-commandAction|23|210-preAction
                // write pre-action user code here
                switchDisplayable(null, getShowDictEntry());//GEN-LINE:|7-commandAction|24|210-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|25|186-preAction
        } else if (displayable == dictsList) {
            if (command == List.SELECT_COMMAND) {//GEN-END:|7-commandAction|25|186-preAction
                // write pre-action user code here
                dictsListAction();//GEN-LINE:|7-commandAction|26|186-postAction
                // write post-action user code here
            } else if (command == okCommand2) {//GEN-LINE:|7-commandAction|27|220-preAction
                // write pre-action user code here
                switchDisplayable(null, getDictEntriesList());//GEN-LINE:|7-commandAction|28|220-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|29|218-preAction
        } else if (displayable == enterDictEntryTextBox) {
            if (command == okCommand1) {//GEN-END:|7-commandAction|29|218-preAction
                // write pre-action user code here
                moreThanOneDictFound();//GEN-LINE:|7-commandAction|30|218-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|31|249-preAction
        } else if (displayable == enterNumberTextBox) {
            if (command == cancelCommand2) {//GEN-END:|7-commandAction|31|249-preAction
                // write pre-action user code here
                returnToMenu();//GEN-LINE:|7-commandAction|32|249-postAction
                // write post-action user code here
            } else if (command == okCommand3) {//GEN-LINE:|7-commandAction|33|247-preAction
                // write pre-action user code here
                isNumberOKCheck();//GEN-LINE:|7-commandAction|34|247-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|35|103-preAction
        } else if (displayable == errorAlert) {
            if (command == DISMISS_COMMAND) {//GEN-END:|7-commandAction|35|103-preAction
                // write pre-action user code here
                switchDisplayable(null, getFileBrowser());//GEN-LINE:|7-commandAction|36|103-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|37|551-preAction
        } else if (displayable == exitBox) {
            if (command == backCommand7) {//GEN-END:|7-commandAction|37|551-preAction
                // write pre-action user code here
                returnToMenu();//GEN-LINE:|7-commandAction|38|551-postAction
                // write post-action user code here
            } else if (command == okCommand11) {//GEN-LINE:|7-commandAction|39|549-preAction
                // write pre-action user code here
                exitMIDlet();//GEN-LINE:|7-commandAction|40|549-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|41|119-preAction
        } else if (displayable == fileBrowser) {
            if (command == CANCEL_COMMAND) {//GEN-END:|7-commandAction|41|119-preAction
                // write pre-action user code here
                displayBookCanvas();//GEN-LINE:|7-commandAction|42|119-postAction
                // write post-action user code here
            } else if (command == FileBrowser.SELECT_FILE_COMMAND) {//GEN-LINE:|7-commandAction|43|34-preAction
                // write pre-action user code here
                bookURL = getFileBrowser().getSelectedFileURL();
                showLoadingScreen();//GEN-LINE:|7-commandAction|44|34-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|45|559-preAction
        } else if (displayable == fontSizes) {
            if (command == List.SELECT_COMMAND) {//GEN-END:|7-commandAction|45|559-preAction
                // write pre-action user code here
                fontSizesAction();//GEN-LINE:|7-commandAction|46|559-postAction
                // write post-action user code here
            } else if (command == backCommand9) {//GEN-LINE:|7-commandAction|47|564-preAction
                // write pre-action user code here
                returnToMenu();//GEN-LINE:|7-commandAction|48|564-postAction
                // write post-action user code here
            } else if (command == okCommand13) {//GEN-LINE:|7-commandAction|49|562-preAction
                // write pre-action user code here
                bookCanvas.setFontSize((byte) fontSizes.getSelectedIndex());
                switchDisplayable(null, bookCanvas);//GEN-LINE:|7-commandAction|50|562-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|51|159-preAction
        } else if (displayable == loadBook) {
            if (command == WaitScreen.FAILURE_COMMAND) {//GEN-END:|7-commandAction|51|159-preAction
                // write pre-action user code here
                getErrorAlert().setString(STRING_ERROR_BOOK);
                switchDisplayable(null, getErrorAlert());//GEN-LINE:|7-commandAction|52|159-postAction
                // write post-action user code here
            } else if (command == WaitScreen.SUCCESS_COMMAND) {//GEN-LINE:|7-commandAction|53|158-preAction
                // write pre-action user code here
                switchDisplayable(null, bookCanvas);//GEN-LINE:|7-commandAction|54|158-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|55|430-preAction
        } else if (displayable == menu) {
            if (command == List.SELECT_COMMAND) {//GEN-END:|7-commandAction|55|430-preAction
                // write pre-action user code here
                menuAction();//GEN-LINE:|7-commandAction|56|430-postAction
                // write post-action user code here
            } else if (command == cancelCommand3) {//GEN-LINE:|7-commandAction|57|434-preAction
                // write pre-action user code here
                switchDisplayable(null, bookCanvas);//GEN-LINE:|7-commandAction|58|434-postAction
                // write post-action user code here
            } else if (command == okCommand10) {//GEN-LINE:|7-commandAction|59|441-preAction
                // write pre-action user code here
                processMenu();//GEN-LINE:|7-commandAction|60|441-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|61|319-preAction
        } else if (displayable == numberErrorAlert) {
            if (command == okCommand7) {//GEN-END:|7-commandAction|61|319-preAction
                // write pre-action user code here
                switchDisplayable(null, enterNumberTextBox);//GEN-LINE:|7-commandAction|62|319-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|63|513-preAction
        } else if (displayable == schemes) {
            if (command == List.SELECT_COMMAND) {//GEN-END:|7-commandAction|63|513-preAction
                // write pre-action user code here
                schemesAction();//GEN-LINE:|7-commandAction|64|513-postAction
                // write post-action user code here
            } else if (command == backCommand8) {//GEN-LINE:|7-commandAction|65|520-preAction
                // write pre-action user code here
                returnToMenu();//GEN-LINE:|7-commandAction|66|520-postAction
                // write post-action user code here
            } else if (command == okCommand12) {//GEN-LINE:|7-commandAction|67|517-preAction
                // write pre-action user code here
                showColors = (schemes.getSelectedIndex() != 0);
                showColorPicker();//GEN-LINE:|7-commandAction|68|517-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|69|295-preAction
        } else if (displayable == showConversionResultForm) {
            if (command == backCommand5) {//GEN-END:|7-commandAction|69|295-preAction
                // write pre-action user code here
                switchDisplayable(null, unitToList);//GEN-LINE:|7-commandAction|70|295-postAction
                // write post-action user code here
            } else if (command == cancelCommand6) {//GEN-LINE:|7-commandAction|71|293-preAction
                // write pre-action user code here
                switchDisplayable(null, bookCanvas);//GEN-LINE:|7-commandAction|72|293-postAction
                // write post-action user code here
            } else if (command == screenCommand) {//GEN-LINE:|7-commandAction|73|297-preAction
                // write pre-action user code here
                switchDisplayable(null, enterNumberTextBox);//GEN-LINE:|7-commandAction|74|297-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|75|233-preAction
        } else if (displayable == showDictEntry) {
            if (command == backCommand1) {//GEN-END:|7-commandAction|75|233-preAction
                // write pre-action user code here
//GEN-LINE:|7-commandAction|76|233-postAction
                // write post-action user code here
            } else if (command == cancelCommand1) {//GEN-LINE:|7-commandAction|77|235-preAction
                // write pre-action user code here
//GEN-LINE:|7-commandAction|78|235-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|79|445-preAction
        } else if (displayable == showLicense) {
            if (command == dismissLicenseCommand) {//GEN-END:|7-commandAction|79|445-preAction
                // write pre-action user code here
                returnToMenu();//GEN-LINE:|7-commandAction|80|445-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|81|368-preAction
        } else if (displayable == splashScreen) {
            if (command == SplashScreen.DISMISS_COMMAND) {//GEN-END:|7-commandAction|81|368-preAction
                // write pre-action user code here
                runsForTheFirstTime();//GEN-LINE:|7-commandAction|82|368-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|83|326-preAction
        } else if (displayable == tocList) {
            if (command == List.SELECT_COMMAND) {//GEN-END:|7-commandAction|83|326-preAction
                // write pre-action user code here
                tocListAction();//GEN-LINE:|7-commandAction|84|326-postAction
                // write post-action user code here
            } else if (command == cancelCommand7) {//GEN-LINE:|7-commandAction|85|338-preAction
                // write pre-action user code here
                returnToMenu();//GEN-LINE:|7-commandAction|86|338-postAction
                // write post-action user code here
            } else if (command == okCommand8) {//GEN-LINE:|7-commandAction|87|336-preAction
                // write pre-action user code here
                switchDisplayable(null, getChapterPositionList());//GEN-LINE:|7-commandAction|88|336-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|89|267-preAction
        } else if (displayable == unitFromList) {
            if (command == List.SELECT_COMMAND) {//GEN-END:|7-commandAction|89|267-preAction
                // write pre-action user code here
                unitFromListAction();//GEN-LINE:|7-commandAction|90|267-postAction
                // write post-action user code here
            } else if (command == backCommand3) {//GEN-LINE:|7-commandAction|91|275-preAction
                // write pre-action user code here
                switchDisplayable(null, getUnitGroupList());//GEN-LINE:|7-commandAction|92|275-postAction
                // write post-action user code here
            } else if (command == okCommand5) {//GEN-LINE:|7-commandAction|93|277-preAction
                // write pre-action user code here
                switchDisplayable(null, unitToList);//GEN-LINE:|7-commandAction|94|277-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|95|256-preAction
        } else if (displayable == unitGroupList) {
            if (command == List.SELECT_COMMAND) {//GEN-END:|7-commandAction|95|256-preAction
                // write pre-action user code here
                unitGroupListAction();//GEN-LINE:|7-commandAction|96|256-postAction
                // write post-action user code here
            } else if (command == backCommand2) {//GEN-LINE:|7-commandAction|97|261-preAction
                // write pre-action user code here
                switchDisplayable(null, enterNumberTextBox);//GEN-LINE:|7-commandAction|98|261-postAction
                // write post-action user code here
            } else if (command == okCommand4) {//GEN-LINE:|7-commandAction|99|265-preAction
                // write pre-action user code here
                loadUnitsToLists();
                switchDisplayable(null, unitFromList);//GEN-LINE:|7-commandAction|100|265-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|101|270-preAction
        } else if (displayable == unitToList) {
            if (command == List.SELECT_COMMAND) {//GEN-END:|7-commandAction|101|270-preAction
                // write pre-action user code here
                unitToListAction();//GEN-LINE:|7-commandAction|102|270-postAction
                // write post-action user code here
            } else if (command == backCommand4) {//GEN-LINE:|7-commandAction|103|285-preAction
                // write pre-action user code here
                switchDisplayable(null, unitFromList);//GEN-LINE:|7-commandAction|104|285-postAction
                // write post-action user code here
            } else if (command == okCommand6) {//GEN-LINE:|7-commandAction|105|287-preAction
                // write pre-action user code here
                convertUnits();
                switchDisplayable(null, showConversionResultForm);//GEN-LINE:|7-commandAction|106|287-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|107|7-postCommandAction
        }//GEN-END:|7-commandAction|107|7-postCommandAction
        // write post-action user code here
    }//GEN-BEGIN:|7-commandAction|108|
    //</editor-fold>//GEN-END:|7-commandAction|108|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: fileBrowser ">//GEN-BEGIN:|32-getter|0|32-preInit
    /**
     * Returns an initiliazed instance of fileBrowser component.
     * @return the initialized component instance
     */
    public FileBrowser getFileBrowser() {
        if (fileBrowser == null) {//GEN-END:|32-getter|0|32-preInit
            // write pre-init user code here
            fileBrowser = new FileBrowser(getDisplay());//GEN-BEGIN:|32-getter|1|32-postInit
            fileBrowser.setTitle("Open book from file...");
            fileBrowser.setCommandListener(this);
            fileBrowser.setFilter(Archive.FILE_EXTENSION);
            fileBrowser.addCommand(FileBrowser.SELECT_FILE_COMMAND);
            fileBrowser.addCommand(getCANCEL_COMMAND());//GEN-END:|32-getter|1|32-postInit
            // write post-init user code here
        }//GEN-BEGIN:|32-getter|2|
        return fileBrowser;
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
            DISMISS_COMMAND = new Command("Alright", Command.OK, 0);//GEN-LINE:|102-getter|1|102-postInit
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

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: errorAlert ">//GEN-BEGIN:|101-getter|0|101-preInit
    /**
     * Returns an initiliazed instance of errorAlert component.
     * @return the initialized component instance
     */
    public Alert getErrorAlert() {
        if (errorAlert == null) {//GEN-END:|101-getter|0|101-preInit
            // write pre-init user code here
            errorAlert = new Alert("Oops, something went wrong...", "", null, AlertType.ERROR);//GEN-BEGIN:|101-getter|1|101-postInit
            errorAlert.addCommand(getDISMISS_COMMAND());
            errorAlert.setCommandListener(this);
            errorAlert.setTimeout(Alert.FOREVER);//GEN-END:|101-getter|1|101-postInit
            // write post-init user code here
        }//GEN-BEGIN:|101-getter|2|
        return errorAlert;
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
            switchDisplayable(null, getFileBrowser());//GEN-LINE:|134-if|4|136-postAction
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
            loadBook.setTitle("Opening book");
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
                    // write task-execution user code here

                    try {
                        /* bookURL already loaded before calling this task */
                        bookCanvas.openBook(bookURL);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw e;
                    }
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
                e.printStackTrace();
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

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: showDictEntry ">//GEN-BEGIN:|181-getter|0|181-preInit
    /**
     * Returns an initiliazed instance of showDictEntry component.
     * @return the initialized component instance
     */
    public Form getShowDictEntry() {
        if (showDictEntry == null) {//GEN-END:|181-getter|0|181-preInit
            // write pre-init user code here
            showDictEntry = new Form("Word Entry", new Item[] { getWordStringItem(), getDefinitionStringItem(), getDictrionaryStringItem() });//GEN-BEGIN:|181-getter|1|181-postInit
            showDictEntry.addCommand(getBackCommand1());
            showDictEntry.addCommand(getCancelCommand1());
            showDictEntry.setCommandListener(this);//GEN-END:|181-getter|1|181-postInit
            // write post-init user code here
        }//GEN-BEGIN:|181-getter|2|
        return showDictEntry;
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

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: dictsList ">//GEN-BEGIN:|185-getter|0|185-preInit
    /**
     * Returns an initiliazed instance of dictsList component.
     * @return the initialized component instance
     */
    public List getDictsList() {
        if (dictsList == null) {//GEN-END:|185-getter|0|185-preInit
            // write pre-init user code here
            dictsList = new List("list", Choice.IMPLICIT);//GEN-BEGIN:|185-getter|1|185-postInit
            dictsList.addCommand(getOkCommand2());
            dictsList.setCommandListener(this);//GEN-END:|185-getter|1|185-postInit
            // write post-init user code here
        }//GEN-BEGIN:|185-getter|2|
        return dictsList;
    }
    //</editor-fold>//GEN-END:|185-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: dictsListAction ">//GEN-BEGIN:|185-action|0|185-preAction
    /**
     * Performs an action assigned to the selected list element in the dictsList component.
     */
    public void dictsListAction() {//GEN-END:|185-action|0|185-preAction
        // enter pre-action user code here
        String __selectedString = getDictsList().getString(getDictsList().getSelectedIndex());//GEN-LINE:|185-action|1|185-postAction
        // enter post-action user code here
    }//GEN-BEGIN:|185-action|2|
    //</editor-fold>//GEN-END:|185-action|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: dictEntriesList ">//GEN-BEGIN:|188-getter|0|188-preInit
    /**
     * Returns an initiliazed instance of dictEntriesList component.
     * @return the initialized component instance
     */
    public List getDictEntriesList() {
        if (dictEntriesList == null) {//GEN-END:|188-getter|0|188-preInit
            // write pre-init user code here
            dictEntriesList = new List("list1", Choice.IMPLICIT);//GEN-BEGIN:|188-getter|1|188-postInit
            dictEntriesList.addCommand(getBackCommand());
            dictEntriesList.addCommand(getCancelCommand());
            dictEntriesList.addCommand(getOkCommand());
            dictEntriesList.setCommandListener(this);//GEN-END:|188-getter|1|188-postInit
            // write post-init user code here
        }//GEN-BEGIN:|188-getter|2|
        return dictEntriesList;
    }
    //</editor-fold>//GEN-END:|188-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: dictEntriesListAction ">//GEN-BEGIN:|188-action|0|188-preAction
    /**
     * Performs an action assigned to the selected list element in the dictEntriesList component.
     */
    public void dictEntriesListAction() {//GEN-END:|188-action|0|188-preAction
        // enter pre-action user code here
        String __selectedString = getDictEntriesList().getString(getDictEntriesList().getSelectedIndex());//GEN-LINE:|188-action|1|188-postAction
        // enter post-action user code here
    }//GEN-BEGIN:|188-action|2|
    //</editor-fold>//GEN-END:|188-action|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: moreThanOneDictFound ">//GEN-BEGIN:|193-if|0|193-preIf
    /**
     * Performs an action assigned to the moreThanOneDictFound if-point.
     */
    public void moreThanOneDictFound() {//GEN-END:|193-if|0|193-preIf
        // enter pre-if user code here
        if (true) {//GEN-LINE:|193-if|1|194-preAction
            // write pre-action user code here
            switchDisplayable(null, getDictsList());//GEN-LINE:|193-if|2|194-postAction
            // write post-action user code here
        } else {//GEN-LINE:|193-if|3|195-preAction
            // write pre-action user code here
            switchDisplayable(null, getDictEntriesList());//GEN-LINE:|193-if|4|195-postAction
            // write post-action user code here
        }//GEN-LINE:|193-if|5|193-postIf
        // enter post-if user code here
    }//GEN-BEGIN:|193-if|6|
    //</editor-fold>//GEN-END:|193-if|6|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: enterDictEntry ">//GEN-BEGIN:|223-entry|0|224-preAction
    /**
     * Performs an action assigned to the enterDictEntry entry-point.
     */
    public void enterDictEntry() {//GEN-END:|223-entry|0|224-preAction
        // write pre-action user code here
        switchDisplayable(null, getEnterDictEntryTextBox());//GEN-LINE:|223-entry|1|224-postAction
        // write post-action user code here
    }//GEN-BEGIN:|223-entry|2|
    //</editor-fold>//GEN-END:|223-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: backCommand ">//GEN-BEGIN:|205-getter|0|205-preInit
    /**
     * Returns an initiliazed instance of backCommand component.
     * @return the initialized component instance
     */
    public Command getBackCommand() {
        if (backCommand == null) {//GEN-END:|205-getter|0|205-preInit
            // write pre-init user code here
            backCommand = new Command("Back", Command.BACK, 0);//GEN-LINE:|205-getter|1|205-postInit
            // write post-init user code here
        }//GEN-BEGIN:|205-getter|2|
        return backCommand;
    }
    //</editor-fold>//GEN-END:|205-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: cancelCommand ">//GEN-BEGIN:|207-getter|0|207-preInit
    /**
     * Returns an initiliazed instance of cancelCommand component.
     * @return the initialized component instance
     */
    public Command getCancelCommand() {
        if (cancelCommand == null) {//GEN-END:|207-getter|0|207-preInit
            // write pre-init user code here
            cancelCommand = new Command("Cancel", Command.CANCEL, 0);//GEN-LINE:|207-getter|1|207-postInit
            // write post-init user code here
        }//GEN-BEGIN:|207-getter|2|
        return cancelCommand;
    }
    //</editor-fold>//GEN-END:|207-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: okCommand ">//GEN-BEGIN:|209-getter|0|209-preInit
    /**
     * Returns an initiliazed instance of okCommand component.
     * @return the initialized component instance
     */
    public Command getOkCommand() {
        if (okCommand == null) {//GEN-END:|209-getter|0|209-preInit
            // write pre-init user code here
            okCommand = new Command("Ok", Command.OK, 0);//GEN-LINE:|209-getter|1|209-postInit
            // write post-init user code here
        }//GEN-BEGIN:|209-getter|2|
        return okCommand;
    }
    //</editor-fold>//GEN-END:|209-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: okCommand1 ">//GEN-BEGIN:|217-getter|0|217-preInit
    /**
     * Returns an initiliazed instance of okCommand1 component.
     * @return the initialized component instance
     */
    public Command getOkCommand1() {
        if (okCommand1 == null) {//GEN-END:|217-getter|0|217-preInit
            // write pre-init user code here
            okCommand1 = new Command("Ok", Command.OK, 0);//GEN-LINE:|217-getter|1|217-postInit
            // write post-init user code here
        }//GEN-BEGIN:|217-getter|2|
        return okCommand1;
    }
    //</editor-fold>//GEN-END:|217-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: okCommand2 ">//GEN-BEGIN:|219-getter|0|219-preInit
    /**
     * Returns an initiliazed instance of okCommand2 component.
     * @return the initialized component instance
     */
    public Command getOkCommand2() {
        if (okCommand2 == null) {//GEN-END:|219-getter|0|219-preInit
            // write pre-init user code here
            okCommand2 = new Command("Ok", Command.OK, 0);//GEN-LINE:|219-getter|1|219-postInit
            // write post-init user code here
        }//GEN-BEGIN:|219-getter|2|
        return okCommand2;
    }
    //</editor-fold>//GEN-END:|219-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: enterDictEntryTextBox ">//GEN-BEGIN:|216-getter|0|216-preInit
    /**
     * Returns an initiliazed instance of enterDictEntryTextBox component.
     * @return the initialized component instance
     */
    public TextBox getEnterDictEntryTextBox() {
        if (enterDictEntryTextBox == null) {//GEN-END:|216-getter|0|216-preInit
            // write pre-init user code here
            enterDictEntryTextBox = new TextBox("textBox", null, 100, TextField.ANY);//GEN-BEGIN:|216-getter|1|216-postInit
            enterDictEntryTextBox.addCommand(getOkCommand1());
            enterDictEntryTextBox.setCommandListener(this);//GEN-END:|216-getter|1|216-postInit
            // write post-init user code here
        }//GEN-BEGIN:|216-getter|2|
        return enterDictEntryTextBox;
    }
    //</editor-fold>//GEN-END:|216-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: backCommand1 ">//GEN-BEGIN:|232-getter|0|232-preInit
    /**
     * Returns an initiliazed instance of backCommand1 component.
     * @return the initialized component instance
     */
    public Command getBackCommand1() {
        if (backCommand1 == null) {//GEN-END:|232-getter|0|232-preInit
            // write pre-init user code here
            backCommand1 = new Command("Back", Command.BACK, 0);//GEN-LINE:|232-getter|1|232-postInit
            // write post-init user code here
        }//GEN-BEGIN:|232-getter|2|
        return backCommand1;
    }
    //</editor-fold>//GEN-END:|232-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: cancelCommand1 ">//GEN-BEGIN:|234-getter|0|234-preInit
    /**
     * Returns an initiliazed instance of cancelCommand1 component.
     * @return the initialized component instance
     */
    public Command getCancelCommand1() {
        if (cancelCommand1 == null) {//GEN-END:|234-getter|0|234-preInit
            // write pre-init user code here
            cancelCommand1 = new Command("Cancel", Command.CANCEL, 0);//GEN-LINE:|234-getter|1|234-postInit
            // write post-init user code here
        }//GEN-BEGIN:|234-getter|2|
        return cancelCommand1;
    }
    //</editor-fold>//GEN-END:|234-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: openLibrary ">//GEN-BEGIN:|242-entry|0|243-preAction
    /**
     * Performs an action assigned to the openLibrary entry-point.
     */
    public void openLibrary() {//GEN-END:|242-entry|0|243-preAction
        // write pre-action user code here
        switchDisplayable(null, getFileBrowser());//GEN-LINE:|242-entry|1|243-postAction
        // write post-action user code here
    }//GEN-BEGIN:|242-entry|2|
    //</editor-fold>//GEN-END:|242-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: okCommand3 ">//GEN-BEGIN:|246-getter|0|246-preInit
    /**
     * Returns an initiliazed instance of okCommand3 component.
     * @return the initialized component instance
     */
    public Command getOkCommand3() {
        if (okCommand3 == null) {//GEN-END:|246-getter|0|246-preInit
            // write pre-init user code here
            okCommand3 = new Command("Next", Command.OK, 0);//GEN-LINE:|246-getter|1|246-postInit
            // write post-init user code here
        }//GEN-BEGIN:|246-getter|2|
        return okCommand3;
    }
    //</editor-fold>//GEN-END:|246-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: cancelCommand2 ">//GEN-BEGIN:|248-getter|0|248-preInit
    /**
     * Returns an initiliazed instance of cancelCommand2 component.
     * @return the initialized component instance
     */
    public Command getCancelCommand2() {
        if (cancelCommand2 == null) {//GEN-END:|248-getter|0|248-preInit
            // write pre-init user code here
            cancelCommand2 = new Command("Back", "<null>", Command.CANCEL, 0);//GEN-LINE:|248-getter|1|248-postInit
            // write post-init user code here
        }//GEN-BEGIN:|248-getter|2|
        return cancelCommand2;
    }
    //</editor-fold>//GEN-END:|248-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: enterNumber ">//GEN-BEGIN:|252-entry|0|253-preAction
    /**
     * Performs an action assigned to the enterNumber entry-point.
     */
    public void enterNumber() {//GEN-END:|252-entry|0|253-preAction
        // write pre-action user code here
        if (entryForLookup == null) {
            entryForLookup = "";
        }

        enterNumberTextBox.setString(entryForLookup);
        switchDisplayable(null, enterNumberTextBox);//GEN-LINE:|252-entry|1|253-postAction
        // write post-action user code here
    }//GEN-BEGIN:|252-entry|2|
    //</editor-fold>//GEN-END:|252-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: backCommand2 ">//GEN-BEGIN:|260-getter|0|260-preInit
    /**
     * Returns an initiliazed instance of backCommand2 component.
     * @return the initialized component instance
     */
    public Command getBackCommand2() {
        if (backCommand2 == null) {//GEN-END:|260-getter|0|260-preInit
            // write pre-init user code here
            backCommand2 = new Command("Back", Command.BACK, 0);//GEN-LINE:|260-getter|1|260-postInit
            // write post-init user code here
        }//GEN-BEGIN:|260-getter|2|
        return backCommand2;
    }
    //</editor-fold>//GEN-END:|260-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: okCommand4 ">//GEN-BEGIN:|264-getter|0|264-preInit
    /**
     * Returns an initiliazed instance of okCommand4 component.
     * @return the initialized component instance
     */
    public Command getOkCommand4() {
        if (okCommand4 == null) {//GEN-END:|264-getter|0|264-preInit
            // write pre-init user code here
            okCommand4 = new Command("Next", Command.OK, 0);//GEN-LINE:|264-getter|1|264-postInit
            // write post-init user code here
        }//GEN-BEGIN:|264-getter|2|
        return okCommand4;
    }
    //</editor-fold>//GEN-END:|264-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: unitGroupListAction ">//GEN-BEGIN:|255-action|0|255-preAction
    /**
     * Performs an action assigned to the selected list element in the unitGroupList component.
     */
    public void unitGroupListAction() {//GEN-END:|255-action|0|255-preAction
        // enter pre-action user code here
        String __selectedString = getUnitGroupList().getString(getUnitGroupList().getSelectedIndex());//GEN-LINE:|255-action|1|255-postAction
        // enter post-action user code here
    }//GEN-BEGIN:|255-action|2|
    //</editor-fold>//GEN-END:|255-action|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: backCommand3 ">//GEN-BEGIN:|274-getter|0|274-preInit
    /**
     * Returns an initiliazed instance of backCommand3 component.
     * @return the initialized component instance
     */
    public Command getBackCommand3() {
        if (backCommand3 == null) {//GEN-END:|274-getter|0|274-preInit
            // write pre-init user code here
            backCommand3 = new Command("Back", Command.BACK, 0);//GEN-LINE:|274-getter|1|274-postInit
            // write post-init user code here
        }//GEN-BEGIN:|274-getter|2|
        return backCommand3;
    }
    //</editor-fold>//GEN-END:|274-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: okCommand5 ">//GEN-BEGIN:|276-getter|0|276-preInit
    /**
     * Returns an initiliazed instance of okCommand5 component.
     * @return the initialized component instance
     */
    public Command getOkCommand5() {
        if (okCommand5 == null) {//GEN-END:|276-getter|0|276-preInit
            // write pre-init user code here
            okCommand5 = new Command("Next", Command.OK, 0);//GEN-LINE:|276-getter|1|276-postInit
            // write post-init user code here
        }//GEN-BEGIN:|276-getter|2|
        return okCommand5;
    }
    //</editor-fold>//GEN-END:|276-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: backCommand4 ">//GEN-BEGIN:|284-getter|0|284-preInit
    /**
     * Returns an initiliazed instance of backCommand4 component.
     * @return the initialized component instance
     */
    public Command getBackCommand4() {
        if (backCommand4 == null) {//GEN-END:|284-getter|0|284-preInit
            // write pre-init user code here
            backCommand4 = new Command("Back", Command.BACK, 0);//GEN-LINE:|284-getter|1|284-postInit
            // write post-init user code here
        }//GEN-BEGIN:|284-getter|2|
        return backCommand4;
    }
    //</editor-fold>//GEN-END:|284-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: okCommand6 ">//GEN-BEGIN:|286-getter|0|286-preInit
    /**
     * Returns an initiliazed instance of okCommand6 component.
     * @return the initialized component instance
     */
    public Command getOkCommand6() {
        if (okCommand6 == null) {//GEN-END:|286-getter|0|286-preInit
            // write pre-init user code here
            okCommand6 = new Command("Next", Command.OK, 0);//GEN-LINE:|286-getter|1|286-postInit
            // write post-init user code here
        }//GEN-BEGIN:|286-getter|2|
        return okCommand6;
    }
    //</editor-fold>//GEN-END:|286-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: cancelCommand6 ">//GEN-BEGIN:|292-getter|0|292-preInit
    /**
     * Returns an initiliazed instance of cancelCommand6 component.
     * @return the initialized component instance
     */
    public Command getCancelCommand6() {
        if (cancelCommand6 == null) {//GEN-END:|292-getter|0|292-preInit
            // write pre-init user code here
            cancelCommand6 = new Command("Close", Command.CANCEL, 0);//GEN-LINE:|292-getter|1|292-postInit
            // write post-init user code here
        }//GEN-BEGIN:|292-getter|2|
        return cancelCommand6;
    }
    //</editor-fold>//GEN-END:|292-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: backCommand5 ">//GEN-BEGIN:|294-getter|0|294-preInit
    /**
     * Returns an initiliazed instance of backCommand5 component.
     * @return the initialized component instance
     */
    public Command getBackCommand5() {
        if (backCommand5 == null) {//GEN-END:|294-getter|0|294-preInit
            // write pre-init user code here
            backCommand5 = new Command("Back", Command.BACK, 0);//GEN-LINE:|294-getter|1|294-postInit
            // write post-init user code here
        }//GEN-BEGIN:|294-getter|2|
        return backCommand5;
    }
    //</editor-fold>//GEN-END:|294-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: screenCommand ">//GEN-BEGIN:|296-getter|0|296-preInit
    /**
     * Returns an initiliazed instance of screenCommand component.
     * @return the initialized component instance
     */
    public Command getScreenCommand() {
        if (screenCommand == null) {//GEN-END:|296-getter|0|296-preInit
            // write pre-init user code here
            screenCommand = new Command("Restart", "Convert another number", Command.SCREEN, 0);//GEN-LINE:|296-getter|1|296-postInit
            // write post-init user code here
        }//GEN-BEGIN:|296-getter|2|
        return screenCommand;
    }
    //</editor-fold>//GEN-END:|296-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: unitFromListAction ">//GEN-BEGIN:|266-action|0|266-preAction
    /**
     * Performs an action assigned to the selected list element in the unitFromList component.
     */
    public void unitFromListAction() {//GEN-END:|266-action|0|266-preAction
        // enter pre-action user code here
        String __selectedString = unitFromList.getString(unitFromList.getSelectedIndex());//GEN-LINE:|266-action|1|266-postAction
        // enter post-action user code here
    }//GEN-BEGIN:|266-action|2|
    //</editor-fold>//GEN-END:|266-action|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: unitToListAction ">//GEN-BEGIN:|269-action|0|269-preAction
    /**
     * Performs an action assigned to the selected list element in the unitToList component.
     */
    public void unitToListAction() {//GEN-END:|269-action|0|269-preAction
        // enter pre-action user code here
        String __selectedString = unitToList.getString(unitToList.getSelectedIndex());//GEN-LINE:|269-action|1|269-postAction
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
            Double.parseDouble(enterNumberTextBox.getString());
        } catch (NumberFormatException e) {
            numberOK = false;
        }
        if (numberOK) {//GEN-LINE:|312-if|1|313-preAction
            // write pre-action user code here
            switchDisplayable(null, getUnitGroupList());//GEN-LINE:|312-if|2|313-postAction
            // write post-action user code here
        } else {//GEN-LINE:|312-if|3|314-preAction
            // write pre-action user code here
            switchDisplayable(null, getNumberErrorAlert());//GEN-LINE:|312-if|4|314-postAction
            // write post-action user code here
        }//GEN-LINE:|312-if|5|312-postIf
        // enter post-if user code here
    }//GEN-BEGIN:|312-if|6|
    //</editor-fold>//GEN-END:|312-if|6|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: okCommand7 ">//GEN-BEGIN:|318-getter|0|318-preInit
    /**
     * Returns an initiliazed instance of okCommand7 component.
     * @return the initialized component instance
     */
    public Command getOkCommand7() {
        if (okCommand7 == null) {//GEN-END:|318-getter|0|318-preInit
            // write pre-init user code here
            okCommand7 = new Command("Ok", Command.OK, 0);//GEN-LINE:|318-getter|1|318-postInit
            // write post-init user code here
        }//GEN-BEGIN:|318-getter|2|
        return okCommand7;
    }
    //</editor-fold>//GEN-END:|318-getter|2|
    
    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: numberErrorAlert ">//GEN-BEGIN:|316-getter|0|316-preInit
    /**
     * Returns an initiliazed instance of numberErrorAlert component.
     * @return the initialized component instance
     */
    public Alert getNumberErrorAlert() {
        if (numberErrorAlert == null) {//GEN-END:|316-getter|0|316-preInit
            // write pre-init user code here
            numberErrorAlert = new Alert("Error.", "What you just entered doesn\'t look like a number.", null, AlertType.ERROR);//GEN-BEGIN:|316-getter|1|316-postInit
            numberErrorAlert.addCommand(getOkCommand7());
            numberErrorAlert.setCommandListener(this);
            numberErrorAlert.setTimeout(Alert.FOREVER);//GEN-END:|316-getter|1|316-postInit
            // write post-init user code here
        }//GEN-BEGIN:|316-getter|2|
        return numberErrorAlert;
    }
    //</editor-fold>//GEN-END:|316-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: tocListAction ">//GEN-BEGIN:|325-action|0|325-preAction
    /**
     * Performs an action assigned to the selected list element in the tocList component.
     */
    public void tocListAction() {//GEN-END:|325-action|0|325-preAction
        // enter pre-action user code here
        String __selectedString = getTocList().getString(getTocList().getSelectedIndex());//GEN-LINE:|325-action|1|325-postAction
        // enter post-action user code here
    }//GEN-BEGIN:|325-action|2|
    //</editor-fold>//GEN-END:|325-action|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: showToc ">//GEN-BEGIN:|332-entry|0|333-preAction
    /**
     * Performs an action assigned to the showToc entry-point.
     */
    public void showToc() {//GEN-END:|332-entry|0|333-preAction
        // write pre-action user code here
        switchDisplayable(null, getTocList());//GEN-LINE:|332-entry|1|333-postAction
        // write post-action user code here
    }//GEN-BEGIN:|332-entry|2|
    //</editor-fold>//GEN-END:|332-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: okCommand8 ">//GEN-BEGIN:|335-getter|0|335-preInit
    /**
     * Returns an initiliazed instance of okCommand8 component.
     * @return the initialized component instance
     */
    public Command getOkCommand8() {
        if (okCommand8 == null) {//GEN-END:|335-getter|0|335-preInit
            // write pre-init user code here
            okCommand8 = new Command("Next", Command.OK, 0);//GEN-LINE:|335-getter|1|335-postInit
            // write post-init user code here
        }//GEN-BEGIN:|335-getter|2|
        return okCommand8;
    }
    //</editor-fold>//GEN-END:|335-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: cancelCommand7 ">//GEN-BEGIN:|337-getter|0|337-preInit
    /**
     * Returns an initiliazed instance of cancelCommand7 component.
     * @return the initialized component instance
     */
    public Command getCancelCommand7() {
        if (cancelCommand7 == null) {//GEN-END:|337-getter|0|337-preInit
            // write pre-init user code here
            cancelCommand7 = new Command("Back", Command.CANCEL, 0);//GEN-LINE:|337-getter|1|337-postInit
            // write post-init user code here
        }//GEN-BEGIN:|337-getter|2|
        return cancelCommand7;
    }
    //</editor-fold>//GEN-END:|337-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: goToChapter ">//GEN-BEGIN:|344-entry|0|345-preAction
    /**
     * Performs an action assigned to the goToChapter entry-point.
     */
    public void goToChapter() {//GEN-END:|344-entry|0|345-preAction
        // write pre-action user code here
        final int index = getChapterPositionList().getSelectedIndex();

        switch (index) {
            case 0:
                bookCanvas.goToSavedPosition(getTocList().getSelectedIndex());
                break;

            case 1:
                bookCanvas.goToFirstPage(getTocList().getSelectedIndex());
                break;

            case 2:
                bookCanvas.goToLastPage(getTocList().getSelectedIndex());
                break;

            default:
                bookCanvas.goToFirstPage(getTocList().getSelectedIndex());
                break;

        }

        switchDisplayable(null, bookCanvas);//GEN-LINE:|344-entry|1|345-postAction
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

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: tocList ">//GEN-BEGIN:|325-getter|0|325-preInit
    /**
     * Returns an initiliazed instance of tocList component.
     * @return the initialized component instance
     */
    public List getTocList() {
        if (tocList == null) {//GEN-END:|325-getter|0|325-preInit
            // write pre-init user code here
            tocList = new List("Table of Contents", Choice.IMPLICIT);//GEN-BEGIN:|325-getter|1|325-postInit
            tocList.addCommand(getOkCommand8());
            tocList.addCommand(getCancelCommand7());
            tocList.setCommandListener(this);
            tocList.setFitPolicy(Choice.TEXT_WRAP_DEFAULT);//GEN-END:|325-getter|1|325-postInit
            // write post-init user code here
        }//GEN-BEGIN:|325-getter|2|
        return tocList;
    }
    //</editor-fold>//GEN-END:|325-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: backCommand6 ">//GEN-BEGIN:|357-getter|0|357-preInit
    /**
     * Returns an initiliazed instance of backCommand6 component.
     * @return the initialized component instance
     */
    public Command getBackCommand6() {
        if (backCommand6 == null) {//GEN-END:|357-getter|0|357-preInit
            // write pre-init user code here
            backCommand6 = new Command("Back", Command.BACK, 0);//GEN-LINE:|357-getter|1|357-postInit
            // write post-init user code here
        }//GEN-BEGIN:|357-getter|2|
        return backCommand6;
    }
    //</editor-fold>//GEN-END:|357-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: okCommand9 ">//GEN-BEGIN:|359-getter|0|359-preInit
    /**
     * Returns an initiliazed instance of okCommand9 component.
     * @return the initialized component instance
     */
    public Command getOkCommand9() {
        if (okCommand9 == null) {//GEN-END:|359-getter|0|359-preInit
            // write pre-init user code here
            okCommand9 = new Command("Go!", Command.OK, 0);//GEN-LINE:|359-getter|1|359-postInit
            // write post-init user code here
        }//GEN-BEGIN:|359-getter|2|
        return okCommand9;
    }
    //</editor-fold>//GEN-END:|359-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: chapterPositionList ">//GEN-BEGIN:|354-getter|0|354-preInit
    /**
     * Returns an initiliazed instance of chapterPositionList component.
     * @return the initialized component instance
     */
    public List getChapterPositionList() {
        if (chapterPositionList == null) {//GEN-END:|354-getter|0|354-preInit
            // write pre-init user code here
            chapterPositionList = new List("Where to go?", Choice.IMPLICIT);//GEN-BEGIN:|354-getter|1|354-postInit
            chapterPositionList.append("Where I was last time", null);
            chapterPositionList.append("Start of chapter", null);
            chapterPositionList.append("End of chapter", null);
            chapterPositionList.addCommand(getBackCommand6());
            chapterPositionList.addCommand(getOkCommand9());
            chapterPositionList.setCommandListener(this);
            chapterPositionList.setSelectedFlags(new boolean[] { true, false, false });//GEN-END:|354-getter|1|354-postInit
            // write post-init user code here
        }//GEN-BEGIN:|354-getter|2|
        return chapterPositionList;
    }
    //</editor-fold>//GEN-END:|354-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: chapterPositionListAction ">//GEN-BEGIN:|354-action|0|354-preAction
    /**
     * Performs an action assigned to the selected list element in the chapterPositionList component.
     */
    public void chapterPositionListAction() {//GEN-END:|354-action|0|354-preAction
        // enter pre-action user code here
        String __selectedString = getChapterPositionList().getString(getChapterPositionList().getSelectedIndex());//GEN-BEGIN:|354-action|1|364-preAction
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
            }//GEN-BEGIN:|354-action|7|354-postAction
        }//GEN-END:|354-action|7|354-postAction
        // enter post-action user code here
    }//GEN-BEGIN:|354-action|8|
    //</editor-fold>//GEN-END:|354-action|8|
    
    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: splashScreen ">//GEN-BEGIN:|367-getter|0|367-preInit
    /**
     * Returns an initiliazed instance of splashScreen component.
     * @return the initialized component instance
     */
    public SplashScreen getSplashScreen() {
        if (splashScreen == null) {//GEN-END:|367-getter|0|367-preInit
            // write pre-init user code here
            splashScreen = new SplashScreen(getDisplay());//GEN-BEGIN:|367-getter|1|367-postInit
            splashScreen.setTitle("splashScreen");
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
            acceptLicense = new Form("License Agreement", new Item[] { getLicense1(), getLicense4(), getLicense5(), getLicense13() });//GEN-BEGIN:|372-getter|1|372-postInit
            acceptLicense.addCommand(getAcceptLicenseCommand());
            acceptLicense.addCommand(getRejectLicenseCommand());
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
            license1 = new StringItem("", "AlbiteREADER is a free ebook reader for the Java ME Platform, developed by Svetlin Ankov.");//GEN-BEGIN:|373-getter|1|373-postInit
            license1.setFont(getNormalFont());//GEN-END:|373-getter|1|373-postInit
            // write post-init user code here
        }//GEN-BEGIN:|373-getter|2|
        return license1;
    }
    //</editor-fold>//GEN-END:|373-getter|2|
    
    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: acceptLicenseCommand ">//GEN-BEGIN:|374-getter|0|374-preInit
    /**
     * Returns an initiliazed instance of acceptLicenseCommand component.
     * @return the initialized component instance
     */
    public Command getAcceptLicenseCommand() {
        if (acceptLicenseCommand == null) {//GEN-END:|374-getter|0|374-preInit
            // write pre-init user code here
            acceptLicenseCommand = new Command("Yes", Command.OK, 0);//GEN-LINE:|374-getter|1|374-postInit
            // write post-init user code here
        }//GEN-BEGIN:|374-getter|2|
        return acceptLicenseCommand;
    }
    //</editor-fold>//GEN-END:|374-getter|2|
    
    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: rejectLicenseCommand ">//GEN-BEGIN:|376-getter|0|376-preInit
    /**
     * Returns an initiliazed instance of rejectLicenseCommand component.
     * @return the initialized component instance
     */
    public Command getRejectLicenseCommand() {
        if (rejectLicenseCommand == null) {//GEN-END:|376-getter|0|376-preInit
            // write pre-init user code here
            rejectLicenseCommand = new Command("No", Command.CANCEL, 0);//GEN-LINE:|376-getter|1|376-postInit
            // write post-init user code here
        }//GEN-BEGIN:|376-getter|2|
        return rejectLicenseCommand;
    }
    //</editor-fold>//GEN-END:|376-getter|2|

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

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: license4 ">//GEN-BEGIN:|414-getter|0|414-preInit
    /**
     * Returns an initiliazed instance of license4 component.
     * @return the initialized component instance
     */
    public StringItem getLicense4() {
        if (license4 == null) {//GEN-END:|414-getter|0|414-preInit
            // write pre-init user code here
            license4 = new StringItem("", "This application is licensed under the Apache 2.0 License, the full text of which can be found here:");//GEN-BEGIN:|414-getter|1|414-postInit
            license4.setFont(getNormalFont());//GEN-END:|414-getter|1|414-postInit
            // write post-init user code here
        }//GEN-BEGIN:|414-getter|2|
        return license4;
    }
    //</editor-fold>//GEN-END:|414-getter|2|

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
            license13 = new StringItem("", "Do you accept the conditions of the license?");//GEN-BEGIN:|427-getter|1|427-postInit
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
        openMenu = true;
        switchDisplayable(null, getMenu());//GEN-LINE:|436-entry|1|437-postAction
        // write post-action user code here
    }//GEN-BEGIN:|436-entry|2|
    //</editor-fold>//GEN-END:|436-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: cancelCommand3 ">//GEN-BEGIN:|433-getter|0|433-preInit
    /**
     * Returns an initiliazed instance of cancelCommand3 component.
     * @return the initialized component instance
     */
    public Command getCancelCommand3() {
        if (cancelCommand3 == null) {//GEN-END:|433-getter|0|433-preInit
            // write pre-init user code here
            cancelCommand3 = new Command("Back", Command.CANCEL, 0);//GEN-LINE:|433-getter|1|433-postInit
            // write post-init user code here
        }//GEN-BEGIN:|433-getter|2|
        return cancelCommand3;
    }
    //</editor-fold>//GEN-END:|433-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: showLicense ">//GEN-BEGIN:|428-getter|0|428-preInit
    /**
     * Returns an initiliazed instance of showLicense component.
     * @return the initialized component instance
     */
    public Form getShowLicense() {
        if (showLicense == null) {//GEN-END:|428-getter|0|428-preInit
            // write pre-init user code here
            showLicense = new Form("About AlbiteREADER", new Item[] { getImageItem(), getStringItem(), getStringItem1(), getStringItem2(), getStringItem3(), getStringItem4(), getStringItem9(), getStringItem5(), getStringItem6(), getStringItem7(), getStringItem8(), getStringItem10(), getStringItem11(), getStringItem12() });//GEN-BEGIN:|428-getter|1|428-postInit
            showLicense.addCommand(getDismissLicenseCommand());
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
            menu = new List("AlbiteREADER", Choice.IMPLICIT);//GEN-BEGIN:|429-getter|1|429-postInit
            menu.append("Table of contents", null);
            menu.append("Open book", null);
            menu.append("Lookup word", null);
            menu.append("Convert number", null);
            menu.append("Font size", null);
            menu.append("Switch day / night", null);
            menu.append("Choose colors", null);
            menu.append("Screen mode", null);
            menu.append("Set dictionary folder", null);
            menu.append("About", null);
            menu.append("Quit", null);
            menu.addCommand(getOkCommand10());
            menu.addCommand(getCancelCommand3());
            menu.setCommandListener(this);
            menu.setSelectedFlags(new boolean[] { false, false, false, false, false, false, false, false, false, false, false });//GEN-END:|429-getter|1|429-postInit
            // write post-init user code here
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
        String __selectedString = getMenu().getString(getMenu().getSelectedIndex());//GEN-BEGIN:|429-action|1|469-preAction
        if (__selectedString != null) {
            if (__selectedString.equals("Table of contents")) {//GEN-END:|429-action|1|469-preAction
                // write pre-action user code here
//GEN-LINE:|429-action|2|469-postAction
                // write post-action user code here
            } else if (__selectedString.equals("Open book")) {//GEN-LINE:|429-action|3|470-preAction
                // write pre-action user code here
//GEN-LINE:|429-action|4|470-postAction
                // write post-action user code here
            } else if (__selectedString.equals("Lookup word")) {//GEN-LINE:|429-action|5|471-preAction
                // write pre-action user code here
//GEN-LINE:|429-action|6|471-postAction
                // write post-action user code here
            } else if (__selectedString.equals("Convert number")) {//GEN-LINE:|429-action|7|472-preAction
                // write pre-action user code here
//GEN-LINE:|429-action|8|472-postAction
                // write post-action user code here
            } else if (__selectedString.equals("Font size")) {//GEN-LINE:|429-action|9|473-preAction
                // write pre-action user code here
                setFontSize();//GEN-LINE:|429-action|10|473-postAction
                // write post-action user code here
            } else if (__selectedString.equals("Switch day / night")) {//GEN-LINE:|429-action|11|474-preAction
                // write pre-action user code here
//GEN-LINE:|429-action|12|474-postAction
                // write post-action user code here
            } else if (__selectedString.equals("Choose colors")) {//GEN-LINE:|429-action|13|475-preAction
                // write pre-action user code here
//GEN-LINE:|429-action|14|475-postAction
                // write post-action user code here
            } else if (__selectedString.equals("Screen mode")) {//GEN-LINE:|429-action|15|476-preAction
                // write pre-action user code here
//GEN-LINE:|429-action|16|476-postAction
                // write post-action user code here
            } else if (__selectedString.equals("Set dictionary folder")) {//GEN-LINE:|429-action|17|477-preAction
                // write pre-action user code here
//GEN-LINE:|429-action|18|477-postAction
                // write post-action user code here
            } else if (__selectedString.equals("About")) {//GEN-LINE:|429-action|19|478-preAction
                // write pre-action user code here
//GEN-LINE:|429-action|20|478-postAction
                // write post-action user code here
            } else if (__selectedString.equals("Quit")) {//GEN-LINE:|429-action|21|479-preAction
                // write pre-action user code here
//GEN-LINE:|429-action|22|479-postAction
                // write post-action user code here
            }//GEN-BEGIN:|429-action|23|429-postAction
        }//GEN-END:|429-action|23|429-postAction
        // enter post-action user code here
    }//GEN-BEGIN:|429-action|24|
    //</editor-fold>//GEN-END:|429-action|24|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: processMenu ">//GEN-BEGIN:|439-switch|0|439-preSwitch
    /**
     * Performs an action assigned to the processMenu switch-point.
     */
    public void processMenu() {//GEN-END:|439-switch|0|439-preSwitch
        // enter pre-switch user code here
        switch (getMenu().getSelectedIndex()) {//GEN-BEGIN:|439-switch|1|480-preAction
            case 0://GEN-END:|439-switch|1|480-preAction
                // write pre-action user code here
                setEntryForLookup("");
                showToc();//GEN-LINE:|439-switch|2|480-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|439-switch|3|489-preAction
            case 1://GEN-END:|439-switch|3|489-preAction
                // write pre-action user code here
                openLibrary();//GEN-LINE:|439-switch|4|489-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|439-switch|5|481-preAction
            case 2://GEN-END:|439-switch|5|481-preAction
                // write pre-action user code here
//GEN-LINE:|439-switch|6|481-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|439-switch|7|482-preAction
            case 3://GEN-END:|439-switch|7|482-preAction
                // write pre-action user code here
                enterNumber();//GEN-LINE:|439-switch|8|482-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|439-switch|9|483-preAction
            case 4://GEN-END:|439-switch|9|483-preAction
                // write pre-action user code here
                setFontSize();//GEN-LINE:|439-switch|10|483-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|439-switch|11|484-preAction
            case 5://GEN-END:|439-switch|11|484-preAction
                // write pre-action user code here
                bookCanvas.cycleColorSchemes();
                switchDisplayable(null, bookCanvas);//GEN-LINE:|439-switch|12|484-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|439-switch|13|485-preAction
            case 6://GEN-END:|439-switch|13|485-preAction
                // write pre-action user code here
                chooseColors();//GEN-LINE:|439-switch|14|485-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|439-switch|15|486-preAction
            case 7://GEN-END:|439-switch|15|486-preAction
                // write pre-action user code here
//GEN-LINE:|439-switch|16|486-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|439-switch|17|499-preAction
            case 8://GEN-END:|439-switch|17|499-preAction
                // write pre-action user code here
//GEN-LINE:|439-switch|18|499-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|439-switch|19|487-preAction
            case 9://GEN-END:|439-switch|19|487-preAction
                // write pre-action user code here
                switchDisplayable(null, getShowLicense());//GEN-LINE:|439-switch|20|487-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|439-switch|21|488-preAction
            case 10://GEN-END:|439-switch|21|488-preAction
                // write pre-action user code here
                quit();//GEN-LINE:|439-switch|22|488-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|439-switch|23|439-postSwitch
        }//GEN-END:|439-switch|23|439-postSwitch
        // enter post-switch user code here
    }//GEN-BEGIN:|439-switch|24|
    //</editor-fold>//GEN-END:|439-switch|24|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: okCommand10 ">//GEN-BEGIN:|440-getter|0|440-preInit
    /**
     * Returns an initiliazed instance of okCommand10 component.
     * @return the initialized component instance
     */
    public Command getOkCommand10() {
        if (okCommand10 == null) {//GEN-END:|440-getter|0|440-preInit
            // write pre-init user code here
            okCommand10 = new Command("Next", Command.OK, 0);//GEN-LINE:|440-getter|1|440-postInit
            // write post-init user code here
        }//GEN-BEGIN:|440-getter|2|
        return okCommand10;
    }
    //</editor-fold>//GEN-END:|440-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: dismissLicenseCommand ">//GEN-BEGIN:|444-getter|0|444-preInit
    /**
     * Returns an initiliazed instance of dismissLicenseCommand component.
     * @return the initialized component instance
     */
    public Command getDismissLicenseCommand() {
        if (dismissLicenseCommand == null) {//GEN-END:|444-getter|0|444-preInit
            // write pre-init user code here
            dismissLicenseCommand = new Command("Dismiss", Command.OK, 0);//GEN-LINE:|444-getter|1|444-postInit
            // write post-init user code here
        }//GEN-BEGIN:|444-getter|2|
        return dismissLicenseCommand;
    }
    //</editor-fold>//GEN-END:|444-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: imageItem ">//GEN-BEGIN:|447-getter|0|447-preInit
    /**
     * Returns an initiliazed instance of imageItem component.
     * @return the initialized component instance
     */
    public ImageItem getImageItem() {
        if (imageItem == null) {//GEN-END:|447-getter|0|447-preInit
            // write pre-init user code here
            imageItem = new ImageItem("", getAlbiteLogo(), ImageItem.LAYOUT_CENTER | ImageItem.LAYOUT_NEWLINE_BEFORE | Item.LAYOUT_2, "");//GEN-LINE:|447-getter|1|447-postInit
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
            stringItem1 = new StringItem("", "AlbiteREADER is a free ebook reader for the Java ME Platform, developed by Svetlin Ankov.");//GEN-BEGIN:|449-getter|1|449-postInit
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
            stringItem3 = new StringItem("", "http://albite.vlexofree.com/", Item.HYPERLINK);//GEN-BEGIN:|451-getter|1|451-postInit
            stringItem3.setFont(getUnderlinedFont());//GEN-END:|451-getter|1|451-postInit
            // write post-init user code here
        }//GEN-BEGIN:|451-getter|2|
        return stringItem3;
    }
    //</editor-fold>//GEN-END:|451-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: stringItem4 ">//GEN-BEGIN:|452-getter|0|452-preInit
    /**
     * Returns an initiliazed instance of stringItem4 component.
     * @return the initialized component instance
     */
    public StringItem getStringItem4() {
        if (stringItem4 == null) {//GEN-END:|452-getter|0|452-preInit
            // write pre-init user code here
            stringItem4 = new StringItem("", "This application is licensed under the Apache 2.0 License, the full text of which can be found here:");//GEN-BEGIN:|452-getter|1|452-postInit
            stringItem4.setFont(getNormalFont());//GEN-END:|452-getter|1|452-postInit
            // write post-init user code here
        }//GEN-BEGIN:|452-getter|2|
        return stringItem4;
    }
    //</editor-fold>//GEN-END:|452-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: stringItem5 ">//GEN-BEGIN:|453-getter|0|453-preInit
    /**
     * Returns an initiliazed instance of stringItem5 component.
     * @return the initialized component instance
     */
    public StringItem getStringItem5() {
        if (stringItem5 == null) {//GEN-END:|453-getter|0|453-preInit
            // write pre-init user code here
            stringItem5 = new StringItem("", "The source code of this application and the sources of all helper tools, used in its creation can be found at github:");//GEN-BEGIN:|453-getter|1|453-postInit
            stringItem5.setFont(getNormalFont());//GEN-END:|453-getter|1|453-postInit
            // write post-init user code here
        }//GEN-BEGIN:|453-getter|2|
        return stringItem5;
    }
    //</editor-fold>//GEN-END:|453-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: stringItem6 ">//GEN-BEGIN:|454-getter|0|454-preInit
    /**
     * Returns an initiliazed instance of stringItem6 component.
     * @return the initialized component instance
     */
    public StringItem getStringItem6() {
        if (stringItem6 == null) {//GEN-END:|454-getter|0|454-preInit
            // write pre-init user code here
            stringItem6 = new StringItem("", "http://github.com/dumbledore/", Item.HYPERLINK);//GEN-BEGIN:|454-getter|1|454-postInit
            stringItem6.setFont(getUnderlinedFont());//GEN-END:|454-getter|1|454-postInit
            // write post-init user code here
        }//GEN-BEGIN:|454-getter|2|
        return stringItem6;
    }
    //</editor-fold>//GEN-END:|454-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: stringItem7 ">//GEN-BEGIN:|455-getter|0|455-preInit
    /**
     * Returns an initiliazed instance of stringItem7 component.
     * @return the initialized component instance
     */
    public StringItem getStringItem7() {
        if (stringItem7 == null) {//GEN-END:|455-getter|0|455-preInit
            // write pre-init user code here
            stringItem7 = new StringItem("", "Apart from the code, written by the author, there are some free and open-source resources that have been integrated:");//GEN-BEGIN:|455-getter|1|455-postInit
            stringItem7.setFont(getNormalFont());//GEN-END:|455-getter|1|455-postInit
            // write post-init user code here
        }//GEN-BEGIN:|455-getter|2|
        return stringItem7;
    }
    //</editor-fold>//GEN-END:|455-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: stringItem8 ">//GEN-BEGIN:|456-getter|0|456-preInit
    /**
     * Returns an initiliazed instance of stringItem8 component.
     * @return the initialized component instance
     */
    public StringItem getStringItem8() {
        if (stringItem8 == null) {//GEN-END:|456-getter|0|456-preInit
            // write pre-init user code here
            stringItem8 = new StringItem("", "1. The Droid Serif font, licensed under the Apache 2.0 license");//GEN-BEGIN:|456-getter|1|456-postInit
            stringItem8.setFont(getNormalFont());//GEN-END:|456-getter|1|456-postInit
            // write post-init user code here
        }//GEN-BEGIN:|456-getter|2|
        return stringItem8;
    }
    //</editor-fold>//GEN-END:|456-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: stringItem9 ">//GEN-BEGIN:|457-getter|0|457-preInit
    /**
     * Returns an initiliazed instance of stringItem9 component.
     * @return the initialized component instance
     */
    public StringItem getStringItem9() {
        if (stringItem9 == null) {//GEN-END:|457-getter|0|457-preInit
            // write pre-init user code here
            stringItem9 = new StringItem("", "http://www.apache.org/licenses/LICENSE-2.0.txt", Item.HYPERLINK);//GEN-BEGIN:|457-getter|1|457-postInit
            stringItem9.setFont(getUnderlinedFont());//GEN-END:|457-getter|1|457-postInit
            // write post-init user code here
        }//GEN-BEGIN:|457-getter|2|
        return stringItem9;
    }
    //</editor-fold>//GEN-END:|457-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: stringItem10 ">//GEN-BEGIN:|458-getter|0|458-preInit
    /**
     * Returns an initiliazed instance of stringItem10 component.
     * @return the initialized component instance
     */
    public StringItem getStringItem10() {
        if (stringItem10 == null) {//GEN-END:|458-getter|0|458-preInit
            // write pre-init user code here
            stringItem10 = new StringItem("", "2. kXML2, licensed under the BSD license.");//GEN-BEGIN:|458-getter|1|458-postInit
            stringItem10.setFont(getNormalFont());//GEN-END:|458-getter|1|458-postInit
            // write post-init user code here
        }//GEN-BEGIN:|458-getter|2|
        return stringItem10;
    }
    //</editor-fold>//GEN-END:|458-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: stringItem11 ">//GEN-BEGIN:|459-getter|0|459-preInit
    /**
     * Returns an initiliazed instance of stringItem11 component.
     * @return the initialized component instance
     */
    public StringItem getStringItem11() {
        if (stringItem11 == null) {//GEN-END:|459-getter|0|459-preInit
            // write pre-init user code here
            stringItem11 = new StringItem("", "3. TinyLineGZIP - software developed by Andrew Girow (http://www.tinyline.com/)");//GEN-BEGIN:|459-getter|1|459-postInit
            stringItem11.setFont(getNormalFont());//GEN-END:|459-getter|1|459-postInit
            // write post-init user code here
        }//GEN-BEGIN:|459-getter|2|
        return stringItem11;
    }
    //</editor-fold>//GEN-END:|459-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: stringItem12 ">//GEN-BEGIN:|460-getter|0|460-preInit
    /**
     * Returns an initiliazed instance of stringItem12 component.
     * @return the initialized component instance
     */
    public StringItem getStringItem12() {
        if (stringItem12 == null) {//GEN-END:|460-getter|0|460-preInit
            // write pre-init user code here
            stringItem12 = new StringItem("", "4. The TeX Hyphenator from the zlibrary: the one used in FBReader");//GEN-BEGIN:|460-getter|1|460-postInit
            stringItem12.setFont(getNormalFont());//GEN-END:|460-getter|1|460-postInit
            // write post-init user code here
        }//GEN-BEGIN:|460-getter|2|
        return stringItem12;
    }
    //</editor-fold>//GEN-END:|460-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: returnToMenu ">//GEN-BEGIN:|462-if|0|462-preIf
    /**
     * Performs an action assigned to the returnToMenu if-point.
     */
    public void returnToMenu() {//GEN-END:|462-if|0|462-preIf
        // enter pre-if user code here
        if (openMenu) {//GEN-LINE:|462-if|1|463-preAction
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
            bookCanvas.setScheme(
                    ColorScheme.TYPE_DEFAULT,
                    0
                    );
            switchDisplayable(null, bookCanvas);//GEN-LINE:|501-if|4|503-postAction
            // write post-action user code here
        }//GEN-LINE:|501-if|5|501-postIf
        // enter post-if user code here
    }//GEN-BEGIN:|501-if|6|
    //</editor-fold>//GEN-END:|501-if|6|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: okCommand12 ">//GEN-BEGIN:|516-getter|0|516-preInit
    /**
     * Returns an initiliazed instance of okCommand12 component.
     * @return the initialized component instance
     */
    public Command getOkCommand12() {
        if (okCommand12 == null) {//GEN-END:|516-getter|0|516-preInit
            // write pre-init user code here
            okCommand12 = new Command("Next", Command.OK, 0);//GEN-LINE:|516-getter|1|516-postInit
            // write post-init user code here
        }//GEN-BEGIN:|516-getter|2|
        return okCommand12;
    }
    //</editor-fold>//GEN-END:|516-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: backCommand8 ">//GEN-BEGIN:|519-getter|0|519-preInit
    /**
     * Returns an initiliazed instance of backCommand8 component.
     * @return the initialized component instance
     */
    public Command getBackCommand8() {
        if (backCommand8 == null) {//GEN-END:|519-getter|0|519-preInit
            // write pre-init user code here
            backCommand8 = new Command("Back", Command.BACK, 0);//GEN-LINE:|519-getter|1|519-postInit
            // write post-init user code here
        }//GEN-BEGIN:|519-getter|2|
        return backCommand8;
    }
    //</editor-fold>//GEN-END:|519-getter|2|

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

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: okCommand14 ">//GEN-BEGIN:|533-getter|0|533-preInit
    /**
     * Returns an initiliazed instance of okCommand14 component.
     * @return the initialized component instance
     */
    public Command getOkCommand14() {
        if (okCommand14 == null) {//GEN-END:|533-getter|0|533-preInit
            // write pre-init user code here
            okCommand14 = new Command("Apply", Command.OK, 0);//GEN-LINE:|533-getter|1|533-postInit
            // write post-init user code here
        }//GEN-BEGIN:|533-getter|2|
        return okCommand14;
    }
    //</editor-fold>//GEN-END:|533-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: backCommand10 ">//GEN-BEGIN:|535-getter|0|535-preInit
    /**
     * Returns an initiliazed instance of backCommand10 component.
     * @return the initialized component instance
     */
    public Command getBackCommand10() {
        if (backCommand10 == null) {//GEN-END:|535-getter|0|535-preInit
            // write pre-init user code here
            backCommand10 = new Command("Back", Command.BACK, 0);//GEN-LINE:|535-getter|1|535-postInit
            // write post-init user code here
        }//GEN-BEGIN:|535-getter|2|
        return backCommand10;
    }
    //</editor-fold>//GEN-END:|535-getter|2|

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

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: chooseColors ">//GEN-BEGIN:|541-entry|0|542-preAction
    /**
     * Performs an action assigned to the chooseColors entry-point.
     */
    public void chooseColors() {//GEN-END:|541-entry|0|542-preAction
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
        switchDisplayable(null, exitBox);//GEN-LINE:|554-entry|1|555-postAction
        // write post-action user code here
    }//GEN-BEGIN:|554-entry|2|
    //</editor-fold>//GEN-END:|554-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: okCommand11 ">//GEN-BEGIN:|548-getter|0|548-preInit
    /**
     * Returns an initiliazed instance of okCommand11 component.
     * @return the initialized component instance
     */
    public Command getOkCommand11() {
        if (okCommand11 == null) {//GEN-END:|548-getter|0|548-preInit
            // write pre-init user code here
            okCommand11 = new Command("Yes", Command.OK, 0);//GEN-LINE:|548-getter|1|548-postInit
            // write post-init user code here
        }//GEN-BEGIN:|548-getter|2|
        return okCommand11;
    }
    //</editor-fold>//GEN-END:|548-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: backCommand7 ">//GEN-BEGIN:|550-getter|0|550-preInit
    /**
     * Returns an initiliazed instance of backCommand7 component.
     * @return the initialized component instance
     */
    public Command getBackCommand7() {
        if (backCommand7 == null) {//GEN-END:|550-getter|0|550-preInit
            // write pre-init user code here
            backCommand7 = new Command("No", Command.BACK, 0);//GEN-LINE:|550-getter|1|550-postInit
            // write post-init user code here
        }//GEN-BEGIN:|550-getter|2|
        return backCommand7;
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
            schemes.addCommand(getOkCommand12());
            schemes.addCommand(getBackCommand8());
            schemes.setCommandListener(this);//GEN-END:|512-getter|1|512-postInit
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
            colors.addCommand(getOkCommand14());
            colors.addCommand(getBackCommand10());
            colors.setCommandListener(this);//GEN-END:|530-getter|1|530-postInit
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

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: unitGroupList ">//GEN-BEGIN:|255-getter|0|255-preInit
    /**
     * Returns an initiliazed instance of unitGroupList component.
     * @return the initialized component instance
     */
    public List getUnitGroupList() {
        if (unitGroupList == null) {//GEN-END:|255-getter|0|255-preInit
            // write pre-init user code here
            unitGroupList = new List("Select Units Group", Choice.IMPLICIT);//GEN-BEGIN:|255-getter|1|255-postInit
            unitGroupList.addCommand(getBackCommand2());
            unitGroupList.addCommand(getOkCommand4());
            unitGroupList.setCommandListener(this);
            unitGroupList.setFitPolicy(Choice.TEXT_WRAP_DEFAULT);//GEN-END:|255-getter|1|255-postInit
            // write post-init user code here

            /*
             * Load metrics list
             */
            final UnitGroup[] groups = UnitGroup.GROUPS;
            for (int i = 0; i < groups.length; i++) {
                unitGroupList.append(groups[i].name, null);
            }
        }//GEN-BEGIN:|255-getter|2|
        return unitGroupList;
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

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: okCommand13 ">//GEN-BEGIN:|561-getter|0|561-preInit
    /**
     * Returns an initiliazed instance of okCommand13 component.
     * @return the initialized component instance
     */
    public Command getOkCommand13() {
        if (okCommand13 == null) {//GEN-END:|561-getter|0|561-preInit
            // write pre-init user code here
            okCommand13 = new Command("Apply", Command.OK, 0);//GEN-LINE:|561-getter|1|561-postInit
            // write post-init user code here
        }//GEN-BEGIN:|561-getter|2|
        return okCommand13;
    }
    //</editor-fold>//GEN-END:|561-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: backCommand9 ">//GEN-BEGIN:|563-getter|0|563-preInit
    /**
     * Returns an initiliazed instance of backCommand9 component.
     * @return the initialized component instance
     */
    public Command getBackCommand9() {
        if (backCommand9 == null) {//GEN-END:|563-getter|0|563-preInit
            // write pre-init user code here
            backCommand9 = new Command("Back", Command.BACK, 0);//GEN-LINE:|563-getter|1|563-postInit
            // write post-init user code here
        }//GEN-BEGIN:|563-getter|2|
        return backCommand9;
    }
    //</editor-fold>//GEN-END:|563-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: fontSizes ">//GEN-BEGIN:|558-getter|0|558-preInit
    /**
     * Returns an initiliazed instance of fontSizes component.
     * @return the initialized component instance
     */
    public List getFontSizes() {
        if (fontSizes == null) {//GEN-END:|558-getter|0|558-preInit
            // write pre-init user code here
            fontSizes = new List("Select font size", Choice.IMPLICIT);//GEN-BEGIN:|558-getter|1|558-postInit
            fontSizes.addCommand(getOkCommand13());
            fontSizes.addCommand(getBackCommand9());
            fontSizes.setCommandListener(this);//GEN-END:|558-getter|1|558-postInit
            // write post-init user code here
            for (int i = 0; i < BookCanvas.FONT_SIZES.length; i++) {
                fontSizes.append(Integer.toString(BookCanvas.FONT_SIZES[i]), null);
            }
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

    /**
     * Returns a display instance.
     * @return the display instance.
     */
    public Display getDisplay () {
        return Display.getDisplay(this);
    }

    /**
     * Called when MIDlet is started.
     * Checks whether the MIDlet have been already started and initialize/starts or resumes the MIDlet.
     */
    public void startApp() {
        if (midletPaused) {
            resumeMIDlet ();
        } else {
            initialize ();
            startMIDlet ();
        }
        midletPaused = false;
    }

    /**
     * Called when MIDlet is paused.
     */
    public void pauseApp() {
        midletPaused = true;
    }

    /**
     * Called to signal the MIDlet to terminate.
     * @param unconditional if true, then the MIDlet has to be unconditionally terminated and all resources has to be released.
     */
    public void destroyApp(boolean unconditional) {
        //MIDlet destroyed by the AMS

        //call clean-up
        exitMIDlet();
    }

    /**
     * Exits MIDlet.
     */
    public void exitMIDlet() {
        //Clean-up code. The MIDlet destroys by its own accord
        bookCanvas.close();
        saveOptionsToRMS();
        closeRMS();
        switchDisplayable(null, null);
        notifyDestroyed();
    }

    private void openRMSAndLoadData() {
        try {
            rs = RecordStore.openRecordStore("application",true);

        if (rs.getNumRecords() > 0) {
            //deserialize first record
            byte[] data = rs.getRecord(1);
            DataInputStream din =
                    new DataInputStream(new ByteArrayInputStream(data));
            try {
                //load last book open
                bookURL = din.readUTF();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

        } else {
            /*
             * No records found, so it must be the first time
             * the app starts on this device.
             */
            firstTime = true;
        }

        } catch (RecordStoreException rse) {
            //no saving is possible
            rse.printStackTrace();
        }
    }

    public final void saveOptionsToRMS() {
        if (bookURL != null && bookURL != "") {
            try {
                ByteArrayOutputStream boas = new ByteArrayOutputStream();
                DataOutputStream dout = new DataOutputStream(boas);
                try {
                    //save last book open
                    dout.writeUTF(bookURL);

                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }

                byte[] data = boas.toByteArray();

                //serialize first record
                if (rs.getNumRecords() > 0) {
                    rs.setRecord(1, data, 0, data.length);
                } else {
                    rs.addRecord(data, 0, data.length);
                }
            } catch (RecordStoreException rse) {
                //no saving is possible
                rse.printStackTrace();
            }
        }
    }

    private void closeRMS() {
        try {
            rs.closeRecordStore();
        } catch (RecordStoreException rse) {
            //no saving is possible
            rse.printStackTrace();
        }
    }

    public void setEntryForLookup(String s) {
        entryForLookup = s;
    }

    private void loadUnitsToLists() {
        /* Find Selected Group */
        UnitGroup group = UnitGroup.GROUPS[unitGroupList.getSelectedIndex()];

        /* Load items in lists */
        unitFromList.deleteAll();
        unitToList.deleteAll();

        Unit[] units = group.units;
        for (int i = 0; i < units.length; i++) {
            unitFromList.append(units[i].name, null);
            unitToList.append(units[i].name, null);
        }
    }

    private void convertUnits() {
        /*
         * Converting units
         */
        UnitGroup group = UnitGroup.GROUPS[unitGroupList.getSelectedIndex()];
        Unit[] units = group.units;
        Unit unitFrom = group.units[unitFromList.getSelectedIndex()];
        Unit unitTo = group.units[unitToList.getSelectedIndex()];
        double quantityFrom =
                Double.parseDouble(enterNumberTextBox.getString());
        double quantityTo = round(Unit.convert(quantityFrom, unitFrom, unitTo));


        resultFromQuantity.setText(Double.toString(quantityFrom));
        resultFromUnit.setText(unitFrom.name);
        resultToQuantity.setText(Double.toString(quantityTo));
        resultToUnit.setText(unitTo.name);
    }

    private double round(double d) {
        double d2 = d * 10;
        long l = (long) d2;
        return ((double) l) / 10;
    }

    public final void calledOutside() {
        openMenu = false;
    }
}