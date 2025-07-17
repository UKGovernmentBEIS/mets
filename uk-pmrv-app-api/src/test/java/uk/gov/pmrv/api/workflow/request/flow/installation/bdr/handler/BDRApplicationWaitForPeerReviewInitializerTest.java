package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class BDRApplicationWaitForPeerReviewInitializerTest {

    @InjectMocks
    BDRApplicationWaitForPeerReviewInitializer initializer;

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsExactly(RequestTaskType.BDR_WAIT_FOR_PEER_REVIEW);
    }
}
