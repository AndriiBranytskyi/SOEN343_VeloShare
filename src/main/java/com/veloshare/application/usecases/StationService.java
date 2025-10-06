package com.veloshare.application.usecases;

import com.veloshare.application.Result;
import com.veloshare.application.dto.StationDto;
import com.veloshare.domain.StationReadModel;
import com.veloshare.domain.bmsService;

public class StationService {

    private final bmsService bms;

    public StationService(bmsService bms) {
        this.bms = bms;
    }

    public Result<StationDto> get(String name) {
        try {
            StationReadModel rm = bms.getStationReadModel(name);
            return Result.ok(new StationDto(rm.getName(), rm.getLatitude(), rm.getLongitude(),
                    rm.getCapacity(), rm.getBikesAvailable(), rm.getFreeDocks()));
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
}
