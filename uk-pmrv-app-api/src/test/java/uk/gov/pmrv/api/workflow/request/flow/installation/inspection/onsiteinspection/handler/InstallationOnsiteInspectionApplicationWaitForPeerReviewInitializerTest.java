package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class InstallationOnsiteInspectionApplicationWaitForPeerReviewInitializerTest {

    @InjectMocks
    private InstallationOnsiteInspectionApplicationWaitForPeerReviewInitializer initializer;

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsExactly(RequestTaskType.INSTALLATION_ONSITE_INSPECTION_WAIT_FOR_PEER_REVIEW);
    }

    @Test
    void getRequestTaskPayloadType() {
        assertThat(initializer.getRequestTaskPayloadType()).isEqualTo(RequestTaskPayloadType.INSTALLATION_ONSITE_INSPECTION_WAIT_FOR_PEER_REVIEW_PAYLOAD);
    }
}
