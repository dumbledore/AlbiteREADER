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
 * ColorSchema.java
 *
 * Created on July 21, 2006, 11:02 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.microedition.lcdui.laf;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * ColorSchema holds information about colors and background 
 * images to be used when drawing custom components based on Canvas 
 * and CustomItem, which support this ColorSchema mechanism. 
 *
 * @author breh
 */
public abstract class ColorSchema {

    
    /**
     * Gets color based on color specifier. The color specifer 
     * corresponds to values listed in Display class.
     * @param aColorSpecifier - color specifier from Display.COLOR* constnants
     * @return color to be used for given specifier
     * @see javax.microedition.lcdui.Display
     */
    public abstract int getColor(int aColorSpecifier);
    
    
    /**
     * Returns image which should be used as a background. 
     *
     * @return Image to be drawn. If null is returned, no image will be drawn on background
     */
    public abstract Image getBackgroundImage();
    
    /**
     * Gets anchor of the background image. See Graphics class for details
     * @return anchor where the image should be drawn. Might retun 0 if 
     * no image is used or if the image is going to be tiled.
     * @see javax.microedition.lcdui.Graphics
     */
    public abstract int getBackgroundImageAnchorPoint();
    
    
    /**
     * If true, background image should be drawn in tiled. Usefull when 
     * using simple patterns to draw background.
     * @return true when the background image should be tiled, false 
     * if just one image should be drawn based on suggestion of position
     *  by getBackgroundImageAnchorPoint() method
     * @return true if the backround image should be drawn in tiles, false 
     * if just one instance of background image should be drawn.
     */
    public abstract boolean isBackgroundImageTiled();
    
    
    /**
     * If true the background is transparent. This is helpful for some devices,
     * for example when drawing custom items on Nokia Seris 40 feature pack 1,
     * the background does not have to be erased and the custom item looks much 
     * better when transparent
     * @return true when the background should be transparent, false otherwise
     */
    public abstract boolean isBackgroundTransparent();
    
    
    /**
     * Utility method for painting background do given Graphics object (using current
     * clipping area). This method is able to paint background, including image
     * and including tiling the image.
     * @param g - Graphics to be used to draw background.
     * @param includeImage - when true, the implementation also draws a background
     * image if specified, when false, the background image is ignored.
     */
    public void paintBackground(Graphics g, boolean includeImage) {
        if (g == null) throw new IllegalArgumentException("Graphics parameter cannot be null");
        int currentColor = g.getColor();
        final int x = g.getClipX();
        final int y = g.getClipY();
        final int width = g.getClipWidth();
        final int height = g.getClipHeight();
        
        if (!this.isBackgroundTransparent()) {
             // fill the background with background color (only if the background is not transparent)
            g.setColor(this.getColor(Display.COLOR_BACKGROUND));
            g.fillRect(x,y,width,height);
        }        
        
        final Image backgroundImage = this.getBackgroundImage();
        if ((backgroundImage != null) && (includeImage)) {
            if (this.isBackgroundImageTiled()) {
                // compute width/height stuff
                final int imageWidth = backgroundImage.getWidth();
                final int imageHeight = backgroundImage.getHeight();
                final int cx = width/imageWidth;
                final int cy  = height/imageHeight;
                //final int rx = width % imageWidth;
                //final int ry = width % imageHeight;
                System.out.println("CX = "+cx+"CY ="+cy);
                for (int i=0; i <= cx; i++) {
                    for (int j=0; j <= cy; j++) {
                        g.drawImage(backgroundImage,i*imageWidth,j*imageHeight,Graphics.LEFT | Graphics.TOP);
                    }
                }
            } else {
                final int backgroundImageAnchorPoint = this.getBackgroundImageAnchorPoint();
                int ix = x;
                int iy = y;
                ix = (backgroundImageAnchorPoint & Graphics.RIGHT) > 0 ? width : 0;
                ix = (backgroundImageAnchorPoint & Graphics.HCENTER) > 0 ? width / 2 : ix;
                iy = (backgroundImageAnchorPoint & Graphics.BOTTOM) > 0 ? height : 0;
                iy = (backgroundImageAnchorPoint & Graphics.VCENTER) > 0 ? height / 2 : iy;
                g.drawImage(backgroundImage,ix,iy,backgroundImageAnchorPoint);
            }
        } 
        // reset the color
        g.setColor(currentColor);
    }
           
}
