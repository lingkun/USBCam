package com.icatch.usbcam.common.customexception;


public class DataFormatException extends Exception{
    public DataFormatException() {
        super();
    }

    public DataFormatException(String tag,String message) {
        super(message);
    }
}
