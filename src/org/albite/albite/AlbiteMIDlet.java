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
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import org.albite.book.book.Book;
import org.albite.book.book.BookException;
import org.netbeans.microedition.lcdui.pda.FileBrowser;


/**
 * @author Albus Dumbledore
 */
public class AlbiteMIDlet extends MIDlet implements CommandListener {

    private boolean midletPaused = false;

    public  String currentBookURL = "";
    String lastBookURL;

    RecordStore rs;

    private long time;
    private boolean problemWithFile = false;
    private boolean lastBookAvailable = false;

    //<editor-fold defaultstate="collapsed" desc=" Generated Fields ">//GEN-BEGIN:|fields|0|
    private Command DISMISS_COMMAND;
    private Command CANCEL_COMMAND;
    private FileBrowser fileBrowser;
    private BookCanvas bookCanvas;
    private Alert errorAlert;
    //</editor-fold>//GEN-END:|fields|0|

    /**
     * The HelloMIDlet constructor.
     */
//    public AlbiteMIDlet() {
//        app = this;
//    }

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
        bookCanvas.setTitle("");
        bookCanvas.setFullScreenMode(true);//GEN-END:|0-initialize|1|0-postInitialize
        // write post-initialize user code here
        openRMSAndLoadData();
        bookCanvas.initialize();
        bookCanvas.initiliazePageViews();
    }//GEN-BEGIN:|0-initialize|2|
    //</editor-fold>//GEN-END:|0-initialize|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: startMIDlet ">//GEN-BEGIN:|3-startMIDlet|0|3-preAction
    /**
     * Performs an action assigned to the Mobile Device - MIDlet Started point.
     */
    public void startMIDlet() {//GEN-END:|3-startMIDlet|0|3-preAction
        // write pre-action user code here

        openLastBook();//GEN-LINE:|3-startMIDlet|1|3-postAction
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
        if (displayable == errorAlert) {//GEN-BEGIN:|7-commandAction|1|103-preAction
            if (command == DISMISS_COMMAND) {//GEN-END:|7-commandAction|1|103-preAction
                // write pre-action user code here
                switchDisplayable(null, getFileBrowser());//GEN-LINE:|7-commandAction|2|103-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|3|119-preAction
        } else if (displayable == fileBrowser) {
            if (command == CANCEL_COMMAND) {//GEN-END:|7-commandAction|3|119-preAction
                // write pre-action user code here
                displayBookCanvas();//GEN-LINE:|7-commandAction|4|119-postAction
                // write post-action user code here
            } else if (command == FileBrowser.SELECT_FILE_COMMAND) {//GEN-LINE:|7-commandAction|5|34-preAction
                // write pre-action user code here
                openByBrowser();//GEN-LINE:|7-commandAction|6|34-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|7|7-postCommandAction
        }//GEN-END:|7-commandAction|7|7-postCommandAction
        // write post-action user code here
    }//GEN-BEGIN:|7-commandAction|8|
    //</editor-fold>//GEN-END:|7-commandAction|8|

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
            fileBrowser.setFilter(".alb");
            fileBrowser.addCommand(FileBrowser.SELECT_FILE_COMMAND);
            fileBrowser.addCommand(getCANCEL_COMMAND());//GEN-END:|32-getter|1|32-postInit
            // write post-init user code here
        }//GEN-BEGIN:|32-getter|2|
        return fileBrowser;
    }
    //</editor-fold>//GEN-END:|32-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: openBook ">//GEN-BEGIN:|89-entry|0|90-preAction
    /**
     * Performs an action assigned to the openBook entry-point.
     */
    public void openBook() {//GEN-END:|89-entry|0|90-preAction
        // write pre-action user code here
        problemWithFile = false;

        //If the book is already open, no need to load it again
        if (lastBookURL != null && lastBookURL.equals(currentBookURL) && bookCanvas.bookOpen()) {
            problemWithFile();
            return;
        }

        //try to open the book
        try {
            Book newBook = new Book();
            newBook.open(currentBookURL);

            //Book has been opened successfully and therefore is VALID
            lastBookURL = currentBookURL;

            bookCanvas.openBook(newBook);
            bookCanvas.goToPosition(newBook.getCurrentChapter(), newBook.getCurrentChapter().getPosition());
        } catch (IOException ioe) {
            //somethign wrong with the io
            problemWithFile = true;
            ioe.printStackTrace();
        } catch (BookException be) {
            //something wrong with the file!
            problemWithFile = true;
            be.printStackTrace();
        }

        //Try freeing resources before showing book
        System.gc();
        problemWithFile();//GEN-LINE:|89-entry|1|90-postAction
        // write post-action user code here
    }//GEN-BEGIN:|89-entry|2|
    //</editor-fold>//GEN-END:|89-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: problemWithFile ">//GEN-BEGIN:|95-if|0|95-preIf
    /**
     * Performs an action assigned to the problemWithFile if-point.
     */
    public void problemWithFile() {//GEN-END:|95-if|0|95-preIf
        // enter pre-if user code here
        if (problemWithFile) {//GEN-LINE:|95-if|1|96-preAction
            // write pre-action user code here
            //need to force the next two here
//            lastBookURL = null;
//            thereIsABookOpen = false;

            getErrorAlert().setString("There was a problem opening this book. Probably the file is corrupted.");
            switchDisplayable(null, getErrorAlert());//GEN-LINE:|95-if|2|96-postAction
            // write post-action user code here
        } else {//GEN-LINE:|95-if|3|97-preAction
            // write pre-action user code here
            switchDisplayable(null, bookCanvas);//GEN-LINE:|95-if|4|97-postAction
            // write post-action user code here
        }//GEN-LINE:|95-if|5|95-postIf
        // enter post-if user code here
    }//GEN-BEGIN:|95-if|6|
    //</editor-fold>//GEN-END:|95-if|6|

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
        if (lastBookAvailable) {//GEN-LINE:|134-if|1|135-preAction
            // write pre-action user code here
            openBook();//GEN-LINE:|134-if|2|135-postAction
            // write post-action user code here
        } else {//GEN-LINE:|134-if|3|136-preAction
            // write pre-action user code here
            switchDisplayable(null, getFileBrowser());//GEN-LINE:|134-if|4|136-postAction
            // write post-action user code here
        }//GEN-LINE:|134-if|5|134-postIf
        // enter post-if user code here
    }//GEN-BEGIN:|134-if|6|
    //</editor-fold>//GEN-END:|134-if|6|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: openLastBook ">//GEN-BEGIN:|140-entry|0|141-preAction
    /**
     * Performs an action assigned to the openLastBook entry-point.
     */
    public void openLastBook() {//GEN-END:|140-entry|0|141-preAction
        // write pre-action user code here
        if (lastBookURL != null) {
            lastBookAvailable = true;
            currentBookURL = lastBookURL;
        }
        lastBookAvailable();//GEN-LINE:|140-entry|1|141-postAction
        // write post-action user code here
    }//GEN-BEGIN:|140-entry|2|
    //</editor-fold>//GEN-END:|140-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: openByBrowser ">//GEN-BEGIN:|147-entry|0|148-preAction
    /**
     * Performs an action assigned to the openByBrowser entry-point.
     */
    public void openByBrowser() {//GEN-END:|147-entry|0|148-preAction
        // write pre-action user code here
        currentBookURL = fileBrowser.getSelectedFileURL();
        openBook();//GEN-LINE:|147-entry|1|148-postAction
        // write post-action user code here
    }//GEN-BEGIN:|147-entry|2|
    //</editor-fold>//GEN-END:|147-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: displayBookCanvas ">//GEN-BEGIN:|151-if|0|151-preIf
    /**
     * Performs an action assigned to the displayBookCanvas if-point.
     */
    public void displayBookCanvas() {//GEN-END:|151-if|0|151-preIf
        // enter pre-if user code here
        if (bookCanvas.bookOpen()) {//GEN-LINE:|151-if|1|152-preAction
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
        //Clean-up code. The MIDlet destroys itself voluntarily
        saveOptionsToRMS();
        bookCanvas.closeBook();
        closeRMS();
        switchDisplayable(null, null);
        notifyDestroyed();
    }

    private void openRMSAndLoadData() {
        try {
            rs = RecordStore.openRecordStore("Options",true);

        if (rs.getNumRecords() > 0) {
            //deserialize first record
            byte[] data = rs.getRecord(1);
            DataInputStream din = new DataInputStream(new ByteArrayInputStream(data));
            try {
                //load profiles
                ColorProfile currentProfile_ = ColorProfile.findProfileByName(din.readUTF());
                ColorProfile otherProfile_ = ColorProfile.findProfileByName(din.readUTF());
                bookCanvas.connectProfiles(currentProfile_, otherProfile_);
                bookCanvas.currentProfile = currentProfile_;

                //load fonts
                bookCanvas.currentFontSizeIndex = din.readByte();

                //load last book open
                lastBookURL = din.readUTF();

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        } catch (RecordStoreException rse) {
            //no saving is possible
            rse.printStackTrace();
        }
    }

    private void saveOptionsToRMS() {
        //If bookCanvas has been opened AT ALL
        if (bookCanvas.bookOpen()) {
                try {
                //serialize first record
                ByteArrayOutputStream boas = new ByteArrayOutputStream();
                DataOutputStream dout = new DataOutputStream(boas);
                try {
                    //save profiles
                    dout.writeUTF(bookCanvas.currentProfile.name);
                    dout.writeUTF(bookCanvas.currentProfile.next.name);

                    //save fonts
                    dout.writeByte(bookCanvas.currentFontSizeIndex);

                    //save last book open
                    dout.writeUTF(lastBookURL);

                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }

                byte[] data = boas.toByteArray();

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
}