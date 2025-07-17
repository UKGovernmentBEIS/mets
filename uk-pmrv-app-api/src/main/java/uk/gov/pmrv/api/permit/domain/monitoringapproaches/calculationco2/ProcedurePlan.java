package uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.common.ProcedureForm;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProcedurePlan extends ProcedureForm {
    
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Builder.Default
    private Set<UUID> procedurePlanIds = new HashSet<>();
    
}
