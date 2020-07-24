package com.icatch.usbcam.utils;

/**
 * @author b.jiang
 * @date 2019/6/28
 * @description
 */
public class HomeKeyEvent {
    private HomeKeyEvent.EventType eventType;

    public HomeKeyEvent(HomeKeyEvent.EventType eventType) {
        this.eventType = eventType;
    }


    public HomeKeyEvent.EventType getEventType() {
        return eventType;
    }

    public enum EventType{
        REASON_RECENT_APPS,
        REASON_HOME_KEY,
    }
}
