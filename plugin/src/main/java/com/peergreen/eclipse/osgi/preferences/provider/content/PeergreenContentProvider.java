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
package com.peergreen.eclipse.osgi.preferences.provider.content;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.peergreen.eclipse.osgi.preferences.PeergreenServerEntry;
import com.peergreen.eclipse.osgi.preferences.ServersConfiguration;

/**
 * Provider of the table view.
 * @author Florent Benoit
 */
public class PeergreenContentProvider implements IStructuredContentProvider {

    @Override
    public void dispose() {
        // nothing
    }

    @Override
    public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
        // nothing
    }


    @Override
    public Object[] getElements(Object arg0) {
        ServersConfiguration serversConfiguration = (ServersConfiguration) arg0;

        // Convert list into array
        return ((List<PeergreenServerEntry>)serversConfiguration.getEntries()).toArray();
    }

}
