/*
Kube Helper
Copyright (C) 2021 JDev

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.kubehelper.domain.results;

import java.net.URI;
import java.util.StringJoiner;

/**
 * @author JDev
 */
public class FileSourceResult {

    private String label = "";
    private String filePath = "";
    private URI uri;
    private boolean readonly = true;


    public String getLabel() {
        return label;
    }

    public FileSourceResult setLabel(String label) {
        this.label = label;
        return this;
    }

    public String getFilePath() {
        return filePath;
    }

    public FileSourceResult setFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public FileSourceResult setReadonly(boolean readonly) {
        this.readonly = readonly;
        return this;
    }

    public URI getUri() {
        return uri;
    }

    public FileSourceResult setUri(URI uri) {
        this.uri = uri;
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", FileSourceResult.class.getSimpleName() + "[", "]")
                .add("label='" + label + "'")
                .add("filePath='" + filePath + "'")
                .add("uri=" + uri)
                .add("readonly=" + readonly)
                .toString();
    }
}
