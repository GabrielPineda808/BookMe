package com.example.book.response;

import com.example.book.model.Booking;
import com.example.book.model.BookingStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
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
        resp.setServiceName(booking.getService().getService_name());
        resp.setServiceHandle(booking.getService().getHandle());
        resp.setStatus(String.valueOf(booking.getStatus()));
        resp.setStart(booking.getStart().toString());
        resp.setEnd(booking.getEnd().toString());
        resp.setDate(booking.getDate().toString());
        resp.setCreatedAt(booking.getCreatedAt().toString());
        resp.setUpdatedAt(booking.getUpdatedAt().toString());

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
}
