package com.veloshare.application.dto;

import java.util.Date;

public record  EndTripResult (
    String tripId, 
    double amount,
    Date startTime,
    Date endTime,
    String bikeId,
    String originStation,
    String arrivalStation
){}