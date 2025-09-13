package com.example.book.response;

import com.example.book.model.Booking;
import com.example.book.model.BookingStatus;
import java.time.LocalDateTime;

public class BookingResponse {
    private Long id;
    private String status;          // "PENDING", "CONFIRMED", "DECLINED", ...
    private Long serviceId;
    private String serviceHandle;   // small summary helps list views
    private String serviceName;     // optional, if UI shows it immediately
    private String start;         // ISO 8601 e.g. "2025-09-10T10:00:00Z" (or with zone you use)
    private String end;           // ISO 8601
    private String date;
    private Boolean canAccept;
    private Boolean canDecline;
    private Boolean canCancel;
    private String createdAt;
    private String updatedAt;

    public static BookingResponse fromBooking(Booking booking, String currentUserEmail) {
        BookingResponse resp = new BookingResponse();
        resp.setId(booking.getId());
        resp.setServiceId(booking.getService().getId());
        resp.setStatus(String.valueOf(booking.getStatus()));
        resp.setStart(booking.getStart().toString());
        resp.setEnd(booking.getEnd().toString());
        resp.setDate(booking.getDate().toString());
        resp.setCreatedAt(booking.getCreatedAt().toString());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = LocalDateTime.of(booking.getDate(), booking.getStart());

        boolean isOwner   = booking.getService().getUser().getEmail().equals(currentUserEmail);
        boolean isBooker  = booking.getUser().getEmail().equals(currentUserEmail);
        boolean pending   = booking.getStatus() == BookingStatus.PENDING;
        boolean future    = now.isBefore(start);

        // rules
        resp.setCanAccept(isOwner && pending && now.isBefore(start.minusHours(1)));
        resp.setCanDecline(isOwner && pending);
        resp.setCanCancel(isBooker && (pending || booking.getStatus() == BookingStatus.CONFIRMED) && future);

        return resp;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // getters/setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceHandle() {
        return serviceHandle;
    }

    public void setServiceHandle(String serviceHandle) {
        this.serviceHandle = serviceHandle;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public Boolean getCanAccept() {
        return canAccept;
    }

    public void setCanAccept(Boolean canAccept) {
        this.canAccept = canAccept;
    }

    public Boolean getCanDecline() {
        return canDecline;
    }

    public void setCanDecline(Boolean canDecline) {
        this.canDecline = canDecline;
    }

    public Boolean getCanCancel() {
        return canCancel;
    }

    public void setCanCancel(Boolean canCancel) {
        this.canCancel = canCancel;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
