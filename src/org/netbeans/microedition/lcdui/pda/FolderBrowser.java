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

/**
 * The <code>FileBrowser</code> custom component lets the user list files and
 * directories. It's uses FileConnection Optional Package (JSR 75). The FileConnection
 * Optional Package APIs give J2ME devices access to file systems residing on mobile devices,
 * primarily access to removable storage media such as external memory cards.
 * @author breh
 */

public class FolderBrowser extends List implements CommandListener {

    /**
     * Command fired on file selection.
     */
    public static final Command SELECT_FOLDER_COMMAND =
            new Command("Select", Command.OK, 1);

    private String currDirName;
    
    private Image dirIcon;
    private Image okIcon;

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

    private String title;

    /**
     * Creates a new instance of FileBrowser for given <code>Display</code> object.
     * @param display non null display object.
     */
    public FolderBrowser(Display display) {
        super("", IMPLICIT);
        currDirName = MEGA_ROOT;
        this.display = display;
        super.setCommandListener(this);
        setSelectCommand(SELECT_FOLDER_COMMAND);
        try {
            dirIcon = Image.createImage("/org/netbeans/microedition/resources/dir.png");
        } catch (IOException e) {
            dirIcon = null;
        }
        try {
            okIcon = Image.createImage("/org/netbeans/microedition/resources/icon-ok.png");
        } catch (IOException e) {
            okIcon = null;
        }
        showDir();
    }

    private void showDir() {
        new Thread(new Runnable() {

            public void run() {
                try {
                    showCurrDir();
                } catch (SecurityException e) {
                    Alert alert = new Alert("Error", "You are not authorized to access the restricted API", null, AlertType.ERROR);
                    alert.setTimeout(2000);
                    display.setCurrent(alert, FolderBrowser.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Indicates that a command event has occurred on Displayable d.
     * @param c a <code>Command</code> object identifying the command. This is either
     * one of the applications have been added to <code>Displayable</code> with <code>addCommand(Command)</code>
     * or is the implicit <code>SELECT_COMMAND</code> of List.
     * @param d the <code>Displayable</code> on which this event has occurred
     */
    public void commandAction(Command c, Displayable d) {
        if (c == SELECT_FOLDER_COMMAND) {
            List curr = (List) d;
            final String selectedFolder =
                    curr.getString(curr.getSelectedIndex());

            new Thread(new Runnable() {
                public void run() {
                    if (selectedFolder.endsWith(SEP_STR) || selectedFolder.equals(UP_DIRECTORY)) {
                        openDir(selectedFolder);
                    } else {
                        //switch To Next
                        doDismiss();
                    }
                }
            }).start();
        } else {
            commandListener.commandAction(c, d);
        }
    }

    /**
     * Sets component's title.
     *  @param title component's title.
     */
    public void setTitle(String title) {
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
            e = FileSystemRegistry.listRoots();
        } else {
            try {
                currDir = (FileConnection) Connector.open("file:///" + currDirName);
                e = currDir.list();
            } catch (IOException ioe) {}
            append(UP_DIRECTORY, dirIcon);
        }

        if (e == null) {
            try {
                currDir.close();
            } catch (IOException ioe) {}
            return;
        }

        while (e.hasMoreElements()) {
            String fileName = (String) e.nextElement();
            if (fileName.charAt(fileName.length() - 1) == SEP) {
                // This is directory
                append(fileName, dirIcon);
            }
        }

        if (!MEGA_ROOT.equals(currDirName)) {
            append("Use current directory.", okIcon);
        }

        if (currDir != null) {
            try {
                currDir.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    private void openDir(String fileName) {
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
            currDirName = currDirName + fileName;
        }
        showDir();
    }

    /**
     * Returns selected <code>FileURL</code> object.
     * @return non null <code>FileURL</code> object
     */
    public String getSelectedFolderURL() {
        return selectedURL;
    }

    /**
     * Returns command listener.
     * @return non null <code>CommandListener</code> object
     */
    protected CommandListener getCommandListener() {
        return commandListener;
    }

    /**
     * Sets command listener to this component.
     * @param commandListener <code>CommandListener</code> to be used
     */
    public void setCommandListener(CommandListener commandListener) {
        this.commandListener = commandListener;
    }

    private void doDismiss() {
        selectedURL = "file:///" + currDirName;
        CommandListener commandListener = getCommandListener();
        if (commandListener != null) {
            commandListener.commandAction(SELECT_FOLDER_COMMAND, this);
        }
    }
}
