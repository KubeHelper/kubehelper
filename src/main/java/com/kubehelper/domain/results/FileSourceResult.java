package com.kubehelper.domain.results;

import java.net.URI;

/**
 * @author JDev
 */
public class FileSourceResult {
    private String label;
    private String filePath;
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
}
