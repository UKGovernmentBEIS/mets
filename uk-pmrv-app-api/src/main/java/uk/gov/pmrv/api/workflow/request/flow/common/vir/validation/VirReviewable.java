package uk.gov.pmrv.api.workflow.request.flow.common.vir.validation;

import java.util.Map;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.OperatorImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.RegulatorReviewResponse;

public interface VirReviewable {

    RegulatorReviewResponse getRegulatorReviewResponse();

    Map<String, OperatorImprovementResponse> getOperatorImprovementResponses();
}
