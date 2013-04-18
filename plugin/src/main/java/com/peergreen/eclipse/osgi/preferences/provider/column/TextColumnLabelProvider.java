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

import com.peergreen.eclipse.osgi.preferences.PeergreenServerEntry;

public class TextColumnLabelProvider extends ColumnLabelProvider {

    @Override
    public String getText(Object element) {
        PeergreenServerEntry serverEntry = (PeergreenServerEntry) element;
      return serverEntry.getName().concat(" v").concat(serverEntry.getVersion());
    }

}
