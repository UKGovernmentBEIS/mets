package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.EmpCorsiaOriginatedData;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayloadVerifiable;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaApplicationRequestTaskPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AviationAerCorsiaApplicationSubmitRequestTaskPayload
    extends AviationAerCorsiaApplicationRequestTaskPayload implements RequestTaskPayloadVerifiable {

    private EmpCorsiaOriginatedData empOriginatedData;

    private boolean verificationPerformed;

    private Long verificationBodyId;

}
