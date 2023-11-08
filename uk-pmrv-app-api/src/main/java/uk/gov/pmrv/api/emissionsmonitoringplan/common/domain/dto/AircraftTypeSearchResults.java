package uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AircraftTypeSearchResults {

    private List<AircraftTypeDTO> aircraftTypes;
    private Long total;
}
