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

import static com.peergreen.eclipse.osgi.preferences.PreferenceConstants.SERVER_XML_CONTENT;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.jar.Attributes.Name;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.peergreen.eclipse.osgi.Activator;
import com.peergreen.eclipse.osgi.preferences.parser.ParsingException;
import com.peergreen.eclipse.osgi.preferences.parser.ServersConfigurationReader;
import com.peergreen.eclipse.osgi.preferences.parser.ServersConfigurationWriter;
import com.peergreen.eclipse.osgi.preferences.provider.column.ActionsColumnLabelProvider;
import com.peergreen.eclipse.osgi.preferences.provider.column.RadioButtonColumnLabelProvider;
import com.peergreen.eclipse.osgi.preferences.provider.column.TextColumnLabelProvider;
import com.peergreen.eclipse.osgi.preferences.provider.content.PeergreenContentProvider;

/**
 * Preference page for the Eclipse plugin.
 * @author Florent Benoit
 */
public class PeergreenPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private ServersConfiguration serversConfiguration;

   private TableViewer tableViewer;

    public static void main(String[] args) throws Exception {
        Display display = new Display();
        // Allows to test the preference page from Eclipse
        Shell shell = new Shell();
        shell.setSize(450, 300);
        shell.setText("SWT Application");

        //create an instance of the custom MyPreference class
        IPreferencePage page = new PeergreenPreferencePage();

        //create a new PreferenceNode that will appear in the Preference window
        PreferenceNode node = new PreferenceNode("1", page);

        //use workbenches's preference manager
        PreferenceManager mgr = new PreferenceManager();
        mgr.addToRoot(node); //add the node in the PreferenceManager

        //instantiate the PreferenceDialog
        PreferenceDialog pd = new PreferenceDialog(shell, mgr);

        pd.create();
        pd.open();


    }



    public PeergreenPreferencePage() {
        super();
        setDescription("Peergreen server :");
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    @Override
    public void init(IWorkbench workbench) {

        // Initialize the values
        Activator activator = Activator.getDefault();
        if (activator != null) {
            IPreferenceStore preferenceStore = activator.getPreferenceStore();
            if (preferenceStore != null) {
                setPreferenceStore(preferenceStore);
                String xmlContent = preferenceStore.getString(SERVER_XML_CONTENT);
                try {
                    this.serversConfiguration = new ServersConfigurationReader().parse(new StringReader(xmlContent));
                } catch (ParsingException e) {
                    throw new IllegalStateException("Cannot parse XML file", e);
                }

            }
        }
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createContents(Composite parent) {

        Composite page = new Composite(parent, 0);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        page.setLayout(layout);

        // Description
        Label descriptionLabel = new Label(page, SWT.LEFT | SWT.WRAP);
        descriptionLabel.setText("Select the default server to use. New servers can be downloaded or installed.");
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        descriptionLabel.setLayoutData(gd);

        int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL |
                SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

        Table peergreenServersTable = new Table(page, style);
        peergreenServersTable.setHeaderVisible(true);
        peergreenServersTable.setLinesVisible(true);


        this.tableViewer = new TableViewer(peergreenServersTable);
        tableViewer.setUseHashlookup(true);

        // default column
        TableColumn defaultTableColumn = new TableColumn(peergreenServersTable, SWT.NONE);
        defaultTableColumn.setWidth(50);
        defaultTableColumn.setText("Default");
        defaultTableColumn.setToolTipText("Select the server that will be used as default server");
        TableViewerColumn defaultTableColumnViewer = new TableViewerColumn(tableViewer, defaultTableColumn);
        defaultTableColumnViewer.setLabelProvider(new RadioButtonColumnLabelProvider(serversConfiguration, tableViewer));


        // name + version column
        TableColumn serverTableColumn = new TableColumn(peergreenServersTable, SWT.NONE);
        serverTableColumn.setWidth(150);
        serverTableColumn.setText("Server Name");
        TableViewerColumn serverTableColumnViewer = new TableViewerColumn(tableViewer, serverTableColumn);
        serverTableColumnViewer.setLabelProvider(new TextColumnLabelProvider());

        // For update/download/etc column
        TableColumn actionsTableColumn = new TableColumn(peergreenServersTable, SWT.NONE);
        actionsTableColumn.setWidth(200);
        actionsTableColumn.setText("Actions");
        TableViewerColumn actionsTableColumnViewer = new TableViewerColumn(tableViewer, actionsTableColumn);
        actionsTableColumnViewer.setLabelProvider(new ActionsColumnLabelProvider(serversConfiguration, tableViewer));

        // Set content provider
        tableViewer.setContentProvider(new PeergreenContentProvider());


        // Refresh button
        Button refreshButton = new Button(page, SWT.CENTER);
        refreshButton.setText("Refresh list");
        refreshButton.setToolTipText("Refresh list of availble servers");
        gd = new GridData();
        gd.horizontalAlignment = SWT.LEFT;
        gd.verticalAlignment = SWT.BOTTOM;
        refreshButton.setLayoutData(gd);
        refreshButton.setVisible(false);

        // Local add button
        Button addLocalServerButton = new Button(page, SWT.CENTER);
        addLocalServerButton.setText("Select a local server...");
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.TOP;
        addLocalServerButton.setLayoutData(gd);

        addLocalServerButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
              // Selection of a File
              FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);
              fileDialog.setText("Select a Peergreen Server");
              fileDialog.setFilterExtensions(new String[] {"*.jar"});
              fileDialog.setFilterNames(new String[] {"Peergreen Server (*.jar)"});
              String fileName = fileDialog.open();
              if (fileName != null) {
                handleLocalFile(fileName);
              }
            }
          });

        tableViewer.setInput(serversConfiguration);

        return page;
    }

    protected void handleLocalFile(String path) {
        // validate if it's a PG Server
        if (!path.endsWith(".jar")) {
            errorMessage(String.format("Selected file %s is not a valid .jar file", path));
            return;
        }

        try (JarFile file = new JarFile(path)) {
            // Check if manifest is OK
            Manifest manifest = file.getManifest();
            String serverName = manifest.getMainAttributes().getValue("Peergreen-Server-Name");
            String version =  manifest.getMainAttributes().getValue(Name.IMPLEMENTATION_VERSION);

            // no name
            if (serverName == null) {
                errorMessage(String.format("Selected file %s is a JAR file but not a valid Peergreen Server", path));
                return;
            }

            // no version
            if (version == null) {
                errorMessage(String.format("Unable to find the version of the Peergreen Server %s", path));
                return;
            }

            // It's time to add this server to the existing server
            PeergreenServerEntry peergreenserverEntry = serversConfiguration.searchServerName(serverName);
            if (peergreenserverEntry == null) {
                // creates a new entry
                peergreenserverEntry = new PeergreenServerEntry(serverName, version);
                serversConfiguration.addEntry(peergreenserverEntry);
            } else {
                int statusCode = warningMessage("This will upgrade current entry named '" + serverName + "' from version '" + peergreenserverEntry.getVersion() + "' to '" + version + "'");
                // Skip upgrade if not wanted
                if (SWT.NO == statusCode) {
                    return;
                }
                // update version
                peergreenserverEntry.setVersion(version);
            }

            // update path
            peergreenserverEntry.setLocalPath(new File(path));

            refresh();

        } catch (IOException e) {
            errorMessage(e.getMessage());
        }



    }


    protected int warningMessage(String message) {
        MessageBox messageDialog = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.YES | SWT.NO);
        messageDialog.setText("Warning");
        messageDialog.setMessage(message);
        return messageDialog.open();
    }


    protected int errorMessage(String message) {
        MessageBox messageDialog = new MessageBox(getShell(), SWT.ERROR);
        messageDialog.setText("Error message");
        messageDialog.setMessage(message);
        return messageDialog.open();
    }


    protected void refresh() {
        tableViewer.refresh();
    }

    /**
     * Save the current configuration
     */
    @Override
    public boolean performOk() {

        // Save the values
        IPreferenceStore preferenceStore = getPreferenceStore();
        if (preferenceStore != null) {
            // save the current configuration
            StringWriter stringWriter = new StringWriter();
            try {
                new ServersConfigurationWriter().write(serversConfiguration, stringWriter);
            } catch (ParsingException e) {
                throw new IllegalStateException("Unable to save the current server configuration", e);
            }
            preferenceStore.setValue(SERVER_XML_CONTENT, stringWriter.toString());

        }




        return super.performOk();
    }


}