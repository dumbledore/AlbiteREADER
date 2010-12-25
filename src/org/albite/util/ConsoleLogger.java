//#condition DebugLevel != "off"
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.util;

/**
 *
 * @author albus
 */
public class ConsoleLogger extends Logger {

    public ConsoleLogger() {
        super();
    }

    public ConsoleLogger(final Logger logger) {
        super(logger);
    }

    protected final void logInternally(final String message, final boolean error) {
        if (error) {
            System.err.println(message);
        } else {
            System.out.println(message);
        }
    }

    protected final void logInternally(final Throwable t) {
        t.printStackTrace();
    }
}
