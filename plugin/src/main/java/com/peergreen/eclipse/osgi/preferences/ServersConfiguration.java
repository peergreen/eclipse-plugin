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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Server configuration
 * @author Florent Benoit
 */
public class ServersConfiguration {

    private PeergreenServerEntry defaultEntry;

    private final List<PeergreenServerEntry> entries;

    public ServersConfiguration() {
        this.entries = new ArrayList<>();
    }

    public void setDefaultEntry(PeergreenServerEntry entry) {
        this.defaultEntry = entry;
    }

    public PeergreenServerEntry getDefaultEntry() {
        return defaultEntry;
    }

    public void addEntry(PeergreenServerEntry peergreenServerEntry) {
        this.entries.add(peergreenServerEntry);
    }

    public void removeEntry(PeergreenServerEntry peergreenServerEntry) {
        if (defaultEntry != null && defaultEntry.equals(peergreenServerEntry)) {
            throw new IllegalStateException("Cannot remove the default entry");
        }
        this.entries.remove(peergreenServerEntry);
    }


    public Collection<PeergreenServerEntry> getEntries() {
        return entries;
    }

    public PeergreenServerEntry searchServerName(String serverName) {
        for (PeergreenServerEntry server : entries) {
            if (server.getName().equals(serverName)) {
                // found it
                return server;
            }
        }

        // not found
        return null;
    }





}
