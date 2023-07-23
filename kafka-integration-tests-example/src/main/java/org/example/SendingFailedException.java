package org.example;

public class SendingFailedException extends RuntimeException{

    public SendingFailedException(Throwable cause) {
        super(cause);
    }
}
