package com.veloshare.api;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.veloshare.application.usecases.OperatorService;
import com.veloshare.application.usecases.ReservationService;
import com.veloshare.application.usecases.StationService;
import com.veloshare.application.usecases.TripService;
import com.veloshare.auth.RolesRepo;
import com.veloshare.domain.Role;
import com.veloshare.domain.bmsService;

//bean is an object managed by Spring that helps provide domain to Spring framework
@Configuration
public class Beans {

    @Bean
    public bmsService bms() {
        bmsService bms = new bmsService();
        java.io.File cfg = new java.io.File("./config.json");
        System.out.println("Config path = " + cfg.getAbsolutePath() + " (exists=" + cfg.exists() + ")");
        bms.loadConfig("./config.json");
        System.out.println("bms.loadConfig done");
        return bms;
    }

    @Bean
    public StationService stationService(bmsService bms) {
        return new StationService(bms);
    }

    @Bean
    public ReservationService reservationService(bmsService bms) {
        return new ReservationService(bms);
    }

    @Bean
    public TripService tripService(bmsService bms) {
        return new TripService(bms);
    }

    @Bean
    public OperatorService operatorService(bmsService bms) {
        return new OperatorService(bms);
    }

    @Bean
    public RolesRepo rolesRepo() {
        return new RolesRepo();
    }

    //preload yourself as operator or rider for testing
    @Bean
    CommandLineRunner preloadOperator(RolesRepo roles) {
        return args -> roles.setRole("fRQDk2UrwKhXfkh52WUK2O8JZUk2", Role.RIDER);
    }

    @Bean
    CommandLineRunner dumpState(bmsService bms) {
        return args -> {
            try {
                var stations = bms.getStations();
                System.out.println("=== BMS SNAPSHOT ===");
                for (var s : stations) {
                    System.out.println("Station: " + s.getName()
                            + " | bikesAvailable=" + s.getBikesAvailable()
                            + " | freeDocks=" + s.getFreeDocks());
                    for (var d : s.getDocks()) {
                        if (d.isOccupied() && d.getBike() != null) {
                            System.out.println("  - bike " + d.getBike().getId() + " (" + d.getBike().getType() + ") state=" + d.getBike().getState());
                        }
                    }
                }
                System.out.println("====================");
            } catch (Exception e) {
                System.out.println("Snapshot failed: " + e.getMessage());
            }
        };
    }

}
