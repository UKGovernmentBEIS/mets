package uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestAviationAccountInfo {

    private String operatorName;
    private String crcoCode;
    private ServiceContactDetails serviceContactDetails;
}
