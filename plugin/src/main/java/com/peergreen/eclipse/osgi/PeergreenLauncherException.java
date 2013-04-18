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

/**
 * Defines exception thrown by the Launcher.
 * @author Florent Benoit
 */
public class PeergreenLauncherException extends Exception {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 2265456153156015840L;

    /**
     * Exception while launching the server.
     * @param message the given message
     * @param e the given error
     */
    public PeergreenLauncherException(String message, Throwable e) {
        super(message, e);
    }

}
