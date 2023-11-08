package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.aviationreporting.ukets.EmpUkEtsOriginatedData;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayloadVerifiable;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.domain.AviationAerUkEtsApplicationRequestTaskPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AviationAerUkEtsApplicationSubmitRequestTaskPayload
    extends AviationAerUkEtsApplicationRequestTaskPayload implements RequestTaskPayloadVerifiable {

    private EmpUkEtsOriginatedData empOriginatedData;

    private boolean verificationPerformed;

    private Long verificationBodyId;

}
