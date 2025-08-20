package org.acme.telemetry.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelemetryResponse {
    private String dataReferencia;
    private List<EndpointStats> listaEndpoints;
}