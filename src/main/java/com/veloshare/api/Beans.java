package com.veloshare.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.veloshare.application.usecases.OperatorService;
import com.veloshare.application.usecases.ReservationService;
import com.veloshare.application.usecases.StationService;
import com.veloshare.application.usecases.TripService;
import com.veloshare.domain.bmsService;

//bean is an object managed by Spring that helps provide domain to Spring framework
@Configuration
public class Beans {

    @Bean
    public bmsService bms() {
        bmsService bms = new bmsService();
        bms.loadConfig("./config.json"); // use your file path
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

}
