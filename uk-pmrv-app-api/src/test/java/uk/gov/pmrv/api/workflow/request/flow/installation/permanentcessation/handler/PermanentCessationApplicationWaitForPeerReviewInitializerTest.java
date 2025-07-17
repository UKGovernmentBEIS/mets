package uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class PermanentCessationApplicationWaitForPeerReviewInitializerTest {

    @InjectMocks
    PermanentCessationApplicationWaitForPeerReviewInitializer initializer;

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsExactly(RequestTaskType.PERMANENT_CESSATION_WAIT_FOR_PEER_REVIEW);
    }
}
