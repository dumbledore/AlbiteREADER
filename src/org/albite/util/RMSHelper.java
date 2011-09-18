/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.microedition.midlet.MIDlet;
import javax.microedition.rms.RecordStoreException;

/**
 *
 * @author albus
 */
public class RMSHelper {
    public static void checkValidity(
            final MIDlet app,
            final DataInputStream in) throws IOException, RecordStoreException {

        final String version = app.getAppProperty("MIDlet-Version");

        if (version == null) {
            throw new RecordStoreException();
        }

        final String rmsVersion = in.readUTF();

        if (rmsVersion == null) {
            throw new RecordStoreException();
        }

        if (!rmsVersion.equals(version)) {
            throw new RecordStoreException();
        }
    }

    public static void writeVersionNumber(
            final MIDlet app,
            final DataOutputStream out) throws IOException {

        String version = app.getAppProperty("MIDlet-Version");

        if (version == null) {
            version = "not_specified";
        }

        out.writeUTF(version);
    }
}
