package com.veloshare.application.usecases;

import java.util.ArrayList;
import java.util.List;

import com.veloshare.application.Result;
import com.veloshare.application.dto.StationDto;
import com.veloshare.domain.Station;
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

        public List<StationDto> getAll() {
        List<StationDto> out = new ArrayList<>();
        for (Station s : bms.getStations()) {   // bmsService#getStations()
            out.add(new StationDto(
                    s.getName(),
                    s.getLatitude(),
                    s.getLongitude(),
                    s.getCapacity(),
                    s.getBikesAvailable(),
                    s.getFreeDocks()
            ));
        }
        return out;
    }
}
