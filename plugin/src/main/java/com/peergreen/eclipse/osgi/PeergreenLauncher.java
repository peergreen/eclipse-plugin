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

package com.peergreen.eclipse.osgi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

/**
 * Peergreen Launcher will start the Peergreen server and install bundles of the user.
 * @author Florent Benoit
 */
public class PeergreenLauncher {

    /**
     * Peergreen Framework Factory classname.
     */
    public static final String FRAMEWORK_FACTORY = "com.peergreen.osgi.framework.PeergreenFrameworkFactory";

    /**
     * Arguments.
     */
    private final List<String> argsList;

    /**
     * Peergreen server framework.
     */
    private Framework framework;

    /**
     * Config directory.
     */
    private File configDirectory;

    /**
     * Original System.out
     */
    private final PrintStream systemOut;

    /**
     * Original System.err
     */
    private final PrintStream systemErr;


    /**
     * Constructor with the given set of args.
     * @param args the arguments of this launcher.
     * It should contains -dev and -peergreenConfigDir
     * -console is optional
     */
    public PeergreenLauncher(String[] args) {
        // Gets arguments
        this.argsList = Arrays.asList(args);

        // Get current stream
        this.systemOut = System.out;
        this.systemErr = System.err;

    }

    /**
     * Initialize the peergreen server.
     * @throws PeergreenLauncherException if initialization fails
     */
    public void init() throws PeergreenLauncherException {
        // Create configuration map
        Map<String, String> configuration = new HashMap<>();

        // Enable console if asked
        if (argsList.contains("-console")) {
            configuration.put("com.peergreen.shell.console", "true");
            // Eclipse console is unsupported terminal
            System.setProperty("jline.terminal", "jline.UnsupportedTerminal");
        }

        // Load the Peergreen framework Factory
        ClassLoader classLoader = PeergreenLauncher.class.getClassLoader();
        Class<? extends FrameworkFactory> peergreenFactory;
        try {
            peergreenFactory = classLoader.loadClass(FRAMEWORK_FACTORY).asSubclass(FrameworkFactory.class);
        } catch (ClassNotFoundException e) {
           throw new PeergreenLauncherException(String.format("Unable to load %s as Framework factory", FRAMEWORK_FACTORY), e);
        }
        FrameworkFactory frameworkFactory;
        try {
            frameworkFactory = peergreenFactory.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new PeergreenLauncherException(String.format("Unable to create instance of %s Framework factory", FRAMEWORK_FACTORY), e);
        }

        // Gets the configuration directory
        int index = argsList.indexOf("-peergreenConfigDir");
        String configDirectoryPath = argsList.get(++index);
        this.configDirectory = new File(configDirectoryPath);

        // Sets the storage directory
        configuration.put(Constants.FRAMEWORK_STORAGE, configDirectory.getPath());

        // Adds the osgi.dev property
        index = argsList.indexOf("-dev");
        String devPath = argsList.get(++index);
        configuration.put("osgi.dev", devPath);


        // Initialize the framework
        this.framework = frameworkFactory.newFramework(configuration);
        try {
            framework.init();
        } catch (BundleException e) {
            throw new PeergreenLauncherException("Unable to initialize the peergreen server", e);
        }
    }


    /**
     * Starts the peergreen server.
     * @throws PeergreenLauncherException
     */
    public void start() throws PeergreenLauncherException {

        try {
            framework.start();
        } catch (BundleException e) {
            throw new PeergreenLauncherException("Unable to start the peergreen server", e);
        }

        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        List<Bundle> bundlesToStart = new ArrayList<>();

        boolean errors = false;
        try ( FileReader fileReader = new FileReader(new File(configDirectory, "bundles.list")); BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String bundle = bufferedReader.readLine();
            systemOut.println();
            while (bundle != null) {
                systemOut.print("Installing bundle '" + bundle + "'");
                try {
                    Bundle osgiBundle = framework.getBundleContext().installBundle(bundle);
                    if (!isFragment(osgiBundle)) {
                        bundlesToStart.add(osgiBundle);
                    }
                    systemOut.println(".");
                } catch (BundleException e) {
                    errors = true;
                    e.printStackTrace(systemErr);
                }
                bundle = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace(systemErr);
        }
        for (Bundle bundle : bundlesToStart) {
            try {
                bundle.start();
            } catch (BundleException e) {
                errors = true;
                e.printStackTrace(systemErr);
            }
        }


        if (!errors) {
            systemOut.println("Peergreen Server is now ready with user OSGi bundles.");
        }

    }


    /**
     * Main method.
     * @param args the given arguments
     */
    public static void main(String[] args) throws Exception {
        PeergreenLauncher peergreenLauncher = new PeergreenLauncher(args);
        peergreenLauncher.init();
        peergreenLauncher.start();
    }

    /**
     * Checks if the given bundle is a fragment
     * @param bundle the bundle to check
     * @return true if the bundle is a fragment.
     */
    protected boolean isFragment(final Bundle bundle) {
        return bundle.getHeaders().get(Constants.FRAGMENT_HOST) != null;
    }

}
