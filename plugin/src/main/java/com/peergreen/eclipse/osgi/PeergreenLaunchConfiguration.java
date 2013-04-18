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

import static com.peergreen.eclipse.osgi.Activator.PLUGIN_ID;
import static com.peergreen.eclipse.osgi.preferences.PreferenceConstants.SERVER_PATH;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.PluginRegistry;
import org.eclipse.pde.internal.launching.launcher.LaunchArgumentsHelper;
import org.eclipse.pde.internal.launching.launcher.LaunchConfigurationHelper;
import org.eclipse.pde.launching.EquinoxLaunchConfiguration;

public class PeergreenLaunchConfiguration extends EquinoxLaunchConfiguration {




    @Override
    public String[] getClasspath(ILaunchConfiguration launchConfiguration) throws CoreException {
        String[] configurationClasspath = super.getClasspath(launchConfiguration);

        List<String> classpath = new ArrayList<>();

        // Find the plugin model
        final IPluginModelBase peergreenPlugin = PluginRegistry.findModel(PLUGIN_ID);
        if (peergreenPlugin == null) {
            throw new CoreException(new Status(IStatus.ERROR, PLUGIN_ID, String.format("Unable to find the '%s' plugin", PLUGIN_ID)));
        }
        String installLocation = peergreenPlugin.getInstallLocation();
        File installLocationPath = new File(installLocation);

        // Jar file ?
        if (installLocationPath.isFile()) {
            classpath.add(installLocation);
        } else {
            // add unpack directory
            classpath.add(installLocationPath.getPath());
            // directory. In development mode, adds target/classes
            classpath.add(new File(installLocationPath, "target" + File.separator + "classes").getPath());
        }

        // Get server path from the preference store
        IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
        String serverPath = preferenceStore.getString(SERVER_PATH);
        classpath.add(serverPath);


        // adds existing classpath from super methode
        classpath.addAll(Arrays.asList(configurationClasspath));

        // transform list into array
        return classpath.toArray(new String[classpath.size()]);

    }

    @Override
    public String getMainClass() {
        return PeergreenLauncher.class.getName();
    }


    @Override
    public String[] getProgramArguments(ILaunchConfiguration configuration) throws CoreException {
        List<String> args = new ArrayList<>();

        // add configuration directory
        args.add("-peergreenConfigDir");
        args.add(getConfigDir(configuration).getPath().toString());

        // Adds also User-defined args
        @SuppressWarnings("restriction")
        String[] userArgs = LaunchArgumentsHelper.getUserProgramArgumentArray(configuration);
        args.addAll(Arrays.asList(userArgs));

        // Get -dev super arguments as this file is used to enhance the classpath
        List<String> superArgs = Arrays.asList(super.getProgramArguments(configuration));
        int index = superArgs.indexOf("-dev");
        String dev = superArgs.get(++index);
        args.add("-dev");
        args.add(dev);

        return args.toArray(new String[args.size()]);
    }


    @Override
    protected void preLaunchCheck(ILaunchConfiguration configuration, ILaunch launch, IProgressMonitor monitor) throws CoreException {
        super.preLaunchCheck(configuration, launch, monitor);

        // Get bundles that we needs to deploy
        @SuppressWarnings("unchecked")
        Map<String, IPluginModelBase> bundles = fAllBundles;
        List<String> bundlesLocation = new ArrayList<>();
        for (Entry<String, IPluginModelBase> entry : bundles.entrySet()) {
            // Do not install equinox
            if ("org.eclipse.osgi".equals(entry.getKey())) {
                continue;
            }

            String bundlePath = LaunchConfigurationHelper.getBundleURL(entry.getValue(), true);
            bundlesLocation.add(bundlePath);

        }

        // Create configuration directory if it doesn't exist for now
        File configDirectory = getConfigDir(configuration);
        configDirectory.mkdirs();

        //TODO : Generate a deployment plan
        try (FileWriter fileWriter = new FileWriter(new File(configDirectory, "bundles.list")); BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            for (String location : bundlesLocation) {
                bufferedWriter.write(location);
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // And sent the path of this deployment plan to the launcher


    }


}
