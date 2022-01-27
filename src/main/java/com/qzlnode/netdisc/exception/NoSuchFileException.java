package com.qzlnode.netdisc.exception;

/**
 * @author qzlzzz
 */
public class NoSuchFileException extends RuntimeException{

    public NoSuchFileException() {
        super();
    }

    public NoSuchFileException(String message) {
        super(message);
    }
}
