package com.veloshare.application.usecases;

import java.util.ArrayList;
import java.util.List;

import com.veloshare.application.Result;
import com.veloshare.application.dto.StationDto;
import com.veloshare.domain.Dock;
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

            Station station = bms.requireStation(name);

            int standardCount = (int) station.getDocks().stream()
                    .filter(Dock::isOccupied)
                    .filter(d -> d.getBike() != null)
                    .filter(d -> !"e-bike".equalsIgnoreCase(d.getBike().getType()))
                    .count();

            int eBikeCount = (int) station.getDocks().stream()
                    .filter(Dock::isOccupied)
                    .filter(d -> d.getBike() != null)
                    .filter(d -> "e-bike".equalsIgnoreCase(d.getBike().getType()))
                    .count();

            List<StationDto.BikeDto> bikes = station.getDocks().stream()
                    .filter(Dock::isOccupied)
                    .filter(d -> d.getBike() != null)
                    .map(d -> new StationDto.BikeDto(
                    d.getBike().getId(),
                    d.getBike().getType()
            ))
                    .toList();

            StationDto dto = new StationDto(
                    rm.getName(),
                    rm.getLatitude(),
                    rm.getLongitude(),
                    rm.getCapacity(),
                    rm.getBikesAvailable(),
                    rm.getFreeDocks(),
                    standardCount,
                    eBikeCount,
                    station.getAddress(),
                    bikes,
                    station.isOutOfService()
            );

            return Result.ok(dto);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    public List<StationDto> getAll() {
        List<StationDto> out = new ArrayList<>();
        for (Station s : bms.getStations()) {   // bmsService#getStations()
            int standardCount = (int) s.getDocks().stream()
                    .filter(Dock::isOccupied)
                    .filter(d -> d.getBike() != null)
                    .filter(d -> !"e-bike".equalsIgnoreCase(d.getBike().getType()))
                    .count();

            int eBikeCount = (int) s.getDocks().stream()
                    .filter(Dock::isOccupied)
                    .filter(d -> d.getBike() != null)
                    .filter(d -> "e-bike".equalsIgnoreCase(d.getBike().getType()))
                    .count();

            List<StationDto.BikeDto> bikes = s.getDocks().stream()
                    .filter(Dock::isOccupied)
                    .filter(d -> d.getBike() != null)
                    .map(d -> new StationDto.BikeDto(
                    d.getBike().getId(),
                    d.getBike().getType()
            ))
                    .toList();

            out.add(new StationDto(
                    s.getName(),
                    s.getLatitude(),
                    s.getLongitude(),
                    s.getCapacity(),
                    s.getBikesAvailable(),
                    s.getFreeDocks(),
                    standardCount,
                    eBikeCount,
                    s.getAddress(),
                    bikes,
                    s.isOutOfService()
            ));
        }
        return out;
    }
}
