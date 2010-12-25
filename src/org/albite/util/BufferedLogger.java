//#condition DebugLevel != "off"
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.util;

import java.util.Vector;

/**
 *
 * @author albus
 */
public class BufferedLogger extends Logger {
    private Vector messages = new Vector(100);

    private class Message {
        final boolean error;
        final String msg;

        Message(final String msg, final boolean error) {
            this.msg = msg;
            this.error = error;
        }
    }

    public BufferedLogger() {
        super();
    }

    public BufferedLogger(final Logger logger) {
        super(logger);
    }

    protected final void logInternally(final String message, final boolean error) {
        messages.addElement(new Message(message, error));
    }

    protected final void logInternally(Throwable t) {
        messages.addElement(new Message(t.getClass() + ": " + t.getMessage(), true));
    }

    public final String getMessages(
            final boolean errorOnly, final boolean ascending) {

        StringBuffer sb = new StringBuffer();
        Message m;

        final int end = messages.size() - 1;

        int i = (ascending ? 0 : end - 1);
        while (
                (ascending && i <= end) ||
                (!ascending && i >= 0)
                ) {

            m = (Message) messages.elementAt(i);
            if (!errorOnly || m.error) {
                sb.append(m.msg);
                sb.append('\n');
            }

            if (ascending) {
                i++;
            } else {
                i--;
            }
        }

        return sb.toString();
    }

    public final boolean isEmpty() {
        return messages.isEmpty();
    }
}
