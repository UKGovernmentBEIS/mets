package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestMetadata;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EmpVariationRequestMetadata extends RequestMetadata {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Integer empConsolidationNumber;
    
    private String initiatorRoleType;
}
