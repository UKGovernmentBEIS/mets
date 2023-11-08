package uk.gov.pmrv.api.workflow.request.flow.common.vir.validation;

import java.util.Map;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.RegulatorImprovementResponse;

public interface VirExpirable {

    Map<String, RegulatorImprovementResponse> getRegulatorImprovementResponses();
}
