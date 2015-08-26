/*****************************************************************************
 * LibSirenException.java
 *****************************************************************************
 *  @author Jensen<Jensen@connect2.com.cn>
 *****************************************************************************/

/**
 * LibSirenException: exceptions thrown by the native LibSiren interface
 */
package com.mmclass.libsiren;

public class LibSirenException extends Exception {
    private static final long serialVersionUID = -1909522348226924189L;

    /**
     * Create an empty error
     */
    public LibSirenException() {
        super();
    }

    /**
     * @param detailMessage
     */
    public LibSirenException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * @param throwable
     */
    public LibSirenException(Throwable throwable) {
        super(throwable);
    }

    /**
     * @param detailMessage
     * @param throwable
     */
    public LibSirenException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

}
