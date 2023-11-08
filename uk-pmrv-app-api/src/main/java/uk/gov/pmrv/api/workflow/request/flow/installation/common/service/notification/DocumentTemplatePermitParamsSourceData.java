package uk.gov.pmrv.api.workflow.request.flow.installation.common.service.notification;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestInfo;

@Data
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentTemplatePermitParamsSourceData {
    
    private Request request;
    private String signatory;
    private PermitContainer permitContainer;
    private int consolidationNumber;
    private PermitIssuanceRequestMetadata issuanceRequestMetadata;
    private List<PermitVariationRequestInfo> variationRequestInfoList;
}
