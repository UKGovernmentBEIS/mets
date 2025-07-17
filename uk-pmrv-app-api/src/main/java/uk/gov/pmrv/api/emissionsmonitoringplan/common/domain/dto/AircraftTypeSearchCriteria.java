package uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.domain.PagingRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AircraftTypeSearchCriteria {

    @Size(min = 3, max = 255)
    private String term;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<@Valid AircraftTypeDTO> excludedAircraftTypes = new ArrayList<>();

    @Valid
    @NotNull
    @JsonUnwrapped
    private PagingRequest paging;
}
