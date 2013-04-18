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


import java.io.Writer;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.peergreen.eclipse.osgi.preferences.PeergreenServerEntry;
import com.peergreen.eclipse.osgi.preferences.ServersConfiguration;

/**
 * Writes the server configuration on a Writer.
 * @author Florent Benoit
 */
public class ServersConfigurationWriter {

    public ServersConfigurationWriter() {
    }



    public void write(ServersConfiguration serversConfiguration, Writer writer) throws ParsingException {
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        XMLStreamWriter xmlStreamWriter;
        try {
            xmlStreamWriter = outputFactory.createXMLStreamWriter(writer);
        } catch (XMLStreamException e) {
            throw new ParsingException("Unable to write document", e);
        }

        try {
            xmlStreamWriter.writeStartDocument();
        } catch (XMLStreamException e) {
            throw new ParsingException("Unable to write document", e);
        }
        try {
            xmlStreamWriter.writeStartElement("peergreen-server");
        } catch (XMLStreamException e) {
            throw new ParsingException("Unable to write document", e);
        }
        //xmlStreamWriter.writeDefaultNamespace(StAXArtifactModelPersistence.PG_NAMESPACE_URI);
        for (PeergreenServerEntry entry : serversConfiguration.getEntries()) {
            try {
                writeEntry(xmlStreamWriter, entry, entry.equals(serversConfiguration.getDefaultEntry()));
            } catch (XMLStreamException e) {
                throw new ParsingException("Unable to write document", e);

            }
        }
        try {
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeEndDocument();
        } catch (XMLStreamException e) {
            throw new ParsingException("Unable to write document", e);
        }

    }

    protected void writeEntry(XMLStreamWriter xmlStreamWriter, PeergreenServerEntry entry, boolean isDefault) throws XMLStreamException {

        xmlStreamWriter.writeStartElement("entry");
        xmlStreamWriter.writeAttribute("name", entry.getName());
        xmlStreamWriter.writeAttribute("version", entry.getVersion());
        xmlStreamWriter.writeAttribute("localPath", entry.getLocalPath().getPath());
        if (isDefault) {
            xmlStreamWriter.writeAttribute("isDefault", "true");
        }
        xmlStreamWriter.writeEndElement();
    }
}
