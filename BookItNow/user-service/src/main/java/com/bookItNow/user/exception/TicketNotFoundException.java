package com.bookItNow.user.exception;

public class TicketNotFoundException extends RuntimeException {

    public TicketNotFoundException(){}
    public TicketNotFoundException(String message){
        super(message);
    }
}
