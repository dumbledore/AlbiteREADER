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
    
    //<editor-fold defaultstate="collapsed" desc=" Generated Fields ">//GEN-BEGIN:|fields|0|
    private Command backCommand3;
    private Command cancelCommand4;
    private Command okCommand5;
    private Command cancelCommand5;
    private Command okCommand6;
    private Command backCommand4;
    private Command cancelCommand3;
    private Command backCommand2;
    private Command okCommand4;
    private Command cancelCommand6;
    private Command backCommand5;
    private Command screenCommand;
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
    private Command okCommand7;
    private List unitFromList;
    private List unitToList;
    private FileBrowser fileBrowser;
    private Form showConversionResultForm;
    private StringItem resultFromQuantity;
    private StringItem resultFromUnit;
    private Spacer spacer;
    private StringItem resultToQuantity;
    private StringItem resultToUnit;
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
    private Alert numberErrorAlert;
    private SimpleCancellableTask loadBookTask;
    private Image albiteLogo;
    private Font loadingFont;
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
        unitFromList = new List("Convert From", Choice.IMPLICIT);//GEN-BEGIN:|0-initialize|1|0-postInitialize
        unitFromList.addCommand(getBackCommand3());
        unitFromList.addCommand(getOkCommand5());
        unitFromList.setCommandListener(this);
        unitToList = new List("Convert To", Choice.IMPLICIT);
        unitToList.addCommand(getBackCommand4());
        unitToList.addCommand(getOkCommand6());
        unitToList.setCommandListener(this);
        resultFromQuantity = new StringItem("Initial Quantity:", "");
        resultFromUnit = new StringItem("Initial Units:", "");
        resultToQuantity = new StringItem("Resulting Quantity:", "");
        resultToUnit = new StringItem("Resulting Units:", "");
        showConversionResultForm = new Form("Conversion Result", new Item[] { resultFromQuantity, resultFromUnit, getSpacer(), resultToQuantity, resultToUnit });
        showConversionResultForm.addCommand(getCancelCommand6());
        showConversionResultForm.addCommand(getBackCommand5());
        showConversionResultForm.addCommand(getScreenCommand());
        showConversionResultForm.setCommandListener(this);
        bookCanvas = new BookCanvas(this);
        bookCanvas.setTitle("bookCanvas");
        bookCanvas.setFullScreenMode(true);
        unitGroupList = new List("Select Units Group", Choice.IMPLICIT);
        unitGroupList.addCommand(getBackCommand2());
        unitGroupList.addCommand(getOkCommand4());
        unitGroupList.setCommandListener(this);
        unitGroupList.setFitPolicy(Choice.TEXT_WRAP_DEFAULT);
        enterNumberTextBox = new TextBox("Enter number", "", 64, TextField.NUMERIC);
        enterNumberTextBox.addCommand(getOkCommand3());
        enterNumberTextBox.addCommand(getCancelCommand2());
        enterNumberTextBox.setCommandListener(this);
        enterNumberTextBox.setInitialInputMode("UCB_BASIC_LATIN");//GEN-END:|0-initialize|1|0-postInitialize
        // write post-initialize user code here

        /* RMS */
        openRMSAndLoadData();

        /*
         * The BookCanvas must be initialized before usage. This is because
         * of the fact, that it wouldn't have correct metrics, i.e.
         * wouldn't be in fullscreenmode when looked at from the constructor
         */
        bookCanvas.initialize();

        /*
         * Load metrics list
         */
        final UnitGroup[] groups = UnitGroup.GROUPS;
        for (int i = 0; i < groups.length; i++) {
            unitGroupList.append(groups[i].name, null);
        }

    }//GEN-BEGIN:|0-initialize|2|
    //</editor-fold>//GEN-END:|0-initialize|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: startMIDlet ">//GEN-BEGIN:|3-startMIDlet|0|3-preAction
    /**
     * Performs an action assigned to the Mobile Device - MIDlet Started point.
     */
    public void startMIDlet() {//GEN-END:|3-startMIDlet|0|3-preAction
        // write pre-action user code here
        lastBookAvailable();//GEN-LINE:|3-startMIDlet|1|3-postAction
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
        if (displayable == dictEntriesList) {//GEN-BEGIN:|7-commandAction|1|189-preAction
            if (command == List.SELECT_COMMAND) {//GEN-END:|7-commandAction|1|189-preAction
                // write pre-action user code here
                dictEntriesListAction();//GEN-LINE:|7-commandAction|2|189-postAction
                // write post-action user code here
            } else if (command == backCommand) {//GEN-LINE:|7-commandAction|3|206-preAction
                // write pre-action user code here
//GEN-LINE:|7-commandAction|4|206-postAction
                // write post-action user code here
            } else if (command == cancelCommand) {//GEN-LINE:|7-commandAction|5|208-preAction
                // write pre-action user code here
                switchDisplayable(null, bookCanvas);//GEN-LINE:|7-commandAction|6|208-postAction
                // write post-action user code here
            } else if (command == okCommand) {//GEN-LINE:|7-commandAction|7|210-preAction
                // write pre-action user code here
//GEN-LINE:|7-commandAction|8|210-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|9|186-preAction
        } else if (displayable == dictsList) {
            if (command == List.SELECT_COMMAND) {//GEN-END:|7-commandAction|9|186-preAction
                // write pre-action user code here
                dictsListAction();//GEN-LINE:|7-commandAction|10|186-postAction
                // write post-action user code here
            } else if (command == okCommand2) {//GEN-LINE:|7-commandAction|11|220-preAction
                // write pre-action user code here
                switchDisplayable(null, getDictEntriesList());//GEN-LINE:|7-commandAction|12|220-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|13|218-preAction
        } else if (displayable == enterDictEntryTextBox) {
            if (command == okCommand1) {//GEN-END:|7-commandAction|13|218-preAction
                // write pre-action user code here
                moreThanOneDictFound();//GEN-LINE:|7-commandAction|14|218-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|15|249-preAction
        } else if (displayable == enterNumberTextBox) {
            if (command == cancelCommand2) {//GEN-END:|7-commandAction|15|249-preAction
                // write pre-action user code here
                switchDisplayable(null, bookCanvas);//GEN-LINE:|7-commandAction|16|249-postAction
                // write post-action user code here
            } else if (command == okCommand3) {//GEN-LINE:|7-commandAction|17|247-preAction
                // write pre-action user code here
                isNumberOKCheck();//GEN-LINE:|7-commandAction|18|247-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|19|103-preAction
        } else if (displayable == errorAlert) {
            if (command == DISMISS_COMMAND) {//GEN-END:|7-commandAction|19|103-preAction
                // write pre-action user code here
                switchDisplayable(null, getFileBrowser());//GEN-LINE:|7-commandAction|20|103-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|21|119-preAction
        } else if (displayable == fileBrowser) {
            if (command == CANCEL_COMMAND) {//GEN-END:|7-commandAction|21|119-preAction
                // write pre-action user code here
                displayBookCanvas();//GEN-LINE:|7-commandAction|22|119-postAction
                // write post-action user code here
            } else if (command == FileBrowser.SELECT_FILE_COMMAND) {//GEN-LINE:|7-commandAction|23|34-preAction
                // write pre-action user code here
                bookURL = getFileBrowser().getSelectedFileURL();
                switchDisplayable(null, getLoadBook());//GEN-LINE:|7-commandAction|24|34-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|25|159-preAction
        } else if (displayable == loadBook) {
            if (command == WaitScreen.FAILURE_COMMAND) {//GEN-END:|7-commandAction|25|159-preAction
                // write pre-action user code here
                getErrorAlert().setString(STRING_ERROR_BOOK);
                switchDisplayable(null, getErrorAlert());//GEN-LINE:|7-commandAction|26|159-postAction
                // write post-action user code here
            } else if (command == WaitScreen.SUCCESS_COMMAND) {//GEN-LINE:|7-commandAction|27|158-preAction
                // write pre-action user code here
                switchDisplayable(null, bookCanvas);//GEN-LINE:|7-commandAction|28|158-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|29|319-preAction
        } else if (displayable == numberErrorAlert) {
            if (command == okCommand7) {//GEN-END:|7-commandAction|29|319-preAction
                // write pre-action user code here
                switchDisplayable(null, enterNumberTextBox);//GEN-LINE:|7-commandAction|30|319-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|31|295-preAction
        } else if (displayable == showConversionResultForm) {
            if (command == backCommand5) {//GEN-END:|7-commandAction|31|295-preAction
                // write pre-action user code here
                switchDisplayable(null, unitToList);//GEN-LINE:|7-commandAction|32|295-postAction
                // write post-action user code here
            } else if (command == cancelCommand6) {//GEN-LINE:|7-commandAction|33|293-preAction
                // write pre-action user code here
                switchDisplayable(null, bookCanvas);//GEN-LINE:|7-commandAction|34|293-postAction
                // write post-action user code here
            } else if (command == screenCommand) {//GEN-LINE:|7-commandAction|35|297-preAction
                // write pre-action user code here
                switchDisplayable(null, enterNumberTextBox);//GEN-LINE:|7-commandAction|36|297-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|37|233-preAction
        } else if (displayable == showDictEntry) {
            if (command == backCommand1) {//GEN-END:|7-commandAction|37|233-preAction
                // write pre-action user code here
//GEN-LINE:|7-commandAction|38|233-postAction
                // write post-action user code here
            } else if (command == cancelCommand1) {//GEN-LINE:|7-commandAction|39|235-preAction
                // write pre-action user code here
//GEN-LINE:|7-commandAction|40|235-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|41|267-preAction
        } else if (displayable == unitFromList) {
            if (command == List.SELECT_COMMAND) {//GEN-END:|7-commandAction|41|267-preAction
                // write pre-action user code here
                unitFromListAction();//GEN-LINE:|7-commandAction|42|267-postAction
                // write post-action user code here
            } else if (command == backCommand3) {//GEN-LINE:|7-commandAction|43|275-preAction
                // write pre-action user code here
                switchDisplayable(null, unitGroupList);//GEN-LINE:|7-commandAction|44|275-postAction
                // write post-action user code here
            } else if (command == okCommand5) {//GEN-LINE:|7-commandAction|45|277-preAction
                // write pre-action user code here
                switchDisplayable(null, unitToList);//GEN-LINE:|7-commandAction|46|277-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|47|256-preAction
        } else if (displayable == unitGroupList) {
            if (command == List.SELECT_COMMAND) {//GEN-END:|7-commandAction|47|256-preAction
                // write pre-action user code here
                unitGroupListAction();//GEN-LINE:|7-commandAction|48|256-postAction
                // write post-action user code here
            } else if (command == backCommand2) {//GEN-LINE:|7-commandAction|49|261-preAction
                // write pre-action user code here
                switchDisplayable(null, enterNumberTextBox);//GEN-LINE:|7-commandAction|50|261-postAction
                // write post-action user code here
            } else if (command == okCommand4) {//GEN-LINE:|7-commandAction|51|265-preAction
                // write pre-action user code here
                loadUnitsToLists();
                switchDisplayable(null, unitFromList);//GEN-LINE:|7-commandAction|52|265-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|53|270-preAction
        } else if (displayable == unitToList) {
            if (command == List.SELECT_COMMAND) {//GEN-END:|7-commandAction|53|270-preAction
                // write pre-action user code here
                unitToListAction();//GEN-LINE:|7-commandAction|54|270-postAction
                // write post-action user code here
            } else if (command == backCommand4) {//GEN-LINE:|7-commandAction|55|285-preAction
                // write pre-action user code here
                switchDisplayable(null, unitFromList);//GEN-LINE:|7-commandAction|56|285-postAction
                // write post-action user code here
            } else if (command == okCommand6) {//GEN-LINE:|7-commandAction|57|287-preAction
                // write pre-action user code here
                convertUnits();
                switchDisplayable(null, showConversionResultForm);//GEN-LINE:|7-commandAction|58|287-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|59|7-postCommandAction
        }//GEN-END:|7-commandAction|59|7-postCommandAction
        // write post-action user code here
    }//GEN-BEGIN:|7-commandAction|60|285-postAction
    //</editor-fold>//GEN-END:|7-commandAction|60|285-postAction


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
            switchDisplayable(null, bookCanvas);//GEN-LINE:|151-if|2|152-postAction
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
    //</editor-fold>

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
    //</editor-fold>

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
            loadingFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_ITALIC, Font.SIZE_SMALL);//GEN-LINE:|180-getter|1|180-postInit
            // write post-init user code here
        }//GEN-BEGIN:|180-getter|2|
        return loadingFont;
    }
    //</editor-fold>//GEN-END:|180-getter|2|
    //</editor-fold>

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
    //</editor-fold>



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
    //</editor-fold>

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
            cancelCommand2 = new Command("Close", "<null>", Command.CANCEL, 0);//GEN-LINE:|248-getter|1|248-postInit
            // write post-init user code here
        }//GEN-BEGIN:|248-getter|2|
        return cancelCommand2;
    }
    //</editor-fold>//GEN-END:|248-getter|2|
    //</editor-fold>



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

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: cancelCommand3 ">//GEN-BEGIN:|258-getter|0|258-preInit
    /**
     * Returns an initiliazed instance of cancelCommand3 component.
     * @return the initialized component instance
     */
    public Command getCancelCommand3() {
        if (cancelCommand3 == null) {//GEN-END:|258-getter|0|258-preInit
            // write pre-init user code here
            cancelCommand3 = new Command("Close", Command.CANCEL, 0);//GEN-LINE:|258-getter|1|258-postInit
            // write post-init user code here
        }//GEN-BEGIN:|258-getter|2|
        return cancelCommand3;
    }
    //</editor-fold>//GEN-END:|258-getter|2|

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
        String __selectedString = unitGroupList.getString(unitGroupList.getSelectedIndex());//GEN-LINE:|255-action|1|255-postAction
        // enter post-action user code here
    }//GEN-BEGIN:|255-action|2|
    //</editor-fold>//GEN-END:|255-action|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: cancelCommand4 ">//GEN-BEGIN:|272-getter|0|272-preInit
    /**
     * Returns an initiliazed instance of cancelCommand4 component.
     * @return the initialized component instance
     */
    public Command getCancelCommand4() {
        if (cancelCommand4 == null) {//GEN-END:|272-getter|0|272-preInit
            // write pre-init user code here
            cancelCommand4 = new Command("Close", Command.CANCEL, 0);//GEN-LINE:|272-getter|1|272-postInit
            // write post-init user code here
        }//GEN-BEGIN:|272-getter|2|
        return cancelCommand4;
    }
    //</editor-fold>//GEN-END:|272-getter|2|

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

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: cancelCommand5 ">//GEN-BEGIN:|282-getter|0|282-preInit
    /**
     * Returns an initiliazed instance of cancelCommand5 component.
     * @return the initialized component instance
     */
    public Command getCancelCommand5() {
        if (cancelCommand5 == null) {//GEN-END:|282-getter|0|282-preInit
            // write pre-init user code here
            cancelCommand5 = new Command("Close", Command.CANCEL, 0);//GEN-LINE:|282-getter|1|282-postInit
            // write post-init user code here
        }//GEN-BEGIN:|282-getter|2|
        return cancelCommand5;
    }
    //</editor-fold>//GEN-END:|282-getter|2|

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







    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: spacer ">//GEN-BEGIN:|309-getter|0|309-preInit
    /**
     * Returns an initiliazed instance of spacer component.
     * @return the initialized component instance
     */
    public Spacer getSpacer() {
        if (spacer == null) {//GEN-END:|309-getter|0|309-preInit
            // write pre-init user code here
            spacer = new Spacer(16, 10);//GEN-BEGIN:|309-getter|1|309-postInit
            spacer.setPreferredSize(-1, 8);//GEN-END:|309-getter|1|309-postInit
            // write post-init user code here
        }//GEN-BEGIN:|309-getter|2|
        return spacer;
    }
    //</editor-fold>//GEN-END:|309-getter|2|
    //</editor-fold>


    //</editor-fold>



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
            switchDisplayable(null, unitGroupList);//GEN-LINE:|312-if|2|313-postAction
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
    //</editor-fold>

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
            DataInputStream din = new DataInputStream(new ByteArrayInputStream(data));
            try {
                //load last book open
                bookURL = din.readUTF();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
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
}