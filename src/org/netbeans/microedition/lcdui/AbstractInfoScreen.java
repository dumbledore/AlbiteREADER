/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 * AbstractInfoScreen.java
 *
 * Created on August 26, 2005, 1:53 PM
 *
 */

package org.netbeans.microedition.lcdui;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import org.netbeans.microedition.lcdui.laf.ColorSchema;
import org.netbeans.microedition.lcdui.laf.SystemColorSchema;

/**
 * 
 * An abstract class serving as a parent for SplashScreen and WaitScreen. This class provides
 * the basic visualization of the screen. 
 * 
 * When this screen is displayed, it can display either supplied text or image. The current implementation
 * shows both, text and image, centered in the middle of the display. 
 * 
 * > Please note, in previous version this component automatically switched to displayables
 * specified by setNextDisplayable() method, but this approach has been deprecated in favor
 * of using static command and calling CommandListener's commandAction() method when an action
 * happens. This gives the developer much higher flexibility for processing the action - it
 * is no longer limited to switching to another displayable, but it can do whatever developer wants.
 *
 * @author breh
 */
public abstract class AbstractInfoScreen extends Canvas {
	
    private Display display;
    
    private Image image;
    private String text;
    
    private static Font DEFAULT_TEXT_FONT = Font.getFont(Font.FONT_STATIC_TEXT);
    private Font textFont = DEFAULT_TEXT_FONT;
    
    private Displayable nextDisplayable;
    private Alert nextAlert;
    
    private Displayable previousDisplayable;
    
    private CommandListener commandListener;
    
    private ColorSchema colorSchema;
    
    /**
     * Creates a new instance of AbstractInfoScreen
     * @param display display parameter. Cannot be null
     * @throws java.lang.IllegalArgumentException if the display parameter is null
     */
    public AbstractInfoScreen(Display display) throws IllegalArgumentException  {
        this(display,null);
    }
    
    
    /**
     * Creates a new instance of AbstractInfoScreen
     * @param display display parameter. Cannot be null
     * @param colorSchema color schema to be used for this component. If null, SystemColorSchema is used.
     * @throws java.lang.IllegalArgumentException if the display parameter is null
     */
    public AbstractInfoScreen(Display display, ColorSchema colorSchema) {
        if (display == null) throw new IllegalArgumentException("Display parameter cannot be null."); // NOI18N
        this.display = display;
        setColorSchemaImpl(display,colorSchema);
    }
    
    
    // properties
    
    /**
     * Gets ColorSchema currently in use
     */
    public ColorSchema getColorSchema() {
        return colorSchema;
    }
    
    /**
     * Sets ColorSchema
     */    
    public void setColorSchema(ColorSchema colorSchema) {
        setColorSchemaImpl(getDisplay(),colorSchema);
        repaint();
    }
    
    
    
    /**
     * Sets the text to be painted on the screen.
     * @param text text to be painter, or null if no text should be shown
     */
    public void setText(String text) {
        this.text = text;
        repaint();
    }
    
    /**
     * Gets the text to be painted on the screen.
     * @return text
     */
    public String getText() {
        return this.text;
    }
    
    
    /**
     * Gets the image to be painted on the screen.
     * @return image
     */
    public Image getImage() {
        return image;
    }
    
    /**
     * Sets image to be painted on the screen. If set to null, no image
     * will be painted
     * @param image image to be painted. Can be null.
     */
    public void setImage(Image image) {
        this.image = image;
        repaint();
    }
    
    /**
     * Sets the font to be used to paint the specified text. If set
     * to null, the default font (Font.STATIC_TEXT_FONT) will be used.
     * @param font font to be used to paint the text. May be null.
     */
    public void setTextFont(Font font) {
        if (font != null) {
            this.textFont = font;
        } else {
            this.textFont = DEFAULT_TEXT_FONT;
        }
        repaint();
    }
    
    /**
     * Gets the current font used to paint the text.
     * @return text font
     */
    public Font getTextFont() {
        return textFont;
    }
    
    /**
     * Gets command listener assigned to this displayable
     * @return command listener assigned to this component or null if there is no command listener assigned
     */
    protected final CommandListener getCommandListener() {
        return this.commandListener;
    }
    
    /**
     * Sets command listener to this component
     * @param commandListener - command listener to be used
     */
    public void setCommandListener(CommandListener commandListener) {
        super.setCommandListener(commandListener);
        this.commandListener = commandListener;
    }      
    
    /**
     * 
     * Sets the displayable to be used to switch when the screen is being dismissed.
     *
     * @param nextDisplayable displayable, or null if the component should switch back
     * to the screen from which was displayed prior showing this component.
     * 
     * @deprecated - use static Commands and CommandListener from the actual implementation 
     */
    public void setNextDisplayable(Displayable nextDisplayable) {
        this.nextDisplayable = nextDisplayable;
    }
    
    
    /**
     * Requests that the specified Alert is going to be shown in the case of
     * screen dismiss, and nextDisplayable be made current after the Alert is dismissed.
     *  <p/>
     * The nextDisplayable parameter cannot be Alert and in the case
     * nextAlert is not null, it also cannot be null.
     * @param nextAlert alert to be shown, or null if the component should return back to the original screen
     * @param nextDisplayable a displayable to be shown after the alert is being dismissed. This displayable
     * cannot be null if the <code>nextAlert</code> is not null and it also cannot be
     * Alert.
     * @throws java.lang.IllegalArgumentException If the nextAlert is not null and nextDisplayable is null at the same time, or
     * if the nextDisplayable is instance of <code>Alert</code>
     *
     * @deprecated - use static Commands and CommandListener pattern from the actual implementation class
     */
    public void setNextDisplayable(Alert nextAlert, Displayable nextDisplayable) throws IllegalArgumentException {
        if ((nextAlert != null) && (nextDisplayable == null))
            throw new IllegalArgumentException("A nextDisplayable parameter cannot be null if the nextAlert parameter is not null.");
        if (nextDisplayable instanceof Alert)
            throw new IllegalArgumentException("nextDisplayable paremter cannot be Alert.");
        this.nextAlert = nextAlert;
        this.nextDisplayable  = nextDisplayable;
    }
    
    // protected methods
    
    /**
     * implementation of abstract method
     * @param g
     */
    protected void paint(Graphics g) {
        // paint background based on color schema
        getColorSchema().paintBackground(g,true);
        //g.setColor(getColorSchema().getColor(Display.COLOR_BACKGROUND));
        //g.fillRect(0,0,g.getClipWidth(), g.getClipHeight());
        g.setColor(getColorSchema().getColor(Display. COLOR_FOREGROUND));
        g.setFont(getTextFont());
        final int centerX = g.getClipWidth() / 2 + g.getClipX();
        final int centerY = g.getClipHeight() / 2 + g.getClipY();
        if (image != null) {
            g.drawImage(image, centerX, centerY, Graphics.HCENTER | Graphics.VCENTER);
        }
        if (text != null) {
            g.drawString(text, centerX, centerY, Graphics.HCENTER | Graphics.BASELINE);
        }
    }
    
    /**
     * repaints the screen whem a size has changed.
     */
    protected void sizeChanged(int w, int h) {
        repaint();
    }
    
    
    /**
     * Gets the used display object
     * @return display object
     */
    protected Display getDisplay() {
        return display;
    }
    
    
    /**
     * Gets the next displayable
     * @return next displayable
     * @deprecated - use static Commands and CommandListener pattern from the actual implementation class
     */
    protected Displayable getNextDisplayable() {
        return nextDisplayable;
    }
    
    
    /**
     * gets the next alert
     * @return next alert
     * @deprecated - use static Commands and CommandListener pattern from the actual implementation class
     */
    protected Alert getNextAlert() {
        return nextAlert;
    }
    
    
    /**
     * switch to the next displayable (or alert)
     * @deprecated - use static Commands and CommandListener pattern from the actual implementation class
     */
    protected void switchToNextDisplayable() {
        if (nextDisplayable != null) {
            switchToDisplayable(display, nextAlert, nextDisplayable);
        } else if (previousDisplayable != null) {
            display.setCurrent(previousDisplayable);
        }
    }
    
    /**
     * Switch to the given displayable and alert
     * @param display
     * @param alert
     * @param displayable
     *
     * @deprecated - use SplashScreen.DISMISS_COMMAND or WaitScreen.SUCCESS_COMMAND in CommandListener.commandAction()
     * to handle this event for specific implementation of the info screen.
     */
    protected static void switchToDisplayable(Display display, Alert alert, Displayable displayable) {
        if (displayable != null) {
            if (alert != null) {
                display.setCurrent(alert,displayable);
            } else {
                display.setCurrent(displayable);
            }
        }
    }
    
    /**
     * sets value of previous displayable. Implementation should always
     * call this super implementation when overriding this method
     *
     */
    protected void showNotify() {
        previousDisplayable = getDisplay().getCurrent();
        super.showNotify();
    }
    
    
    /**
     * Sets color schema. If null, creates a new SystemColorSchema based on the display
     */
    private void setColorSchemaImpl(Display display, ColorSchema colorSchema) {
        if (colorSchema != null) {
            this.colorSchema = colorSchema;
        } else {
            this.colorSchema = SystemColorSchema.getForDisplay(display);
        }
    }
    
    

}
