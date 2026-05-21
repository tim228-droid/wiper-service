package com.example.wiperservice.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Random;

@Entity
@Table(name = "wipers")
public class Wiper {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String manufacturer;
    private Double price;
    private Integer wiperLength;
    
    @Enumerated(EnumType.STRING)
    private AnomalyType anomalyType;
    private Boolean isFixed = false;
    private LocalDateTime createdDate = LocalDateTime.now();
    private Double repairCost;
    private LocalDateTime repairDate;

    @PrePersist
    public void randomAnomaly() {
        AnomalyType[] types = {AnomalyType.RUBBER_WEAR, AnomalyType.FRAME_DAMAGE, 
            AnomalyType.MOTOR_FAILURE, AnomalyType.ELECTRICAL_ISSUE, AnomalyType.MOUNTING_DEFECT};
        this.anomalyType = types[new Random().nextInt(types.length)];
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Integer getWiperLength() { return wiperLength; }
    public void setWiperLength(Integer wiperLength) { this.wiperLength = wiperLength; }
    public AnomalyType getAnomalyType() { return anomalyType; }
    public void setAnomalyType(AnomalyType anomalyType) { this.anomalyType = anomalyType; }
    public Boolean getIsFixed() { return isFixed; }
    public void setIsFixed(Boolean isFixed) { this.isFixed = isFixed; }
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    public Double getRepairCost() { return repairCost; }
    public void setRepairCost(Double repairCost) { this.repairCost = repairCost; }
    public LocalDateTime getRepairDate() { return repairDate; }
    public void setRepairDate(LocalDateTime repairDate) { this.repairDate = repairDate; }
}