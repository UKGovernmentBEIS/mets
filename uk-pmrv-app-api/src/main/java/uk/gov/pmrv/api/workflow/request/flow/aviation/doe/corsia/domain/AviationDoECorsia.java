package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AviationDoECorsia {

    @Valid
    @NotNull
    private AviationDoECorsiaDeterminationReason determinationReason;

    @Valid
    @NotNull
    private AviationDoECorsiaEmissions emissions;


    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> supportingDocuments = new HashSet<>();

    @Valid
    @NotNull
    private AviationDoECorsiaFee fee;
}
