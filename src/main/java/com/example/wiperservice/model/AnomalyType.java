package com.example.wiperservice.model;

public enum AnomalyType {
    NO_ANOMALY("Без аномалии"),
    RUBBER_WEAR("Износ резины"),
    FRAME_DAMAGE("Повреждение каркаса"),
    MOTOR_FAILURE("Отказ двигателя"),
    ELECTRICAL_ISSUE("Электрическая проблема"),
    MOUNTING_DEFECT("Дефект крепления");
    
    private final String desc;
    AnomalyType(String desc) { this.desc = desc; }
    public String getDescription() { return desc; }
}