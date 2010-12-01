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
 * SplashScreen.java
 *
 * Created on August 26, 2005, 10:19 AM
 */

package org.netbeans.microedition.lcdui;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;

/**
 * This component represents a splash screen, which is usually being displayed
 * when the application starts. It waits for a specified amount of time (by default
 * 5000 milliseconds) and then calls specified command listener commandAction method
 * with DISMISS_COMMAND as command parameter.
 * <p/>
 * This version is using CommandListener and static Command pattern, but is still
 * compatible with older version. So if there is no command listener specified,
 * it still can use setNextDisplayable() method to specify the dismiss screen and
 * automatically switch to it.
 * @author breh
 */
public class SplashScreen extends AbstractInfoScreen {

        
    /**
     * Command fired when the screen is about to be dismissed
     */
    public static final Command DISMISS_COMMAND = new Command("Dismiss",Command.OK,0);
    
    /**
     * Timeout value which wait forever. Value is "0".
     */
    public static final int FOREVER = 0;
    
    private static final int DEFAULT_TIMEOUT = 5000;
    
    private int timeout = DEFAULT_TIMEOUT;
    private boolean allowTimeoutInterrupt = true;
    
    private long currentDisplayTimestamp;
    
    /**
     * Creates a new instance of SplashScreen
     * @param display display - cannot be null
     * @throws java.lang.IllegalArgumentException when the display parameter is null
     */
    public SplashScreen(Display display) throws IllegalArgumentException  {
        super(display);
    }
    
    // properties
    
    
    /**
     * Sets the timeout of the splash screen - i.e. the time in milliseconds for
     * how long the splash screen is going to be shown on the display.
     * <p/>
     * If the supplied timeout is 0, then the splashscreen waits forever (it needs to
     * be dismissed by pressing a key)
     *
     * @param timeout in milliseconds
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    
    /**
     * Gets current timeout of the splash screen
     *
     * @return timeout value
     */
    public int getTimeout() {
        return timeout;
    }
    
    /**
     * When set to true, the splashscreen timeout can be interrupted 
     * (and thus dismissed) by pressing a key.
     *
     * @param allow true if the user can interrupt the screen, false if the user need to wait
     * until timeout.
     */
    public void setAllowTimeoutInterrupt(boolean allow) {
        this.allowTimeoutInterrupt = allow;
    }
    
    /**
     * Can be the splashscreen interrupted (dismissed) by the user pressing a key?
     * @return true if user can interrupt it, false otherwise
     */
    public boolean isAllowTimeoutInterrupt() {
        return allowTimeoutInterrupt;
    }
    
    
    // canvas methods
    
    /**
     * keyPressed callback
     * @param keyCode
     */
    protected void keyPressed(int keyCode) {
        if (allowTimeoutInterrupt) {
            doDismiss();
        }
    }
    
    
    /**
     * pointerPressed callback
     * @param x
     * @param y
     */
    protected void pointerPressed(int x, int y) {
        if (allowTimeoutInterrupt) {
            doDismiss();
        }
    }
    
    /**
     * starts the coundown of the timeout
     */
    protected void showNotify() {
        super.showNotify();
        // start watchdog task - only when applicable
        currentDisplayTimestamp = System.currentTimeMillis();
        if (timeout > 0) {
            Watchdog w = new Watchdog(timeout, currentDisplayTimestamp);
            w.start();
        }
    }
    
    
    protected void hideNotify() {
        super.hideNotify();
        currentDisplayTimestamp = System.currentTimeMillis();
    }
    
    
    
    // private stuff
    
    private void doDismiss() {
        CommandListener commandListener = getCommandListener();
        if (commandListener == null) {
            switchToNextDisplayable(); // @deprecated - works only if 
                                       // appropriate setters were called and no command listener 
                                       // was assigned to this component
        } else {
            commandListener.commandAction(DISMISS_COMMAND,this);
        }
    }
    
    
    
    private class Watchdog extends Thread {
        
        private int timeout;
        private long currentDisplayTimestamp;
        
        private Watchdog(int timeout, long currentDisplayTimestamp) {
            this.timeout = timeout;
            this.currentDisplayTimestamp = currentDisplayTimestamp;
        }
        
        public void run() {
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException ie) {
            }
            // doDismiss (only if current display timout matches) - this means this
            // splash screen is still being shown on the display
            if (this.currentDisplayTimestamp == SplashScreen.this.currentDisplayTimestamp) {
                doDismiss();
            }
        }
        
        
    }
    

}
