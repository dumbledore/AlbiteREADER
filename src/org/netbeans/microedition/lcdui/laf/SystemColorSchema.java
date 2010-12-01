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
 * SystemColorSchema.java
 *
 * Created on July 21, 2006, 11:39 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.microedition.lcdui.laf;

import java.util.Hashtable;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * System color schema - gets all colors from Display.getColor() method call.
 * @author breh
 */
public class SystemColorSchema extends ColorSchema {
    
    private static Hashtable /*<display,SystemColorSchema>*/ systemColorSchemas = new Hashtable(1); // usually one display is in charge
    
    private Display display;
    
    /**
     * Creates a new instance of SystemColorSchema
     */
    private SystemColorSchema(Display display) {        
        this.display = display;
    }

    /**
     * Gets SystemColorSchema for given display 
     */
    public static  SystemColorSchema getForDisplay(Display display) {
        if (display == null) throw new IllegalArgumentException("Display parameter cannot be null");
        SystemColorSchema schema = (SystemColorSchema)systemColorSchemas.get(display);
        if (schema == null) {
            schema = new SystemColorSchema(display);
            systemColorSchemas.put(display,schema);            
        }
        return schema;
    }
    
    
    public int getColor(int aColorSpecifier) {
        return display.getColor(aColorSpecifier);
    }

    public Image getBackgroundImage() {
        return null;
    }

    public int getBackgroundImageAnchorPoint() {
        return Graphics.TOP | Graphics.LEFT;
    }

    public boolean isBackgroundImageTiled() {
        return false;
    }

    public boolean isBackgroundTransparent() {
        return false;
    }
    
    
    
}
