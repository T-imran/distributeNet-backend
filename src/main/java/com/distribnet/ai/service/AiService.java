package com.distribnet.ai.service;

import com.distribnet.common.model.Tenant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiService {

    public List<Map<String, Object>> getPredictions(Tenant tenant) {
        return List.of(
                Map.of("period", "2026-Q2", "predictedRevenue", 550000.00),
                Map.of("period", "2026-Q3", "predictedRevenue", 620000.00)
        );
    }
}
