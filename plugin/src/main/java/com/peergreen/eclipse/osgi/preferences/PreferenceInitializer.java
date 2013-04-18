/**
 * Copyright 2013 Peergreen S.A.S.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.peergreen.eclipse.osgi.preferences;

import static com.peergreen.eclipse.osgi.Activator.PLUGIN_ID;
import static com.peergreen.eclipse.osgi.preferences.PreferenceConstants.SERVER_PATH;
import static com.peergreen.eclipse.osgi.preferences.PreferenceConstants.SERVER_XML_CONTENT;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes.Name;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.peergreen.eclipse.osgi.Activator;

/**
 * Class used to initialize default preference values.
 * @author Florent Benoit
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

    // Search servers in this folder
    public static final String SERVERS_PATH = "/servers/";


	@Override
    public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

        Enumeration<String> serversPath = Activator.getDefault().getBundle().getEntryPaths(SERVERS_PATH);
        if (serversPath == null || !serversPath.hasMoreElements()) {
            throw new IllegalStateException(String.format("Unable to find any peergreen server in '%s' plugin", PLUGIN_ID));
        }

        URL serverURL = null;
        String defaultServerPath = null;
        // Now, add entry for each server
        while (serversPath.hasMoreElements()) {
            String serverPath = serversPath.nextElement();
            try {
                URL url = Activator.getDefault().getBundle().getEntry(serverPath);
                if (url == null) {
                    throw new IllegalStateException(String.format("Unable to find the peergreen server in '%s' plugin", PLUGIN_ID));
                }
                serverURL = FileLocator.toFileURL(FileLocator.resolve(url));
                defaultServerPath = serverURL.getPath();
            } catch (IOException e) {
                throw new IllegalStateException(String.format("Unable to find the peergreen server in '%s' plugin", PLUGIN_ID), e);
            }
            store.setDefault(SERVER_PATH, defaultServerPath);

        }


        // validate the current value
        String path = store.getString(SERVER_PATH);
        if (path != null && !path.equals(defaultServerPath)) {
            File localFile = new File(path);
            // file removed so revert to the default value
            if (!localFile.exists()) {
                store.setValue(SERVER_PATH, defaultServerPath);
            }
        }

        // XM file ?
        String xmlContent = store.getString(SERVER_XML_CONTENT);
        if (xmlContent == null || "".equals(xmlContent)) {
            // initialize default xml

            try (JarFile jarFile = new JarFile(defaultServerPath)) {
                // Check if manifest is OK
                Manifest manifest = jarFile.getManifest();
                String serverName = manifest.getMainAttributes().getValue("Peergreen-Server-Name");
                String version =  manifest.getMainAttributes().getValue(Name.IMPLEMENTATION_VERSION);

                // no name
                if (serverName == null) {
                    throw new IllegalStateException(String.format("Selected file %s is a JAR file but not a valid Peergreen Server", defaultServerPath));
                }

                // no version
                if (version == null) {
                    throw new IllegalStateException(String.format("Unable to find the version of the Peergreen Server %s", defaultServerPath));
                }

                xmlContent = "<peergreen-server><entry name='".concat(serverName).concat("' version='").concat(version).concat("' isDefault='true' localPath='").concat(defaultServerPath).concat("' /></peergreen-server>>");
                        store.setDefault(SERVER_XML_CONTENT, xmlContent);

            } catch (IOException e) {
                throw new IllegalStateException(String.format("Unable to open the jar file %s", defaultServerPath));
            }

        }



		/*String defaultServerPath = null;
		URL serverURL = null;
		try {
		    URL url = Activator.getDefault().getBundle().getEntry(ENTRY);
		    if (url == null) {
		        throw new IllegalStateException(String.format("Unable to find the peergreen server in '%s' plugin", PLUGIN_ID));
		    }
		    serverURL = FileLocator.toFileURL(FileLocator.resolve(url));
		    defaultServerPath = serverURL.getPath();
		} catch (IOException e) {
            throw new IllegalStateException(String.format("Unable to find the peergreen server in '%s' plugin", PLUGIN_ID), e);
		}
		store.setDefault(SERVER_PATH, defaultServerPath);


		// validate the current value
		String path = store.getString(SERVER_PATH);
		if (path != null && !path.equals(defaultServerPath)) {
		    File localFile = new File(path);
		    // file removed so revert to the default value
		    if (!localFile.exists()) {
		        store.setValue(SERVER_PATH, defaultServerPath);
		    }
		}

		// XM file ?
		String xmlContent = store.getString(SERVER_XML_CONTENT);
		if (xmlContent == null || "".equals(xmlContent)) {
		    // initialize default xml
		    xmlContent = "<peergreen-server><entry name='Peergreen Server Light' version='1.0.0-M0' isDefault='true' localPath='".concat(defaultServerPath).concat("' /></peergreen-server>>");
		    store.setDefault(SERVER_XML_CONTENT, xmlContent);
		}*/


	}

}
