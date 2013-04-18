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
package com.peergreen.eclipse.osgi.preferences.parser;


import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.io.File;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.peergreen.eclipse.osgi.preferences.PeergreenServerEntry;
import com.peergreen.eclipse.osgi.preferences.ServersConfiguration;

/**
 * Parse the server entry of the XML file.
 * @author Florent Benoit
 */
public class ServerEntryParser {

    protected PeergreenServerEntry getServerEntry(ServersConfiguration serversConfiguration, XMLStreamReader xmlStreamReader) throws XMLStreamException {

        PeergreenServerEntry peergreenServerEntry = null;
        String name = null;
        String version = null;
        String localPath = null;
        boolean isDefault = false;

        int eventType = START_ELEMENT;
        // parse the feed message
        while(xmlStreamReader.hasNext()) {

            switch (eventType) {

            case START_ELEMENT:
                if ("entry".equals(xmlStreamReader.getLocalName())) {
                    name = xmlStreamReader.getAttributeValue(null, "name");
                    version = xmlStreamReader.getAttributeValue(null, "version");
                    localPath = xmlStreamReader.getAttributeValue(null, "localPath");
                    isDefault = Boolean.parseBoolean(xmlStreamReader.getAttributeValue(null, "isDefault"));

                }
                break;

            case END_ELEMENT:
                // Ending element

                // callback
                if ("entry".equals(xmlStreamReader.getLocalName())) {
                    peergreenServerEntry = new PeergreenServerEntry(name, version);
                    if (localPath != null) {
                        peergreenServerEntry.setLocalPath(new File(localPath));
                    }

                    if (isDefault) {
                        serversConfiguration.setDefaultEntry(peergreenServerEntry);
                    }

                    return peergreenServerEntry;
                }
            }
            eventType = xmlStreamReader.next();

        }
        return null;
    }
}
