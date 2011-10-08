/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */


package org.netbeans.microedition.lcdui.pda;

import java.util.*;
import java.io.*;
import javax.microedition.io.*;
import javax.microedition.io.file.*;
import javax.microedition.lcdui.*;
import org.albite.albite.AlbiteMIDlet;
import org.albite.book.model.book.Book;
//#if !((defined(LightMode) || defined(TinyMode) || defined(LightModeExport) || defined(TinyModeExport)))
import org.albite.lang.AlbiteCharacter;
//#endif
/**
 * The <code>FileBrowser</code> custom component lets the user list files and
 * directories. It's uses FileConnection Optional Package (JSR 75). The FileConnection
 * Optional Package APIs give J2ME devices access to file systems residing on mobile devices,
 * primarily access to removable storage media such as external memory cards.
 * @author breh
 */

public class FileBrowser extends List implements CommandListener {

    /**
     * Command fired on file selection.
     */
    public static final Command SELECT_FILE_COMMAND = new Command("Select", Command.OK, 1);

    private String currDirName;
    private String currFile;
    private Image dirIcon;
    private Image fileIcon;
    private Image[] iconList;
    private CommandListener commandListener;

    /* special string denotes upper directory */
    private static final String UP_DIRECTORY = "..";

    /* special string that denotes upper directory accessible by this browser.
     * this virtual directory contains all roots.
     */
    private static final String MEGA_ROOT = "/";

    /* separator string as defined by FC specification */
    private static final String SEP_STR = "/";

    /* separator character as defined by FC specification */
    private static final char SEP = '/';

    private Display display;

    private String selectedURL;

    private String filter = null;

    private String title;

    /**
     * Creates a new instance of FileBrowser for given <code>Display</code> object.
     * @param display non null display object.
     */
    public FileBrowser(final Display display) {
        super("", IMPLICIT);
        currDirName = MEGA_ROOT;
        this.display = display;
        super.setCommandListener(this);
        setSelectCommand(SELECT_FILE_COMMAND);
        try {
            dirIcon = Image.createImage("/org/netbeans/microedition/resources/dir.png");
        } catch (IOException e) {
            dirIcon = null;
        }
        try {
            fileIcon = Image.createImage("/org/netbeans/microedition/resources/file.png");
        } catch (IOException e) {
            fileIcon = null;
        }
        iconList = new Image[]{fileIcon, dirIcon};

        showDir();
    }

    /**
     * Sets up an initial directory, i.e. possibly different from MEGA_ROOT
     */
    public void setDir(final String dir) {
        if (dir != null) {
            try {
                FileConnection currDir =
                        (FileConnection) Connector.open(
                        "file:///" + dir, Connector.READ);
                try {
                    if (currDir.exists() && currDir.isDirectory()) {
                        currDirName = dir;
                        showDir();
                    }
                } finally {
                    currDir.close();
                }
            } catch (IOException ioe) {
                //#debug
                AlbiteMIDlet.LOGGER.log(ioe);
            }
        }
    }

    private void showDir() {
//        new Thread(new Runnable() {

//            public void run() {
                try {
                    showCurrDir();
                } catch (SecurityException e) {
                    Alert alert = new Alert("Error", "You are not authorized to access the restricted API", null, AlertType.ERROR);
                    alert.setTimeout(2000);
                    display.setCurrent(alert, FileBrowser.this);
                } catch (Exception e) {
                    //#debug
                    AlbiteMIDlet.LOGGER.log(e);
                }
//            }
//        }).start();
    }

    /**
     * Indicates that a command event has occurred on Displayable d.
     * @param c a <code>Command</code> object identifying the command. This is either
     * one of the applications have been added to <code>Displayable</code> with <code>addCommand(Command)</code>
     * or is the implicit <code>SELECT_COMMAND</code> of List.
     * @param d the <code>Displayable</code> on which this event has occurred
     */
    public final void commandAction(final Command c, final Displayable d) {
        if (c.equals(SELECT_FILE_COMMAND)) {
            List curr = (List) d;
            currFile = curr.getString(curr.getSelectedIndex());
//            new Thread(new Runnable() {
//
//                public void run() {
                    if (currFile.endsWith(SEP_STR) || currFile.equals(UP_DIRECTORY)) {
                        openDir(currFile);
                    } else {
                        //switch To Next
                        doDismiss();
                    }
//                }
//            }).start();
        } else {
            commandListener.commandAction(c, d);
        }
    }

    /**
     * Sets component's title.
     *  @param title component's title.
     */
    public final void setTitle(final String title) {
        this.title = title;
        super.setTitle(title);
    }

    /**
     * Show file list in the current directory .
     */
    private void showCurrDir() {
        if (title == null) {
            super.setTitle(currDirName);
        }
        Enumeration e = null;
        FileConnection currDir = null;

        deleteAll();
        if (MEGA_ROOT.equals(currDirName)) {
//            append(UP_DIRECTORY, dirIcon);
            e = FileSystemRegistry.listRoots();
        } else {
            try {
                currDir =
                        (FileConnection) Connector.open(
                        "file:///" + currDirName, Connector.READ);
                e = currDir.list();
            } catch (IOException ioe) {
                //#debug
                AlbiteMIDlet.LOGGER.log(ioe);
            }
            append(UP_DIRECTORY, dirIcon);
        }

        if (e == null) {
            try {
                currDir.close();
            } catch (IOException ioe) {
                //#debug
                AlbiteMIDlet.LOGGER.log(ioe);
            }
            return;
        }

        final Vector directoriesVector = new Vector();
        final Vector filesVector = new Vector();

        while (e.hasMoreElements()) {
            String fileName = (String) e.nextElement();
            if (fileName.charAt(fileName.length() - 1) == SEP) {
                // This is directory
                directoriesVector.addElement(fileName);
            } else {
                // this is regular file
                boolean append = false;

                final String fileNameLW = fileName.toLowerCase();

                for (int i = 0; i < Book.SUPPORTED_BOOK_EXTENSIONS.length; i++){
                    if (fileNameLW.endsWith(Book.SUPPORTED_BOOK_EXTENSIONS[i])) {
                        append = true;
                        break;
                    }
                }

                if (append) {
                    filesVector.addElement(fileName);
                }
            }
        }

        if (!directoriesVector.isEmpty()) {
            final String[] directories = new String[directoriesVector.size()];
            directoriesVector.copyInto(directories);
            sortStringArray(directories);
            for (int i = 0; i < directories.length; i++) {
                append(directories[i], dirIcon);
            }
        }

        if (!filesVector.isEmpty()) {
            final String[] files = new String[filesVector.size()];
            filesVector.copyInto(files);
            sortStringArray(files);
            for (int i = 0; i < files.length; i++) {
                append(files[i], fileIcon);
            }
        }

        if (currDir != null) {
            try {
                currDir.close();
            } catch (IOException ioe) {
                //#debug
                AlbiteMIDlet.LOGGER.log(ioe);
            }
        }
    }

    private void openDir(final String fileName) {
        /* In case of directory just change the current directory
         * and show it
         */
        if (currDirName.equals(MEGA_ROOT)) {
            if (fileName.equals(UP_DIRECTORY)) {
                // can not go up from MEGA_ROOT
                return;
            }
            currDirName = fileName;
        } else if (fileName.equals(UP_DIRECTORY)) {
            // Go up one directory
            // TODO use setFileConnection when implemented
            int i = currDirName.lastIndexOf(SEP, currDirName.length() - 2);
            if (i != -1) {
                currDirName = currDirName.substring(0, i + 1);
            } else {
                currDirName = MEGA_ROOT;
            }
        } else {
            currDirName += fileName;
        }
        showDir();
    }

    /**
     * Returns selected file as a <code>FileConnection</code> object.
     * @return non null <code>FileConection</code> object
     */
    public final FileConnection getSelectedFile() throws IOException {
        FileConnection fileConnection =
                (FileConnection) Connector.open(selectedURL);
        return fileConnection;
    }

    /**
     * Returns selected <code>FileURL</code> object.
     * @return non null <code>FileURL</code> object
     */
    public final String getSelectedFileURL() {
        return selectedURL;
    }

    /**
     * Sets the file filter.
     * @param filter file filter String object
     */
    public final void setFilter(final String filter) {
        this.filter = filter;
    }

    /**
     * Returns command listener.
     * @return non null <code>CommandListener</code> object
     */
    protected final CommandListener getCommandListener() {
        return commandListener;
    }

    /**
     * Sets command listener to this component.
     * @param commandListener <code>CommandListener</code> to be used
     */
    public final void setCommandListener(
            final CommandListener commandListener) {

        this.commandListener = commandListener;
    }

    private void doDismiss() {
        selectedURL = "file:///" + currDirName + currFile;
        CommandListener commandListener = getCommandListener();
        if (commandListener != null) {
            commandListener.commandAction(SELECT_FILE_COMMAND, this);
        }
    }

    protected static void sortStringArray(final String[] strings) {
        /*
         * If one wants useful results one should be comparing lowercase letters
         */
        final String[] lowercaseStrings = new String[strings.length];
        for (int i = 0; i < strings.length; i++) {
            //#if (defined(LightMode) || defined(TinyMode) || defined(LightModeExport) || defined(TinyModeExport))
//#             lowercaseStrings[i] = strings[i].toLowerCase();
            //#else
            lowercaseStrings[i] = AlbiteCharacter.toLowerCase(strings[i]);
            //#endif
        }

        int n = strings.length;
        String temp;

        for(int i = 0; i < n; i++){
            for(int j = 1; j < (n-i); j++){
                if(lowercaseStrings[j - 1].compareTo(lowercaseStrings[j]) > 0 ){
                    //swap the elements!
                    temp = strings[j - 1];
                    strings[j - 1] = strings[j];
                    strings[j] = temp;

                    temp = lowercaseStrings[j - 1];
                    lowercaseStrings[j - 1] = lowercaseStrings[j];
                    lowercaseStrings[j] = temp;
                }
            }
        }
    }

//    protected static void sortStringArray(final String[] strings) {
//
//    int newLowest = 0;            // index of first comparison
//    int newHighest = strings.length-1;  // index of last comparison
//
//    while (newLowest < newHighest) {
//        int highest = newHighest;
//        int lowest  = newLowest;
//        newLowest = strings.length;    // start higher than any legal index
//        for (int i=lowest; i<highest; i++) {
//            if (strings[i].compareTo(strings[i + 1]) > 0) {
//               // exchange elements
//               String temp = strings[i];
//               strings[i] = strings[i+1];
//               strings[i+1] = temp;
//               if (i<newLowest) {
//                   newLowest = i-1;
//                   if (newLowest < 0) {
//                       newLowest = 0;
//                   }
//               } else if (i>newHighest) {
//                   newHighest = i+1;
//               }
//            }
//        }
//    }
//    }
}