package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AviationDoECorsiaDeterminationReason {

    @NotNull
    private AviationDoECorsiaDeterminationReasonType type;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Builder.Default
    Set<AviationDoECorsiaDeterminationReasonSubType> subtypes = new HashSet<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Size(max=10000)
    private String furtherDetails;
}
