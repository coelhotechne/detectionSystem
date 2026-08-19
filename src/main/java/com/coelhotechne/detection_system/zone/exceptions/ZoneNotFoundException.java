package com.coelhotechne.detection_system.zone.exceptions;


public class ZoneNotFoundException extends RuntimeException {
    private final String uuid;
    private final String description;
    public ZoneNotFoundException(String uuid,String description,String message) {
        super("Id: "+uuid
                +"\nDescription"+description
                +"\n Error: "+message);
        this.uuid=uuid;
        this.description=description;
    }
}
