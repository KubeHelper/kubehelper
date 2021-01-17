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

    public KubeHelperException(String message, Exception exception) {
        super(message, exception);
    }

    public String getStack() {
        return ExceptionUtils.getStackTrace(super.getCause());
    }

    public int getHash() {
        return super.hashCode();
    }
}
