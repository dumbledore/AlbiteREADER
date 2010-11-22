/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.util;

/**
 *
 * @author albus
 */
public class ErrorReporter {
    private StringBuffer errors = new StringBuffer(2048);

    public final void report(final Throwable e) {
        errors.append(e.getClass().getName());
        errors.append(": ");
        errors.append(e.getMessage());
        errors.append('\n');
    }

    public final String getErrors() {
        if (errors.length() > 0) {
            return errors.toString();
        } else {
            return "No errors.";
        }
    }
}
