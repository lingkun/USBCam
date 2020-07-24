package com.icatch.usbcam.common.usb;

import android.hardware.usb.UsbDevice;

/**
 * @author b.jiang
 * @date 2019/6/21
 * @description
 */
public class UsbEvent {
    private EventType eventType;
    private UsbDevice device;

    public UsbEvent(EventType eventType,UsbDevice device) {
        this.eventType = eventType;
        this.device = device;
    }


    public UsbDevice getDevice() {
        return device;
    }

    public EventType getEventType() {
        return eventType;
    }

    public enum EventType{
        USB_ATTACH,
        USB_DETTACH,
        USB_CONNECT,
        USB_DISCONNECT,
        USB_CANCEL,
    }
}
