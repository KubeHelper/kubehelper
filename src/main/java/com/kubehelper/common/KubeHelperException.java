package com.kubehelper.common;

import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Wrap Exceptions for Error dialog box.
 *
 * @author JDev
 */
public class KubeHelperException extends Exception {

    public KubeHelperException(Exception exception) {
        super(exception);
    }

    public String getMessage() {
        return super.getMessage();
    }

    public String getStack() {
        return ExceptionUtils.getStackTrace(super.getCause());
    }

    public int getHash() {
        return super.hashCode();
    }
}
