package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AviationDreDeterminationReason {

    @NotNull
    private AviationDreDeterminationReasonType type;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Size(max=10000)
    private String furtherDetails;
}
