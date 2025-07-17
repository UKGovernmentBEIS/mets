package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.handler;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class InstallationOnsiteInspectionSubmitPeerReviewDecisionActionHandlerTest {

    @InjectMocks
    private InstallationOnsiteInspectionSubmitPeerReviewDecisionActionHandler handler;

    @Test
    void getRequestTaskTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.INSTALLATION_ONSITE_INSPECTION_SUBMIT_PEER_REVIEW_DECISION);
    }

    @Test
    void getRequestTaskPayloadType() {
        assertThat(handler.getSubmitOutcomeBpmnConstantKey()).isEqualTo(BpmnProcessConstants.INSTALLATION_ONSITE_INSPECTION_SUBMIT_OUTCOME);
    }
}
