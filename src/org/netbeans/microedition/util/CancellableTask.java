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
 * CancellableTask.java
 *
 * Created on August 12, 2005, 12:01 PM
 *
 */

package org.netbeans.microedition.util;

/**
 * A CancellableTask object is used in <code>WaitScreen</code> component to be run in
 * the background.
 * @author breh
 */
public interface CancellableTask extends Runnable {
	
	/**
	 * Advises to interrupt the run method and cancel it's task. It's the task
	 * responsibility to implement the cancel method in a cooperative manner.
	 *
	 * @return true if the task was successfully cancelled, false otherwise
	 */
	public boolean cancel();
	
	/**
	 * Informs whether the task run was not successfull. For example when an
	 * exception was thrown in the task code.
	 * @return true if the task did not finish correctly. False if everything was ok.
	 */
	public boolean hasFailed();
	
	
	/**
	 * Gets the reason for the failure. In the case there was not any failure, this method should return null.
	 * @return A descriptive message of the failuire or null if there was no failure.
	 */
	public String getFailureMessage();
}
