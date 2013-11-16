package com.acmetelecom;

public class CallStart extends CallEvent {

    public CallStart(String caller, String callee, long timeStamp) {
        super(caller, callee, timeStamp);
    }
}
