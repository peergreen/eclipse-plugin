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

import java.io.Reader;
import java.util.Iterator;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.peergreen.eclipse.osgi.preferences.PeergreenServerEntry;
import com.peergreen.eclipse.osgi.preferences.ServersConfiguration;

/**
 * Read the server configuration for the preferences
 * @author Florent Benoit
 */
public class ServersConfigurationReader {

    private final ServerEntryParser serverEntryReader;

    public ServersConfigurationReader() {
        this.serverEntryReader = new ServerEntryParser();
    }


    protected ServersConfiguration buildServerConfiguration(XMLStreamReader xmlStreamReader) throws XMLStreamException {

        ServersConfiguration serversConfiguration = new ServersConfiguration();

        // Content of the characters type
        String content = "";

        // parse the feed message
        while(xmlStreamReader.hasNext()) {
            int eventType = xmlStreamReader.next();

            switch (eventType) {

            case START_ELEMENT:
                if ("entry".equals(xmlStreamReader.getLocalName())) {
                    serversConfiguration.addEntry(serverEntryReader.getServerEntry(serversConfiguration, xmlStreamReader));
                }
                break;

            case END_ELEMENT:
                // Ending element

                // reset content for the next element
                content = "";

                // callback
                if ("peergreen-server".equals(xmlStreamReader.getLocalName())) {
                    return serversConfiguration;
                }
            }
        }
        return null;
    }

    public ServersConfiguration parse(Reader reader) throws ParsingException {
        // Create XML Stream reader
        XMLStreamReader xmlStreamReader;
        try {
            xmlStreamReader = XMLInputFactory.newFactory().createXMLStreamReader(reader);
        } catch (XMLStreamException | FactoryConfigurationError e) {
            throw new ParsingException("Unable to parse", e);
        }

        // Read configuration
        ServersConfiguration serversConfiguration = null;
        try {
            serversConfiguration = buildServerConfiguration(xmlStreamReader);
        } catch (XMLStreamException e) {
            throw new ParsingException("Unable to parse", e);
        } finally {
            try {
                xmlStreamReader.close();
            } catch (XMLStreamException e) {
                throw new ParsingException("Unable to parse", e);
            }
        }

        Iterator<PeergreenServerEntry> itEntry = serversConfiguration.getEntries().iterator();
        while (itEntry.hasNext()) {
            PeergreenServerEntry peergreenServerEntry = itEntry.next();
            // File has been removed, remove the entry
            if (peergreenServerEntry.getLocalPath() == null) {
                itEntry.remove();
                if (peergreenServerEntry.equals(serversConfiguration.getDefaultEntry())) {
                    serversConfiguration.setDefaultEntry(null);
                }
            }
        }


        return serversConfiguration;
    }
}
