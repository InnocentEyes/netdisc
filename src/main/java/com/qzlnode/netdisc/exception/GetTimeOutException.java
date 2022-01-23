package com.qzlnode.netdisc.exception;

/**
 * @author qzlzzz
 */
public class GetTimeOutException extends RuntimeException{

    public GetTimeOutException() {
        super();
    }

    public GetTimeOutException(String message) {
        super(message);
    }
}
