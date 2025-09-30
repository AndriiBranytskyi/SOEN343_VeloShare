import java.util.Date;

// R-BMD-02 - BMS shall prevent undocking from an empty station and docking to a full station.
// R-BMS-05 - BMS shall block bike returns, checkouts, and reservations for a dock that is out of service.

public class bike {
    private String bikeID;
    private String status;
    private String type;
    private Date reservationExpiryDate;
    

    public bike(String bikeID, String type) {
        this.bikeID = bikeID;
        this.type = type;
        this.status = "available";
        this.reservationExpiryDate = null;

    }

    public String getId() { 
        return bikeID; 
    }
    public String getType() { 
        return type; 
    }
    public String getState() { 
        return status; 
    }
    public Date getReservationExpiry() { 
        return reservationExpiryDate; 
    }
    public void setReservationExpiry(Date reservationExpiryDate) { 
        this.reservationExpiryDate = reservationExpiryDate; }

// R-BMS-04 - BMS shall record all state transitions with the event ID.

    public void setBikeStatus(String status) {
        if ("available".equals(this.status) && "reserved".equals(status)) {
            this.status = status;
        } else if ("reserved".equals(this.status) && "on_trip".equals(status)) {
            this.status = status;
            this.reservationExpiryDate = null;
        } else {
            throw new IllegalStateException("Invalid state transition for bike " + bikeID);
        }
    }

    public boolean isReservable() {
        return "available".equals(status);
    }
}
