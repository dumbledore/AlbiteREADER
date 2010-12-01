/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt. 
  * 
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */ 

/*
 * WaitScreen.java
 *
 * Created on August 11, 2005, 1:33 PM
 *
 */

package org.netbeans.microedition.lcdui;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import org.netbeans.microedition.util.CancellableTask;

/**
 * This component suits as a wait screen, which let the user to execute a blocking
 * background task (e.g. a network communication) and waits for it until finished.
 * <p/>
 * The background task is being started immediately prior the component is being
 * shown on the screen.
 * <p/>
 * When the background task is finished, this component calls commandAction method
 * on assigned CommandListener object. In the case of success, the commandAction method
 * is called with SUCCESS_COMMAND as parameter, in the case of failure, the commandAction
 * method is called with FAILURE_COMMAND as parameter.
 * <p/>
 * The functionality from previous version, where the component automatically swiches
 * to another <code>Displayable</code> objects is still available, but has beeen 
 * deprecated.
 * <p/>
 * The deprecated behavior is the following - in the case of success (i.e. the
 * task finished successfully), it switches to displayable(s) supplied by
 * <code>setNextDisplayable()</code> methods, in the case of failure, it switched to displaybles(s)
 * supplied by <code>setFailureDisplayable()</code> methods. In the case there is not
 * set failure displayable, <code>WaitScreen</code> even in the
 * case of failure switches to displayables specified by the <code>setNextDisplayable()</code>
 * method. In the case there is even no next displayable specified, after the
 * task is finished, <code>WaitScreen</code> switches back to the screen it was
 * previously visible on the display.
 *
 * @author breh
 */
public class WaitScreen extends AbstractInfoScreen {
    
    private CancellableTask task = null;
    private Thread backgroundExecutor = null;
    
    private Displayable failureDisplayble;
    private Alert failureAlert;
    
    /**
     * Command fired when the background task was finished succesfully
     */
    public static final Command SUCCESS_COMMAND = new Command("Success",Command.OK,0);
    
     /**
     * Command fired when the background task failed (threw exception)
     */
    public static final Command FAILURE_COMMAND = new Command("Failure",Command.OK,0);    
    
    
    /**
     * Creates a new instance of WaitScreen for given <code>Display</code> object.
     * @param display A non-null display object.
     * @throws IllegalArgumentException when the supplied argument is null
     */
    public WaitScreen(Display display) throws IllegalArgumentException {
        super(display);
    }
    
    
    /**
     * Sets the displayable to be used to switch to in the case of the background
     * task failure.
     * @param failureDisplayble displayable, or null if the component should switch to <code>nextDisplayable</code>
     * even in the case of failure.
     *
     * @deprecated - use FAILURE_COMMAND in CommandListener.commandAction() to handle failure event
     */
    public void setFailureDisplayable(Displayable failureDisplayble) {
        this.failureDisplayble = failureDisplayble;
    }
    
    
    /**
     * Requests that the specified Alert is going to be shown in the case of
     * failure, and failureDisplayable be made current after the Alert is dismissed.
     *  <p/>
     * The failureDisplayable parameter cannot be Alert and in the case
     * failureAlert is not null, it also cannot be null.
     * @param failureAlert alert to be shown, or null if the component should always switch to nextDisplayable
     * @param failureDisplayble a displayable to be shown after the alert is being dismissed. This displayable
     * cannot be null if the <code>failureAlert</code> is not null and it also cannot be
     * Alert.
     * @throws java.lang.IllegalArgumentException If the failureAlert is not null and failureDisplay is null at the same time, or
     * if the failureDisplayable is instance of <code>Alert</code>
     *
     * @deprecated - use FAILURE_COMMAND in CommandListener.commandAction() to handle failure event
     */
    public void setFailureDisplayable(Alert failureAlert, Displayable failureDisplayble) throws IllegalArgumentException  {
        if ((failureAlert != null) && (failureDisplayble == null))
            throw new IllegalArgumentException("A failureDisplayable parameter cannot be null if the failureAlert parameter is not null.");
        if (failureDisplayble instanceof Alert)
            throw new IllegalArgumentException("failureDisplayable paremter cannot be Alert.");
        this.failureAlert = failureAlert;
        this.failureDisplayble  = failureDisplayble;
    }
    
    
    /**
     * Sets the task to be run on the background.
     * @param task task to be executed
     */
    public void setTask(CancellableTask task) {
        this.task = task;
    }
    
    
    /**
     * Gets the background task.
     * @return task being executed in background while this component is being shown
     * on the screen
     */
    public CancellableTask getTask() {
        return task;
    }
    
    
    /**
     * starts the supplied task
     */
    protected void showNotify() {
        super.showNotify();
        // and start the task
        if (task != null) {
            if (backgroundExecutor == null) {
                backgroundExecutor = new Thread(new BackgroundExecutor(task));
                backgroundExecutor.start();
            }
        }  else {
            // switch to next displayable immediatelly - no task was assigned
            // do it when the task is repainted - on some devices there are 
            // some race-conditions 
            getDisplay().callSerially(new Runnable() {
                public void run() {
                    doAction();
                }
            });
        }
    }
    
    // private stuff    
    // private stuff
    private void doAction() {
        CommandListener cl = getCommandListener();
        if (cl != null) {
            if ((task != null) && task.hasFailed()) {
                cl.commandAction(FAILURE_COMMAND,this);
            } else {
                // task didn't failed - success !!!
                cl.commandAction(SUCCESS_COMMAND,this);
            }
        } else {
            // old behavior - now deprecated
            if ((task != null) && task.hasFailed() && (failureDisplayble != null))  {
                switchToDisplayable(getDisplay(), failureAlert, failureDisplayble);
            } else {
                switchToNextDisplayable();
            }
        }
        
    } 
    
    /**
     * BackgroundExecutor task
     */
    private class BackgroundExecutor implements Runnable {
        
        private CancellableTask task;
        
        public BackgroundExecutor(CancellableTask task)	throws IllegalArgumentException {
            if (task == null) throw new IllegalArgumentException("Task parameter cannot be null");
            this.task = task;
        }
        
        public void run() {
            try {
                task.run();
            } finally {
                getDisplay().callSerially(new Runnable() {
                    public void run() {
                        WaitScreen.this.backgroundExecutor = null;
                        doAction();                
                    }
                });
                
            }
        }
    }
	
}
