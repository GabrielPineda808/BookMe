package com.example.book.response;

import com.example.book.dto.LocationDto;

import java.time.LocalTime;

public class ServiceResponse {
    private String handle;
    private String service_name;
    private String desc;
    private LocationDto location;
    private LocalTime open;
    private LocalTime close;
    private int interval;

    public ServiceResponse() {
    }

    public ServiceResponse( String handle, String service_name, String desc, LocationDto location, LocalTime open, LocalTime close, int interval) {

        this.handle = handle;
        this.service_name = service_name;
        this.desc = desc;
        this.location = location;
        this.open = open;
        this.close = close;
        this.interval = interval;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public LocationDto getLocation() {
        return location;
    }

    public void setLocation(LocationDto location) {
        this.location = location;
    }

    public LocalTime getOpen() {
        return open;
    }

    public void setOpen(LocalTime open) {
        this.open = open;
    }

    public LocalTime getClose() {
        return close;
    }

    public void setClose(LocalTime close) {
        this.close = close;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
}
