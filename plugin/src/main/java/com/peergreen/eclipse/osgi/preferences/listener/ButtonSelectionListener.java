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
package com.peergreen.eclipse.osgi.preferences.listener;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;

import com.peergreen.eclipse.osgi.Activator;
import com.peergreen.eclipse.osgi.preferences.PeergreenServerEntry;
import com.peergreen.eclipse.osgi.preferences.ServersConfiguration;

/**
 * Super class for the action listener of buttons used in the preferences page.
 * This allows to get some helper methods
 * @author Florent Benoit
 */
public abstract class ButtonSelectionListener extends SelectionAdapter {

        private final ServersConfiguration serversConfiguration;
        private final PeergreenServerEntry peergreenServerEntry;
        private final TableViewer tableViewer;
        private final Button button;

        public ButtonSelectionListener(ServersConfiguration serversConfiguration, PeergreenServerEntry peergreenServerEntry, TableViewer tableViewer) {
            this(serversConfiguration, peergreenServerEntry, tableViewer, null);
        }


        public ButtonSelectionListener(ServersConfiguration serversConfiguration, PeergreenServerEntry peergreenServerEntry, TableViewer tableViewer, Button button) {
            this.serversConfiguration = serversConfiguration;
            this.peergreenServerEntry = peergreenServerEntry;
            this.tableViewer = tableViewer;
            this.button = button;
        }


        protected void refresh() {
            Control[] children = tableViewer.getTable().getChildren();
            for(Control element : children) {
                element.dispose();
            }
            tableViewer.refresh();
        }

        protected PeergreenServerEntry getServerEntry() {
            return peergreenServerEntry;
        }

        protected Button getButton() {
            return button;
        }

        protected TableViewer getTableViewer() {
            return tableViewer;
        }

        protected ServersConfiguration getServersConfiguration() {
            return serversConfiguration;
        }


        protected IPreferenceStore getPreferenceStore() {
            Activator activator = Activator.getDefault();
            if (activator != null) {
                return activator.getPreferenceStore();
            }
            return null;
        }

}
