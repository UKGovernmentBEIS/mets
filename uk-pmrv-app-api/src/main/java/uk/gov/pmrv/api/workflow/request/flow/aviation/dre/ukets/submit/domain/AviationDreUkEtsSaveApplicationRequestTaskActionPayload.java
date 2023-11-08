package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.submit.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDre;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AviationDreUkEtsSaveApplicationRequestTaskActionPayload extends RequestTaskActionPayload {

    private AviationDre dre;

    private Boolean sectionCompleted;
}
