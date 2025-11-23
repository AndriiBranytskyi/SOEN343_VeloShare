package com.veloshare.application.dto;

record ProfileDto(String uid,
    String name,
    String role,
    boolean canRide,
    boolean canOperate,
    String tier,
    String tierStatus) {
}