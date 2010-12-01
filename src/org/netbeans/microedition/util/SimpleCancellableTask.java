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
 * SimpleCancellableTask.java
 *
 * Created on August 15, 2005, 2:25 PM
 *
 */

package org.netbeans.microedition.util;

/**
 * A simple implementation of <code>CancellableTask</code>. This implementation uses a supplied
 * Runnable object, which is being run when this task starts.
 * @author breh
 */
public class SimpleCancellableTask implements CancellableTask {
    
    private Executable executable;
    private Throwable caughtThrowable;
    
    
    /**
     * Creates a new instance of SimpleCancellableTask
     */
    public SimpleCancellableTask() {
    }
    
    
    /**
     * Creates a new instance of SimpleCancellableTask with supplied executable
     * object
     * @param executable Executable to be used for execution.
     */
    public SimpleCancellableTask(Executable executable) {
        this.executable = executable;
    }
    
    /**
     * Sets the executable object for this task. Also resets the failure message
     * and the failure state.
     *
     * @param executable Executable to be used for execution.
     */
    public void setExecutable(Executable executable) {
        caughtThrowable = null;
        this.executable = executable;
    }
    
    /**
     * Cancel this task. In this implementation this method does not cancel the runnable
     * task, this it always returns false.
     * @return always returns false
     */
    public boolean cancel() {
        // cancel does nothing in this simple implementation - always return false
        return false;
    }
    
    /**
     * Gets the failure message of the failed task. Since this implementation considers
     * as a failure an exception from the Runnable object (more exactly <code>run()</code>
     * method), this methods returns a message from this exception.
     * @return Message from failure exception
     */
    public String getFailureMessage() {
        if (caughtThrowable != null) {
            return caughtThrowable.getMessage();
        } else {
            return null;
        }
    }
    
    /**
     * Checks whether the task has failed. In this implementation this means
     * the the <code>execute()</code> method of the supplied <code>Executable</code> object has
     * thrown an exception.
     * @return true when the task has failed.
     */
    public boolean hasFailed() {
        return caughtThrowable != null;
    }
    
    /**
     * Implementation of run method. This method basically calls <code>execute()</code> method
     * from the suplied <code>Executable</code> object.
     */
    public void run() {
        caughtThrowable = null;
        if (executable != null) {
            try {
                executable.execute();
            } catch (Throwable t) {
                caughtThrowable = t;
            }
        }
    }
    
    
    
}
