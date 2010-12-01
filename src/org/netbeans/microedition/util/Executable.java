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

package org.netbeans.microedition.util;

/**
 * Interface defining an execute method, which can throw exception. This
 * exception is then used in WaitScreen to determine whether the Executable
 * succeeded or failed.
 *
 *
 * @author breh
 */
public interface Executable {
    

    /**
     * 
     * Method to be executed. By throwing/not throwing exception the method notifies whether the code finished succesfully or whetehr it failed.
     * @throws java.lang.Exception  can throw this exception when the method fails.
     *
     */
    public void execute() throws Exception;
    
    
}
