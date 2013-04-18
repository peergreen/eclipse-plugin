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

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;

import com.peergreen.eclipse.osgi.preferences.PeergreenServerEntry;
import com.peergreen.eclipse.osgi.preferences.ServersConfiguration;
import com.peergreen.eclipse.osgi.preferences.listener.ButtonSelectionListener;

/**
 * Provides actions on a selected node.
 * @author Florent Benoit
 */
public class ActionsColumnLabelProvider extends ColumnLabelProvider {

    private final ServersConfiguration serversConfiguration;

    private Composite composite;

    private final TableViewer tableViewer;

    public ActionsColumnLabelProvider(ServersConfiguration serversConfiguration, TableViewer tableViewer) {
        this.serversConfiguration = serversConfiguration;
        this.tableViewer = tableViewer;
    }


    @Override
    public void update(ViewerCell cell) {
        PeergreenServerEntry serverEntry = (PeergreenServerEntry) cell.getElement();

        TableItem item = (TableItem) cell.getItem();

        composite = new Composite((Composite)cell.getViewerRow().getControl(), SWT.BORDER_DASH);
        composite.setLayout(new FillLayout());

        // Download button if assembly not installed
        if (serverEntry.isDownloadable()) {
            Button downloadButton = new Button(composite, SWT.NONE);
            downloadButton.setText("Download...");
            downloadButton.setToolTipText("Download this version");
        }

        // Remove button if assembly is installed
        if (!serverEntry.isDownloadable() && (serversConfiguration.getDefaultEntry() != null && !serversConfiguration.getDefaultEntry().equals(serverEntry))) {
            Button removeButton = new Button(composite, SWT.NONE);
            removeButton.setText("Remove");
            removeButton.setToolTipText("Remove this assembly");
            removeButton.addSelectionListener(new RemoveEntryListener(serversConfiguration, serverEntry, tableViewer));

        }

        // Update button if assembly has a new version available
        if (serverEntry.isUpdatable()) {
            Button updateButton = new Button(composite, SWT.NONE);
            updateButton.setText("Update...");
            updateButton.setToolTipText("Update this assembly to a newer version");
        }

        // Create editor
        TableEditor editor = new TableEditor(item.getParent());

        editor.grabHorizontal  = true;
        editor.grabVertical = true;
        editor.setEditor(composite, item, cell.getColumnIndex());
        editor.layout();

    }


    class RemoveEntryListener extends ButtonSelectionListener {

        public RemoveEntryListener(ServersConfiguration serversConfiguration, PeergreenServerEntry peergreenServerEntry, TableViewer tableViewer) {
            super(serversConfiguration, peergreenServerEntry, tableViewer);

        }


        @Override
        public void widgetSelected(SelectionEvent selectionEvent) {
            // needs to remove the server
            serversConfiguration.removeEntry(getServerEntry());

            // refresh
            refresh();

        }

    }
}
