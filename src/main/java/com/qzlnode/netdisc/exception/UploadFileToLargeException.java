package com.qzlnode.netdisc.exception;

/**
 * @author qzlzzz
 */
public class UploadFileToLargeException extends RuntimeException{

    public UploadFileToLargeException() {
        super();
    }

    public UploadFileToLargeException(String message) {
        super(message);
    }
}
