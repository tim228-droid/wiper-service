package com.example.wiperservice.repository;

import com.example.wiperservice.model.AnomalyType;
import com.example.wiperservice.model.Wiper;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WiperRepository extends JpaRepository<Wiper, Long> {
    List<Wiper> findByAnomalyTypeNot(AnomalyType type);
}