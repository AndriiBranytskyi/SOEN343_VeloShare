package com.veloshare.application;

import com.veloshare.domain.bmsService;

public class ApplicationContext {

    private final bmsService bms;

    public ApplicationContext(bmsService bms) {
        this.bms = bms;
    }

    public bmsService bms() {
        return bms;
    }
}
