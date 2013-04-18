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
package com.peergreen.eclipse.osgi.preferences.provider.column;

import static com.peergreen.eclipse.osgi.preferences.PreferenceConstants.SERVER_PATH;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;

import com.peergreen.eclipse.osgi.preferences.PeergreenServerEntry;
import com.peergreen.eclipse.osgi.preferences.ServersConfiguration;
import com.peergreen.eclipse.osgi.preferences.listener.ButtonSelectionListener;

/**
 * Sets the default entry
 * @author Florent Benoit
 */
public class RadioButtonColumnLabelProvider extends ColumnLabelProvider {

    private final ServersConfiguration serversConfiguration;

    private final TableViewer tableViewer;

    public RadioButtonColumnLabelProvider(ServersConfiguration serversConfiguration, TableViewer tableViewer) {
        this.serversConfiguration = serversConfiguration;
        this.tableViewer = tableViewer;
    }


    @Override
    public void update(ViewerCell cell) {
        final PeergreenServerEntry serverEntry = (PeergreenServerEntry) cell.getElement();

        TableItem item = (TableItem) cell.getItem();

        TableEditor editor = new TableEditor(item.getParent());
        Button radioButton = new Button((Composite) cell.getViewerRow().getControl(), SWT.RADIO);

        // It's the default selection
        if (serversConfiguration.getDefaultEntry() != null && serversConfiguration.getDefaultEntry().equals(serverEntry)) {
            radioButton.setSelection(true);
        }

        // Downloadable so not ready, greyed
        radioButton.setEnabled(!serverEntry.isDownloadable());
        radioButton.addSelectionListener(new RadioButtonSelectionListener(serversConfiguration, serverEntry, tableViewer, radioButton));
        radioButton.pack();
        editor.minimumWidth = radioButton.getSize ().x;
        editor.horizontalAlignment = SWT.CENTER;
        editor.setEditor(radioButton, item, cell.getColumnIndex());
        editor.layout();

    }

    class RadioButtonSelectionListener extends ButtonSelectionListener {

        public RadioButtonSelectionListener(ServersConfiguration serversConfiguration, PeergreenServerEntry peergreenServerEntry, TableViewer tableViewer, Button radioButton) {
            super(serversConfiguration, peergreenServerEntry, tableViewer, radioButton);
        }

        @Override
        public void widgetSelected(SelectionEvent arg0) {

            // we've been selected
            if (getButton().getSelection()) {
                serversConfiguration.setDefaultEntry(getServerEntry());
                IPreferenceStore preferenceStore = getPreferenceStore();
                if (preferenceStore != null) {
                    preferenceStore.setValue(SERVER_PATH, getServerEntry().getLocalPath().toString());
                }
                refresh();
            }

        }



    }


}
